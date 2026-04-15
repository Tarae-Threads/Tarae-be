package com.taraethreads.tarae.admin.dto

import com.taraethreads.tarae.place.domain.Place

data class PlaceSelectOption(
    val id: Long,
    val name: String,
    val region: String,
    val district: String,
) {
    companion object {
        fun from(place: Place) = PlaceSelectOption(
            id = place.id,
            name = place.name,
            region = place.region,
            district = place.district,
        )
    }
}
