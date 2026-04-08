package com.taraethreads.tarae.event.dto

import com.taraethreads.tarae.event.domain.Event
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.time.LocalDate

@Schema(description = "이벤트 목록 응답")
data class EventListResponse(
    @Schema(description = "이벤트 ID", example = "1") val id: Long,
    @Schema(description = "이벤트 제목", example = "봄맞이 뜨개 마켓") val title: String,
    @Schema(description = "이벤트 유형", example = "POPUP") val eventType: String,
    @Schema(description = "시작일") val startDate: LocalDate,
    @Schema(description = "종료일") val endDate: LocalDate?,
    @Schema(description = "장소 텍스트") val locationText: String?,
    @Schema(description = "위도") val lat: BigDecimal?,
    @Schema(description = "경도") val lng: BigDecimal?,
    @Schema(description = "활성 여부") val active: Boolean,
    @Schema(description = "관련 링크") val links: String?,
) {
    companion object {
        fun from(event: Event) = EventListResponse(
            id = event.id,
            title = event.title,
            eventType = event.eventType.name,
            startDate = event.startDate,
            endDate = event.endDate,
            locationText = event.locationText,
            lat = event.lat,
            lng = event.lng,
            active = event.active,
            links = event.links,
        )
    }
}
