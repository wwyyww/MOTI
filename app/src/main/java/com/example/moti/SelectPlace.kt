package com.example.moti

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts

class SelectPlace : AppCompatActivity() {

    lateinit var textV_departure : TextView
    lateinit var textV_destination : TextView

    lateinit var btn_selectLayover : Button


    lateinit var departure: PoiItem
    lateinit var destination: PoiItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_place)


        // 인텐트 값 가져오기

      //  val intent: Intent = getIntent()
     //   var placePoiItem = intent.getParcelableExtra<PoiItem>("placePoiItem")
      //  Log.d("successM", "getIntent : ${placePoiItem}")


        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                var placePoiItem = data?.getParcelableExtra<PoiItem>("placePoiItem")
                Log.d("successM", "onActivityResult : ${placePoiItem}")

                if (placePoiItem?.type =="departure"){
                    departure = placePoiItem
//                    placePoiItemList.add(0,placePoiItem )
                    Log.d("successM", "final : ${placePoiItem}")
                    textV_departure.text = placePoiItem.name +"\n" + placePoiItem.fullAddressRoad

                }else if (placePoiItem?.type =="destination"){
                    destination = placePoiItem
                    //placePoiItemList.add(1,placePoiItem )
                    Log.d("successM", "final : ${destination}")
                    textV_destination.text = destination.name +"\n" + destination.fullAddressRoad
                }
            }
        }




        textV_departure = findViewById(R.id.textV_departure)
        textV_destination = findViewById(R.id.textV_destination)
        btn_selectLayover = findViewById(R.id.btn_selectLayover)


        if (this::destination.isInitialized && this::departure.isInitialized){

            textV_destination.text = destination.name  // 인덱스 o이 departure
            textV_departure.text = departure.name  // 인덱스 1은 destination
        }



        if (textV_departure.text != null && textV_destination != null ){
            btn_selectLayover.visibility =  View.VISIBLE
        }else{
            btn_selectLayover.visibility =  View.GONE
        }



        textV_departure.setOnClickListener {
            val intent = Intent(this, SearchPoi::class.java)
            intent.putExtra("type", "departure")
            resultLauncher.launch(intent)
        }


        textV_destination.setOnClickListener {
            val intent = Intent(this, SearchPoi::class.java)
            intent.putExtra("type", "destination")
            resultLauncher.launch(intent)
        }

        btn_selectLayover.setOnClickListener {
            val intent = Intent(this, SelectLayover::class.java).apply {
                putExtra("departure", departure)
                putExtra("destination", destination)
            }.run {startActivity(this) }

        }

    }

}