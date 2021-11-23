package com.example.moti

data class Coordinate (
    var lat : String ?= null,
    var lng : String ?= null
){
    fun toMap() : Map<String, Any?>{
        return mapOf(
            "lat" to lat,
            "lng" to lng
        )
    }

}

