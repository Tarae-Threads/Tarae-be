-- ============================================================
-- V1__create_place_tables.sql
-- 로컬: ✅ 2026-04-05
-- ============================================================

CREATE TABLE categories
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '카테고리 고유 식별자',
    name VARCHAR(50) NOT NULL              COMMENT '카테고리명 (예: 뜨개샵, 공방, 뜨개카페, 손염색실, 공예용품점)'
) COMMENT '장소 카테고리';

CREATE TABLE tags
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '태그 고유 식별자',
    name VARCHAR(50) NOT NULL              COMMENT '태그명 (예: 주차가능, 반려동물동반)'
) COMMENT '장소 태그';

CREATE TABLE brands
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY                  COMMENT '브랜드 고유 식별자',
    name VARCHAR(100) NOT NULL                              COMMENT '브랜드명 (예: 산네스간, 히야히야)',
    type ENUM ('YARN', 'NEEDLE', 'NOTIONS') NOT NULL        COMMENT '브랜드 타입 (YARN: 실, NEEDLE: 바늘, NOTIONS: 부자재)'
) COMMENT '뜨개 관련 브랜드';

CREATE TABLE places
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY              COMMENT '장소 고유 식별자',
    name          VARCHAR(100) NOT NULL                          COMMENT '장소명',
    region        VARCHAR(50)  NOT NULL                          COMMENT '지역 (예: 서울, 경기, 부산)',
    district      VARCHAR(100) NOT NULL                          COMMENT '동네 (예: 성수, 홍대, 망원)',
    address       VARCHAR(255) NOT NULL                          COMMENT '도로명 주소',
    lat           DECIMAL(10, 7)                                 COMMENT '위도 (GPS 좌표)',
    lng           DECIMAL(10, 7)                                 COMMENT '경도 (GPS 좌표)',
    hours_text    VARCHAR(255)                                   COMMENT '영업시간 텍스트 (예: 화~금 10:00-19:00, 토~일 09:00-19:00)',
    closed_days   VARCHAR(100)                                   COMMENT '정기 휴무일 (예: 월요일, 매주 화요일)',
    description   TEXT                                           COMMENT '장소 설명',
    instagram_url VARCHAR(255)                                   COMMENT '인스타그램 URL',
    website_url   VARCHAR(255)                                   COMMENT '웹사이트 URL',
    naver_map_url VARCHAR(255)                                   COMMENT '네이버 지도 URL',
    status        ENUM ('OPEN', 'CLOSED', 'RELOCATED') NOT NULL
                  DEFAULT 'OPEN'                                 COMMENT '운영 상태 (OPEN: 운영중, CLOSED: 폐업, RELOCATED: 이전함)',
    created_at    DATETIME NOT NULL                              COMMENT '생성일시',
    updated_at    DATETIME NOT NULL                              COMMENT '수정일시',
    INDEX idx_places_region (region)
) COMMENT '뜨개 관련 장소 정보';

CREATE TABLE place_categories
(
    place_id    BIGINT NOT NULL COMMENT '장소 ID (FK → places)',
    category_id BIGINT NOT NULL COMMENT '카테고리 ID (FK → categories)',
    PRIMARY KEY (place_id, category_id),
    FOREIGN KEY (place_id) REFERENCES places (id),
    FOREIGN KEY (category_id) REFERENCES categories (id),
    INDEX idx_place_categories_place_id (place_id),
    INDEX idx_place_categories_category_id (category_id)
) COMMENT '장소-카테고리 매핑 (N:M)';

CREATE TABLE place_tags
(
    place_id BIGINT NOT NULL COMMENT '장소 ID (FK → places)',
    tag_id   BIGINT NOT NULL COMMENT '태그 ID (FK → tags)',
    PRIMARY KEY (place_id, tag_id),
    FOREIGN KEY (place_id) REFERENCES places (id),
    FOREIGN KEY (tag_id) REFERENCES tags (id),
    INDEX idx_place_tags_place_id (place_id),
    INDEX idx_place_tags_tag_id (tag_id)
) COMMENT '장소-태그 매핑 (N:M)';

CREATE TABLE place_brands
(
    place_id BIGINT NOT NULL COMMENT '장소 ID (FK → places)',
    brand_id BIGINT NOT NULL COMMENT '브랜드 ID (FK → brands)',
    PRIMARY KEY (place_id, brand_id),
    FOREIGN KEY (place_id) REFERENCES places (id),
    FOREIGN KEY (brand_id) REFERENCES brands (id),
    INDEX idx_place_brands_place_id (place_id)
) COMMENT '장소-브랜드 매핑 (N:M)';
