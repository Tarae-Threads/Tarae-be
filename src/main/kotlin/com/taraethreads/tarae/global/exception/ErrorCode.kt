package com.taraethreads.tarae.global.exception

enum class ErrorCode(val status: Int, val message: String) {
    // 400
    INVALID_INPUT(400, "잘못된 요청입니다"),
    INVALID_REQUEST_BODY(400, "요청 형식이 올바르지 않습니다"),

    // 404
    NOT_FOUND(404, "리소스를 찾을 수 없습니다"),

    // 500
    INTERNAL_ERROR(500, "서버 오류가 발생했습니다"),
}
