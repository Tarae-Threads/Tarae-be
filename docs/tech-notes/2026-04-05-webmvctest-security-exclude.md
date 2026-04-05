# @WebMvcTest에서 Security 제외하는 패턴
- 작성일: 2026-04-05

## 문제

`@WebMvcTest`는 기본적으로 Spring Security를 함께 로드한다.
Security와 무관한 테스트(ExceptionHandler 등)를 작성할 때 401/403이 반환되어 테스트가 깨진다.

## 해결

테스트 대상이 Security와 무관하면 `excludeAutoConfiguration`으로 제외한다.

```kotlin
@WebMvcTest(
    controllers = [MyController::class],
    excludeAutoConfiguration = [
        SecurityAutoConfiguration::class,
        SecurityFilterAutoConfiguration::class,
    ]
)
class MyControllerTest { ... }
```

## 언제 제외하고 언제 포함하나

| 상황 | Security 처리 |
|------|--------------|
| ExceptionHandler, 비즈니스 로직 테스트 | `excludeAutoConfiguration`으로 제외 |
| 인증/인가 동작 검증이 필요한 테스트 | `@WithMockUser` 등으로 인증 상태 설정 |
| Security 설정 자체를 테스트할 때 | Security 포함 + `@WithMockUser` |
