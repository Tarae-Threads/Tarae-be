# TASKS: 리뷰 기능

## Phase 1: 도메인 + Repository

- [ ] T1. Review 엔티티 + ReviewTargetType 열거형 작성
  - 목적: 핵심 도메인 모델
  - 작업 내용:
    - `Review.kt` — BaseEntity 상속, targetType/targetId/nickname/email/password/content
    - `ReviewTargetType.kt` — PLACE, EVENT
    - `(target_type, target_id)` 복합 인덱스
  - 예상 변경 파일: `review/domain/Review.kt`, `review/domain/ReviewTargetType.kt`
  - 완료 기준: 컴파일 통과, 테이블 생성 확인

- [ ] T2. ReviewRepository + 통합 테스트
  - 목적: 데이터 접근 계층
  - 작업 내용:
    - `ReviewRepository` — findByTargetTypeAndTargetIdOrderByCreatedAtDesc
    - 통합 테스트: 저장/조회/삭제, 정렬 순서 검증
  - 예상 변경 파일: `review/repository/ReviewRepository.kt`, 테스트 파일
  - 완료 기준: Repository 통합 테스트 통과

## Phase 2: Service

- [ ] T3. ReviewService + 단위 테스트
  - 목적: 비즈니스 로직
  - 작업 내용:
    - 리뷰 작성: 대상 존재 검증 → 비밀번호 BCrypt 해시 → 저장
    - 리뷰 목록: targetType + targetId로 조회
    - 리뷰 삭제: 비밀번호 매칭 → 삭제
    - ErrorCode 추가 (REVIEW_NOT_FOUND, INVALID_PASSWORD)
  - 예상 변경 파일: `review/service/ReviewService.kt`, `global/exception/ErrorCode.kt`, 테스트 파일
  - 완료 기준: Service 단위 테스트 통과

## Phase 3: Controller + DTO

- [ ] T4. DTO 작성 (ReviewCreateRequest, ReviewDeleteRequest, ReviewResponse)
  - 목적: API 입출력 모델
  - 작업 내용:
    - ReviewCreateRequest: nickname, email, password, content + validation
    - ReviewDeleteRequest: password
    - ReviewResponse: id, nickname, content, createdAt + from() 팩토리
  - 예상 변경 파일: `review/dto/*.kt`
  - 완료 기준: 컴파일 통과

- [ ] T5. ReviewController + 통합 테스트
  - 목적: REST API 엔드포인트
  - 작업 내용:
    - POST /api/places/{id}/reviews, GET /api/places/{id}/reviews
    - POST /api/events/{id}/reviews, GET /api/events/{id}/reviews
    - DELETE /api/reviews/{id}
    - SecurityConfig에 경로 permit 추가
    - Swagger 문서화
  - 예상 변경 파일: `review/controller/ReviewController.kt`, `global/config/SecurityConfig.kt`, 테스트 파일
  - 완료 기준: Controller 통합 테스트 통과, Swagger에서 API 확인

## Phase 4: 관리자 페이지

- [ ] T6. AdminReviewService + AdminReviewResponse
  - 목적: 관리자용 리뷰 조회/삭제 로직
  - 작업 내용:
    - 리뷰 목록: targetType별 조회 + 대상명(Place.name/Event.title) 매핑
    - 관리자 삭제: 비밀번호 없이 삭제
    - AdminReviewResponse: id, targetName, nickname, content(50자 미리보기), createdAt
  - 예상 변경 파일: `admin/service/AdminReviewService.kt`, `admin/dto/AdminReviewResponse.kt`
  - 완료 기준: 단위 테스트 통과

- [ ] T7. AdminReviewController + 리뷰 관리 템플릿
  - 목적: 관리자 UI
  - 작업 내용:
    - GET /admin/reviews?type={place|event}
    - POST /admin/reviews/{id}/delete
    - list.html — 탭(장소/이벤트) + 테이블 + 삭제 버튼
    - base.html 사이드바에 리뷰 관리 메뉴 추가
  - 예상 변경 파일: `admin/controller/AdminReviewController.kt`, `templates/admin/reviews/list.html`, `templates/admin/layout/base.html`
  - 완료 기준: 관리자 페이지에서 리뷰 목록/삭제 동작

- [ ] T8. 대시보드 리뷰 통계 추가
  - 목적: 대시보드에 리뷰 수 표시
  - 작업 내용:
    - AdminDashboardService에 리뷰 카운트 추가
    - dashboard.html에 통계 카드 추가
  - 예상 변경 파일: `admin/service/AdminDashboardService.kt`, `templates/admin/dashboard.html`
  - 완료 기준: 대시보드에 리뷰 수 표시
