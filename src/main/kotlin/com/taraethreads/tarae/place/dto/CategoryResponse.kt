package com.taraethreads.tarae.place.dto

import com.taraethreads.tarae.place.domain.Category
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "카테고리 응답")
data class CategoryResponse(
    @Schema(description = "카테고리 ID", example = "1") val id: Long,
    @Schema(description = "카테고리명", example = "뜨개카페") val name: String,
) {
    companion object {
        fun from(category: Category) = CategoryResponse(
            id = category.id,
            name = category.name,
        )
    }
}
