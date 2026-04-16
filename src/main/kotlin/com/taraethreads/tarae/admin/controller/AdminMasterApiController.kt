package com.taraethreads.tarae.admin.controller

import com.taraethreads.tarae.global.exception.CustomException
import com.taraethreads.tarae.global.exception.ErrorCode
import com.taraethreads.tarae.place.domain.BrandType
import com.taraethreads.tarae.place.service.BrandService
import com.taraethreads.tarae.place.service.CategoryService
import com.taraethreads.tarae.place.service.TagService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/api")
class AdminMasterApiController(
    private val categoryService: CategoryService,
    private val tagService: TagService,
    private val brandService: BrandService,
) {

    data class QuickCreateResponse(val id: Long, val name: String, val type: String? = null)

    @PostMapping("/categories")
    fun createCategory(@RequestParam name: String): QuickCreateResponse {
        requireNotBlank(name)
        val category = categoryService.create(name)
        return QuickCreateResponse(id = category.id, name = category.name)
    }

    @PostMapping("/tags")
    fun createTag(@RequestParam name: String): QuickCreateResponse {
        requireNotBlank(name)
        val tag = tagService.create(name)
        return QuickCreateResponse(id = tag.id, name = tag.name)
    }

    @PostMapping("/brands")
    fun createBrand(@RequestParam name: String, @RequestParam type: BrandType): QuickCreateResponse {
        requireNotBlank(name)
        val brand = brandService.create(name, type)
        return QuickCreateResponse(id = brand.id, name = brand.name, type = brand.type.name)
    }

    private fun requireNotBlank(name: String) {
        if (name.isBlank()) throw CustomException(ErrorCode.INVALID_INPUT)
    }
}
