package com.example.moti

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.skt.Tmap.TMapView
import kotlinx.android.synthetic.main.activity_main.*

class CommunityMain: AppCompatActivity() {

    lateinit var edit_searchBar : EditText
    
    var tmapview: TMapView? = null

    lateinit var searchPlace : PoiItem
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community_main)

        edit_searchBar = findViewById(R.id.edit_searchBar)


        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    var placePoiItem = data?.getParcelableExtra<PoiItem>("placePoiItem")
                    Log.d("successM", "getIntent : ${placePoiItem}")

                    if (placePoiItem != null) {
                        searchPlace = placePoiItem
                    }

                }
            }

        val actionBar: ActionBar?
        actionBar=supportActionBar
        actionBar?.hide()


        //tmap 세팅
        tmapview = TMapView(this)
        tmapview!!.setSKTMapApiKey("l7xx3a42dd9f094c468e969018fba936e361")
        tmapview!!.setIconVisibility(true)
        tmapview!!.setCompassMode(true)
        tmapview!!.setTrackingMode(true)
        main_tmaplayout2.addView(tmapview)


        // 검색바
        edit_searchBar.setOnClickListener {
            val intent = Intent(this, SearchPoi::class.java)
            intent.putExtra("type", "searchPlace")
            resultLauncher.launch(intent)
        }
        
        
    }
}