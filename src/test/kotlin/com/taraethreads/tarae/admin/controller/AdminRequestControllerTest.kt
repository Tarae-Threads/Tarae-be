package com.taraethreads.tarae.admin.controller

import com.ninjasquad.springmockk.MockkBean
import com.taraethreads.tarae.admin.service.AdminRequestService
import com.taraethreads.tarae.event.domain.EventType
import com.taraethreads.tarae.place.dto.BrandResponse
import com.taraethreads.tarae.place.dto.CategoryResponse
import com.taraethreads.tarae.place.dto.TagResponse
import com.taraethreads.tarae.place.service.BrandService
import com.taraethreads.tarae.place.service.CategoryService
import com.taraethreads.tarae.place.service.TagService
import com.taraethreads.tarae.request.domain.EventRequest
import com.taraethreads.tarae.request.domain.PlaceRequest
import com.taraethreads.tarae.request.domain.RequestStatus
import com.taraethreads.tarae.request.domain.RequestType
import io.mockk.every
import io.mockk.justRun
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.time.LocalDate
import java.time.LocalDateTime

@WebMvcTest(
    controllers = [AdminRequestController::class],
    excludeAutoConfiguration = [SecurityAutoConfiguration::class, SecurityFilterAutoConfiguration::class],
)
class AdminRequestControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var adminRequestService: AdminRequestService

    @MockkBean
    lateinit var categoryService: CategoryService

    @MockkBean
    lateinit var tagService: TagService

    @MockkBean
    lateinit var brandService: BrandService

    @Nested
    inner class `GET 제보 목록` {

        @Test
        fun `type=place이면 장소 제보 목록을 반환한다`() {
            // given
            val pr = PlaceRequest(requestType = RequestType.NEW, name = "실과 바늘")
            pr.javaClass.superclass.getDeclaredField("createdAt").apply {
                isAccessible = true
                set(pr, LocalDateTime.of(2026, 4, 5, 0, 0))
            }
            every { adminRequestService.getPlaceRequests(RequestStatus.PENDING) } returns listOf(pr)

            // when & then
            mockMvc.get("/admin/requests?type=place&status=PENDING").andExpect {
                status { isOk() }
                model { attribute("type", "place") }
            }
        }

        @Test
        fun `type=event이면 이벤트 제보 목록을 반환한다`() {
            // given
            every { adminRequestService.getEventRequests(null) } returns emptyList()

            // when & then
            mockMvc.get("/admin/requests?type=event").andExpect {
                status { isOk() }
                model { attribute("type", "event") }
            }
        }
    }

    @Nested
    inner class `GET 장소 제보 상세` {

        @Test
        fun `장소 제보 상세를 반환한다`() {
            // given
            every { adminRequestService.getPlaceRequest(1L) } returns
                PlaceRequest(requestType = RequestType.NEW, name = "실과 바늘")
            every { categoryService.getCategories() } returns listOf(CategoryResponse(id = 1L, name = "뜨개샵"))
            every { tagService.getAll() } returns listOf(TagResponse(id = 1L, name = "주차가능"))
            every { brandService.getAll() } returns listOf(BrandResponse(id = 1L, name = "산네스간", type = "YARN"))

            // when & then
            mockMvc.get("/admin/requests/place/1").andExpect {
                status { isOk() }
                model { attributeExists("request") }
                model { attributeExists("form") }
                model { attributeExists("categories") }
            }
        }
    }

    @Nested
    inner class `POST 장소 제보 승인` {

        @Test
        fun `승인 후 PENDING 목록으로 리다이렉트한다`() {
            // given
            justRun { adminRequestService.approvePlaceRequest(1L, any()) }

            // when & then
            mockMvc.post("/admin/requests/place/1/approve") {
                param("name", "실과 바늘")
                param("region", "서울")
                param("district", "성수")
                param("address", "서울 성동구")
            }.andExpect {
                status { is3xxRedirection() }
                redirectedUrl("/admin/requests?type=place&status=PENDING")
            }
        }
    }

    @Nested
    inner class `POST 장소 제보 거절` {

        @Test
        fun `거절 후 PENDING 목록으로 리다이렉트한다`() {
            // given
            justRun { adminRequestService.rejectPlaceRequest(1L) }

            // when & then
            mockMvc.post("/admin/requests/place/1/reject").andExpect {
                status { is3xxRedirection() }
                redirectedUrl("/admin/requests?type=place&status=PENDING")
            }
        }
    }

    @Nested
    inner class `GET 이벤트 제보 상세` {

        @Test
        fun `이벤트 제보 상세를 반환한다`() {
            // given
            every { adminRequestService.getEventRequest(1L) } returns EventRequest(
                title = "뜨개 팝업",
                eventType = EventType.EVENT_POPUP,
                startDate = LocalDate.of(2026, 5, 1),
            )

            // when & then
            mockMvc.get("/admin/requests/event/1").andExpect {
                status { isOk() }
                model { attributeExists("request") }
                model { attributeExists("form") }
                model { attributeExists("eventTypes") }
            }
        }
    }

    @Nested
    inner class `POST 이벤트 제보 승인` {

        @Test
        fun `승인 후 PENDING 목록으로 리다이렉트한다`() {
            // given
            justRun { adminRequestService.approveEventRequest(1L, any()) }

            // when & then
            mockMvc.post("/admin/requests/event/1/approve") {
                param("title", "뜨개 팝업")
                param("eventType", "EVENT_POPUP")
                param("startDate", "2026-05-01")
            }.andExpect {
                status { is3xxRedirection() }
                redirectedUrl("/admin/requests?type=event&status=PENDING")
            }
        }
    }

    @Nested
    inner class `POST 이벤트 제보 거절` {

        @Test
        fun `거절 후 PENDING 목록으로 리다이렉트한다`() {
            // given
            justRun { adminRequestService.rejectEventRequest(1L) }

            // when & then
            mockMvc.post("/admin/requests/event/1/reject").andExpect {
                status { is3xxRedirection() }
                redirectedUrl("/admin/requests?type=event&status=PENDING")
            }
        }
    }
}
