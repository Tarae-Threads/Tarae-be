package com.taraethreads.tarae.place.service

import com.taraethreads.tarae.place.domain.Category
import com.taraethreads.tarae.place.repository.CategoryRepository
import org.springframework.stereotype.Service

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository,
) {

    fun getCategories(): List<Category> = categoryRepository.findAll()
}
