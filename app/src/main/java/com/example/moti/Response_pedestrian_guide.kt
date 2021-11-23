package com.example.moti

/*
data class response_pedestrian_guide(
    var type1 : String,
    var features : Int?,
    var type2 : String?,
    var geometry : Int?,
    var type3 : String?,
    var coordinates : String?,
    var properties  : String?,
    var index : String?,
    var pointIndex : Int?,
    var name : String,
    var description : String,
    var direction : Int,
    var intersectionName : String,
    var nearPoiX: String,
    var nearPoiY: String,
    var nearPoiName: String,
    var turnType : Int ,
    var pointType : String,
    var facilityType : String,
    var facilityName : String,
    var totalDistance : Int,
    var totalTime : Int ,
    var type4 : String,
    var geometry2: Int,
    var type5 : String?,

    var coordinates2: String?,
    var properties2: Int,
    var index2: String?,
    var lineIndex: String?,
    var name2: String?,
    var description2: String?,
    var direction2: String?,
    var time: String?,
    var distance: String?
)
*/

/*
data class response_pedestrian_guide(
    var totalDistance : Int?,
    var totalTime : Int?,
    var index : Int?,
    var pointIndex : Int?,
    var name : String?,
    var description : String?,
    var directions: String?,
    var nearPoiName : String?,
    var nearPoiX : String?,
    var nearPoiY: String?,
    var intersectionName : String?,
    var facilityType : String?,
    var facilityName : String?,
    var turnType : Int?,
    var pointType : String?
)
*/

data class response_pedestrian_guide(
    var type : String,
    var features : List<response_features>
)

data class response_features(
    var type : String,
    var geometry : geometry,
    var properties : properties,
)


data class geometry(
    var type: String,
    var coordinate : List<String>
)


data class properties(

    var totalDistance : String?,
    var totalTime : String?,
    var index : Number?,
    var pointIndex : Number?,
    var name : String?,
    var description : String?,
    var directions: String?,
    var nearPoiName : String?,
    var nearPoiX : String?,
    var nearPoiY: String?,
    var intersectionName : String?,
    var facilityType : Number?,
    var facilityName : String?,
    var turnType : Number?,
    var pointType : String?,
)

