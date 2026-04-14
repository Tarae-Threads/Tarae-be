package com.taraethreads.tarae.review.service

import com.taraethreads.tarae.event.repository.EventRepository
import com.taraethreads.tarae.global.exception.CustomException
import com.taraethreads.tarae.global.exception.ErrorCode
import com.taraethreads.tarae.place.repository.PlaceRepository
import com.taraethreads.tarae.review.domain.Review
import com.taraethreads.tarae.review.domain.ReviewTargetType
import com.taraethreads.tarae.review.dto.ReviewCreateRequest
import com.taraethreads.tarae.review.dto.ReviewResponse
import com.taraethreads.tarae.review.repository.ReviewRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.util.ReflectionTestUtils
import java.time.LocalDateTime
import java.util.Optional

class ReviewServiceTest {

    private val reviewRepository: ReviewRepository = mockk()
    private val placeRepository: PlaceRepository = mockk()
    private val eventRepository: EventRepository = mockk()
    private val passwordEncoder = BCryptPasswordEncoder()
    private val reviewService = ReviewService(
        reviewRepository, placeRepository, eventRepository, passwordEncoder,
    )

    private fun request(
        nickname: String = "뜨개하는고양이",
        email: String = "cat@example.com",
        password: String = "1234",
        content: String = "좋아요!",
    ) = ReviewCreateRequest(nickname = nickname, email = email, password = password, content = content)

    private fun review(
        id: Long = 1L,
        targetType: ReviewTargetType = ReviewTargetType.PLACE,
        targetId: Long = 1L,
        password: String = "hashed",
    ): Review {
        val review = Review(
            targetType = targetType,
            targetId = targetId,
            nickname = "뜨개하는고양이",
            email = "cat@example.com",
            password = password,
            content = "좋아요!",
        )
        ReflectionTestUtils.setField(review, "createdAt", LocalDateTime.of(2026, 4, 14, 12, 0))
        ReflectionTestUtils.setField(review, "updatedAt", LocalDateTime.of(2026, 4, 14, 12, 0))
        return review
    }

    @Nested
    inner class `리뷰 작성` {

        @Test
        fun `장소 리뷰를 작성하면 비밀번호가 해시되어 저장된다`() {
            // given
            every { placeRepository.existsById(1L) } returns true
            val saved = slot<Review>()
            every { reviewRepository.save(capture(saved)) } answers {
                ReflectionTestUtils.setField(saved.captured, "createdAt", LocalDateTime.of(2026, 4, 14, 12, 0))
                ReflectionTestUtils.setField(saved.captured, "updatedAt", LocalDateTime.of(2026, 4, 14, 12, 0))
                saved.captured
            }

            // when
            val result: ReviewResponse = reviewService.createReview(ReviewTargetType.PLACE, 1L, request())

            // then
            assertThat(result.nickname).isEqualTo("뜨개하는고양이")
            assertThat(passwordEncoder.matches("1234", saved.captured.password)).isTrue()
            verify { reviewRepository.save(any()) }
        }

        @Test
        fun `이벤트 리뷰를 작성할 수 있다`() {
            // given
            every { eventRepository.existsById(1L) } returns true
            every { reviewRepository.save(any()) } answers {
                val r = firstArg<Review>()
                ReflectionTestUtils.setField(r, "createdAt", LocalDateTime.of(2026, 4, 14, 12, 0))
                ReflectionTestUtils.setField(r, "updatedAt", LocalDateTime.of(2026, 4, 14, 12, 0))
                r
            }

            // when
            val result = reviewService.createReview(ReviewTargetType.EVENT, 1L, request())

            // then
            assertThat(result.nickname).isEqualTo("뜨개하는고양이")
        }

        @Test
        fun `존재하지 않는 장소에 리뷰를 작성하면 PLACE_NOT_FOUND 예외가 발생한다`() {
            // given
            every { placeRepository.existsById(999L) } returns false

            // when & then
            assertThatThrownBy { reviewService.createReview(ReviewTargetType.PLACE, 999L, request()) }
                .isInstanceOf(CustomException::class.java)
                .matches { (it as CustomException).errorCode == ErrorCode.PLACE_NOT_FOUND }
        }

        @Test
        fun `존재하지 않는 이벤트에 리뷰를 작성하면 EVENT_NOT_FOUND 예외가 발생한다`() {
            // given
            every { eventRepository.existsById(999L) } returns false

            // when & then
            assertThatThrownBy { reviewService.createReview(ReviewTargetType.EVENT, 999L, request()) }
                .isInstanceOf(CustomException::class.java)
                .matches { (it as CustomException).errorCode == ErrorCode.EVENT_NOT_FOUND }
        }
    }

    @Nested
    inner class `리뷰 목록 조회` {

        @Test
        fun `targetType과 targetId로 리뷰 목록을 반환한다`() {
            // given
            every {
                reviewRepository.findByTargetTypeAndTargetIdOrderByCreatedAtDesc(ReviewTargetType.PLACE, 1L)
            } returns listOf(review())

            // when
            val result = reviewService.getReviews(ReviewTargetType.PLACE, 1L)

            // then
            assertThat(result).hasSize(1)
            assertThat(result[0].nickname).isEqualTo("뜨개하는고양이")
        }
    }

    @Nested
    inner class `리뷰 삭제` {

        @Test
        fun `비밀번호가 일치하면 리뷰를 삭제한다`() {
            // given
            val hashed = passwordEncoder.encode("1234")
            val review = review(password = hashed)
            every { reviewRepository.findById(1L) } returns Optional.of(review)
            every { reviewRepository.delete(review) } returns Unit

            // when
            reviewService.deleteReview(1L, "1234")

            // then
            verify { reviewRepository.delete(review) }
        }

        @Test
        fun `비밀번호가 일치하지 않으면 INVALID_PASSWORD 예외가 발생한다`() {
            // given
            val hashed = passwordEncoder.encode("1234")
            every { reviewRepository.findById(1L) } returns Optional.of(review(password = hashed))

            // when & then
            assertThatThrownBy { reviewService.deleteReview(1L, "wrong") }
                .isInstanceOf(CustomException::class.java)
                .matches { (it as CustomException).errorCode == ErrorCode.INVALID_PASSWORD }
        }

        @Test
        fun `존재하지 않는 리뷰를 삭제하면 REVIEW_NOT_FOUND 예외가 발생한다`() {
            // given
            every { reviewRepository.findById(999L) } returns Optional.empty()

            // when & then
            assertThatThrownBy { reviewService.deleteReview(999L, "1234") }
                .isInstanceOf(CustomException::class.java)
                .matches { (it as CustomException).errorCode == ErrorCode.REVIEW_NOT_FOUND }
        }
    }
}
