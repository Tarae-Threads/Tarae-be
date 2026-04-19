---
paths:
  - "src/main/resources/db/migration/**/*.sql"
---

# DB 스키마 컨벤션

## SQL 파일 위치

`src/main/resources/db/migration/` — Flyway 형식으로 관리하되, **수동 적용** (Flyway 미사용)

파일 헤더에 로컬 적용 여부 기록:
```sql
-- 로컬: ✅ 2026-04-05   (적용 완료)
-- 로컬: ⬜ (미적용)      (아직 미적용)
```

## 컬럼 코멘트 — 필수

모든 컬럼에 `COMMENT '설명'` 필수. 테이블에도 테이블 코멘트 필수.

```sql
CREATE TABLE places
(
    id     BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '장소 고유 식별자',
    name   VARCHAR(100) NOT NULL             COMMENT '장소명',
    status ENUM('OPEN','CLOSED') NOT NULL    COMMENT '운영 상태 (OPEN: 운영중, CLOSED: 폐업, RELOCATED: 이전)'
) COMMENT '뜨개 관련 장소 정보';
```

## 스키마 변경 규칙

### 새 도메인 / 테이블 추가
- 새 버전 SQL 파일 생성: `V{N}__create_{domain}_tables.sql`
- `docs/architecture/db.md` 테이블 섹션 추가
- `docs/architecture/overview.md` 도메인 섹션 추가

### 컬럼 추가
- 새 버전 SQL 파일 생성: `V{N}__add_{column}_to_{table}.sql`
- `docs/architecture/db.md` 해당 테이블 업데이트

```sql
-- V4__add_phone_to_places.sql
ALTER TABLE places
    ADD COLUMN phone VARCHAR(20) COMMENT '전화번호' AFTER naver_map_url;
```

### 컬럼 수정
- 새 버전 SQL 파일 생성: `V{N}__modify_{column}_in_{table}.sql`

```sql
-- V5__modify_name_length_in_places.sql
ALTER TABLE places
    MODIFY COLUMN name VARCHAR(200) NOT NULL COMMENT '장소명';
```

### 컬럼 삭제
- 새 버전 SQL 파일 생성: `V{N}__drop_{column}_from_{table}.sql`
- 삭제 전 `docs/architecture/db.md`에서 해당 컬럼 제거

```sql
-- V6__drop_website_url_from_places.sql
ALTER TABLE places
    DROP COLUMN website_url;
```

## 네이밍 규칙

| 항목 | 규칙 | 예시 |
|------|------|------|
| 테이블명 | snake_case 복수형 | `places`, `place_categories` |
| 컬럼명 | snake_case | `naver_map_url`, `created_at` |
| PK | `id BIGINT AUTO_INCREMENT` | |
| FK | `{참조테이블_단수}_id` | `place_id`, `category_id` |
| ENUM | 대문자 | `ENUM('OPEN', 'CLOSED')` |
| 공통 컬럼 | `created_at`, `updated_at` (BaseEntity) | |
