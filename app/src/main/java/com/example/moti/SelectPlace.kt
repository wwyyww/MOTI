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


    var placePoiItemList: MutableList<PoiItem> = mutableListOf<PoiItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_place)


//        // getSharedPreferences POI 가져온거 여부 알아보기
//        val prefs: SharedPreferences = this.getSharedPreferences("selectPlace_type", Context.MODE_PRIVATE)
//        prefs.getString("type", "no email")


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

                    placePoiItemList.add(0,placePoiItem )
                    Log.d("successM", "final : ${placePoiItemList}")
                    textV_departure.text = placePoiItemList[0].name +"\n" + placePoiItemList[1].fullAddressRoad
                }else if (placePoiItem?.type =="destination"){

                    placePoiItemList.add(1,placePoiItem )
                    Log.d("successM", "final : ${placePoiItemList}")
                    textV_destination.text = placePoiItemList[1].name +"\n" + placePoiItemList[1].fullAddressRoad
                }
            }
        }




        textV_departure = findViewById(R.id.textV_departure)
        textV_destination = findViewById(R.id.textV_destination)
        btn_selectLayover = findViewById(R.id.btn_selectLayover)


        if (placePoiItemList.isEmpty() == false) {

            textV_destination.text = placePoiItemList[0].name  // 인덱스 o이 departure
            textV_departure.text = placePoiItemList[1].name  // 인덱스 1은 destination

            /*
            var type: String = placePoiItem.type

            if (type == "destination") {
                textV_destination.text = placePoiItem.fullAddressRoad

            } else if (type == "departure") {
                textV_departure.text = placePoiItem.fullAddressRoad
            }

             */
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
                putExtra("departure", placePoiItemList[0])
                putExtra("destination", placePoiItemList[1])
            }.run {startActivity(this) }

        }

    }

/*
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("successM", "onActivityResult!")
        if (resultCode == RESULT_OK) {
            var placePoiItem = data?.getParcelableExtra<PoiItem>("placePoiItem")
            Log.d("successM", "onActivityResult : ${placePoiItem}")

            if (placePoiItem?.type =="departure"){
                placePoiItemList[0] = placePoiItem
            }else if (placePoiItem?.type =="destination"){
                placePoiItemList[1] = placePoiItem
            }
        }
    }

 */
}