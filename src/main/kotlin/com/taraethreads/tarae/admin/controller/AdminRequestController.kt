package com.taraethreads.tarae.admin.controller

import com.taraethreads.tarae.admin.dto.EventCreateForm
import com.taraethreads.tarae.admin.dto.PlaceCreateForm
import com.taraethreads.tarae.admin.service.AdminRequestService
import com.taraethreads.tarae.event.domain.EventType
import com.taraethreads.tarae.place.repository.BrandRepository
import com.taraethreads.tarae.place.repository.CategoryRepository
import com.taraethreads.tarae.place.repository.TagRepository
import com.taraethreads.tarae.request.domain.RequestStatus
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
    private val categoryRepository: CategoryRepository,
    private val tagRepository: TagRepository,
    private val brandRepository: BrandRepository,
) {

    @GetMapping
    fun list(
        @RequestParam(defaultValue = "place") type: String,
        @RequestParam(required = false) status: RequestStatus?,
        model: Model,
    ): String {
        model.addAttribute("type", type)
        model.addAttribute("status", status)
        model.addAttribute("allStatuses", RequestStatus.entries)

        if (type == "event") {
            model.addAttribute("requests", adminRequestService.getEventRequests(status))
        } else {
            model.addAttribute("requests", adminRequestService.getPlaceRequests(status))
        }

        return "admin/requests/list"
    }

    // --- 장소 제보 상세 ---

    @GetMapping("/place/{id}")
    fun placeDetail(@PathVariable id: Long, model: Model): String {
        val placeRequest = adminRequestService.getPlaceRequest(id)
        model.addAttribute("request", placeRequest)
        model.addAttribute("form", PlaceCreateForm(
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
        ))
        model.addAttribute("categories", categoryRepository.findAll())
        model.addAttribute("tags", tagRepository.findAll())
        model.addAttribute("brands", brandRepository.findAll())
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
        ))
        model.addAttribute("eventTypes", EventType.entries)
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
