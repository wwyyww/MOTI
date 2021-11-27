package com.example.moti

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.moti.RidingActivity.Companion.PERMISSION_REQUEST_CODE
import com.skt.Tmap.TMapGpsManager
import com.skt.Tmap.TMapPoint
import kotlinx.android.synthetic.main.activity_search_poi.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.*


class SearchPoi : AppCompatActivity(), TMapGpsManager.onLocationChangedCallback{

    lateinit var imgV_compass : ImageView
    lateinit var editT_searchPoi : EditText
    lateinit var btn_searchPoi : Button
    lateinit var destination : String
    lateinit var retrofit: Retrofit
    lateinit var tmapApi :TmapAPI
    lateinit var recyV_poiItemList: RecyclerView
    lateinit var poiItemAdapter : PoiItemAdapter


    lateinit var locatioNManager: LocationManager
    val PERMISSIONS_REQUEST_CODE = 100
    var REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION)

    var poiItemList = mutableListOf<Poi>()


    // gps 관련
    lateinit var longitude : String
    lateinit var latitude : String


    companion object {
        const val PERMISSION_REQUEST_CODE = 1001
    }
    // T Map GPS
    var tMapGPS: TMapGpsManager? = null
    lateinit var currentPointGeo: TMapPoint
    lateinit var address:String


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_poi)

        val intent: Intent = getIntent()
        var type : String? = intent.getStringExtra("type")


        //action bar 숨기기
        var actionBar: ActionBar?
        actionBar=supportActionBar
        actionBar?.hide()


        // Retrofit & tmapAPI 불러오기
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL_TMAP_API)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        tmapApi = retrofit.create(TmapAPI::class.java)



        editT_searchPoi = findViewById(R.id.editT_searchPoi)
        btn_searchPoi = findViewById(R.id.btn_searchPoi)
        recyV_poiItemList = findViewById(R.id.recyV_poiItemList)
        imgV_compass = findViewById(R.id.imgV_compass)

        //위치 권한 확인
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "권한 승인", Toast.LENGTH_SHORT).show()
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), // 1
                SearchPoi.PERMISSION_REQUEST_CODE
            ) // 2
        }

        // GPS using T Map
        tMapGPS = TMapGpsManager(this)

        // Initial Setting
        tMapGPS!!.minTime=1000
        tMapGPS!!.minDistance= 10F
        tMapGPS!!.provider= TMapGpsManager.NETWORK_PROVIDER

        tMapGPS!!.OpenGps()




        poiItemAdapter = type?.let { PoiItemAdapter(this, it) }!!
        recyV_poiItemList.adapter = poiItemAdapter


        imgV_compass.setOnClickListener{

        }



        btn_searchPoi.setOnClickListener {

            if (this::longitude.isInitialized && this::latitude.isInitialized)
            {

                destination = editT_searchPoi.text.toString()
                Toast.makeText(this, destination, Toast.LENGTH_SHORT).show()
                Log.d("successM", "성공 : ${destination}")
                CallSearchPoi(destination)  // api 함수 호출
            }
            else {
                //onRequestPermissionsResult()
                Toast.makeText(this, "권한 요청 필요", Toast.LENGTH_SHORT).show()

            }
        }



        // 명칭(POI) 검색 api  호출을 위해 payload 담기
//        var request = PoiParamRqst()
//        request.version


    }



    fun CallSearchPoi(searchPoi: String){
        // + 추가 : 현재 위치 반영해서 api 호출하기 => radius(거리) 받아오기 위함

        // 명칭(POI) 검색 api 호출
        val callSearchPoi = tmapApi.searchPoi(version = "1", searchKeyword = searchPoi,centerLon=longitude, centerLat= latitude, appKey = APPKEY)

        callSearchPoi.enqueue(object : Callback<PoiParamRspns> {

            override fun onResponse(
                call: Call<PoiParamRspns>,
                response: Response<PoiParamRspns>,
            ) {
                Log.d("successM", "[Poi] 성공1: ${response.raw()}")
                Log.d("successM", "[Poi] 성공2 : ${response.body()?.searchPoiInfo?.pois?.poi}")
                // Log.d("successM", "성공 : ${response.body()}")
                Log.d("successM", "[Poi] 성공3 : ${response.headers()}")

                if (response.body() != null) {

                    //Toast.makeText(this@SearchPoi, response.body().toString(), Toast.LENGTH_SHORT).show()

                    poiItemList.apply {
                        poiItemList = response.body()!!.searchPoiInfo.pois.poi as MutableList<Poi>
                        poiItemAdapter.poiItemList = poiItemList
                        poiItemAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onFailure(call: Call<PoiParamRspns>, t: Throwable) {
                Log.d("errorM", t.message.toString() )
            }
        })
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            SearchPoi.PERMISSION_REQUEST_CODE -> {  // 1
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
                            SearchPoi.PERMISSION_REQUEST_CODE
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

    override fun onLocationChange(location: Location) {
        currentPointGeo = TMapPoint(location.latitude, location.longitude)


        latitude = location.latitude.toString()
        longitude = location.longitude.toString()

        textView11.text = getCompleteAddressString(this, location.latitude, location.longitude)

        Log.d("SUCCESSM", "Raw: ${latitude.toString()}")
        Log.d("SUCCESSM", "Raw: ${longitude.toString()}")


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