# 코틀린 테스트 구조 — @Nested + 백틱
- 작성일: 2026-04-05

## 자바 테스트와 뭐가 다른가

자바 Spring 테스트는 클래스 안에 메서드를 쭉 나열한다.

```java
@Test
void region으로_필터링하면_해당_지역만_반환() { ... }

@Test
void categoryId로_필터링하면_해당_카테고리만_반환() { ... }

@Test
void region과_categoryId_함께_사용하면_모두_만족() { ... }
```

Kotlin + JUnit5에서는 `@Nested` + 백틱 네이밍을 쓴다.

```kotlin
@Nested
inner class `지역 필터` {
    @Test
    fun `region으로 필터링하면 해당 지역만 반환된다`() { ... }
}

@Nested
inner class `복합 필터` {
    @Test
    fun `region과 categoryId를 함께 사용하면 모두 만족하는 장소만 반환된다`() { ... }
}
```

---

## @Nested 란?

JUnit5 어노테이션. 테스트 클래스 안에 **중첩 클래스**를 만들어 관련 테스트끼리 묶는다.

```kotlin
class PlaceRepositoryTest {

    @Nested
    inner class `지역 필터` {   // ← 이 클래스가 하나의 그룹
        @Test fun `서울만 반환된다`() { ... }
        @Test fun `부산만 반환된다`() { ... }
    }

    @Nested
    inner class `카테고리 필터` {
        @Test fun `뜨개샵만 반환된다`() { ... }
    }
}
```

> `inner class` 를 붙이는 이유: Kotlin에서 중첩 클래스는 기본적으로 바깥 클래스에 접근 불가. `inner`를 붙여야 바깥 클래스의 필드(`placeRepository` 등)를 쓸 수 있다.

---

## 백틱(\`) 네이밍

Kotlin은 함수/클래스 이름을 백틱으로 감싸면 **공백 포함 한글 이름**을 쓸 수 있다.

```kotlin
fun `region으로 필터링하면 해당 지역만 반환된다`() { ... }
```

자바에서는 언더스코어로 연결했지만(`region으로_필터링하면`), Kotlin은 백틱으로 자연스러운 문장을 쓴다.
테스트 이름이 그대로 문서가 되는 효과가 있다.

---

## 테스트 리포트에서 보이는 모습

```
PlaceRepositoryTest
  ├── 지역 필터
  │     ├── region으로 필터링하면 해당 지역만 반환된다 ✅
  │     └── 부산만 반환된다 ✅
  ├── 카테고리 필터
  │     └── 뜨개샵만 반환된다 ✅
  └── 복합 필터
        └── region과 categoryId를 함께 사용하면 모두 만족하는 장소만 반환된다 ✅
```

테스트가 많아져도 어떤 케이스를 테스트하는지 한눈에 보인다.

---

## 정리

| | 자바 | Kotlin |
|--|------|--------|
| 테스트 네이밍 | `void region으로_필터링하면_()` | `` fun `region으로 필터링하면` `` |
| 그룹핑 | 없음 (플랫하게 나열) | `@Nested inner class` |
| 실무 사용 여부 | 레거시 프로젝트 | Kotlin 프로젝트 표준 |
