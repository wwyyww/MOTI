package com.example.moti

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_ready.*
import kotlinx.android.synthetic.main.activity_search_poi.*
import java.util.*
import kotlin.collections.ArrayList


class ReadyAdapter(val context: Context, private val ReadyData: ArrayList<String>): RecyclerView.Adapter<ReadyAdapter.ViewHolder>() {


        inner class ViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        val ready_txt = view!!.findViewById<TextView>(R.id.ready_num_text)


        fun bind(ready : String, context: Context, position: Int) {
            ready_txt!!.text = ready
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReadyAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.ready_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReadyAdapter.ViewHolder, position: Int) {
        holder.bind(ReadyData[position], context, position)
    }

    override fun getItemCount() = ReadyData.size

}

class ReadyActivity: AppCompatActivity() {
    private fun transparentStatusAndNavigation() {
        //make full transparent statusBar
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                        or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, true
            )
        }
        if (Build.VERSION.SDK_INT >= 19) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(
                (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                        or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION), false
            )
            window.statusBarColor = Color.TRANSPARENT
            window.navigationBarColor = Color.TRANSPARENT
//            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
//            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            //window.statusBarColor = Color.parseColor("#0CE795")
            //window.navigationBarColor = Color.TRANSPARENT
        }
    }

    private fun setWindowFlag(bits: Int, on: Boolean) {
        val win = window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }

    lateinit var readyRecyclerView: RecyclerView
    lateinit var readyAdapter: ReadyAdapter

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
        transparentStatusAndNavigation()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        //action bar 숨기기
        var actionBar: ActionBar?
        actionBar=supportActionBar
        actionBar?.hide()

        readyRecyclerView = findViewById(R.id.ready_name_layout)
//        readyAdapter = ReadyAdapter(this, placeNameList)
//        readyRecyclerView.adapter = readyAdapter

        // 인텐트 값 가져오기
//        val intent: Intent = getIntent()
//        var departureIntent = intent.getParcelableExtra<PoiItem>("departure")
//        var destinationIntent = intent.getParcelableExtra<PoiItem>("destination")
//        var layoverIntent = intent.getParcelableExtra<PoiItem>("layover")
//
//
//
//        if (departureIntent != null && destinationIntent != null) {
//
//            departure = departureIntent
//            destination = destinationIntent
//            layover = layoverIntent!!
//            departFulladdress = getCompleteAddressString(this, departure.frontLat.toDouble(), departure.frontLon.toDouble())
//            arriveFulladdress = getCompleteAddressString(this, destination.frontLat.toDouble(), destination.frontLon.toDouble())
//
//            departaddress = departFulladdress.split(" ")[2]
//            arriveaddress = arriveFulladdress.split(" ")[2]
//
//            placeNameList.add(departure.name)
//            placeNameList.add(destination.name)
//            placeNameList.add(layover.name)
//
//
//
//
//            readyAdapter = ReadyAdapter(this, placeNameList)
//            readyRecyclerView.adapter = readyAdapter
//
//        }

        placeNameList.add("롯데월드 잠실점")
        placeNameList.add("성수역[2호선]")
        placeNameList.add("잠실역[2호선]")

        readyAdapter = ReadyAdapter(this, placeNameList)
        readyRecyclerView.adapter = readyAdapter

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