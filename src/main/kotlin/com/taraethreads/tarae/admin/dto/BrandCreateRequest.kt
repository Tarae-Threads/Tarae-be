package com.taraethreads.tarae.admin.dto

import com.taraethreads.tarae.place.domain.BrandType

data class BrandCreateRequest(
    val name: String = "",
    val type: BrandType = BrandType.YARN,
)
