package com.example.moti

import android.Manifest
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

class RidingActivity:AppCompatActivity(), TMapGpsManager.onLocationChangedCallback  {

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


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riding)
        riding_course_textview.bringToFront()
        riding_gps_textview.bringToFront()
        riding_button.bringToFront()


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
        tMapGPS!!.minTime=1000
        tMapGPS!!.minDistance= 10F
        tMapGPS!!.provider=NETWORK_PROVIDER

        tMapGPS!!.OpenGps()
        polyline= TMapPolyLine()

        riding_button.setOnClickListener{
            //getCoordinates()

        }

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
//            for (coord in coordList){
//                val latitude: Double = coord.lat!!.toDouble()
//                val longitude: Double = coord.lng!!.toDouble()
//                var point= TMapPoint(latitude, longitude)
//                polyline.addLinePoint(point)
//                tmapview!!.addTMapPolyLine("line", polyline)
//            }

        }
    }





    override fun onLocationChange(location: Location) {
        tmapview!!.setLocationPoint(location.longitude, location.latitude)
        tmapview!!.setCenterPoint(location.longitude, location.latitude)

        var lat=location.latitude
        var long=location.longitude
        var point=TMapPoint(lat, long)

        //save coordinate
        pushRef.child("$courseCount/lat").setValue("$lat")
        pushRef.child("$courseCount/lng").setValue("$long")
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