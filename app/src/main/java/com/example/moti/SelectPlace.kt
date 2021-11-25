package com.example.moti

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
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
    lateinit var textV_layover1 : TextView

    lateinit var v_departure : View
    lateinit var v_destination : View
    lateinit var v_layover1 : View

    lateinit var btn_next : Button


    lateinit var departure: PoiItem
    lateinit var destination: PoiItem
    lateinit var layover1 :PoiItem


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_place)


        // 인텐트 값 가져오기

      //  val intent: Intent = getIntent()
     //   var placePoiItem = intent.getParcelableExtra<PoiItem>("placePoiItem")
      //  Log.d("successM", "getIntent : ${placePoiItem}")


        textV_departure = findViewById(R.id.textV_departure)
        textV_destination = findViewById(R.id.textV_destination)
        textV_layover1 = findViewById(R.id.textV_layover1)
        btn_next = findViewById(R.id.btn_next)
        v_departure = findViewById(R.id.v_departure)
        v_layover1 = findViewById(R.id.v_layover1)
        v_destination = findViewById(R.id.v_destination)



        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                var placePoiItem = data?.getParcelableExtra<PoiItem>("placePoiItem")
                Log.d("successM", "onActivityResult : ${placePoiItem}")

                if (placePoiItem?.type =="departure"){
                    departure = placePoiItem
//                    placePoiItemList.add(0,placePoiItem )
                    Log.d("successM", "final : ${placePoiItem}")
                    textV_departure.text = placePoiItem.name
                    textV_departure.setTextColor(Color.BLACK)

                }else if (placePoiItem?.type =="destination"){
                    destination = placePoiItem
                    //placePoiItemList.add(1,placePoiItem )
                    Log.d("successM", "final : ${destination}")
                    textV_destination.text = destination.name
                    textV_destination.setTextColor(Color.BLACK)

                }else if (placePoiItem?.type =="layover"){
                    layover1 = placePoiItem
                    textV_layover1.text = layover1.name
                    textV_layover1.setTextColor(Color.BLACK)
                }
            }
        }



        if (this::destination.isInitialized && this::departure.isInitialized && this::layover1.isInitialized) {

            textV_destination.text = destination.name
            textV_departure.text = departure.name
            textV_layover1.text = layover1.name
        }



        if (textV_departure.text != null && textV_destination != null &&  textV_destination != null ){
            btn_next.visibility =  View.VISIBLE
        }else{
            btn_next.visibility =  View.GONE
        }



        v_departure.setOnClickListener {
            val intent = Intent(this, SearchPoi::class.java)
            intent.putExtra("type", "departure")
            resultLauncher.launch(intent)
        }


        v_destination.setOnClickListener {
            val intent = Intent(this, SearchPoi::class.java)
            intent.putExtra("type", "destination")
            resultLauncher.launch(intent)
        }


        v_layover1.setOnClickListener {
            val intent = Intent(this, SearchPoi::class.java)
            intent.putExtra("type", "layover1")
            resultLauncher.launch(intent)
        }


        btn_next.setOnClickListener {

            Log.d("SUCCESSM", "다음 버튼 : ${departure} ${destination} ${layover1} ")
            val intent = Intent(this, SelectLayover::class.java).apply {
                putExtra("departure", departure)
                putExtra("destination", destination)
                putExtra("layover", layover1)
            }.run {startActivity(this) }

        }

    }

}