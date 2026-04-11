package com.taraethreads.tarae.place.service

import com.taraethreads.tarae.place.domain.BrandType
import com.taraethreads.tarae.place.dto.BrandGroupResponse
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

    fun getBrandsGroupedByType(): List<BrandGroupResponse.BrandTypeGroup> {
        val grouped = brandRepository.findAll().groupBy { it.type }
        return BrandType.entries.mapNotNull { type ->
            grouped[type]?.let { brands ->
                BrandGroupResponse.BrandTypeGroup(
                    type = type.name,
                    brands = brands.map { BrandGroupResponse.BrandItem(id = it.id, name = it.name) },
                )
            }
        }
    }
}
