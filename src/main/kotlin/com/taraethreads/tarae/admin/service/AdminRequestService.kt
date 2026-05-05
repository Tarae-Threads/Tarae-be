package com.taraethreads.tarae.admin.service

import com.taraethreads.tarae.admin.dto.AdminRequestListRow
import com.taraethreads.tarae.admin.dto.EventCreateForm
import com.taraethreads.tarae.admin.dto.PlaceCreateForm
import com.taraethreads.tarae.event.domain.Event
import com.taraethreads.tarae.event.repository.EventRepository
import com.taraethreads.tarae.global.exception.CustomException
import com.taraethreads.tarae.global.exception.ErrorCode
import com.taraethreads.tarae.place.domain.Place
import com.taraethreads.tarae.place.repository.PlaceRepository
import com.taraethreads.tarae.request.domain.EventRequest
import com.taraethreads.tarae.request.domain.PlaceRequest
import com.taraethreads.tarae.request.domain.RequestStatus
import com.taraethreads.tarae.request.domain.RequestType
import com.taraethreads.tarae.request.repository.EventRequestRepository
import com.taraethreads.tarae.request.repository.PlaceRequestRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AdminRequestService(
    private val placeRequestRepository: PlaceRequestRepository,
    private val eventRequestRepository: EventRequestRepository,
    private val placeRepository: PlaceRepository,
    private val eventRepository: EventRepository,
    private val placeAssociationSyncer: PlaceAssociationSyncer,
) {

    // --- 장소 제보 ---

    fun getPlaceRequests(status: RequestStatus?, requestType: RequestType?): List<AdminRequestListRow> =
        when {
            status != null && requestType != null ->
                placeRequestRepository.findAllByStatusAndRequestTypeOrderByCreatedAtDesc(status, requestType)
            status != null -> placeRequestRepository.findAllByStatusOrderByCreatedAtDesc(status)
            requestType != null -> placeRequestRepository.findAllByRequestTypeOrderByCreatedAtDesc(requestType)
            else -> placeRequestRepository.findAllByOrderByCreatedAtDesc()
        }.map { AdminRequestListRow.from(it) }

    fun getPlaceRequest(id: Long): PlaceRequest =
        placeRequestRepository.findById(id)
            .orElseThrow { CustomException(ErrorCode.PLACE_REQUEST_NOT_FOUND) }

    fun getPlace(id: Long): Place =
        placeRepository.findById(id)
            .orElseThrow { CustomException(ErrorCode.PLACE_NOT_FOUND) }

    @Transactional
    fun approvePlaceRequest(id: Long, form: PlaceCreateForm) {
        val placeRequest = getPlaceRequest(id)
        placeRequest.approve()

        if (placeRequest.requestType == RequestType.UPDATE) {
            val existing = placeRepository.findById(placeRequest.placeId!!)
                .orElseThrow { CustomException(ErrorCode.PLACE_NOT_FOUND) }
            existing.update(form)
            placeAssociationSyncer.sync(existing, form)
        } else {
            val newPlace = Place(
                name = form.name,
                region = form.region,
                district = form.district,
                address = form.address,
                lat = form.lat,
                lng = form.lng,
                hoursText = form.hoursText,
                closedDays = form.closedDays,
                description = form.description,
                instagramUrl = form.instagramUrl,
                websiteUrl = form.websiteUrl,
                naverMapUrl = form.naverMapUrl,
            )
            placeRepository.save(newPlace)
            placeAssociationSyncer.attach(newPlace, form)
        }
    }

    @Transactional
    fun rejectPlaceRequest(id: Long) {
        val placeRequest = getPlaceRequest(id)
        placeRequest.reject()
    }

    // --- 이벤트 제보 ---

    fun getEventRequests(status: RequestStatus?): List<AdminRequestListRow> =
        (if (status == null) eventRequestRepository.findAllByOrderByCreatedAtDesc()
        else eventRequestRepository.findAllByStatusOrderByCreatedAtDesc(status))
            .map { AdminRequestListRow.from(it) }

    fun getEventRequest(id: Long): EventRequest =
        eventRequestRepository.findById(id)
            .orElseThrow { CustomException(ErrorCode.EVENT_REQUEST_NOT_FOUND) }

    @Transactional
    fun approveEventRequest(id: Long, form: EventCreateForm) {
        val eventRequest = getEventRequest(id)
        eventRequest.approve()

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

        eventRepository.save(event)
    }

    @Transactional
    fun rejectEventRequest(id: Long) {
        val eventRequest = getEventRequest(id)
        eventRequest.reject()
    }
}
