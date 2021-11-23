package com.example.moti

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitBuilder {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://apis.openapi.sk.com/tmap")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val tmapApi = retrofit.create(TmapAPI::class.java)
}
