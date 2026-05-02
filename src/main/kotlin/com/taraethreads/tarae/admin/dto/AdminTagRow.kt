package com.taraethreads.tarae.admin.dto

import com.taraethreads.tarae.place.domain.Tag

data class AdminTagRow(
    val id: Long,
    val name: String,
    val placeCount: Long = 0,
) {
    companion object {
        fun from(tag: Tag, placeCount: Long = 0) = AdminTagRow(
            id = tag.id,
            name = tag.name,
            placeCount = placeCount,
        )
    }
}
