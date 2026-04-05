---

> **SDD (Software Design Document) 란?**
> "어떻게 만드는지" 를 정의하는 문서.
> 패키지 구조, 클래스 설계, API 명세, 테스트 전략 등 실제 구현 전 설계를 확정.
> PRD의 요구사항을 코드로 어떻게 옮길지 미리 그려두는 용도.

---

# SDD: B-01 Phase 0 셋업
- 작성일: 2026-04-05
- 완료일: 2026-04-05
- 상태: Done

## 기술 스택

- Kotlin + Spring Boot 3.5
- Spring Security 6
- Spring Data JPA + Hibernate
- JUnit 5 + MockK

## 아키텍처

```
com.taraethreads.tarae.
└── global/
    ├── common/
    │   └── BaseEntity.kt          (MappedSuperclass)
    ├── config/
    │   └── SecurityConfig.kt      (SecurityFilterChain)
    └── exception/
        ├── GlobalExceptionHandler.kt
        └── ErrorResponse.kt       (에러 응답 DTO)
```

## 도메인 모델

### BaseEntity

```kotlin
@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity {
    @CreatedDate
    val createdAt: LocalDateTime

    @LastModifiedDate
    var updatedAt: LocalDateTime
}
```

- `@EnableJpaAuditing` 은 Application 클래스 또는 별도 JpaConfig에 선언

## DB 설계

해당 없음 (BaseEntity는 테이블 직접 생성 안 함)

## API 설계

해당 없음 (인프라 코드)

## 예외 처리

### ErrorResponse

```json
{
  "status": 400,
  "message": "잘못된 요청입니다."
}
```

### GlobalExceptionHandler 처리 대상

| 상황 | 예외 | HTTP Status |
|------|------|-------------|
| 요청 바디 형식 오류 | `HttpMessageNotReadableException` | 400 |
| Bean Validation 실패 | `MethodArgumentNotValidException` | 400 |
| 리소스 없음 (추후 사용) | `NoSuchElementException` | 404 |
| 그 외 서버 오류 | `Exception` | 500 |

## 테스트 전략

- **SecurityConfig**: `@WebMvcTest` + `MockMvc`로 `/admin/**` 401, 일반 경로 200 확인
- **GlobalExceptionHandler**: `@WebMvcTest` + 의도적 예외 발생으로 응답 형식 확인
- **BaseEntity**: 통합 테스트에서 Entity 저장 후 `createdAt` / `updatedAt` null 아님 확인 (도메인 Entity 생기면 작성)
