# MockK vs Mockito — 왜 코틀린에서는 MockK인가

- 날짜: 2026-04-04

## 개념

MockK는 코틀린 전용 모킹 라이브러리. Mockito는 자바 생태계 표준이지만 코틀린에서 쓰면 마찰이 생긴다.

## 왜 MockK를 쓰나

### 1. 코틀린 클래스는 기본 `final`

자바는 클래스가 기본 open이라 Mockito가 바로 mock 가능.
코틀린은 기본 `final`이라 Mockito가 **mock을 못 만든다.**

```kotlin
// 코틀린 클래스는 기본 final
class MemberService(val repo: MemberRepository) { ... }

// Mockito → 에러 발생 (final class can't be mocked)
val service = Mockito.mock(MemberService::class.java) // ❌

// 해결하려면 mockito-kotlin + mockito-inline 플러그인을 추가해야 함
// = 설정이 복잡해짐

// MockK → 그냥 됨
val service = mockk<MemberService>() // ✅
```

### 2. 코틀린 DSL 문법 지원

```kotlin
// Mockito 스타일 (자바스러움)
`when`(repo.findById(1L)).thenReturn(Optional.of(member))
verify(repo, times(1)).save(any())

// MockK 스타일 (코틀린스러움)
every { repo.findById(1L) } returns Optional.of(member)
verify(exactly = 1) { repo.save(any()) }
```

MockK는 코틀린 람다 문법을 활용해서 가독성이 훨씬 좋다.

### 3. suspend 함수 (코루틴) 지원

```kotlin
// 코루틴 함수 모킹
coEvery { repo.findByIdSuspend(1L) } returns member
coVerify { repo.save(any()) }
```

Mockito는 코루틴 모킹이 자연스럽지 않다.

### 4. data class / object / companion object 모킹

```kotlin
// companion object의 팩토리 메서드 모킹
mockkObject(Member)
every { Member.of("test@test.com", "홍길동") } returns fakeMember

// 확장 함수 모킹도 가능
mockkStatic("com.example.ExtensionsKt")
every { "hello".myExtension() } returns "mocked"
```

Mockito로는 이런 코틀린 특화 기능을 모킹하기 매우 어렵다.

## Java Spring과 다른 점

| | Java + Mockito | Kotlin + MockK |
|---|---|---|
| 클래스 모킹 | 바로 가능 (기본 open) | MockK가 final 클래스 처리 |
| 문법 | `when().thenReturn()` | `every { } returns` |
| null 처리 | `any()` → nullable 이슈 | `any()` → 코틀린 타입 시스템과 호환 |
| Spring 통합 | `@MockBean` | `@MockkBean` (springmockk 라이브러리) |
| 코루틴 | 별도 라이브러리 필요 | `coEvery`, `coVerify` 내장 |

## 실제 코드

```kotlin
// Service 단위테스트 기본 패턴
class MemberServiceTest {

    private val memberRepository = mockk<MemberRepository>()
    private val memberService = MemberService(memberRepository)

    @Test
    fun `이메일 중복 시 예외가 발생한다`() {
        // given
        val email = "dup@test.com"
        every { memberRepository.existsByEmail(email) } returns true

        // when & then
        assertThrows<DuplicateEmailException> {
            memberService.register(MemberCreateRequest(email, "홍길동"))
        }
    }

    @Test
    fun `정상 가입 시 저장된다`() {
        // given
        val request = MemberCreateRequest("new@test.com", "홍길동")
        every { memberRepository.existsByEmail(any()) } returns false
        every { memberRepository.save(any()) } returns Member(email = "new@test.com", name = "홍길동")

        // when
        val result = memberService.register(request)

        // then
        assertThat(result.email).isEqualTo("new@test.com")
        verify(exactly = 1) { memberRepository.save(any()) }
    }
}
```

```kotlin
// Controller 슬라이스 테스트에서 @MockkBean 사용
@WebMvcTest(MemberController::class)
class MemberControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var memberService: MemberService

    @Test
    fun `회원 조회 API가 200을 반환한다`() {
        every { memberService.getById(1L) } returns MemberResponse(1L, "test@test.com", "홍길동")

        mockMvc.perform(get("/api/v1/members/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("홍길동"))
    }
}
```

## 필요한 의존성

```kotlin
// build.gradle.kts
testImplementation("io.mockk:mockk:1.13.16")
testImplementation("com.ninja-squad:springmockk:4.0.2") // @MockkBean 지원
```

## 주의할 점

- `relaxed = true` 남발 금지 — `mockk<T>(relaxed = true)`는 모든 메서드가 기본값을 반환해서 편하지만, 의도하지 않은 동작을 놓칠 수 있다. 필요한 메서드만 `every`로 정의할 것.
- `unmockkAll()` — `@AfterEach`에서 호출하면 static mock이 다른 테스트에 영향 주는 걸 방지.
- `@MockkBean` 사용 시 `com.ninja-squad:springmockk` 의존성 필수 — Spring 기본 `@MockBean`은 Mockito 전용.
