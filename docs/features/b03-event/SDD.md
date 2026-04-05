# SDD: B-03 Event 도메인
- 작성일: 2026-04-05
- 완료일: 2026-04-05
- 상태: Done

## 아키텍처

```
com.taraethreads.tarae.
└── event/
    ├── controller/
    │   └── EventController.kt
    ├── service/
    │   └── EventService.kt
    ├── repository/
    │   ├── EventRepository.kt
    │   ├── EventRepositoryCustom.kt
    │   └── EventRepositoryImpl.kt
    ├── domain/
    │   ├── Event.kt
    │   └── EventType.kt        (ENUM)
    └── dto/
        ├── EventListResponse.kt
        └── EventDetailResponse.kt
```

## 도메인 모델

### EventType
```kotlin
enum class EventType { TESTER_RECRUIT, SALE, EVENT_POPUP }
```

### Event
```kotlin
@Entity
class Event(
    val title: String,
    val description: String?,
    val eventType: EventType,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val place: Place?,           // nullable FK
    val locationText: String?,
    val lat: BigDecimal?,
    val lng: BigDecimal?,
    val links: String?,          // JSON 문자열
    val active: Boolean = true,
) : BaseEntity()
```

## DB

Flyway: `V2__create_event_tables.sql`

## API 설계

### 목록 조회

```
GET /api/events
```

| 파라미터 | 타입 | 설명 |
|----------|------|------|
| eventType | EventType | nullable, 타입 필터 |
| active | Boolean | nullable, 활성 필터 |

**Response** `200 OK` — `List<EventListResponse>`

### 상세 조회

```
GET /api/events/{id}
```

**Response** `200 OK` — `EventDetailResponse`

| 상황 | ErrorCode | HTTP |
|------|-----------|------|
| 존재하지 않는 id | EVENT_NOT_FOUND | 404 |

## 테스트 전략

- **EventRepository**: `@DataJpaTest` — eventType 필터, active 필터
- **EventService**: MockK 단위테스트 — 필터 전달, EVENT_NOT_FOUND 예외
- **EventController**: `@WebMvcTest` — 200/404 응답 검증
