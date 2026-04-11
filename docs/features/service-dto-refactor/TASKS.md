# TASKS: Controller→Service DTO 변환 전면 리팩토링

- [x] T1. PlaceService DTO 반환으로 변경
  - 목적: Service → Controller 구간에서 Entity 노출 제거
  - 작업 내용:
    - `PlaceService.getPlaces()`: `List<Place>` → `List<PlaceListResponse>` 반환
    - `PlaceService.getPlace(id)`: `Place` → `PlaceDetailResponse` 반환
    - `private fun findPlaceById(id): Place` 내부 헬퍼 추출 (향후 다른 메서드 재사용)
    - `PlaceController`: `.map { PlaceListResponse.from(it) }`, `PlaceDetailResponse.from(...)` 변환 코드 제거, service 결과 바로 반환
  - 예상 변경 파일:
    - `place/service/PlaceService.kt`
    - `place/controller/PlaceController.kt`
  - 완료 기준: 컴파일 통과, `PlaceController`에서 `PlaceListResponse.from`, `PlaceDetailResponse.from` import 제거됨

- [x] T2. PlaceService 단위 테스트 작성
  - 목적: TDD — DTO 반환 검증
  - 작업 내용:
    - `PlaceServiceTest.kt` 수정
    - `getPlace(id)`: 존재하는 id → `PlaceDetailResponse` 반환 검증 (status 필드 포함)
    - `getPlace(id)`: 없는 id → `PLACE_NOT_FOUND` 예외 검증
    - `getPlaces(...)`: 필터 없이 → `List<PlaceListResponse>` 반환 검증
  - 예상 변경 파일:
    - `place/service/PlaceServiceTest.kt`
  - 완료 기준: 테스트 GREEN

- [x] T3. EventService DTO 반환으로 변경
  - 목적: Service → Controller 구간에서 Entity 노출 제거 (Event 도메인)
  - 작업 내용:
    - `EventService.getEvents()`: `List<Event>` → `List<EventListResponse>` 반환
    - `EventService.getEvent(id)`: `Event` → `EventDetailResponse` 반환
    - `private fun findEventById(id): Event` 내부 헬퍼 추출
    - `EventController`: 변환 코드 제거
  - 예상 변경 파일:
    - `event/service/EventService.kt`
    - `event/controller/EventController.kt`
  - 완료 기준: 컴파일 통과, `EventController`에서 `EventListResponse.from`, `EventDetailResponse.from` import 제거됨

- [x] T4. EventService 단위 테스트 작성
  - 목적: TDD — DTO 반환 검증
  - 작업 내용:
    - `EventServiceTest.kt` 수정
    - `getEvent(id)`: 존재하는 id → `EventDetailResponse` 반환 검증
    - `getEvent(id)`: 없는 id → `EVENT_NOT_FOUND` 예외 검증
    - `getEvents(...)`: 필터 없이 → `List<EventListResponse>` 반환 검증
  - 예상 변경 파일:
    - `event/service/EventServiceTest.kt`
  - 완료 기준: 테스트 GREEN

- [x] T5. AdminRequestService approve 반환 타입 정리
  - 목적: 컨트롤러가 반환값 미사용 → 불필요한 Entity 노출 제거
  - 작업 내용:
    - `approvePlaceRequest()`: `Place` → `Unit`
    - `approveEventRequest()`: `Event` → `Unit`
    - `AdminRequestController`: 반환값 미사용 코드 정리 (이미 redirect이므로 호출 방식 변경 없음)
  - 예상 변경 파일:
    - `admin/service/AdminRequestService.kt`
  - 완료 기준: 컴파일 통과

- [x] T6. TagService, BrandService 신규 생성
  - 목적: AdminRequestController가 Repository 직접 주입하는 패턴 제거 준비
  - 작업 내용:
    - `TagService.kt` 신규: `getAll(): List<Tag>`
    - `BrandService.kt` 신규: `getAll(): List<Brand>`
    - 각 서비스 단위 테스트 작성 (TagServiceTest, BrandServiceTest)
  - 예상 변경 파일:
    - `place/service/TagService.kt` (신규)
    - `place/service/BrandService.kt` (신규)
    - `place/service/TagServiceTest.kt` (신규)
    - `place/service/BrandServiceTest.kt` (신규)
  - 완료 기준: 컴파일 통과, 테스트 GREEN

- [x] T7. AdminRequestController Repository 직접 주입 → 서비스 경유
  - 목적: Controller가 Repository를 직접 알면 안 됨
  - 작업 내용:
    - `AdminRequestController` 생성자에서 `categoryRepository`, `tagRepository`, `brandRepository` 제거
    - `CategoryService`, `TagService`, `BrandService` 주입
    - `placeDetail()` 내 `categoryRepository.findAll()` → `categoryService.getCategories()` 등으로 교체
    - `AdminRequestControllerTest` mock 수정 (repository → service)
  - 예상 변경 파일:
    - `admin/controller/AdminRequestController.kt`
    - `admin/controller/AdminRequestControllerTest.kt`
  - 완료 기준: 컴파일 통과, `AdminRequestController`에서 `Repository` import 0건

- [x] T8. 전체 테스트 통과 확인
  - 목적: 리팩토링 후 회귀 없음 확인
  - 작업 내용:
    - `./gradlew test` 전체 GREEN 확인
  - 완료 기준: `./gradlew test` BUILD SUCCESSFUL
