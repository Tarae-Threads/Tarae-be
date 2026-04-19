CREATE TABLE inquiries
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    title      VARCHAR(255) NOT NULL,
    body       TEXT         NOT NULL,
    email      VARCHAR(100) NOT NULL,
    status     VARCHAR(15)  NOT NULL DEFAULT 'PENDING',
    reply_body TEXT,
    memo       TEXT,
    replied_at DATETIME,
    created_at DATETIME     NOT NULL,
    updated_at DATETIME     NOT NULL
);

CREATE INDEX idx_inquiries_status ON inquiries (status);
