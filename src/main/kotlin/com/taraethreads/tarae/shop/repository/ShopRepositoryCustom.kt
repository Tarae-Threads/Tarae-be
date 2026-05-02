package com.taraethreads.tarae.shop.repository

import com.taraethreads.tarae.shop.domain.Shop
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ShopRepositoryCustom {
    fun findAllWithFilters(tagId: Long?, keyword: String?): List<Shop>
    fun findAllForAdmin(keyword: String?, pageable: Pageable): Page<Shop>
}
