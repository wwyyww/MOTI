package com.example.moti

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_after_riding.*
import kotlinx.android.synthetic.main.activity_ready_ticket.*
import kotlinx.android.synthetic.main.activity_riding.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class AfterRidingActivity: AppCompatActivity() {

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
            window.statusBarColor = Color.TRANSPARENT
            window.navigationBarColor = Color.TRANSPARENT
//            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
//            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//            window.statusBarColor = Color.parseColor("#00000000")
//            window.navigationBarColor = Color.parseColor("#00000000")
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

    var ridingKey=String()

    lateinit var departure: PoiItem
    lateinit var destination: PoiItem
    lateinit var layover : PoiItem

    var departaddress=String()
    var arriveaddress=String()

    //db용
    private var auth : FirebaseAuth? = null
    private lateinit var database: DatabaseReference
    private val CurrentUser = FirebaseAuth.getInstance().currentUser
    val uid = CurrentUser?.uid

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_after_riding)
        transparentStatusAndNavigation()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        //action bar 숨기기
        var actionBar: ActionBar?
        actionBar=supportActionBar
        actionBar?.hide()

        //db 세팅
        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

        //시간 세팅 & 시간 형식 : 10:30 12/11/2021
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy")
        val formattedDate = current.format(formatter)

        // 인텐트 값 가져오기
        val intent: Intent = getIntent()
        var departureIntent = intent.getParcelableExtra<PoiItem>("departure")
        var destinationIntent = intent.getParcelableExtra<PoiItem>("destination")
        var layoverIntent = intent.getParcelableExtra<PoiItem>("layover")
        var sharedPlaces = intent.getParcelableExtra<Post>("sharing")

        if (departureIntent != null && destinationIntent != null) {

            departure = departureIntent
            destination = destinationIntent
            layover = layoverIntent!!

        }


        after_time_txtview.text=intent.getStringExtra("time")
        after_date_txtview.text = formattedDate
        after_maintxt_txtView.text = "${sharedPlaces!!.title}"
        after_course_textview.text = sharedPlaces!!.title

        ridingKey= intent.getStringExtra("pushKey")!!
        Log.d("intent", "check pushKey ${ridingKey}")


        database.child("users/${uid}/nickname").get().addOnSuccessListener {
            Log.d("readyTicket", "nickname check ${it.value}")
            after_name_txtview.text = it.value.toString()
        }


        after_departure_textview.text = departure.fullAddressRoad.split(" ")[2]
        after_arrival_textview.text = destination.fullAddressRoad.split(" ")[2]




        after_next_button.setOnClickListener {
            val intent = Intent(this, SharingActivity::class.java).apply {
//                putExtra("time", riding_timer_textview.text)
                putExtra("pushKey", ridingKey)

            }.run {startActivity(this) }
        }


    }

}