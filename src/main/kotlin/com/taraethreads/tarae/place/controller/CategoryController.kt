package com.taraethreads.tarae.place.controller

import com.taraethreads.tarae.global.common.ApiResponse
import com.taraethreads.tarae.place.dto.CategoryResponse
import com.taraethreads.tarae.place.service.CategoryService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "카테고리", description = "카테고리 API")
@RestController
@RequestMapping("/api/categories")
class CategoryController(
    private val categoryService: CategoryService,
) {

    @Operation(summary = "카테고리 목록 조회")
    @ApiResponses(
        SwaggerApiResponse(responseCode = "200", description = "조회 성공"),
    )
    @GetMapping
    fun getCategories(): ResponseEntity<ApiResponse<List<CategoryResponse>>> =
        ApiResponse.ok(
            categoryService.getCategories().map { CategoryResponse.from(it) }
        )
}
