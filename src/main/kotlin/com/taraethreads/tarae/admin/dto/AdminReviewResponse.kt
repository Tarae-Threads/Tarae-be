package com.taraethreads.tarae.admin.dto

import com.taraethreads.tarae.review.domain.Review
import java.time.LocalDateTime

data class AdminReviewResponse(
    val id: Long,
    val targetName: String,
    val nickname: String,
    val contentPreview: String,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(review: Review, targetName: String) = AdminReviewResponse(
            id = review.id,
            targetName = targetName,
            nickname = review.nickname,
            contentPreview = if (review.content.length > 50) review.content.take(50) + "…" else review.content,
            createdAt = review.createdAt,
        )
    }
}
