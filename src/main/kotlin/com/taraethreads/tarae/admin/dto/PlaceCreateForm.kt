package com.taraethreads.tarae.admin.dto

import com.taraethreads.tarae.place.domain.PlaceStatus
import java.math.BigDecimal

data class PlaceCreateForm(
    val name: String = "",
    val status: PlaceStatus = PlaceStatus.OPEN,
    val region: String = "",
    val district: String = "",
    val address: String = "",
    val lat: BigDecimal? = null,
    val lng: BigDecimal? = null,
    val hoursText: String? = null,
    val closedDays: String? = null,
    val description: String? = null,
    val instagramUrl: String? = null,
    val websiteUrl: String? = null,
    val naverMapUrl: String? = null,
    val categoryIds: List<Long> = emptyList(),
    val tagIds: List<Long> = emptyList(),
    val brandIds: List<Long> = emptyList(),
)
