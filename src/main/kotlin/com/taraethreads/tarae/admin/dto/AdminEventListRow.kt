package com.taraethreads.tarae.admin.dto

import com.taraethreads.tarae.event.domain.Event
import com.taraethreads.tarae.event.domain.EventType
import java.time.LocalDate

enum class AdminEventDerivedStatus { ONGOING, EXPIRING_SOON, EXPIRED }

data class AdminEventListRow(
    val id: Long,
    val title: String,
    val eventType: EventType,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val active: Boolean,
    val status: AdminEventDerivedStatus,
) {
    companion object {
        fun from(event: Event, today: LocalDate, expiringThreshold: LocalDate): AdminEventListRow {
            val end = event.endDate
            val status = when {
                end == null -> AdminEventDerivedStatus.ONGOING
                end.isBefore(today) -> AdminEventDerivedStatus.EXPIRED
                !end.isAfter(expiringThreshold) -> AdminEventDerivedStatus.EXPIRING_SOON
                else -> AdminEventDerivedStatus.ONGOING
            }
            return AdminEventListRow(
                id = event.id,
                title = event.title,
                eventType = event.eventType,
                startDate = event.startDate,
                endDate = event.endDate,
                active = event.active,
                status = status,
            )
        }
    }
}
