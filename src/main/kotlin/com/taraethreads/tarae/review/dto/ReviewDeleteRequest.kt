package com.taraethreads.tarae.review.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "리뷰 삭제 요청")
data class ReviewDeleteRequest(
    @field:NotBlank
    @Schema(description = "비밀번호", example = "1234")
    val password: String,
)
