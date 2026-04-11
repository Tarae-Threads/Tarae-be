package com.taraethreads.tarae.place.dto

import com.taraethreads.tarae.place.domain.Place
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal

@Schema(description = "장소 상세 응답")
data class PlaceDetailResponse(
    @Schema(description = "장소 ID", example = "1") val id: Long,
    @Schema(description = "장소명", example = "실과 바늘") val name: String,
    @Schema(description = "지역", example = "서울") val region: String,
    @Schema(description = "동네", example = "성수") val district: String,
    @Schema(description = "주소", example = "서울 성동구 연무장길 1") val address: String,
    @Schema(description = "위도") val lat: BigDecimal?,
    @Schema(description = "경도") val lng: BigDecimal?,
    @Schema(description = "영업시간 텍스트", example = "화~금 10:00-19:00") val hoursText: String?,
    @Schema(description = "휴무일", example = "월요일") val closedDays: String?,
    @Schema(description = "장소 설명") val description: String?,
    @Schema(description = "운영 상태", example = "OPEN") val status: String,
    val categories: List<CategoryInfo>,
    val tags: List<TagInfo>,
    val brands: List<BrandInfo>,
    @Schema(description = "인스타그램 URL") val instagramUrl: String?,
    @Schema(description = "웹사이트 URL") val websiteUrl: String?,
    @Schema(description = "네이버 지도 URL") val naverMapUrl: String?,
) {
    companion object {
        fun from(place: Place) = PlaceDetailResponse(
            id = place.id,
            name = place.name,
            region = place.region,
            district = place.district,
            address = place.address,
            lat = place.lat,
            lng = place.lng,
            hoursText = place.hoursText,
            closedDays = place.closedDays,
            description = place.description,
            status = place.status.name,
            categories = place.placeCategories.map { CategoryInfo(it.category.id, it.category.name) },
            tags = place.placeTags.map { TagInfo(it.tag.id, it.tag.name) },
            brands = place.placeBrands.map { BrandInfo(it.brand.id, it.brand.name, it.brand.type.name) },
            instagramUrl = place.instagramUrl,
            websiteUrl = place.websiteUrl,
            naverMapUrl = place.naverMapUrl,
        )
    }
}
