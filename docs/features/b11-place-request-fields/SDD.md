# SDD: 제보 폼 필드 확장

## 기술 스택

- Kotlin / Spring Boot
- JPA (Hibernate) — `LongListJsonConverter` 재사용
- MySQL — JSON 컬럼

## 아키텍처

변경 레이어: DTO → Entity → DB 컬럼
Controller/Service 로직 변경 없음.

```
PlaceRequestInput (DTO) → PlaceRequest (Entity) → place_requests (DB)
```

## 도메인 모델

### PlaceRequest 엔티티 변경

| 필드 | 타입 | 신규/기존 | 설명 |
|------|------|-----------|------|
| `categoryText` | `String?` | **신규** | 카테고리 직접 입력 |
| `brandYarnIds` | `List<Long>` | **신규** | 실 브랜드 ID 목록 |
| `brandNeedleIds` | `List<Long>` | **신규** | 바늘 브랜드 ID 목록 |
| `brandNotionsIds` | `List<Long>` | **신규** | 부자재 브랜드 ID 목록 |
| `brandPatternbookIds` | `List<Long>` | **신규** | 도안 브랜드 ID 목록 |
| `brandsPatternbook` | `String?` | **신규** | 도안 브랜드 직접 입력 |
| `brandsYarn` | `String?` | 기존 유지 | 실 브랜드 직접 입력 |
| `brandsNeedle` | `String?` | 기존 유지 | 바늘 브랜드 직접 입력 |
| `brandsNotions` | `String?` | 기존 유지 | 부자재 브랜드 직접 입력 |

## DB 설계

`place_requests` 테이블 컬럼 추가:

```sql
ALTER TABLE place_requests
    ADD COLUMN category_text    VARCHAR(255)    NULL,
    ADD COLUMN brand_yarn_ids   JSON            NULL,
    ADD COLUMN brand_needle_ids JSON            NULL,
    ADD COLUMN brand_notions_ids JSON           NULL,
    ADD COLUMN brand_patternbook_ids JSON       NULL,
    ADD COLUMN brands_patternbook VARCHAR(255)  NULL;
```

## API 설계

기존 엔드포인트 유지, Request Body만 확장:

**POST /api/requests/places**

```json
{
  "requestType": "NEW",
  "categoryIds": [1, 2],
  "categoryText": "뜨개카페",
  "brandYarnIds": [10, 11],
  "brandsYarn": "직접입력한실브랜드",
  "brandNeedleIds": [20],
  "brandsNeedle": null,
  "brandNotionsIds": [],
  "brandsNotions": null,
  "brandPatternbookIds": [30],
  "brandsPatternbook": "직접입력한도안브랜드"
}
```

## 예외 처리

추가 없음 — 모든 필드 optional, 기존 validation 유지.

## 테스트 전략

- 단위 테스트: `PlaceRequestInput.toEntity()` — 새 필드 매핑 확인
- 통합 테스트: POST /api/requests/places — 새 필드 포함/미포함 각각 저장 확인
