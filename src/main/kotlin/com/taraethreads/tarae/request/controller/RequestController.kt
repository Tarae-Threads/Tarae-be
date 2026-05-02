package com.taraethreads.tarae.request.controller

import com.taraethreads.tarae.global.common.ApiResponse
import com.taraethreads.tarae.request.dto.EventRequestInput
import com.taraethreads.tarae.request.dto.PlaceRequestInput
import com.taraethreads.tarae.request.dto.RequestResponse
import com.taraethreads.tarae.request.dto.ShopRequestInput
import com.taraethreads.tarae.request.service.RequestService
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

@Tag(name = "등록 요청", description = "장소/이벤트/온라인샵 등록 요청 API")
@RestController
@RequestMapping("/api/requests")
class RequestController(
    private val requestService: RequestService,
) {

    @Operation(summary = "장소 등록 요청")
    @ApiResponses(
        SwaggerApiResponse(responseCode = "201", description = "요청 등록 성공"),
        SwaggerApiResponse(responseCode = "400", description = "유효하지 않은 요청 값"),
    )
    @PostMapping("/places")
    fun requestPlace(
        @Valid @RequestBody input: PlaceRequestInput,
    ): ResponseEntity<ApiResponse<RequestResponse>> =
        ApiResponse.created(requestService.requestPlace(input))

    @Operation(summary = "이벤트 등록 요청")
    @ApiResponses(
        SwaggerApiResponse(responseCode = "201", description = "요청 등록 성공"),
        SwaggerApiResponse(responseCode = "400", description = "유효하지 않은 요청 값"),
    )
    @PostMapping("/events")
    fun requestEvent(
        @Valid @RequestBody input: EventRequestInput,
    ): ResponseEntity<ApiResponse<RequestResponse>> =
        ApiResponse.created(requestService.requestEvent(input))

    @Operation(summary = "온라인샵 등록 요청")
    @ApiResponses(
        SwaggerApiResponse(responseCode = "201", description = "요청 등록 성공"),
        SwaggerApiResponse(responseCode = "400", description = "유효하지 않은 요청 값"),
    )
    @PostMapping("/shops")
    fun requestShop(
        @Valid @RequestBody input: ShopRequestInput,
    ): ResponseEntity<ApiResponse<RequestResponse>> =
        ApiResponse.created(requestService.requestShop(input))
}
