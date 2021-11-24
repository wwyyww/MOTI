package com.example.moti

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PoiItem (
        var type : String, // 출발지, 도착지, 경유지 구분을 위함
        var name : String, // 장소명
        var lowerBizName : String, // 업종 소분류명
        var radius : String, // 거리
        var fullAddressRoad : String, // 전체 도로명 주소
        var lowerAddrName : String, // 표출 주소 소분류명 (ex. 소사동)
        var frontLat : String, // 시설물 입구 위도 좌표
        var frontLon : String, // 시설물 입구 경도 좌표
 ) : Parcelable