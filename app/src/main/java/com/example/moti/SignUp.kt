package com.example.moti

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.*

class SignUp : AppCompatActivity() {
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
            //window.statusBarColor = Color.TRANSPARENT
            window.navigationBarColor = Color.TRANSPARENT
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = Color.parseColor("#E5E5E5")
            window.navigationBarColor = Color.parseColor("#E5E5E5")
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

    lateinit var signup_nickname: EditText
    lateinit var signup_signBtn: Button

    private var auth: FirebaseAuth? = null
    private lateinit var database: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        transparentStatusAndNavigation()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        signup_nickname = findViewById(R.id.signup_nickname)
        signup_signBtn = findViewById(R.id.signup_signBtn)

        signup_signBtn.setOnClickListener {
            var intent: Intent = Intent(this, OnBoardActivity::class.java)
            startActivity(intent)
        }

//
//        supportActionBar!!.hide()
//
//        // 회원가입 절차
//        signup_signBtn.setOnClickListener {
//
//            var email = signup_email.text.toString()
//            var password = signup_password.text.toString()
//
//            if (email.length < 1 || password.length < 1) {
//                Toast.makeText(this, "입력칸이 공란입니다.", Toast.LENGTH_SHORT).show()
//            } else {
//                auth?.createUserWithEmailAndPassword(email, password)
//                    ?.addOnCompleteListener { task ->
//                        if (task.isSuccessful) {
//
//                            // Firebase Realtime database에 User 정보 저장
//                            val CurrentUser = FirebaseAuth.getInstance().currentUser
//                            var User = User()
//
//                            User.uid = CurrentUser?.uid
//                            User.id = CurrentUser?.email
//                            User.pw = signup_password.text.toString()  // 우선은 암호화하지 않고 저장함
//                            User.nickname = signup_nickname.text.toString()
//                            User.tel = signup_phone.text.toString()
//
//                            /*
//                            var mCalendar = Calendar.getInstance()
//                            var todayDate =
//                                (mCalendar.get(Calendar.YEAR)).toString() + "/" + (mCalendar.get(
//                                    Calendar.MONTH) + 1).toString() + "/" + (mCalendar.get(Calendar.DAY_OF_MONTH)).toString()
//                            User.joindate = todayDate
//                             */
//
//
//                            var UserValues = User.toMap()
//
//                            val uid = CurrentUser?.uid
//
//                            if (uid != null) {
//                                database.child("users").child(uid).setValue(UserValues)
//                            }
//                            /*
//                            val UserUpdates = hashMapOf<String,Any>(
//                                "/User/$uid" to UserValues
//                            )
//                              database.updateChildren(UserUpdates)
//                            */
//
//                                Toast.makeText(this, "회원가입 완료", Toast.LENGTH_LONG).show()
//                            AlertDialog.Builder(this)
//                                .setTitle("알림")
//                                .setMessage("회원가입이 완료되었습니다.")
//                                .setPositiveButton("ok", object : DialogInterface.OnClickListener {
//                                    override fun onClick(dialog: DialogInterface, which: Int) {
//                                        Log.d("MyTag", "positive")
//                                        finish()
//                                        overridePendingTransition(0, 0)
//                                    }
//                                }).create().show()
//
//
//                                if (auth!!.currentUser != null) {
//                                    var intent = Intent(this, MEDIA_COMMUNICATION_SERVICE::class.java)
//                                    startActivity(intent)
//                                }
//
//                        } else {
//                            //show the error message
//                                Toast.makeText(
//                                        this,
//                                        task.exception?.message + "회원가입 실패",
//                                        Toast.LENGTH_LONG
//                               ).show()
//                        }
//                    }
//            }
//        }


    }



    override fun onResume() {
        super.onResume()
        supportActionBar!!.hide()
    }

    override fun onDestroy() {
        super.onDestroy()
        supportActionBar!!.show()
    }


}




