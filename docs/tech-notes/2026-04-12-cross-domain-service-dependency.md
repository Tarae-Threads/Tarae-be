# 크로스 도메인 의존 — 서비스는 다른 도메인의 Repository를 직접 쓰지 않는다
- 날짜: 2026-04-12

## 상황

`PlaceService`에서 장소 상세 조회 시 해당 장소의 활성 이벤트도 함께 반환해야 했다.
Event는 Place와 별도 도메인이고, `Event → Place` 단방향 `@ManyToOne` 관계라 Place 쪽에 역참조가 없어서
`EventRepository`에서 placeId로 직접 조회하는 방식을 택했는데, 처음엔 `PlaceService`가 `EventRepository`를 직접 주입받았다.

## 문제

```kotlin
// ❌ 처음 구현
@Service
class PlaceService(
    private val placeRepository: PlaceRepository,
    private val eventRepository: EventRepository,  // 다른 도메인의 Repository 직접 주입
)
```

`place` 서비스 레이어가 `event` 레포지토리 레이어를 직접 참조 → 레이어 규칙(`Service → Repository`) 위반이자 도메인 경계 침범.
지금은 작아서 티가 안 나지만, 도메인이 커질수록 의존이 뒤엉킨다.

## 해결

`EventService`에 메서드를 추가하고, `PlaceService`는 `EventService`를 경유한다.

```kotlin
// EventService
fun findActiveEventsByPlaceId(placeId: Long): List<Event> =
    eventRepository.findAllByPlaceIdAndActiveTrue(placeId)

// ✅ 수정된 PlaceService
@Service
class PlaceService(
    private val placeRepository: PlaceRepository,
    private val eventService: EventService,   // service-to-service
) {
    fun getPlace(id: Long): PlaceDetailResponse {
        val place = findPlaceById(id)
        val events = eventService.findActiveEventsByPlaceId(id).map { it.toPlaceEventDto() }
        return PlaceDetailResponse.from(place, events)
    }

    private fun Event.toPlaceEventDto() = PlaceEventDto(...)  // 변환은 PlaceService 내부에서
}
```

## 왜 중요한가

- `Service → 다른 도메인 Repository` 직접 참조는 레이어 구조를 무너뜨린다
- 테스트에서도 `EventRepository` mock 대신 `EventService` mock으로 명확해짐
- `EventService`에 메서드가 생기면 다른 서비스에서도 재사용 가능

## 원칙 정리

| 방향 | 허용 여부 |
|------|----------|
| Service → 같은 도메인 Repository | ✅ |
| Service → 다른 도메인 Service | ✅ |
| Service → 다른 도메인 Repository | ❌ |
| DTO → 다른 도메인 Entity (변환) | ❌ (변환은 Service 내부로) |
