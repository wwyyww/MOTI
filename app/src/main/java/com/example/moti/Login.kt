package com.example.moti

import android.content.ContentValues
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.login.*
import java.security.MessageDigest
import java.security.Signature
import java.util.*


class Login : AppCompatActivity() {
    private fun transparentStatusAndNavigation() {
        //make full transparent statusBar
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                        or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, true
            )
        }
        if (Build.VERSION.SDK_INT >= 19) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(
                (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                        or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION), false
            )
//            window.statusBarColor = Color.TRANSPARENT
//            window.navigationBarColor = Color.TRANSPARENT
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = Color.parseColor("#F6F6F6")
            window.navigationBarColor = Color.parseColor("#F6F6F6")
        }
    }

    private fun setWindowFlag(bits: Int, on: Boolean) {
        val win = window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }

    lateinit var login_signUpBtn: TextView
    lateinit var login_btn: Button
    lateinit var login_email: EditText
    lateinit var login_pw: EditText
    private lateinit var database: DatabaseReference

    //private lateinit var login_googleBtn : GoogleSignInClient

    private lateinit var auth: FirebaseAuth

    // 구글 로그인
    var googleSignInClient: GoogleSignInClient? = null
    val RC_SIGN_IN = 1000


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        transparentStatusAndNavigation()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT


        database = Firebase.database.reference
        //val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        auth = Firebase.auth

        login_signUpBtn = findViewById(R.id.login_signUpBtn)
        login_btn = findViewById(R.id.login_btn)
        login_email = findViewById(R.id.login_email)
        login_pw = findViewById(R.id.login_pw)

        supportActionBar!!.hide()

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            Log.e("user", "user: ${user.toString()}")
//            Toast.makeText(this, "[Login] currentUser가 null이 아님" + user, Toast.LENGTH_SHORT).show()
            var intent = Intent(this, SignUp::class.java)
            startActivity(intent)
            finish()
        }



        login_btn.setOnClickListener {
            login()
        }


        // /* 구글 로그인 */
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("382573987032-88vb0ul5dhkdiu4hk759l9bo95v8sn8j.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)


        //login_googleBtn = findViewById(R.id.login_googleBtn)

        login_googleBtn.setOnClickListener {
            GooglesignIn()
//            Toast.makeText(this, "[Login] Google 버튼 누름", Toast.LENGTH_SHORT).show()
        }


        // /* 회원 가입 */
        login_signUpBtn.setOnClickListener {
            var intent = Intent(this, SignUp::class.java)
            startActivity(intent)
            //val myRef : DatabaseReference = database.getReference("message")
            //myRef.setValue("안녕 반가워!")
        }

    }

    override fun onResume() {
        super.onResume()
        supportActionBar!!.hide()
    }

    override fun onDestroy() {
        super.onDestroy()
        supportActionBar!!.show()
    }




    override fun onStart() {
        // 활동은 초기화 할 때 사용자가 현재 로그인되어 있는지 확인함
        super.onStart()
        val currentUser = auth?.currentUser
        updateUI(currentUser)
    }


    private fun login() {
        var email = login_email.text.toString()
        var password = login_pw.text.toString()

        if (email.length < 1 || password.length < 1) {
//            Toast.makeText(this, "입력칸이 공란입니다.", Toast.LENGTH_SHORT).show()
        } else {
            auth?.signInWithEmailAndPassword(email, password)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //Login
                        //moveMainPage(task.result?.user)
//                            Toast.makeText(this, "로그인 완료", Toast.LENGTH_SHORT).show()
                        if (auth!!.currentUser != null) {
//                                Toast.makeText(this, "로그인 찐 완료", Toast.LENGTH_SHORT).show()
                            var intent = Intent(this, SignUp::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        //show the error message
                        Log.w("Login", "signInWithEmail:failure", task.exception)
//                            Toast.makeText(
//                                    this,
//                                    task.exception?.message + "로그인 실패",
//                                    Toast.LENGTH_LONG
//                            ).show()

                    }
                }
        }
    }


    // 구글 로그인
    private fun GooglesignIn() {
//        Toast.makeText(this, "[Login] signIn 함수 실행", Toast.LENGTH_SHORT).show()
        val signInIntent : Intent = googleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
//                Toast.makeText(this, "[Login] firebaseAuthWithGoogle"+ account.id, Toast.LENGTH_SHORT).show()
                firebaseAuthWithGoogle(account.idToken!!)
            }catch (e : ApiException){
                Log.w(ContentValues.TAG, "Google sign in failed", e)
//                Toast.makeText(this, "[Login] Google sign in failed", Toast.LENGTH_SHORT).show()
            }
        }
        else{
//            Toast.makeText(this, "[Login] startForResult 안 됨", Toast.LENGTH_SHORT).show()
        }
    }


    //사용자가 정상적으로 로그인하면 GoogleSignInAccount 객체에서 ID 토큰을 가져와서
    // Firebase 사용자 인증 정보로 교환하고 해당 정보를 사용해 Firebase에 인증합니다.
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // 아이디 비밀번호 맞을 때
                    Log.d("Login", "signInWithCredential:success")
//                        Toast.makeText(this, "[Login] signInWithCredential:success", Toast.LENGTH_SHORT).show()

                    val CurrentUser = Firebase.auth.currentUser
                    var isExist1: Boolean = false


                    val userRef = Firebase.database.getReference("users")
                    userRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (!snapshot.child(CurrentUser?.uid.toString()).exists()) {
                                var User = User()
                                CurrentUser?.let {
                                    for (profile in it.providerData) {
                                        User.uid = CurrentUser?.uid
                                        User.id = CurrentUser?.email
                                        // google 연동이라 password는 따로 저장하지 않음
                                        User.nickname = CurrentUser?.displayName
                                        User.tel = CurrentUser?.phoneNumber
/*
                                        var mCalendar = Calendar.getInstance()
                                        var todayDate = (mCalendar.get(Calendar.YEAR)).toString() + "/" + (mCalendar.get(Calendar.MONTH) + 1).toString() + "/" + (mCalendar.get(Calendar.DAY_OF_MONTH)).toString()
                                        User.joindate = todayDate

 */
                                    }
                                    var UserValues = User.toMap()

                                    val uid = CurrentUser?.uid

                                    if (uid != null) {
                                        database.child("users").child(uid).setValue(UserValues)
                                    }
                                }
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // Getting Post failed, log a message
                        }
                    })

                    val user = auth!!.currentUser
                    updateUI(user)
                } else {
                    // 아이디 비밀번호 틀렸을 때
                    // If sign in fails, display a message to the user.
                    Log.w("Login", "signInWithCredential:failure", task.exception)
//                        Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) { //update ui code here
        if (user != null) {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        // super.onBackPressed()
    }
}