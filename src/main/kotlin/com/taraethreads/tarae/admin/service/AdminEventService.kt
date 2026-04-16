package com.taraethreads.tarae.admin.service

import com.taraethreads.tarae.admin.dto.AdminEventListRow
import com.taraethreads.tarae.admin.dto.AdminEventStatusFilter
import com.taraethreads.tarae.admin.dto.EventCreateForm
import com.taraethreads.tarae.admin.dto.PlaceSelectOption
import com.taraethreads.tarae.event.domain.Event
import com.taraethreads.tarae.event.repository.EventRepository
import com.taraethreads.tarae.global.exception.CustomException
import com.taraethreads.tarae.global.exception.ErrorCode
import com.taraethreads.tarae.place.repository.PlaceRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional(readOnly = true)
class AdminEventService(
    private val eventRepository: EventRepository,
    private val placeRepository: PlaceRepository,
) {

    companion object {
        const val EXPIRING_THRESHOLD_DAYS = 7L
    }

    fun placeOptions(): List<PlaceSelectOption> =
        placeRepository.findAll().map { PlaceSelectOption.from(it) }

    fun list(filter: AdminEventStatusFilter, pageable: Pageable): Page<AdminEventListRow> {
        val today = LocalDate.now()
        val threshold = today.plusDays(EXPIRING_THRESHOLD_DAYS)
        return eventRepository.findAllForAdmin(filter, pageable)
            .map { AdminEventListRow.from(it, today, threshold) }
    }

    fun getEntity(id: Long): Event =
        eventRepository.findById(id)
            .orElseThrow { CustomException(ErrorCode.EVENT_NOT_FOUND) }

    fun getForm(id: Long): EventCreateForm {
        val event = getEntity(id)
        return EventCreateForm(
            title = event.title,
            eventType = event.eventType,
            startDate = event.startDate,
            endDate = event.endDate,
            placeId = event.place?.id,
            locationText = event.locationText,
            description = event.description,
            lat = event.lat,
            lng = event.lng,
            instagramUrl = event.instagramUrl,
            websiteUrl = event.websiteUrl,
            naverMapUrl = event.naverMapUrl,
        )
    }

    @Transactional
    fun createBulk(forms: List<EventCreateForm>): List<Long> =
        forms.map { form ->
            val place = form.placeId?.let { placeRepository.findById(it).orElse(null) }
            val event = Event(
                title = form.title,
                eventType = form.eventType,
                startDate = form.startDate,
                endDate = form.endDate,
                place = place,
                locationText = form.locationText,
                description = form.description,
                lat = form.lat,
                lng = form.lng,
                instagramUrl = form.instagramUrl,
                websiteUrl = form.websiteUrl,
                naverMapUrl = form.naverMapUrl,
            )
            eventRepository.save(event).id
        }

    @Transactional
    fun update(id: Long, form: EventCreateForm) {
        val event = getEntity(id)
        event.place = form.placeId?.let { placeRepository.findById(it).orElse(null) }
        event.update(form)
    }

    @Transactional
    fun toggleActive(id: Long) {
        val event = getEntity(id)
        if (event.active) event.deactivate() else event.activate()
    }

    @Transactional
    fun delete(id: Long) {
        val event = getEntity(id)
        eventRepository.delete(event)
    }
}
