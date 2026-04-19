package com.taraethreads.tarae.admin.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class AdminInquiryReplyRequest(
    @field:NotBlank
    @field:Size(max = 10000)
    val replyBody: String = "",
)
