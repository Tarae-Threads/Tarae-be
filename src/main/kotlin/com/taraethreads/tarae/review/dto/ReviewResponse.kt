package com.taraethreads.tarae.review.dto

import com.taraethreads.tarae.review.domain.Review
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "리뷰 응답")
data class ReviewResponse(
    @Schema(description = "리뷰 ID", example = "1") val id: Long,
    @Schema(description = "닉네임", example = "뜨개하는고양이") val nickname: String,
    @Schema(description = "리뷰 내용") val content: String,
    @Schema(description = "작성일시") val createdAt: LocalDateTime,
) {
    companion object {
        fun from(review: Review) = ReviewResponse(
            id = review.id,
            nickname = review.nickname,
            content = review.content,
            createdAt = review.createdAt,
        )
    }
}
