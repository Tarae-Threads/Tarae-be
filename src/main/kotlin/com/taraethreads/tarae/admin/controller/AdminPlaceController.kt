package com.taraethreads.tarae.admin.controller

import com.taraethreads.tarae.admin.dto.PlaceBulkCreateRequest
import com.taraethreads.tarae.admin.dto.PlaceCreateForm
import com.taraethreads.tarae.admin.service.AdminPlaceService
import com.taraethreads.tarae.place.service.BrandService
import com.taraethreads.tarae.place.service.CategoryService
import com.taraethreads.tarae.place.service.TagService
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/admin/places")
class AdminPlaceController(
    private val adminPlaceService: AdminPlaceService,
    private val categoryService: CategoryService,
    private val tagService: TagService,
    private val brandService: BrandService,
) {

    @GetMapping
    fun list(
        @RequestParam(required = false) keyword: String?,
        @RequestParam(defaultValue = "0") page: Int,
        model: Model,
    ): String {
        val pageable = PageRequest.of(page, PAGE_SIZE)
        val result = adminPlaceService.list(keyword, pageable)
        model.addAttribute("places", result.content)
        model.addAttribute("page", result)
        model.addAttribute("keyword", keyword ?: "")
        return "admin/places/list"
    }

    @GetMapping("/new")
    fun newForm(model: Model): String {
        model.addAttribute("mode", "create")
        model.addAttribute("form", PlaceCreateForm())
        loadSelectorOptions(model)
        return "admin/places/form"
    }

    @PostMapping
    fun create(@ModelAttribute request: PlaceBulkCreateRequest): String {
        val valid = request.places.filter { it.name.isNotBlank() }
        if (valid.isNotEmpty()) {
            adminPlaceService.createBulk(valid)
        }
        return "redirect:/admin/places"
    }

    @GetMapping("/{id}/edit")
    fun editForm(@PathVariable id: Long, model: Model): String {
        model.addAttribute("mode", "edit")
        model.addAttribute("placeId", id)
        model.addAttribute("form", adminPlaceService.getForm(id))
        loadSelectorOptions(model)
        return "admin/places/form"
    }

    @PostMapping("/{id}")
    fun update(@PathVariable id: Long, @ModelAttribute form: PlaceCreateForm): String {
        adminPlaceService.update(id, form)
        return "redirect:/admin/places"
    }

    @PostMapping("/{id}/toggle-active")
    fun toggleActive(@PathVariable id: Long): String {
        adminPlaceService.toggleActive(id)
        return "redirect:/admin/places"
    }

    @PostMapping("/{id}/delete")
    fun delete(@PathVariable id: Long): String {
        adminPlaceService.delete(id)
        return "redirect:/admin/places"
    }

    private fun loadSelectorOptions(model: Model) {
        model.addAttribute("categories", categoryService.getCategories())
        model.addAttribute("tags", tagService.getAll())
        model.addAttribute("brandGroups", brandService.getBrandsGroupedByType())
    }

    companion object {
        const val PAGE_SIZE = 20
    }
}
