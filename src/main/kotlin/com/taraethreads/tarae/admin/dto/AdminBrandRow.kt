package com.taraethreads.tarae.admin.dto

import com.taraethreads.tarae.place.domain.Brand
import com.taraethreads.tarae.place.domain.BrandType

data class AdminBrandRow(
    val id: Long,
    val name: String,
    val type: BrandType,
) {
    companion object {
        fun from(brand: Brand) = AdminBrandRow(
            id = brand.id,
            name = brand.name,
            type = brand.type,
        )
    }
}
