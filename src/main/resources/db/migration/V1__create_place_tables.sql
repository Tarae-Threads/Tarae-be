-- ============================================================
-- V1__create_place_tables.sql
-- 로컬: ✅ 2026-04-05
-- ============================================================

CREATE TABLE categories
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE tags
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE brands
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100)                    NOT NULL,
    type ENUM ('YARN', 'NEEDLE', 'NOTIONS') NOT NULL
);

CREATE TABLE places
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100)                        NOT NULL,
    region        VARCHAR(50)                         NOT NULL,
    district      VARCHAR(100)                        NOT NULL,
    address       VARCHAR(255)                        NOT NULL,
    lat           DECIMAL(10, 7),
    lng           DECIMAL(10, 7),
    hours_text    VARCHAR(255),
    closed_days   VARCHAR(100),
    description   TEXT,
    instagram_url VARCHAR(255),
    website_url   VARCHAR(255),
    naver_map_url VARCHAR(255),
    status        ENUM ('OPEN', 'CLOSED', 'RELOCATED') NOT NULL DEFAULT 'OPEN',
    created_at    DATETIME                            NOT NULL,
    updated_at    DATETIME                            NOT NULL,
    INDEX idx_places_region (region)
);

CREATE TABLE place_categories
(
    place_id    BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    PRIMARY KEY (place_id, category_id),
    FOREIGN KEY (place_id) REFERENCES places (id),
    FOREIGN KEY (category_id) REFERENCES categories (id),
    INDEX idx_place_categories_place_id (place_id),
    INDEX idx_place_categories_category_id (category_id)
);

CREATE TABLE place_tags
(
    place_id BIGINT NOT NULL,
    tag_id   BIGINT NOT NULL,
    PRIMARY KEY (place_id, tag_id),
    FOREIGN KEY (place_id) REFERENCES places (id),
    FOREIGN KEY (tag_id) REFERENCES tags (id),
    INDEX idx_place_tags_place_id (place_id),
    INDEX idx_place_tags_tag_id (tag_id)
);

CREATE TABLE place_brands
(
    place_id BIGINT NOT NULL,
    brand_id BIGINT NOT NULL,
    PRIMARY KEY (place_id, brand_id),
    FOREIGN KEY (place_id) REFERENCES places (id),
    FOREIGN KEY (brand_id) REFERENCES brands (id),
    INDEX idx_place_brands_place_id (place_id)
);
