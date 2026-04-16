-- 初始化测试数据库脚本
-- 创建测试所需的表结构和初始数据

-- 使用测试数据库
USE im_test;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    user_id VARCHAR(64) PRIMARY KEY,
    username VARCHAR(64) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20) UNIQUE,
    email VARCHAR(100) UNIQUE,
    nickname VARCHAR(64),
    avatar_url VARCHAR(255),
    status ENUM('ACTIVE', 'INACTIVE', 'LOCKED') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP NULL,
    login_fail_count INT DEFAULT 0,
    is_locked BOOLEAN DEFAULT FALSE,
    INDEX idx_username (username),
    INDEX idx_phone (phone),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 好友关系表
CREATE TABLE IF NOT EXISTS friends (
    friend_id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    friend_user_id VARCHAR(64) NOT NULL,
    status ENUM('PENDING', 'ACCEPTED', 'REJECTED', 'BLOCKED') DEFAULT 'PENDING',
    apply_message VARCHAR(500),
    remark VARCHAR(64),
    is_starred BOOLEAN DEFAULT FALSE,
    is_pinned BOOLEAN DEFAULT FALSE,
    is_muted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    accepted_at TIMESTAMP NULL,
    last_chat_at TIMESTAMP NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_friend_user_id (friend_user_id),
    INDEX idx_user_friend (user_id, friend_user_id),
    UNIQUE KEY uk_user_friend (user_id, friend_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 消息表
CREATE TABLE IF NOT EXISTS messages (
    message_id VARCHAR(64) PRIMARY KEY,
    conversation_id VARCHAR(64) NOT NULL,
    sender_id VARCHAR(64) NOT NULL,
    content_type ENUM('TEXT', 'IMAGE', 'VIDEO', 'AUDIO', 'FILE', 'LOCATION') DEFAULT 'TEXT',
    content TEXT,
    client_message_id VARCHAR(64),
    reply_to_message_id VARCHAR(64),
    reply_to_sender_id VARCHAR(64),
    reply_to_content TEXT,
    status ENUM('SENDING', 'SENT', 'DELIVERED', 'READ', 'FAILED') DEFAULT 'SENDING',
    is_recalled BOOLEAN DEFAULT FALSE,
    is_deleted BOOLEAN DEFAULT FALSE,
    is_pinned BOOLEAN DEFAULT FALSE,
    is_favorite BOOLEAN DEFAULT FALSE,
    recalled_at TIMESTAMP NULL,
    deleted_at TIMESTAMP NULL,
    pinned_at TIMESTAMP NULL,
    favorited_at TIMESTAMP NULL,
    read_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_conversation_id (conversation_id),
    INDEX idx_sender_id (sender_id),
    INDEX idx_client_message_id (client_message_id),
    INDEX idx_created_at (created_at),
    FULLTEXT INDEX idx_content (content)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 群组表
CREATE TABLE IF NOT EXISTS groups (
    group_id VARCHAR(64) PRIMARY KEY,
    group_name VARCHAR(128) NOT NULL,
    description TEXT,
    avatar_url VARCHAR(255),
    owner_id VARCHAR(64) NOT NULL,
    max_members INT DEFAULT 200,
    current_member_count INT DEFAULT 0,
    announcement TEXT,
    is_mute_all BOOLEAN DEFAULT FALSE,
    require_validation BOOLEAN DEFAULT TRUE,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_owner_id (owner_id),
    FULLTEXT INDEX idx_group_name (group_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 群成员表
CREATE TABLE IF NOT EXISTS group_members (
    member_id VARCHAR(64) PRIMARY KEY,
    group_id VARCHAR(64) NOT NULL,
    user_id VARCHAR(64) NOT NULL,
    role ENUM('OWNER', 'ADMIN', 'MEMBER') DEFAULT 'MEMBER',
    nickname_in_group VARCHAR(64),
    is_muted BOOLEAN DEFAULT FALSE,
    mute_end_time TIMESTAMP NULL,
    do_not_disturb BOOLEAN DEFAULT FALSE,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_group_id (group_id),
    INDEX idx_user_id (user_id),
    INDEX idx_group_user (group_id, user_id),
    UNIQUE KEY uk_group_user (group_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Refresh Token表
CREATE TABLE IF NOT EXISTS refresh_tokens (
    token_id VARCHAR(64) PRIMARY KEY,
    token VARCHAR(512) UNIQUE NOT NULL,
    user_id VARCHAR(64) NOT NULL,
    device_id VARCHAR(64),
    is_revoked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_token (token)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入测试数据
INSERT INTO users (user_id, username, password, phone, email, nickname, status) VALUES
('user_001', 'testuser1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EG', '13800138001', 'test1@example.com', 'Test User 1', 'ACTIVE'),
('user_002', 'testuser2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EG', '13800138002', 'test2@example.com', 'Test User 2', 'ACTIVE'),
('user_003', 'testuser3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EG', '13800138003', 'test3@example.com', 'Test User 3', 'ACTIVE');

INSERT INTO groups (group_id, group_name, description, owner_id, max_members, current_member_count) VALUES
('group_001', 'Test Group', 'A test group for integration testing', 'user_001', 200, 3);

INSERT INTO group_members (member_id, group_id, user_id, role) VALUES
('member_001', 'group_001', 'user_001', 'OWNER'),
('member_002', 'group_001', 'user_002', 'MEMBER'),
('member_003', 'group_001', 'user_003', 'MEMBER');

INSERT INTO friends (friend_id, user_id, friend_user_id, status, remark) VALUES
('friend_001', 'user_001', 'user_002', 'ACCEPTED', 'Friend 2'),
('friend_002', 'user_002', 'user_001', 'ACCEPTED', 'Friend 1');
