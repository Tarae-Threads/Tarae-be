# @EnableJpaAuditing을 @SpringBootApplication에 두면 @WebMvcTest가 깨진다
- 작성일: 2026-04-05

## 문제

`@SpringBootApplication`에 `@EnableJpaAuditing`을 붙이면 `@WebMvcTest` 실행 시 아래 에러가 발생한다.

```
Caused by: java.lang.IllegalArgumentException: JPA metamodel must not be empty
```

## 원인

`@WebMvcTest`는 Web 레이어만 로드하는 슬라이스 테스트다.
그런데 `@EnableJpaAuditing`이 `@SpringBootApplication`에 있으면 전체 컨텍스트 로드 시 JPA Metamodel을 요구한다.
`@WebMvcTest`에는 JPA가 없으니 Metamodel이 비어있어 에러가 난다.

## 해결

`@EnableJpaAuditing`을 별도 `@Configuration` 클래스로 분리한다.

```kotlin
// ❌ TaraeApplication.kt
@SpringBootApplication
@EnableJpaAuditing  // 여기 두면 안 됨
class TaraeApplication

// ✅ JpaConfig.kt
@Configuration
@EnableJpaAuditing  // 여기로 분리
class JpaConfig(private val em: EntityManager) {
    @Bean
    fun jpaQueryFactory() = JPAQueryFactory(em)
}
```

`@WebMvcTest`는 `@Configuration` 클래스를 자동으로 로드하지 않으므로 충돌이 없다.
