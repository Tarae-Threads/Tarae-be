package com.taraethreads.tarae.shop.controller

import com.taraethreads.tarae.global.common.ApiResponse
import com.taraethreads.tarae.shop.dto.ShopDetailResponse
import com.taraethreads.tarae.shop.dto.ShopListResponse
import com.taraethreads.tarae.shop.service.ShopService
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

@Tag(name = "온라인샵", description = "뜨개 관련 온라인샵 API")
@RestController
@RequestMapping("/api/shops")
class ShopController(
    private val shopService: ShopService,
) {

    @Operation(summary = "온라인샵 목록 조회")
    @ApiResponses(
        SwaggerApiResponse(responseCode = "200", description = "조회 성공"),
    )
    @GetMapping
    fun getShops(
        @Parameter(description = "검색어 (온라인샵명, 태그, 브랜드 통합 검색)") @RequestParam keyword: String?,
        @Parameter(description = "카테고리 ID") @RequestParam categoryId: Long?,
        @Parameter(description = "태그 ID") @RequestParam tagId: Long?,
    ): ResponseEntity<ApiResponse<List<ShopListResponse>>> =
        ApiResponse.ok(shopService.getShops(categoryId, tagId, keyword))

    @Operation(summary = "온라인샵 상세 조회")
    @ApiResponses(
        SwaggerApiResponse(responseCode = "200", description = "조회 성공"),
        SwaggerApiResponse(responseCode = "404", description = "존재하지 않는 온라인샵"),
    )
    @GetMapping("/{id}")
    fun getShop(
        @Parameter(description = "온라인샵 ID") @PathVariable id: Long,
    ): ResponseEntity<ApiResponse<ShopDetailResponse>> =
        ApiResponse.ok(shopService.getShop(id))
}
