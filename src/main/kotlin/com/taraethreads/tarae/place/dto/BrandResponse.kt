package com.taraethreads.tarae.place.dto

import com.taraethreads.tarae.place.domain.Brand
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "브랜드 응답")
data class BrandResponse(
    @Schema(description = "브랜드 ID", example = "1") val id: Long,
    @Schema(description = "브랜드명", example = "산네스간") val name: String,
    @Schema(description = "브랜드 타입", example = "YARN") val type: String,
) {
    companion object {
        fun from(brand: Brand) = BrandResponse(id = brand.id, name = brand.name, type = brand.type.name)
    }
}
