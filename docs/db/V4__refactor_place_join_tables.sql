-- ============================================================
-- V4__refactor_place_join_tables.sql
-- @ManyToMany 중간 엔티티 전환: 복합PK → 단일 PK(id) 추가
-- 로컬: ✅ 2026-04-11
-- 운영: ✅ 2026-04-11
-- ============================================================

-- place_categories: 복합 PK 제거 → id 추가 → unique 제약
ALTER TABLE place_categories DROP PRIMARY KEY;
ALTER TABLE place_categories
    ADD COLUMN id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '고유 식별자' FIRST;
ALTER TABLE place_categories
    ADD UNIQUE KEY uk_place_category (place_id, category_id);

-- place_tags: 복합 PK 제거 → id 추가 → unique 제약
ALTER TABLE place_tags DROP PRIMARY KEY;
ALTER TABLE place_tags
    ADD COLUMN id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '고유 식별자' FIRST;
ALTER TABLE place_tags
    ADD UNIQUE KEY uk_place_tag (place_id, tag_id);

-- place_brands: 복합 PK 제거 → id 추가 → unique 제약
ALTER TABLE place_brands DROP PRIMARY KEY;
ALTER TABLE place_brands
    ADD COLUMN id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '고유 식별자' FIRST;
ALTER TABLE place_brands
    ADD UNIQUE KEY uk_place_brand (place_id, brand_id);
