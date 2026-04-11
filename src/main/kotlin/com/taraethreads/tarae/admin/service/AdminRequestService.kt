package com.taraethreads.tarae.admin.service

import com.taraethreads.tarae.admin.dto.EventCreateForm
import com.taraethreads.tarae.admin.dto.PlaceCreateForm
import com.taraethreads.tarae.event.domain.Event
import com.taraethreads.tarae.event.repository.EventRepository
import com.taraethreads.tarae.global.exception.CustomException
import com.taraethreads.tarae.global.exception.ErrorCode
import com.taraethreads.tarae.place.domain.Place
import com.taraethreads.tarae.place.repository.BrandRepository
import com.taraethreads.tarae.place.repository.CategoryRepository
import com.taraethreads.tarae.place.repository.PlaceRepository
import com.taraethreads.tarae.place.repository.TagRepository
import com.taraethreads.tarae.request.domain.EventRequest
import com.taraethreads.tarae.request.domain.PlaceRequest
import com.taraethreads.tarae.request.domain.RequestStatus
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
    private val categoryRepository: CategoryRepository,
    private val tagRepository: TagRepository,
    private val brandRepository: BrandRepository,
) {

    // --- 장소 제보 ---

    fun getPlaceRequests(status: RequestStatus?): List<PlaceRequest> =
        if (status == null) placeRequestRepository.findAll()
        else placeRequestRepository.findAllByStatus(status)

    fun getPlaceRequest(id: Long): PlaceRequest =
        placeRequestRepository.findById(id)
            .orElseThrow { CustomException(ErrorCode.PLACE_REQUEST_NOT_FOUND) }

    @Transactional
    fun approvePlaceRequest(id: Long, form: PlaceCreateForm) {
        val placeRequest = getPlaceRequest(id)
        placeRequest.approve()

        val place = Place(
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

        if (form.categoryIds.isNotEmpty()) {
            categoryRepository.findAllById(form.categoryIds).forEach { place.addCategory(it) }
        }
        if (form.tagIds.isNotEmpty()) {
            tagRepository.findAllById(form.tagIds).forEach { place.addTag(it) }
        }
        if (form.brandIds.isNotEmpty()) {
            brandRepository.findAllById(form.brandIds).forEach { place.addBrand(it) }
        }

        placeRepository.save(place)
    }

    @Transactional
    fun rejectPlaceRequest(id: Long) {
        val placeRequest = getPlaceRequest(id)
        placeRequest.reject()
    }

    // --- 이벤트 제보 ---

    fun getEventRequests(status: RequestStatus?): List<EventRequest> =
        if (status == null) eventRequestRepository.findAll()
        else eventRequestRepository.findAllByStatus(status)

    fun getEventRequest(id: Long): EventRequest =
        eventRequestRepository.findById(id)
            .orElseThrow { CustomException(ErrorCode.EVENT_REQUEST_NOT_FOUND) }

    @Transactional
    fun approveEventRequest(id: Long, form: EventCreateForm) {
        val eventRequest = getEventRequest(id)
        eventRequest.approve()

        val event = Event(
            title = form.title,
            eventType = form.eventType,
            startDate = form.startDate,
            endDate = form.endDate,
            locationText = form.locationText,
            description = form.description,
            lat = form.lat,
            lng = form.lng,
            links = form.links,
        )

        eventRepository.save(event)
    }

    @Transactional
    fun rejectEventRequest(id: Long) {
        val eventRequest = getEventRequest(id)
        eventRequest.reject()
    }
}
