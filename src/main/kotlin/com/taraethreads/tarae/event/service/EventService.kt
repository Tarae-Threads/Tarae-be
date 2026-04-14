package com.taraethreads.tarae.event.service

import com.taraethreads.tarae.event.domain.Event
import com.taraethreads.tarae.event.domain.EventType
import com.taraethreads.tarae.event.dto.EventDetailResponse
import com.taraethreads.tarae.event.dto.EventListResponse
import com.taraethreads.tarae.event.repository.EventRepository
import com.taraethreads.tarae.global.exception.CustomException
import com.taraethreads.tarae.global.exception.ErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional(readOnly = true)
class EventService(
    private val eventRepository: EventRepository,
) {

    fun getEvents(eventType: EventType?, active: Boolean?): List<EventListResponse> =
        eventRepository.findAllWithFilters(eventType, active)
            .map { EventListResponse.from(it) }

    fun getEvent(id: Long): EventDetailResponse {
        val event = findEventById(id)
        if (!isPublic(event)) {
            throw CustomException(ErrorCode.EVENT_NOT_FOUND)
        }
        return EventDetailResponse.from(event)
    }

    fun findActiveEventsByPlaceId(placeId: Long): List<Event> =
        eventRepository.findPublicEventsByPlaceId(placeId, LocalDate.now())

    private fun isPublic(event: Event): Boolean {
        if (!event.active) return false
        val end = event.endDate ?: return true
        return !end.isBefore(LocalDate.now())
    }

    private fun findEventById(id: Long): Event =
        eventRepository.findById(id)
            .orElseThrow { CustomException(ErrorCode.EVENT_NOT_FOUND) }
}
