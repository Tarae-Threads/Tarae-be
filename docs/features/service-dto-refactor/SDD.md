# SDD: Controller→Service DTO 변환 전면 리팩토링

## 아키텍처

### 변경 전
```
Controller → placeService.getPlace(id): Place → PlaceDetailResponse.from(entity)
```

### 변경 후
```
Controller → placeService.getPlace(id): PlaceDetailResponse
```

Controller는 `ApiResponse.ok(service.getXxx(...))` 형태만 유지.

## 변경 대상 메서드 시그니처

### PlaceService

```kotlin
// Before
fun getPlaces(region, categoryId, tagId, keyword): List<Place>
fun getPlace(id: Long): Place

// After
fun getPlaces(region, categoryId, tagId, keyword): List<PlaceListResponse>
fun getPlace(id: Long): PlaceDetailResponse
private fun findPlaceById(id: Long): Place  // 내부 재사용용
```

### EventService

```kotlin
// Before
fun getEvents(eventType, active): List<Event>
fun getEvent(id: Long): Event

// After
fun getEvents(eventType, active): List<EventListResponse>
fun getEvent(id: Long): EventDetailResponse
private fun findEventById(id: Long): Event  // 내부 재사용용
```

### AdminRequestService

```kotlin
// Before
fun approvePlaceRequest(id, form): Place
fun approveEventRequest(id, form): Event

// After
fun approvePlaceRequest(id, form): Unit
fun approveEventRequest(id, form): Unit
```

### 신규 서비스

```kotlin
// TagService (신규)
fun getAll(): List<Tag>

// BrandService (신규)
fun getAll(): List<Brand>
```

### AdminRequestController 변경

```kotlin
// Before
class AdminRequestController(
    private val adminRequestService: AdminRequestService,
    private val categoryRepository: CategoryRepository,  // 제거
    private val tagRepository: TagRepository,            // 제거
    private val brandRepository: BrandRepository,        // 제거
)

// After
class AdminRequestController(
    private val adminRequestService: AdminRequestService,
    private val categoryService: CategoryService,
    private val tagService: TagService,
    private val brandService: BrandService,
)
```

## 테스트 전략

- PlaceController 테스트: service mock이 DTO 반환하도록 변경
- EventController 테스트: 동일
- PlaceService 테스트 (신규): DTO 반환 검증 단위 테스트
- EventService 테스트 (신규 또는 기존 수정)
- TagService, BrandService 테스트 (신규)
