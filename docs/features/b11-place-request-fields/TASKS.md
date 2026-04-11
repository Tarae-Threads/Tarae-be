# TASKS: 제보 폼 필드 확장

- [x] T1. PlaceRequest 엔티티에 새 필드 추가 (완료일: 2026-04-12)
  - 목적: DB에 신규 컬럼 반영
  - 작업 내용:
    - `PlaceRequest`에 `categoryText`, `brandYarnIds`, `brandNeedleIds`, `brandNotionsIds`, `brandPatternbookIds`, `brandsPatternbook` 추가
    - ID 리스트 필드는 `@Convert(converter = LongListJsonConverter::class)` + `@Column(columnDefinition = "JSON")` 적용
  - 예상 변경 파일: `PlaceRequest.kt`
  - 완료 기준: 엔티티 컴파일 성공, `LongListJsonConverter` 재사용 확인

- [x] T2. PlaceRequestInput DTO에 새 필드 추가 및 toEntity() 매핑 (완료일: 2026-04-12)
  - 목적: API 요청으로 새 필드 수신
  - 작업 내용:
    - `PlaceRequestInput`에 동일 필드 추가
    - `toEntity()`에서 새 필드 매핑
  - 예상 변경 파일: `PlaceRequestInput.kt`
  - 완료 기준: `toEntity()` 단위 테스트 통과 (새 필드 포함/미포함 케이스)

- [x] T3. DB 마이그레이션 작성 (완료일: 2026-04-12)
  - 목적: `place_requests` 테이블에 신규 컬럼 추가
  - 작업 내용: Flyway 마이그레이션 파일 또는 `schema.sql` 업데이트 (프로젝트 방식 확인 후 적용)
  - 예상 변경 파일: `schema.sql` 또는 `V{N}__place_request_fields.sql`
  - 완료 기준: 스키마 적용 후 통합 테스트 (POST /api/requests/places) 통과

- [x] T4. 아키텍처 문서 업데이트 (완료일: 2026-04-12)
  - 목적: docs 동기화
  - 작업 내용: `docs/architecture/db.md`의 `place_requests` 테이블 컬럼 목록 업데이트
  - 예상 변경 파일: `docs/architecture/db.md`
  - 완료 기준: 문서에 신규 컬럼 반영
