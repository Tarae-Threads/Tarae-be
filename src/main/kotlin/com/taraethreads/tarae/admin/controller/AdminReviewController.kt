package com.taraethreads.tarae.admin.controller

import com.taraethreads.tarae.admin.service.AdminReviewService
import com.taraethreads.tarae.review.domain.ReviewTargetType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/admin/reviews")
class AdminReviewController(
    private val adminReviewService: AdminReviewService,
) {

    @GetMapping
    fun list(
        @RequestParam(defaultValue = "place") type: String,
        model: Model,
    ): String {
        val targetType = when (type.lowercase()) {
            "event" -> ReviewTargetType.EVENT
            else -> ReviewTargetType.PLACE
        }
        model.addAttribute("type", type)
        model.addAttribute("reviews", adminReviewService.getReviews(targetType))
        return "admin/reviews/list"
    }

    @PostMapping("/{id}/delete")
    fun delete(
        @PathVariable id: Long,
        @RequestParam(defaultValue = "place") type: String,
    ): String {
        adminReviewService.deleteReview(id)
        return "redirect:/admin/reviews?type=$type"
    }
}
