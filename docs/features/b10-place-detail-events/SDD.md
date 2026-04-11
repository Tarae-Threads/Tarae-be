# SDD: 장소 상세 조회 응답에 활성 이벤트 목록 포함

## 기술 스택
- Kotlin + Spring Boot
- Spring Data JPA
- MySQL

## 아키텍처

```
GET /api/places/{id}
  → PlaceController.getPlace()
  → PlaceService.getPlace()
      → PlaceRepository.findById()
      → EventRepository.findAllByPlaceIdAndActiveTrue()   ← 신규
  → PlaceDetailResponse (events 필드 포함)
```

## 도메인 모델

변경 없음. 기존 구조 활용:
- `Event.place: Place? (@ManyToOne LAZY)` — placeId로 조회 가능
- Place 쪽 역참조(`@OneToMany`) 추가하지 않음 — 서비스 레이어에서 별도 조회

## API 설계

### 변경 API

| Method | URL | Response | 설명 |
|--------|-----|----------|------|
| GET | `/api/places/{id}` | `PlaceDetailResponse` (events 추가) | 장소 상세 (이벤트 포함) |

### 응답 변경 사항

`PlaceDetailResponse`에 `events: List<PlaceEventInfo>` 필드 추가

```json
{
  "id": 1,
  "name": "실타래 뜨개공방",
  "...(기존 필드)",
  "events": [
    {
      "id": 10,
      "title": "봄 시즌 클래스 모집",
      "eventType": "TESTER_RECRUIT",
      "startDate": "2026-04-15",
      "endDate": "2026-05-10",
      "active": true,
      "links": "https://forms.gle/xxxx"
    }
  ]
}
```

## 신규 DTO

```kotlin
// place/dto/PlaceDetailResponse.kt 내 중첩 또는 별도 파일
data class PlaceEventInfo(
    val id: Long,
    val title: String,
    val eventType: String,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val active: Boolean,
    val links: String?,
)
```

## 신규 Repository 메서드

```kotlin
// event/repository/EventRepository.kt
fun findAllByPlaceIdAndActiveTrue(placeId: Long): List<Event>
```

## 예외 처리

| 상황 | 처리 |
|------|------|
| 장소에 이벤트 없음 | `events: []` 반환 (예외 아님) |
| 존재하지 않는 장소 ID | 기존 `PlaceNotFoundException` 동일 |

## 테스트 전략

### 단위 테스트 (Service)
- 이벤트 있는 장소 → events 리스트 포함 검증
- 이벤트 없는 장소 → events 빈 배열 검증
- `active = false` 이벤트 → 응답에서 제외 검증

### 단위 테스트 (Controller)
- 응답 JSON에 `events` 필드 존재 검증
- 이벤트 필드(`title`, `eventType`, `startDate` 등) 값 검증
