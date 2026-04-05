---

> **TASKS 란?**
> SDD를 실제 작업 단위로 쪼갠 체크리스트.
> 한 Task = 한 번에 구현하고 테스트까지 완료할 수 있는 단위.
> 승인 후 이 순서대로 TDD(테스트 먼저 → 구현)로 진행.

---

# TASKS: B-02 Place 도메인
- 작성일: 2026-04-05

- [x] T1. DDL 작성 및 로컬 DB 적용
  - 목적: 장소 관련 테이블을 실제 DB에 생성
  - 작업 내용:
    - `src/main/resources/db/schema.sql` 작성 (places, categories, place_categories, tags, place_tags, brands, place_brands)
    - 로컬 MySQL에 수동 적용
  - 예상 변경 파일:
    - `src/main/resources/db/schema.sql` (신규)
  - 완료 기준: 로컬 DB에 테이블 생성 확인
  - 완료일: 2026-04-05

- [x] T2. Place 도메인 Entity 구현
  - 목적: JPA Entity 정의
  - 작업 내용:
    - `Place.kt`, `PlaceStatus.kt`
    - `Category.kt`, `PlaceCategory.kt`
    - `Tag.kt`, `PlaceTag.kt`
    - `Brand.kt`, `BrandType.kt`, `PlaceBrand.kt`
  - 예상 변경 파일:
    - `src/main/kotlin/.../place/domain/` 하위 신규 파일들
  - 완료 기준: 빌드 성공, 앱 기동 시 Entity 인식 확인
  - 완료일: 2026-04-05

- [x] T3. PlaceRepository 구현
  - 목적: 목록 필터 쿼리 구현 및 검증
  - 작업 내용:
    - `PlaceRepository.kt` (Spring Data JPA)
    - 필터 쿼리: region, categoryId, tagId (JPQL 또는 QueryDSL)
  - 예상 변경 파일:
    - `src/main/kotlin/.../place/repository/PlaceRepository.kt` (신규)
    - `src/test/kotlin/.../place/repository/PlaceRepositoryTest.kt` (신규)
  - 완료 기준:
    - region 필터 쿼리 테스트 통과
    - categoryId 필터 쿼리 테스트 통과
    - tagId 필터 쿼리 테스트 통과
  - 완료일: 2026-04-05

- [ ] T4. PlaceService 구현
  - 목적: 비즈니스 로직 처리
  - 작업 내용:
    - `PlaceService.kt`
    - 목록 조회 (필터 파라미터 전달)
    - 상세 조회 (없는 id → PLACE_NOT_FOUND 예외)
    - `ErrorCode`에 `PLACE_NOT_FOUND` 추가
  - 예상 변경 파일:
    - `src/main/kotlin/.../place/service/PlaceService.kt` (신규)
    - `src/main/kotlin/.../global/exception/ErrorCode.kt` (PLACE_NOT_FOUND 추가)
    - `src/test/kotlin/.../place/service/PlaceServiceTest.kt` (신규)
  - 완료 기준:
    - 목록 조회 서비스 테스트 통과
    - 없는 id 예외 처리 테스트 통과
  - 완료일: -

- [ ] T5. PlaceController + DTO 구현
  - 목적: API 엔드포인트 노출
  - 작업 내용:
    - `PlaceListResponse.kt`, `PlaceDetailResponse.kt`
    - `PlaceController.kt` (`GET /api/places`, `GET /api/places/{id}`)
  - 예상 변경 파일:
    - `src/main/kotlin/.../place/dto/` 하위 신규 파일들
    - `src/main/kotlin/.../place/controller/PlaceController.kt` (신규)
    - `src/test/kotlin/.../place/controller/PlaceControllerTest.kt` (신규)
  - 완료 기준:
    - `GET /api/places` 200 + 응답 필드 검증 테스트 통과
    - `GET /api/places/{id}` 200 + 응답 필드 검증 테스트 통과
    - `GET /api/places/999` 404 테스트 통과
  - 완료일: -
