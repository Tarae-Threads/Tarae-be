package com.taraethreads.tarae.admin.dto

import com.taraethreads.tarae.place.domain.Tag

data class AdminTagRow(
    val id: Long,
    val name: String,
) {
    companion object {
        fun from(tag: Tag) = AdminTagRow(
            id = tag.id,
            name = tag.name,
        )
    }
}
