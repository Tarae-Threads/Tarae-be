# SDD: Place @ManyToMany → 중간 엔티티 전환

## 기술 스택
- Kotlin + Spring Boot + JPA (Hibernate)
- QueryDSL 5.1.0
- Flyway (수동 적용)

## 아키텍처

변경되는 레이어:
```
AdminRequestService  (place.addCategory() 도메인 메서드 사용)
       ↓
Place (domain)       (@OneToMany placeCategories/placeTags/placeBrands + 편의 프로퍼티)
       ↓
PlaceCategory/PlaceTag/PlaceBrand (신규 중간 엔티티)
       ↓
PlaceRepositoryImpl  (QueryDSL join 경로 변경)
```

DTO/Controller/Service는 변경 없음.

## 도메인 모델

### 신규 중간 엔티티 (3개 동일 패턴)

```kotlin
@Entity
@Table(
    name = "place_categories",
    uniqueConstraints = [UniqueConstraint(columnNames = ["place_id", "category_id"])]
)
class PlaceCategory(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    val place: Place,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    val category: Category,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    override fun equals(other: Any?): Boolean { ... }  // id 기반
    override fun hashCode(): Int = id.hashCode()
}
```

PlaceTag(tag: Tag), PlaceBrand(brand: Brand) 동일 패턴.

### Place 엔티티 변경

```kotlin
// 제거
@BatchSize(size = 100)
@ManyToMany(fetch = FetchType.LAZY)
@JoinTable(name = "place_categories", ...)
val categories: MutableList<Category>

// 추가
@BatchSize(size = 100)
@OneToMany(mappedBy = "place", cascade = [CascadeType.ALL], orphanRemoval = true)
val placeCategories: MutableList<PlaceCategory> = mutableListOf()

// 도메인 메서드
fun addCategory(category: Category) {
    placeCategories.add(PlaceCategory(place = this, category = category))
}
fun addTag(tag: Tag) { ... }
fun addBrand(brand: Brand) { ... }

// 편의 프로퍼티 (DTO 코드 변경 불필요)
val categories: List<Category> get() = placeCategories.map { it.category }
val tags: List<Tag> get() = placeTags.map { it.tag }
val brands: List<Brand> get() = placeBrands.map { it.brand }
```

`cascade = ALL + orphanRemoval = true` → Place 저장 시 중간 엔티티도 함께 저장/삭제.

## DB 설계

### 마이그레이션 (V4)

기존 복합 PK → 단일 PK(id) 전환. 데이터 보존.

```sql
-- place_categories
ALTER TABLE place_categories DROP PRIMARY KEY;
ALTER TABLE place_categories ADD COLUMN id BIGINT AUTO_INCREMENT PRIMARY KEY FIRST;
ALTER TABLE place_categories ADD UNIQUE KEY uk_place_category (place_id, category_id);

-- place_tags (동일 패턴)
-- place_brands (동일 패턴)
```

### 변경 후 테이블 구조

| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | BIGINT AUTO_INCREMENT PK | 신규 |
| place_id | BIGINT FK | 기존 |
| category_id / tag_id / brand_id | BIGINT FK | 기존 |
| UNIQUE (place_id, category_id) | | 복합 PK 대체 |

## QueryDSL 변경

```kotlin
// AS-IS
query.join(place.categories, category)
    .where(category.id.eq(categoryId))

// TO-BE
val placeCategory = QPlaceCategory.placeCategory
query.join(place.placeCategories, placeCategory)
    .join(placeCategory.category, category)
    .where(category.id.eq(categoryId))

// 키워드 leftJoin (AS-IS)
query.leftJoin(place.tags, searchTag)
query.leftJoin(place.brands, searchBrand)

// 키워드 leftJoin (TO-BE)
val searchPlaceTag = QPlaceTag("searchPlaceTag")
val searchPlaceBrand = QPlaceBrand("searchPlaceBrand")
query.leftJoin(place.placeTags, searchPlaceTag)
    .leftJoin(searchPlaceTag.tag, searchTag)
query.leftJoin(place.placeBrands, searchPlaceBrand)
    .leftJoin(searchPlaceBrand.brand, searchBrand)
```

## 테스트 전략

- **단위 테스트 불필요**: 리팩토링이므로 기존 테스트가 통과하면 충분
- **수정 필요한 테스트**:
  - `PlaceRepositoryTest` — `place.categories.addAll()` → `place.addCategory()` 로 픽스처 수정
- **통과 기준**: 기존 테스트 전체 GREEN
