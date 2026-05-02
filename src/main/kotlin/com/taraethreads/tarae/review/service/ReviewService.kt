package com.taraethreads.tarae.review.service

import com.taraethreads.tarae.event.repository.EventRepository
import com.taraethreads.tarae.global.exception.CustomException
import com.taraethreads.tarae.global.exception.ErrorCode
import com.taraethreads.tarae.place.repository.PlaceRepository
import com.taraethreads.tarae.shop.repository.ShopRepository
import com.taraethreads.tarae.review.domain.Review
import com.taraethreads.tarae.review.domain.ReviewTargetType
import com.taraethreads.tarae.review.dto.ReviewCreateRequest
import com.taraethreads.tarae.review.dto.ReviewResponse
import com.taraethreads.tarae.review.repository.ReviewRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ReviewService(
    private val reviewRepository: ReviewRepository,
    private val placeRepository: PlaceRepository,
    private val eventRepository: EventRepository,
    private val shopRepository: ShopRepository,
    private val passwordEncoder: PasswordEncoder,
) {

    @Transactional
    fun createReview(targetType: ReviewTargetType, targetId: Long, request: ReviewCreateRequest): ReviewResponse {
        validateTargetExists(targetType, targetId)

        val review = reviewRepository.save(
            Review(
                targetType = targetType,
                targetId = targetId,
                nickname = request.nickname,
                email = request.email,
                password = passwordEncoder.encode(request.password),
                content = request.content,
            )
        )
        return ReviewResponse.from(review)
    }

    fun getReviews(targetType: ReviewTargetType, targetId: Long): List<ReviewResponse> =
        reviewRepository.findByTargetTypeAndTargetIdOrderByCreatedAtDesc(targetType, targetId)
            .map { ReviewResponse.from(it) }

    @Transactional
    fun deleteReview(reviewId: Long, password: String) {
        val review = findReviewById(reviewId)
        if (!passwordEncoder.matches(password, review.password)) {
            throw CustomException(ErrorCode.INVALID_PASSWORD)
        }
        reviewRepository.delete(review)
    }

    private fun validateTargetExists(targetType: ReviewTargetType, targetId: Long) {
        val exists = when (targetType) {
            ReviewTargetType.PLACE -> placeRepository.existsById(targetId)
            ReviewTargetType.EVENT -> eventRepository.existsById(targetId)
            ReviewTargetType.SHOP -> shopRepository.existsById(targetId)
        }
        if (!exists) {
            throw CustomException(
                when (targetType) {
                    ReviewTargetType.PLACE -> ErrorCode.PLACE_NOT_FOUND
                    ReviewTargetType.EVENT -> ErrorCode.EVENT_NOT_FOUND
                    ReviewTargetType.SHOP -> ErrorCode.SHOP_NOT_FOUND
                }
            )
        }
    }

    private fun findReviewById(id: Long): Review =
        reviewRepository.findById(id)
            .orElseThrow { CustomException(ErrorCode.REVIEW_NOT_FOUND) }
}
