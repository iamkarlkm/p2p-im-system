# 消息存储方案学习笔记

## 概述
消息存储是IM系统的核心功能之一，需要支持海量消息的高效存储和快速查询。消息存储方案直接影响IM系统的性能和用户体验。

## 存储方案

### 1. 关系型数据库(MySQL)
- **优点**：事务支持、查询灵活、成熟稳定
- **缺点**：写入性能有限、扩展困难
- **适用场景**：用户数据、好友关系、群组信息

### 2. 文档数据库(MongoDB)
- **优点**：schema灵活、水平扩展、支持全文检索
- **缺点**：事务较弱
- **适用场景**：消息存储、聊天记录

### 3. 内存数据库(Redis)
- **优点**：高速读写、数据结构丰富
- **缺点**：内存有限、成本高
- **适用场景**：缓存、未读数、在线状态

### 4. 时序数据库
- **优点**：高效写入、压缩存储
- **适用场景**：消息统计、日志存储

## 消息表设计

### 单聊消息表
```sql
CREATE TABLE message_single (
    id BIGINT PRIMARY KEY,
    from_user_id BIGINT NOT NULL,
    to_user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    msg_type INT DEFAULT 1,  -- 1:text, 2:image, 3:voice, etc.
    created_at BIGINT NOT NULL,
    INDEX idx_from_to (from_user_id, to_user_id, created_at)
);
```

### 群聊消息表
```sql
CREATE TABLE message_group (
    id BIGINT PRIMARY KEY,
    group_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    msg_type INT DEFAULT 1,
    created_at BIGINT NOT NULL,
    INDEX idx_group (group_id, created_at)
);
```

## 消息存储架构

```
┌─────────────┐
│   写入请求   │
└──────┬──────┘
       │
       ▼
┌─────────────┐    ┌─────────────┐
│   Redis     │───▶│  消息确认   │
│  (消息缓存) │    └─────────────┘
└──────┬──────┘
       │
       ▼
┌─────────────┐    ┌─────────────┐
│   MySQL     │    │  MongoDB   │
│ (核心数据)  │    │ (消息历史)  │
└─────────────┘    └─────────────┘
```

## 消息历史方案

### 1. 本地存储
- 消息存储在客户端本地
- 优点：离线查看、无需网络
- 缺点：多端不同步

### 2. 云端存储
- 消息存储在服务器
- 优点：多端同步、随时查看
- 缺点：需要网络

### 3. 混合存储
- 最近消息云端+历史消息归档
- 平衡性能和成本

## 消息查询优化

### 1. 分页查询
```sql
SELECT * FROM message_single
WHERE from_user_id = ? AND to_user_id = ?
AND created_at < ?
ORDER BY created_at DESC
LIMIT 20;
```

### 2. 全文检索
- 使用Elasticsearch
- 支持关键词搜索
- 消息内容检索

### 3. 消息漫游
- 跨设备消息同步
- 云端消息同步

## 消息删除策略

### 1. 逻辑删除
- 标记删除、可恢复
- 占用存储空间

### 2. 物理删除
- 彻底删除、释放空间
- 定期清理历史消息

### 3. 定时清理
- 按时间清理
- 按会话清理

## 学习资源

### 开源IM支持哪些数据库存储方案?
- https://www.easemob.com/news/17029

### 开源IM关于消息存储归档方案
- https://www.easemob.com/news/21991
