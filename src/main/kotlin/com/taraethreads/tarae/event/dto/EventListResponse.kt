package com.taraethreads.tarae.event.dto

import com.taraethreads.tarae.event.domain.Event
import java.time.LocalDate

data class EventListResponse(
    val id: Long,
    val title: String,
    val eventType: String,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val locationText: String?,
    val active: Boolean,
    val links: String?,
) {
    companion object {
        fun from(event: Event) = EventListResponse(
            id = event.id,
            title = event.title,
            eventType = event.eventType.name,
            startDate = event.startDate,
            endDate = event.endDate,
            locationText = event.locationText,
            active = event.active,
            links = event.links,
        )
    }
}
