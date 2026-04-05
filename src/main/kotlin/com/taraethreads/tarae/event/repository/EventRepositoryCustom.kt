package com.taraethreads.tarae.event.repository

import com.taraethreads.tarae.event.domain.Event
import com.taraethreads.tarae.event.domain.EventType

interface EventRepositoryCustom {
    fun findAllWithFilters(eventType: EventType?, active: Boolean?): List<Event>
}
