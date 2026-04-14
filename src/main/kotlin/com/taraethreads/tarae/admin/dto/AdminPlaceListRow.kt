package com.taraethreads.tarae.admin.dto

import com.taraethreads.tarae.place.domain.Place
import com.taraethreads.tarae.place.domain.PlaceStatus

data class AdminPlaceListRow(
    val id: Long,
    val name: String,
    val region: String,
    val district: String,
    val active: Boolean,
    val status: PlaceStatus,
    val categoryNames: List<String>,
) {
    companion object {
        fun from(place: Place) = AdminPlaceListRow(
            id = place.id,
            name = place.name,
            region = place.region,
            district = place.district,
            active = place.active,
            status = place.status,
            categoryNames = place.categories.map { it.name },
        )
    }
}
