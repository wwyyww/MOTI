package com.example.moti

import android.Manifest
import android.Manifest.permission.CAMERA
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.skt.Tmap.TMapGpsManager.GPS_PROVIDER
import com.skt.Tmap.TMapGpsManager.NETWORK_PROVIDER
import kotlinx.android.synthetic.main.activity_riding.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.skt.Tmap.*
import java.util.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList


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

    var ResponsecoordList= ArrayList<Coordinate>()


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


    // 카메라 변수
    lateinit var riding_camera_imgview : ConstraintLayout
    lateinit var photoURI : Uri
    val REQUEST_TAKE_PHOTO = 1
    lateinit var currentPhotoPath: String // 사진 경로
    lateinit var fileName : String

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
//            window.statusBarColor = Color.parseColor("#00000000")
//            window.navigationBarColor = Color.parseColor("#00000000")
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

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transparentStatusAndNavigation()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setContentView(R.layout.activity_riding)
        riding_course_textview.bringToFront()
        riding_gps_textview.bringToFront()
        riding_guide_layout.bringToFront()
        riding_bottom_layout.bringToFront()

        // 인텐트 값 가져오기
        val intent: Intent = getIntent()
        var departureIntent = intent.getParcelableExtra<PoiItem>("departure")
        var destinationIntent = intent.getParcelableExtra<PoiItem>("destination")
        var layoverIntent = intent.getParcelableExtra<PoiItem>("layover")
        var sharedPlaces = intent.getParcelableExtra<Post>("sharing")


        if (departureIntent != null && destinationIntent != null) {

            departure = departureIntent
            destination = destinationIntent
            layover = layoverIntent!!

        }

        Log.d("riding", "departure 확인 : ${departure}")


        riding_course_textview.text=sharedPlaces!!.title

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
//                Log.d("tmap", "oncreate responsefeature list[i+1] : ${responseFeatureList[1].geometry}")

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
        polyline.lineWidth = 4F
        polyline.lineColor = Color.parseColor("#0BE795")
        polyline.outLineColor = Color.parseColor("#0BE795")

        timerIsRunning = !timerIsRunning
        if (timerIsRunning) startTimer()

        Handler().postDelayed({

            for (res in responseFeatureList){

                var resCoordList=res.geometry.coordinates
                for (rescoord in resCoordList){
                    var restext=rescoord.toString()

                    if(restext.indexOf("[")==0){
//                        Log.d("tmap", "rescoord 확인 : ${rescoord}")
                        var array=rescoord as ArrayList<Any>
                        var coord=Coordinate()
                        coord.lat= array[1].toString()
                        coord.lng= array[0].toString()
                        ResponsecoordList.add(coord)

                    }
                }
            }

            //코스 경로 지도에 세팅
            getCoordinates()



            if (responseFeatureList.size>0){
                var setdistance=responseFeatureList[i+1].properties.distance
                var setaddr=responseFeatureList[i+1].properties.name
                riding_mainGuide_textview.text="${setdistance}m"
                riding_mainAddr_textview.text="${setaddr}"
                if (i<responseFeatureList.size){
                    i += 2
                    if(i<responseFeatureList.size){
                        var subdistance=responseFeatureList[i+1].properties.distance
                        riding_subGuide_textview.text="${subdistance}m"
                    }
                }
            }else{
                Log.d("tmap", "postdelayed : no response feature list")

            }

        }, 500)

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
            pushRef.child("Record/time").setValue(riding_timer_textview.text)
            pushRef.child("Record/date").setValue(todayDate)
            pushRef.child("Record/startName").setValue(departure.name)
            pushRef.child("Record/startLat").setValue(departure.frontLat)
            pushRef.child("Record/startLon").setValue(departure.frontLon)
            pushRef.child("Record/dstName").setValue(destination.name)
            pushRef.child("Record/dstLat").setValue(destination.frontLat)
            pushRef.child("Record/dstLon").setValue(destination.frontLon)
            pushRef.child("Record/layoverName").setValue(layover.name)
            pushRef.child("Record/layoverLat").setValue(layover.frontLat)
            pushRef.child("Record/layoverLon").setValue(layover.frontLon)
            pushRef.child("Record/text").setValue("사용자가 생각 쓰는 부분")
            pushRef.child("Record/title").setValue(sharedPlaces!!.title)
            pushRef.child("Photo/${photoCount}").setValue("photo url")

            //save coordinate
            courseCount=0
            for (coord in ResponsecoordList){
                val latitude: String? = coord.lat
                val longitude: String? = coord.lng
                pushRef.child("Coordinates/$courseCount/lat").setValue(latitude)
                pushRef.child("Coordinates/$courseCount/lng").setValue(longitude)
                courseCount+=1
            }


            val intent = Intent(this, AfterRidingActivity::class.java).apply {
                putExtra("time", riding_timer_textview.text)
                putExtra("pushKey", pushRef.key)
                putExtra("departure", departure)
                putExtra("destination", destination)
                putExtra("layover", layover)
                putExtra("sharing", sharedPlaces)

            }.run {startActivity(this) }

//            intent.putExtra("type", "departure")
//            Log.d("tmap", "list[i+1] : ${responseFeatureList[i+1].geometry}")


        }

        riding_guide_layout.setOnClickListener {
//            Log.d("tmap", "click check")
//            Log.d("tmap","잘 넘어왔는지 확인. departure : ${departure}")
//            Log.d("tmap","잘 넘어왔는지 확인. destination : ${destination}")
//            Log.d("tmap","잘 넘어왔는지 확인. layover : ${layover}")
//            Log.d("tmap", "list[i] : ${responseFeatureList[i]}")

//            for (res in responseFeatureList){
//
//                var resCoordList=res.geometry.coordinates
//                for (rescoord in resCoordList){
//                    var restext=rescoord.toString()
//
//                    if(restext.indexOf("[")==0){
////                        Log.d("tmap", "rescoord 확인 : ${rescoord}")
//                        var array=rescoord as ArrayList<Any>
//                        var coord=Coordinate()
//                        coord.lat= array[1].toString()
//                        coord.lng= array[0].toString()
//                        ResponsecoordList.add(coord)
//
//                    }
//                }
//            }
//
//            //코스 경로 지도에 세팅
//            getCoordinates()

            //길 안내 세팅
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
        }



        // 카메라 연결
        riding_camera_imgview = findViewById(R.id.riding_camera_imgview)
        riding_camera_imgview.setOnClickListener{
            
            Log.d("CameraClick", "touch됨")
            val cameraPermissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
            if (cameraPermissionCheck != PackageManager.PERMISSION_GRANTED) { // 권한이 없는 경우
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 1000)
            } else { //권한이 있는 경우
                val REQUEST_IMAGE_CAPTURE = 1
                Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                    takePictureIntent.resolveActivity(packageManager)?.also {
                        dispatchTakePictureIntent()
                        uploadImage(photoURI) // 이미지 firestore에 업로드
                    }
                }
            }
        }


    } //onCreate 함수 종료 지점



    // 카메라 권한 없을 때
/*
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1000) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
*/
    // 카메라 관련 : 이미지 firestore에 업로드
    private fun uploadImage(uri : Uri) {

        var storage : FirebaseStorage ?= FirebaseStorage.getInstance()
        var uid = FirebaseAuth.getInstance().currentUser?.uid

        // 파일 이름 설정
        fileName = "IMAGE_${uid}_${SimpleDateFormat("yyyymmdd_HHmmss").format(Date())}_.png"

        // 클라우드 파일을 가리키는 포인터
        var imageRef = storage!!.reference.child("community").child(fileName)

        // 이미지 파일 업로드
        imageRef.putFile(uri!!).addOnSuccessListener {
            Toast.makeText(this, " 업로드 성공" + imageRef.toString() , Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            Toast.makeText(this, " 업로드 실패" , Toast.LENGTH_SHORT).show()
        }
    }
    private fun requestPermission(){
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,CAMERA),1)

    }
    private fun checkPermission():Boolean{

        return (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)

    }


    /*
    @Override
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if( requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "권한 설정 OK", Toast.LENGTH_SHORT).show()
        }
        else
        {
            Toast.makeText(this, "권한 허용 안됨", Toast.LENGTH_SHORT).show()
        }
    }



     */


    // 카메라 관련
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    photoURI = FileProvider.getUriForFile(
                        this,"com.example.moti.fileprovider", it   // 수정해야 됨
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }




    // 카메라 관련
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File ?= getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
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
//        request.startX="126.92365493654832" // 시작 위치
//        request.startY="37.556770374096615" // 시작 위치
//        request.angle="1"
//        request.speed="60"
//        request.endPoiId="334852"
//        request.endX="126.92432158129688" // 도착 위치
//        request.endY="37.55279861528311" // 도착 위치
//        request.passList="126.92774822,37.55395475" // 경유지 목록
//        request.reqCoordType="WGS84GEO"
//        request.startName="출발지" // 출발지 이름
//        request.endName="도착지" // 도착지 이름
//        request.searchOption="0"
//        request.resCoordType="WGS84GEO"
        request.startX="${departure.frontLon}" // 시작 위치
        request.startY="${departure.frontLat}"// 시작 위치
        request.angle="1"
        request.speed="60"
        request.endPoiId="334852"
        request.endX="${destination.frontLon}" // 도착 위치
        request.endY="${destination.frontLat}" // 도착 위치
        request.passList="${layover.frontLon},${layover.frontLat}" // 경유지 목록
        request.reqCoordType="WGS84GEO"
        request.startName="출발지" // 출발지 이름
        request.endName="도착지" // 도착지 이름
        request.searchOption="0"
        request.resCoordType="WGS84GEO"
    }

    fun getCoordinates(){

        for (coord in ResponsecoordList){
            val latitude: Double = coord.lat!!.toDouble()
            val longitude: Double = coord.lng!!.toDouble()
            var point= TMapPoint(latitude, longitude)
            polyline.addLinePoint(point)
            tmapview!!.addTMapPolyLine("line", polyline)
        }

//        database.child("user/$uid/course/date").child(todayDate).child("-Mp6jea0jyVGqqdUmII_").get().addOnSuccessListener {
//            var post=it.children
//            for(p in post){
//                var pvalue=p.value
//                Log.i("firebase", "check value $pvalue")
//                var cord = Coordinate()
//                cord.lat = p.child("lat").value.toString()
//                cord.lng = p.child("lng").value.toString()
//                Log.i("firebase", "check cord lng ${cord.lng}")
//                coordList.add(cord)
//            }
//
//            //set coordinates to polyline
//            for (coord in coordList){
//                val latitude: Double = coord.lat!!.toDouble()
//                val longitude: Double = coord.lng!!.toDouble()
//                var point= TMapPoint(latitude, longitude)
//                polyline.addLinePoint(point)
//                tmapview!!.addTMapPolyLine("line", polyline)
//            }
//
//        }

    }


    override fun onLocationChange(location: Location) {
//        tmapview!!.setLocationPoint(location.longitude, location.latitude)
//        tmapview!!.setCenterPoint(location.longitude, location.latitude)

        tmapview!!.setLocationPoint(departure.frontLon.toDouble(), departure.frontLat.toDouble())
        tmapview!!.setCenterPoint(departure.frontLon.toDouble(), departure.frontLat.toDouble())

        var fulladdr=getCompleteAddressString(this, location.latitude, location.longitude)
        riding_gps_textview.text=fulladdr.split(" ")[2]

        var lat=location.latitude
        var long=location.longitude
        var point=TMapPoint(lat, long)

        //save coordinate
//        pushRef.child("Coordinates/$courseCount/lat").setValue("$lat")
//        pushRef.child("Coordinates/$courseCount/lng").setValue("$long")
//        courseCount+=1

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