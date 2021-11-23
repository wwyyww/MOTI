package com.example.moti

import KaKaoAPI_category
import KakaoAPI
import ResultCategoryKeyword
import ResultSearchKeyword
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.moti.databinding.ActivityTestKakaoBinding


class KaKaoTestActivity : AppCompatActivity() {
    private lateinit var binding : ActivityTestKakaoBinding

    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
        const val API_KEY = "KakaoAK 638dc203e2c1c41fc03b925e2d80613b"  // REST API 키
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestKakaoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        searchKeyword("은행")
        searchCategory("CE7")

    }
    // 카테고리 검색 함
    private fun searchCategory(category_group_code: String) {
        val retrofit = Retrofit.Builder()   // Retrofit 구성
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(KaKaoAPI_category::class.java)   // 통신 인터페이스를 객체로 생성
        val call = api.getSearchKeyword(API_KEY, category_group_code)   // 검색 조건 입력

        // API 서버에 요청
        call.enqueue(object: Callback<ResultCategoryKeyword> {
            override fun onResponse(
                call: Call<ResultCategoryKeyword>,
                response: Response<ResultCategoryKeyword>
            ) {
                // 통신 성공 (검색 결과는 response.body()에 담겨있음)
                Log.d("Test", "Raw: ${response.raw()}")
                Log.d("Test", "Body: ${response.body()}")
            }

            override fun onFailure(call: Call<ResultCategoryKeyword>, t: Throwable) {
                // 통신 실패
                Log.w("KaKaoTestActivity", "통신 실패: ${t.message}")
            }
        })
    }
    // 키워드 검색 함수
    private fun searchKeyword(keyword: String) {
        val retrofit = Retrofit.Builder()   // Retrofit 구성
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(KakaoAPI::class.java)   // 통신 인터페이스를 객체로 생성
        val call = api.getSearchKeyword(API_KEY, keyword)   // 검색 조건 입력

        // API 서버에 요청
        call.enqueue(object: Callback<ResultSearchKeyword> {
            override fun onResponse(
                call: Call<ResultSearchKeyword>,
                response: Response<ResultSearchKeyword>
            ) {
                // 통신 성공 (검색 결과는 response.body()에 담겨있음)
                Log.d("Test", "Raw: ${response.raw()}")
                Log.d("Test", "Body: ${response.body()}")
            }

            override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                // 통신 실패
                Log.w("KaKaoTestActivity", "통신 실패: ${t.message}")
            }
        })
    }
}