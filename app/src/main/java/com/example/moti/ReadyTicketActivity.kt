package com.example.moti

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_ready_ticket.*


@Suppress("DEPRECATION")
class ReadyTicketActivity: AppCompatActivity() {


    lateinit var departure: PoiItem
    lateinit var destination: PoiItem
    lateinit var layover : PoiItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ready_ticket)

        //action bar 숨기기
        var actionBar: ActionBar?
        actionBar=supportActionBar
        actionBar?.hide()

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




        Handler().postDelayed({
            val nextintent = Intent(this, RidingActivity::class.java).apply {
                putExtra("departure", departure)
                putExtra("destination", destination)
                putExtra("layover", layover)
            }.run {startActivity(this) }
        }, 10000)








    }

}