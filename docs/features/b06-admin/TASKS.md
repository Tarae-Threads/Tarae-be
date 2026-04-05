# TASKS: 관리자 페이지 (Thymeleaf)

---

- [x] T1. 셋업 — 의존성, Security, 공통 레이아웃 (완료일: 2026-04-05)
  - 목적: 관리자 페이지 개발 기반 마련
  - 작업 내용:
    - `SecurityConfig`에서 `/admin/**` permitAll 처리, STATELESS 세션 제거
    - `templates/admin/layout/base.html` 공통 레이아웃 (사이드바 nav 포함)
    - `static/admin/admin.css` 어스톤 팔레트 기본 스타일
    - `AdminDashboardController` skeleton + `templates/admin/dashboard.html`
  - 예상 변경 파일: `SecurityConfig.kt`, `templates/admin/layout/base.html`, `static/admin/admin.css`
  - 완료 기준: `/admin` 접근 시 레이아웃 정상 렌더링

---

- [x] T2. 엔티티 — PlaceRequest, EventRequest status 가변화 (완료일: 2026-04-05)
  - 목적: 제보 승인/거절 상태 전이를 도메인 메서드로 처리
  - 작업 내용:
    - `PlaceRequest.status` → `var` + `internal set` (kotlin-allopen open 클래스 제약으로 private set 불가)
    - `PlaceRequest.approve()`, `PlaceRequest.reject()` 도메인 메서드 추가
    - `EventRequest` 동일하게 적용
    - 단위 테스트 추가 (`PlaceRequestTest`, `EventRequestTest`)
  - 예상 변경 파일: `PlaceRequest.kt`, `EventRequest.kt`, 테스트 파일
  - 완료 기준: approve/reject 호출 시 status 변경, PENDING 아닌 상태에서 호출 시 예외

---

- [x] T3. 대시보드 (완료일: 2026-04-05)
  - 목적: 현황 한눈에 파악
  - 작업 내용:
    - `AdminDashboardService` — count 쿼리 활용
    - `AdminDashboardController` — 통계 조회 후 `dashboard.html`로 전달
    - `dashboard.html` — 장소 수, 이벤트 수, PENDING 제보 수 (장소/이벤트 각각)
    - `PlaceRequestRepository`, `EventRequestRepository`에 `countByStatus`, `findAllByStatus` 추가
    - `@WebMvcTest` 컨트롤러 테스트
  - 예상 변경 파일: `AdminDashboardController.kt`, `AdminDashboardService.kt`, `templates/admin/dashboard.html`
  - 완료 기준: `/admin` 접근 시 통계 숫자 정상 표시

---

- [x] T4. 제보 목록 (완료일: 2026-04-05)
  - 목적: 검수 대기 중인 제보 확인
  - 작업 내용:
    - `AdminRequestController.list()` — type(place/event), status(PENDING/APPROVED/REJECTED) 필터
    - `AdminRequestService.getPlaceRequests()`, `getEventRequests()`
    - `templates/admin/requests/list.html` — 탭(장소/이벤트) + 상태 필터 + 목록 테이블
    - 서비스 단위 테스트, 컨트롤러 `@WebMvcTest` 테스트
  - 예상 변경 파일: `AdminRequestController.kt`, `AdminRequestService.kt`, `list.html`
  - 완료 기준: PENDING 제보 목록 표시, 탭/필터 동작

---

- [x] T5. 장소 제보 검수 (완료일: 2026-04-05)
  - 목적: 제보 내용 보면서 실제 DB 입력
  - 작업 내용:
    - `AdminRequestController.placeDetail()` — PlaceRequest 조회, 카테고리/태그/브랜드 목록 전달
    - `AdminRequestController.approvePlaceRequest()` — PlaceCreateForm 바인딩 → Place 생성 + status APPROVED
    - `AdminRequestController.rejectPlaceRequest()` — status REJECTED
    - `AdminRequestService` 승인/거절 로직 (`approvePlaceRequest`, `rejectPlaceRequest`)
    - `PlaceCreateForm` DTO
    - `templates/admin/requests/place-detail.html` — 좌: 원본, 우: 입력 폼
    - 서비스 단위 테스트, 컨트롤러 `@WebMvcTest` 테스트
  - 완료 기준: 승인 시 Place 생성 + 제보 APPROVED, 거절 시 REJECTED, 이미 처리된 제보 재처리 시 에러

---

- [x] T6. 이벤트 제보 검수 (완료일: 2026-04-05)
  - 목적: 이벤트 제보 검수 (T5와 동일한 구조)
  - 작업 내용:
    - `AdminRequestController.eventDetail()`, `approveEventRequest()`, `rejectEventRequest()`
    - `AdminRequestService` 이벤트 승인/거절 로직
    - `EventCreateForm` DTO
    - `templates/admin/requests/event-detail.html`
    - 서비스 단위 테스트, 컨트롤러 `@WebMvcTest` 테스트
  - 완료 기준: T5와 동일

---

> T7(장소 CRUD), T8(이벤트 CRUD), T9(Excel import)는 별도 백로그로 분리 (B-07 이후)
