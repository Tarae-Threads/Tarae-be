ALTER TABLE place_requests ADD COLUMN email VARCHAR(100) NULL AFTER note;
ALTER TABLE event_requests ADD COLUMN email VARCHAR(100) NULL AFTER naver_map_url;
ALTER TABLE shop_requests ADD COLUMN email VARCHAR(100) NULL AFTER note;
