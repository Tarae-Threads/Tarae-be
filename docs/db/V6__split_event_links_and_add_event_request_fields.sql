-- Event: links(JSON TEXT) → 3개 URL 컬럼 분리
ALTER TABLE events DROP COLUMN links;
ALTER TABLE events ADD COLUMN instagram_url VARCHAR(255);
ALTER TABLE events ADD COLUMN website_url VARCHAR(255);
ALTER TABLE events ADD COLUMN naver_map_url VARCHAR(255);

-- EventRequest: lat/lng + 3개 URL 컬럼 추가
ALTER TABLE event_requests ADD COLUMN lat DECIMAL(10, 7) COMMENT '위도';
ALTER TABLE event_requests ADD COLUMN lng DECIMAL(10, 7) COMMENT '경도';
ALTER TABLE event_requests ADD COLUMN instagram_url VARCHAR(255);
ALTER TABLE event_requests ADD COLUMN website_url VARCHAR(255);
ALTER TABLE event_requests ADD COLUMN naver_map_url VARCHAR(255);
