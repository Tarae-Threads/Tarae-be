package com.taraethreads.tarae.place.service

import com.taraethreads.tarae.admin.dto.AdminBrandRow
import com.taraethreads.tarae.global.exception.CustomException
import com.taraethreads.tarae.global.exception.ErrorCode
import com.taraethreads.tarae.place.domain.Brand
import com.taraethreads.tarae.place.domain.BrandType
import com.taraethreads.tarae.place.dto.BrandGroupResponse
import com.taraethreads.tarae.place.dto.BrandResponse
import com.taraethreads.tarae.place.repository.BrandRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class BrandService(
    private val brandRepository: BrandRepository,
) {

    fun getAll(): List<BrandResponse> = brandRepository.findAll().map { BrandResponse.from(it) }

    fun getAllForAdmin(): List<AdminBrandRow> {
        val usageMap = brandRepository.countPlaceUsagesGrouped()
            .associate { (it[0] as Long) to (it[1] as Long) }
        return brandRepository.findAllByOrderById()
            .map { AdminBrandRow.from(it, usageMap[it.id] ?: 0) }
    }

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
        val brand = brandRepository.findByIdOrNull(id)
            ?: throw CustomException(ErrorCode.BRAND_NOT_FOUND)
        val trimmed = name.trim()
        if (trimmed != brand.name && brandRepository.existsByName(trimmed)) {
            throw CustomException(ErrorCode.DUPLICATE_MASTER_NAME)
        }
        brand.update(trimmed, type)
    }

    fun countPlaceUsages(id: Long): Long = brandRepository.countPlaceUsages(id)

    @Transactional
    fun delete(id: Long) {
        val brand = brandRepository.findByIdOrNull(id)
            ?: throw CustomException(ErrorCode.BRAND_NOT_FOUND)
        brandRepository.deletePlaceMappings(id)
        brandRepository.delete(brand)
    }
}
