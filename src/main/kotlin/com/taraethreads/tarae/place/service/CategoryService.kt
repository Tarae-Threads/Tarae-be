package com.taraethreads.tarae.place.service

import com.taraethreads.tarae.admin.dto.AdminCategoryRow
import com.taraethreads.tarae.global.exception.CustomException
import com.taraethreads.tarae.global.exception.ErrorCode
import com.taraethreads.tarae.place.domain.Category
import com.taraethreads.tarae.place.dto.CategoryResponse
import com.taraethreads.tarae.place.repository.CategoryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CategoryService(
    private val categoryRepository: CategoryRepository,
) {

    fun getCategories(): List<CategoryResponse> =
        categoryRepository.findAll().map { CategoryResponse.from(it) }

    fun getAllForAdmin(): List<AdminCategoryRow> =
        categoryRepository.findAll()
            .sortedBy { it.id }
            .map { AdminCategoryRow.from(it) }

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
        val category = categoryRepository.findById(id)
            .orElseThrow { CustomException(ErrorCode.CATEGORY_NOT_FOUND) }
        val trimmed = name.trim()
        if (trimmed != category.name && categoryRepository.existsByName(trimmed)) {
            throw CustomException(ErrorCode.DUPLICATE_MASTER_NAME)
        }
        category.rename(trimmed)
    }
}
