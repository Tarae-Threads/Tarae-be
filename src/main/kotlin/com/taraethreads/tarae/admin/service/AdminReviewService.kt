package com.taraethreads.tarae.admin.service

import com.taraethreads.tarae.admin.dto.AdminReviewResponse
import com.taraethreads.tarae.event.repository.EventRepository
import com.taraethreads.tarae.global.exception.CustomException
import com.taraethreads.tarae.global.exception.ErrorCode
import com.taraethreads.tarae.place.repository.PlaceRepository
import com.taraethreads.tarae.shop.repository.ShopRepository
import com.taraethreads.tarae.review.domain.ReviewTargetType
import com.taraethreads.tarae.review.repository.ReviewRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AdminReviewService(
    private val reviewRepository: ReviewRepository,
    private val placeRepository: PlaceRepository,
    private val eventRepository: EventRepository,
    private val shopRepository: ShopRepository,
) {

    fun getReviews(targetType: ReviewTargetType): List<AdminReviewResponse> {
        val reviews = reviewRepository.findByTargetTypeOrderByCreatedAtDesc(targetType)
        val targetNames = resolveTargetNames(targetType, reviews.map { it.targetId }.distinct())

        return reviews.map { review ->
            AdminReviewResponse.from(review, targetNames[review.targetId] ?: "(삭제됨)")
        }
    }

    fun getReviewCount(): Long = reviewRepository.count()

    @Transactional
    fun deleteReview(reviewId: Long) {
        val review = reviewRepository.findById(reviewId)
            .orElseThrow { CustomException(ErrorCode.REVIEW_NOT_FOUND) }
        reviewRepository.delete(review)
    }

    private fun resolveTargetNames(targetType: ReviewTargetType, targetIds: List<Long>): Map<Long, String> =
        when (targetType) {
            ReviewTargetType.PLACE -> placeRepository.findAllById(targetIds).associate { it.id to it.name }
            ReviewTargetType.EVENT -> eventRepository.findAllById(targetIds).associate { it.id to it.title }
            ReviewTargetType.SHOP -> shopRepository.findAllById(targetIds).associate { it.id to it.name }
        }
}
