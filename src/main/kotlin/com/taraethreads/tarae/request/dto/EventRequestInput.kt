package com.taraethreads.tarae.request.dto

import com.taraethreads.tarae.event.domain.EventType
import com.taraethreads.tarae.request.domain.EventRequest
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
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
    val lat: BigDecimal? = null,
    val lng: BigDecimal? = null,
    val instagramUrl: String? = null,
    val websiteUrl: String? = null,
    val naverMapUrl: String? = null,
    @field:Email
    val email: String? = null,
) {
    fun toEntity() = EventRequest(
        title = requireNotNull(title) { "title은 필수입니다" },
        eventType = requireNotNull(eventType) { "eventType은 필수입니다" },
        startDate = requireNotNull(startDate) { "startDate는 필수입니다" },
        endDate = endDate,
        locationText = locationText,
        description = description,
        lat = lat,
        lng = lng,
        instagramUrl = instagramUrl,
        websiteUrl = websiteUrl,
        naverMapUrl = naverMapUrl,
        email = email,
    )
}
