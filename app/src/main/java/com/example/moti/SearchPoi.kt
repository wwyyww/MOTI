package com.example.moti

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchPoi : AppCompatActivity() {

    lateinit var EditT_searchPoi : EditText
    lateinit var Btn_searchPoi : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_poi)

        // Retrofit & tmapAPI 불러오기
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL_TMAP_API)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val tmapApi = retrofit.create(TmapAPI::class.java)


        var department : String

        // 장소 검색 입력값 가져오기
        EditT_searchPoi = findViewById(R.id.editT_searchPoi)
        Btn_searchPoi = findViewById(R.id.btn_searchPoi)


        Btn_searchPoi.setOnClickListener {
            EditT_searchPoi.text
        }


        // 명칭(POI) 검색 api  호출을 위해 payload 담기
//        var request = PoiParamRqst()
//        request.version


        // 명칭(POI) 검색 api 호출
        val callSearchPoi = tmapApi.searchPoi(version = "1", searchKeyword = "서울여자대학교", appKey = APPKEY)


        callSearchPoi.enqueue(object : Callback<PoiParamRspns> {

            override fun onResponse(
                call: Call<PoiParamRspns>,
                response: Response<PoiParamRspns>
            ) {
                Log.d("successM", "[Poi] 성공 : ${response.raw()}")
                Log.d("successM", "[Poi] 성공 : ${response.body()}")
                // Log.d("successM", "성공 : ${response.body()}")
                Log.d("successM", "[Poi] 성공 : ${response.headers()}")
                Toast.makeText(this@SearchPoi, response.body().toString(), Toast.LENGTH_SHORT);
            }

            override fun onFailure(call: Call<PoiParamRspns>, t: Throwable) {
                Log.d("errorM", t.message.toString() )
            }
        })

    }
}