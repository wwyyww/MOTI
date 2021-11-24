package com.example.moti

data class PoiParamRspns(
    var searchPoiInfo : SearchPoiInfo
)

data class SearchPoiInfo(
    var count : String, // 페이지당 출력되는 개수
    var page : String,  // 조회한 페이지 번호
    var totalCount : String, // 조회 결과의 총 개수
    var pois : Pois //POI 목록
)

data class Pois( // POI 목록
    var poi : List<Poi>
)

data class Poi( // POI 정보
    var dataKind : String?, //
    var detailBizName : String?,
    var detailInfoFlag : String?,

    var frontLon : String?,
    var roadName : String?, // 도로명



    var newAddressList : NewAddressList,
    var bizName : String?,


    var frontLat : String?,
    var id : String?, // POI ID
    var pkey : String?, //POI 식별자
    var radius : String?, // 거리 (요청 좌표에서 떨어진 거리) 단위 km
    var rpFlag : String?,
    var upperAddrName : String?, // 표출 주소 대 분류명 (경기)
    var lowerAddrName : String?, // 표출 주소 소 분류명 (소사동)
    var middleAddrName : String?, // 표출 주소 중 분류명 (부천시 원마구)
    var detailAddrName : String?, // 표출 주소 세분류명 (미주아파트 111동 22호)
    var secondNo : String?,
    var mlClass : String?,
    var telNo : String?,
    var secondBuildNo : String?,


    var noorLon : String?,
    var upperBizName : String?, // 업종 대분류명 (의료편의)
    var middleBizName : String?, // 업종 중분류명 (의료시설)
    var lowerBizName : String?, // 업종 소 분류명 (종합병원)
    var collectionType : String?,
    var noorLat : String?,
    var firstBuildNo : String?,
    var firstNo : String?,

    var name : String?, // 시설물 명칭
    var parkFlag : String?,
    var navSeq : String?, // 입구점 일련번호
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
    var fullAddressRoad : String, // 전체 도로명 주소 (서울 중구 을지로 65)
    var centerLon : String,
    var roadId : String,
    var frontLat : String,
    var centerLat : String,
    var bldNo2 : String, // 건물번호 2
    var bldNo1 : String, // 건물번호 1
    var frontLon : String,
    var roadName : String,
)

