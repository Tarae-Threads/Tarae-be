package com.taraethreads.tarae.event.controller

import com.ninjasquad.springmockk.MockkBean
import com.taraethreads.tarae.event.domain.EventType
import com.taraethreads.tarae.event.dto.EventDetailResponse
import com.taraethreads.tarae.event.dto.EventListResponse
import com.taraethreads.tarae.event.service.EventService
import com.taraethreads.tarae.global.exception.CustomException
import com.taraethreads.tarae.global.exception.ErrorCode
import com.taraethreads.tarae.global.exception.GlobalExceptionHandler
import io.mockk.every
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.time.LocalDate

@WebMvcTest(
    controllers = [EventController::class, GlobalExceptionHandler::class],
    excludeAutoConfiguration = [SecurityAutoConfiguration::class, SecurityFilterAutoConfiguration::class],
)
class EventControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var eventService: EventService

    private fun eventListResponse(title: String = "뜨개 팝업") = EventListResponse(
        id = 1L,
        title = title,
        eventType = "EVENT_POPUP",
        startDate = LocalDate.of(2026, 5, 1),
        endDate = null,
        locationText = null,
        lat = null,
        lng = null,
        active = true,
        links = null,
    )

    private fun eventDetailResponse(title: String = "뜨개 팝업") = EventDetailResponse(
        id = 1L,
        title = title,
        description = null,
        eventType = "EVENT_POPUP",
        startDate = LocalDate.of(2026, 5, 1),
        endDate = null,
        placeId = null,
        locationText = null,
        lat = null,
        lng = null,
        active = true,
        links = null,
    )

    @Nested
    inner class `GET 목록 조회` {

        @Test
        fun `active 파라미터 없이 호출하면 active=true 기본값으로 조회한다`() {
            // given
            every { eventService.getEvents(null, true) } returns listOf(eventListResponse())

            // when & then
            mockMvc.get("/api/events").andExpect {
                status { isOk() }
                jsonPath("$.data[0].title") { value("뜨개 팝업") }
                jsonPath("$.data[0].eventType") { value("EVENT_POPUP") }
                jsonPath("$.data[0].active") { value(true) }
            }
        }

        @Test
        fun `eventType 파라미터를 서비스에 전달한다`() {
            // given
            every { eventService.getEvents(EventType.SALE, true) } returns emptyList()

            // when & then
            mockMvc.get("/api/events?eventType=SALE").andExpect {
                status { isOk() }
            }
        }

        @Test
        fun `active 파라미터를 서비스에 전달한다`() {
            // given
            every { eventService.getEvents(null, true) } returns listOf(eventListResponse())

            // when & then
            mockMvc.get("/api/events?active=true").andExpect {
                status { isOk() }
            }
        }
    }

    @Nested
    inner class `GET 상세 조회` {

        @Test
        fun `200과 이벤트 상세를 반환한다`() {
            // given
            every { eventService.getEvent(1L) } returns eventDetailResponse()

            // when & then
            mockMvc.get("/api/events/1").andExpect {
                status { isOk() }
                jsonPath("$.data.title") { value("뜨개 팝업") }
                jsonPath("$.data.eventType") { value("EVENT_POPUP") }
                jsonPath("$.data.active") { value(true) }
            }
        }

        @Test
        fun `존재하지 않는 id 요청 시 404를 반환한다`() {
            // given
            every { eventService.getEvent(999L) } throws CustomException(ErrorCode.EVENT_NOT_FOUND)

            // when & then
            mockMvc.get("/api/events/999").andExpect {
                status { isNotFound() }
                jsonPath("$.code") { value("EVENT_NOT_FOUND") }
                jsonPath("$.status") { value(404) }
            }
        }
    }
}
