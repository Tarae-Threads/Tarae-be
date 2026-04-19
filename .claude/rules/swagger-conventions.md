---
paths:
  - "src/main/**/controller/**/*.kt"
  - "src/main/**/dto/**/*.kt"
---

# Swagger (Springdoc OpenAPI) 컨벤션

## 응답 래퍼 — ApiResponse<T>

모든 정상 응답은 `ApiResponse<T>`로 래핑한다. 에러 응답은 `ErrorResponse`를 직접 반환 (GlobalExceptionHandler가 처리).

```kotlin
// 성공 → { "data": { ... } }
// 에러 → { "code": "...", "status": 404, "message": "..." }
```

HTTP 상태별 팩토리 메서드:

```kotlin
ApiResponse.ok(data)       // 200 OK
ApiResponse.created(data)  // 201 Created
```

## Controller

`@ApiResponse` (Swagger 어노테이션)와 `ApiResponse` (우리 클래스)가 이름이 같으므로 반드시 alias 처리:

```kotlin
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse
```

```kotlin
@Tag(name = "장소", description = "뜨개 관련 장소 API")
@RestController
@RequestMapping("/api/places")
class PlaceController(private val placeService: PlaceService) {

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

    @Operation(summary = "장소 등록 요청")
    @ApiResponses(
        SwaggerApiResponse(responseCode = "201", description = "요청 등록 성공"),
        SwaggerApiResponse(responseCode = "400", description = "유효하지 않은 요청 값"),
    )
    @PostMapping
    fun createPlace(
        @Valid @RequestBody input: PlaceInput,
    ): ResponseEntity<ApiResponse<PlaceDetailResponse>> =
        ApiResponse.created(PlaceDetailResponse.from(placeService.createPlace(input)))
}
```

## DTO

```kotlin
@Schema(description = "장소 상세 응답")
data class PlaceDetailResponse(
    @Schema(description = "장소 ID", example = "1") val id: Long,
    @Schema(description = "장소명", example = "실과 바늘") val name: String,
)

data class PlaceInput(
    @Schema(description = "장소명", example = "실과 바늘", required = true)
    @field:NotBlank
    val name: String?,
)
```

## 작성 원칙

- `summary`: 동사+목적어 형식 ("장소 조회", "이벤트 등록 요청")
- `description`: 복잡한 제약조건 있을 때만, 없으면 생략
- `@Schema(example = ...)`: 실제 사용 가능한 값으로
- 에러 응답은 주요 케이스(400, 404 등)만 명시
- `@Parameter`는 path variable, query param에만 (body는 DTO의 @Schema로)
