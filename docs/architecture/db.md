# DB 스키마

> 스키마 변경 시 업데이트

## 설계 원칙

- PK: BIGINT AUTO_INCREMENT (`GenerationType.IDENTITY`)
- 공통 컬럼: `created_at`, `updated_at` (BaseEntity, JPA Auditing)
- 외래키 네이밍: `{참조테이블}_id`
- Soft Delete 필요 시: `is_deleted`, `deleted_at`

## SQL 파일 위치

마이그레이션 SQL은 `docs/db/` 에서 관리 (수동 적용, Flyway 미사용):

| 파일 | 내용 |
|------|------|
| `V1__create_place_tables.sql` | places, categories, tags, brands, 매핑 테이블 |
| `V2__create_event_tables.sql` | events |
| `V3__create_request_tables.sql` | place_requests, event_requests |
| `V4__refactor_place_join_tables.sql` | 매핑 테이블 id 컬럼 추가 (PK 전환) |

---

## 테이블 목록

### places (장소)

| 컬럼 | 타입 | 비고 |
|------|------|------|
| id | BIGINT AUTO_INCREMENT | PK |
| name | VARCHAR(100) | |
| region | VARCHAR(50) | 서울, 경기 등 |
| district | VARCHAR(100) | 성수, 홍대 등 |
| address | VARCHAR(255) | |
| lat | DECIMAL(10,7) | nullable |
| lng | DECIMAL(10,7) | nullable |
| hours_text | VARCHAR(255) | nullable. "화~금 10:00-19:00, 토~일 09:00-19:00" |
| closed_days | VARCHAR(100) | nullable. "월요일, 화요일" |
| description | TEXT | nullable |
| instagram_url | VARCHAR(255) | nullable |
| website_url | VARCHAR(255) | nullable |
| naver_map_url | VARCHAR(255) | nullable |
| status | ENUM(OPEN, CLOSED, RELOCATED) | DEFAULT OPEN |
| created_at | DATETIME | BaseEntity |
| updated_at | DATETIME | BaseEntity |

### categories (카테고리)

| 컬럼 | 타입 | 비고 |
|------|------|------|
| id | BIGINT AUTO_INCREMENT | PK |
| name | VARCHAR(50) | 뜨개샵, 공방, 뜨개카페, 손염색실, 공예용품점 |

### place_categories (장소-카테고리 매핑)

| 컬럼 | 타입 | 비고 |
|------|------|------|
| id | BIGINT AUTO_INCREMENT | PK |
| place_id | BIGINT | FK → places |
| category_id | BIGINT | FK → categories |
| UNIQUE | (place_id, category_id) | 중복 방지 |

> 한 장소가 여러 카테고리를 가질 수 있음 (뜨개샵 + 공방 등)

### tags (태그)

| 컬럼 | 타입 | 비고 |
|------|------|------|
| id | BIGINT AUTO_INCREMENT | PK |
| name | VARCHAR(50) | |

### place_tags (장소-태그 매핑)

| 컬럼 | 타입 | 비고 |
|------|------|------|
| id | BIGINT AUTO_INCREMENT | PK |
| place_id | BIGINT | FK → places |
| tag_id | BIGINT | FK → tags |
| UNIQUE | (place_id, tag_id) | 중복 방지 |

### brands (브랜드)

| 컬럼 | 타입 | 비고 |
|------|------|------|
| id | BIGINT AUTO_INCREMENT | PK |
| name | VARCHAR(100) | 산네스간, 히야히야 등 |
| type | ENUM(YARN, NEEDLE, NOTIONS) | 실, 바늘, 부자재 |

### place_brands (장소-브랜드 매핑)

| 컬럼 | 타입 | 비고 |
|------|------|------|
| id | BIGINT AUTO_INCREMENT | PK |
| place_id | BIGINT | FK → places |
| brand_id | BIGINT | FK → brands |
| UNIQUE | (place_id, brand_id) | 중복 방지 |

### events (일정)

| 컬럼 | 타입 | 비고 |
|------|------|------|
| id | BIGINT AUTO_INCREMENT | PK |
| title | VARCHAR(200) | |
| description | TEXT | nullable |
| event_type | ENUM(TESTER_RECRUIT, SALE, EVENT_POPUP) | |
| start_date | DATE | |
| end_date | DATE | nullable |
| place_id | BIGINT | FK → places, nullable (기존 장소 연결) |
| location_text | VARCHAR(255) | nullable (임시 장소 텍스트) |
| lat | DECIMAL(10,7) | nullable |
| lng | DECIMAL(10,7) | nullable |
| links | JSON | nullable. {"instagram":"...", "website":"..."} |
| active | BOOLEAN | DEFAULT true |
| created_at | DATETIME | BaseEntity |
| updated_at | DATETIME | BaseEntity |

### place_requests (장소 등록 요청)

| 컬럼 | 타입 | 비고 |
|------|------|------|
| id | BIGINT AUTO_INCREMENT | PK |
| request_type | ENUM(NEW, UPDATE) | 새 장소 / 기존 장소 업데이트 |
| place_id | BIGINT | FK → places, UPDATE일 때만 |
| name | VARCHAR(100) | nullable |
| address | VARCHAR(255) | nullable |
| address_detail | VARCHAR(100) | nullable |
| lat | DECIMAL(10,7) | nullable, 프론트 geocoding 후 전송 |
| lng | DECIMAL(10,7) | nullable |
| category_ids | JSON | nullable. `[1, 3]` |
| hours_text | VARCHAR(255) | nullable |
| closed_days | VARCHAR(100) | nullable |
| brands_yarn | VARCHAR(255) | nullable, 쉼표 구분 |
| brands_needle | VARCHAR(255) | nullable, 쉼표 구분 |
| brands_notions | VARCHAR(255) | nullable, 쉼표 구분 |
| instagram_url | VARCHAR(255) | nullable |
| website_url | VARCHAR(255) | nullable |
| naver_map_url | VARCHAR(255) | nullable |
| tags | VARCHAR(500) | nullable, 쉼표 구분 |
| note | TEXT | nullable |
| status | ENUM(PENDING, APPROVED, REJECTED) | DEFAULT PENDING |
| created_at | DATETIME | BaseEntity |
| updated_at | DATETIME | BaseEntity |

### event_requests (이벤트 등록 요청)

| 컬럼 | 타입 | 비고 |
|------|------|------|
| id | BIGINT AUTO_INCREMENT | PK |
| title | VARCHAR(200) | |
| event_type | ENUM(TESTER_RECRUIT, SALE, EVENT_POPUP) | |
| start_date | DATE | |
| end_date | DATE | nullable |
| location_text | VARCHAR(255) | nullable |
| description | TEXT | nullable |
| status | ENUM(PENDING, APPROVED, REJECTED) | DEFAULT PENDING |
| created_at | DATETIME | BaseEntity |
| updated_at | DATETIME | BaseEntity |

---

## ERD (간략)

```
places ──< place_categories >── categories
places ──< place_tags >── tags
places ──< place_brands >── brands
places ──o< events
place_requests (독립)
event_requests (독립)
```
