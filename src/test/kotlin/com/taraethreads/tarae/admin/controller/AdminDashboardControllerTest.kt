package com.taraethreads.tarae.admin.controller

import com.ninjasquad.springmockk.MockkBean
import com.taraethreads.tarae.admin.service.AdminDashboardService
import io.mockk.every
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@WebMvcTest(
    controllers = [AdminDashboardController::class],
    excludeAutoConfiguration = [SecurityAutoConfiguration::class, SecurityFilterAutoConfiguration::class],
)
class AdminDashboardControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var adminDashboardService: AdminDashboardService

    @Nested
    inner class `GET 대시보드` {

        @Test
        fun `통계 데이터를 모델에 담아 대시보드 뷰를 반환한다`() {
            // given
            every { adminDashboardService.getPlaceCount() } returns 10L
            every { adminDashboardService.getEventCount() } returns 5L
            every { adminDashboardService.getPendingPlaceRequestCount() } returns 3L
            every { adminDashboardService.getPendingEventRequestCount() } returns 2L
            every { adminDashboardService.getReviewCount() } returns 7L

            // when & then
            mockMvc.get("/admin").andExpect {
                status { isOk() }
                model { attribute("placeCount", 10L) }
                model { attribute("eventCount", 5L) }
                model { attribute("pendingPlaceRequestCount", 3L) }
                model { attribute("pendingEventRequestCount", 2L) }
                model { attribute("reviewCount", 7L) }
            }
        }
    }
}
