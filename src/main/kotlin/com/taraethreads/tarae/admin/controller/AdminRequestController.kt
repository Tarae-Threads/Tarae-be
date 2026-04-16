package com.taraethreads.tarae.admin.controller

import com.taraethreads.tarae.admin.dto.EventCreateForm
import com.taraethreads.tarae.admin.dto.PlaceCreateForm
import com.taraethreads.tarae.admin.service.AdminEventService
import com.taraethreads.tarae.admin.service.AdminRequestService
import com.taraethreads.tarae.event.domain.EventType
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
@RequestMapping("/admin/requests")
class AdminRequestController(
    private val adminRequestService: AdminRequestService,
    private val adminEventService: AdminEventService,
    private val categoryService: CategoryService,
    private val tagService: TagService,
    private val brandService: BrandService,
) {

    @GetMapping
    fun list(
        @RequestParam(defaultValue = "place") type: String,
        @RequestParam(required = false) status: RequestStatus?,
        @RequestParam(required = false) requestType: RequestType?,
        model: Model,
    ): String {
        model.addAttribute("type", type)
        model.addAttribute("status", status)
        model.addAttribute("requestType", requestType)
        model.addAttribute("allStatuses", RequestStatus.entries)

        when (type.lowercase()) {
            "event" -> model.addAttribute("requests", adminRequestService.getEventRequests(status))
            "place" -> model.addAttribute("requests", adminRequestService.getPlaceRequests(status, requestType))
            else -> model.addAttribute("requests", emptyList<Any>())
        }

        return "admin/requests/list"
    }

    // --- 장소 제보 상세 ---

    @GetMapping("/place/{id}")
    fun placeDetail(@PathVariable id: Long, model: Model): String {
        val placeRequest = adminRequestService.getPlaceRequest(id)
        model.addAttribute("request", placeRequest)

        val placeId = placeRequest.placeId
        val form = if (placeRequest.requestType == RequestType.UPDATE && placeId != null) {
            val existingPlace = adminRequestService.getPlace(placeId)
            model.addAttribute("existingPlace", existingPlace)
            PlaceCreateForm(
                name = existingPlace.name,
                region = existingPlace.region,
                district = existingPlace.district,
                address = existingPlace.address,
                lat = existingPlace.lat,
                lng = existingPlace.lng,
                hoursText = existingPlace.hoursText,
                closedDays = existingPlace.closedDays,
                description = existingPlace.description,
                instagramUrl = existingPlace.instagramUrl,
                websiteUrl = existingPlace.websiteUrl,
                naverMapUrl = existingPlace.naverMapUrl,
                categoryIds = existingPlace.categories.map { it.id },
                tagIds = existingPlace.tags.map { it.id },
                brandIds = existingPlace.brands.map { it.id },
            )
        } else {
            PlaceCreateForm(
                name = placeRequest.name ?: "",
                address = placeRequest.address ?: "",
                lat = placeRequest.lat,
                lng = placeRequest.lng,
                hoursText = placeRequest.hoursText,
                closedDays = placeRequest.closedDays,
                instagramUrl = placeRequest.instagramUrl,
                websiteUrl = placeRequest.websiteUrl,
                naverMapUrl = placeRequest.naverMapUrl,
                categoryIds = placeRequest.categoryIds,
            )
        }

        model.addAttribute("form", form)

        val allCategories = categoryService.getCategories()
        val allBrands = brandService.getAll()
        model.addAttribute("categories", allCategories)
        model.addAttribute("tags", tagService.getAll())
        model.addAttribute("brands", allBrands)
        model.addAttribute("brandGroups", brandService.getBrandsGroupedByType())

        // 제보 원본에서 선택된 ID → 이름 resolve
        val categoryIdSet = placeRequest.categoryIds.toSet()
        model.addAttribute("selectedCategoryNames", allCategories.filter { it.id in categoryIdSet }.map { it.name })

        val brandMap = allBrands.associateBy { it.id }
        model.addAttribute("selectedBrandYarnNames", placeRequest.brandYarnIds.mapNotNull { brandMap[it]?.name })
        model.addAttribute("selectedBrandNeedleNames", placeRequest.brandNeedleIds.mapNotNull { brandMap[it]?.name })
        model.addAttribute("selectedBrandNotionsNames", placeRequest.brandNotionsIds.mapNotNull { brandMap[it]?.name })
        model.addAttribute("selectedBrandPatternbookNames", placeRequest.brandPatternbookIds.mapNotNull { brandMap[it]?.name })

        return "admin/requests/place-detail"
    }

    @PostMapping("/place/{id}/approve")
    fun approvePlaceRequest(
        @PathVariable id: Long,
        @ModelAttribute form: PlaceCreateForm,
    ): String {
        adminRequestService.approvePlaceRequest(id, form)
        return "redirect:/admin/requests?type=place&status=PENDING"
    }

    @PostMapping("/place/{id}/reject")
    fun rejectPlaceRequest(@PathVariable id: Long): String {
        adminRequestService.rejectPlaceRequest(id)
        return "redirect:/admin/requests?type=place&status=PENDING"
    }

    // --- 이벤트 제보 상세 ---

    @GetMapping("/event/{id}")
    fun eventDetail(@PathVariable id: Long, model: Model): String {
        val eventRequest = adminRequestService.getEventRequest(id)
        model.addAttribute("request", eventRequest)
        model.addAttribute("form", EventCreateForm(
            title = eventRequest.title,
            eventType = eventRequest.eventType,
            startDate = eventRequest.startDate,
            endDate = eventRequest.endDate,
            locationText = eventRequest.locationText,
            description = eventRequest.description,
            lat = eventRequest.lat,
            lng = eventRequest.lng,
            instagramUrl = eventRequest.instagramUrl,
            websiteUrl = eventRequest.websiteUrl,
            naverMapUrl = eventRequest.naverMapUrl,
        ))
        model.addAttribute("eventTypes", EventType.entries)
        model.addAttribute("places", adminEventService.placeOptions())
        return "admin/requests/event-detail"
    }

    @PostMapping("/event/{id}/approve")
    fun approveEventRequest(
        @PathVariable id: Long,
        @ModelAttribute form: EventCreateForm,
    ): String {
        adminRequestService.approveEventRequest(id, form)
        return "redirect:/admin/requests?type=event&status=PENDING"
    }

    @PostMapping("/event/{id}/reject")
    fun rejectEventRequest(@PathVariable id: Long): String {
        adminRequestService.rejectEventRequest(id)
        return "redirect:/admin/requests?type=event&status=PENDING"
    }
}
