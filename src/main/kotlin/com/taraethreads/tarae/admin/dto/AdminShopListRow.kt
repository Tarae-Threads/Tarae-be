package com.taraethreads.tarae.admin.dto

import com.taraethreads.tarae.shop.domain.Shop

data class AdminShopListRow(
    val id: Long,
    val name: String,
    val active: Boolean,
    val categoryNames: List<String>,
) {
    companion object {
        fun from(shop: Shop) = AdminShopListRow(
            id = shop.id,
            name = shop.name,
            active = shop.active,
            categoryNames = shop.categories.map { it.name },
        )
    }
}
