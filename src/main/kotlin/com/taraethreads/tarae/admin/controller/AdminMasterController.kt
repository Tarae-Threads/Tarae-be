package com.taraethreads.tarae.admin.controller

import com.taraethreads.tarae.admin.dto.BrandCreateRequest
import com.taraethreads.tarae.admin.dto.BrandUpdateRequest
import com.taraethreads.tarae.admin.dto.MasterCreateRequest
import com.taraethreads.tarae.place.domain.BrandType
import com.taraethreads.tarae.place.service.BrandService
import com.taraethreads.tarae.place.service.CategoryService
import com.taraethreads.tarae.place.service.TagService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/admin/masters")
class AdminMasterController(
    private val categoryService: CategoryService,
    private val tagService: TagService,
    private val brandService: BrandService,
) {

    @GetMapping
    fun list(
        @RequestParam(defaultValue = "categories") type: String,
        model: Model,
    ): String {
        model.addAttribute("type", type)
        model.addAttribute("categories", categoryService.getAllForAdmin())
        model.addAttribute("tags", tagService.getAllForAdmin())
        model.addAttribute("brands", brandService.getAllForAdmin())
        model.addAttribute("brandTypes", BrandType.entries)
        return "admin/masters/list"
    }

    @PostMapping("/categories")
    fun createCategory(@ModelAttribute request: MasterCreateRequest): String {
        categoryService.create(request.name)
        return "redirect:/admin/masters?type=categories"
    }

    @PostMapping("/categories/{id}")
    fun renameCategory(@PathVariable id: Long, @ModelAttribute request: MasterCreateRequest): String {
        categoryService.rename(id, request.name)
        return "redirect:/admin/masters?type=categories"
    }

    @PostMapping("/tags")
    fun createTag(@ModelAttribute request: MasterCreateRequest): String {
        tagService.create(request.name)
        return "redirect:/admin/masters?type=tags"
    }

    @PostMapping("/tags/{id}")
    fun renameTag(@PathVariable id: Long, @ModelAttribute request: MasterCreateRequest): String {
        tagService.rename(id, request.name)
        return "redirect:/admin/masters?type=tags"
    }

    @PostMapping("/brands")
    fun createBrand(@ModelAttribute request: BrandCreateRequest): String {
        brandService.create(request.name, request.type)
        return "redirect:/admin/masters?type=brands"
    }

    @PostMapping("/brands/{id}")
    fun updateBrand(@PathVariable id: Long, @ModelAttribute request: BrandUpdateRequest): String {
        brandService.update(id, request.name, request.type)
        return "redirect:/admin/masters?type=brands"
    }
}
