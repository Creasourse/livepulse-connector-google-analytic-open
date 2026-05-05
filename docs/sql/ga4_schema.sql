-- ============================================
-- Google Analytics 4 连接器 - PostgreSQL 数据库表结构
-- 版本: 2.1
-- 创建时间: 2025-05-04
-- ============================================

-- ============================================
-- 1. GA4 属性表
-- ============================================
DROP TABLE IF EXISTS ga4_property CASCADE;

CREATE TABLE ga4_property (
    -- 主键
    id BIGSERIAL PRIMARY KEY,

    -- GA4 属性信息
    property_id VARCHAR(50) NOT NULL,
    property_name VARCHAR(200) NOT NULL,

    -- BigQuery 配置
    bigquery_project_id VARCHAR(100),
    bigquery_dataset_id VARCHAR(100),
    data_stream_id VARCHAR(100),

    -- 状态信息
    enabled BOOLEAN DEFAULT TRUE,
    sync_status VARCHAR(20),
    last_sync_time TIMESTAMP,
    last_error_message TEXT,

    -- 审计字段
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by VARCHAR(50),
    update_by VARCHAR(50),

    -- 约束
    CONSTRAINT uk_ga4_property_id UNIQUE (property_id)
);

-- 索引
CREATE INDEX idx_ga4_property_enabled ON ga4_property(enabled) WHERE enabled = TRUE;
CREATE INDEX idx_ga4_property_sync_status ON ga4_property(sync_status);
CREATE INDEX idx_ga4_property_create_time ON ga4_property(create_time DESC);

-- 注释
COMMENT ON TABLE ga4_property IS 'GA4 属性配置表';
COMMENT ON COLUMN ga4_property.id IS '主键 ID';
COMMENT ON COLUMN ga4_property.property_id IS 'GA4 Property ID (如: 123456789)';
COMMENT ON COLUMN ga4_property.property_name IS '属性名称';
COMMENT ON COLUMN ga4_property.bigquery_project_id IS 'BigQuery 项目 ID';
COMMENT ON COLUMN ga4_property.bigquery_dataset_id IS 'BigQuery 数据集 ID';
COMMENT ON COLUMN ga4_property.data_stream_id IS '数据流 ID';
COMMENT ON COLUMN ga4_property.enabled IS '是否启用';
COMMENT ON COLUMN ga4_property.sync_status IS '同步状态: pending/syncing/success/failed';
COMMENT ON COLUMN ga4_property.last_sync_time IS '最后同步时间';
COMMENT ON COLUMN ga4_property.last_error_message IS '最后错误信息';


-- ============================================
-- 2. GA4 事件表
-- ============================================
DROP TABLE IF EXISTS ga4_event CASCADE;

CREATE TABLE ga4_event (
    -- 主键
    id BIGSERIAL PRIMARY KEY,

    -- 关联信息
    ga4_property_id BIGINT NOT NULL,

    -- 事件基本信息
    event_name VARCHAR(100) NOT NULL,
    event_timestamp BIGINT,
    event_date VARCHAR(8),
    event_time VARCHAR(6),

    -- 用户标识
    session_id VARCHAR(100),
    user_id VARCHAR(100),
    client_id VARCHAR(100),

    -- 事件参数和属性（JSON 格式）
    event_params JSONB,
    user_properties JSONB,

    -- 设备信息
    device_category VARCHAR(50),
    browser VARCHAR(100),
    operating_system VARCHAR(100),

    -- 地理位置
    country VARCHAR(100),
    city VARCHAR(100),

    -- 页面信息
    page_location TEXT,
    page_title VARCHAR(500),
    page_referrer TEXT,

    -- 营销活动信息
    campaign_source VARCHAR(200),
    campaign_medium VARCHAR(200),
    campaign_name VARCHAR(200),

    -- 事件值
    event_value NUMERIC(18,4),

    -- 处理状态
    processed BOOLEAN DEFAULT FALSE,
    processed_time TIMESTAMP,

    -- 审计字段
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- 外键约束
    CONSTRAINT fk_ga4_event_property FOREIGN KEY (ga4_property_id)
        REFERENCES ga4_property(id) ON DELETE CASCADE
);

-- 索引
CREATE INDEX idx_ga4_event_property_id ON ga4_event(ga4_property_id);
CREATE INDEX idx_ga4_event_event_name ON ga4_event(event_name);
CREATE INDEX idx_ga4_event_event_date ON ga4_event(event_date);
CREATE INDEX idx_ga4_event_user_id ON ga4_event(user_id);
CREATE INDEX idx_ga4_event_client_id ON ga4_event(client_id);
CREATE INDEX idx_ga4_event_session_id ON ga4_event(session_id);
CREATE INDEX idx_ga4_event_processed ON ga4_event(processed) WHERE processed = FALSE;
CREATE INDEX idx_ga4_event_create_time ON ga4_event(create_time DESC);

-- JSONB 索引（用于查询事件参数）
CREATE INDEX idx_ga4_event_params ON ga4_event USING GIN (event_params);
CREATE INDEX idx_ga4_event_user_props ON ga4_event USING GIN (user_properties);

-- 复合索引
CREATE INDEX idx_ga4_event_property_date ON ga4_event(ga4_property_id, event_date DESC);

-- 注释
COMMENT ON TABLE ga4_event IS 'GA4 事件数据表';
COMMENT ON COLUMN ga4_event.id IS '主键 ID';
COMMENT ON COLUMN ga4_event.ga4_property_id IS '关联的 GA4 属性 ID';
COMMENT ON COLUMN ga4_event.event_name IS '事件名称 (如: page_view, session_start, purchase)';
COMMENT ON COLUMN ga4_event.event_timestamp IS '事件时间戳（微秒）';
COMMENT ON COLUMN ga4_event.event_date IS '事件日期 (YYYYMMDD 格式)';
COMMENT ON COLUMN ga4_event.event_time IS '事件时间 (HHMMSS 格式)';
COMMENT ON COLUMN ga4_event.session_id IS '会话 ID';
COMMENT ON COLUMN ga4_event.user_id IS '用户 ID';
COMMENT ON COLUMN ga4_event.client_id IS '客户端 ID';
COMMENT ON COLUMN ga4_event.event_params IS '事件参数 (JSONB 格式)';
COMMENT ON COLUMN ga4_event.user_properties IS '用户属性 (JSONB 格式)';
COMMENT ON COLUMN ga4_event.device_category IS '设备类别';
COMMENT ON COLUMN ga4_event.browser IS '浏览器';
COMMENT ON COLUMN ga4_event.operating_system IS '操作系统';
COMMENT ON COLUMN ga4_event.country IS '国家';
COMMENT ON COLUMN ga4_event.city IS '城市';
COMMENT ON COLUMN ga4_event.page_location IS '页面位置 (URL)';
COMMENT ON COLUMN ga4_event.page_title IS '页面标题';
COMMENT ON COLUMN ga4_event.page_referrer IS '页面引用来源';
COMMENT ON COLUMN ga4_event.campaign_source IS '营销活动来源';
COMMENT ON COLUMN ga4_event.campaign_medium IS '营销活动媒介';
COMMENT ON COLUMN ga4_event.campaign_name IS '营销活动名称';
COMMENT ON COLUMN ga4_event.event_value IS '事件值';
COMMENT ON COLUMN ga4_event.processed IS '是否已处理';
COMMENT ON COLUMN ga4_event.processed_time IS '处理时间';


-- ============================================
-- 3. GA4 事件订阅表
-- ============================================
DROP TABLE IF EXISTS ga4_event_subscription CASCADE;

CREATE TABLE ga4_event_subscription (
    -- 主键
    id BIGSERIAL PRIMARY KEY,

    -- 关联信息
    ga4_property_id BIGINT NOT NULL,

    -- 订阅配置
    event_name VARCHAR(100) NOT NULL,
    event_type VARCHAR(20) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    filter_condition TEXT,

    -- 审计字段
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by VARCHAR(50),
    update_by VARCHAR(50),

    -- 外键约束
    CONSTRAINT fk_ga4_event_subscription_property FOREIGN KEY (ga4_property_id)
        REFERENCES ga4_property(id) ON DELETE CASCADE
);

-- 索引
CREATE INDEX idx_ga4_event_subscription_property_id ON ga4_event_subscription(ga4_property_id);
CREATE INDEX idx_ga4_event_subscription_enabled ON ga4_event_subscription(enabled) WHERE enabled = TRUE;
CREATE INDEX idx_ga4_event_subscription_event_name ON ga4_event_subscription(event_name);

-- 注释
COMMENT ON TABLE ga4_event_subscription IS 'GA4 事件订阅配置表';
COMMENT ON COLUMN ga4_event_subscription.id IS '主键 ID';
COMMENT ON COLUMN ga4_event_subscription.ga4_property_id IS '关联的 GA4 属性 ID';
COMMENT ON COLUMN ga4_event_subscription.event_name IS '事件名称（支持通配符 * 表示所有事件）';
COMMENT ON COLUMN ga4_event_subscription.event_type IS '事件类型: standard(标准事件)/custom(自定义事件)';
COMMENT ON COLUMN ga4_event_subscription.enabled IS '是否启用';
COMMENT ON COLUMN ga4_event_subscription.filter_condition IS '过滤条件（JSON 格式）';


-- ============================================
-- 4. GA4 同步日志表
-- ============================================
DROP TABLE IF EXISTS ga4_sync_log CASCADE;

CREATE TABLE ga4_sync_log (
    -- 主键
    id BIGSERIAL PRIMARY KEY,

    -- 关联信息
    ga4_property_id BIGINT NOT NULL,

    -- 同步信息
    sync_type VARCHAR(50) NOT NULL,
    sync_status VARCHAR(20) NOT NULL,

    -- 时间信息
    start_time TIMESTAMP,
    end_time TIMESTAMP,

    -- 日期范围
    start_date VARCHAR(8),
    end_date VARCHAR(8),

    -- 统计信息
    success_count BIGINT DEFAULT 0,
    failure_count BIGINT DEFAULT 0,
    total_count BIGINT DEFAULT 0,

    -- 错误信息
    error_message TEXT,

    -- 执行耗时
    duration BIGINT,

    -- 审计字段
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- 外键约束
    CONSTRAINT fk_ga4_sync_log_property FOREIGN KEY (ga4_property_id)
        REFERENCES ga4_property(id) ON DELETE CASCADE
);

-- 索引
CREATE INDEX idx_ga4_sync_log_property_id ON ga4_sync_log(ga4_property_id);
CREATE INDEX idx_ga4_sync_log_sync_type ON ga4_sync_log(sync_type);
CREATE INDEX idx_ga4_sync_log_sync_status ON ga4_sync_log(sync_status);
CREATE INDEX idx_ga4_sync_log_start_time ON ga4_sync_log(start_time DESC);
CREATE INDEX idx_ga4_sync_log_create_time ON ga4_sync_log(create_time DESC);

-- 复合索引
CREATE INDEX idx_ga4_sync_log_property_time ON ga4_sync_log(ga4_property_id, start_time DESC);

-- 注释
COMMENT ON TABLE ga4_sync_log IS 'GA4 同步日志表';
COMMENT ON COLUMN ga4_sync_log.id IS '主键 ID';
COMMENT ON COLUMN ga4_sync_log.ga4_property_id IS '关联的 GA4 属性 ID';
COMMENT ON COLUMN ga4_sync_log.sync_type IS '同步类型: event(事件)/user_property(用户属性)';
COMMENT ON COLUMN ga4_sync_log.sync_status IS '同步状态: pending/running/success/failed/partial_success';
COMMENT ON COLUMN ga4_sync_log.start_time IS '开始时间';
COMMENT ON COLUMN ga4_sync_log.end_time IS '结束时间';
COMMENT ON COLUMN ga4_sync_log.start_date IS '起始日期 (YYYYMMDD 格式)';
COMMENT ON COLUMN ga4_sync_log.end_date IS '结束日期 (YYYYMMDD 格式)';
COMMENT ON COLUMN ga4_sync_log.success_count IS '成功记录数';
COMMENT ON COLUMN ga4_sync_log.failure_count IS '失败记录数';
COMMENT ON COLUMN ga4_sync_log.total_count IS '总记录数';
COMMENT ON COLUMN ga4_sync_log.error_message IS '错误信息';
COMMENT ON COLUMN ga4_sync_log.duration IS '执行耗时（毫秒）';


-- ============================================
-- 5. 初始化数据
-- ============================================

-- 插入示例 GA4 属性配置（可选）
-- INSERT INTO ga4_property (
--     property_id, property_name, bigquery_project_id, bigquery_dataset_id,
--     data_stream_id, enabled, sync_status
-- ) VALUES (
--     '123456789',
--     '示例 GA4 属性',
--     'your-gcp-project-id',
--     'analytics_123456789',
--     '1234567890',
--     TRUE,
--     'pending'
-- );

-- 插入示例事件订阅配置（可选）
-- INSERT INTO ga4_event_subscription (
--     ga4_property_id, event_name, event_type, enabled
-- ) VALUES
-- (1, '*', 'standard', TRUE),  -- 订阅所有标准事件
-- (1, 'purchase', 'standard', TRUE),  -- 订阅购买事件
-- (1, 'sign_up', 'custom', TRUE);  -- 订阅自定义注册事件


-- ============================================
-- 6. 自动更新触发器（可选）
-- ============================================

-- 创建更新时间自动更新函数
CREATE OR REPLACE FUNCTION update_updated_time_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.update_time = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 为 ga4_property 表创建触发器
CREATE TRIGGER trigger_ga4_property_update_time
BEFORE UPDATE ON ga4_property
FOR EACH ROW
EXECUTE FUNCTION update_updated_time_column();

-- 为 ga4_event 表创建触发器
CREATE TRIGGER trigger_ga4_event_update_time
BEFORE UPDATE ON ga4_event
FOR EACH ROW
EXECUTE FUNCTION update_updated_time_column();

-- 为 ga4_event_subscription 表创建触发器
CREATE TRIGGER trigger_ga4_event_subscription_update_time
BEFORE UPDATE ON ga4_event_subscription
FOR EACH ROW
EXECUTE FUNCTION update_updated_time_column();


-- ============================================
-- 结束
-- ============================================