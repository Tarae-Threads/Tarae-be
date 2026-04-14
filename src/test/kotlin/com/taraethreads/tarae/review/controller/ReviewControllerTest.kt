package com.taraethreads.tarae.review.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.taraethreads.tarae.global.exception.CustomException
import com.taraethreads.tarae.global.exception.ErrorCode
import com.taraethreads.tarae.global.exception.GlobalExceptionHandler
import com.taraethreads.tarae.review.domain.ReviewTargetType
import com.taraethreads.tarae.review.dto.ReviewCreateRequest
import com.taraethreads.tarae.review.dto.ReviewResponse
import com.taraethreads.tarae.review.service.ReviewService
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.time.LocalDateTime

@WebMvcTest(
    controllers = [ReviewController::class, GlobalExceptionHandler::class],
    excludeAutoConfiguration = [SecurityAutoConfiguration::class, SecurityFilterAutoConfiguration::class],
)
class ReviewControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockkBean
    lateinit var reviewService: ReviewService

    private val sampleResponse = ReviewResponse(
        id = 1L,
        nickname = "뜨개하는고양이",
        content = "좋아요!",
        createdAt = LocalDateTime.of(2026, 4, 14, 12, 0),
    )

    private val createRequest = ReviewCreateRequest(
        nickname = "뜨개하는고양이",
        email = "cat@example.com",
        password = "1234",
        content = "좋아요!",
    )

    @Nested
    inner class `장소 리뷰 작성` {

        @Test
        fun `201과 리뷰 응답을 반환한다`() {
            // given
            every { reviewService.createReview(ReviewTargetType.PLACE, 1L, any()) } returns sampleResponse

            // when & then
            mockMvc.post("/api/places/1/reviews") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(createRequest)
            }.andExpect {
                status { isCreated() }
                jsonPath("$.data.id") { value(1) }
                jsonPath("$.data.nickname") { value("뜨개하는고양이") }
                jsonPath("$.data.content") { value("좋아요!") }
            }
        }

        @Test
        fun `존재하지 않는 장소에 작성하면 404를 반환한다`() {
            // given
            every { reviewService.createReview(ReviewTargetType.PLACE, 999L, any()) } throws
                CustomException(ErrorCode.PLACE_NOT_FOUND)

            // when & then
            mockMvc.post("/api/places/999/reviews") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(createRequest)
            }.andExpect {
                status { isNotFound() }
                jsonPath("$.code") { value("PLACE_NOT_FOUND") }
            }
        }

        @Test
        fun `필수 필드 누락 시 400을 반환한다`() {
            // when & then
            mockMvc.post("/api/places/1/reviews") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"nickname": "", "email": "", "password": "", "content": ""}"""
            }.andExpect {
                status { isBadRequest() }
            }
        }
    }

    @Nested
    inner class `이벤트 리뷰 작성` {

        @Test
        fun `201과 리뷰 응답을 반환한다`() {
            // given
            every { reviewService.createReview(ReviewTargetType.EVENT, 1L, any()) } returns sampleResponse

            // when & then
            mockMvc.post("/api/events/1/reviews") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(createRequest)
            }.andExpect {
                status { isCreated() }
                jsonPath("$.data.nickname") { value("뜨개하는고양이") }
            }
        }
    }

    @Nested
    inner class `장소 리뷰 목록 조회` {

        @Test
        fun `200과 리뷰 목록을 반환한다`() {
            // given
            every { reviewService.getReviews(ReviewTargetType.PLACE, 1L) } returns listOf(sampleResponse)

            // when & then
            mockMvc.get("/api/places/1/reviews").andExpect {
                status { isOk() }
                jsonPath("$.data[0].nickname") { value("뜨개하는고양이") }
            }
        }
    }

    @Nested
    inner class `이벤트 리뷰 목록 조회` {

        @Test
        fun `200과 리뷰 목록을 반환한다`() {
            // given
            every { reviewService.getReviews(ReviewTargetType.EVENT, 1L) } returns listOf(sampleResponse)

            // when & then
            mockMvc.get("/api/events/1/reviews").andExpect {
                status { isOk() }
                jsonPath("$.data[0].nickname") { value("뜨개하는고양이") }
            }
        }
    }

    @Nested
    inner class `리뷰 삭제` {

        @Test
        fun `비밀번호가 일치하면 200을 반환한다`() {
            // given
            every { reviewService.deleteReview(1L, "1234") } just runs

            // when & then
            mockMvc.delete("/api/reviews/1") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"password": "1234"}"""
            }.andExpect {
                status { isOk() }
            }
        }

        @Test
        fun `비밀번호 불일치 시 400을 반환한다`() {
            // given
            every { reviewService.deleteReview(1L, "wrong") } throws
                CustomException(ErrorCode.INVALID_PASSWORD)

            // when & then
            mockMvc.delete("/api/reviews/1") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"password": "wrong"}"""
            }.andExpect {
                status { isBadRequest() }
                jsonPath("$.code") { value("INVALID_PASSWORD") }
            }
        }

        @Test
        fun `존재하지 않는 리뷰 삭제 시 404를 반환한다`() {
            // given
            every { reviewService.deleteReview(999L, "1234") } throws
                CustomException(ErrorCode.REVIEW_NOT_FOUND)

            // when & then
            mockMvc.delete("/api/reviews/999") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"password": "1234"}"""
            }.andExpect {
                status { isNotFound() }
                jsonPath("$.code") { value("REVIEW_NOT_FOUND") }
            }
        }
    }
}
