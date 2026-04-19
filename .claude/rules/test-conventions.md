---
paths:
  - "src/test/**/*.kt"
---

# 테스트 컨벤션

## 테스트 네이밍

백틱으로 한국어 설명형 이름 사용:

```kotlin
@Test
fun `회원 가입시 이메일이 중복이면 예외가 발생한다`() { ... }

@Test
fun `존재하지 않는 회원 조회시 MemberNotFoundException이 발생한다`() { ... }
```

## 테스트 구조

모든 테스트는 given/when/then 구조:

```kotlin
@Test
fun `회원 생성이 정상적으로 동작한다`() {
    // given
    val request = MemberCreateRequest(email = "test@example.com", name = "홍길동")

    // when
    val result = memberService.create(request)

    // then
    assertThat(result.email).isEqualTo("test@example.com")
}
```

## 테스트 그룹핑

`@Nested`로 관련 케이스 묶기:

```kotlin
@Nested
inner class `회원 생성` {
    @Test fun `정상 생성된다`() { ... }
    @Test fun `이메일 중복시 예외 발생`() { ... }
}
```

## 레이어별 테스트 전략

| 레이어 | 방식 | 어노테이션 | 언제 |
|--------|------|-----------|------|
| **Service** | 단위테스트 (기본) | 없음 (JUnit5 + MockK) | 비즈니스 로직, 분기, 예외 케이스 |
| **Service** | 통합테스트 (필요 시) | `@SpringBootTest` + Testcontainers | 트랜잭션 경계, 여러 서비스 협력, @EventListener 등 |
| **Repository** | 슬라이스 통합테스트 | `@DataJpaTest` + Testcontainers | 커스텀 JPQL, 검색 쿼리 |
| **Controller** | 슬라이스 통합테스트 | `@WebMvcTest` + MockkBean | 요청/응답 직렬화, 상태코드, 인증 |

### 서비스 단위테스트가 충분한 경우 (기본)
- 비즈니스 로직/도메인 규칙 검증 (할인 계산, 상태 전이 등)
- if/when 분기가 많은 메서드
- 예외 케이스 (존재하지 않는 엔티티 등)
- 단순 CRUD 위임 — 테스트 불필요 (작성 금지)

### 통합테스트가 필요한 경우 (추가)
- `@Transactional` 전파 속성이 중요한 시나리오
- 복잡한 JPQL fetch join, `@EntityGraph` 쿼리의 실제 동작
- 여러 서비스 간 협력으로 DB 상태가 변하는 흐름
- Spring 기능 의존: `@EventListener`, `@Async`, `@Cacheable`

## 테스트 DB

- **H2 인메모리** 사용 — `MODE=MySQL`로 MySQL 호환 모드 적용
- `@Transactional`로 테스트 간 격리 (자동 롤백)

```yaml
# application-test.yml
spring:
  datasource:
    url: jdbc:h2:mem:tarae;MODE=MySQL
```

## 서비스 통합테스트에서 @Transactional 사용 규칙

서비스 통합테스트(`@SpringBootTest`)에서 `@Transactional`을 붙이면 테스트 트랜잭션이 서비스 트랜잭션을 감싸기 때문에, **실제 커밋이 발생하지 않아 트랜잭션 경계 검증이 불가능**하다.

### 기본 방침: `@Transactional` 붙이고 인지하며 사용

대부분의 서비스 통합테스트는 `@Transactional`을 붙여 자동 롤백으로 테스트 격리한다. 단, 이 방식은 **트랜잭션 커밋 여부를 검증하지 못한다는 한계를 팀이 인지하고 있어야 한다.**

```kotlin
@SpringBootTest
@ActiveProfiles("test")
@Transactional  // 대부분의 케이스 — 자동 롤백, 편리
class SomeServiceIntegrationTest { ... }
```

### 예외: 트랜잭션 커밋이 핵심인 케이스

여러 테이블에 걸친 상태 변경, 트랜잭션 전파 검증이 목적인 테스트는 `@Transactional`을 **붙이지 않고** 명시적으로 cleanup한다.

```kotlin
@SpringBootTest
@ActiveProfiles("test")
// @Transactional 없음
class ApproveIntegrationTest {

    @AfterEach
    fun cleanup() {
        placeRequestRepository.deleteAll()
        placeRepository.deleteAll()
    }

    @Test
    fun `제보 승인 시 두 테이블이 함께 변경된다`() {
        // 실제 커밋까지 검증 가능
    }
}
```

### 판단 기준

| 케이스 | @Transactional |
|--------|---------------|
| 조회 로직 검증 | ✅ 붙임 |
| 단일 테이블 쓰기 | ✅ 붙임 |
| 여러 테이블 동시 변경 (트랜잭션 경계가 핵심) | ❌ 빼고 명시적 cleanup |

## MockK 사용 (Mockito 사용 금지)

```kotlin
// 의존성 모킹
val memberRepository = mockk<MemberRepository>()
val memberService = MemberService(memberRepository)

// 동작 정의
every { memberRepository.findById(1L) } returns Optional.of(member)

// 호출 검증
verify { memberRepository.save(any()) }

// Spring 통합 테스트에서
@MockkBean
private lateinit var memberService: MemberService
```

## 테스트 데이터

- 테스트 픽스처는 `companion object` 또는 별도 팩토리 함수로 분리
- 하드코딩된 ID, 이메일 등은 상수로 관리

## 테스트 비율 가이드

```
 단위테스트 ~70%    — MockK, Spring 없이, 빠르게
 통합테스트 ~25-30% — @DataJpaTest, @WebMvcTest, 필요시 @SpringBootTest
 E2E       ~0-5%   — 사이드 프로젝트에서는 생략 가능
```

핵심 비즈니스 로직에 두껍게, 단순 CRUD에는 쓰지 않는다.
