package com.taraethreads.tarae.global.exception

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrorResponse(
    val code: String,
    val status: Int,
    val message: String,
    val detail: String? = null,
) {
    companion object {
        fun of(errorCode: ErrorCode, message: String = errorCode.message, detail: String? = null) = ErrorResponse(
            code = errorCode.name,
            status = errorCode.status,
            message = message,
            detail = detail,
        )
    }
}
