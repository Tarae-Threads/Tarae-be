# TASKS: Place @ManyToMany → 중간 엔티티 전환

- [x] T1. DB 마이그레이션 SQL 작성
  - 목적: place_categories, place_tags, place_brands 테이블에 id 컬럼 추가 및 PK 전환
  - 작업 내용:
    - `V4__refactor_place_join_tables.sql` 작성
    - 복합 PK 제거 → id AUTO_INCREMENT PK 추가 → UNIQUE 제약 추가
    - 기존 데이터 보존 확인
  - 예상 변경 파일: `docs/db/V4__refactor_place_join_tables.sql`
  - 완료 기준: SQL 파일 작성 완료 (실제 적용은 T6 이후 개발자가 수동 적용)

- [x] T2. 중간 엔티티 3개 생성
  - 목적: PlaceCategory, PlaceTag, PlaceBrand 엔티티로 중간 테이블 명시적 관리
  - 작업 내용:
    - `PlaceCategory.kt` — place: Place, category: Category, id: Long
    - `PlaceTag.kt` — place: Place, tag: Tag, id: Long
    - `PlaceBrand.kt` — place: Place, brand: Brand, id: Long
    - @ManyToOne LAZY, @UniqueConstraint, equals/hashCode id 기반
    - BaseEntity 상속 안 함 (감사 컬럼 불필요)
  - 예상 변경 파일: `place/domain/PlaceCategory.kt`, `PlaceTag.kt`, `PlaceBrand.kt` (신규)
  - 완료 기준: 컴파일 통과, QPlace* Q클래스 자동 생성됨

- [x] T3. Place 엔티티 리팩토링
  - 목적: @ManyToMany 제거, @OneToMany + 도메인 메서드 + 편의 프로퍼티로 전환
  - 작업 내용:
    - `@ManyToMany` 3개 제거, `@BatchSize + @OneToMany(mappedBy, cascade=ALL, orphanRemoval=true)` 3개 추가
    - `addCategory(category)`, `addTag(tag)`, `addBrand(brand)` 도메인 메서드 추가
    - `val categories`, `val tags`, `val brands` 편의 프로퍼티 추가 (DTO 코드 변경 불필요)
  - 예상 변경 파일: `place/domain/Place.kt`
  - 완료 기준: 컴파일 통과, PlaceDetailResponse/PlaceListResponse 변경 없이 동작

- [x] T4. AdminRequestService 리팩토링
  - 목적: 컬렉션 직접 조작 → 도메인 메서드 사용으로 개선
  - 작업 내용:
    - `place.categories.addAll(categories)` → `categories.forEach { place.addCategory(it) }`
    - `place.tags.addAll(tags)` → `tags.forEach { place.addTag(it) }`
    - `place.brands.addAll(brands)` → `brands.forEach { place.addBrand(it) }`
  - 예상 변경 파일: `admin/service/AdminRequestService.kt`
  - 완료 기준: 컴파일 통과

- [x] T5. PlaceRepositoryImpl QueryDSL 수정
  - 목적: 중간 엔티티를 거친 명시적 join 경로로 변경
  - 작업 내용:
    - 카테고리 필터: `place.categories` → `place.placeCategories + placeCategory.category`
    - 태그 필터: `place.tags` → `place.placeTags + placeTag.tag`
    - 키워드 leftJoin: tag/brand 각각 중간 엔티티 경유
    - 사용하지 않는 import 정리
  - 예상 변경 파일: `place/repository/PlaceRepositoryImpl.kt`
  - 완료 기준: 컴파일 통과

- [x] T6. 테스트 수정 및 전체 통과 확인
  - 목적: 중간 엔티티 전환에 따른 테스트 픽스처 수정
  - 작업 내용:
    - `PlaceRepositoryTest` — `place.categories.addAll()` → `place.addCategory()` 등
    - `./gradlew test` 전체 GREEN 확인
  - 예상 변경 파일: `place/repository/PlaceRepositoryTest.kt`
  - 완료 기준: `./gradlew test` BUILD SUCCESSFUL

- [x] T7. 문서 업데이트
  - 목적: 스키마 변경 반영
  - 작업 내용:
    - `docs/architecture/db.md` — place_categories/place_tags/place_brands 테이블에 id 컬럼 추가 반영
  - 예상 변경 파일: `docs/architecture/db.md`
  - 완료 기준: 문서 업데이트 완료
