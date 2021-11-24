package com.example.moti

data class PoiParamRspns(
    var searchPoiInfo : SearchPoiInfo
)

data class SearchPoiInfo(
    var count : String,
    var page : String,
    var totalCount : String,
    var pois : Pois
)

data class Pois(
    var poi : List<Poi>
)

data class Poi(
    var dataKind : String?,
    var detailBizName : String?,
    var detailInfoFlag : String?,
    var lowerAddrName : String?,
    var frontLon : String?,
    var roadName : String?,
    var newAddressList : NewAddressList,
    var bizName : String?,
    var lowerBizName : String?,
    var upperAddrName : String?,
    var frontLat : String?,
    var id : String?,
    var pkey : String?,
    var radius : String?,
    var rpFlag : String?,
    var middleAddrName : String?,
    var detailAddrName : String?,
    var secondNo : String?,
    var mlClass : String?,
    var telNo : String?,
    var secondBuildNo : String?,


    var noorLon : String?,
    var middleBizName : String?,
    var collectionType : String?,
    var noorLat : String?,
    var firstBuildNo : String?,
    var firstNo : String?,
    var upperBizName : String?,
    var name : String?,
    var parkFlag : String?,
    var navSeq : String?,
    var evChargers : EvChargers,
    var desc : String?
    )

data class EvChargers(
    var evCharger : List<String>
)


data class NewAddressList(
    var newAddress : List<NewAddress>
)


data class NewAddress(
    var fullAddressRoad : String,
    var centerLon : String,
    var roadId : String,
    var frontLat : String,
    var centerLat : String,
    var bldNo2 : String,
    var bldNo1 : String,
    var frontLon : String,
    var roadName : String,
)

