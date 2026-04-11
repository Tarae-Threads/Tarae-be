package com.taraethreads.tarae.admin.dto

import com.taraethreads.tarae.event.domain.EventType
import java.math.BigDecimal
import java.time.LocalDate

data class EventCreateForm(
    val title: String = "",
    val eventType: EventType = EventType.EVENT_POPUP,
    val startDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate? = null,
    val locationText: String? = null,
    val description: String? = null,
    val lat: BigDecimal? = null,
    val lng: BigDecimal? = null,
    val links: String? = null,
)
