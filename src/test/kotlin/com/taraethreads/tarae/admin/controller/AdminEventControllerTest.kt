package com.taraethreads.tarae.admin.controller

import com.ninjasquad.springmockk.MockkBean
import com.taraethreads.tarae.admin.dto.AdminEventStatusFilter
import com.taraethreads.tarae.admin.service.AdminEventService
import io.mockk.every
import io.mockk.justRun
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@WebMvcTest(
    controllers = [AdminEventController::class],
    excludeAutoConfiguration = [SecurityAutoConfiguration::class, SecurityFilterAutoConfiguration::class],
)
class AdminEventControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var adminEventService: AdminEventService

    @Test
    fun `GET 목록은 200을 반환한다`() {
        every { adminEventService.list(AdminEventStatusFilter.ALL, any()) } returns
            PageImpl(emptyList(), PageRequest.of(0, 20), 0)

        mockMvc.get("/admin/events").andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `POST 토글은 302 리다이렉트한다`() {
        justRun { adminEventService.toggleActive(1L) }

        mockMvc.post("/admin/events/1/toggle-active").andExpect {
            status { is3xxRedirection() }
        }
    }
}
