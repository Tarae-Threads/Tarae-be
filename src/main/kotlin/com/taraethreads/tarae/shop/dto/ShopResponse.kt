package com.taraethreads.tarae.shop.dto

import com.taraethreads.tarae.place.dto.BrandDto
import com.taraethreads.tarae.place.dto.CategoryDto
import com.taraethreads.tarae.place.dto.TagDto
import com.taraethreads.tarae.shop.domain.Shop
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "온라인샵 목록 응답")
data class ShopListResponse(
    @Schema(description = "온라인샵 ID", example = "1") val id: Long,
    @Schema(description = "온라인샵명", example = "실뭉치샵") val name: String,
    @Schema(description = "인스타그램 URL") val instagramUrl: String?,
    @Schema(description = "네이버 스마트스토어 URL") val naverUrl: String?,
    @Schema(description = "웹사이트 URL") val websiteUrl: String?,
    @Schema(description = "카테고리 목록") val categories: List<CategoryDto>,
    @Schema(description = "태그 목록") val tags: List<TagDto>,
    @Schema(description = "브랜드 목록") val brands: List<BrandDto>,
) {
    companion object {
        fun from(shop: Shop) = ShopListResponse(
            id = shop.id,
            name = shop.name,
            instagramUrl = shop.instagramUrl,
            naverUrl = shop.naverUrl,
            websiteUrl = shop.websiteUrl,
            categories = shop.shopCategories.map { CategoryDto(it.category.id, it.category.name) },
            tags = shop.shopTags.map { TagDto(it.tag.id, it.tag.name) },
            brands = shop.shopBrands.map { BrandDto(it.brand.id, it.brand.name, it.brand.type.name) },
        )
    }
}

@Schema(description = "온라인샵 상세 응답")
data class ShopDetailResponse(
    @Schema(description = "온라인샵 ID", example = "1") val id: Long,
    @Schema(description = "온라인샵명", example = "실뭉치샵") val name: String,
    @Schema(description = "인스타그램 URL") val instagramUrl: String?,
    @Schema(description = "네이버 스마트스토어 URL") val naverUrl: String?,
    @Schema(description = "웹사이트 URL") val websiteUrl: String?,
    @Schema(description = "카테고리 목록") val categories: List<CategoryDto>,
    @Schema(description = "태그 목록") val tags: List<TagDto>,
    @Schema(description = "브랜드 목록") val brands: List<BrandDto>,
) {
    companion object {
        fun from(shop: Shop) = ShopDetailResponse(
            id = shop.id,
            name = shop.name,
            instagramUrl = shop.instagramUrl,
            naverUrl = shop.naverUrl,
            websiteUrl = shop.websiteUrl,
            categories = shop.shopCategories.map { CategoryDto(it.category.id, it.category.name) },
            tags = shop.shopTags.map { TagDto(it.tag.id, it.tag.name) },
            brands = shop.shopBrands.map { BrandDto(it.brand.id, it.brand.name, it.brand.type.name) },
        )
    }
}
