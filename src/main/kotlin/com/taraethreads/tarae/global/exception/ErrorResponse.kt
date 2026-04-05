package com.taraethreads.tarae.global.exception

data class ErrorResponse(
    val code: String,
    val status: Int,
    val message: String,
) {
    companion object {
        fun of(errorCode: ErrorCode) = ErrorResponse(
            code = errorCode.name,
            status = errorCode.status,
            message = errorCode.message,
        )

        fun of(errorCode: ErrorCode, message: String) = ErrorResponse(
            code = errorCode.name,
            status = errorCode.status,
            message = message,
        )
    }
}
