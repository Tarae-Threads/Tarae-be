package com.taraethreads.tarae.admin.service

import com.taraethreads.tarae.admin.dto.ShopCreateForm
import com.taraethreads.tarae.place.repository.BrandRepository
import com.taraethreads.tarae.place.repository.CategoryRepository
import com.taraethreads.tarae.place.repository.TagRepository
import com.taraethreads.tarae.shop.domain.Shop
import org.springframework.stereotype.Component

@Component
class ShopAssociationSyncer(
    private val categoryRepository: CategoryRepository,
    private val tagRepository: TagRepository,
    private val brandRepository: BrandRepository,
) {

    fun attach(shop: Shop, form: ShopCreateForm) {
        if (form.categoryIds.isNotEmpty()) {
            categoryRepository.findAllById(form.categoryIds).forEach { shop.addCategory(it) }
        }
        if (form.tagIds.isNotEmpty()) {
            tagRepository.findAllById(form.tagIds).forEach { shop.addTag(it) }
        }
        if (form.brandIds.isNotEmpty()) {
            brandRepository.findAllById(form.brandIds).forEach { shop.addBrand(it) }
        }
    }

    fun sync(shop: Shop, form: ShopCreateForm) {
        val newCatIds = form.categoryIds.toSet()
        val newTagIds = form.tagIds.toSet()
        val newBrandIds = form.brandIds.toSet()

        shop.shopCategories.removeIf { it.category.id !in newCatIds }
        shop.shopTags.removeIf { it.tag.id !in newTagIds }
        shop.shopBrands.removeIf { it.brand.id !in newBrandIds }

        val existingCatIds = shop.shopCategories.map { it.category.id }.toSet()
        val existingTagIds = shop.shopTags.map { it.tag.id }.toSet()
        val existingBrandIds = shop.shopBrands.map { it.brand.id }.toSet()

        val catsToAdd = newCatIds - existingCatIds
        val tagsToAdd = newTagIds - existingTagIds
        val brandsToAdd = newBrandIds - existingBrandIds

        if (catsToAdd.isNotEmpty()) {
            categoryRepository.findAllById(catsToAdd).forEach { shop.addCategory(it) }
        }
        if (tagsToAdd.isNotEmpty()) {
            tagRepository.findAllById(tagsToAdd).forEach { shop.addTag(it) }
        }
        if (brandsToAdd.isNotEmpty()) {
            brandRepository.findAllById(brandsToAdd).forEach { shop.addBrand(it) }
        }
    }
}
