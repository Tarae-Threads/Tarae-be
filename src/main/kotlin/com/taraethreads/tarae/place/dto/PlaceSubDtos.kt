package com.taraethreads.tarae.place.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(description = "카테고리 요약 정보")
data class CategoryDto(
    @Schema(description = "카테고리 ID", example = "1") val id: Long,
    @Schema(description = "카테고리명", example = "공방") val name: String,
)

@Schema(description = "태그 요약 정보")
data class TagDto(
    @Schema(description = "태그 ID", example = "1") val id: Long,
    @Schema(description = "태그명", example = "초보환영") val name: String,
)

@Schema(description = "브랜드 요약 정보")
data class BrandDto(
    @Schema(description = "브랜드 ID", example = "1") val id: Long,
    @Schema(description = "브랜드명", example = "드미트리아") val name: String,
    @Schema(description = "브랜드 유형", example = "YARN") val type: String,
)

@Schema(description = "장소에 연결된 이벤트 요약 정보")
data class PlaceEventDto(
    @Schema(description = "이벤트 ID", example = "10") val id: Long,
    @Schema(description = "이벤트 제목", example = "봄 시즌 클래스 모집") val title: String,
    @Schema(description = "이벤트 유형", example = "TESTER_RECRUIT") val eventType: String,
    @Schema(description = "시작일", example = "2026-04-15") val startDate: LocalDate,
    @Schema(description = "종료일", example = "2026-05-10") val endDate: LocalDate?,
    @Schema(description = "활성 여부", example = "true") val active: Boolean,
    @Schema(description = "인스타그램 URL") val instagramUrl: String?,
    @Schema(description = "웹사이트 URL") val websiteUrl: String?,
    @Schema(description = "네이버 지도 URL") val naverMapUrl: String?,
)
