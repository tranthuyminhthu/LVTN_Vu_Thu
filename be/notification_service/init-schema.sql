-- PostgreSQL Schema for Notification Service
-- This file will be executed when the PostgreSQL container starts

-- Create database if not exists (already created by environment variable)
-- CREATE DATABASE notification_db;

-- Connect to the database
\c notification_db;

-- Create extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

-- Create enum types
DO $$ BEGIN
    CREATE TYPE notification_type AS ENUM ('EMAIL', 'SMS', 'PUSH', 'IN_APP');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE notification_category AS ENUM ('ORDER', 'PROMOTION', 'SYSTEM', 'SECURITY', 'MARKETING');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE notification_status AS ENUM ('PENDING', 'SENT', 'FAILED', 'READ', 'SCHEDULED');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

-- Create notifications table
CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    type notification_type NOT NULL,
    category notification_category NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    template_id VARCHAR(100),
    status notification_status NOT NULL DEFAULT 'PENDING',
    recipient VARCHAR(255) NOT NULL,
    data JSONB, -- Using JSONB for better performance
    scheduled_at TIMESTAMP,
    sent_at TIMESTAMP,
    read_at TIMESTAMP,
    error_message TEXT,
    retry_count INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create notification_templates table
CREATE TABLE IF NOT EXISTS notification_templates (
    id BIGSERIAL PRIMARY KEY,
    template_id VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    type notification_type NOT NULL,
    category notification_category NOT NULL,
    subject VARCHAR(255),
    content TEXT NOT NULL,
    html_content TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create user_notification_preferences table
CREATE TABLE IF NOT EXISTS user_notification_preferences (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    type notification_type NOT NULL,
    category notification_category NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(user_id, type, category)
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_notifications_user_id ON notifications(user_id);
CREATE INDEX IF NOT EXISTS idx_notifications_type ON notifications(type);
CREATE INDEX IF NOT EXISTS idx_notifications_status ON notifications(status);
CREATE INDEX IF NOT EXISTS idx_notifications_created_at ON notifications(created_at);
CREATE INDEX IF NOT EXISTS idx_notifications_scheduled_at ON notifications(scheduled_at);
CREATE INDEX IF NOT EXISTS idx_notifications_user_status ON notifications(user_id, status);
CREATE INDEX IF NOT EXISTS idx_notifications_type_status ON notifications(type, status);
CREATE INDEX IF NOT EXISTS idx_notifications_retry_candidates ON notifications(status, retry_count, created_at);

-- Create composite indexes for common queries
CREATE INDEX IF NOT EXISTS idx_notifications_user_status_created ON notifications(user_id, status, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_notifications_type_status_scheduled ON notifications(type, status, scheduled_at);

-- Create GIN index for JSONB data column
CREATE INDEX IF NOT EXISTS idx_notifications_data_gin ON notifications USING GIN (data);

-- Create indexes for templates
CREATE INDEX IF NOT EXISTS idx_templates_type_category ON notification_templates(type, category);
CREATE INDEX IF NOT EXISTS idx_templates_active ON notification_templates(is_active);

-- Create indexes for user preferences
CREATE INDEX IF NOT EXISTS idx_preferences_user_id ON user_notification_preferences(user_id);
CREATE INDEX IF NOT EXISTS idx_preferences_type_category ON user_notification_preferences(type, category);

-- Create function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create triggers to automatically update updated_at
CREATE TRIGGER update_notifications_updated_at 
    BEFORE UPDATE ON notifications 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_templates_updated_at 
    BEFORE UPDATE ON notification_templates 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_preferences_updated_at 
    BEFORE UPDATE ON user_notification_preferences 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Create function to get notification statistics
CREATE OR REPLACE FUNCTION get_notification_stats(since_date TIMESTAMP)
RETURNS TABLE (
    notification_type notification_type,
    total_count BIGINT,
    sent_count BIGINT,
    failed_count BIGINT,
    delivery_rate NUMERIC
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        n.type,
        COUNT(*) as total_count,
        COUNT(CASE WHEN n.status = 'SENT' THEN 1 END) as sent_count,
        COUNT(CASE WHEN n.status = 'FAILED' THEN 1 END) as failed_count,
        ROUND(
            COUNT(CASE WHEN n.status = 'SENT' THEN 1 END) * 100.0 / COUNT(*), 
            2
        ) as delivery_rate
    FROM notifications n
    WHERE n.created_at >= since_date
    GROUP BY n.type;
END;
$$ LANGUAGE plpgsql;

-- Create function to get daily notification statistics
CREATE OR REPLACE FUNCTION get_daily_notification_stats(since_date TIMESTAMP)
RETURNS TABLE (
    day_date DATE,
    notification_type notification_type,
    count BIGINT
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        DATE(n.created_at) as day_date,
        n.type,
        COUNT(*) as count
    FROM notifications n
    WHERE n.created_at >= since_date
    GROUP BY DATE(n.created_at), n.type
    ORDER BY day_date DESC, n.type;
END;
$$ LANGUAGE plpgsql;

-- Insert sample data for testing
INSERT INTO notification_templates (template_id, name, type, category, subject, content, html_content) VALUES
('order-confirmation', 'Order Confirmation Email', 'EMAIL', 'ORDER', 'Xác nhận đơn hàng #{orderId}', 
 'Xin chào {userName}, cảm ơn bạn đã đặt hàng. Tổng tiền: {totalAmount} VND', 
 '<h1>Xác nhận đơn hàng #{orderId}</h1><p>Xin chào {userName},</p><p>Cảm ơn bạn đã đặt hàng. Tổng tiền: {totalAmount} VND</p>')
ON CONFLICT (template_id) DO NOTHING;

INSERT INTO notification_templates (template_id, name, type, category, subject, content, html_content) VALUES
('welcome-email', 'Welcome Email', 'EMAIL', 'SYSTEM', 'Chào mừng bạn đến với chúng tôi', 
 'Xin chào {userName}, chào mừng bạn đến với hệ thống của chúng tôi!', 
 '<h1>Chào mừng bạn!</h1><p>Xin chào {userName},</p><p>Chào mừng bạn đến với hệ thống của chúng tôi!</p>')
ON CONFLICT (template_id) DO NOTHING;

-- Grant permissions (if needed for external connections)
-- GRANT ALL PRIVILEGES ON DATABASE notification_db TO postgres;
-- GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO postgres;
-- GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO postgres; 