# 即时通讯系统架构设计文档

## 1. 架构概述

### 1.1 技术栈选型

| 层级 | 技术选型 | 说明 |
|------|----------|------|
| 桌面端 | Tauri 2.x | Rust + WebView2，性能优异，体积小 |
| 移动端 | Flutter 3.x | 跨平台 UI 框架，一套代码多端运行 |
| 后端框架 | Spring Boot 3.x + Spring Cloud | 微服务架构，统一配置管理 |
| 通信网关 | Netty | 高性能 TCP/WebSocket 服务端 |
| 消息队列 | Redis Pub/Sub + Kafka | 消息路由、削峰填谷 |
| 缓存层 | Redis Cluster | 会话缓存、消息缓存、限流 |
| 持久化 | MySQL 8.0 + ShardingSphere | 分布式数据库，分库分表 |
| 文件存储 | MinIO / OSS | 头像、图片、文件存储 |

### 1.2 系统架构图

```
┌─────────────────────────────────────────────────────────────────────────┐
│                              客户端层                                     │
├─────────────────────┬─────────────────────┬─────────────────────────────┤
│   Tauri 桌面端      │   Flutter iOS      │   Flutter Android           │
│   (Windows/macOS)  │                    │                             │
└─────────┬───────────┴─────────┬─────────┴─────────────┬─────────────────┘
          │                     │                      │
          │         ┌───────────┴───────────┐          │
          │         │   API Gateway        │          │
          │         │   (Spring Cloud)     │          │
          │         └───────────┬───────────┘          │
          │                     │                      │
          ▼                     ▼                      ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                           服务层 (Spring Cloud)                         │
├──────────────┬──────────────┬──────────────┬──────────────┬─────────────┤
│  用户服务    │  消息服务    │  群组服务    │  文件服务    │  通知服务   │
│  User       │  Message    │  Group      │  File       │  Notify     │
│  Service    │  Service    │  Service    │  Service    │  Service    │
└──────┬───────┴──────┬───────┴──────┬───────┴──────┬───────┴──────┬──────┘
       │              │              │              │              │
       │   ┌──────────┴───────────┐  │              │              │
       │   │   WebSocket Gateway  │  │              │              │
       │   │   (Netty Server)     │  │              │              │
       │   └──────────┬───────────┘  │              │              │
       │              │              │              │              │
       ▼              ▼              ▼              ▼              ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                           数据层                                         │
├─────────────────┬─────────────────┬─────────────────┬───────────────────┤
│  MySQL Cluster │  Redis Cluster │  Kafka Cluster  │  MinIO/OSS        │
│  (分库分表)     │  (缓存/会话)    │  (消息队列)     │  (文件存储)       │
└─────────────────┴─────────────────┴─────────────────┴───────────────────┘
```

### 1.3 核心流程

```
用户登录流程:
1. 客户端 → API Gateway → Auth Service (JWT token)
2. 客户端 → Netty WebSocket (建立长连接)
3. Netty → Redis (绑定 userId → channel)
4. 客户端 ← 登录成功通知

消息发送流程:
1. 客户端 → Netty WebSocket (发送消息)
2. Netty → Message Service (消息落库)
3. Message Service → Kafka (异步投递)
4. Kafka → 消费者 → Redis (更新会话缓存)
5. Netty → 目标用户 WebSocket (推送消息)
6. 目标用户 ← 消息送达确认
```

---

## 2. 模块划分

### 2.1 微服务模块清单

| 服务名称 | 端口 | 功能职责 | 技术栈 |
|----------|------|----------|--------|
| api-gateway | 8080 | 请求路由、鉴权、限流 | Spring Cloud Gateway |
| auth-service | 8081 | 用户认证、JWT签发 | Spring Boot |
| user-service | 8082 | 用户信息、好友管理 | Spring Boot |
| message-service | 8083 | 消息存储、转发 | Spring Boot + Netty |
| group-service | 8084 | 群组管理、群消息 | Spring Boot |
| file-service | 8085 | 文件上传下载 | Spring Boot |
| notification-service | 8086 | 离线通知、推送 | Spring Boot |
| websocket-gateway | 8888 | WebSocket长连接 | Netty |

### 2.2 客户端模块

#### Tauri 桌面端

```
src-tauri/
├── src/
│   ├── main.rs           # 入口
│   ├── lib.rs            # 库导出
│   ├── commands/         # Tauri命令
│   │   ├── mod.rs
│   │   ├── auth.rs       # 认证命令
│   │   ├── message.rs    # 消息命令
│   │   └── file.rs       # 文件命令
│   ├── models/           # 数据模型
│   ├── db/               # 本地SQLite
│   └── crypto/           # 加密模块
├── src-ui/               # Flutter Web UI (嵌入)
└── tauri.conf.json
```

#### Flutter 移动端

```
lib/
├── main.dart
├── app.dart              # App入口
├── core/                 # 核心模块
│   ├── config/           # 配置
│   ├── network/         # 网络层
│   ├── storage/         # 本地存储
│   └── utils/           # 工具类
├── models/               # 数据模型
├── services/             # 业务服务
│   ├── auth_service.dart
│   ├── message_service.dart
│   ├── user_service.dart
│   └── file_service.dart
├── repositories/         # 数据仓库
├── pages/                # 页面
├── widgets/              # 组件
└── providers/            # 状态管理
```

---

## 3. 核心数据结构

### 3.1 用户表 (t_user)

```sql
CREATE TABLE t_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(128) NOT NULL,
    nickname VARCHAR(50),
    avatar_url VARCHAR(255),
    phone VARCHAR(20),
    email VARCHAR(100),
    status TINYINT DEFAULT 1,  -- 1:正常 2:禁用
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_username (username),
    INDEX idx_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 3.2 好友关系表 (t_friend)

```sql
CREATE TABLE t_friend (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    friend_id BIGINT NOT NULL,
    friend_remark VARCHAR(50),
    status TINYINT DEFAULT 1,  -- 1:正常 2:拉黑
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_friend (user_id, friend_id),
    INDEX idx_friend_id (friend_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 3.3 消息表 (t_message)

```sql
CREATE TABLE t_message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    msg_id VARCHAR(64) UNIQUE NOT NULL,  -- 消息唯一ID (UUID)
    from_user_id BIGINT NOT NULL,
    to_user_id BIGINT NOT NULL,
    chat_type TINYINT NOT NULL,  -- 1:私聊 2:群聊
    chat_id BIGINT NOT NULL,
    msg_type TINYINT NOT NULL,  -- 1:文本 2:图片 3:文件 4:语音
    content TEXT,
    status TINYINT DEFAULT 1,  -- 1:发送中 2:已发送 3:已送达 4:已读 5:发送失败
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_from_user (from_user_id),
    INDEX idx_to_user (to_user_id),
    INDEX idx_chat (chat_type, chat_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 3.4 群组表 (t_group)

```sql
CREATE TABLE t_group (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    group_id VARCHAR(64) UNIQUE NOT NULL,
    group_name VARCHAR(100) NOT NULL,
    avatar_url VARCHAR(255),
    owner_id BIGINT NOT NULL,
    member_count INT DEFAULT 0,
    notice TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_owner (owner_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE t_group_member (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    group_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role TINYINT DEFAULT 1,  -- 1:成员 2:管理员 3:群主
    nickname VARCHAR(50),
    join_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_group_user (group_id, user_id),
    INDEX idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

---

## 4. 通信协议设计

### 4.1 WebSocket 消息格式

```json
{
    "cmd": 1001,
    "seq": "uuid-v4",
    "timestamp": 1709999999999,
    "data": {
        "fromUserId": 12345,
        "toUserId": 67890,
        "chatType": 1,
        "msgType": 1,
        "content": "Hello"
    }
}
```

### 4.2 消息类型定义

| cmd | 说明 | 方向 |
|-----|------|------|
| 1001 | 私聊消息 | C→S, S→C |
| 1002 | 群聊消息 | C→S, S→C |
| 1003 | 消息ACK | S→C |
| 1004 | 消息撤回 | C→S |
| 1005 | 消息已读 | C→S |
| 2001 | 用户上线 | S→C |
| 2002 | 用户离线 | S→C |
| 2003 | 好友状态变更 | S→C |
| 3001 | 心跳 ping | C→S |
| 3002 | 心跳 pong | S→C |

---

## 5. 高可用设计

### 5.1 负载均衡

- **API Gateway**: Nginx + Keepalived
- **WebSocket**: Nginx stream 负载均衡 (IP Hash)
- **服务注册**: Nacos / Consul

### 5.2 缓存策略

```
Redis Key 设计:
- user:session:{userId}      -> 用户会话信息
- user:online:{userId}       -> 用户在线状态
- chat:cache:{chatId}        -> 会话缓存
- message:seq:{chatId}       -> 消息序号
- rate:limit:{userId}        -> 限流计数器
```

### 5.3 消息可靠性

1. **消息持久化**: 发送后立即落库
2. **消息确认**: ACK 机制确保送达
3. **离线消息**: 存储在 MySQL，用户上线后推送
4. **消息重发**: 发送失败自动重试 (3次)

### 5.4 安全措施

- 密码加盐哈希 (BCrypt)
- JWT Token 鉴权
- HTTPS/WSS 传输加密
- 请求签名验签
- 接口限流防刷

---

## 6. 部署架构

```
                        ┌─────────────────┐
                        │   CDN / 静态资源 │
                        └────────┬────────┘
                                 │
                        ┌────────▼────────┐
                        │   Nginx L7     │
                        │  (API + 静态)  │
                        └────────┬────────┘
                                 │
              ┌──────────────────┼──────────────────┐
              │                  │                  │
       ┌──────▼──────┐    ┌──────▼──────┐    ┌──────▼──────┐
       │ Gateway    │    │  WebSocket  │    │  Nginx     │
       │ Cluster    │    │  Cluster    │    │  Stream    │
       └──────┬──────┘    └──────┬──────┘    └──────┬──────┘
              │                  │                  │
              └──────────────────┼──────────────────┘
                                 │
                    ┌────────────▼────────────┐
                    │   Spring Cloud Cluster  │
                    │  (Nacos Service Reg)    │
                    └────────────┬────────────┘
                                 │
    ┌───────────────┬────────────┼────────────┬───────────────┐
    │               │            │            │               │
┌───▼───┐      ┌────▼────┐  ┌───▼────┐  ┌───▼────┐      ┌────▼────┐
│ Auth  │      │ Message │  │  User  │  │ Group │      │  File   │
│Service│      │ Service │  │Service │  │Service│      │ Service │
└───┬───┘      └────┬────┘  └───┬────┘  └───┬────┘      └────┬────┘
    │               │            │            │               │
    └───────────────┴────────────┼────────────┴───────────────┘
                                 │
        ┌───────────┬────────────┼────────────┬───────────┐
        │           │            │            │           │
    ┌───▼───┐   ┌───▼────┐  ┌───▼────┐  ┌───▼────┐  ┌───▼────┐
    │ MySQL │   │ Redis  │  │ Kafka  │  │  MinIO │  │ Prometheus
    │Cluster│   │Cluster │  │Cluster │  │ Cluster│  │+Grafana│
    └───────┘   └────────┘  └────────┘  └────────┘  └────────┘
```

---

## 7. 开发规范

### 7.1 代码结构

```
项目根目录/
├── docs/                    # 文档
├── config/                  # 配置文件
├── deploy/                  # 部署脚本
├── client/
│   ├── tauri/              # 桌面端
│   └── flutter/            # 移动端
└── server/
    ├── api-gateway/        # 网关服务
    ├── auth-service/       # 认证服务
    ├── user-service/       # 用户服务
    ├── message-service/    # 消息服务
    ├── group-service/      # 群组服务
    ├── file-service/       # 文件服务
    └── common/             # 公共模块
```

### 7.2 Git 分支策略

- `main`: 主分支 (生产环境)
- `develop`: 开发分支
- `feature/*`: 功能分支
- `hotfix/*`: 热修复分支
- `release/*`: 发布分支

---

## 8. 下一步工作

- [ ] 搭建开发环境
- [ ] 实现用户模块 (注册/登录)
- [ ] 实现 WebSocket 长连接
- [ ] 实现消息收发
- [ ] 实现群组功能
- [ ] 桌面端/移动端开发
- [ ] 性能测试与优化

---

*文档版本: 1.0*
*更新时间: 2024-01-01*
*作者: 架构设计代理*
