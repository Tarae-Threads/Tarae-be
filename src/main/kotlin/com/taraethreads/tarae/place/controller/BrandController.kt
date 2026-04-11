package com.taraethreads.tarae.place.controller

import com.taraethreads.tarae.global.common.ApiResponse
import com.taraethreads.tarae.place.dto.BrandGroupResponse
import com.taraethreads.tarae.place.service.BrandService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "브랜드", description = "브랜드 API")
@RestController
@RequestMapping("/api/brands")
class BrandController(
    private val brandService: BrandService,
) {

    @Operation(summary = "타입별 브랜드 목록 조회")
    @ApiResponses(
        SwaggerApiResponse(responseCode = "200", description = "조회 성공"),
    )
    @GetMapping
    fun getBrands(): ResponseEntity<ApiResponse<List<BrandGroupResponse.BrandTypeGroup>>> =
        ApiResponse.ok(brandService.getBrandsGroupedByType())
}
