-- ============================================================
-- V2__create_event_tables.sql
-- 로컬: ✅ 2026-04-05
-- ============================================================

CREATE TABLE events
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY                      COMMENT '이벤트 고유 식별자',
    title         VARCHAR(200) NOT NULL                                   COMMENT '이벤트 제목',
    description   TEXT                                                    COMMENT '이벤트 상세 설명',
    event_type    ENUM ('TESTER_RECRUIT', 'SALE', 'EVENT_POPUP') NOT NULL COMMENT '이벤트 타입 (TESTER_RECRUIT: 테스터 모집, SALE: 세일, EVENT_POPUP: 팝업 이벤트)',
    start_date    DATE NOT NULL                                           COMMENT '이벤트 시작일',
    end_date      DATE                                                    COMMENT '이벤트 종료일 (당일 이벤트면 NULL)',
    place_id      BIGINT                                                  COMMENT '연결된 장소 ID (FK → places, 기존 장소와 연결 시 사용)',
    location_text VARCHAR(255)                                            COMMENT '임시 장소 텍스트 (place_id 없을 때 사용)',
    lat           DECIMAL(10, 7)                                          COMMENT '위도 (GPS 좌표)',
    lng           DECIMAL(10, 7)                                          COMMENT '경도 (GPS 좌표)',
    links         JSON                                                    COMMENT '관련 링크 JSON (예: {"instagram":"...", "website":"..."})',
    active        BOOLEAN NOT NULL DEFAULT TRUE                           COMMENT '활성 여부 (false: 종료/숨김 처리)',
    created_at    DATETIME NOT NULL                                       COMMENT '생성일시',
    updated_at    DATETIME NOT NULL                                       COMMENT '수정일시',
    FOREIGN KEY (place_id) REFERENCES places (id),
    INDEX idx_events_event_type (event_type),
    INDEX idx_events_active (active),
    INDEX idx_events_start_date (start_date)
) COMMENT '뜨개 관련 이벤트/팝업/세일 정보';
