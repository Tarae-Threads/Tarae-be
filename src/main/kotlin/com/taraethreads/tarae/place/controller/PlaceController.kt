package com.taraethreads.tarae.place.controller

import com.taraethreads.tarae.global.common.ApiResponse
import com.taraethreads.tarae.place.dto.PlaceDetailResponse
import com.taraethreads.tarae.place.dto.PlaceListResponse
import com.taraethreads.tarae.place.service.PlaceService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "장소", description = "뜨개 관련 장소 API")
@RestController
@RequestMapping("/api/places")
class PlaceController(
    private val placeService: PlaceService,
) {

    @Operation(summary = "장소 목록 조회")
    @ApiResponses(
        SwaggerApiResponse(responseCode = "200", description = "조회 성공"),
    )
    @GetMapping
    fun getPlaces(
        @Parameter(description = "검색어 (장소명, 주소, 태그, 브랜드 통합 검색)") @RequestParam keyword: String?,
        @Parameter(description = "지역 필터 (예: 서울, 경기)") @RequestParam region: String?,
        @Parameter(description = "카테고리 ID") @RequestParam categoryId: Long?,
        @Parameter(description = "태그 ID") @RequestParam tagId: Long?,
    ): ResponseEntity<ApiResponse<List<PlaceListResponse>>> =
        ApiResponse.ok(
            placeService.getPlaces(region, categoryId, tagId, keyword)
                .map { PlaceListResponse.from(it) }
        )

    @Operation(summary = "장소 상세 조회")
    @ApiResponses(
        SwaggerApiResponse(responseCode = "200", description = "조회 성공"),
        SwaggerApiResponse(responseCode = "404", description = "존재하지 않는 장소"),
    )
    @GetMapping("/{id}")
    fun getPlace(
        @Parameter(description = "장소 ID") @PathVariable id: Long,
    ): ResponseEntity<ApiResponse<PlaceDetailResponse>> =
        ApiResponse.ok(PlaceDetailResponse.from(placeService.getPlace(id)))
}
