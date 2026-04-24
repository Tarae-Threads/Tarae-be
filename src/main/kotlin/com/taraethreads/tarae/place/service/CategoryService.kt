package com.taraethreads.tarae.place.service

import com.taraethreads.tarae.admin.dto.AdminCategoryRow
import com.taraethreads.tarae.global.exception.CustomException
import com.taraethreads.tarae.global.exception.ErrorCode
import com.taraethreads.tarae.place.domain.Category
import com.taraethreads.tarae.place.dto.CategoryResponse
import com.taraethreads.tarae.place.repository.CategoryRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CategoryService(
    private val categoryRepository: CategoryRepository,
) {

    fun getCategories(): List<CategoryResponse> =
        categoryRepository.findAll().map { CategoryResponse.from(it) }

    fun getAllForAdmin(): List<AdminCategoryRow> {
        val usageMap = categoryRepository.countPlaceUsagesGrouped()
            .associate { (it[0] as Long) to (it[1] as Long) }
        return categoryRepository.findAllByOrderById()
            .map { AdminCategoryRow.from(it, usageMap[it.id] ?: 0) }
    }

    @Transactional
    fun create(name: String): Category {
        val trimmed = name.trim()
        if (categoryRepository.existsByName(trimmed)) {
            throw CustomException(ErrorCode.DUPLICATE_MASTER_NAME)
        }
        return categoryRepository.save(Category(name = trimmed))
    }

    @Transactional
    fun rename(id: Long, name: String) {
        val category = categoryRepository.findByIdOrNull(id)
            ?: throw CustomException(ErrorCode.CATEGORY_NOT_FOUND)
        val trimmed = name.trim()
        if (trimmed != category.name && categoryRepository.existsByName(trimmed)) {
            throw CustomException(ErrorCode.DUPLICATE_MASTER_NAME)
        }
        category.rename(trimmed)
    }

    fun countPlaceUsages(id: Long): Long = categoryRepository.countPlaceUsages(id)

    @Transactional
    fun delete(id: Long) {
        val category = categoryRepository.findByIdOrNull(id)
            ?: throw CustomException(ErrorCode.CATEGORY_NOT_FOUND)
        categoryRepository.deletePlaceMappings(id)
        categoryRepository.delete(category)
    }
}
