package com.taraethreads.tarae.event.controller

import com.taraethreads.tarae.event.domain.EventType
import com.taraethreads.tarae.event.dto.EventDetailResponse
import com.taraethreads.tarae.event.dto.EventListResponse
import com.taraethreads.tarae.event.service.EventService
import com.taraethreads.tarae.global.common.ApiResponse
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

@Tag(name = "이벤트", description = "뜨개 관련 이벤트/팝업/세일 API")
@RestController
@RequestMapping("/api/events")
class EventController(
    private val eventService: EventService,
) {

    @Operation(summary = "이벤트 목록 조회")
    @ApiResponses(
        SwaggerApiResponse(responseCode = "200", description = "조회 성공"),
    )
    @GetMapping
    fun getEvents(
        @Parameter(description = "이벤트 타입 (TESTER_RECRUIT, SALE, EVENT_POPUP)") @RequestParam eventType: EventType?,
        @Parameter(description = "활성 여부 (기본값: true)") @RequestParam active: Boolean? = true,
    ): ResponseEntity<ApiResponse<List<EventListResponse>>> =
        ApiResponse.ok(
            eventService.getEvents(eventType, active)
                .map { EventListResponse.from(it) }
        )

    @Operation(summary = "이벤트 상세 조회")
    @ApiResponses(
        SwaggerApiResponse(responseCode = "200", description = "조회 성공"),
        SwaggerApiResponse(responseCode = "404", description = "존재하지 않는 이벤트"),
    )
    @GetMapping("/{id}")
    fun getEvent(
        @Parameter(description = "이벤트 ID") @PathVariable id: Long,
    ): ResponseEntity<ApiResponse<EventDetailResponse>> =
        ApiResponse.ok(EventDetailResponse.from(eventService.getEvent(id)))
}
