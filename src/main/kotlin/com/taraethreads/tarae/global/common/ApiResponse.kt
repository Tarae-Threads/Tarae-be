package com.taraethreads.tarae.global.common

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

data class ApiResponse<T>(val data: T) {
    companion object {
        fun <T> ok(data: T): ResponseEntity<ApiResponse<T>> =
            ResponseEntity.ok(ApiResponse(data))

        fun <T> created(data: T): ResponseEntity<ApiResponse<T>> =
            ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse(data))
    }
}
