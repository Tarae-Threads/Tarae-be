package com.taraethreads.tarae.place.dto

import com.taraethreads.tarae.place.domain.Tag
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "태그 응답")
data class TagResponse(
    @Schema(description = "태그 ID", example = "1") val id: Long,
    @Schema(description = "태그명", example = "주차가능") val name: String,
) {
    companion object {
        fun from(tag: Tag) = TagResponse(id = tag.id, name = tag.name)
    }
}
