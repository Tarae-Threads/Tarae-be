-- ============================================================
-- V3__create_request_tables.sql
-- 로컬: ✅ 2026-04-05
-- ============================================================

CREATE TABLE place_requests
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY               COMMENT '장소 등록 요청 고유 식별자',
    request_type   ENUM ('NEW', 'UPDATE') NOT NULL                 COMMENT '요청 타입 (NEW: 신규 장소 등록, UPDATE: 기존 장소 정보 수정 요청)',
    place_id       BIGINT                                          COMMENT '수정 대상 장소 ID (request_type=UPDATE일 때만 사용, FK → places)',
    name           VARCHAR(100)                                    COMMENT '장소명',
    address        VARCHAR(255)                                    COMMENT '도로명 주소',
    address_detail VARCHAR(100)                                    COMMENT '상세 주소 (동/호수 등)',
    lat            DECIMAL(10, 7)                                  COMMENT '위도 (프론트에서 geocoding 후 전송)',
    lng            DECIMAL(10, 7)                                  COMMENT '경도 (프론트에서 geocoding 후 전송)',
    category_ids   JSON                                            COMMENT '카테고리 ID 목록 (예: [1, 3])',
    hours_text     VARCHAR(255)                                    COMMENT '영업시간 텍스트',
    closed_days    VARCHAR(100)                                    COMMENT '정기 휴무일',
    brands_yarn    VARCHAR(255)                                    COMMENT '실 브랜드 목록 (쉼표 구분 텍스트, 관리자 승인 시 brands 테이블에 매핑)',
    brands_needle  VARCHAR(255)                                    COMMENT '바늘 브랜드 목록 (쉼표 구분 텍스트)',
    brands_notions VARCHAR(255)                                    COMMENT '부자재 브랜드 목록 (쉼표 구분 텍스트)',
    instagram_url  VARCHAR(255)                                    COMMENT '인스타그램 URL',
    website_url    VARCHAR(255)                                    COMMENT '웹사이트 URL',
    naver_map_url  VARCHAR(255)                                    COMMENT '네이버 지도 URL',
    tags           VARCHAR(500)                                    COMMENT '태그 목록 (쉼표 구분 텍스트)',
    note           TEXT                                            COMMENT '제보자 추가 메모',
    status         ENUM ('PENDING', 'APPROVED', 'REJECTED') NOT NULL
                   DEFAULT 'PENDING'                               COMMENT '처리 상태 (PENDING: 검토 대기, APPROVED: 승인됨, REJECTED: 거절됨)',
    created_at     DATETIME NOT NULL                               COMMENT '생성일시',
    updated_at     DATETIME NOT NULL                               COMMENT '수정일시',
    FOREIGN KEY (place_id) REFERENCES places (id)
) COMMENT '사용자 장소 등록/수정 요청';

CREATE TABLE event_requests
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY                      COMMENT '이벤트 등록 요청 고유 식별자',
    title         VARCHAR(200) NOT NULL                                   COMMENT '이벤트 제목',
    event_type    ENUM ('TESTER_RECRUIT', 'SALE', 'EVENT_POPUP') NOT NULL COMMENT '이벤트 타입 (TESTER_RECRUIT: 테스터 모집, SALE: 세일, EVENT_POPUP: 팝업 이벤트)',
    start_date    DATE NOT NULL                                           COMMENT '이벤트 시작일',
    end_date      DATE                                                    COMMENT '이벤트 종료일 (당일 이벤트면 NULL)',
    location_text VARCHAR(255)                                            COMMENT '장소 텍스트 (자유 입력)',
    description   TEXT                                                    COMMENT '이벤트 상세 설명',
    status        ENUM ('PENDING', 'APPROVED', 'REJECTED') NOT NULL
                  DEFAULT 'PENDING'                                       COMMENT '처리 상태 (PENDING: 검토 대기, APPROVED: 승인됨, REJECTED: 거절됨)',
    created_at    DATETIME NOT NULL                                       COMMENT '생성일시',
    updated_at    DATETIME NOT NULL                                       COMMENT '수정일시'
) COMMENT '사용자 이벤트 등록 요청';
