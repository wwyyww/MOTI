package com.example.moti

import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_ready.*
import kotlinx.android.synthetic.main.activity_search_poi.*
import java.util.*
import kotlin.collections.ArrayList


class ReadyActivity: AppCompatActivity() {


    lateinit var departure: PoiItem
    lateinit var destination: PoiItem
    lateinit var layover : PoiItem

    var departFulladdress=String()
    var arriveFulladdress=String()

    var departaddress=String()
    var arriveaddress=String()

    //리사이클러뷰에 들어갈 데이터
    var placeNameList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ready)

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
            departFulladdress = getCompleteAddressString(this, departure.frontLat.toDouble(), departure.frontLon.toDouble())
            arriveFulladdress = getCompleteAddressString(this, destination.frontLat.toDouble(), destination.frontLon.toDouble())

            departaddress = departFulladdress.split(" ")[2]
            arriveaddress = arriveFulladdress.split(" ")[2]

            placeNameList.add(departure.name)
            placeNameList.add(destination.name)
            placeNameList.add(layover.name)

        }



        ready_start_btn.setOnClickListener {
            val intent = Intent(this, ReadyTicketActivity::class.java).apply {
                putExtra("departure", departure)
                putExtra("destination", destination)
                putExtra("layover", layover)
                putExtra("departaddress", departaddress)
                putExtra("arriveaddress", arriveaddress)
            }.run {startActivity(this) }
        }



    }


    //주소 좌표를 한글 주소로 반환
    private fun getCompleteAddressString(context: Context?, LATITUDE: Double, LONGITUDE: Double): String {
        var strAdd = ""
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1)
            if (addresses != null) {
                val returnedAddress: Address = addresses[0]
                val strReturnedAddress = StringBuilder("")
                for (i in 0..returnedAddress.getMaxAddressLineIndex()) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n")
                }
                strAdd = strReturnedAddress.toString()
                Log.w("MyCurrentloctionaddress", strReturnedAddress.toString())
            } else {
                Log.w("MyCurrentloctionaddress", "No Address returned!")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.w("MyCurrentloctionaddress", "Canont get Address!")
        }

        // "대한민국 " 글자 지워버림
        strAdd = strAdd.substring(5)
        return strAdd
    }


}