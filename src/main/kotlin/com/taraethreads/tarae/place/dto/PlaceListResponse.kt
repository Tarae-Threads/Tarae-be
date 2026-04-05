package com.taraethreads.tarae.place.dto

import com.taraethreads.tarae.place.domain.Place
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "장소 목록 응답")
data class PlaceListResponse(
    @Schema(description = "장소 ID", example = "1") val id: Long,
    @Schema(description = "장소명", example = "실과 바늘") val name: String,
    @Schema(description = "지역", example = "서울") val region: String,
    @Schema(description = "동네", example = "성수") val district: String,
    @Schema(description = "주소", example = "서울 성동구 연무장길 1") val address: String,
    @Schema(description = "운영 상태", example = "OPEN") val status: String,
    val categories: List<CategoryInfo>,
    val tags: List<TagInfo>,
    @Schema(description = "인스타그램 URL") val instagramUrl: String?,
    @Schema(description = "네이버 지도 URL") val naverMapUrl: String?,
) {
    data class CategoryInfo(val id: Long, val name: String)
    data class TagInfo(val id: Long, val name: String)

    companion object {
        fun from(place: Place) = PlaceListResponse(
            id = place.id,
            name = place.name,
            region = place.region,
            district = place.district,
            address = place.address,
            status = place.status.name,
            categories = place.categories.map { CategoryInfo(it.id, it.name) },
            tags = place.tags.map { TagInfo(it.id, it.name) },
            instagramUrl = place.instagramUrl,
            naverMapUrl = place.naverMapUrl,
        )
    }
}
