package com.taraethreads.tarae.admin.controller

import com.taraethreads.tarae.admin.dto.ShopCreateForm
import com.taraethreads.tarae.admin.service.AdminShopRequestService
import com.taraethreads.tarae.place.service.BrandService
import com.taraethreads.tarae.place.service.CategoryService
import com.taraethreads.tarae.place.service.TagService
import com.taraethreads.tarae.request.domain.RequestStatus
import com.taraethreads.tarae.request.domain.RequestType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/admin/shop-requests")
class AdminShopRequestController(
    private val adminShopRequestService: AdminShopRequestService,
    private val categoryService: CategoryService,
    private val tagService: TagService,
    private val brandService: BrandService,
) {

    @GetMapping
    fun list(
        @RequestParam(required = false) status: RequestStatus?,
        @RequestParam(required = false) requestType: RequestType?,
        model: Model,
    ): String {
        model.addAttribute("status", status)
        model.addAttribute("requestType", requestType)
        model.addAttribute("allStatuses", RequestStatus.entries)
        model.addAttribute("requests", adminShopRequestService.getShopRequests(status, requestType))
        return "admin/shop-requests/list"
    }

    @GetMapping("/{id}")
    fun detail(@PathVariable id: Long, model: Model): String {
        val shopRequest = adminShopRequestService.getShopRequest(id)
        model.addAttribute("request", shopRequest)

        val shopId = shopRequest.shopId
        val form = if (shopRequest.requestType == RequestType.UPDATE && shopId != null) {
            val existingShop = adminShopRequestService.getShop(shopId)
            model.addAttribute("existingShop", existingShop)
            ShopCreateForm(
                name = existingShop.name,
                instagramUrl = existingShop.instagramUrl,
                naverUrl = existingShop.naverUrl,
                websiteUrl = existingShop.websiteUrl,
                categoryIds = existingShop.categories.map { it.id },
                tagIds = existingShop.tags.map { it.id },
                brandIds = existingShop.brands.map { it.id },
            )
        } else {
            ShopCreateForm(
                name = shopRequest.name ?: "",
                instagramUrl = shopRequest.instagramUrl,
                naverUrl = shopRequest.naverUrl,
                websiteUrl = shopRequest.websiteUrl,
                categoryIds = shopRequest.categoryIds,
            )
        }

        model.addAttribute("form", form)

        val allCategories = categoryService.getCategories()
        val allBrands = brandService.getAll()
        model.addAttribute("categories", allCategories)
        model.addAttribute("tags", tagService.getAll())
        model.addAttribute("brands", allBrands)
        model.addAttribute("brandGroups", brandService.getBrandsGroupedByType())

        val categoryIdSet = shopRequest.categoryIds.toSet()
        model.addAttribute("selectedCategoryNames", allCategories.filter { it.id in categoryIdSet }.map { it.name })

        val brandMap = allBrands.associateBy { it.id }
        model.addAttribute("selectedBrandYarnNames", shopRequest.brandYarnIds.mapNotNull { brandMap[it]?.name })
        model.addAttribute("selectedBrandNeedleNames", shopRequest.brandNeedleIds.mapNotNull { brandMap[it]?.name })
        model.addAttribute("selectedBrandNotionsNames", shopRequest.brandNotionsIds.mapNotNull { brandMap[it]?.name })
        model.addAttribute("selectedBrandPatternbookNames", shopRequest.brandPatternbookIds.mapNotNull { brandMap[it]?.name })

        return "admin/shop-requests/detail"
    }

    @PostMapping("/{id}/approve")
    fun approve(
        @PathVariable id: Long,
        @ModelAttribute form: ShopCreateForm,
    ): String {
        adminShopRequestService.approveShopRequest(id, form)
        return "redirect:/admin/shop-requests?status=PENDING"
    }

    @PostMapping("/{id}/reject")
    fun reject(@PathVariable id: Long): String {
        adminShopRequestService.rejectShopRequest(id)
        return "redirect:/admin/shop-requests?status=PENDING"
    }
}
