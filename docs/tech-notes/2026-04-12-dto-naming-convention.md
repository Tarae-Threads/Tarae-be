# DTO 네이밍 컨벤션 — ~Request/~Response vs ~Dto
- 날짜: 2026-04-12

## 상황

`CategoryInfo`, `TagInfo`, `BrandInfo` 같은 중첩 DTO 클래스명이 `Category`, `Tag` 엔티티와 너무 비슷해서
코드 읽을 때 "이게 DTO야, 엔티티야?" 바로 구분이 안 되는 문제 제기.

## 결정

| suffix | 용도 | 예시 |
|--------|------|------|
| `~Request` | API 요청 입력 최상위 객체 | `PlaceCreateRequest` |
| `~Response` | API 응답 출력 최상위 객체 | `PlaceDetailResponse`, `EventListResponse` |
| `~Dto` | 응답/요청 내부에 중첩되는 서브 객체 | `CategoryDto`, `PlaceEventDto` |

## 변경 내역

```
CategoryInfo  → CategoryDto
TagInfo       → TagDto
BrandInfo     → BrandDto
PlaceEventInfo → PlaceEventDto
PlaceInfo.kt  → PlaceSubDtos.kt  (파일명도 정리)
```

## 왜 ~Dto인가

- `Dto` suffix는 관습적으로 DTO임을 즉시 알 수 있음 — 팀 합류 시에도 바로 이해
- `~Info`는 엔티티(`Category`)와 이름이 너무 비슷해 혼동 유발
- `dto/` 패키지에 있다고 DTO임을 알 수 있다고 보기 어려움 — 클래스명 자체가 말해야 함

## 주의할 점

- `~Response` 내부 필드 타입으로 `~Dto`가 들어가는 건 정상
  ```kotlin
  data class PlaceDetailResponse(
      val categories: List<CategoryDto>,  // ✅
      val events: List<PlaceEventDto>,    // ✅
  )
  ```
- `~Dto`를 최상위 API 응답으로 쓰지 않는다 — 그건 `~Response`
