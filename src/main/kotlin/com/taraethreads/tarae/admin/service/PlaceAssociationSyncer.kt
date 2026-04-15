package com.taraethreads.tarae.admin.service

import com.taraethreads.tarae.admin.dto.PlaceCreateForm
import com.taraethreads.tarae.place.domain.Place
import com.taraethreads.tarae.place.repository.BrandRepository
import com.taraethreads.tarae.place.repository.CategoryRepository
import com.taraethreads.tarae.place.repository.TagRepository
import org.springframework.stereotype.Component

@Component
class PlaceAssociationSyncer(
    private val categoryRepository: CategoryRepository,
    private val tagRepository: TagRepository,
    private val brandRepository: BrandRepository,
) {

    fun attach(place: Place, form: PlaceCreateForm) {
        if (form.categoryIds.isNotEmpty()) {
            categoryRepository.findAllById(form.categoryIds).forEach { place.addCategory(it) }
        }
        if (form.tagIds.isNotEmpty()) {
            tagRepository.findAllById(form.tagIds).forEach { place.addTag(it) }
        }
        if (form.brandIds.isNotEmpty()) {
            brandRepository.findAllById(form.brandIds).forEach { place.addBrand(it) }
        }
    }

    fun sync(place: Place, form: PlaceCreateForm) {
        val newCatIds = form.categoryIds.toSet()
        val newTagIds = form.tagIds.toSet()
        val newBrandIds = form.brandIds.toSet()

        place.placeCategories.removeIf { it.category.id !in newCatIds }
        place.placeTags.removeIf { it.tag.id !in newTagIds }
        place.placeBrands.removeIf { it.brand.id !in newBrandIds }

        val existingCatIds = place.placeCategories.map { it.category.id }.toSet()
        val existingTagIds = place.placeTags.map { it.tag.id }.toSet()
        val existingBrandIds = place.placeBrands.map { it.brand.id }.toSet()

        val catsToAdd = newCatIds - existingCatIds
        val tagsToAdd = newTagIds - existingTagIds
        val brandsToAdd = newBrandIds - existingBrandIds

        if (catsToAdd.isNotEmpty()) {
            categoryRepository.findAllById(catsToAdd).forEach { place.addCategory(it) }
        }
        if (tagsToAdd.isNotEmpty()) {
            tagRepository.findAllById(tagsToAdd).forEach { place.addTag(it) }
        }
        if (brandsToAdd.isNotEmpty()) {
            brandRepository.findAllById(brandsToAdd).forEach { place.addBrand(it) }
        }
    }
}
