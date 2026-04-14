package com.taraethreads.tarae.review.repository

import com.taraethreads.tarae.global.config.JpaConfig
import com.taraethreads.tarae.review.domain.Review
import com.taraethreads.tarae.review.domain.ReviewTargetType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaConfig::class)
class ReviewRepositoryTest {

    @Autowired
    lateinit var reviewRepository: ReviewRepository

    private fun createReview(
        targetType: ReviewTargetType = ReviewTargetType.PLACE,
        targetId: Long = 1L,
        nickname: String = "뜨개하는고양이",
        content: String = "좋아요!",
    ): Review = reviewRepository.save(
        Review(
            targetType = targetType,
            targetId = targetId,
            nickname = nickname,
            email = "test@example.com",
            password = "hashed",
            content = content,
        )
    )

    @Nested
    inner class `대상별 리뷰 조회` {

        @Test
        fun `targetType과 targetId로 리뷰를 조회한다`() {
            // given
            createReview(targetType = ReviewTargetType.PLACE, targetId = 1L)
            createReview(targetType = ReviewTargetType.PLACE, targetId = 2L)
            createReview(targetType = ReviewTargetType.EVENT, targetId = 1L)

            // when
            val result = reviewRepository.findByTargetTypeAndTargetIdOrderByCreatedAtDesc(
                ReviewTargetType.PLACE, 1L,
            )

            // then
            assertThat(result).hasSize(1)
            assertThat(result[0].targetType).isEqualTo(ReviewTargetType.PLACE)
            assertThat(result[0].targetId).isEqualTo(1L)
        }

        @Test
        fun `최신순으로 정렬된다`() {
            // given
            val first = createReview(content = "첫 번째")
            val second = createReview(content = "두 번째")

            // when
            val result = reviewRepository.findByTargetTypeAndTargetIdOrderByCreatedAtDesc(
                ReviewTargetType.PLACE, 1L,
            )

            // then
            assertThat(result).hasSize(2)
            assertThat(result[0].content).isEqualTo("두 번째")
            assertThat(result[1].content).isEqualTo("첫 번째")
        }

        @Test
        fun `리뷰가 없으면 빈 리스트를 반환한다`() {
            // when
            val result = reviewRepository.findByTargetTypeAndTargetIdOrderByCreatedAtDesc(
                ReviewTargetType.PLACE, 999L,
            )

            // then
            assertThat(result).isEmpty()
        }
    }

    @Nested
    inner class `리뷰 삭제` {

        @Test
        fun `리뷰를 삭제할 수 있다`() {
            // given
            val review = createReview()

            // when
            reviewRepository.delete(review)

            // then
            assertThat(reviewRepository.findById(review.id)).isEmpty()
        }
    }
}
