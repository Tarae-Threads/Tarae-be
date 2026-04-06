# PRD: 장소 키워드 검색

- 작성일: 2026-04-06
- 상태: Done
- 완료일: 2026-04-06

## 배경 & 목적

프론트엔드가 현재 `places.json` 전체를 로드한 뒤 클라이언트에서 JS로 필터링 중이다.
데이터가 늘어날수록 초기 로딩이 무거워지고, 서버가 검색 제어권을 갖지 못하는 구조다.
서버 사이드 키워드 검색 API를 제공해 이를 해결한다.

## 요구사항

### 기능 요구사항

- [ ] `GET /api/places`에 `keyword` 쿼리 파라미터 추가
- [ ] keyword가 있으면 다음 필드에 대해 포함 검색 수행:
  - 장소명(`name`), 세부지역(`district`), 주소(`address`), 설명(`description`)
  - 태그명(`tags.name`), 브랜드명(`brands.name`)
- [ ] keyword를 공백 기준으로 토큰화해 각 토큰을 AND 조건으로 적용
  - 예: `"대바늘 서울"` → `대바늘` 포함 AND `서울` 포함
- [ ] keyword가 없으면 기존 동작 유지 (전체 반환)
- [ ] 기존 `region`, `categoryId`, `tagId` 필터와 AND로 조합 가능

### 비기능 요구사항

- N+1 방지: tags, brands JOIN FETCH로 한 번에 조회
- 빈 문자열 keyword는 null과 동일하게 처리 (전체 반환)

## 가정 & 미확정 사항

- 가정: 현재 데이터 규모(수백~수천 건)에서 LIKE 검색으로 충분
- 미확정: `categoryId` (Long) vs `category` slug 문자열 — 프론트는 `yarn_store` 같은 슬러그를 사용 중. 현재 스펙에서는 기존 `categoryId` 유지, 별도 백로그로 분리

## 범위 외

- Full-text search, Elasticsearch 도입
- 이벤트 검색 (별도 백로그)
- `categoryId` → slug 전환
