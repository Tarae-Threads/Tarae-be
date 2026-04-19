package com.taraethreads.tarae.inquiry.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "문의 제출 요청")
data class InquiryCreateRequest(
    @Schema(description = "문의 제목", example = "배송 관련 문의")
    @field:NotBlank
    @field:Size(max = 255)
    val title: String,

    @Schema(description = "문의 내용")
    @field:NotBlank
    val body: String,

    @Schema(description = "답변받을 이메일", example = "user@example.com")
    @field:NotBlank
    @field:Email
    @field:Size(max = 100)
    val email: String,
)
