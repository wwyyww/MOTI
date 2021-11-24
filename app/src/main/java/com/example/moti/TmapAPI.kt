package com.example.moti


import retrofit2.Call
import retrofit2.http.*

interface TmapAPI {
   // 길 안내
    @POST("tmap/routes/pedestrian")
    @Headers("Content-Type:application/x-www-form-urlencoded")
    fun guidePedestrian(
        @Query("appKey") appKey: String?,
        @Query("startX") startX: String?,
        @Query("startY") startY: String?,
        @Query("angle") angle: String?,
        @Query("speed") speed: String?,
        @Query("endPoild") endPoild: String?,
        @Query("endX") endX: String?,
        @Query("endY") endY: String?,
        @Query("passList") passList: String?,
        @Query("reqCoordType") reqCoordType: String?,
        @Query("startName") startName: String?,
        @Query("endName") endName: String?,
        @Query("searchOption") searchOption: String?,
        @Query("resCoordType") resCoordType: String?,
       // @Query("sort") sort: String?,
    ): Call<response_pedestrian_guide>


    // 명칭 상세 검색
    @GET("tmap/pois")
    @Headers("Content-Type:application/x-www-form-urlencoded")
    fun searchPoi(
        @Query("version") version: String?,
        @Query("page") page: String? = null,
        @Query("count") count: String? = null,
        @Query("searchKeyword") searchKeyword: String? = null,
        @Query("areaLLCode") areaLLCode: String? = null,
        @Query("areaLMCode") areaLMCode: String? = null,
        @Query("resCoordType") resCoordType: String? = null,
        @Query("searchType") searchType: String? = null,
        @Query("multiPoint") multiPoint: String? = null,
        @Query("searchtypCd") searchtypCd: String? = null,
        @Query("radius") radius: String? = null,
        @Query("reqCoordType") reqCoordType: String? = null,
        @Query("centerLon") centerLon: String? = null,
        @Query("centerLat") centerLat: String? = null,
        @Query("poiGroupYn") poiGroupYn: String? = null,
        @Query("callback") callback: String? = null,
        @Query("appKey") appKey: String?,
    ) : Call<PoiParamRspns>

}