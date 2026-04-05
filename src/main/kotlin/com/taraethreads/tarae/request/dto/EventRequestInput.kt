package com.taraethreads.tarae.request.dto

import com.taraethreads.tarae.event.domain.EventType
import com.taraethreads.tarae.request.domain.EventRequest
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class EventRequestInput(
    @field:NotBlank
    val title: String?,
    @field:NotNull
    val eventType: EventType?,
    @field:NotNull
    val startDate: LocalDate?,
    val endDate: LocalDate? = null,
    val locationText: String? = null,
    val description: String? = null,
) {
    fun toEntity() = EventRequest(
        title = title!!,
        eventType = eventType!!,
        startDate = startDate!!,
        endDate = endDate,
        locationText = locationText,
        description = description,
    )
}
