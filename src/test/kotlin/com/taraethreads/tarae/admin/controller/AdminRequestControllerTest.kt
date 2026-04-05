package com.taraethreads.tarae.admin.controller

import com.ninjasquad.springmockk.MockkBean
import com.taraethreads.tarae.admin.dto.EventCreateForm
import com.taraethreads.tarae.admin.dto.PlaceCreateForm
import com.taraethreads.tarae.admin.service.AdminRequestService
import com.taraethreads.tarae.event.domain.EventType
import com.taraethreads.tarae.place.domain.Brand
import com.taraethreads.tarae.place.domain.BrandType
import com.taraethreads.tarae.place.domain.Category
import com.taraethreads.tarae.place.domain.Tag
import com.taraethreads.tarae.place.repository.BrandRepository
import com.taraethreads.tarae.place.repository.CategoryRepository
import com.taraethreads.tarae.place.repository.TagRepository
import com.taraethreads.tarae.request.domain.EventRequest
import com.taraethreads.tarae.request.domain.PlaceRequest
import com.taraethreads.tarae.request.domain.RequestStatus
import com.taraethreads.tarae.request.domain.RequestType
import io.mockk.every
import io.mockk.verify
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
    lateinit var categoryRepository: CategoryRepository

    @MockkBean
    lateinit var tagRepository: TagRepository

    @MockkBean
    lateinit var brandRepository: BrandRepository

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
            every { categoryRepository.findAll() } returns emptyList()
            every { tagRepository.findAll() } returns emptyList()
            every { brandRepository.findAll() } returns emptyList()

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
            val place = com.taraethreads.tarae.place.domain.Place(
                name = "실과 바늘", region = "서울", district = "성수", address = "서울 성동구"
            )
            every { adminRequestService.approvePlaceRequest(1L, any()) } returns place

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
            every { adminRequestService.rejectPlaceRequest(1L) } returns Unit

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
            val event = com.taraethreads.tarae.event.domain.Event(
                title = "뜨개 팝업",
                eventType = EventType.EVENT_POPUP,
                startDate = LocalDate.of(2026, 5, 1),
            )
            every { adminRequestService.approveEventRequest(1L, any()) } returns event

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
            every { adminRequestService.rejectEventRequest(1L) } returns Unit

            // when & then
            mockMvc.post("/admin/requests/event/1/reject").andExpect {
                status { is3xxRedirection() }
                redirectedUrl("/admin/requests?type=event&status=PENDING")
            }
        }
    }
}
