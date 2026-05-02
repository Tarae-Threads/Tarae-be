package com.taraethreads.tarae.admin.controller

import com.taraethreads.tarae.admin.dto.ShopBulkCreateRequest
import com.taraethreads.tarae.admin.dto.ShopCreateForm
import com.taraethreads.tarae.admin.service.AdminShopService
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
@RequestMapping("/admin/shops")
class AdminShopController(
    private val adminShopService: AdminShopService,
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
        val result = adminShopService.list(keyword, pageable)
        model.addAttribute("shops", result.content)
        model.addAttribute("page", result)
        model.addAttribute("keyword", keyword ?: "")
        return "admin/shops/list"
    }

    @GetMapping("/new")
    fun newForm(model: Model): String {
        model.addAttribute("mode", "create")
        model.addAttribute("form", ShopCreateForm())
        loadSelectorOptions(model)
        return "admin/shops/form"
    }

    @PostMapping
    fun create(@ModelAttribute request: ShopBulkCreateRequest): String {
        val valid = request.shops.filter { it.name.isNotBlank() }
        if (valid.isNotEmpty()) {
            adminShopService.createBulk(valid)
        }
        return "redirect:/admin/shops"
    }

    @GetMapping("/{id}/edit")
    fun editForm(@PathVariable id: Long, model: Model): String {
        model.addAttribute("mode", "edit")
        model.addAttribute("shopId", id)
        model.addAttribute("form", adminShopService.getForm(id))
        loadSelectorOptions(model)
        return "admin/shops/form"
    }

    @PostMapping("/{id}")
    fun update(@PathVariable id: Long, @ModelAttribute form: ShopCreateForm): String {
        adminShopService.update(id, form)
        return "redirect:/admin/shops"
    }

    @PostMapping("/{id}/toggle-active")
    fun toggleActive(@PathVariable id: Long): String {
        adminShopService.toggleActive(id)
        return "redirect:/admin/shops"
    }

    @PostMapping("/{id}/delete")
    fun delete(@PathVariable id: Long): String {
        adminShopService.delete(id)
        return "redirect:/admin/shops"
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
