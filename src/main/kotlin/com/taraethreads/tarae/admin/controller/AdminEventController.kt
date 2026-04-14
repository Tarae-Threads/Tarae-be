package com.taraethreads.tarae.admin.controller

import com.taraethreads.tarae.admin.dto.AdminEventStatusFilter
import com.taraethreads.tarae.admin.dto.EventBulkCreateRequest
import com.taraethreads.tarae.admin.dto.EventCreateForm
import com.taraethreads.tarae.admin.service.AdminEventService
import com.taraethreads.tarae.event.domain.EventType
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
@RequestMapping("/admin/events")
class AdminEventController(
    private val adminEventService: AdminEventService,
) {

    @GetMapping
    fun list(
        @RequestParam(required = false) filter: AdminEventStatusFilter?,
        @RequestParam(defaultValue = "0") page: Int,
        model: Model,
    ): String {
        val effectiveFilter = filter ?: AdminEventStatusFilter.ALL
        val pageable = PageRequest.of(page, PAGE_SIZE)
        val result = adminEventService.list(effectiveFilter, pageable)
        model.addAttribute("events", result.content)
        model.addAttribute("page", result)
        model.addAttribute("filter", effectiveFilter)
        model.addAttribute("filters", AdminEventStatusFilter.entries)
        return "admin/events/list"
    }

    @GetMapping("/new")
    fun newForm(model: Model): String {
        model.addAttribute("mode", "create")
        model.addAttribute("form", EventCreateForm())
        model.addAttribute("eventTypes", EventType.entries)
        return "admin/events/form"
    }

    @PostMapping
    fun create(@ModelAttribute request: EventBulkCreateRequest): String {
        val valid = request.events.filter { it.title.isNotBlank() }
        if (valid.isNotEmpty()) {
            adminEventService.createBulk(valid)
        }
        return "redirect:/admin/events"
    }

    @GetMapping("/{id}/edit")
    fun editForm(@PathVariable id: Long, model: Model): String {
        model.addAttribute("mode", "edit")
        model.addAttribute("eventId", id)
        model.addAttribute("form", adminEventService.getForm(id))
        model.addAttribute("eventTypes", EventType.entries)
        return "admin/events/form"
    }

    @PostMapping("/{id}")
    fun update(@PathVariable id: Long, @ModelAttribute form: EventCreateForm): String {
        adminEventService.update(id, form)
        return "redirect:/admin/events"
    }

    @PostMapping("/{id}/toggle-active")
    fun toggleActive(@PathVariable id: Long): String {
        adminEventService.toggleActive(id)
        return "redirect:/admin/events"
    }

    @PostMapping("/{id}/delete")
    fun delete(@PathVariable id: Long): String {
        adminEventService.delete(id)
        return "redirect:/admin/events"
    }

    companion object {
        const val PAGE_SIZE = 20
    }
}
