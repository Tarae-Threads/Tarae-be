-- V5: place_requests 테이블에 카테고리 텍스트 및 브랜드 ID 리스트 필드 추가
-- 배경: 제보 폼에서 목록 선택(ID) + 직접 입력(텍스트) 동시 지원, PATTERNBOOK 브랜드 타입 추가
-- 로컬: ✅ 2026-04-12
-- 운영: ✅ 2026-04-12

ALTER TABLE place_requests
    ADD COLUMN category_text         VARCHAR(255) NULL AFTER category_ids,
    ADD COLUMN brand_yarn_ids        JSON         NULL AFTER brands_yarn,
    ADD COLUMN brand_needle_ids      JSON         NULL AFTER brands_needle,
    ADD COLUMN brand_notions_ids     JSON         NULL AFTER brands_notions,
    ADD COLUMN brand_patternbook_ids JSON         NULL,
    ADD COLUMN brands_patternbook    VARCHAR(255) NULL;

-- brands 테이블 type enum에 PATTERNBOOK 추가
ALTER TABLE brands
    MODIFY COLUMN type ENUM('YARN', 'NEEDLE', 'NOTIONS', 'PATTERNBOOK') NOT NULL;
