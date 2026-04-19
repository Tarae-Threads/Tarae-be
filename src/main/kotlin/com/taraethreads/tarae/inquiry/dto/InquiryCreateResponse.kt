package com.taraethreads.tarae.inquiry.dto

import com.taraethreads.tarae.inquiry.domain.Inquiry
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "문의 제출 응답")
data class InquiryCreateResponse(
    @Schema(description = "문의 ID", example = "1")
    val id: Long,
) {
    companion object {
        fun from(inquiry: Inquiry) = InquiryCreateResponse(id = inquiry.id)
    }
}
