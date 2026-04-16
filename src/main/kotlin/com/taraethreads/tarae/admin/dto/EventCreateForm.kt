package com.taraethreads.tarae.admin.dto

import com.taraethreads.tarae.event.domain.EventType
import org.springframework.format.annotation.DateTimeFormat
import java.math.BigDecimal
import java.time.LocalDate

data class EventCreateForm(
    val title: String = "",
    val eventType: EventType = EventType.EVENT_POPUP,
    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val startDate: LocalDate = LocalDate.now(),
    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val endDate: LocalDate? = null,
    val placeId: Long? = null,
    val locationText: String? = null,
    val description: String? = null,
    val lat: BigDecimal? = null,
    val lng: BigDecimal? = null,
    val instagramUrl: String? = null,
    val websiteUrl: String? = null,
    val naverMapUrl: String? = null,
)
