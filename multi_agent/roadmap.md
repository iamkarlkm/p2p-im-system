# 即时通讯系统 - 功能路线图 (Roadmap)

> 最后更新: 2026-03-19 05:32 (第十次规划 - 离线优先与多设备同步)

---

## 系统概览

| 指标 | 数值 |
|------|------|
| 计划总功能数 | 56 |
| 已完成功能数 | 2 (端到端加密, 限流和熔断) |
| 当前完成率 | ~3% |

---

## 已完成功能 ✅

### 1. 端到端加密 (Phase 2)
- RSA密钥对生成
- AES-GCM消息加密
- 密钥交换服务
- **实现文件**: EncryptionService.java, KeyExchangeService.java, encryption.ts, encryption_service.dart

### 2. 限流和熔断 (Phase 3)
- Token Bucket + Sliding Window限流
- 三态熔断器 (CLOSED/OPEN/HALF_OPEN)
- IP级/用户级/API级限流
- 登录防暴力破解
- Netty Pipeline集成
- **实现文件**: RateLimitService.java, CircuitBreaker.java, RateLimitFilter.java等

---

## 第十阶段 - 离线优先与多设备同步 (2026-03-19)

> 核心主题：**让IM系统在不稳定网络下依然可靠**

### 新增功能模块 (Phase 10)

#### F1: 离线消息队列 (Offline Message Queue)
- **技术来源**: Phase 4消息云同步规划 + 现代IM可靠性要求
- **涉及模块**: im-backend (offline-queue-service), im-client
- **核心功能**:
  - 本地消息持久化队列 (SQLite/RocksDB)
  - 网络断开时自动入队
  - 网络恢复后自动重试发送
  - 指数退避重试策略
  - 队列状态可视化 (pending/sending/failed)
  - 队列满时自动清理最旧消息
  - 多队列优先级 (语音>图片>文本)

#### F2: 消息状态追踪 (Message Status Tracking)
- **技术来源**: Phase 6消息序列号机制 + 状态同步需求
- **涉及模块**: im-backend (message-service), im-client
- **核心功能**:
  - 消息多状态机: sending → sent → delivered → read
  - 状态变更事件WebSocket推送
  - 状态回执批量确认 (减少网络开销)
  - 超时未送达自动重发
  - 状态变更历史记录

#### F3: 设备同步管理器 (Device Sync Manager)
- **技术来源**: Phase 4消息草稿同步 + 多设备管理需求
- **涉及模块**: im-backend (sync-service), im-client
- **核心功能**:
  - 设备注册与识别 (设备指纹+Token)
  - 草稿跨设备同步
  - 会话标记状态同步 (已读位置)
  - 消息撤回跨设备同步
  - 设备列表管理 (最多5个活跃设备)
  - 设备在线状态实时感知

#### F4: 消息云端历史存储 (Cloud Message History)
- **技术来源**: Phase 4消息云端历史规划 + message_storage.md知识
- **涉及模块**: im-backend (storage-service), MySQL, MongoDB
- **核心功能**:
  - 消息按时间分片存储 (MySQL冷数据 + MongoDB热数据)
  - 基于seqId的增量同步协议
  - 首次登录全量拉取 + 增量更新
  - 历史消息分页加载 (每次50条)
  - 消息清理策略 (可配置保留天数)

#### F5: Sync冲突解决引擎 (Sync Conflict Resolution)
- **技术来源**: Phase 4多设备同步 + 分布式一致性
- **涉及模块**: im-backend (sync-service)
- **核心功能**:
  - 基于时间戳的Last-Write-Wins策略
  - 消息编辑冲突检测
  - 消息删除冲突处理
  - 群组成员变更冲突解决
  - 冲突日志记录与审计

#### F6: 网络状态感知服务 (Network Awareness Service)
- **技术来源**: Phase 10离线优先主题 + 现代移动端需求
- **涉及模块**: im-backend, im-client
- **核心功能**:
  - 客户端网络状态实时上报
  - 服务器端连接质量评估
  - 自动降级策略 (弱网模式下减少心跳频率)
  - 网络切换平滑过渡 (WiFi↔蜂窝)
  - 长连接保活优化

---

### 第十阶段规划详情

#### F1: 离线消息队列 (Offline Message Queue)
**目录结构**:
```
im-backend/src/main/java/com/im/backend/queue/
├── OfflineMessageQueue.java        # 队列核心
├── MessageQueueConfig.java        # 队列配置
├── QueuePersistence.java          # 持久化层
├── RetryStrategy.java             # 重试策略
├── QueueManager.java              # 队列管理器
└── QueueMetrics.java              # 队列监控指标

im-client/offline/
├── OfflineQueue.ts                # 客户端队列
├── LocalDatabase.ts               # 本地存储
├── SyncManager.ts                # 同步管理器
└── NetworkListener.ts            # 网络监听
```

**技术要点**:
- 队列存储: Redis List (服务器端) + SQLite (客户端)
- 重试策略: 指数退避 1s → 2s → 4s → 8s → 16s (最大)
- 队列容量: 客户端最多500条，服务器端按用户级别配额
- 优先级: 语音 > 图片 > 视频 > 文本

#### F2: 消息状态追踪 (Message Status Tracking)
**目录结构**:
```
im-backend/src/main/java/com/im/backend/status/
├── MessageStatus.java             # 状态枚举与定义
├── MessageStatusService.java     # 状态服务
├── StatusEventPublisher.java     # 状态变更发布
├── StatusBatchAckHandler.java    # 批量确认处理
└── StatusTimeoutMonitor.java     # 超时监控
```

**消息状态机**:
```
[sending] → [sent] → [delivered] → [read]
    ↓
  [failed] → 重试 → [sending]
```

**状态回执协议**:
- 客户端批量确认: 每收到N条或每T秒发送一次ACK
- 服务器聚合确认: 避免回执风暴
- 超时阈值: sending→sent (10s), sent→delivered (30s)

#### F3: 设备同步管理器 (Device Sync Manager)
**目录结构**:
```
im-backend/src/main/java/com/im/backend/sync/
├── DeviceRegistry.java            # 设备注册表
├── DeviceTokenService.java       # Token管理
├── DraftSyncService.java         # 草稿同步
├── ReadPositionSync.java         # 已读位置同步
└── DeviceSessionManager.java     # 会话管理
```

**设备管理策略**:
- 最大活跃设备: 5个 (免费版) / 无限 (付费版)
- Token有效期: 30天自动刷新
- 设备离线超过90天自动归档

#### F4: 消息云端历史存储 (Cloud Message History)
**目录结构**:
```
im-backend/src/main/java/com/im/backend/storage/
├── MessageStorageService.java     # 存储服务
├── HotColdDataSeparation.java    # 冷热分离
├── IncrementalSyncProtocol.java  # 增量同步协议
├── MessageRetentionPolicy.java   # 保留策略
└── HistoryArchiveService.java    # 历史归档
```

**存储分层**:
- 热数据 (最近7天): MongoDB, SSD存储
- 温数据 (7-90天): MySQL, 普通磁盘
- 冷数据 (90天+): 归档存储，可选压缩

#### F5: Sync冲突解决引擎 (Sync Conflict Resolution)
**目录结构**:
```
im-backend/src/main/java/com/im/backend/sync/
├── ConflictDetector.java          # 冲突检测
├── LastWriteWinsResolver.java    # LWW解决器
├── EditConflictResolver.java     # 编辑冲突解决
├── DeleteConflictResolver.java   # 删除冲突解决
└── ConflictLog.java              # 冲突日志
```

**冲突类型处理**:
- 消息编辑冲突: 保留最新版本，历史版本可追溯
- 消息删除冲突: 以删除为准
- 群组消息冲突: 合并消息列表，去重

#### F6: 网络状态感知服务 (Network Awareness Service)
**目录结构**:
```
im-backend/src/main/java/com/im/backend/network/
├── NetworkStateRegistry.java      # 网络状态注册
├── ConnectionQualityTracker.java # 连接质量追踪
├── AdaptiveHeartbeatService.java # 自适应心跳
└── NetworkTransitionHandler.java # 网络切换处理
```

**自适应心跳策略**:
- 强网: 心跳间隔 30s
- 弱网: 心跳间隔 120s
- 离线: 停止心跳，改为长轮询

---

### 第十阶段技术依赖

```
Phase 10 依赖关系:
                    ┌─────────────────┐
                    │  F1 离线队列     │
                    └────────┬────────┘
                             │
          ┌──────────────────┼──────────────────┐
          ▼                  ▼                  ▼
   ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
   │ F2 状态追踪   │  │ F3 设备同步  │  │ F6 网络感知  │
   └───────┬──────┘  └───────┬──────┘  └──────┬───────┘
           │                 │                 │
           └─────────────────┼─────────────────┘
                             ▼
                    ┌─────────────────┐
                    │ F4 云端历史存储  │
                    └────────┬────────┘
                             │
                             ▼
                    ┌─────────────────┐
                    │ F5 冲突解决引擎 │
                    └─────────────────┘
```

**前置条件**:
- F2需要F1提供基础消息队列
- F4需要数据库分片层 (Phase 9已有)
- F5需要F4提供历史数据支撑

---

### 第十阶段验收标准

- [ ] 客户端断开网络后，消息仍可发送，恢复后自动补发
- [ ] 消息状态 (sent/delivered/read) 全链路可追踪
- [ ] 切换设备后，草稿和已读位置自动同步
- [ ] 新设备登录后，能拉取完整历史消息
- [ ] 多设备同时编辑消息时，冲突正确处理
- [ ] 弱网环境下，心跳频率自动调整

---

## 历史功能清单

### Phase 1 (基础功能)
- 桌面端完整功能 (登录注册、联系人列表、聊天窗口)
- 消息队列集成
- 消息推送服务
- 实时音视频基础
- 消息已读/未读同步
- 消息搜索功能
- WebSocket连接增强
- 分布式数据库集成

### Phase 2 (已完成 ✅)
- 端到端加密

### Phase 3 (已完成 ✅)
- 限流和熔断

### Phase 4 (多设备与消息历史)
- 消息云端历史与多设备同步
- 正在输入状态 (Typing Indicator)
- 消息表情回应
- 消息草稿同步

### Phase 5 (高级社交与安全)
- 消息翻译
- 消息转发
- 聊天置顶
- 联系人置顶
- 截屏通知
- 消息举报与内容审核

### Phase 6 (生产级基础设施)
- 统一推送服务
- Webhook系统
- 消息序列号机制
- 数据备份与恢复
- 一对一视频通话
- ES消息全文搜索

### Phase 7 (安全加固)
- 阅后即焚
- 定时发送消息
- 消息对话回复链
- 登录异常告警
- 登录设备管理
- 两步验证 (2FA)

### Phase 8 (生产力工具)
- 会话分组/文件夹
- 对话笔记
- 消息提醒/稍后回复
- 对话批量管理

### Phase 9 (可观测性与分布式)
- 应用性能监控 (APM)
- Kafka消息队列集成
- 分布式数据库层
- 健康检查与指标API
- 消息已读回执
- 消息表情回应
- 群组视频/语音通话
- 智能消息过滤

### Phase 10 (离线优先与多设备同步)
- 离线消息队列
- 消息状态追踪
- 设备同步管理器
- 消息云端历史存储
- Sync冲突解决引擎
- 网络状态感知服务

---

## 总进度

| 阶段 | 功能数 | 完成数 | 进度 |
|------|--------|--------|------|
| Phase 1 | 8 | 0 | 0% |
| Phase 2 | 1 | 1 | 100% |
| Phase 3 | 1 | 1 | 100% |
| Phase 4 | 4 | 0 | 0% |
| Phase 5 | 6 | 0 | 0% |
| Phase 6 | 6 | 0 | 0% |
| Phase 7 | 6 | 0 | 0% |
| Phase 8 | 4 | 0 | 0% |
| Phase 9 | 8 | 0 | 0% |
| Phase 10 | 6 | 0 | 0% |
| **总计** | **50** | **2** | **~3%** |

---

*路线图持续更新中*
