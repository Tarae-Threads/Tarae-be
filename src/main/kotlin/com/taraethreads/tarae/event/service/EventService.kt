package com.taraethreads.tarae.event.service

import com.taraethreads.tarae.event.domain.Event
import com.taraethreads.tarae.event.domain.EventType
import com.taraethreads.tarae.event.repository.EventRepository
import com.taraethreads.tarae.global.exception.CustomException
import com.taraethreads.tarae.global.exception.ErrorCode
import org.springframework.stereotype.Service

@Service
class EventService(
    private val eventRepository: EventRepository,
) {

    fun getEvents(eventType: EventType?, active: Boolean?): List<Event> =
        eventRepository.findAllWithFilters(eventType, active)

    fun getEvent(id: Long): Event =
        eventRepository.findById(id)
            .orElseThrow { CustomException(ErrorCode.EVENT_NOT_FOUND) }
}
