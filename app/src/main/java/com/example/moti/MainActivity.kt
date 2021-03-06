package com.example.moti

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.skt.Tmap.TMapGpsManager.NETWORK_PROVIDER
import com.skt.Tmap.*
import kotlinx.android.synthetic.main.activity_main2.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*



class MainActivity : AppCompatActivity(), TMapGpsManager.onLocationChangedCallback {

    var tmapview: TMapView? = null
    lateinit var search_place: Button
    lateinit var lat : String
    lateinit var long : String


    // T Map GPS
    var tMapGPS: TMapGpsManager? = null
    lateinit var currentPointGeo: TMapPoint
    lateinit var address:String

    lateinit var toggle: ActionBarDrawerToggle
    lateinit var main_total : Button

    //db용
    private var auth : FirebaseAuth? = null
    private lateinit var database: DatabaseReference
    private val CurrentUser = FirebaseAuth.getInstance().currentUser
    val uid = CurrentUser?.uid

    var nowSelectedPlace = Post()


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        transparentStatusAndNavigation()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT


        val drawerLayout : DrawerLayout = findViewById(R.id.main_draw)
        val navView : NavigationView = findViewById(R.id.nav)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()


        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setBackgroundColor(resources.getColor(R.color.white))

        navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.mypage -> {
                    //Toast.makeText(applicationContext, "mymy", Toast.LENGTH_SHORT).show()
                    var intent = Intent(this, Mypage::class.java)
                    startActivity(intent)
                }
                //R.id.mypage2 -> Toast.makeText(applicationContext, "mymy", Toast.LENGTH_SHORT).show()
            }
            true

        }

        val actionBar:ActionBar?
        actionBar=supportActionBar
        actionBar!!.title = ""

        actionBar.setBackgroundDrawable(ColorDrawable(Color.parseColor("#00000000")))
        //actionBar.setStackedBackgroundDrawable(ColorDrawable(Color.parseColor("#00000000")))


        //db 세팅
        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

        search_place = findViewById(R.id.search_place)
        main_total = findViewById(R.id.main_total)

        database.child("community/-MpY8fK8ByYzUzpLkoEL").get().addOnSuccessListener {
            activity_main_course_textview.text=it.child("title").value.toString()
            var date=it.child("date").value.toString()
            var recordId=it.child("recordId").value.toString()
            nowSelectedPlace.date=date
            nowSelectedPlace.recordId=recordId
            nowSelectedPlace.riderId=uid
            database.child("user/$uid/course/date/$date/$recordId/Record").get().addOnSuccessListener { itChild ->
                activity_main_depart_textview.text=itChild.child("startName").value.toString()
                activity_main_arrival_textview.text=itChild.child("dstName").value.toString()

            }
        }

        main_total.setOnClickListener {
            var intent = Intent(this, CommunityMain::class.java)
            startActivity(intent)
        }



        val current = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now()
        } else {
            TODO("VERSION.SDK_INT < O")
        }
//        val formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy")
        val formatter = DateTimeFormatter.ofPattern("MM.dd EE요일 a hh:mm ")
        val formattedDate = current.format(formatter)

        activity_main_date_textview.text=formattedDate

        //actionBar?.hide()

        //tmap 세팅
        tmapview = TMapView(this)
        tmapview!!.setSKTMapApiKey("l7xx3a42dd9f094c468e969018fba936e361")
        tmapview!!.setIconVisibility(true)
        tmapview!!.setCompassMode(true)
        tmapview!!.setTrackingMode(true)
        main_tmaplayout2.addView(tmapview)

        //위치 권한 확인
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "권한 승인", Toast.LENGTH_SHORT).show()
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), // 1
                RidingActivity.PERMISSION_REQUEST_CODE
            ) // 2
        }

        // GPS using T Map
        tMapGPS = TMapGpsManager(this)

        // Initial Setting
        tMapGPS!!.minTime=1000
        tMapGPS!!.minDistance= 10F
        tMapGPS!!.provider=NETWORK_PROVIDER

        tMapGPS!!.OpenGps()

        search_place.setOnClickListener {
            var intent = Intent(this, SearchPlaceActivity::class.java)
            intent.putExtra("lat", this.lat)
            intent.putExtra("long", this.long)
            intent.putExtra("address", this.address.toString())
            startActivity(intent)
        }

        main_total.setOnClickListener {
            var comMainIntent = Intent(this, CommunityMain::class.java)
            startActivity(comMainIntent)
        }

        activity_main_course_layout.setOnClickListener {
            val intent = Intent(this, SelectPlace::class.java)
            intent.putExtra("sharing", nowSelectedPlace)
            startActivity(intent)
        }



//        activity_main_reload_textview.setOnClickListener {
//            address = getCompleteAddressString(this, currentPointGeo.latitude, currentPointGeo.longitude)
//            Log.i("tmap", "변환 주소 : $address")
//
//        }

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
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


    override fun onLocationChange(location: Location) {
        currentPointGeo = TMapPoint(location.latitude, location.longitude)

        tmapview!!.setLocationPoint(location.longitude, location.latitude)
        tmapview!!.setCenterPoint(location.longitude, location.latitude)

        this.lat=location.latitude.toString()
        this.long=location.longitude.toString()
        this.address = getCompleteAddressString(this, location.latitude, location.longitude)
        main_fulladdr_textview.text=getCompleteAddressString(this, location.latitude, location.longitude)
        main_city_textview.text=address.split(" ")[2]
        main_fulladdr_textview.bringToFront()
        main_city_textview.bringToFront()
        Log.d("latitude", "Raw: ${lat.toString()}")
        Log.d("longitude", "Raw: ${long.toString()}")


    }

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
            //window.statusBarColor = Color.TRANSPARENT
            window.navigationBarColor = Color.TRANSPARENT
//            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
//            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = Color.parseColor("#00000000")
            //window.navigationBarColor = Color.parseColor("#00000000")
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



    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            RidingActivity.PERMISSION_REQUEST_CODE -> {  // 1
                if (grantResults.isEmpty()) {  // 2
                    throw RuntimeException("Empty permission result")
                }
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {  // 3
                    Toast.makeText(this, "권한 승인", Toast.LENGTH_SHORT).show()
                } else {
                    if (shouldShowRequestPermissionRationale(
                            Manifest.permission.ACCESS_FINE_LOCATION)) { // 4
                        Log.d(ContentValues.TAG, "User declined, but i can still ask for more")
                        requestPermissions(
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            RidingActivity.PERMISSION_REQUEST_CODE
                        )
                    } else {
                        Log.d(ContentValues.TAG, "User declined and i can't ask")
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