package com.taraethreads.tarae.inquiry.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.taraethreads.tarae.global.exception.GlobalExceptionHandler
import com.taraethreads.tarae.inquiry.dto.InquiryCreateResponse
import com.taraethreads.tarae.inquiry.service.InquiryService
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
    controllers = [InquiryController::class, GlobalExceptionHandler::class],
    excludeAutoConfiguration = [SecurityAutoConfiguration::class, SecurityFilterAutoConfiguration::class],
)
class InquiryControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockkBean
    lateinit var inquiryService: InquiryService

    @Nested
    inner class `POST 문의 제출` {

        @Test
        fun `201과 생성된 id를 반환한다`() {
            // given
            every { inquiryService.create(any()) } returns InquiryCreateResponse(id = 1L)
            val body = objectMapper.writeValueAsString(
                mapOf(
                    "title" to "배송 문의",
                    "body" to "언제 도착하나요?",
                    "email" to "user@example.com",
                )
            )

            // when & then
            mockMvc.post("/api/inquiries") {
                contentType = MediaType.APPLICATION_JSON
                content = body
            }.andExpect {
                status { isCreated() }
                jsonPath("$.data.id") { value(1) }
            }
        }

        @Test
        fun `title 누락 시 400을 반환한다`() {
            // given
            val body = objectMapper.writeValueAsString(
                mapOf("body" to "내용", "email" to "user@example.com")
            )

            // when & then
            mockMvc.post("/api/inquiries") {
                contentType = MediaType.APPLICATION_JSON
                content = body
            }.andExpect {
                status { isBadRequest() }
            }
        }

        @Test
        fun `유효하지 않은 이메일 형식이면 400을 반환한다`() {
            // given
            val body = objectMapper.writeValueAsString(
                mapOf("title" to "제목", "body" to "내용", "email" to "not-an-email")
            )

            // when & then
            mockMvc.post("/api/inquiries") {
                contentType = MediaType.APPLICATION_JSON
                content = body
            }.andExpect {
                status { isBadRequest() }
            }
        }
    }
}
