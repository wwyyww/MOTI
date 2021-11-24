package com.example.moti

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts

class SelectLayover : AppCompatActivity() {


    lateinit var textV_layover1: TextView
    lateinit var btn_startRiding: Button


    lateinit var departure: PoiItem
    lateinit var destination: PoiItem
    lateinit var layover : PoiItem


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_layover)

        textV_layover1 = findViewById(R.id.textV_layover1)
        btn_startRiding = findViewById(R.id.btn_startRiding)


        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    var placePoiItem = data?.getParcelableExtra<PoiItem>("placePoiItem")
                    Log.d("successM", "getIntent : ${placePoiItem}")

                    if (placePoiItem != null) {
                        layover = placePoiItem
                        textV_layover1.text = layover.name +"\n" + layover.fullAddressRoad
                    }

                }
            }

        // 인텐트 값 가져오기
        val intent: Intent = getIntent()
        var departureIntent = intent.getParcelableExtra<PoiItem>("departure")
        var destinationIntent = intent.getParcelableExtra<PoiItem>("destination")



        if (departureIntent != null && destinationIntent != null) {

            departure = departureIntent
            destination = destinationIntent

        }


        textV_layover1.setOnClickListener {
            val intent = Intent(this, SearchPoi::class.java)
            intent.putExtra("type", "layover1")
            resultLauncher.launch(intent)
        }


        btn_startRiding.setOnClickListener {

            val intent = Intent(this, RidingActivity::class.java).apply {
                putExtra("departure", departure)
                putExtra("destination", destination)
                putExtra("layover", layover)
            }.run {startActivity(this) }
        }

    }



}
