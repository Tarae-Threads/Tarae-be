# TASKS: 관리자 장소/이벤트 CRUD 및 만료 관리

## 1. 도메인 리팩토링
- [x] 1-1. `Event` 엔티티 모든 비즈니스 필드 `val` → `var`
- [x] 1-2. `Event.update(form: EventCreateForm)` / `activate()` / `deactivate()` 도메인 메서드 추가
- [x] 1-3. `Place.status` `val` → `var`, `var active: Boolean = true` 추가
- [x] 1-4. `Place.activate()` / `deactivate()` 추가
- [x] 1-5. 도메인 메서드 단위 테스트
- [x] 1-6. `EventScheduler` + 관련 테스트 삭제, `EventRepository.deactivateExpiredEvents()` 삭제

## 2. Repository 확장
- [x] 2-1. `PlaceRepository.findAllWithFilters`에 `active=true AND status=OPEN` 강제
- [x] 2-2. `PlaceRepositoryCustom.findAllForAdmin(keyword, Pageable): Page<Place>` 추가 + 테스트
- [x] 2-3. `EventRepository.findAllWithFilters`에 `active=true` 강제
- [x] 2-4. `EventRepository.findPublicEventsByPlaceId(placeId, today)` 추가, 기존 `findAllByPlaceIdAndActiveTrue` 교체
- [x] 2-5. `EventRepositoryCustom.findAllForAdmin(filter, Pageable)` + `countExpiringSoon` 추가 + 테스트
- [x] 2-6. `AdminEventStatusFilter` enum 신규

## 3. 공개 서비스 수정
- [x] 3-1. `PlaceService.getPlaces/getPlace`: 비공개 장소 처리 (404)
- [x] 3-2. `PlaceService`의 연관 이벤트: `findPublicEventsByPlaceId` 사용
- [x] 3-3. `EventService.getEvents/getEvent`: 비공개 이벤트 처리 (404)
- [x] 3-4. 관련 테스트 업데이트/추가

## 4. 관리자 백엔드
- [x] 4-1. `AdminPlaceService` 신규 + 단위 테스트
- [x] 4-2. `AdminEventService` 신규 + 단위 테스트
- [x] 4-3. `AdminPlaceController` 신규 + WebMvcTest
- [x] 4-4. `AdminEventController` 신규 + WebMvcTest
- [x] 4-5. `PlaceBulkCreateRequest`, `EventBulkCreateRequest` DTO
- [x] 4-6. `AdminPlaceListRow`, `AdminEventListRow` 뷰 DTO
- [x] 4-7. `AdminDashboardService`에 `expiringSoonCount` 추가

## 5. 관리자 UI
- [x] 5-1. `templates/admin/places/list.html` (검색 + 페이징 + 테이블)
- [x] 5-2. `templates/admin/places/form.html` (다건 입력 + 프리뷰 모달)
- [x] 5-3. `templates/admin/events/list.html` (상태 필터 + 페이징 + 테이블)
- [x] 5-4. `templates/admin/events/form.html` (다건 입력 + 프리뷰 모달)
- [x] 5-5. `static/admin/admin.js` 신규 (공통 confirmSubmit 헬퍼; 행 추가/프리뷰는 페이지별 inline)
- [x] 5-6. `admin.css` 확장 — preview-modal, row-input, badge 추가
- [x] 5-7. `layout/base.html` 사이드바 메뉴 추가
- [x] 5-8. `dashboard.html` 만료 임박 카드 추가

## 6. 마무리
- [x] 6-1. 전체 `./gradlew test` 통과
- [x] 6-2. `./gradlew build` 통과
- [x] 6-3. 데이터 마이그레이션 스크립트 `docs/tech-notes/`에 기록
- [x] 6-4. `docs/architecture/overview.md` 갱신 (admin 도메인 설명)
