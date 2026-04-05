# SDD: B-04 제보 스키마 설계
- 작성일: 2026-04-05
- 완료일: 2026-04-05
- 상태: Done

## 도메인 모델

### RequestStatus
```kotlin
enum class RequestStatus { PENDING, APPROVED, REJECTED }
```

### RequestType (장소 요청 전용)
```kotlin
enum class RequestType { NEW, UPDATE }
```

### PlaceRequest
- 새 장소 등록 요청 (NEW) 또는 기존 장소 업데이트 요청 (UPDATE)
- 브랜드는 관리자 승인 시 brands 테이블에 매핑 → 요청 시 쉼표 구분 텍스트로 저장

### EventRequest
- 이벤트 등록 요청

## 패키지 구조

```
com.taraethreads.tarae.
└── request/
    ├── domain/
    │   ├── RequestStatus.kt
    │   ├── RequestType.kt
    │   ├── PlaceRequest.kt
    │   └── EventRequest.kt
    └── repository/
        ├── PlaceRequestRepository.kt
        └── EventRequestRepository.kt
```

## DB

SQL 체크리스트: `src/main/resources/db/migration/V3__create_request_tables.sql`
