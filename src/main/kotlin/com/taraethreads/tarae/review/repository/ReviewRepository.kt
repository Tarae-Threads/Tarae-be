package com.taraethreads.tarae.review.repository

import com.taraethreads.tarae.review.domain.Review
import com.taraethreads.tarae.review.domain.ReviewTargetType
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface ReviewRepository : JpaRepository<Review, Long> {

    fun findByTargetTypeAndTargetIdOrderByCreatedAtDesc(
        targetType: ReviewTargetType,
        targetId: Long,
    ): List<Review>

    fun findByTargetTypeOrderByCreatedAtDesc(targetType: ReviewTargetType): List<Review>

    fun countByCreatedAtBetween(from: LocalDateTime, to: LocalDateTime): Long
}
