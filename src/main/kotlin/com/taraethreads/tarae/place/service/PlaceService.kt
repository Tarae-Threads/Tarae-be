package com.taraethreads.tarae.place.service

import com.taraethreads.tarae.event.domain.Event
import com.taraethreads.tarae.event.service.EventService
import com.taraethreads.tarae.global.exception.CustomException
import com.taraethreads.tarae.global.exception.ErrorCode
import com.taraethreads.tarae.place.domain.Place
import com.taraethreads.tarae.place.domain.PlaceStatus
import com.taraethreads.tarae.place.dto.PlaceDetailResponse
import com.taraethreads.tarae.place.dto.PlaceEventDto
import com.taraethreads.tarae.place.dto.PlaceListResponse
import com.taraethreads.tarae.place.repository.PlaceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class PlaceService(
    private val placeRepository: PlaceRepository,
    private val eventService: EventService,
) {

    fun getPlaces(region: String?, categoryId: Long?, tagId: Long?, keyword: String? = null): List<PlaceListResponse> =
        placeRepository.findAllWithFilters(region, categoryId, tagId, keyword)
            .map { PlaceListResponse.from(it) }

    fun getPlace(id: Long): PlaceDetailResponse {
        val place = findPlaceById(id)
        val events = eventService.findActiveEventsByPlaceId(id).map { it.toPlaceEventDto() }
        return PlaceDetailResponse.from(place, events)
    }

    private fun findPlaceById(id: Long): Place {
        val place = placeRepository.findById(id)
            .orElseThrow { CustomException(ErrorCode.PLACE_NOT_FOUND) }
        if (!place.active || place.status != PlaceStatus.OPEN) {
            throw CustomException(ErrorCode.PLACE_NOT_FOUND)
        }
        return place
    }

    private fun Event.toPlaceEventDto() = PlaceEventDto(
        id = id,
        title = title,
        eventType = eventType.name,
        startDate = startDate,
        endDate = endDate,
        active = active,
        instagramUrl = instagramUrl,
        websiteUrl = websiteUrl,
        naverMapUrl = naverMapUrl,
    )
}
