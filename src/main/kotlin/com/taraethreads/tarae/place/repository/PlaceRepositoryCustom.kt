package com.taraethreads.tarae.place.repository

import com.taraethreads.tarae.place.domain.Place

interface PlaceRepositoryCustom {
    fun findAllWithFilters(region: String?, categoryId: Long?, tagId: Long?): List<Place>
}
