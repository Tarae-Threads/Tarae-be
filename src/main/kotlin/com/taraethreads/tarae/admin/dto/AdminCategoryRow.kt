package com.taraethreads.tarae.admin.dto

import com.taraethreads.tarae.place.domain.Category

data class AdminCategoryRow(
    val id: Long,
    val name: String,
    val placeCount: Long = 0,
) {
    companion object {
        fun from(category: Category, placeCount: Long = 0) = AdminCategoryRow(
            id = category.id,
            name = category.name,
            placeCount = placeCount,
        )
    }
}
