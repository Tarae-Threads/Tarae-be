---
paths:
  - "src/main/**/domain/**/*.kt"
  - "src/main/**/entity/**/*.kt"
---

# JPA 엔티티 규칙

## BaseEntity 패턴

모든 엔티티는 BaseEntity 상속:

```kotlin
@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity {
    @CreatedDate
    @Column(nullable = false, updatable = false)
    lateinit var createdAt: LocalDateTime

    @LastModifiedDate
    @Column(nullable = false)
    lateinit var updatedAt: LocalDateTime
}
```

## 엔티티 기본 구조

```kotlin
@Entity
@Table(name = "members")
class Member(
    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    var name: String,
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    // 도메인 메서드
    fun changeName(name: String) {
        this.name = name
    }

    // equals/hashCode는 id 기반
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Member) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
```

## 컬렉션 필드

```kotlin
// 참조는 val (재할당 금지), 내용은 MutableList
@OneToMany(mappedBy = "place", cascade = [CascadeType.ALL], orphanRemoval = true)
val placeCategories: MutableList<PlaceCategory> = mutableListOf()
```

- 컬렉션 참조(`val`) — JPA가 프록시로 교체하므로 재할당 불필요
- 서비스/컨트롤러에서 직접 `placeCategories.add(...)` 금지 — 반드시 도메인 메서드 통해서

## 다중 필드 업데이트

여러 필드를 한번에 수정할 때는 단일 도메인 메서드로:

```kotlin
// ❌ 서비스에서 직접 대입
place.name = form.name
place.address = form.address

// ✅ 도메인 메서드로 위임
fun update(form: PlaceCreateForm) {
    name = form.name
    address = form.address
    // ...
}
```

## 규칙

- **data class 사용 금지** — Entity는 일반 class
- **LAZY 기본** — `@OneToMany`, `@ManyToMany` 는 반드시 LAZY
- **EAGER 금지** — 성능 문제 유발
- **`@Column(nullable = false)` 명시** — DB 제약 조건 코드에서도 표현
- **protected no-arg 생성자** — JPA 요구사항 (kotlin-jpa 플러그인이 자동 생성)

## 연관관계 N+1 방지

```kotlin
// Repository에서 fetch join 명시
@Query("SELECT m FROM Member m JOIN FETCH m.orders WHERE m.id = :id")
fun findByIdWithOrders(id: Long): Member?

// 또는 @EntityGraph
@EntityGraph(attributePaths = ["orders"])
fun findById(id: Long): Optional<Member>
```

## 소프트 삭제 (필요시)

```kotlin
@Column(nullable = false)
var isDeleted: Boolean = false
    private set

var deletedAt: LocalDateTime? = null
    private set

fun delete() {
    isDeleted = true
    deletedAt = LocalDateTime.now()
}
```
