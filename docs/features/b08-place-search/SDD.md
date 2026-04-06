# SDD: 장소 키워드 검색

## 기술 스택

- QueryDSL — 기존 `PlaceRepositoryImpl`에 LIKE 조건 추가
- Spring Data JPA — 기존 구조 그대로

## 아키텍처

```
PlaceController
  └─ keyword: String? (RequestParam 추가)
       ↓
PlaceService
  └─ getPlaces(region, categoryId, tagId, keyword)
       ↓
PlaceRepositoryCustom.findAllWithFilters(region, categoryId, tagId, keyword)
       ↓
PlaceRepositoryImpl — QueryDSL LIKE 쿼리
```

## 검색 로직

### 토큰화
```
keyword = "대바늘 서울" → tokens = ["대바늘", "서울"]
```
- 공백 기준 split, 빈 토큰 제거
- keyword가 null 또는 blank면 토큰 없음 → 조건 추가 안 함

### 토큰별 조건 (AND)
각 토큰마다 아래 필드들에 대해 OR 조건, 토큰 간에는 AND:

```
token = "대바늘"
→ (name LIKE '%대바늘%'
   OR district LIKE '%대바늘%'
   OR address LIKE '%대바늘%'
   OR description LIKE '%대바늘%'
   OR ANY tag.name LIKE '%대바늘%'
   OR ANY brand.name LIKE '%대바늘%')
```

### JOIN 전략
- tags, brands: `leftJoin` + `on` 조건으로 JOIN (keyword 있을 때만)
- `selectDistinct`로 중복 제거 (이미 적용 중)

## API 설계

| Method | URL | 변경 내용 |
|--------|-----|---------|
| GET | /api/places | `keyword` 파라미터 추가 |

### Request 파라미터

| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| keyword | String | N | 검색어 (공백 토큰화 AND) |
| region | String | N | 지역 필터 (기존) |
| categoryId | Long | N | 카테고리 ID (기존) |
| tagId | Long | N | 태그 ID (기존) |

### Response

기존 `PlaceListResponse` 그대로 사용.

## 변경 파일

| 파일 | 변경 내용 |
|------|---------|
| `PlaceRepositoryCustom.kt` | `keyword` 파라미터 추가 |
| `PlaceRepositoryImpl.kt` | LIKE 쿼리 로직 추가 |
| `PlaceService.kt` | `keyword` 파라미터 전달 |
| `PlaceController.kt` | `keyword` RequestParam 추가 |

## 테스트 전략

- **Repository 통합 테스트**: `@DataJpaTest` + QueryDSL, 실제 쿼리 검증
  - keyword 단일 토큰 검색
  - keyword 복수 토큰 AND 검색
  - keyword + region 복합 필터
  - keyword 없을 때 전체 반환
  - 태그명/브랜드명으로 검색
- **Service 단위 테스트**: MockK, repository mock으로 파라미터 전달 검증
