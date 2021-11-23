package com.example.moti

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.View.MeasureSpec
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.skt.Tmap.*
import com.skt.Tmap.TMapGpsManager.NETWORK_PROVIDER
import kotlinx.android.synthetic.main.test.*
import java.util.*


@Suppress("DEPRECATION")
class test:AppCompatActivity(), TMapGpsManager.onLocationChangedCallback  {


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
    lateinit var currentPointGeo: TMapPoint
    lateinit var address:String



    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test)
        test_button.bringToFront()

        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

        todayDate = (mCalendar.get(Calendar.YEAR)).toString() + "/" + (mCalendar.get(Calendar.MONTH) + 1).toString() +
                "/" + (mCalendar.get(Calendar.DAY_OF_MONTH)).toString()

//        pushRef=database.child("user/$uid/course/date").child(todayDate).push()


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
        test_tmap_layout.addView(tmapview)

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
        tMapGPS!!.minTime=1000
        tMapGPS!!.minDistance= 10F
        tMapGPS!!.provider=NETWORK_PROVIDER

        tMapGPS!!.OpenGps()
        polyline= TMapPolyLine()

//        var tmapdata=TMapData()
//        var address=tmapdata.convertGpsToAddress(37.63183831411279, 127.07751348422546)
//        Log.i("tmap", "주소 확인 $address")



        test_button.setOnClickListener{

            getPoint()
//            address = getCompleteAddressString(this, currentPointGeo.latitude, currentPointGeo.longitude)
//            Log.i("tmap", "변환 주소 : $address")

//            Projection projection = googleMap.getProjection();


//            getCoordinates()
//            var point= TMapPoint(37.64001039557176, 127.06858864059623)
//            var onepoint= TMapPoint(37.640955197578386, 127.06668769580261)
//            var twopoint= TMapPoint(37.64680199247718, 127.07095015322732)
//
//            var marker=TMapMarkerItem()
//            var bitmap=BitmapFactory.decodeResource(resources, R.drawable.common_google_signin_btn_icon_dark)
//            marker.icon=bitmap
//            marker.setPosition(0.5f, 1.0f)
//            marker.tMapPoint=point
//            marker.name="홈플러스"
//
//            var onemarker=TMapMarkerItem()
//            onemarker.icon=bitmap
//            onemarker.setPosition(0.5f, 1.0f)
//            onemarker.tMapPoint=twopoint
//            onemarker.name="홈플러스"
//
//            tmapview!!.addMarkerItem("marker", marker)
//            tmapview!!.addMarkerItem("one marker", onemarker)
//
//
//
//            polyline.addLinePoint(point)
//            polyline.addLinePoint(onepoint)
//            polyline.addLinePoint(twopoint)
//
//            tmapview!!.addTMapPolyLine("second", polyline)


        }


    }

    fun getPoint(){
        var x = tmapview!!.getRotatedMapXForPoint(currentPointGeo.latitude, currentPointGeo.longitude)
        var y = tmapview!!.getRotatedMapYForPoint(currentPointGeo.latitude, currentPointGeo.longitude)




//        canvas.drawLine(x, y)

        Log.i("tmap", "check get point : $x, $y")

//        canvas.save()
//        canvas.rotate(-tmapview!!.rotate, tmapview!!.centerPointX.toFloat(),
//            tmapview!!.centerPointY.toFloat()
//        )
//        var xPos=positionX
//        var yPos=positionY



    }


    fun getCoordinates(){

        database.child("user/$uid/course/date").child("2021/11/23").child("-MpAVlAyaXo9sommFfPK").get().addOnSuccessListener {
            var post=it.children
            for(p in post){
                var pvalue=p.value
                Log.i("firebase", "check value $pvalue")
                var cord = Coordinate()
                cord.lat = p.child("lat").value.toString()
                cord.lng = p.child("lng").value.toString()
                var latlng=TMapPoint(cord.lat!!.toDouble(), cord.lng!!.toDouble())
                Log.i("firebase", "check point $latlng")
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





//    private fun createPolylineBitmap(): Bitmap? {
//        val bitmap = Bitmap.createBitmap(
//            tmapview!!.getWidth(),
//            tmapview!!.getHeight(),
//            Bitmap.Config.ARGB_8888
//        )
//        val canvas = Canvas(bitmap)
//        val paint = Paint()
//        paint.setColor(ContextCompat.getColor(this, R.color.holo_green_light))
//        paint.setStrokeWidth(10F)
//        paint.setDither(true)
//        paint.setStyle(Paint.Style.STROKE)
//        paint.setStrokeJoin(Paint.Join.ROUND)
//        paint.setStrokeCap(Paint.Cap.ROUND)
//        paint.setAntiAlias(true)
//        for (i in 0 until coordList.size) {
//            val latLng1 = LatLng(coordList[i].lat!!.toDouble(), coordList[i].lng!!.toDouble())
//            val latLng2 = LatLng(coordList[i + 1].lat!!.toDouble(), coordList[i + 1].lng!!.toDouble())
//            canvas.drawLine(
//                LatLngToPoint(latLng1).x,
//                LatLngToPoint(latLng1).y, LatLngToPoint(latLng2).x, LatLngToPoint(latLng2).y, paint
//            )
//        }
//        return bitmap
//    }
//
//    private fun LatLngToPoint(coordinate: LatLng): Point {
//        val projection=TMapp
//        return projection.toScreenLocation(coordinate)
//    }


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

    override fun onLocationChange(location: Location) {
        currentPointGeo = TMapPoint(location.latitude, location.longitude)
        tmapview!!.setLocationPoint(location.longitude, location.latitude)
        tmapview!!.setCenterPoint(location.longitude, location.latitude)
//        address = getCompleteAddressString(this, currentPointGeo.latitude, currentPointGeo.longitude)
//        Log.i("tmap", "변환한 주소 : $address")


    }




}