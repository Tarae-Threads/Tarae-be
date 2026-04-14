package com.taraethreads.tarae.admin.dto

import com.taraethreads.tarae.place.domain.Category

data class AdminCategoryRow(
    val id: Long,
    val name: String,
) {
    companion object {
        fun from(category: Category) = AdminCategoryRow(
            id = category.id,
            name = category.name,
        )
    }
}
