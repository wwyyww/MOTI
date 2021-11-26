package com.example.moti

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.skt.Tmap.TMapGpsManager.GPS_PROVIDER
import com.skt.Tmap.TMapGpsManager.NETWORK_PROVIDER
import kotlinx.android.synthetic.main.activity_riding.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.skt.Tmap.*
import java.util.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@Suppress("DEPRECATION")
class RidingActivity:AppCompatActivity(), TMapGpsManager.onLocationChangedCallback  {

    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL_TMAP_API)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val tmapApi = retrofit.create(TmapAPI::class.java)
    var request = requst_pedestrian_guide()

    var tmapview: TMapView? = null

    // T Map GPS
    var tMapGPS: TMapGpsManager? = null
    lateinit var polyline : TMapPolyLine

    companion object {
        const val PERMISSION_REQUEST_CODE = 1001
    }

    //db용
    private var auth : FirebaseAuth? = null
    private lateinit var database: DatabaseReference
    private val CurrentUser = FirebaseAuth.getInstance().currentUser
    val uid = CurrentUser?.uid
    private lateinit var pushRef:DatabaseReference


    var mCalendar = Calendar.getInstance()
    lateinit var todayDate:String

    var courseCount=0
    var coordList= ArrayList<Coordinate>()
    var placePoiItemList: MutableList<PoiItem> = mutableListOf<PoiItem>()
    var responseFeatureList= ArrayList<response_features>()
    var responseCoordList=ArrayList<List<String>>()

    lateinit var departure:PoiItem
    lateinit var destination:PoiItem
    lateinit var layover:PoiItem

    var i = 0

    //시간 측정용 변수
    private var timerTask:Timer?=null
    private var time = 0
    private var timerIsRunning = false
    var hour = 0
    var min = 0
    var sec = 0
    var timerStart = true

    var photoCount = 0
    var hashtagCount = 0

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riding)
        riding_course_textview.bringToFront()
        riding_gps_textview.bringToFront()
        riding_button.bringToFront()
        riding_guide_layout.bringToFront()
        riding_bottom_layout.bringToFront()

//        departure = intent.getParcelableExtra<PoiItem>("departure")!!
//        destination = intent.getParcelableExtra<PoiItem>("destination")!!
//        layover = intent.getParcelableExtra<PoiItem>("layover")!!



        setPayload()
        val callGuidePedestrian = tmapApi.guidePedestrian(APPKEY, request.startX,request.startY,
            request.angle, request.speed, request.endPoiId, request.endX, request.endY, request.passList,
            request.reqCoordType, request.startName, request.endName, request.searchOption, request.resCoordType)

        callGuidePedestrian.enqueue(object : Callback<response_pedestrian_guide>{

            override fun onResponse(
                call: Call<response_pedestrian_guide>,
                response: Response<response_pedestrian_guide>
            ) {
//                Log.d("tmap", "성공 : ${response.raw()}")
                Log.d("tmap", "성공 : ${response.body()!!.features}")
//                Log.d("tmap", "성공 : ${response.headers()}")
                responseFeatureList= response.body()!!.features as ArrayList<response_features>
//                Log.d("tmap", "list : ${responseFeatureList[0].properties.turnType}")
//                Log.d("tmap", "list : ${responseFeatureList[1].properties.distance}")

            }

            override fun onFailure(call: Call<response_pedestrian_guide>, t: Throwable) {
                Log.d("errorM", t.message.toString() )
            }
        })



        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

        todayDate = (mCalendar.get(Calendar.YEAR)).toString() + "/" + (mCalendar.get(Calendar.MONTH) + 1).toString() +
                "/" + (mCalendar.get(Calendar.DAY_OF_MONTH)).toString()

        pushRef=database.child("user/$uid/course/date").child(todayDate).push()


        //action bar 숨기기
        var actionBar: ActionBar?
        actionBar=supportActionBar
        actionBar?.hide()

        //tmap 세팅
        tmapview = TMapView(this)
        tmapview!!.setSKTMapApiKey("l7xx3a42dd9f094c468e969018fba936e361")
        tmapview!!.setIconVisibility(true)
        tmapview!!.setCompassMode(true)
        tmapview!!.setTrackingMode(true)
        tmap_layout.addView(tmapview)

        //위치 권한 확인
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "권한 승인", Toast.LENGTH_SHORT).show()
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), // 1
                PERMISSION_REQUEST_CODE) // 2
        }

        // GPS using T Map
        tMapGPS = TMapGpsManager(this)

        // Initial Setting
        // Initial Setting
        tMapGPS!!.minTime=1000
        tMapGPS!!.minDistance= 10F
        tMapGPS!!.provider=NETWORK_PROVIDER

        tMapGPS!!.OpenGps()
        polyline= TMapPolyLine()

        timerIsRunning = !timerIsRunning
        if (timerIsRunning) startTimer()

        riding_button.setOnClickListener{
            //getCoordinates()
//            Log.d("tmap","잘 넘어왔는지 확인. departure : ${departure}")
//            Log.d("tmap","잘 넘어왔는지 확인. destination : ${destination}")
//            Log.d("tmap","잘 넘어왔는지 확인. layover : ${layover}")
//            Log.d("tmap", "list[i] : ${responseFeatureList[i]}")
//            Log.d("tmap", "list[i+1] : ${responseFeatureList[i+1].geometry}")
            var distance=responseFeatureList[i+1].properties.distance
            var addr=responseFeatureList[i+1].properties.name
            riding_mainGuide_textview.text="${distance}m"
            riding_mainAddr_textview.text="${addr}"
            if (i<responseFeatureList.size){
                i += 2
                if(i<responseFeatureList.size){
                    var subdistance=responseFeatureList[i+1].properties.distance
                    riding_subGuide_textview.text="${subdistance}m"
                }
            }

//            for (res in responseFeatureList){
//                if (res.geometry.coordinate.size > 1){
//                    var tmpCoordList=res.geometry.coordinate
//                    for (coord in tmpCoordList){
//                        responseCoordList.add(coord)
//                    }
//                }else{
//                    responseCoordList.add(res.geometry.coordinate)
//
//                }
//
//            }
        }

        riding_stop_imgview.setOnClickListener{
            pauseTimer()
            timerStart = !timerStart

            if (timerStart){
                riding_stop_imgview.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_pause_circle_filled_24))
                startTimer()
            }else{
                riding_stop_imgview.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_play_circle_filled_24))
            }

        }

        riding_end_imgview.setOnClickListener {
            pauseTimer()

            pushRef.child("Record/distance").setValue("10")
            pushRef.child("Record/time").setValue("00:11:11")
            pushRef.child("Record/date").setValue("2021/11/25")
            pushRef.child("Record/start").setValue("출발지")
            pushRef.child("Record/arrive").setValue("도착지")
            pushRef.child("Record/layover").setValue("경유지")
            pushRef.child("Record/text").setValue("사용자가 생각 쓰는 부분")
            pushRef.child("Photo/${photoCount}").setValue("photo url")
            pushRef.child("Hashtag/${hashtagCount}").setValue("해시태그")




            val intent = Intent(this, AfterRidingActivity::class.java).apply {
                putExtra("time", riding_timer_textview.text)
                putExtra("pushKey", pushRef.key)



            }.run {startActivity(this) }

//            intent.putExtra("type", "departure")


        }




    }

    private fun startTimer(){
        timerTask=kotlin.concurrent.timer(period = 10){
            time++
            hour=time/100/3600
            min=(time/100/60)%60
            sec=(time/100)%60
            this@RidingActivity?.runOnUiThread{
                riding_timer_textview.text=String.format("%02d:%02d:%02d", hour, min, sec)
            }

        }
    }

    private fun pauseTimer(){
        timerTask?.cancel()
    }



    // 길찾기 api 호출을 위해 payload 담기
    fun setPayload(){
        request.startX="126.92365493654832" // 시작 위치
        request.startY="37.556770374096615" // 시작 위치
        request.angle="1"
        request.speed="60"
        request.endPoiId="334852"
        request.endX="126.92432158129688" // 도착 위치
        request.endY="37.55279861528311" // 도착 위치
        request.passList="126.92774822,37.55395475" // 경유지 목록
        request.reqCoordType="WGS84GEO"
        request.startName="출발지" // 출발지 이름
        request.endName="도착지" // 도착지 이름
        request.searchOption="0"
        request.resCoordType="WGS84GEO"
//        request.startX="${departure.frontLon}" // 시작 위치
//        request.startY="${departure.frontLat}"// 시작 위치
//        request.angle="1"
//        request.speed="60"
//        request.endPoiId="334852"
//        request.endX="${destination.frontLon}" // 도착 위치
//        request.endY="${destination.frontLat}" // 도착 위치
//        request.passList="${layover.frontLon},${layover.frontLat}" // 경유지 목록
//        request.reqCoordType="WGS84GEO"
//        request.startName="출발지" // 출발지 이름
//        request.endName="도착지" // 도착지 이름
//        request.searchOption="0"
//        request.resCoordType="WGS84GEO"
    }

    fun getCoordinates(){

        database.child("user/$uid/course/date").child(todayDate).child("-Mp6jea0jyVGqqdUmII_").get().addOnSuccessListener {
            var post=it.children
            for(p in post){
                var pvalue=p.value
                Log.i("firebase", "check value $pvalue")
                var cord = Coordinate()
                cord.lat = p.child("lat").value.toString()
                cord.lng = p.child("lng").value.toString()
                Log.i("firebase", "check cord lng ${cord.lng}")
                coordList.add(cord)
            }

            //set coordinates to polyline
            for (coord in coordList){
                val latitude: Double = coord.lat!!.toDouble()
                val longitude: Double = coord.lng!!.toDouble()
                var point= TMapPoint(latitude, longitude)
                polyline.addLinePoint(point)
                tmapview!!.addTMapPolyLine("line", polyline)
            }

        }

    }





    override fun onLocationChange(location: Location) {
        tmapview!!.setLocationPoint(location.longitude, location.latitude)
        tmapview!!.setCenterPoint(location.longitude, location.latitude)

        var lat=location.latitude
        var long=location.longitude
        var point=TMapPoint(lat, long)

        //save coordinate
        pushRef.child("Coordinates/$courseCount/lat").setValue("$lat")
        pushRef.child("Coordinates/$courseCount/lng").setValue("$long")
        courseCount+=1

        polyline.addLinePoint(point)
        tmapview!!.addTMapPolyLine("line", polyline)
        Log.i("gps", "check gps change")


    }



    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {  // 1
                if (grantResults.isEmpty()) {  // 2
                    throw RuntimeException("Empty permission result")
                }
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {  // 3
                    Toast.makeText(this, "권한 승인", Toast.LENGTH_SHORT).show()
                } else {
                    if (shouldShowRequestPermissionRationale(
                            Manifest.permission.ACCESS_FINE_LOCATION)) { // 4
                        Log.d(TAG, "User declined, but i can still ask for more")
                        requestPermissions(
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            PERMISSION_REQUEST_CODE)
                    } else {
                        Log.d(TAG, "User declined and i can't ask")
                        showDialogToGetPermission()   // 5
                    }
                }
            }
        }
    }

    private fun showDialogToGetPermission() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Permisisons request")
            .setMessage("We need the location permission for some reason. " +
                    "You need to move on Settings to grant some permissions")

        builder.setPositiveButton("OK") { dialogInterface, i ->
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", packageName, null))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)   // 6
        }
        builder.setNegativeButton("Later") { dialogInterface, i ->
            // ignore
        }
        val dialog = builder.create()
        dialog.show()
    }


}