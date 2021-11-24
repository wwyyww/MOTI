package com.example.moti

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    /*
    var uid: String ?= "",
    var email: String ?= "",
    var password: String ?= "",
    var nickname: String ?= "",
    var phone: String ?= "",
    var credit: Int ?= 0,
    var grade: Int ?= 1,
    var lastAuth: String ?= "", // 일 인증 횟수 1회로 제한을 위해 마지막 인증 날짜 저장
    var lastAuthPost : String ?= "", // 마지막 인증시 올린 글 id
    var joindate: String ?= "",
    //var posts : String ?= ""
    var posts:  MutableMap<String, Boolean> = HashMap(),  // 게시물 목록

     */

    var uid : String ?= "",
    var id :  String ?= "",
    var pw : String ?= "",
    var tel : String ?= "",
    var birth : String ?= "",
    var sex : String ?= "",
    var name : String ?= "",
    var nickname : String ?= "",
    var local : String ?= "",
    var taste : String ?= "",
    var freindNum : Int ?= 0,
    var recordNum : String ?= "",
    var isActive : Boolean ?= false,
    var location : String ?= "",
){
    /*
       data class TmpRecord(
         var startPhoto : String ?= null,
         var endPhoto : String ?= null,
         var endQr : String ?= null
       )


       data class Record(

       )
     */

    @Exclude
    fun toMap() : Map<String, Any?>{
        return mapOf(
            "uid" to uid,
            "pw" to pw,
            "tel" to tel,
            "birth " to birth ,
            "sex" to sex,
            "name" to name,
            "nickname" to nickname,
            "local " to local,
            "taste " to taste,
            "freindNum " to freindNum ,
            "recordNum" to recordNum,
            "isActive" to isActive,
            "location" to location,
        )
    }

}