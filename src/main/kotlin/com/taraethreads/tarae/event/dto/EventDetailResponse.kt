package com.taraethreads.tarae.event.dto

import com.taraethreads.tarae.event.domain.Event
import java.math.BigDecimal
import java.time.LocalDate

data class EventDetailResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val eventType: String,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val placeId: Long?,
    val locationText: String?,
    val lat: BigDecimal?,
    val lng: BigDecimal?,
    val active: Boolean,
    val links: String?,
) {
    companion object {
        fun from(event: Event) = EventDetailResponse(
            id = event.id,
            title = event.title,
            description = event.description,
            eventType = event.eventType.name,
            startDate = event.startDate,
            endDate = event.endDate,
            placeId = event.place?.id,
            locationText = event.locationText,
            lat = event.lat,
            lng = event.lng,
            active = event.active,
            links = event.links,
        )
    }
}
