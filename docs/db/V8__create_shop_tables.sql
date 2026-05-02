-- ============================================================
-- V8__create_shop_tables.sql
-- 온라인샵 도메인 추가
-- 로컬:
-- 운영:
-- ============================================================

CREATE TABLE shops
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '온라인샵 고유 식별자',
    name          VARCHAR(100) NOT NULL              COMMENT '온라인샵명',
    instagram_url VARCHAR(255)                       COMMENT '인스타그램 URL',
    naver_url     VARCHAR(255)                       COMMENT '네이버 스마트스토어 URL',
    website_url   VARCHAR(255)                       COMMENT '웹사이트 URL',
    active        BOOLEAN NOT NULL DEFAULT TRUE      COMMENT '활성화 여부',
    created_at    DATETIME NOT NULL                  COMMENT '생성일시',
    updated_at    DATETIME NOT NULL                  COMMENT '수정일시'
) COMMENT '뜨개 관련 온라인샵 정보';

CREATE TABLE shop_categories
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '고유 식별자',
    shop_id     BIGINT NOT NULL                   COMMENT '온라인샵 ID (FK → shops)',
    category_id BIGINT NOT NULL                   COMMENT '카테고리 ID (FK → categories)',
    UNIQUE KEY uk_shop_category (shop_id, category_id),
    FOREIGN KEY (shop_id)     REFERENCES shops (id),
    FOREIGN KEY (category_id) REFERENCES categories (id),
    INDEX idx_shop_categories_shop_id (shop_id),
    INDEX idx_shop_categories_category_id (category_id)
) COMMENT '온라인샵-카테고리 매핑 (N:M)';

CREATE TABLE shop_tags
(
    id      BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '고유 식별자',
    shop_id BIGINT NOT NULL                   COMMENT '온라인샵 ID (FK → shops)',
    tag_id  BIGINT NOT NULL                   COMMENT '태그 ID (FK → tags)',
    UNIQUE KEY uk_shop_tag (shop_id, tag_id),
    FOREIGN KEY (shop_id) REFERENCES shops (id),
    FOREIGN KEY (tag_id)  REFERENCES tags (id),
    INDEX idx_shop_tags_shop_id (shop_id),
    INDEX idx_shop_tags_tag_id (tag_id)
) COMMENT '온라인샵-태그 매핑 (N:M)';

CREATE TABLE shop_brands
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '고유 식별자',
    shop_id  BIGINT NOT NULL                   COMMENT '온라인샵 ID (FK → shops)',
    brand_id BIGINT NOT NULL                   COMMENT '브랜드 ID (FK → brands)',
    UNIQUE KEY uk_shop_brand (shop_id, brand_id),
    FOREIGN KEY (shop_id)  REFERENCES shops (id),
    FOREIGN KEY (brand_id) REFERENCES brands (id),
    INDEX idx_shop_brands_shop_id (shop_id),
    INDEX idx_shop_brands_brand_id (brand_id)
) COMMENT '온라인샵-브랜드 매핑 (N:M)';

CREATE TABLE shop_requests
(
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY               COMMENT '온라인샵 등록 요청 고유 식별자',
    request_type          ENUM ('NEW', 'UPDATE') NOT NULL                 COMMENT '요청 타입 (NEW: 신규 등록, UPDATE: 기존 정보 수정 요청)',
    shop_id               BIGINT                                          COMMENT '수정 대상 온라인샵 ID (request_type=UPDATE일 때만 사용, FK → shops)',
    name                  VARCHAR(100)                                    COMMENT '온라인샵명',
    instagram_url         VARCHAR(255)                                    COMMENT '인스타그램 URL',
    naver_url             VARCHAR(255)                                    COMMENT '네이버 스마트스토어 URL',
    website_url           VARCHAR(255)                                    COMMENT '웹사이트 URL',
    category_ids          JSON                                            COMMENT '카테고리 ID 목록 (예: [1, 3])',
    category_text         VARCHAR(255)                                    COMMENT '직접 입력한 카테고리 텍스트',
    brand_yarn_ids        JSON                                            COMMENT '실 브랜드 ID 목록',
    brands_yarn           VARCHAR(255)                                    COMMENT '실 브랜드 직접 입력 텍스트',
    brand_needle_ids      JSON                                            COMMENT '바늘 브랜드 ID 목록',
    brands_needle         VARCHAR(255)                                    COMMENT '바늘 브랜드 직접 입력 텍스트',
    brand_notions_ids     JSON                                            COMMENT '부자재 브랜드 ID 목록',
    brands_notions        VARCHAR(255)                                    COMMENT '부자재 브랜드 직접 입력 텍스트',
    brand_patternbook_ids JSON                                            COMMENT '도안집 브랜드 ID 목록',
    brands_patternbook    VARCHAR(255)                                    COMMENT '도안집 브랜드 직접 입력 텍스트',
    tags                  VARCHAR(500)                                    COMMENT '태그 목록 (쉼표 구분 텍스트)',
    note                  TEXT                                            COMMENT '제보자 추가 메모',
    status                ENUM ('PENDING', 'APPROVED', 'REJECTED') NOT NULL
                          DEFAULT 'PENDING'                               COMMENT '처리 상태 (PENDING: 검토 대기, APPROVED: 승인됨, REJECTED: 거절됨)',
    created_at            DATETIME NOT NULL                               COMMENT '생성일시',
    updated_at            DATETIME NOT NULL                               COMMENT '수정일시',
    FOREIGN KEY (shop_id) REFERENCES shops (id)
) COMMENT '사용자 온라인샵 등록/수정 요청';

-- reviews 테이블 target_type ENUM에 SHOP 추가
ALTER TABLE reviews
    MODIFY COLUMN target_type ENUM ('PLACE', 'EVENT', 'SHOP') NOT NULL COMMENT '리뷰 대상 타입';
