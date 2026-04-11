package com.taraethreads.tarae.place.service

import com.taraethreads.tarae.global.exception.CustomException
import com.taraethreads.tarae.global.exception.ErrorCode
import com.taraethreads.tarae.place.domain.Place
import com.taraethreads.tarae.place.dto.PlaceDetailResponse
import com.taraethreads.tarae.place.dto.PlaceListResponse
import com.taraethreads.tarae.place.repository.PlaceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class PlaceService(
    private val placeRepository: PlaceRepository,
) {

    fun getPlaces(region: String?, categoryId: Long?, tagId: Long?, keyword: String? = null): List<PlaceListResponse> =
        placeRepository.findAllWithFilters(region, categoryId, tagId, keyword)
            .map { PlaceListResponse.from(it) }

    fun getPlace(id: Long): PlaceDetailResponse =
        PlaceDetailResponse.from(findPlaceById(id))

    private fun findPlaceById(id: Long): Place =
        placeRepository.findById(id)
            .orElseThrow { CustomException(ErrorCode.PLACE_NOT_FOUND) }
}
