package com.taraethreads.tarae.place.service

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
}
