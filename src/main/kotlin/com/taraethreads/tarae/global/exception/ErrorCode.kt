package com.taraethreads.tarae.global.exception

enum class ErrorCode(val status: Int, val message: String) {
    // 400
    INVALID_INPUT(400, "잘못된 요청입니다"),
    INVALID_REQUEST_BODY(400, "요청 형식이 올바르지 않습니다"),
    INVALID_PASSWORD(400, "비밀번호가 일치하지 않습니다"),
    DUPLICATE_MASTER_NAME(400, "이미 존재하는 이름입니다"),

    // 404
    NOT_FOUND(404, "리소스를 찾을 수 없습니다"),
    PLACE_NOT_FOUND(404, "존재하지 않는 장소입니다"),
    EVENT_NOT_FOUND(404, "존재하지 않는 이벤트입니다"),
    SHOP_NOT_FOUND(404, "존재하지 않는 온라인샵입니다"),
    PLACE_REQUEST_NOT_FOUND(404, "존재하지 않는 장소 제보입니다"),
    EVENT_REQUEST_NOT_FOUND(404, "존재하지 않는 이벤트 제보입니다"),
    SHOP_REQUEST_NOT_FOUND(404, "존재하지 않는 온라인샵 제보입니다"),
    REVIEW_NOT_FOUND(404, "존재하지 않는 리뷰입니다"),
    CATEGORY_NOT_FOUND(404, "존재하지 않는 카테고리입니다"),
    TAG_NOT_FOUND(404, "존재하지 않는 태그입니다"),
    BRAND_NOT_FOUND(404, "존재하지 않는 브랜드입니다"),
    INQUIRY_NOT_FOUND(404, "존재하지 않는 문의입니다"),

    // 409
    REQUEST_ALREADY_PROCESSED(409, "이미 처리된 제보입니다"),
    INQUIRY_ALREADY_PROCESSED(409, "이미 처리된 문의입니다"),

    // 500
    INTERNAL_ERROR(500, "서버 오류가 발생했습니다"),
}
