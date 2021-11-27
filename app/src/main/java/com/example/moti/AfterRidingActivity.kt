package com.example.moti

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
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

        if (departureIntent != null && destinationIntent != null) {

            departure = departureIntent
            destination = destinationIntent
            layover = layoverIntent!!

        }


        after_time_txtview.text=intent.getStringExtra("time")
        after_date_txtview.text = formattedDate

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