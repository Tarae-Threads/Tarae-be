package com.taraethreads.tarae.event.repository

import com.taraethreads.tarae.admin.dto.AdminEventStatusFilter
import com.taraethreads.tarae.event.domain.Event
import com.taraethreads.tarae.event.domain.EventType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDate

interface EventRepositoryCustom {
    fun findAllWithFilters(eventType: EventType?, active: Boolean?): List<Event>

    fun findAllForAdmin(filter: AdminEventStatusFilter, pageable: Pageable): Page<Event>

    fun countExpiringSoon(today: LocalDate, threshold: LocalDate): Long
}
