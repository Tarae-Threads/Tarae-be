---

> **TASKS 란?**
> SDD를 실제 작업 단위로 쪼갠 체크리스트.
> 한 Task = 한 번에 구현하고 테스트까지 완료할 수 있는 단위.
> 승인 후 이 순서대로 TDD(테스트 먼저 → 구현)로 진행.

---

# TASKS: B-01 Phase 0 셋업
- 작성일: 2026-04-05

- [ ] T1. BaseEntity 구현
  - 목적: 모든 Entity가 공통으로 상속할 auditing 필드 제공
  - 작업 내용:
    - `global/common/BaseEntity.kt` 작성 (`createdAt`, `updatedAt`)
    - `@EnableJpaAuditing` 활성화 (TaraeApplication 또는 JpaConfig)
  - 예상 변경 파일:
    - `src/main/kotlin/.../global/common/BaseEntity.kt` (신규)
    - `src/main/kotlin/.../TaraeApplication.kt` (어노테이션 추가)
  - 완료 기준: 빌드 성공, `@EnableJpaAuditing` 적용 확인
  - 완료일: -

- [ ] T2. SecurityConfig 구현
  - 목적: `/admin/**` 인증 필요, 나머지 전체 공개
  - 작업 내용:
    - `global/config/SecurityConfig.kt` 작성
    - CSRF 비활성화 (REST API), 세션 STATELESS
  - 예상 변경 파일:
    - `src/main/kotlin/.../global/config/SecurityConfig.kt` (신규)
  - 완료 기준:
    - `GET /api/test` → 200
    - `GET /admin/anything` → 401
  - 완료일: -

- [ ] T3. GlobalExceptionHandler 구현
  - 목적: 예외 발생 시 일관된 JSON 응답
  - 작업 내용:
    - `global/exception/ErrorResponse.kt` (data class)
    - `global/exception/GlobalExceptionHandler.kt` (`@RestControllerAdvice`)
    - 처리: `MethodArgumentNotValidException`, `HttpMessageNotReadableException`, `NoSuchElementException`, `Exception`
  - 예상 변경 파일:
    - `src/main/kotlin/.../global/exception/ErrorResponse.kt` (신규)
    - `src/main/kotlin/.../global/exception/GlobalExceptionHandler.kt` (신규)
  - 완료 기준:
    - 각 예외 케이스별 올바른 status + message JSON 응답
  - 완료일: -
