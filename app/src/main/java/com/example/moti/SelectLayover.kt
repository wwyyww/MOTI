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


    var placePoiItemList: MutableList<PoiItem> = mutableListOf<PoiItem>()


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
                        placePoiItemList.add(2, placePoiItem)
                        textV_layover1.text = placePoiItemList[2].fullAddressRoad
                    }

                }
            }


        // 인텐트 값 가져오기
        val intent: Intent = getIntent()
        var departure = intent.getParcelableExtra<PoiItem>("departure")
        var destination = intent.getParcelableExtra<PoiItem>("destination")


        if (departure != null && destination != null) {
            placePoiItemList.add(0, departure)
            placePoiItemList.add(1, destination)
        }


        textV_layover1.setOnClickListener {
            val intent = Intent(this, SearchPoi::class.java)
            intent.putExtra("type", "layover1")
            resultLauncher.launch(intent)
        }


        btn_startRiding.setOnClickListener {
            Log.d("successM", "final : ${placePoiItemList}")

            val intent = Intent(this, RidingActivity::class.java).apply {
                putExtra("departure", placePoiItemList[0])
                putExtra("destination", placePoiItemList[1])
                putExtra("layout",placePoiItemList[2] )
            }.run {startActivity(this) }
        }

    }

    /*
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            var placePoiItem = data?.getParcelableExtra<PoiItem>("placePoiItem")
            Log.d("successM", "getIntent : ${placePoiItem}")

            if (placePoiItem != null) {
                placePoiItemList[3] = placePoiItem
            }

        }
    }

     */

}
