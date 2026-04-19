package com.taraethreads.tarae.inquiry.controller

import com.taraethreads.tarae.global.common.ApiResponse
import com.taraethreads.tarae.inquiry.dto.InquiryCreateRequest
import com.taraethreads.tarae.inquiry.dto.InquiryCreateResponse
import com.taraethreads.tarae.inquiry.service.InquiryService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "문의", description = "문의하기 API")
@RestController
@RequestMapping("/api/inquiries")
class InquiryController(
    private val inquiryService: InquiryService,
) {

    @Operation(summary = "문의 제출")
    @ApiResponses(
        SwaggerApiResponse(responseCode = "201", description = "문의 제출 성공"),
        SwaggerApiResponse(responseCode = "400", description = "유효하지 않은 요청 값"),
    )
    @PostMapping
    fun create(
        @Valid @RequestBody request: InquiryCreateRequest,
    ): ResponseEntity<ApiResponse<InquiryCreateResponse>> =
        ApiResponse.created(inquiryService.create(request))
}
