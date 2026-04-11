package com.taraethreads.tarae.place.service

import com.taraethreads.tarae.place.dto.BrandResponse
import com.taraethreads.tarae.place.repository.BrandRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class BrandService(
    private val brandRepository: BrandRepository,
) {

    fun getAll(): List<BrandResponse> = brandRepository.findAll().map { BrandResponse.from(it) }
}
