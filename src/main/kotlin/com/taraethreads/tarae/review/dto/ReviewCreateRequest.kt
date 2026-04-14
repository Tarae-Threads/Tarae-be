package com.taraethreads.tarae.review.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

@Schema(description = "리뷰 작성 요청")
data class ReviewCreateRequest(
    @field:NotBlank(message = "닉네임을 입력해주세요")
    @field:Size(max = 50, message = "닉네임은 50자 이하여야 합니다")
    @Schema(description = "닉네임", example = "뜨개하는고양이")
    val nickname: String,

    @field:NotBlank(message = "이메일을 입력해주세요")
    @field:Email(message = "올바른 이메일 형식이 아닙니다")
    @field:Size(max = 100, message = "이메일은 100자 이하여야 합니다")
    @Schema(description = "이메일", example = "cat@example.com")
    val email: String,

    @field:NotBlank(message = "비밀번호를 입력해주세요")
    @field:Size(min = 4, max = 20, message = "비밀번호는 4~20자여야 합니다")
    @field:Pattern(
        regexp = "^[a-zA-Z0-9!@#\$%^&*()_+\\-=]+$",
        message = "비밀번호는 영문, 숫자, 특수문자(!@#\$%^&*()_+-=)만 사용 가능합니다",
    )
    @Schema(description = "비밀번호 (삭제 시 사용, 4~20자)", example = "1234")
    val password: String,

    @field:NotBlank(message = "리뷰 내용을 입력해주세요")
    @Schema(description = "리뷰 내용", example = "실 종류가 다양하고 사장님이 친절해요!")
    val content: String,
)
