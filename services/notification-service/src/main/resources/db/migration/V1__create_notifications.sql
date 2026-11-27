CREATE TABLE notifications (
    id UUID PRIMARY KEY,
    event_key VARCHAR(200) NOT NULL UNIQUE,
    type VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL,
    recipient VARCHAR(254) NOT NULL,
    subject VARCHAR(200) NOT NULL,
    details VARCHAR(2000) NOT NULL,
    attempts INTEGER NOT NULL DEFAULT 0 CHECK (attempts >= 0),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    sent_at TIMESTAMP WITH TIME ZONE
);
CREATE INDEX idx_notifications_pending ON notifications(status, created_at);
