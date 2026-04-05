# 코틀린 생성자 vs 자바 빌더 패턴
- 작성일: 2026-04-05

## 자바에서 빌더를 쓰는 이유

파라미터가 많거나 일부만 선택적으로 넣고 싶을 때 가독성 문제를 해결하기 위해 빌더 패턴을 사용했다.

```java
// 뭐가 뭔지 알 수 없음
new Place("실과바늘", null, null, "서울", true, false);

// 빌더로 해결
Place.builder().name("실과바늘").region("서울").build();
```

## 코틀린은 빌더가 필요 없다

### 1. Named Argument — 파라미터 이름 명시

```kotlin
// 어떤 값인지 바로 알 수 있음
Place(name = "실과바늘", region = "서울")
```

### 2. 기본값 (Default Parameter) — 선택적 파라미터

```kotlin
class Place(
    val name: String,
    val region: String = "미정",   // 기본값 지정
    val lat: BigDecimal? = null,   // nullable = 생략 가능
)

// 일부만 넣어도 됨
Place(name = "실과바늘")  // region = "미정", lat = null 자동 적용
```

### 3. 주 생성자 (Primary Constructor)

클래스 이름 바로 옆에 생성자를 붙여 쓰고, `val`/`var`를 붙이면 필드 선언까지 한 번에 된다.

```kotlin
class Brand(        // ← 여기가 주 생성자
    val name: String,   // 파라미터 받으면서 동시에 필드 선언
    val type: BrandType,
)
```

자바로 풀면:
```java
public class Brand {
    private final String name;
    private final BrandType type;

    public Brand(String name, BrandType type) {
        this.name = name;
        this.type = type;
    }
    public String getName() { return name; }
    public BrandType getType() { return type; }
}
```

## 결론

코틀린에서는 named argument + 기본값으로 빌더의 필요성이 사라진다.
실무 코틀린 코드에서 빌더 패턴은 거의 사용하지 않는다.
