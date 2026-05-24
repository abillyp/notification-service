CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS notifications (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    type            VARCHAR(10)     NOT NULL,
    recipient       VARCHAR(255)    NOT NULL,
    subject         VARCHAR(150)    NOT NULL,
    body            TEXT            NOT NULL,
    priority        VARCHAR(10)     NOT NULL,
    status          VARCHAR(10)     NOT NULL DEFAULT 'PENDING',
    metadata        JSONB,
    created_at      TIMESTAMP       NOT NULL DEFAULT now(),
    sent_at         TIMESTAMP,
    error_message   TEXT,
    version         BIGINT      NOT NULL DEFAULT 0
    );

CREATE INDEX IF NOT EXISTS idx_notifications_status ON notifications(status);
CREATE INDEX IF NOT EXISTS idx_notifications_type   ON notifications(type);