package com.taraethreads.tarae.admin.controller

import com.taraethreads.tarae.admin.dto.AdminInquiryReplyRequest
import com.taraethreads.tarae.admin.service.AdminInquiryService
import com.taraethreads.tarae.inquiry.domain.InquiryStatus
import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/admin/inquiries")
class AdminInquiryController(
    private val adminInquiryService: AdminInquiryService,
) {

    @GetMapping
    fun list(
        @RequestParam(required = false) status: InquiryStatus?,
        model: Model,
    ): String {
        model.addAttribute("inquiries", adminInquiryService.getInquiries(status))
        model.addAttribute("status", status)
        model.addAttribute("allStatuses", InquiryStatus.entries)
        return "admin/inquiries/list"
    }

    @GetMapping("/{id}")
    fun detail(@PathVariable id: Long, model: Model): String {
        model.addAttribute("inquiry", adminInquiryService.getInquiryDetail(id))
        return "admin/inquiries/detail"
    }

    @PostMapping("/{id}/reply")
    fun reply(
        @PathVariable id: Long,
        @Valid @ModelAttribute request: AdminInquiryReplyRequest,
        bindingResult: BindingResult,
        model: Model,
    ): String {
        if (bindingResult.hasErrors()) {
            model.addAttribute("inquiry", adminInquiryService.getInquiryDetail(id))
            model.addAttribute("replyError", "답변 내용을 입력해주세요.")
            return "admin/inquiries/detail"
        }
        adminInquiryService.reply(id, request.replyBody)
        return "redirect:/admin/inquiries/$id"
    }

    @PostMapping("/{id}/resend")
    fun resend(@PathVariable id: Long): String {
        adminInquiryService.resend(id)
        return "redirect:/admin/inquiries/$id"
    }

    @PostMapping("/{id}/close")
    fun close(@PathVariable id: Long): String {
        adminInquiryService.close(id)
        return "redirect:/admin/inquiries"
    }

    @PostMapping("/{id}/memo")
    fun updateMemo(
        @PathVariable id: Long,
        @RequestParam(required = false) memo: String?,
    ): String {
        adminInquiryService.updateMemo(id, memo)
        return "redirect:/admin/inquiries/$id"
    }
}
