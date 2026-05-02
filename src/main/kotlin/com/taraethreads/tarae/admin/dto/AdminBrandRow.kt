package com.taraethreads.tarae.admin.dto

import com.taraethreads.tarae.place.domain.Brand
import com.taraethreads.tarae.place.domain.BrandType

data class AdminBrandRow(
    val id: Long,
    val name: String,
    val type: BrandType,
    val placeCount: Long = 0,
) {
    companion object {
        fun from(brand: Brand, placeCount: Long = 0) = AdminBrandRow(
            id = brand.id,
            name = brand.name,
            type = brand.type,
            placeCount = placeCount,
        )
    }
}
