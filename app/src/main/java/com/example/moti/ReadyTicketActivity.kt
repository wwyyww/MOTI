package com.example.moti

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_ready_ticket.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


@Suppress("DEPRECATION")
class ReadyTicketActivity: AppCompatActivity() {

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

    var mCalendar = Calendar.getInstance()
    lateinit var todayDate:String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ready_ticket)

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
        Log.d("readyTicket", "today datetime check ${formattedDate}")
//        todayDate = (mCalendar.get(Calendar.YEAR)).toString() + "/" + (mCalendar.get(Calendar.MONTH) + 1).toString() +
//                "/" + (mCalendar.get(Calendar.DAY_OF_MONTH)).toString()

        // 인텐트 값 가져오기
        val intent: Intent = getIntent()
        var departureIntent = intent.getParcelableExtra<PoiItem>("departure")
        var destinationIntent = intent.getParcelableExtra<PoiItem>("destination")
        var layoverIntent = intent.getParcelableExtra<PoiItem>("layover")
        departaddress = intent.getStringExtra("departaddress")!!
        arriveaddress = intent.getStringExtra("arriveaddress")!!
        var sharedPlaces = intent.getParcelableExtra<Post>("sharing")


        if (departureIntent != null && destinationIntent != null) {

            departure = departureIntent
            destination = destinationIntent
            layover = layoverIntent!!

        }

        ready_ticket_depart_txtview.text = departaddress
        ready_ticket_arrive_txtview.text = arriveaddress
        ready_ticket_date_txtview.text = formattedDate


        ready_ticket_title_textview.text = sharedPlaces!!.title

        database.child("users/${uid}/nickname").get().addOnSuccessListener {
            Log.d("readyTicket", "nickname check ${it.value}")
            ready_ticket_name_txtview.text = it.value.toString()
        }

        Handler().postDelayed({
            val nextintent = Intent(this, RidingActivity::class.java).apply {
                putExtra("departure", departure)
                putExtra("destination", destination)
                putExtra("layover", layover)
                putExtra("sharing", sharedPlaces)

            }.run {startActivity(this) }
        }, 4000)






    }

}