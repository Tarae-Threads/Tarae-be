package com.taraethreads.tarae.place.service

import com.taraethreads.tarae.global.exception.CustomException
import com.taraethreads.tarae.global.exception.ErrorCode
import com.taraethreads.tarae.place.domain.Place
import com.taraethreads.tarae.place.repository.PlaceRepository
import org.springframework.stereotype.Service

@Service
class PlaceService(
    private val placeRepository: PlaceRepository,
) {

    fun getPlaces(region: String?, categoryId: Long?, tagId: Long?, keyword: String? = null): List<Place> =
        placeRepository.findAllWithFilters(region, categoryId, tagId, keyword)

    fun getPlace(id: Long): Place =
        placeRepository.findById(id)
            .orElseThrow { CustomException(ErrorCode.PLACE_NOT_FOUND) }
}
