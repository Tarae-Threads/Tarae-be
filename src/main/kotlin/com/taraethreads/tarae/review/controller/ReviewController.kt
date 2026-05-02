package com.taraethreads.tarae.review.controller

import com.taraethreads.tarae.global.common.ApiResponse
import com.taraethreads.tarae.review.domain.ReviewTargetType
import com.taraethreads.tarae.review.dto.ReviewCreateRequest
import com.taraethreads.tarae.review.dto.ReviewDeleteRequest
import com.taraethreads.tarae.review.dto.ReviewResponse
import com.taraethreads.tarae.review.service.ReviewService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@Tag(name = "리뷰", description = "장소/이벤트 리뷰 API")
@RestController
class ReviewController(
    private val reviewService: ReviewService,
) {

    @Operation(summary = "장소 리뷰 작성")
    @ApiResponses(
        SwaggerApiResponse(responseCode = "201", description = "작성 성공"),
        SwaggerApiResponse(responseCode = "404", description = "존재하지 않는 장소"),
    )
    @PostMapping("/api/places/{placeId}/reviews")
    fun createPlaceReview(
        @Parameter(description = "장소 ID") @PathVariable placeId: Long,
        @Valid @RequestBody request: ReviewCreateRequest,
    ): ResponseEntity<ApiResponse<ReviewResponse>> =
        ApiResponse.created(reviewService.createReview(ReviewTargetType.PLACE, placeId, request))

    @Operation(summary = "장소 리뷰 목�� 조회")
    @ApiResponses(
        SwaggerApiResponse(responseCode = "200", description = "조회 성공"),
    )
    @GetMapping("/api/places/{placeId}/reviews")
    fun getPlaceReviews(
        @Parameter(description = "장소 ID") @PathVariable placeId: Long,
    ): ResponseEntity<ApiResponse<List<ReviewResponse>>> =
        ApiResponse.ok(reviewService.getReviews(ReviewTargetType.PLACE, placeId))

    @Operation(summary = "이벤트 리뷰 작성")
    @ApiResponses(
        SwaggerApiResponse(responseCode = "201", description = "작성 성공"),
        SwaggerApiResponse(responseCode = "404", description = "존재하지 않는 이벤트"),
    )
    @PostMapping("/api/events/{eventId}/reviews")
    fun createEventReview(
        @Parameter(description = "이벤트 ID") @PathVariable eventId: Long,
        @Valid @RequestBody request: ReviewCreateRequest,
    ): ResponseEntity<ApiResponse<ReviewResponse>> =
        ApiResponse.created(reviewService.createReview(ReviewTargetType.EVENT, eventId, request))

    @Operation(summary = "이벤트 리뷰 목록 조회")
    @ApiResponses(
        SwaggerApiResponse(responseCode = "200", description = "조회 성공"),
    )
    @GetMapping("/api/events/{eventId}/reviews")
    fun getEventReviews(
        @Parameter(description = "이벤트 ID") @PathVariable eventId: Long,
    ): ResponseEntity<ApiResponse<List<ReviewResponse>>> =
        ApiResponse.ok(reviewService.getReviews(ReviewTargetType.EVENT, eventId))

    @Operation(summary = "온라인샵 리뷰 작성")
    @ApiResponses(
        SwaggerApiResponse(responseCode = "201", description = "작성 성공"),
        SwaggerApiResponse(responseCode = "404", description = "존재하지 않는 온라인샵"),
    )
    @PostMapping("/api/shops/{shopId}/reviews")
    fun createShopReview(
        @Parameter(description = "온라인샵 ID") @PathVariable shopId: Long,
        @Valid @RequestBody request: ReviewCreateRequest,
    ): ResponseEntity<ApiResponse<ReviewResponse>> =
        ApiResponse.created(reviewService.createReview(ReviewTargetType.SHOP, shopId, request))

    @Operation(summary = "온라인샵 리뷰 목록 조회")
    @ApiResponses(
        SwaggerApiResponse(responseCode = "200", description = "조회 성공"),
    )
    @GetMapping("/api/shops/{shopId}/reviews")
    fun getShopReviews(
        @Parameter(description = "온라인샵 ID") @PathVariable shopId: Long,
    ): ResponseEntity<ApiResponse<List<ReviewResponse>>> =
        ApiResponse.ok(reviewService.getReviews(ReviewTargetType.SHOP, shopId))

    @Operation(summary = "리뷰 삭제 (비밀번호 검증)")
    @ApiResponses(
        SwaggerApiResponse(responseCode = "200", description = "삭제 성공"),
        SwaggerApiResponse(responseCode = "400", description = "비밀번호 불일치"),
        SwaggerApiResponse(responseCode = "404", description = "존재하지 ��는 리뷰"),
    )
    @DeleteMapping("/api/reviews/{reviewId}")
    fun deleteReview(
        @Parameter(description = "���뷰 ID") @PathVariable reviewId: Long,
        @Valid @RequestBody request: ReviewDeleteRequest,
    ): ResponseEntity<ApiResponse<Nothing?>> {
        reviewService.deleteReview(reviewId, request.password)
        return ApiResponse.ok(null)
    }
}
