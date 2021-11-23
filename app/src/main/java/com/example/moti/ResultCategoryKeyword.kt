//검색 결과를 담는 클래스

data class ResultCategoryKeyword (
    var meta: PlaceMeta,                // 장소 메타데이터
    var documents: List<Place>          // 검색 결과
)
