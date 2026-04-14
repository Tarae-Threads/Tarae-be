# SDD: 리뷰 기능

## 기술 스택

- Spring Data JPA — Review 엔티티 + Repository
- BCryptPasswordEncoder — 비밀번호 해시/검증
- Thymeleaf — 관리자 리뷰 관리 페이지

## 아키텍처

```
[공개 API]
ReviewController
  └─ POST /api/places/{id}/reviews      → 장소 리뷰 작성
  └─ GET  /api/places/{id}/reviews       → 장소 리뷰 목록
  └─ POST /api/events/{id}/reviews       → 이벤트 리뷰 작성
  └─ GET  /api/events/{id}/reviews       → 이벤트 리뷰 목록
  └─ DELETE /api/reviews/{id}            → 리뷰 삭제 (비밀번호 검증)
       ↓
ReviewService
       ↓
ReviewRepository (JpaRepository)

[관리자]
AdminReviewController
  └─ GET  /admin/reviews                 → 리뷰 목록 (탭: 장소/이벤트)
  └─ POST /admin/reviews/{id}/delete     → 관리자 삭제
       ↓
AdminReviewService
       ↓
ReviewRepository
```

## 도메인 모델

### Review Entity

| 필드 | 타입 | 제약 | 설명 |
|------|------|------|------|
| id | Long (PK) | IDENTITY | |
| targetType | ReviewTargetType | STRING, 10자 | PLACE / EVENT |
| targetId | Long | NOT NULL | 대상 ID |
| nickname | String | 50자 | 프론트에서 전달 |
| email | String | 100자 | 정보용 |
| password | String | 255자 | BCrypt 해시 |
| content | String | TEXT | 리뷰 내용 |
| createdAt | LocalDateTime | | BaseEntity |
| updatedAt | LocalDateTime | | BaseEntity |

### 인덱스

- `idx_review_target`: `(target_type, target_id)` — 대상별 리뷰 조회

### Enum

```kotlin
enum class ReviewTargetType { PLACE, EVENT }
```

## API 설계

### 리뷰 작성

**POST** `/api/places/{placeId}/reviews`
**POST** `/api/events/{eventId}/reviews`

Request Body:
```json
{
  "nickname": "뜨개하는고양이",
  "email": "cat@example.com",
  "password": "1234",
  "content": "실 종류가 다양하고 사장님이 친절해요!"
}
```

Response: `201 Created`
```json
{
  "data": {
    "id": 1,
    "nickname": "뜨개하는고양이",
    "content": "실 종류가 다양하고 사장님이 친절해요!",
    "createdAt": "2026-04-14T12:00:00"
  }
}
```

### 리뷰 목록

**GET** `/api/places/{placeId}/reviews`
**GET** `/api/events/{eventId}/reviews`

Response: `200 OK`
```json
{
  "data": [
    {
      "id": 1,
      "nickname": "뜨개하는고양이",
      "content": "실 종류가 다양하고 사장님이 친절해요!",
      "createdAt": "2026-04-14T12:00:00"
    }
  ]
}
```

> email, password는 응답에 포함하지 않음

### 리뷰 삭제

**DELETE** `/api/reviews/{reviewId}`

Request Body:
```json
{
  "password": "1234"
}
```

Response: `200 OK`
```json
{
  "data": null
}
```

> 비밀번호 불일치 시 `400 INVALID_PASSWORD`

## DTO

| DTO | 용도 |
|-----|------|
| ReviewCreateRequest | 리뷰 작성 요청 (nickname, email, password, content) |
| ReviewDeleteRequest | 리뷰 삭제 요청 (password) |
| ReviewResponse | 리뷰 응답 (id, nickname, content, createdAt) |

## ErrorCode 추가

| 코드 | status | message |
|------|--------|---------|
| REVIEW_NOT_FOUND | 404 | 존재하지 않는 리뷰입니다 |
| INVALID_PASSWORD | 400 | 비밀번호가 일치하지 않습니다 |

## 관리자 페이지

### UI 구조

기존 제보 관리 페이지 패턴 그대로:

- 사이드바에 "리뷰 관리" 메뉴 추가
- 탭: `장소 리뷰` | `이벤트 리뷰`
- 테이블 컬럼: 대상명 | 닉네임 | 내용(미리보기 50자) | 작성일 | 삭제 버튼
- 최신순 정렬
- 삭제 시 confirm 다이얼로그

### 관리자 서비스

- `AdminReviewService`에서 리뷰 조회 시 대상명(Place.name / Event.title) 함께 반환
- 대상이 삭제된 경우 "(삭제됨)" 표시

## 변경 파일

### 신규

| 파일 | 설명 |
|------|------|
| `review/domain/Review.kt` | Entity |
| `review/domain/ReviewTargetType.kt` | Enum |
| `review/repository/ReviewRepository.kt` | Repository |
| `review/service/ReviewService.kt` | Service |
| `review/controller/ReviewController.kt` | REST Controller |
| `review/dto/ReviewCreateRequest.kt` | 작성 요청 DTO |
| `review/dto/ReviewDeleteRequest.kt` | 삭제 요청 DTO |
| `review/dto/ReviewResponse.kt` | 응답 DTO |
| `admin/controller/AdminReviewController.kt` | 관리자 컨트롤러 |
| `admin/service/AdminReviewService.kt` | 관리자 서비스 |
| `admin/dto/AdminReviewResponse.kt` | 관리자 리뷰 응답 DTO |
| `templates/admin/reviews/list.html` | 관리자 리뷰 목록 |

### 수정

| 파일 | 변경 내용 |
|------|---------|
| `global/exception/ErrorCode.kt` | REVIEW_NOT_FOUND, INVALID_PASSWORD 추가 |
| `global/config/SecurityConfig.kt` | 리뷰 API 경로 permit 추가 |
| `templates/admin/layout/base.html` | 사이드바에 리뷰 관리 메뉴 추가 |
| `admin/service/AdminDashboardService.kt` | 대시보드 리뷰 통계 추가 |
| `templates/admin/dashboard.html` | 리뷰 수 통계 카드 추가 |

## 테스트 전략

- **Repository 통합 테스트**: `@DataJpaTest`
  - targetType + targetId로 조회
  - 최신순 정렬 확인
- **Service 단위 테스트**: MockK
  - 리뷰 작성 시 비밀번호 해시 검증
  - 삭제 시 비밀번호 일치/불일치
  - 대상 존재 여부 검증
- **Controller 통합 테스트**: `@WebMvcTest`
  - 요청 validation
  - 정상 응답 구조
