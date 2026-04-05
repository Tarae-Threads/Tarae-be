# QueryDSL KAPT 세팅 (Spring Boot 3 + Kotlin)
- 작성일: 2026-04-05

## 배경

필터 조건이 여러 개일 때 JPQL nullable 방식은 조건 없어도 JOIN이 발생하고 쿼리가 복잡해진다.
QueryDSL을 쓰면 조건 있을 때만 JOIN하고, 타입 안전하게 동적 쿼리를 작성할 수 있다.

## build.gradle.kts 설정

```kotlin
plugins {
    kotlin("kapt") version "1.9.25"  // 추가
}

dependencies {
    implementation("com.querydsl:querydsl-jpa:5.1.0:jakarta")
    kapt("com.querydsl:querydsl-apt:5.1.0:jakarta")
    kapt("jakarta.annotation:jakarta.annotation-api")
    kapt("jakarta.persistence:jakarta.persistence-api")
}
```

> Spring Boot 3.x는 Jakarta EE → `:jakarta` classifier 필수

## Q클래스 생성

```bash
./gradlew kaptKotlin
```

`build/generated/source/kapt/main/` 하위에 `QPlace`, `QCategory` 등 자동 생성됨.
Entity 추가/수정 시마다 재실행 필요.

## JPAQueryFactory 빈 등록

```kotlin
@Configuration
@EnableJpaAuditing
class JpaConfig(private val em: EntityManager) {
    @Bean
    fun jpaQueryFactory(): JPAQueryFactory = JPAQueryFactory(em)
}
```

## Repository 구조

QueryDSL을 사용하는 커스텀 메서드는 별도 인터페이스 + 구현체로 분리한다.

```
PlaceRepository : JpaRepository<Place, Long>, PlaceRepositoryCustom
PlaceRepositoryCustom        ← 커스텀 메서드 인터페이스
PlaceRepositoryImpl          ← QueryDSL 구현체
```

Spring Data JPA가 `PlaceRepositoryImpl`을 자동으로 찾아서 주입한다 (네이밍 컨벤션: `{Repository이름}Impl`).

## 동적 쿼리 예시

```kotlin
override fun findAllWithFilters(region: String?, categoryId: Long?, tagId: Long?): List<Place> {
    val query = queryFactory.selectDistinct(place).from(place)

    if (region != null) query.where(place.region.eq(region))
    if (categoryId != null) query.join(place.categories, category).where(category.id.eq(categoryId))
    if (tagId != null) query.join(place.tags, tag).where(tag.id.eq(tagId))

    return query.fetch()
}
```

조건이 없으면 JOIN 자체를 하지 않아 불필요한 쿼리가 발생하지 않는다.
