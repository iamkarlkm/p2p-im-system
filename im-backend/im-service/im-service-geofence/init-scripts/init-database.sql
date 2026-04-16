-- ============================================
-- IM 地理围栏服务数据库初始化脚本
-- Database: im_geofence
-- ============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS im_geofence 
    DEFAULT CHARACTER SET utf8mb4 
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE im_geofence;

-- ============================================
-- 1. 地理围栏表
-- ============================================
CREATE TABLE IF NOT EXISTS im_geofence (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '自增ID',
    geofence_id VARCHAR(64) NOT NULL UNIQUE COMMENT '围栏唯一标识',
    name VARCHAR(100) NOT NULL COMMENT '围栏名称',
    description VARCHAR(500) DEFAULT NULL COMMENT '围栏描述',
    fence_type VARCHAR(20) NOT NULL COMMENT '围栏类型: CIRCLE-圆形, POLYGON-多边形, POLYLINE-路线',
    merchant_id VARCHAR(64) DEFAULT NULL COMMENT '关联商户ID',
    poi_id VARCHAR(64) DEFAULT NULL COMMENT '关联POI ID',
    center_latitude DOUBLE DEFAULT NULL COMMENT '围栏中心纬度',
    center_longitude DOUBLE DEFAULT NULL COMMENT '围栏中心经度',
    radius INT DEFAULT NULL COMMENT '半径(米)，仅圆形围栏使用',
    coordinates TEXT DEFAULT NULL COMMENT '多边形/路线坐标点，JSON格式: [{"lat": xxx, "lng": xxx}, ...]',
    geo_hash VARCHAR(20) DEFAULT NULL COMMENT 'GeoHash编码',
    level INT DEFAULT 1 COMMENT '围栏层级',
    parent_id VARCHAR(64) DEFAULT NULL COMMENT '父围栏ID',
    trigger_condition VARCHAR(20) DEFAULT 'ENTER' COMMENT '触发条件: ENTER-进入, EXIT-离开, DWELL-停留',
    dwell_time INT DEFAULT NULL COMMENT '停留时间(秒)，仅DWELL类型使用',
    effective_start_time DATETIME DEFAULT NULL COMMENT '生效开始时间',
    effective_end_time DATETIME DEFAULT NULL COMMENT '生效结束时间',
    business_hours VARCHAR(500) DEFAULT NULL COMMENT '营业时间，JSON格式: {"Mon": "09:00-21:00", ...}',
    effective_weekdays VARCHAR(50) DEFAULT NULL COMMENT '有效工作日，逗号分隔: "Mon,Tue,Wed,Thu,Fri"',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '围栏状态: ACTIVE-激活, INACTIVE-未激活, PAUSED-暂停, EXPIRED-过期',
    is_enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用: 1-启用, 0-禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_merchant_id (merchant_id),
    INDEX idx_poi_id (poi_id),
    INDEX idx_geo_hash (geo_hash),
    INDEX idx_parent_id (parent_id),
    INDEX idx_status (status),
    INDEX idx_enabled (is_enabled),
    INDEX idx_fence_type (fence_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='地理围栏表';

-- ============================================
-- 2. 位置分享表
-- ============================================
CREATE TABLE IF NOT EXISTS im_location_share (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '自增ID',
    share_id VARCHAR(64) NOT NULL UNIQUE COMMENT '分享唯一标识',
    user_id VARCHAR(64) NOT NULL COMMENT '分享者用户ID',
    recipient_id VARCHAR(64) DEFAULT NULL COMMENT '接收者用户ID',
    latitude DOUBLE NOT NULL COMMENT '纬度',
    longitude DOUBLE NOT NULL COMMENT '经度',
    accuracy DOUBLE DEFAULT NULL COMMENT '精度(米)',
    address VARCHAR(200) DEFAULT NULL COMMENT '地址描述',
    share_type VARCHAR(20) NOT NULL DEFAULT 'REALTIME' COMMENT '分享类型: REALTIME-实时, ONCE-一次性, TIMED-定时',
    is_active TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否激活: 1-激活, 0-非激活',
    expires_at DATETIME DEFAULT NULL COMMENT '过期时间',
    duration_minutes INT DEFAULT 1440 COMMENT '持续时间(分钟)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_user_id (user_id),
    INDEX idx_recipient_id (recipient_id),
    INDEX idx_share_id (share_id),
    INDEX idx_is_active (is_active),
    INDEX idx_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='位置分享表';

-- ============================================
-- 3. 地理围栏事件表
-- ============================================
CREATE TABLE IF NOT EXISTS im_geofence_event (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '自增ID',
    event_id VARCHAR(64) NOT NULL UNIQUE COMMENT '事件唯一标识',
    geofence_id VARCHAR(64) NOT NULL COMMENT '围栏ID',
    user_id VARCHAR(64) NOT NULL COMMENT '用户ID',
    event_type VARCHAR(20) NOT NULL COMMENT '事件类型: ENTER-进入, EXIT-离开, DWELL-停留',
    latitude DOUBLE NOT NULL COMMENT '纬度',
    longitude DOUBLE NOT NULL COMMENT '经度',
    accuracy DOUBLE DEFAULT NULL COMMENT '精度(米)',
    speed DOUBLE DEFAULT NULL COMMENT '速度(m/s)',
    bearing DOUBLE DEFAULT NULL COMMENT '方向角(度)',
    device_id VARCHAR(64) DEFAULT NULL COMMENT '设备ID',
    app_version VARCHAR(20) DEFAULT NULL COMMENT '应用版本',
    event_time DATETIME NOT NULL COMMENT '事件发生时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    INDEX idx_geofence_id (geofence_id),
    INDEX idx_user_id (user_id),
    INDEX idx_event_type (event_type),
    INDEX idx_event_time (event_time),
    INDEX idx_geofence_user (geofence_id, user_id),
    INDEX idx_user_event_time (user_id, event_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='地理围栏事件表';

-- ============================================
-- 4. 初始化数据（可选）
-- ============================================

-- 插入示例围栏数据（测试用）
-- INSERT INTO im_geofence (geofence_id, name, description, fence_type, center_latitude, center_longitude, radius, geo_hash, status, is_enabled) 
-- VALUES ('GEO_TEST_001', '测试围栏', '这是一个测试围栏', 'CIRCLE', 39.908722, 116.397499, 500, 'wx4g0', 'ACTIVE', 1);

-- ============================================
-- 完成
-- ============================================
SELECT 'im_geofence database initialized successfully!' AS message;
