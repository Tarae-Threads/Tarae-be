package com.taraethreads.tarae.place.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "타입별 브랜드 목록 응답")
data class BrandGroupResponse(
    @Schema(description = "타입별 브랜드 그룹 목록") val data: List<BrandTypeGroup>,
) {
    @Schema(description = "브랜드 타입 그룹")
    data class BrandTypeGroup(
        @Schema(description = "브랜드 타입", example = "YARN") val type: String,
        @Schema(description = "브랜드 목록") val brands: List<BrandItem>,
    )

    @Schema(description = "브랜드 항목")
    data class BrandItem(
        @Schema(description = "브랜드 ID", example = "1") val id: Long,
        @Schema(description = "브랜드명", example = "산네스간") val name: String,
    )
}
