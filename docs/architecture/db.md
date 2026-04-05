# DB 스키마

> 스키마 변경 시 업데이트

## 설계 원칙

- PK: BIGINT AUTO_INCREMENT (`GenerationType.IDENTITY`)
- 공통 컬럼: `created_at`, `updated_at` (BaseEntity, JPA Auditing)
- 외래키 네이밍: `{참조테이블}_id`
- Soft Delete 필요 시: `is_deleted`, `deleted_at`

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
| place_id | BIGINT | FK → places |
| category_id | BIGINT | FK → categories |

> 한 장소가 여러 카테고리를 가질 수 있음 (뜨개샵 + 공방 등)

### tags (태그)

| 컬럼 | 타입 | 비고 |
|------|------|------|
| id | BIGINT AUTO_INCREMENT | PK |
| name | VARCHAR(50) | |

### place_tags (장소-태그 매핑)

| 컬럼 | 타입 | 비고 |
|------|------|------|
| place_id | BIGINT | FK → places |
| tag_id | BIGINT | FK → tags |

### brands (브랜드)

| 컬럼 | 타입 | 비고 |
|------|------|------|
| id | BIGINT AUTO_INCREMENT | PK |
| name | VARCHAR(100) | 산네스간, 히야히야 등 |
| type | ENUM(YARN, NEEDLE, NOTIONS) | 실, 바늘, 부자재 |

### place_brands (장소-브랜드 매핑)

| 컬럼 | 타입 | 비고 |
|------|------|------|
| place_id | BIGINT | FK → places |
| brand_id | BIGINT | FK → brands |

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

### place_submissions (장소 제보)

| 컬럼 | 타입 | 비고 |
|------|------|------|
| id | BIGINT AUTO_INCREMENT | PK |
| submission_type | ENUM(NEW, UPDATE) | 새 장소 / 기존 장소 업데이트 |
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

### event_submissions (일정 제보)

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
place_submissions (독립)
event_submissions (독립)
```
