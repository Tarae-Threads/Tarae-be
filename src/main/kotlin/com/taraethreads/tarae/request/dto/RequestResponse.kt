package com.taraethreads.tarae.request.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "등록 요청 응답")
data class RequestResponse(
    @Schema(description = "생성된 요청 ID", example = "1") val id: Long,
)
