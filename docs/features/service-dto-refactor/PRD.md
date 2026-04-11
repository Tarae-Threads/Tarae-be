# PRD: Controller→Service DTO 변환 전면 리팩토링
- 작성일: 2026-04-11
- 완료일: 2026-04-11
- 상태: Done

## 배경 & 목적

현재 PlaceService, EventService는 Entity를 그대로 반환하고,
Controller에서 `.from()` 정적 메서드로 DTO 변환을 수행하고 있음.

이 구조의 문제:
- Controller가 비즈니스 변환 로직을 알고 있음 (책임 혼재)
- 나중에 admin API, app API가 분리될 때 각 컨트롤러마다 변환 코드 중복
- 관리자 화면에서 PlaceService를 써야 할 때 Entity를 받아 다시 DTO 변환해야 함
- 단위 테스트 작성 시 Service가 Entity 반환하면 DTO 검증이 Controller 테스트에서만 가능

목표: **Controller → Service 구간에서 무조건 DTO**, Controller는 라우팅 + ApiResponse 래핑만.

## 요구사항

### 기능 요구사항
- [ ] PlaceService: Entity 반환 → DTO 반환
  - `getPlaces(...)`: `List<Place>` → `List<PlaceListResponse>`
  - `getPlace(id)`: `Place` → `PlaceDetailResponse`
- [ ] EventService: Entity 반환 → DTO 반환
  - `getEvents(...)`: `List<Event>` → `List<EventListResponse>`
  - `getEvent(id)`: `Event` → `EventDetailResponse`
- [ ] AdminRequestService: approve 반환값 정리
  - `approvePlaceRequest()`: `Place` → `Unit` (컨트롤러가 반환값 미사용)
  - `approveEventRequest()`: `Event` → `Unit`
- [ ] AdminRequestController: Repository 직접 주입 제거
  - `categoryRepository`, `tagRepository`, `brandRepository` 직접 주입 → 서비스 경유
  - `CategoryService.getAll()` 활용 (기존 존재)
  - `TagService`, `BrandService` 신규 생성

### 비기능 요구사항
- 외부 API 응답 변경 없음 (JSON 구조 동일)
- 기존 테스트 전체 GREEN 유지
- Entity를 직접 노출하는 코드 0건

## 가정 & 미확정 사항
- 가정: Admin Thymeleaf SSR 컨트롤러에서 PlaceRequest/EventRequest Entity를 model에 전달하는 것은 SSR 패턴상 허용
- 가정: `private fun findPlaceById(id)`, `private fun findEventById(id)` 내부 헬퍼로 두면 Service 내 재사용 가능

## 범위 외
- PlaceRequest, EventRequest 조회 DTO 변환 (admin SSR 패턴으로 entity 직접 사용 유지)
- RequestService DTO 변환
- 새 API 추가
