package com.example.moti;

data class PoiParamRqst(
    var version: String?,
    var page: String?,
    var count: String?,
    var searchKeyword: String?,
    var areaLLCode: String?,
    var areaLMCode: String?,
    var resCoordType: String?,
    var searchType: String?,
    var multiPoint: String?,
    var searchtypCd: String?,
    var radius: String?,
    var reqCoordType: String?,
    var centerLon: String?,
    var centerLat: String?,
    var poiGroupYn: String?,
    var callback: String?,
    var appKey: String?
)
