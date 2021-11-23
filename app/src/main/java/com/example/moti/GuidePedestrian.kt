package com.example.moti


import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.skt.Tmap.TMapView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val APPKEY = "l7xx9df189c56f96410aa6b97c8043bb91d5"
const val BASE_URL_TMAP_API = "https://apis.openapi.sk.com/"

class GuidePedestrian : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_directions)

        /*
        val linearLayoutTmap = findViewById<View>(R.id.linearLayoutTmap) as LinearLayout
        val tMapView = TMapView(this)

        tMapView.setSKTMapApiKey("l7xx9df189c56f96410aa6b97c8043bb91d5")
        linearLayoutTmap.addView(tMapView)
        */

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL_TMAP_API)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val tmapApi = retrofit.create(TmapAPI::class.java)



        // 길찾기 api 호출을 위해 payload 담기
        var request = requst_pedestrian_guide()
        request.startX="126.92365493654832"
        request.startY="37.556770374096615"
        request.angle="1"
        request.speed="60"
        request.endPoiId="334852"
        request.endX="126.92432158129688"
        request.endY="37.55279861528311"
        request.passList="126.92774822,37.55395475"
        request.reqCoordType="WGS84GEO"
        request.startName="출발지"
        request.endName="도착지"
        request.searchOption="0"
        request.resCoordType="WGS84GEO"

        // 길찾기 api 호출
        val callGuidePedestrian = tmapApi.guidePedestrian(APPKEY, request.startX,request.startY,
            request.angle, request.speed, request.endPoiId, request.endX, request.endY, request.passList,
            request.reqCoordType, request.startName, request.endName, request.searchOption, request.resCoordType)


        callGuidePedestrian.enqueue(object : Callback<response_pedestrian_guide>{

            override fun onResponse(
                call: Call<response_pedestrian_guide>,
                response: Response<response_pedestrian_guide>
            ) {
                Log.d("successM", "성공 : ${response.raw()}")
                Log.d("successM", "성공 : ${response.body()}")
               // Log.d("successM", "성공 : ${response.body()}")
                Log.d("successM", "성공 : ${response.headers()}")

                Toast.makeText(this@GuidePedestrian, response.body().toString(), Toast.LENGTH_SHORT);
            }

            override fun onFailure(call: Call<response_pedestrian_guide>, t: Throwable) {
                Log.d("errorM", t.message.toString() )
            }
        })


    }
}