package com.taraethreads.tarae.place.repository

import com.taraethreads.tarae.place.domain.Place
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface PlaceRepositoryCustom {
    fun findAllWithFilters(region: String?, categoryId: Long?, tagId: Long?, keyword: String? = null): List<Place>

    fun findAllForAdmin(keyword: String?, pageable: Pageable): Page<Place>
}
