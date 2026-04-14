# SDD: 관리자 장소/이벤트 CRUD 및 만료 관리

## 기술 스택

- Spring Data JPA + QueryDSL (기존 Repository 패턴 확장)
- Thymeleaf SSR (기존 `admin.css` 디자인 시스템 재사용)
- Vanilla JS (행 추가/프리뷰 모달/확인 다이얼로그)

## 아키텍처

```
[관리자 페이지]
AdminPlaceController                    AdminEventController
 ├ GET  /admin/places (list, q, page)     ├ GET  /admin/events (list, filter, page)
 ├ GET  /admin/places/new                  ├ GET  /admin/events/new
 ├ POST /admin/places (다건 생성)           ├ POST /admin/events (다건 생성)
 ├ GET  /admin/places/{id}/edit             ├ GET  /admin/events/{id}/edit
 ├ POST /admin/places/{id}                  ├ POST /admin/events/{id}
 ├ POST /admin/places/{id}/toggle-active    ├ POST /admin/events/{id}/toggle-active
 └ POST /admin/places/{id}/delete           └ POST /admin/events/{id}/delete
        ↓                                           ↓
 AdminPlaceService                        AdminEventService
        ↓                                           ↓
 PlaceRepository (확장)                    EventRepository (확장)

[공개 API 필터 변경]
 PlaceService.getPlaces/getPlace          → active + status 필터 강화
 EventService.getEvents                   → active + endDate 필터 강화
 PlaceService.getPlace.events             → active + endDate 필터 강화
```

## 도메인 변경

### Place 엔티티
- [ ] `var active: Boolean = true` 컬럼 추가
- [ ] `val status` → `var status`
- [ ] 도메인 메서드 추가: `activate()`, `deactivate()`

### Event 엔티티
- [ ] 모든 비즈니스 필드 `val` → `var`
- [ ] 도메인 메서드 추가: `update(form: EventCreateForm)`, `activate()`, `deactivate()`

### EventScheduler 삭제
- `EventScheduler.kt` 전체 삭제
- `EventRepository.deactivateExpiredEvents()` 쿼리 삭제
- 관련 테스트 삭제

### 데이터 마이그레이션
- `UPDATE events SET active = true` — 기존 자동 비활성 데이터 복원
- 운영 DB에 수동 반영 (마이그레이션 스크립트는 `docs/tech-notes/`에 기록)

## Repository 쿼리 변경

### PlaceRepository

**공개용 (`findAllWithFilters` 수정)** — `active=true AND status='OPEN'` 조건 기본 추가

**관리자용 (신규)**
```kotlin
// PlaceRepositoryCustom
fun findAllForAdmin(keyword: String?, pageable: Pageable): Page<Place>
```
- 검색: 장소명 / 주소 / 카테고리명 LIKE (distinct join)
- 정렬: id DESC
- 필터 없음 (비활성/폐업 포함)

### EventRepository

**공개용 (`findAllWithFilters` 수정)** — `active=true AND (endDate IS NULL OR endDate >= today)` 강제

**장소 연관 이벤트 (`findAllByPlaceIdAndActiveTrue` 대체)**
```kotlin
// 기존
fun findAllByPlaceIdAndActiveTrue(placeId: Long): List<Event>
// 신규
fun findPublicEventsByPlaceId(placeId: Long, today: LocalDate): List<Event>
```
- 조건: `place_id = :placeId AND active = true AND (end_date IS NULL OR end_date >= :today)`

**관리자용 (신규)**
```kotlin
// EventRepositoryCustom
fun findAllForAdmin(filter: AdminEventStatusFilter, pageable: Pageable): Page<Event>
fun countExpiringSoon(today: LocalDate, threshold: LocalDate): Long
```
- 필터 enum: `ALL / ONGOING / EXPIRING_SOON / EXPIRED`
  - ONGOING: `end_date IS NULL OR end_date >= today`
  - EXPIRING_SOON: `end_date BETWEEN today AND today+7`
  - EXPIRED: `end_date < today`
- `deactivateExpiredEvents` 삭제

## 관리자 API 설계

### 장소 등록 (다건)

**POST** `/admin/places`

Form body (spring `@ModelAttribute`로 배열 바인딩):
```
places[0].name=...&places[0].region=...&places[0].categoryIds=1,2&...
places[1].name=...&...
```

프리뷰 모달은 클라이언트 단에서 form 데이터를 읽어 미리 렌더, 확인 시 submit.

### 장소 수정

**POST** `/admin/places/{id}` — 단건. form 바인딩은 기존 `PlaceCreateForm` 재사용

### 활성 토글 / 삭제

- `POST /admin/places/{id}/toggle-active`
- `POST /admin/places/{id}/delete`

이벤트도 동일한 구조 (`/admin/events/...`)

### 관리자 응답

관리자 페이지는 Thymeleaf Model에 직접 주입 (별도 JSON 응답 DTO 없음).
`AdminPlaceListRow`, `AdminEventListRow` 같은 뷰 전용 DTO를 서비스에서 구성.

## DTO 설계

### 신규
| DTO | 용도 |
|-----|------|
| `PlaceBulkCreateRequest` | `places: List<PlaceCreateForm>` 래퍼 |
| `EventBulkCreateRequest` | `events: List<EventCreateForm>` 래퍼 |
| `AdminPlaceListRow` | 목록 행 (id, name, region, active, status, categories) |
| `AdminEventListRow` | 목록 행 (id, title, startDate, endDate, active, 상태: ongoing/expiring/expired) |
| `AdminEventStatusFilter` | enum ALL/ONGOING/EXPIRING_SOON/EXPIRED |

### 수정
- `PlaceCreateForm`: 이미 존재, 변경 없음
- `EventCreateForm`: 이미 존재, 변경 없음

## 서비스 설계

### AdminPlaceService (신규)
```kotlin
@Transactional(readOnly = true)
class AdminPlaceService(
    private val placeRepository: PlaceRepository,
    private val categoryService: CategoryService,
    private val tagService: TagService,
    private val brandService: BrandService,
) {
    fun list(keyword: String?, pageable: Pageable): Page<AdminPlaceListRow>
    fun get(id: Long): PlaceCreateForm  // 수정 폼 초기값

    @Transactional
    fun createBulk(forms: List<PlaceCreateForm>): List<Long>

    @Transactional
    fun update(id: Long, form: PlaceCreateForm)

    @Transactional
    fun toggleActive(id: Long)

    @Transactional
    fun delete(id: Long)
}
```

### AdminEventService (신규)
```kotlin
@Transactional(readOnly = true)
class AdminEventService(
    private val eventRepository: EventRepository,
    private val placeRepository: PlaceRepository,
) {
    fun list(filter: AdminEventStatusFilter, pageable: Pageable): Page<AdminEventListRow>
    fun get(id: Long): EventCreateForm

    @Transactional
    fun createBulk(forms: List<EventCreateForm>): List<Long>

    @Transactional
    fun update(id: Long, form: EventCreateForm)

    @Transactional
    fun toggleActive(id: Long)

    @Transactional
    fun delete(id: Long)
}
```

### PlaceService 수정
- `getPlaces()`: 공개 조회 시 `active=true AND status=OPEN` 필터 추가
- `getPlace()`: 존재 + `active=true AND status=OPEN` 아니면 404
- 연관 이벤트: `findPublicEventsByPlaceId(id, today)` 사용

### EventService 수정
- `getEvents()`: `active=true AND (endDate IS NULL OR endDate >= today)` 강제
- `getEvent()`: 존재 + 조건 통과 아니면 404

### AdminDashboardService 수정
- 통계에 `countExpiringSoon(today, today+7)` 추가

## 관리자 UI 설계

### 공통 컴포넌트 (기존 재사용)
- `filter-btn` — 이벤트 상태 필터
- `chip-toggle` — 카테고리/태그/브랜드 선택
- `badge` — 활성/비활성/만료/임박 상태 표시
- `card` — 프리뷰 모달 내부 카드
- 테이블 스타일 그대로

### 신규 CSS 추가 (admin.css)
```
.row-input-table    /* 다건 입력 행 테이블 */
.row-add-btn        /* 행 추가 버튼 */
.row-remove-btn     /* 행 삭제 아이콘 */
.preview-modal      /* 프리뷰 모달 백드롭 + 컨테이너 */
.preview-modal-body /* 내부 카드 그리드 */
.badge-active / .badge-inactive
.badge-ongoing / .badge-expiring / .badge-expired
```

### 페이지별 템플릿

| 템플릿 | 설명 |
|--------|------|
| `admin/places/list.html` | 검색바 + 페이징 + 테이블 |
| `admin/places/form.html` | 다건 입력 폼 (등록/수정 공용) + 프리뷰 모달 |
| `admin/events/list.html` | 상태 필터 + 페이징 + 테이블 |
| `admin/events/form.html` | 다건 입력 폼 + 프리뷰 모달 |

사이드바에 "장소 관리", "이벤트 관리" 메뉴 추가.

### 클라이언트 JS (`admin.js` 신규)
- `addRow(tableId, templateId)` — 입력 행 복제 추가
- `removeRow(btn)` — 행 제거
- `openPreviewModal(formEl)` — 폼 데이터 수집 → 카드 렌더 → 확인 시 실제 submit
- `closePreviewModal()`
- `confirmAction(message)` — 토글/삭제 확인

## 공개 API 영향

| 엔드포인트 | 변경 |
|-----------|------|
| `GET /api/places` | `active=true AND status=OPEN` 필터 추가 |
| `GET /api/places/{id}` | 동일 조건 아니면 404 |
| `GET /api/places/{id}.events` | `active=true AND endDate 유효` 필터 |
| `GET /api/events` | `active=true AND endDate 유효` 강제 |
| `GET /api/events/{id}` | 동일 조건 아니면 404 |

> 기존 `EventScheduler`가 자동으로 비활성화하던 케이스가 쿼리 조건으로 대체됨.

## 변경 파일

### 신규
| 파일 | 설명 |
|------|------|
| `admin/controller/AdminPlaceController.kt` | |
| `admin/controller/AdminEventController.kt` | |
| `admin/service/AdminPlaceService.kt` | |
| `admin/service/AdminEventService.kt` | |
| `admin/dto/AdminPlaceListRow.kt` | |
| `admin/dto/AdminEventListRow.kt` | |
| `admin/dto/AdminEventStatusFilter.kt` | enum |
| `admin/dto/PlaceBulkCreateRequest.kt` | |
| `admin/dto/EventBulkCreateRequest.kt` | |
| `templates/admin/places/list.html` | |
| `templates/admin/places/form.html` | |
| `templates/admin/events/list.html` | |
| `templates/admin/events/form.html` | |
| `static/admin/admin.js` | 행 추가/프리뷰 모달 JS |

### 수정
| 파일 | 변경 |
|------|------|
| `place/domain/Place.kt` | `active` 추가, `status` var, 도메인 메서드 |
| `event/domain/Event.kt` | 모든 필드 var, update/activate/deactivate 추가 |
| `place/repository/PlaceRepository.kt` | active 필터, 관리자 페이징 쿼리 |
| `place/repository/PlaceRepositoryCustom.kt` | `findAllForAdmin` |
| `place/repository/PlaceRepositoryImpl.kt` | 구현 |
| `event/repository/EventRepository.kt` | `findPublicEventsByPlaceId`, `deactivateExpiredEvents` 삭제 |
| `event/repository/EventRepositoryCustom.kt` | `findAllForAdmin`, `countExpiringSoon` |
| `event/repository/EventRepositoryImpl.kt` | 구현 |
| `place/service/PlaceService.kt` | 공개 조회 필터 강화 |
| `event/service/EventService.kt` | 공개 조회 필터 강화 |
| `admin/service/AdminDashboardService.kt` | 임박 이벤트 카운트 |
| `templates/admin/dashboard.html` | 임박 이벤트 카드 |
| `templates/admin/layout/base.html` | 사이드바 메뉴 추가 |
| `static/admin/admin.css` | 신규 컴포넌트 CSS |

### 삭제
| 파일 | 사유 |
|------|------|
| `event/scheduler/EventScheduler.kt` | active 의미 재정의로 불필요 |
| 관련 스케줄러 테스트 | |

## 테스트 전략

### Entity / 도메인 메서드
- 단위 테스트: `Place.activate()/deactivate()`, `Event.update(form)` 상태 변화 검증

### Repository (`@DataJpaTest`)
- `PlaceRepository.findAllForAdmin`: 검색 토큰 매칭, 페이징 경계
- `PlaceRepository.findAllWithFilters` 공개용: active/status 필터 검증
- `EventRepository.findAllForAdmin`: 상태 필터별 결과
- `EventRepository.findPublicEventsByPlaceId`: active + endDate 필터

### Service (단위/`@SpringBootTest` 혼용)
- `AdminPlaceService.createBulk`: N건 저장 성공
- `AdminPlaceService.toggleActive`: active 상태 뒤집힘
- `AdminPlaceService.delete`: 삭제 후 조회 불가
- `AdminEventService.list`: 필터별 정렬/페이징
- `PlaceService.getPlace`: 비활성 장소 404
- `EventService.getEvents`: 만료 이벤트 제외

### Controller (`@WebMvcTest`)
- 리스트 페이지 렌더, 필터 파라미터 바인딩
- 다건 생성 폼 바인딩
- 토글/삭제 302 리다이렉트

## 오픈 이슈

- **다건 입력 실패 처리**: N개 중 하나 실패 시 전체 롤백할지 부분 성공할지 → **전체 롤백** (`@Transactional`). 실패 시 에러와 함께 입력값 유지해서 다시 렌더
- **카테고리 검색 쿼리 성능**: distinct join 3개(tag/brand/category)로 느릴 수 있음 → 데이터 소량 가정, 필요 시 추후 튜닝
- **프리뷰 모달에서 뒤로가기**: 모달 닫고 폼 유지. 이탈 시 경고 없음 (과한 UX로 판단)
