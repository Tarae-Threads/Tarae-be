# TASKS: 장소 키워드 검색

- [x] T1. PlaceRepositoryCustom 인터페이스 keyword 파라미터 추가 (완료일: 2026-04-06)
  - 목적: 인터페이스-구현체 시그니처 일치
  - 작업 내용: `findAllWithFilters`에 `keyword: String?` 추가
  - 예상 변경 파일: `PlaceRepositoryCustom.kt`
  - 완료 기준: 컴파일 통과

- [x] T2. PlaceRepositoryImpl LIKE 검색 쿼리 구현 (완료일: 2026-04-06)
  - 목적: 핵심 검색 로직
  - 작업 내용:
    - keyword를 공백으로 split → token 목록
    - 각 token: name/district/address/description LIKE OR tags.name LIKE OR brands.name LIKE
    - token 간 AND 조건
    - tags, brands leftJoin (keyword 있을 때)
  - 예상 변경 파일: `PlaceRepositoryImpl.kt`
  - 완료 기준: Repository 통합 테스트 통과

- [x] T3. PlaceService keyword 파라미터 추가 (완료일: 2026-04-06)
  - 목적: 레이어 간 파라미터 전달
  - 작업 내용: `getPlaces`에 `keyword: String?` 추가 후 repository로 전달
  - 예상 변경 파일: `PlaceService.kt`
  - 완료 기준: Service 단위 테스트 통과

- [x] T4. PlaceController keyword RequestParam 추가 (완료일: 2026-04-06)
  - 목적: API 엔드포인트에 keyword 노출
  - 작업 내용: `@RequestParam keyword: String?` 추가, Swagger `@Parameter` 문서화
  - 예상 변경 파일: `PlaceController.kt`
  - 완료 기준: `GET /api/places?keyword=대바늘` 정상 응답
