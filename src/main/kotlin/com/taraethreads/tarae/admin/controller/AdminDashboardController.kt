package com.taraethreads.tarae.admin.controller

import com.taraethreads.tarae.admin.service.AdminDashboardService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class AdminDashboardController(
    private val adminDashboardService: AdminDashboardService,
) {

    @GetMapping("/admin")
    fun dashboard(model: Model): String {
        model.addAttribute("placeCount", adminDashboardService.getPlaceCount())
        model.addAttribute("eventCount", adminDashboardService.getEventCount())
        model.addAttribute("pendingPlaceRequestCount", adminDashboardService.getPendingPlaceRequestCount())
        model.addAttribute("pendingEventRequestCount", adminDashboardService.getPendingEventRequestCount())
        model.addAttribute("reviewCount", adminDashboardService.getReviewCount())
        model.addAttribute("expiringSoonEventCount", adminDashboardService.getExpiringSoonEventCount())
        model.addAttribute("pendingInquiryCount", adminDashboardService.getPendingInquiryCount())
        return "admin/dashboard"
    }
}
