# 即时通讯系统 Roadmap

## 项目概述
- **后端**: Spring Boot + Netty WebSocket
- **桌面端**: Tauri (HTML/CSS/JS)
- **移动端**: Flutter

---

## 当前已实现功能

### 后端 (im-backend) ✅
| 模块 | 功能 |
|------|------|
| 用户认证 | 登录、注册 (JWT) |
| 用户管理 | 用户信息CRUD、搜索、密码管理 |
| 好友管理 | 添加、删除、列表、好友请求 |
| 群组管理 | 创建、解散、成员管理 |
| 消息管理 | 消息发送、接收、撤回 |
| 实时通讯 | Netty WebSocket |
| 文件服务 | 图片/文件/语音上传下载 |
| 通知服务 | 通知创建、已读、未读 |
| 在线状态 | 用户在线状态跟踪 |
| 安全配置 | Spring Security + Redis |

### 桌面端 (im-desktop) ⚠️
| 模块 | 功能 |
|------|------|
| 基础框架 | 基础HTML/CSS/JS结构 |

### 移动端 (im-mobile) ✅
| 模块 | 功能 |
|------|------|
| 登录 | 登录界面 |
| 好友 | 好友列表、好友请求 |
| 群组 | 群组列表、创建群组 |
| 聊天 | 聊天界面 |
| 设置 | 设置界面 |

---

## 下一阶段规划

### 1. 桌面端完整功能 (HIGH) - 🔴 第一优先

**功能描述**: 完成桌面端的完整功能，包括登录、联系人、聊天

#### 1.1 桌面端登录注册 - 🔴 HIGH
- **功能描述**: 完整的桌面端登录和注册界面
- **涉及文件**:
  - `src/index.html` - 添加登录/注册表单
  - `src/styles.css` - 登录注册样式
  - `src/main.js` - 登录注册逻辑
  - `src/api.js` - API调用封装 (新建)
- **功能点**:
  - 登录表单（用户名/密码）
  - 注册表单（用户名/密码/确认密码）
  - 登录状态管理（localStorage）
  - 错误提示

#### 1.2 桌面端联系人列表 - 🟡 MEDIUM
- **功能描述**: 显示好友和群组列表
- **涉及文件**:
  - `src/index.html` - 添加联系人列表区域
  - `src/styles.css` - 列表样式
  - `src/contacts.js` - 联系人管理 (新建)
- **功能点**:
  - 好友列表展示
  - 群组列表展示
  - 搜索好友/群组
  - 添加好友入口

#### 1.3 桌面端聊天窗口 - 🟡 MEDIUM
- **功能描述**: 完整的聊天界面
- **涉及文件**:
  - `src/chat.js` - 聊天逻辑 (新建)
  - `src/websocket.js` - WebSocket连接 (新建)
- **功能点**:
  - 消息列表展示
  - 消息输入和发送
  - WebSocket实时消息
  - 消息时间戳
  - 发送状态显示

---

### 2. 消息队列集成 (新功能) - 🔴 HIGH

**功能描述**: 引入消息队列实现消息异步处理、可靠投递和系统解耦

**技术方案** (基于message_queue.md知识):
- 引入RocketMQ或Kafka作为消息队列
- 消息发布/订阅模式
- 延迟消息支持（消息撤回、定时提醒）
- 消息投递确认机制

**涉及文件**:
- 新增 `config/MQConfig.java` - 消息队列配置
- 新增 `mq/MessageProducer.java` - 消息生产者
- 新增 `mq/MessageConsumer.java` - 消息消费者
- 修改 `service/MessageService.java` - 集成消息队列

**功能点**:
- 消息异步写入存储
- 离线消息队列处理
- 消息撤回延迟执行
- 群消息广播优化

---

### 3. 消息推送服务 (新功能) - 🔴 HIGH

**功能描述**: 实现离线消息推送，确保用户及时收到通知

**技术方案** (基于message_push.md知识):
- 集成APNs（苹果推送）
- 集成安卓厂商通道（华为、小米、OPPO、vivo）
- 集成第三方推送（极光、个推）
- 推送消息合并和离线消息策略

**涉及文件**:
- 新增 `config/PushConfig.java` - 推送配置
- 新增 `service/PushService.java` - 推送服务
- 新增 `service/DeviceTokenService.java` - 设备Token管理
- 新增 `handler/PushMessageHandler.java` - 推送消息处理

**功能点**:
- 设备Token注册和管理
- 离线消息推送通知
- 推送消息合并
- 免打扰时段设置

---

### 4. 实时音视频基础 (新功能) - 🟡 MEDIUM

**功能描述**: 预留音视频通话接口，为后续语音/视频通话功能做准备

**技术方案** (基于realtime_audio_video.md知识):
- 集成WebRTC信令服务器
- 预留音视频通话接口
- 集成第三方RTC服务（腾讯云TRTC、声网Agora）

**涉及文件**:
- 新增 `controller/RTCController.java` - 音视频控制接口
- 新增 `service/RTCService.java` - 音视频服务
- 新增 `netty/RTCHandler.java` - WebRTC信令处理

**功能点**:
- 音视频通话邀请/接听/拒绝
- WebRTC信令转发
- 通话状态管理

---

### 5. 消息已读/未读同步 (新功能) - 🟡 MEDIUM

**功能描述**: 实现消息已读未读状态同步，确保多端消息状态一致

**技术方案** (基于message_storage.md知识):
- 消息状态存储（已读/未读/已送达）
- 已读回执机制
- 多端状态同步

**涉及文件**:
- 新增 `entity/MessageStatus.java` - 消息状态实体
- 新增 `repository/MessageStatusRepository.java` - 消息状态数据访问
- 新增 `service/MessageStatusService.java` - 消息状态服务
- 修改 `controller/MessageController.java` - 添加已读接口

**功能点**:
- 消息已读状态标记
- 未读消息计数
- 已读回执发送
- 批量已读处理

---

### 6. 消息搜索功能 (新功能) - 🟢 LOW

**功能描述**: 实现消息全文搜索，支持关键词搜索聊天记录

**技术方案** (基于message_storage.md知识):
- 引入Elasticsearch或MySQL全文索引
- 支持关键词搜索
- 搜索结果高亮

**涉及文件**:
- 新增 `service/MessageSearchService.java` - 消息搜索服务
- 新增 `controller/SearchController.java` - 搜索接口
- 新增 `config/ElasticsearchConfig.java` - ES配置（可选）

**功能点**:
- 聊天记录搜索
- 搜索结果分页
- 关键词高亮显示

---

### 7. WebSocket连接增强 (新功能) - 🟢 LOW

**功能描述**: 增强WebSocket连接的稳定性和可靠性

**技术方案** (基于websocket.md知识):
- 心跳保活机制
- 自动重连
- 连接状态管理
- 消息可靠性保证

**涉及文件**:
- 修改 `netty/WebSocketServer.java` - 添加心跳
- 修改 `netty/ChatMessageHandler.java` - 添加重连逻辑
- 新增 `config/WebSocketConfig.java` - WebSocket配置

**功能点**:
- 心跳检测
- 断线自动重连
- 连接状态提示
- 消息补发机制

---

### 8. 分布式数据库集成 (新功能) - 🟢 LOW

**功能描述**: 引入分布式数据库架构，支持海量数据存储

**技术方案** (基于distributed_database.md知识):
- MySQL读写分离
- 分库分表策略（按用户ID）
- Redis缓存策略

**涉及文件**:
- 新增 `config/DataSourceConfig.java` - 多数据源配置
- 新增 `config/RedisConfig.java` - Redis集群配置
- 修改 `entity/` - 添加分片键

**功能点**:
- 读写分离配置
- 缓存策略优化
- 分库分表支持

---

### 9. 端到端加密 (新功能) - 🟡 MEDIUM

**功能描述**: 实现端到端加密，确保消息内容只有通信双方可以读取

**技术方案** (基于end_to_end_encryption.md知识):
- 集成 Signal Protocol（libsignal-protocol-java）
- **X3DH 密钥协商**：支持离线发起方建立安全会话（PreKeyBundle 机制）
- **Double Ratchet 算法**：对称棘轮（每消息密钥前推）+ DH棘轮（定期刷新密钥链）
- **三层密钥架构**：Identity Key（长期）→ Signed PreKey（中期）→ PreKey（一次性）
- **Curve25519 + AES-256-GCM**：高性能椭圆曲线 + AEAD 加密
- **密钥本地存储**：私钥不上传服务器，仅上传公钥到 KeyRegistry
- 客户端密钥：Tauri 桌面端用 Rust crypto 库，移动端用 flutter_secure_storage

**涉及文件**:
- 新增 `security/E2EEInitService.java` - 客户端 E2EE 初始化（密钥生成）
- 新增 `security/SessionManager.java` - Signal Protocol 会话管理
- 新增 `security/E2EEncryptUtil.java` - 加密工具类（SessionCipher 封装）
- 新增 `security/SecureStorageService.java` - 密钥安全存储（DPAPI/Keychain）
- 新增 `controller/KeyRegistryController.java` - 公钥注册/获取 API
- 修改 `service/MessageService.java` - 消息加密/解密集成
- 修改 `entity/Message.java` - 添加加密消息字段
- 新增 `config/E2EEConfig.java` - E2EE 配置（密钥轮换周期等）
- 新增 `mq/PreKeyReplenishmentProducer.java` - PreKey 补充消息队列
- 新增 `mq/PreKeyReplenishmentConsumer.java` - PreKey 补充消费者

**功能点**:
- 客户端安装时密钥生成（Identity Key + Signed PreKey + 100 PreKeys）
- X3DH 会话建立（PreKeyBundle 获取 → 密钥协商 → 会话初始化）
- Double Ratchet 消息加密（每条消息独立密钥，前向保密）
- 消息加密发送与接收解密全流程
- Signed PreKey 月度轮换
- PreKey 池自动补充（低于阈值触发）
- 多设备独立密钥链（每个设备有独立 Identity/Session）
- 密钥安全存储（桌面端 DPAPI，移动端 Keychain）

**依赖库**:
- libsignal-protocol-java 或 libsignal-protocol-c
- Curve25519 实现（curve25519-dalek）
- 传输层 TLS 1.3

---

### 10. 限流和熔断 (新功能) - 🔴 HIGH

**功能描述**: 实现网关限流和熔断机制，保护系统稳定性

**技术方案** (基于rate_limiting_circuit_breaker.md知识):
- 引入 Sentinel 1.8+（推荐）或 Hystrix
- **分层限流架构**：客户端层（本地限流+指数退避重试）+ 网关层（IP限流+Token限流+WebSocket连接数）+ 业务层（接口级+用户级+群组级）+ 存储层（连接池限制）
- **限流策略**：直接拒绝（明确处理能力）、冷启动（突发流量）、匀速器（消息队列/推送）
- **熔断策略**：慢调用比例（数据库）、异常比例（第三方API）、异常数（内部服务）
- **熔断器状态机**：CLOSED → OPEN → HALF-OPEN → CLOSED，支持事件监听
- **静默期设计**：MinRequestAmount 避免偶发慢请求误判，建议设为 10
- **Netty WebSocket 集成**：SphU.entry() 包装消息处理，BlockException 统一处理
- **推荐配置**：消息发送 1000QPS、用户全局 100QPS、WebSocket 并发 5万、数据库慢调用 RT>200ms+30%比例熔断30秒

**涉及文件**:
- 新增 `config/SentinelConfig.java` - Sentinel配置（InitFunc 加载规则）
- 新增 `config/RateLimitConfig.java` - 限流规则常量（QPS阈值、熔断参数）
- 新增 `config/DegradeConfig.java` - 熔断降级规则配置
- 新增 `handler/SentinelFlowHandler.java` - 网关层限流处理（Netty Pipeline）
- 新增 `handler/SentinelDegradeHandler.java` - 熔断降级处理
- 新增 `handler/WebSocketRateLimitHandler.java` - WebSocket 消息限流
- 新增 `service/SentinelAlarmService.java` - 熔断/限流告警通知
- 新增 `aspect/SentinelAspect.java` - AOP 切面集成 Sentinel 注解
- 修改 `netty/WebSocketServer.java` - 添加 Sentinel Pipeline
- 新增 `config/SentinelDashboardConfig.java` - Dashboard 连接配置

**功能点**:
- 请求限流
- 熔断降级
- 限流规则配置
- 限流日志和监控

---

### 11. 阅后即焚 (新功能) - 🟢 LOW

**功能描述**: 实现阅后即焚功能，增强隐私保护

**技术方案**:
- 消息设置销毁计时器
- 双方确认阅读后开始倒计时
- 计时结束后自动删除
- 本地和服务器双重删除

**涉及文件**:
- 新增 `entity/BurnMessage.java` - 阅后即焚消息实体
- 新增 `repository/BurnMessageRepository.java` - 数据访问
- 新增 `service/BurnMessageService.java` - 阅后即焚服务
- 修改 `controller/MessageController.java` - 添加阅后即焚接口

**功能点**:
- 阅后即焚计时设置
- 消息自动销毁
- 已焚消息标记
- 销毁状态同步

---

### 12. 消息引用/回复 (新功能) - 🟢 LOW

**功能描述**: 实现消息引用和回复功能，增强聊天体验

**技术方案**:
- 引用消息ID关联
- 回复消息上下文展示
- 引用消息预览

**涉及文件**:
- 修改 `entity/Message.java` - 添加引用字段
- 修改 `service/MessageService.java` - 集成引用逻辑
- 修改 `controller/MessageController.java` - 添加引用接口

**功能点**:
- 引用消息
- 回复消息
- 引用上下文展示

---

## 优先级总结

| 优先级 | 功能 | 所属模块 | 知识来源 |
|--------|------|----------|----------|
| 🔴 HIGH | 桌面端登录注册 | 桌面端 | 现有功能 |
| 🔴 HIGH | 桌面端联系人列表 | 桌面端 | 现有功能 |
| 🔴 HIGH | 桌面端聊天窗口 | 桌面端 | 现有功能 |
| 🔴 HIGH | 消息队列集成 | 后端 | message_queue.md |
| 🔴 HIGH | 消息推送服务 | 后端 | message_push.md |
| 🔴 HIGH | 消息云端历史与多设备同步 (新增) | 后端+客户端 | message_storage.md |
| 🟡 MEDIUM | 实时音视频基础 | 后端/客户端 | realtime_audio_video.md |
| 🟡 MEDIUM | 消息已读/未读同步 | 后端 | message_storage.md |
| 🟡 MEDIUM | 端到端加密 | 后端 | end_to_end_encryption.md |
| 🟡 MEDIUM | 限流和熔断 | 后端 | rate_limiting_circuit_breaker.md |
| 🟡 MEDIUM | 正在输入状态 (新增) | 后端+客户端 | websocket.md |
| 🔴 HIGH | 消息内容审核与反垃圾系统 (新增) | 后端 | content_moderation_spam_detection.md |
| 🔴 HIGH | 分布式事务保障模块 (新增) | 后端 | distributed_transaction_consistency.md |
| 🟢 LOW | 消息搜索功能 | 后端 | message_storage.md |
| 🟢 LOW | WebSocket连接增强 | 后端 | websocket.md |
| 🟢 LOW | 分布式数据库集成 | 后端 | distributed_database.md |
| 🟢 LOW | 阅后即焚 | 后端 | 新功能 |
| 🟢 LOW | 消息引用/回复 | 后端/客户端 | 新功能 |
| 🟢 LOW | 消息表情回应 (新增) | 后端+客户端 | 新功能 |
| 🟢 LOW | 消息草稿同步 (新增) | 后端+客户端 | 新功能 |

---

## 开发顺序建议

1. **第一阶段**: 桌面端完整功能（登录+联系人+聊天）
2. **第二阶段**: 消息队列集成（后端）
3. **第三阶段**: 消息推送服务（后端）
4. **第四阶段**: 消息云端历史与多设备同步
5. **第五阶段**: 消息已读/未读同步
6. **第六阶段**: 正在输入状态
7. **第七阶段**: 实时音视频基础
8. **第八阶段**: 消息搜索功能
9. **第九阶段**: WebSocket连接增强
10. **第十阶段**: 分布式数据库集成

---

## 新增功能模块说明

### 消息队列集成模块 (NEW)
- **模块名称**: MQ Integration
- **功能**: 异步消息处理、可靠投递、系统解耦
- **技术**: RocketMQ/Kafka

### 消息推送服务模块 (NEW)
- **模块名称**: Push Service
- **功能**: 离线消息通知、多通道推送
- **技术**: APNs、厂商通道、第三方推送

### 实时音视频基础模块 (NEW)
- **模块名称**: RTC Foundation
- **功能**: 音视频通话接口预留
- **技术**: WebRTC、信令服务器

### 消息已读/未读同步模块 (NEW)
- **模块名称**: Message Status Sync
- **功能**: 消息状态同步、多端一致
- **技术**: 消息状态表、已读回执

### 消息搜索模块 (NEW)
- **模块名称**: Message Search
- **功能**: 聊天记录全文搜索
- **技术**: Elasticsearch/全文索引

### WebSocket增强模块 (NEW)
- **模块名称**: WebSocket Enhancement
- **功能**: 心跳保活、自动重连
- **技术**: Netty心跳检测

### 分布式数据库模块 (NEW)
- **模块名称**: Distributed Database
- **功能**: 读写分离、分库分表
- **技术**: MySQL主从、Redis缓存

### 端到端加密模块 (NEW)
- **模块名称**: E2E Encryption
- **功能**: 消息内容加密、密钥交换
- **技术**: Signal Protocol、Diffie-Hellman

### 限流和熔断模块 (NEW)
- **模块名称**: Rate Limiting & Circuit Breaker
- **功能**: 分层限流（IP/用户/接口/群组）、熔断降级、系统自适应保护、告警通知
- **技术**: Sentinel 1.8+（Java/Go多语言）、Netty Pipeline集成、熔断器状态机（CLOSED/OPEN/HALF-OPEN）
- **配置**: Nacos/ZooKeeper 动态规则推送、Sentinel Dashboard 可视化监控
- **IM专属**: WebSocket 连接数限制、消息发送频率控制、数据库慢调用熔断、第三方推送熔断降级

### 阅后即焚模块 (NEW)
- **模块名称**: Burn After Reading
- **功能**: 消息自动销毁、隐私保护
- **技术**: 定时器、本地/服务器双重删除

### 消息引用/回复模块 (NEW)
- **模块名称**: Message Reply
- **功能**: 消息引用、回复、上下文展示
- **技术**: 消息关联、上下文渲染

---

*最后更新: 2026-03-19 09:52*

---

## 第四阶段：多设备与消息历史 (2026-03-18 新增)

### 13. 消息云端历史与多设备同步 (新功能) - 🔴 HIGH

**功能描述**: 消息云端存储、跨设备漫游、增量同步，让用户在任何设备上都能访问完整聊天历史

**技术方案** (基于message_storage.md知识):
- 消息云端存储（热数据MySQL + 冷数据MongoDB归档）
- 增量同步协议（同步点机制 + 增量消息拉取）
- 多设备消息状态一致性
- 跨设备消息已读状态同步

**涉及文件**:
- 新增 `service/MessageHistoryService.java` - 云端消息存储服务
- 新增 `service/SyncService.java` - 多设备同步服务
- 新增 `controller/SyncController.java` - 同步控制接口
- 新增 `entity/DeviceSession.java` - 设备会话实体
- 新增 `repository/MessageHistoryRepository.java` - 历史消息数据访问
- 修改 `service/MessageService.java` - 集成云端存储

**功能点**:
- 新设备登录自动拉取历史消息
- 多设备消息状态一致性
- 增量同步（只同步新消息，减少流量）
- 跨设备消息已读状态同步
- 消息漫游（任意设备查看全部历史）

---

### 14. 正在输入状态 (新功能) - 🟡 MEDIUM

**功能描述**: 实时显示对方正在输入状态，提升聊天体验

**技术方案** (基于websocket.md知识):
- Typing状态WebSocket广播
- 输入状态防抖动（合并短时间输入）
- 3秒超时自动消失

**涉及文件**:
- 新增 `service/TypingStatusService.java` - Typing状态服务
- 新增 `netty/TypingHandler.java` - Typing消息处理
- 修改 `netty/ChatMessageHandler.java` - 添加Typing消息类型
- 修改 `ws/WsMessage.java` - 添加Typing消息类型

**功能点**:
- 单聊输入状态广播
- 群聊输入状态广播（显示正在输入的成员）
- 输入状态防抖动（debounce）
- 3秒超时自动清除

---

### 15. 消息表情回应 (新功能) - 🟢 LOW

**功能描述**: 消息表情回应/反应，增加互动乐趣

**技术方案**:
- 表情反应数据模型
- 反应聚合统计
- 实时反应通知

**涉及文件**:
- 新增 `entity/MessageReaction.java` - 反应实体
- 新增 `repository/MessageReactionRepository.java` - 反应数据访问
- 新增 `service/ReactionService.java` - 反应服务
- 新增 `controller/ReactionController.java` - 反应接口
- 修改 `entity/Message.java` - 添加反应关联

**功能点**:
- 预设表情反应（👍❤️😂😮😢🎉）
- 反应统计气泡展示
- 反应变更WebSocket通知
- 表情选择器UI

---

### 16. 消息草稿同步 (新功能) - 🟢 LOW

**功能描述**: 多设备间聊天草稿自动同步

**技术方案**:
- 草稿存储（Redis缓存 + MySQL持久化）
- 草稿同步协议
- 自动清除策略

**涉及文件**:
- 新增 `entity/Draft.java` - 草稿实体
- 新增 `repository/DraftRepository.java` - 草稿数据访问
- 新增 `service/DraftService.java` - 草稿服务
- 新增 `controller/DraftController.java` - 草稿接口
- 修改 `websocket/WsMessage.java` - 添加草稿同步消息类型

**功能点**:
- 输入框草稿实时保存（防丢失）
- 切换设备草稿不丢失
- 发送后自动清除草稿
- 草稿同步到所有在线设备

### 消息云端历史与多设备同步模块 (NEW)
- **模块名称**: Message Cloud History & Multi-Device Sync
- **功能**: 消息云端存储、跨设备漫游、增量同步
- **技术**: MySQL+MongoDB分层存储、增量同步协议、设备会话管理

### 正在输入状态模块 (NEW)
- **模块名称**: Typing Indicator
- **功能**: 实时显示对方正在输入状态
- **技术**: WebSocket Typing状态广播、防抖动、3秒超时

### 消息表情回应模块 (NEW)
- **模块名称**: Message Reactions
- **功能**: 消息表情反应、增加互动乐趣
- **技术**: 反应实体、WebSocket通知、聚合统计

### 消息草稿同步模块 (NEW)
- **模块名称**: Draft Sync
- **功能**: 多设备间聊天草稿自动同步
- **技术**: Redis+MySQL草稿存储、实时同步协议

---

### 17. 消息内容审核与反垃圾系统 (新功能) - 🔴 HIGH

**功能描述**: 实现完整的消息内容审核与反垃圾系统，保障平台健康运营，拦截涉政、涉黄、涉暴、垃圾广告等违规内容

**技术方案** (基于content_moderation_spam_detection.md知识):
- **分层防御架构**：客户端层（本地过滤）+ 网关层（频率限制/黑名单）+ 消息服务层（路由分发）+ 审核服务层（AI推理）+ 存储层（词库/记录）
- **同步+异步混合审核**：高风险内容同步审核（100-500ms），普通内容异步审核（<10ms），AI模型实时推理+人工复核
- **文本审核算法**：DFA算法（高效敏感词匹配）+ AC自动机（大规模词库）+ Trie树+Hash优化 + 变体词识别（谐音/简繁/符号干扰/拼音/emoji）
- **AI文本分类**：基于BERT/ERNIE的多标签分类（色情/暴力/政治/广告/仇恨/诈骗），SimHash文本相似度检测
- **图片审核**：敏感词OCR识别 → 肤色检测（YCbCr色彩空间）→ 场景识别 → 物体检测 → 图片鉴伪（AI生成检测）
- **视频/音频审核**：关键帧提取（每秒1帧）+ 并行帧审核 + 语音识别(ASR) → 文本审核 + 声纹分析（敏感人物）
- **反垃圾策略**：用户行为分析（发送频率/相似内容/新好友发送率）+ 群发控制（消息频率/好友请求频率）+ 相似内容拦截
- **机器学习特征工程**：文本特征（长度/URL/手机号/emoji占比）+ 用户特征（发送频率/好友数量/账号年龄）+ 上下文特征（历史消息相似度）
- **人工审核系统**：审核工作台 + 智能任务分配（负载均衡/优先级调度）+ 举报反馈系统

**涉及文件**:
- 新增 `config/ModerationConfig.java` - 审核服务配置（同步/异步比例、阈值）
- 新增 `filter/DFAFilter.java` - DFA敏感词过滤
- 新增 `filter/ACAutoMachineFilter.java` - AC自动机敏感词过滤
- 新增 `filter/VariantWordRecognizer.java` - 变体词识别（谐音/拼音/简繁）
- 新增 `filter/RegexModerationFilter.java` - 正则表达式审核
- 新增 `service/TextModerationService.java` - 文本审核服务
- 新增 `service/ImageModerationService.java` - 图片审核服务
- 新增 `service/VideoModerationService.java` - 视频审核服务
- 新增 `service/AudioModerationService.java` - 音频审核服务
- 新增 `service/AntiSpamService.java` - 反垃圾检测服务
- 新增 `service/HumanReviewService.java` - 人工审核服务
- 新增 `controller/ModerationController.java` - 审核控制接口
- 新增 `controller/ReportController.java` - 用户举报接口
- 新增 `entity/ModerationTask.java` - 审核任务实体
- 新增 `entity/SensitiveWord.java` - 敏感词实体
- 新增 `entity/SpamReport.java` - 举报记录实体
- 新增 `ml/TextClassifier.java` - 文本分类模型（BERT/ERNIE）
- 新增 `ml/ImageClassifier.java` - 图像分类模型（ResNet/EfficientNet）
- 新增 `ml/SkinDetector.java` - 肤色检测
- 新增 `repository/ModerationTaskRepository.java` - 审核任务数据访问
- 新增 `repository/SensitiveWordRepository.java` - 敏感词数据访问
- 新增 `repository/SpamReportRepository.java` - 举报记录数据访问
- 修改 `service/MessageService.java` - 集成消息审核（在发送前/后拦截）
- 修改 `websocket/WsHandler.java` - 实时消息审核拦截

**功能点**:
- 敏感词过滤（DFA + AC自动机，支持变体词识别）
- 正则表达式审核（手机号/QQ号/网址/邮箱/微信号/诱导外链）
- AI文本分类（色情/暴力/政治/广告/仇恨/诈骗多标签分类）
- 图片审核（肤色检测 + 场景识别 + 物体检测 + 图片鉴伪）
- 视频审核（关键帧提取 + 并行审核 + 音频识别）
- 音频审核（语音识别 + 声纹分析）
- 用户行为分析（发送频率 + 内容相似度 + 群发检测）
- 举报与反馈（用户举报 + 重复检测 + 自动审核触发）
- 人工审核工作台（任务列表 + 审核操作 + 结果提交）
- 审核数据统计（违规率/误杀率/漏过率监控）

**依赖库/服务**:
- Spring Boot + MyBatis-Plus
- Deeplearning4j / PyTorch Serving（AI模型推理）
- Redis（热点缓存、敏感词缓存）
- 阿里云内容安全 / 腾讯云安全审核 / 网易云盾（可选对接）
- AWS Rekognition / Google Cloud Vision / Azure Content Moderator（可选对接）

**IM系统集成**:
- 消息发送前拦截（同步审核，涉政/涉黄/涉暴直接拒绝）
- 消息发送后异步审核（普通广告内容降权/撤回处理）
- WebSocket实时消息审核（结合Sentinel限流）
- 与 webhook_callback.md 的 Before Callback 联动
- 与 rate_limiting_circuit_breaker.md 的分层限流联动

### 消息内容审核与反垃圾系统模块 (NEW)
- **模块名称**: Content Moderation & Anti-Spam
- **功能**: 文本/图片/视频/音频审核，反垃圾检测，人工复核，保障平台安全合规
- **技术**: DFA/AC自动机敏感词过滤、BERT/ERNIE文本分类、深度学习图像分类、肤色检测YCbCr、SimHash文本相似度、Webhook Before Callback集成、分层防御架构（同步<1ms → 异步<10ms → AI<100ms → 人工复核）、用户行为分析、机器学习特征工程

---

## 第五阶段：分布式事务与高可用保障 (2026-03-20 新增)

### 18. 分布式事务保障模块 (新功能) - 🔴 HIGH

**功能描述**: 引入 Apache Seata 分布式事务平台，为跨服务、跨数据库的业务操作提供强一致性或最终一致性保障，覆盖好友关系变更、群组操作、消息发送等核心场景

**技术方案** (基于 distributed_transaction_consistency.md 知识):
- **Seata AT 模式**（首选）：MySQL 跨库操作（消息 + 会话 + 未读计数），零代码侵入，通过 UNDO LOG 自动补偿回滚
- **Seata TCC 模式**：异构系统场景（MySQL + Redis + MongoDB），实现 Try/Confirm/Cancel 三接口，预留资源 + 确认执行 + 回滚释放
- **Seata Saga 模式**：长流程异步场景（消息归档、批量处理），编排器管理正向链 + 补偿链
- **Seata XA 模式**：金融级消息（红包/支付通知），强一致性，牺牲部分性能
- **幂等性保障**：消息 ID + 操作类型唯一索引、Redis 分布式锁、状态机 + 版本号乐观锁
- **最佳实践**：消息发送采用"本地事务写消息库 → Kafka 异步更新会话"规避分布式事务；好友关系用 Saga Choreography；群组创建用 TCC 或 Saga Orchestration

**涉及文件**:
- 新增 `config/SeataConfig.java` - Seata 配置（AT/TCC/Saga 模式切换、TC Server 连接）
- 新增 `config/SeataServerConfig.java` - Seata Server（TC）配置（Session 存储、锁模式、高可用集群）
- 新增 `tcc/FriendTCCService.java` - 好友关系 TCC 服务（Try/Confirm/Cancel 实现）
- 新增 `tcc/GroupTCCService.java` - 群组操作 TCC 服务（创建/添加成员/解散）
- 新增 `tcc/MessageTCCService.java` - 消息发送 TCC 服务（预分配序列号 + 频率检查）
- 新增 `saga/GroupCreateSaga.java` - 群组创建 Saga 编排器（Seata Saga JSON 编排）
- 新增 `saga/FriendSagaOrchestrator.java` - 好友关系 Saga 编排器（Java 状态机）
- 新增 `interceptor/IdempotencyInterceptor.java` - 幂等性拦截器（重复操作过滤）
- 新增 `service/IdempotentService.java` - 幂等性服务（唯一索引 + 分布式锁）
- 新增 `repository/IdempotentLogRepository.java` - 幂等性日志表（msg_id + operation_type）
- 修改 `pom.xml` - 引入 seata-spring-boot-starter、seata-core
- 新增 `sql/seata_undo_log.sql` - UNDO LOG 表（Seata AT 模式依赖）

**功能点**:
- 好友添加分布式事务（双向记录 + 请求状态 + 通知）
- 群组创建分布式事务（群信息 + 成员 + 锁机制 + 通知）
- 群组解散分布式事务（成员清理 + 消息处理 + 通知）
- 消息发送幂等性保障（重复发送检测，消息 ID 唯一索引）
- Seata TC Server 高可用集群部署
- 分布式事务监控（成功率、耗时、补偿率）
- AT/TCC/Saga 模式按场景智能切换

**依赖库/服务**:
- Apache Seata Server（TC，单独部署，建议 3 节点集群）
- seata-spring-boot-starter 2.x
- MySQL 8.0 + InnoDB（AT 模式依赖）
- Redis 7.0（Seata Session 存储可选）
- Kafka/RocketMQ（消息发送异步化，配合 Saga 最终一致）

**IM 系统集成**:
- Seata AT：消息 + 会话 + 未读计数三表一致性（全局事务包裹）
- Seata TCC：好友关系（Try 预验证）、群组操作（Try 预占资源）
- Seata Saga：消息归档（批量处理补偿链）、好友关系变更事件链
- 与 message_queue.md 的消息队列协同（异步处理最终一致路径）
- 与 rate_limiting_circuit_breaker.md 的 Sentinel 协同（分布式事务 + 限流双重保护）

### 分布式事务保障模块 (NEW)
- **模块名称**: Distributed Transaction Consistency
- **功能**: 跨服务、跨数据库的强一致性或最终一致性保障，覆盖好友/群组/消息核心场景
- **技术**: Apache Seata（AT/TCC/Saga/XA 四种模式）+ 幂等性保障（唯一索引/分布式锁/状态机）+ 最佳实践（消息发送本地事务 + Kafka 异步）

---

*最后更新: 2026-03-20 03:52*

---

## 第六阶段：CRDT 多设备同步与冲突处理（2026-03-20 新增）

### 19. 多设备同步与冲突处理核心（CRDT/OT） - 🔴 HIGH

**功能描述**: 基于 CRDT（无冲突复制数据类型）和混合冲突处理策略，实现多设备间的消息、草稿、会话状态同步与自动冲突合并

**技术方案** (基于 multi_device_sync_crdt.md 知识):
- **消息同步**：基于 Snowflake 消息 ID 序列的增量拉取（全局唯一且有序，简单可靠）
- **草稿同步**：Yjs Y.Text CRDT（自动冲突合并，支持 "Hello"+"World"→"HelloWorld"）
- **会话状态**：Last-Write-Wins（LWW）+ sync_token 机制
- **离线追赶**：设备上线按 sync_token 增量拉取，Pending Queue 批量推送
- **冲突策略矩阵**：消息发送→乐观追加、消息删除→逻辑删除+墓碑、已读计数→LWW、编辑→LWW+版本历史
- **设备管理**：device_token 认证、最多10设备限制、主设备/从设备优先级分发

**涉及文件**:
- 新增 entity/Device.java - 设备实体（已存在）
- 新增 entity/SyncCheckpoint.java - 同步检查点实体
- 新增 entity/PendingMessage.java - 待推送消息队列实体
- 新增 entity/MessageDraft.java - 草稿实体（已存在，需增强 Yjs 字段）
- 新增 epository/DeviceRepository.java - 设备数据访问（已存在）
- 新增 epository/SyncCheckpointRepository.java - 同步检查点数据访问
- 新增 epository/PendingMessageRepository.java - 待推送消息数据访问
- 新增 service/DeviceService.java - 设备管理服务（已存在）
- 新增 service/MultiDeviceSyncService.java - 多设备同步核心服务（新增）
- 新增 service/DraftSyncService.java - 草稿同步服务（集成 Yjs）
- 新增 controller/SyncController.java - 同步控制接口
- 新增 controller/DraftController.java - 草稿接口（已存在）
- 修改 websocket/WsHandler.java - 添加多设备消息分发逻辑
- 新增 config/YjsConfig.java - Yjs WASM 配置（Tauri 桌面端）

**功能点**:
- 设备注册与身份认证（device_token）
- 增量同步（按 sync_token 只拉取新消息）
- 离线消息追赶（Pending Queue 批量推送）
- 草稿跨设备实时同步（Yjs Y.Text CRDT）
- 已读/未读状态多设备一致（LWW）
- 消息编辑历史（"已编辑"提示 + LWW）
- 设备登出与数据清理
- 多设备消息分发优先级（主设备实时推送）

**依赖库/服务**:
- yjs（JavaScript CRDT，可用于 Web 前端）
- yjs-wasm 或 yrs（Rust CRDT，可用于 Tauri 桌面端）
- y-indexeddb（本地持久化）
- y-websocket（WebSocket 同步提供者）
- Redis（Pending Queue 缓存）
- MySQL（同步检查点、设备信息）

**IM 系统集成**:
- 与 end_to_end_encryption.md 联动（多设备密钥链：每设备独立 Session）
- 与 message_storage.md 联动（消息 ID Snowflake 生成 + 消息序列）
- 与 websocket.md 联动（多设备 WebSocket 连接管理）

### 20. 协同编辑功能（基于 CRDT） - 🟡 MEDIUM

**功能描述**: 在群组聊天中支持多人协同编辑文档、公告、投票等复杂内容，基于 CRDT 实现无冲突协同编辑

**技术方案** (基于 multi_device_sync_crdt.md 知识):
- **Yjs Y.Text**：群公告、群描述的协同文本编辑
- **Yjs Y.Map**：投票选项、复杂配置的协同编辑
- **Yjs Y.Array**：群成员列表、投票选项列表的协同管理
- **光标同步**：显示其他用户的编辑光标位置
- **实时感知**：显示在线用户、正在编辑的用户

**涉及文件**:
- 新增 service/CollabService.java - 协同编辑服务（基于 Yjs）
- 新增 websocket/CollabWebSocketHandler.java - 协同 WebSocket 处理器
- 新增 dto/CollabUpdate.java - 协同更新数据传输对象
- 修改 entity/GroupAnnouncement.java - 添加 Yjs 协同编辑字段
- 修改 entity/Poll.java - 添加 Yjs 投票选项字段
- 新增 controller/CollabController.java - 协同编辑接口

**功能点**:
- 群公告协同编辑（多人同时编辑同一公告）
- 投票选项协同编辑（实时显示投票选项变更）
- 协作光标显示（显示其他用户正在编辑的位置）
- 协作用户感知（显示在线用户、编辑中用户）
- 协同版本历史（查看历史协同版本快照）
- 离线协同支持（Yjs 自动合并离线编辑）

**依赖库/服务**:
- yjs（前端 JavaScript）
- yrs（Tauri/Rust 端）
- y-websocket 或 y-redis（协同状态同步）
- y-redis（分布式环境下的协同状态存储）

### CRDT 多设备同步与冲突处理模块 (NEW)
- **模块名称**: Multi-Device Sync & Conflict Resolution
- **功能**: 多设备消息/草稿/状态同步，基于 CRDT 的自动冲突合并，离线追赶机制
- **技术**: Yjs Y.Text CRDT（草稿）+ Snowflake ID（消息序列）+ LWW（状态）+ sync_token（增量同步）+ Pending Queue（离线推送）+ 混合冲突策略矩阵

---

*最后更新: 2026-03-20 09:52*
