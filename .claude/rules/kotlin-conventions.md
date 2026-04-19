---
paths:
  - "src/main/**/*.kt"
---

# Kotlin 코딩 컨벤션

## 불변성 & 상태 관리

- `val` 기본, `var`는 정말 필요한 경우만
- **Setter 금지** — 상태 변경은 의도를 표현하는 도메인 메서드로:
  ```kotlin
  // ❌
  member.name = "홍길동"
  // ✅
  member.changeName("홍길동")
  ```

## 객체 생성

- 복잡한 객체는 `companion object` 팩토리 메서드 사용:
  ```kotlin
  companion object {
      fun of(email: String, name: String): Member { ... }
      fun create(request: MemberCreateRequest): Member { ... }
  }
  ```
- 파라미터 3개 초과 시 named argument 활용

## 메서드 정렬

1. public 메서드 (호출자가 피호출자 위에)
2. protected 메서드
3. private 메서드
4. `companion object` (클래스 맨 아래)

## Null 안전성

- `!!` 절대 금지 — 로직 재설계로 null 제거
- `?.let`, `?.also`, `?: return`, `?: throw` 활용
- `require(condition) { "message" }` — 파라미터 검증
- `check(condition) { "message" }` — 상태 검증

## 예외 처리

- 커스텀 예외: `class MemberNotFoundException(id: Long) : RuntimeException("Member not found: $id")`
- 전역 처리: `@RestControllerAdvice`
- `Exception` 광범위 catch 금지
- 에러 코드/메시지는 반드시 `ErrorCode` enum에서 가져올 것 — 하드코딩 금지
  ```kotlin
  // ❌
  ErrorResponse(404, "리소스를 찾을 수 없습니다")

  // ✅
  ErrorResponse.of(ErrorCode.NOT_FOUND)
  ```
- 새 에러 시나리오 추가 시 `ErrorCode`에 먼저 정의 후 사용

## 로깅

```kotlin
private val log = LoggerFactory.getLogger(this::class.java)
```

- ERROR: 복구 불가능한 오류
- WARN: 복구 가능한 이상 상황
- INFO: 비즈니스 주요 이벤트
- DEBUG: 개발 시 디버깅 정보
- 민감정보(비밀번호, 토큰) 절대 로그 금지

## 주석

- "왜"만 주석, "무엇"은 코드가 설명
- KDoc(`/** */`)은 public API 메서드만
- 주석 처리된 코드 금지

## N+1 방지

- 연관 엔티티 함께 조회 시 `@EntityGraph` 또는 JPQL `fetch join`
- `@OneToMany`, `@ManyToMany` 기본 LAZY 유지

## Kotlin 관용 표현 우선

```kotlin
// ❌ Java 스타일
if (list.size > 0) { ... }
// ✅ Kotlin
if (list.isNotEmpty()) { ... }

// ❌
when {
    x == 1 -> ...
    x == 2 -> ...
}
// ✅
when (x) {
    1 -> ...
    2 -> ...
}
```
