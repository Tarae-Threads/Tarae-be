---
paths:
  - "src/main/**/repository/**/*.kt"
---

# Repository 규칙

## 구조 패턴

단순 조회는 Spring Data JPA, 동적/복잡 쿼리는 QueryDSL:

```
XxxRepository        — JpaRepository + XxxRepositoryCustom 상속
XxxRepositoryCustom  — 커스텀 메서드 인터페이스
XxxRepositoryImpl    — QueryDSL 구현체
```

```kotlin
interface PlaceRepository : JpaRepository<Place, Long>, PlaceRepositoryCustom

interface PlaceRepositoryCustom {
    fun findAllWithFilters(region: String?, categoryId: Long?): List<Place>
}

class PlaceRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : PlaceRepositoryCustom { ... }
```

## Spring Data JPA 네이밍

```kotlin
fun findByRegion(region: String): List<Place>
fun existsByName(name: String): Boolean
fun countByActive(active: Boolean): Long
fun findByIdOrNull(id: Long): Place?   // null 반환 시 OrNull suffix
```

## QueryDSL 조건 분리

조건식은 private 메서드로 분리:

```kotlin
private fun tokenCondition(token: String): BooleanExpression =
    place.name.containsIgnoreCase(token)
        .or(place.address.containsIgnoreCase(token))
```

## count 쿼리

`fetch().size` 금지 — 전체 데이터를 메모리에 올림:

```kotlin
// ❌
queryFactory.selectFrom(entity).fetch().size.toLong()

// ✅
queryFactory.select(entity.count()).from(entity).where(...).fetchOne() ?: 0L
```

## Page vs Slice

- 총 개수가 필요한 관리자 목록 → `Page`
- 무한 스크롤 / 다음 페이지 여부만 필요 → `Slice`

Page 구성 시 content 쿼리와 count 쿼리 분리:

```kotlin
val content = queryFactory.selectFrom(entity)...fetch()
val total = queryFactory.select(entity.count()).from(entity).where(...).fetchOne() ?: 0L
return PageImpl(content, pageable, total)
```

## N+1 방지

- 연관 엔티티 함께 로딩: `join fetch` + `selectDistinct`
- 컬렉션 다건 조회: `@BatchSize(size = 100)` (Entity에 선언)
- 단건 상세 조회: `join fetch`로 한 방에
