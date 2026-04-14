package com.taraethreads.tarae.place.service

import com.taraethreads.tarae.admin.dto.AdminBrandRow
import com.taraethreads.tarae.global.exception.CustomException
import com.taraethreads.tarae.global.exception.ErrorCode
import com.taraethreads.tarae.place.domain.Brand
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

    fun getAllForAdmin(): List<AdminBrandRow> =
        brandRepository.findAll()
            .sortedBy { it.id }
            .map { AdminBrandRow.from(it) }

    fun getBrandsGroupedByType(): List<BrandGroupResponse.BrandTypeGroup> {
        val grouped = brandRepository.findAll().groupBy { it.type }
        return BrandType.entries.map { type ->
            BrandGroupResponse.BrandTypeGroup(
                type = type.name,
                brands = grouped[type]?.map { BrandGroupResponse.BrandItem(id = it.id, name = it.name) } ?: emptyList(),
            )
        }
    }

    @Transactional
    fun create(name: String, type: BrandType): Brand {
        val trimmed = name.trim()
        if (brandRepository.existsByName(trimmed)) {
            throw CustomException(ErrorCode.DUPLICATE_MASTER_NAME)
        }
        return brandRepository.save(Brand(name = trimmed, type = type))
    }

    @Transactional
    fun update(id: Long, name: String, type: BrandType) {
        val brand = brandRepository.findById(id)
            .orElseThrow { CustomException(ErrorCode.BRAND_NOT_FOUND) }
        val trimmed = name.trim()
        if (trimmed != brand.name && brandRepository.existsByName(trimmed)) {
            throw CustomException(ErrorCode.DUPLICATE_MASTER_NAME)
        }
        brand.update(trimmed, type)
    }
}
