package com.taraethreads.tarae.request.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.taraethreads.tarae.event.domain.EventType
import com.taraethreads.tarae.global.exception.GlobalExceptionHandler
import com.taraethreads.tarae.request.dto.RequestResponse
import com.taraethreads.tarae.request.service.RequestService
import io.mockk.every
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@WebMvcTest(
    controllers = [RequestController::class, GlobalExceptionHandler::class],
    excludeAutoConfiguration = [SecurityAutoConfiguration::class, SecurityFilterAutoConfiguration::class],
)
class RequestControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockkBean
    lateinit var requestService: RequestService

    @Nested
    inner class `POST 장소 등록 요청` {

        @Test
        fun `201과 생성된 id를 반환한다`() {
            // given
            every { requestService.requestPlace(any()) } returns RequestResponse(id = 1L)
            val body = objectMapper.writeValueAsString(
                mapOf("requestType" to "NEW", "name" to "실과 바늘", "address" to "서울 성동구")
            )

            // when & then
            mockMvc.post("/api/requests/places") {
                contentType = MediaType.APPLICATION_JSON
                content = body
            }.andExpect {
                status { isCreated() }
                jsonPath("$.data.id") { value(1) }
            }
        }

        @Test
        fun `requestType 누락 시 400을 반환한다`() {
            // given
            val body = objectMapper.writeValueAsString(mapOf("name" to "실과 바늘"))

            // when & then
            mockMvc.post("/api/requests/places") {
                contentType = MediaType.APPLICATION_JSON
                content = body
            }.andExpect {
                status { isBadRequest() }
            }
        }

        @Test
        fun `이메일 형식이 잘못된 경우 400을 반환한다`() {
            // given
            val body = objectMapper.writeValueAsString(
                mapOf("requestType" to "NEW", "email" to "not-an-email")
            )

            // when & then
            mockMvc.post("/api/requests/places") {
                contentType = MediaType.APPLICATION_JSON
                content = body
            }.andExpect {
                status { isBadRequest() }
            }
        }
    }

    @Nested
    inner class `POST 이벤트 등록 요청` {

        @Test
        fun `201과 생성된 id를 반환한다`() {
            // given
            every { requestService.requestEvent(any()) } returns RequestResponse(id = 2L)
            val body = objectMapper.writeValueAsString(
                mapOf("title" to "뜨개 팝업", "eventType" to EventType.EVENT_POPUP.name, "startDate" to "2026-05-01")
            )

            // when & then
            mockMvc.post("/api/requests/events") {
                contentType = MediaType.APPLICATION_JSON
                content = body
            }.andExpect {
                status { isCreated() }
                jsonPath("$.data.id") { value(2) }
            }
        }

        @Test
        fun `title 누락 시 400을 반환한다`() {
            // given
            val body = objectMapper.writeValueAsString(
                mapOf("eventType" to "SALE", "startDate" to "2026-05-01")
            )

            // when & then
            mockMvc.post("/api/requests/events") {
                contentType = MediaType.APPLICATION_JSON
                content = body
            }.andExpect {
                status { isBadRequest() }
            }
        }
    }
}
