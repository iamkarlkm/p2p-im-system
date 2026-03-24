# 技术学习日志

## 学习日期
2024年-2026年

---

## 2026年3月24日 - 设备端机器学习与边缘AI推理

### 新的学习方向：设备端机器学习与边缘AI推理在即时通讯系统中的应用

**核心概念：**
1. **设备端机器学习**：在用户设备上直接运行AI模型
2. **边缘AI推理**：在靠近用户的边缘服务器上执行复杂AI任务
3. **隐私优先设计**：所有AI功能在保护用户隐私的前提下运行

**技术架构：**
- **本地推理层**：TensorFlow Lite、PyTorch Mobile、Core ML
- **边缘计算节点**：处理复杂模型，减少延迟
- **云端协调**：模型更新、联邦学习、数据聚合

**在IM系统中的具体应用：**
1. **本地智能回复系统**：意图识别、上下文分析、回复生成完全在设备端
2. **实时消息摘要**：轻量级本地模型提取关键信息
3. **内容安全和隐私保护**：本地垃圾检测、敏感内容识别

**核心技术：**
- 模型轻量化：量化、剪枝、知识蒸馏
- 资源优化：内存、计算、功耗、存储优化
- 隐私保护：差分隐私、安全多方计算

**实际案例：**
1. Signal的隐私保护AI功能
2. WhatsApp的智能回复建议
3. Telegram的本地搜索和分类

---

## 2026年3月23日 - 联邦学习与隐私保护

### 新的学习方向：即时通讯系统中的联邦学习与隐私保护

**核心概念：**
1. **联邦学习（Federated Learning）**：分布式机器学习，数据留在设备本地
2. **隐私保护技术**：差分隐私、安全多方计算、同态加密
3. **应用场景**：智能回复、垃圾检测、情感分析

**技术架构：**
- **联邦学习服务器**：模型聚合、客户端选择、全局模型管理
- **客户端系统**：本地训练、模型更新、隐私保护
- **通信协议**：加密传输、安全聚合、差分隐私噪声

**在IM系统中的具体应用：**
1. **智能回复建议的联邦训练**：保护用户对话隐私
2. **垃圾消息检测**：快速适应新垃圾模式
3. **消息情感分析**：敏感数据本地处理

**性能优化策略：**
- 通信优化：模型压缩、增量更新、异步训练
- 计算优化：设备感知调度、边缘计算、缓存机制
- 能源优化：训练时机选择、批处理训练、模型轻量化

**隐私保护水平：**
- ε-差分隐私参数控制
- 安全多方计算防止数据泄露
- 同态加密保护传输过程

---

## 2026年3月21日 - AI增强IM系统

### 新的学习方向：IM系统的AI增强功能设计

**核心AI功能：**
1. **智能消息排序**：基于发件人重要性、消息类型、提及次数、时间衰减等
2. **实时消息摘要**：轻量级本地模型，关键信息提取，个性化摘要
3. **智能回复建议**：意图识别 -> 上下文分析 -> 回复生成 -> 排序展示
4. **内容理解和分类**：自动标签、情感分析、多媒体理解

**技术架构：**
- **前端AI层**：智能回复系统、本地推理
- **后端AI服务**：消息排序服务、用户行为分析
- **隐私保护**：本地处理优先、差分隐私、透明度控制

**性能优化：**
- 模型压缩（量化、剪枝、知识蒸馏）
- 缓存策略（模板缓存、偏好缓存）
- 异步处理（批量处理、离线学习）

**未来方向：**
- 多模态理解（文本+图像+语音）
- 个性化学习（持续学习、联邦学习）
- 协作增强（群聊协调、会议纪要）

---

## 2024年3月17日 - 深度学习

### 1. Tauri 2.0 最新特性

**核心优势:**
- 基于Rust构建，享受内存、线程和类型安全
- 使用系统原生WebView，**最小包可以小于600KB**
- 支持任何前端框架（HTML/JS/CSS）
- 经过安全审计

**关键特性:**
- 安全性：所有应用自动获得Rust的安全特性
- 体积小：不捆绑浏览器引擎
- 灵活性：支持多种前端框架和语言绑定
- 插件系统：丰富的社区插件生态

**相关项目:**
- TAO: 窗口创建
- WRY: WebView渲染

### 2. Flutter 3.41 最新版本

**版本信息:**
- Flutter 3.41 于2024年发布
- 单代码库支持多平台

**在IM应用中的优势:**
- 高性能渲染（Skia图形引擎）
- 丰富的UI组件库
- 良好的消息列表性能
- 支持平台特定功能

### 3. Netty 技术栈

**版本状态:**
- **Netty 5.0**: 正在开发中
- **Netty 4.2**: 稳定版本（推荐）
- **Netty 4.1**: 稳定版本
- **Netty 3.10**: 开发中

**关键功能:**
- WebSocket支持（双向全双工通信）
- TLS加密
- 异步IO处理
- 高性能TCP/UDP服务

**IM应用推荐:**
- 使用Netty 4.2作为WebSocket网关
- 支持Protobuf协议序列化

### 4. Spring Cloud 2025

**版本对应关系:**
| Release Train | Spring Boot |
|---------------|--------------|
| 2025.1.x (Oakwood) | 4.0.x |
| 2025.0.x (Northfields) | 3.5.x |
| 2024.0.x (Moorgate) | 3.4.x |

**核心组件:**
- Spring Cloud Gateway: 智能路由
- Spring Cloud Netflix: 服务注册发现
- Spring Cloud Config: 配置管理
- Spring Cloud Stream: 消息驱动
- Spring Cloud OpenFeign: 服务调用

---

## 关键技术建议

### Tauri桌面端
- 使用Tauri 2.x版本
- 利用Rust后端处理加密和文件操作
- 最小化WebView依赖

### Flutter移动端
- 使用Flutter 3.x最新稳定版
- 使用provider/riverpod进行状态管理
- 优化长列表滚动性能

### 后端服务
- 使用Spring Cloud 2024.x + Spring Boot 3.x
- Netty 4.2作为WebSocket网关
- 考虑Spring Boot 4.0 + Spring Cloud 2025.1

### 数据库
- Redis Cluster用于缓存和会话
- MySQL 8.0分布式架构
- 考虑TiDB/CockroachDB作为分布式方案

---

## 技术改进建议

### 1. 客户端架构优化
- **Tauri**: 建议使用Tauri 2.x，利用Rust处理消息加密和本地存储
- **Flutter**: 使用Isolate处理大量消息的序列化/反序列化，避免UI卡顿

### 2. 后端服务优化
- **WebSocket网关**: 使用Netty 4.2，支持百万级并发连接
- **消息队列**: 考虑Redis Stream替代Kafka（中小规模）或保留Kafka（大规模）
- **服务注册**: 使用Nacos替代Eureka（国产化支持更好）

### 3. 数据库优化
- **消息分库分表**: 按user_id分片
- **读写分离**: 消息查询使用只读副本
- **冷热分离**: 超过30天的消息归档到冷存储

### 4. 安全增强
- 端到端加密（E2EE）
- 消息阅后即焚
- 设备管理和登录验证

---

## 待学习主题（需要搜索API支持）

1. 微信/WhatsApp技术架构分析
2. 即时通讯系统性能优化最佳实践
3. 分布式数据库选型（TiDB vs CockroachDB）
4. 消息可靠性和顺序性保证
5. 高并发IM系统设计方案

---

## 2026-03-18 21:52 - 开源IM系统架构与部署

### 新增知识文件: open_source_im_architecture.md

**核心发现**:

1. **OpenIM 开源IM解决方案**: 与Telegram/Signal不同，OpenIM是专为开发者设计的开源IM SDK+Server，可私有部署
2. **微服务架构**: OpenIM Server采用微服务架构，Gateway + 多个RPC服务（msg/user/friend/group/push/third）
3. **Kafka消息队列**: 4个核心主题（toRedis/toMongo/toPush/toOfflinePush），每主题8分区
4. **Kafka KRaft模式**: 无需ZooKeeper，使用Raft协议自我管理，Kafka 3.5+推荐
5. **基础设施栈**: MongoDB 7.0 + Redis 7.0.0 + Kafka 3.5.1 + Etcd 3.5.13 + MinIO
6. **监控栈**: Prometheus + AlertManager + Grafana + Node-Exporter
7. **SDK架构**: OpenIMSDK分为本地存储、监听器回调、API封装、连接管理四大模块

**下一步学习方向**:
1. Webhook系统设计
2. 消息序列号机制
3. 多设备同步协议
4. 推送服务集成（个推/FCM）
5. 服务扩缩容与负载均衡
6. 数据备份与恢复策略

---

## 2026-03-19 03:52 - Webhook & Callback 系统设计

### 新增知识文件: webhook_callback.md

**学习内容**:

1. **Webhook vs Callback 概念区分**
   - Webhook：IM系统向外部第三方系统主动推送事件通知（对外）
   - Callback：IM系统内部的事件钩子，可在关键节点插入自定义逻辑（对内）

2. **Webhook 系统设计**
   - 事件类型定义（im.message.*, im.user.*, im.group.* 等）
   - 投递格式（id, event, timestamp, signature, data）
   - 签名验证（HMAC-SHA256，参考 Stripe/GitHub 最佳实践）
   - 重放攻击防护（时间戳 + event_id 幂等）
   - 可靠性设计（异步投递 + 重试队列：1s→5s→30s→2min→10min→1h）
   - 快速响应要求（参考 GitHub：10秒内返回 2xx）

3. **Internal Callback 机制设计**
   - 三种类型：Before Callback（可拒绝/修改）、After Callback（只读）、Replace Callback（完全替代）
   - 典型场景：内容审核（Before）、计费统计（After）
   - 回调调度器（串行执行Before，并行执行After）

4. **实际应用场景**
   - 内容审核集成（对接第三方审核服务，消息发送前检测）
   - 业务系统集成（消息发送后通知 ERP/CRM）
   - 消息归档（合规要求，自动归档到数据湖）
   - 计费与统计（消息发送量计费、DAU/MAU 统计）

5. **安全性最佳实践**
   - HTTPS 强制 + HMAC 签名验证
   - 时间戳检查（5分钟内有效）
   - IP 白名单（可选）
   - 回调超时熔断保护

6. **性能优化**
   - HTTP 连接池复用
   - 异步投递 + 批量投递
   - 回调并行执行 + 超时熔断

7. **监控指标**
   - 投递成功率、失败率、超时率、平均延迟
   - 回调执行次数、拒绝数、失败数、平均执行时间

**技术来源**:
- Stripe Webhooks 最佳实践（签名验证、异步处理、Handler 模式）
- GitHub Webhooks 最佳实践（10秒响应、重放防护、事件类型检查）
- Ably Webhooks 概念详解（Webhook vs API、规模化投递）
- OpenIM 架构参考（事件驱动、业务集成）

**下一步学习方向**:
1. 负载均衡与 WebSocket 粘性会话设计
2. 微信/Telegram/WhatsApp 技术架构深度分析
3. 分布式事务与消息一致性（2PC/TCC/Saga）
4. 限流熔断与服务保护（Sentinel/Resilience4j）
5. 多设备同步协议与冲突处理
6. 数据备份与灾难恢复策略

---

## 2026-03-19 09:52 - 限流与熔断降级（Sentinel）

### 新增知识文件: rate_limiting_circuit_breaker.md

**核心发现**:

1. **Sentinel 核心概念**
   - 资源（Resource）：Sentinel 保护的任何代码块，可以是服务、方法、URL
   - 规则（Rule）：流量控制规则、熔断降级规则、系统保护规则，全部支持动态实时调整
   - Sentinel 1.8+ 对熔断降级进行了全新改进

2. **流量控制核心机制**
   - QPS 限流 vs 并发线程数限流
   - 三种控制效果：直接拒绝（default）、冷启动（warm_up）、匀速器（rate_limiter）
   - 基于调用关系限流：按调用方（origin）、按调用链路（chain）、关联限流（relate）
   - IM系统关键：IP限流 + 用户级限流 + 接口级限流分层防御

3. **熔断器状态机**
   - CLOSED（闭合）：正常放行
   - OPEN（熔断）：拒绝所有请求
   - HALF-OPEN（半开）：放行探测请求，成功则恢复，失败则继续熔断
   - 三种熔断策略：慢调用比例（SLOW_REQUEST_RATIO）、异常比例（ERROR_RATIO）、异常数（ERROR_COUNT）
   - 静默期（MinRequestAmount）：避免统计周期开始时的偶发慢请求误判

4. **Sentinel vs Hystrix 核心区别**
   - Hystrix：线程池隔离，预分配线程，有切换开销
   - Sentinel：并发线程数限制 + 响应时间降级，无需预分配，动态统计

5. **IM系统分层限流架构**
   - 客户端层：本地限流 + 智能重试（指数退避）
   - 网关层：IP限流 + Token限流 + WebSocket连接数限制
   - 业务服务层：接口级 + 用户级 + 群组级限流
   - 存储层：MySQL连接池 + Redis + MongoDB + Kafka 生产者限流

6. **Netty WebSocket 集成 Sentinel**
   - 通过 SphU.entry() 包装消息处理逻辑
   - BlockException 统一处理限流/熔断响应
   - WebSocket 返回 RATE_LIMIT 类型消息通知客户端

7. **IM系统推荐配置**
   - 消息发送：1000 QPS（直接拒绝）
   - 用户全局：100 QPS/用户
   - WebSocket并发：50000 连接
   - 数据库慢调用：RT>200ms + 30% 比例熔断，30秒熔断时长
   - 最小请求数（静默期）：10

8. **最佳实践要点**
   - 限流：明确知道处理能力用直接拒绝，突发流量用冷启动，消息队列用匀速器
   - 熔断：数据库用慢调用比例，第三方API用异常比例，内部服务用异常数
   - 必须设置 MinRequestAmount（静默期）避免误判
   - 熔断时长建议 30s~60s，太短无法恢复，太长影响体验

**技术来源**:
- Sentinel 官方文档（https://sentinelguard.io/zh-cn/）
- Sentinel 流量控制文档
- Sentinel 熔断降级文档（Java 1.8+ 和 Go 版本）
- Sentinel 简介与设计理念

**下一步学习方向**:
1. Sentinel Dashboard 高可用部署
2. Sentinel 规则动态配置（Nacos/ZooKeeper/Apollo）
3. Sentinel 与 Spring Cloud Gateway 深度集成
4. 分布式事务一致性（2PC/TCC/Saga）
5. 微信/Telegram/WhatsApp 技术架构深度分析
6. 端到端加密（Signal Protocol）
7. 消息序号机制与多端同步
8. 消息内容审核与反垃圾系统
9. 大规模 WebSocket 集群架构
10. 多语言限流（Sentinel Go/C++/Rust）

---

## 2026-03-19 11:32 - 架构规划结果

### 规划结果
基于 webhook_callback.md + rate_limiting_circuit_breaker.md + 现代IM系统分析，新增功能：

**功能 #12: 应用内通知中心 (In-App Notification Center)**
- 与 webhook_callback.md 的 After Callback 机制联动
- 与 rate_limiting_circuit_breaker.md 的 Sentinel 限流联动（系统繁忙时自动静默）
- 覆盖 Roadmap 中"统一推送服务"缺失的应用内通知管理模块
- 详细规划：通知列表/未读计数/已读管理/分类/聚合/免打扰/偏好设置/清理
- 详见：roadmap.md 功能#12

### Roadmap 更新
- 总功能数：59 → 60
- 进度：13% (8/60)

---

*注：由于web_search API需要配置Brave API key，部分搜索任务未能完成*

---

## 2026-03-19 15:52 - Signal Protocol 端到端加密详解

### 新增知识文件: end_to_end_encryption.md（新增）

**核心发现**:

1. **Signal Protocol 架构组成**
   - **X3DH（Extended Triple Diffie-Hellman）**：初始密钥协商协议，支持**离线发起方**建立安全会话
   - **Double Ratchet**：消息加密算法，对称棘轮 + DH 棘轮组合，实现**前向保密**

2. **三层密钥体系**
   - L1: Identity Key Pair（长期，安装时生成，用于签署 Signed PreKey）
   - L2: Signed PreKey（中期，约 1 个月，用 Identity Key 签名）
   - L3: PreKey（一次性，100+ 个，用于异步建立会话）
   - X3DH 通过组合 4 个 DH 计算：`DH1 || DH2 || DH3 || DH4` → HKDF → 共享密钥 SK

3. **Double Ratchet 安全机制**
   - 对称棘轮：每条消息后 `messageKey = HKDF(chainKey)` 前推，不可逆
   - DH 棘轮：定期引入新 DH 密钥对，彻底刷新密钥链
   - 密钥泄露影响范围有限：Identity Key 泄露**不影响历史会话**

4. **密码学基础**
   - Curve25519（256 位安全，32 字节密钥）
   - AES-256-GCM（消息加密 + AEAD 认证）
   - HKDF-SHA256（密钥派生）
   - Ed25519（Signed PreKey 签名）

5. **服务器最小化原则**
   - 服务器**仅存储公钥**（Identity Key + PreKeys）
   - 服务器**转发加密消息，不解密**
   - 服务器**不持有任何私钥**
   - 元数据（发送方/接收方）不可避免

6. **开源库生态**
   - 官方：libsignal-protocol-java（Java/Android）、libsignal-protocol-c（C）、libsignal-protocol-rust（Rust）
   - 前端：libsignal-protocol-js（Web）
   - 协议已被 WhatsApp、Facebook Messenger、Skype 采用

7. **IM 系统实现方案**
   - 客户端：密钥生成（KeyHelper）+ 会话建立（SessionBuilder）+ 消息加解密（SessionCipher）
   - 服务器端：KeyRegistry（公钥注册）+ MessageHub（密文转发）+ DeviceManager（多设备分发）
   - 数据库：user_keys（公钥）、prekeys（PreKey 公钥）、encrypted_messages（密文）

**技术来源**:
- Signal Protocol 官方文档（signal.org/docs）
- GitHub: libsignal-protocol-java（https://github.com/signalapp/libsignal-protocol-java）
- GitHub: libsignal-protocol-c（https://github.com/signalapp/libsignal-protocol-c）
- GitHub: libsignal-service-java（https://github.com/signalapp/libsignal-service-java）
- Rust libsignal-protocol 文档（https://docs.rs/libsignal-protocol）
- Signal Server 架构参考

**下一步学习方向**:
1. Sealed Sender / 密封发送者（元数据最小化）
2. 群组加密（Sender Keys 方案）
3. 分布式事务一致性（2PC/TCC/Saga）
4. 微信/Telegram/WhatsApp 技术架构深度分析
5. 消息内容审核与反垃圾系统 ✅（已完成）
6. 大规模 WebSocket 集群 + 负载均衡
7. 服务网格（Service Mesh）与 IM 微服务治理
8. 数据库全文搜索（Elasticsearch）与消息检索

---

## 2026-03-20 09:52 - 第七轮架构规划：AI增强与SaaS架构

### 新增功能: 功能 #47-51（5个新功能）

**知识来源分析**:
本次规划重点挖掘了之前未被充分利用的知识文件：

1. **websocket.md** (高级WebSocket特性):
   - permessage-deflate压缩：减少60-80%带宽
   - 多路复用：单TCP连接承载多个Stream（消息/心跳/文件/信令）
   - Stream流控制、优先级队列
   - Protobuf替代JSON减少30-50%体积
   - → 驱动 **功能#51: WebSocket多路复用与压缩**

2. **message_storage.md** (消息存储方案):
   - Elasticsearch全文检索已覆盖（功能#7）
   - 语义向量检索 → RAG（Retrieval-Augmented Generation）
   - 混合检索：BM25 + 向量语义 Reciprocal Rank Fusion
   - → 驱动 **功能#48: 语义搜索与向量检索**

3. **im_architecture.md** (架构设计文档):
   - 多语言IM支持 → 神经网络机器翻译
   - 企业管理架构 → 管理员控制台
   - SaaS多租户架构模式
   - → 驱动 **功能#47: 消息翻译服务** + **功能#49: 企业管理后台** + **功能#50: 多租户SaaS架构**

4. **open_source_im_architecture.md** (OpenIM架构):
   - 企业级功能：管理后台、计费、审计
   - 搜索能力增强
   - → 支撑 **功能#48/#49**

### 新增功能详解

**功能 #47: 消息翻译服务** 🔴 HIGH
- NMT神经网络翻译（Transformer），20+语言支持
- 自动语言检测、上下文感知翻译
- 翻译缓存（Redis MD5(content+lang) → TTL=24h）
- CJK语言专项优化
- 知识来源: im_architecture.md + message_storage.md + WhatsApp/Telegram翻译功能

**功能 #48: 语义搜索与向量检索** 🔴 HIGH
- text-embedding-3-small/BGE向量嵌入，1536维稠密向量
- Pinecone/Milvus/Qdrant向量数据库
- 自然语言语义搜索（"找关于项目会议的消息"）
- 混合检索（BM25 + RRF融合）+ Cross-Encoder重排序
- RAG：为AI聊天机器人提供上下文检索
- 知识来源: message_storage.md (ES全文检索) + open_source_im_architecture.md

**功能 #49: 企业管理后台** 🟡 MEDIUM
- 用户/群组/消息全局管理（CRUD/批量操作）
- RBAC角色权限（超级管理员/普通管理员/审计员）
- 系统健康仪表盘 + DAU/MAU使用量分析
- 审计日志（Append-only表，全量记录）
- 举报管理 + 计费配额
- 知识来源: im_architecture.md (企业架构) + open_source_im_architecture.md

**功能 #50: 多租户SaaS架构** 🟡 MEDIUM
- 租户命名空间隔离（tenant_id字段）
- 租户感知路由 + TenantContext ThreadLocal
- 白标定制（Logo/主题色/域名）
- 计费方案（Free/Pro/Enterprise分级）
- 欠费自动暂停/恢复
- 知识来源: im_architecture.md (SaaS架构模式)

**功能 #51: WebSocket多路复用与压缩** 🟡 MEDIUM
- permessage-deflate压缩（Netty WebSocketServerCompressionHandler）
- 多Stream多路复用（channelId: 消息/心跳/文件/信令）
- 自适应压缩（RTT/丢包率动态调整）
- Protobuf替代JSON
- 消息分片传输
- 知识来源: websocket.md (高级WebSocket)

### 路线图统计更新
- 总功能数: 94 → **99**
- 总进度: 13/94 (14%) → **13/99 (13%)**

### 下一步规划方向
1. 知识来源枯竭度评估 → 需要新增外部知识
2. 已有功能的开发推进（当前仅13/99=13%完成）
3. Phase 4-8 的待开发功能推进
4. 技术债务整理（代码质量/测试覆盖）

---

## 2026-03-20 03:52 - 分布式事务一致性（2PC / TCC / Saga）

### 新增知识文件: distributed_transaction_consistency.md（新增）

**核心发现**:

1. **CAP 定理与 BASE 理论**：IM 系统通常选择 CP（强一致性 + 分区容错），核心数据走最终一致性路径

2. **2PC 两阶段提交**：协调者管理 Prepare → Commit/Rollback 两阶段。问题：同步阻塞、单点故障、数据不一致风险。IM 系统中 2PC 不直接使用，但思想是其他方案的基础。XA 协议是 2PC 的工业标准实现。

3. **TCC（Try-Confirm-Cancel）**：业务层面的两阶段提交，Try 预留资源 → Confirm 确认执行 → Cancel 回滚释放资源。IM 适用场景：发送消息（预分配序列号 + 频率检查）、添加好友（预验证 + 创建记录）、群组创建（预分配 ID + 锁群名）。关键技术：幂等性、空回滚防护、悬挂处理。

4. **Saga 模式**：长事务拆分为本地事务 + 补偿事务链，不锁定资源，性能最高。两种协调方式：Choreography（事件驱动，各服务对等）和 Orchestration（编排器中心协调）。IM 适用场景：消息归档（批量处理）、好友关系变更（事件链）。缺点：无隔离性，需业务侧乐观锁防护。

5. **Apache Seata**：支持 AT/TCC/Saga/XA 四种模式。AT 模式零代码侵入，通过 UNDO LOG 自动补偿，最适合 IM 的 MySQL 跨库场景。TCC 模式需实现 Try/Confirm/Cancel 三接口，适合异构系统。Saga 模式适合长流程异步场景。XA 模式强一致但性能最低，适合金融级消息。

6. **IM 分布式事务最佳实践**：
   - 消息发送：本地事务写消息库 → Kafka 异步更新会话和未读计数 → 消费端幂等消费（最终一致，规避分布式事务）
   - 好友关系：Saga Choreography 模式，事件驱动链
   - 群组创建：Saga Orchestration 或 TCC，保证多步骤强一致
   - Seata AT + TCC 混合使用：MySQL 部分用 AT，异构系统（Redis/MongoDB）用 TCC

7. **幂等性保障**：数据库唯一索引（msg_id + operation_type）、Redis 分布式锁、状态机 + 版本号乐观锁

**技术来源**:
- Apache Seata 官方文档（https://seata.io/zh-cn/docs/overview/what-is-seata）
- microservices.io Saga Pattern（https://microservices.io/patterns/data/saga.html）
- Chris Richardson《Microservices Patterns》第四章
- Eventuate Tram Sagas 开源框架

**下一步学习方向**:
1. Sealed Sender / 密封发送者（元数据最小化）
2. 群组加密（Sender Keys 方案）
3. 微信/Telegram/WhatsApp 技术架构深度分析
4. 大规模 WebSocket 集群 + 负载均衡
5. 服务网格（Service Mesh）与 IM 微服务治理
6. 数据库全文搜索（Elasticsearch）与消息检索
7. 多设备同步协议与冲突处理（OT / CRDT）

---

## 2026-03-19 21:52 - 消息内容审核与反垃圾系统

### 新增知识文件: content_moderation_spam_detection.md（新增）

**核心发现**:

1. **分层防御架构**
   - 客户端层：本地敏感词过滤、输入框实时检测、用户举报入口
   - 网关层：请求验证、频率限制、IP/设备黑名单、初步内容过滤
   - 消息服务层：消息路由、内容分发、撤回处理、审核状态追踪
   - 审核服务层：文本审核、图片审核、视频审核、音频审核、AI模型推理
   - 存储层：敏感词库、审核记录、违规记录、机器学习模型

2. **同步审核 vs 异步审核 vs 混合模式**
   - 同步审核（Sync）：高风险内容（涉政/涉黄/涉暴），消息发送前必须通过审核，延迟100-500ms
   - 异步审核（Async）：普通垃圾广告，消息先发送后台异步审核，延迟<10ms
   - 混合模式（Hybrid）：高风险词同步拦截 + 普通内容异步审核 + AI实时推理+人工复核

3. **文本内容审核技术**
   - **DFA算法**：确定有限自动机，高效敏感词匹配，时间复杂度O(n)
   - **AC自动机**：Aho-Corasick，适用于大规模敏感词库（>100万词），批量模式匹配
   - **Trie树+Hash优化**：前缀树结构，支持快速前缀搜索
   - **变体词识别**：谐音替换、简繁体转换、特殊符号干扰、拼音首字母、emoji符号
   - **正则表达式审核**：手机号、QQ号、网址、邮箱、微信号、诱导外链话术

4. **AI文本审核模型**
   - 基于BERT/ERNIE等预训练模型的多标签分类
   - 色情内容、暴力恐怖、政治敏感、垃圾广告、仇恨言论、诈骗信息分类
   - **SimHash**：文本相似度检测，海明距离<3判定为相似内容
   - 增量学习：在线更新模型参数，适应新型违规内容

5. **图片内容审核多层策略**
   - 敏感词OCR识别 → 肤色检测（快速过滤）→ 场景识别 → 物体检测 → 图片鉴伪
   - **YCbCr色彩空间肤色检测**：CB[77-127]、CR[133-173]
   - 深度学习模型：ResNet/EfficientNet图像分类，涉黄/涉暴/政治敏感检测
   - AI生成图片检测：鉴别PS或AI生成内容

6. **视频/音频审核**
   - 视频：关键帧提取（每秒1帧）+ 并行帧审核 + 音频审核
   - 音频：语音识别(ASR) → 文本审核 + 声纹分析（敏感人物声纹检测）
   - 实时性优化：流式处理，边上传边审核

7. **反垃圾策略**
   - 用户行为分析：发送频率检测、相似内容检测、新好友发送率、内容相似度、链接/二维码发送
   - 群发控制：消息频率限制（每分钟最多100条）、好友请求频率（每分钟最多20个）
   - 相似内容拦截：与历史消息相似度>0.9判定为重复内容

8. **机器学习特征工程**
   - 文本特征：文本长度、URL/手机号/邮箱/emoji/特殊字符/数字占比
   - 用户特征：发送频率、好友数量、账号年龄、认证状态、今日消息数
   - 上下文特征：与历史消息的语义相似度
   - 模型：SpamClassifier多分类，集成学习（XGBoost/LightGBM）

9. **人工审核系统**
   - 审核工作台：任务列表、审核操作、结果提交
   - 智能任务分配：负载均衡、优先级调度（高风险优先）
   - 举报与反馈：用户举报、重复举报检测、举报统计

10. **最佳实践**
    - 分层防御：敏感词过滤(<1ms) → 频率限制(<1ms) → 规则引擎(<5ms) → AI模型(<100ms) → 人工复核(异步)
    - 性能优化：热点内容缓存、DFA结果缓存
    - 监控指标：审核请求数、延迟分布、违规率、误杀率、漏过率
    - 灰度发布：新策略逐步放量，A/B测试验证效果

**技术来源**:
- 阿里云内容安全
- 腾讯云安全审核
- 网易云盾
- AWS Rekognition
- Google Cloud Vision API
- Azure Content Moderator
- 《深度学习文本分类实战》
- DFA算法与AC自动机算法

**下一步学习方向**:
1. Sealed Sender / 密封发送者（元数据最小化）
2. 群组加密（Sender Keys 方案）
3. 分布式事务一致性（2PC/TCC/Saga）
4. 微信/Telegram/WhatsApp 技术架构深度分析
5. 大规模 WebSocket 集群 + 负载均衡
6. 服务网格（Service Mesh）与 IM 微服务治理
7. 数据库全文搜索（Elasticsearch）与消息检索
8. 多设备同步协议与冲突处理（CRDT / OT）
---

## 2026-03-20 09:52 - 多设备同步与冲突处理（CRDT / OT）

### 新增知识文件: multi_device_sync_crdt.md（新增）

**核心发现**:

1. **乐观复制 vs 强一致性复制**
   - IM 系统选择乐观复制（离线优先体验）
   - 强一致性：需中心协调，离线时无法工作
   - 乐观复制：设备可独立修改，重新连接时自动合并，可能产生冲突

2. **OT（Operational Transformation）操作转换**
   - 核心：将操作作为基本单元，通过 Transform 函数将并发操作转换为等效序列
   - 典型场景：Google Docs 编辑，在位置5插入"Hello"，另一个设备同时插入"World"
   - 局限性：正确性证明困难，难以支持复杂数据结构，需中央服务器协调
   - 代表系统：Google Wave、ShareDB（Node.js OT）

3. **CRDT（Conflict-free Replicated Data Types）无冲突复制数据类型**
   - 核心：设计数学上保证最终一致的数据结构，无需中央协调
   - 三大特性：交换律（merge(A,B)=merge(B,A)）+ 幂等性 + 单调性
   - CmRDT（基于操作）：操作本身满足交换律，适合消息追加
   - CvRDT（基于状态）：状态可比较，取上界合并，适合配置同步
   - OT vs CRDT：OT 正确性难证明，CRDT 天然收敛，设计更简单

4. **Yjs：最高性能 CRDT 实现**
   - Benchmark 最快（vs Automerge）
   - 被 AFFiNE、Gitbook、Evernote、Linear、JupyterLab、NextCloud 等使用
   - 共享数据类型：Y.Text（协同文本）、Y.Array（有序列表）、Y.Map（键值对）
   - 网络无关（Network Agnostic）：只关心 Update 传递，可接 WebSocket/WebRTC/Redis
   - Yjs Provider 生态：y-websocket、y-webrtc、y-indexeddb、y-redis
   - IM 适用场景：消息草稿实时同步（Y.Text）、协作文档（群公告）

5. **IM 系统多设备同步架构设计**
   - 消息同步：基于消息 ID 序列的增量拉取（Snowflake ID 保证全局唯一有序）
   - 草稿同步：Yjs Y.Text CRDT（冲突自动合并，"Hello"+"World"→"HelloWorld"）
   - 会话状态：Last-Write-Wins（LWW）+ sync_token
   - 冲突策略矩阵：消息发送→乐观追加、消息删除→逻辑删除+墓碑、已读计数→LWW、编辑→LWW+版本历史

6. **离线追赶机制（Catch-up）**
   - 设备上线 → 按 sync_token 增量拉取 → 发送离线消息 → 服务器广播 → 客户端合并
   - Pending Queue：设备离线时消息暂存，上线后批量推送
   - 幂等保障：client_msg_id 唯一索引，重复发送不重复

7. **多设备消息分发策略**
   - 主设备（手机）：实时推送
   - 从设备（平板/桌面）：可批量推送（减少连接数）
   - 设备优先级：按设备活跃度分配推送带宽

8. **为本项目设计的混合方案**
   - 消息同步：消息 ID 序列增量拉取（简单可靠）
   - 草稿同步：Yjs Y.Text CRDT（自动冲突合并）
   - 会话状态：LWW + sync_token
   - 防冲突：Snowflake ID + client_msg_id 幂等 + 设备锁定 + 版本向量

9. **参考开源项目**
   - Tinode（Go，IM 服务器，类似 Matrix 的消息序列+增量拉取）
   - Matrix/Element（事件溯源+同步令牌，开源 IM 协议标准）
   - Yjs（JavaScript，通用 CRDT，适合草稿和协作文档）
   - ShareDB（Node.js，OT，适合 JSON 文档协同编辑）
   - OpenIM（Go，Kafka+增量同步）

**技术来源**:
- CRDT 官方站点: https://crdt.tech/（Preguiça et al. 论文）
- Yjs 文档: https://docs.yjs.dev/ + GitHub: https://github.com/yjs/yjs
- Yjs Benchmark: https://github.com/dmonad/crdt-benchmarks
- Automerge: https://automerge.org/（Rust+JS CRDT）
- Tinode IM: https://github.com/tinode/chat（Go IM 服务器）

**下一步学习方向**:
1. 服务网格（Service Mesh）与 IM 微服务治理（Istio/Linkerd） ✅ (已完成)
2. 大规模 WebSocket 集群架构与负载均衡
3. 数据库全文搜索（Elasticsearch）与消息语义检索
4. Sealed Sender / 密封发送者（元数据最小化）
5. 群组加密（Sender Keys 方案）

---

## 2026-03-20 21:52 - 服务网格（Service Mesh）与 IM 微服务治理

### 新增知识文件: service_mesh_im.md

**学习内容**:

1. **服务网格核心概念**
   - **服务网格定义**：微服务架构的基础设施层，处理服务间通信的复杂性
   - **数据平面 vs 控制平面**：代理层（数据平面）+ 管理组件（控制平面）
   - **零信任安全**：默认拒绝所有流量，仅允许经过验证的通信

2. **Istio vs Linkerd 对比**
   - **Istio**：最流行、功能最全面的服务网格，基于Envoy代理，支持Sidecar和Ambient两种数据平面模式
   - **Linkerd**：轻量级服务网格，使用Rust编写的微代理，专为Kubernetes优化
   - **IM系统选择建议**：复杂场景选Istio，资源敏感环境选Linkerd

3. **Istio 核心特性详解**
   - **安全架构**：身份与证书管理、双向TLS认证（mTLS）、授权策略、Permissive模式（便于迁移）
   - **流量管理**：虚拟服务（Virtual Services）、目标规则（Destination Rules）、网关（Gateways）
   - **可观察性**：指标（Metrics）、分布式追踪（Distributed Traces）、访问日志（Access Logs）

4. **Linkerd 架构分析**
   - **控制平面**：目的地服务（Destination Service）、身份服务（Identity Service）、代理注入器（Proxy Injector）
   - **数据平面**：Linkerd2-proxy（Rust编写）、Linkerd init容器（配置iptables规则）
   - **优势**：极致轻量（<10MB内存）、内存安全、简单易用

5. **IM系统服务网格架构设计**
   - **消息网关层**：Istio Ingress Gateway实现TLS终止和智能路由
   - **消息服务层**：基于消息优先级的智能路由、自动重试机制
   - **推送服务层**：外部服务集成（APNs、FCM等）、多区域推送优化

6. **关键IM场景配置**
   - **WebSocket连接管理**：长连接超时配置、最大连接数限制
   - **消息重试与可靠性**：5次重试策略、特定错误码重试
   - **用户在线状态同步**：连接池优化、熔断器配置

7. **性能优化策略**
   - **连接池优化**：基于业务负载调整连接池大小（WebSocket连接可达5万+）
   - **负载均衡策略**：最少连接（LEAST_CONN）策略、区域感知负载均衡
   - **缓存策略优化**：Redis原生协议支持（禁用TLS）

8. **监控与告警体系**
   - **关键监控指标**：连接相关指标、消息相关指标、错误相关指标
   - **Grafana仪表板**：WebSocket连接数监控、消息投递延迟P99监控
   - **告警配置**：基于业务SLA设置合理的告警阈值

9. **部署策略**
   - **渐进式部署**：监控模式（Permissive）→ 严格模式（STRICT）
   - **金丝雀部署**：10%流量到新版本，逐步增加权重
   - **蓝绿部署**：通过Virtual Service实现零停机部署

10. **最佳实践总结**
    - **安全性**：启用自动mTLS、实施零信任网络、定期轮换证书、审计日志记录
    - **性能**：连接池调优、合理超时设置、熔断器配置、负载均衡优化
    - **可观察性**：统一指标收集、分布式追踪、结构化日志、告警阈值优化

**技术来源**:
- Istio官方文档（istio.io）
- Linkerd官方文档（linkerd.io）
- Envoy代理文档（envoyproxy.io）
- 服务网格模式与实践（servicemesh.io）
- 零信任网络架构（Google BeyondProd）

**下一步学习方向**:
1. 大规模 WebSocket 集群架构与负载均衡 ✅ (已完成)
2. 数据库全文搜索（Elasticsearch）与消息语义检索
3. Sealed Sender / 密封发送者（元数据最小化）
4. 群组加密（Sender Keys 方案）
5. 边缘计算与IM系统（CDN集成、边缘节点消息路由）

---

## 2026-03-21 03:52 - 大规模 WebSocket 集群架构与负载均衡

### 新增知识文件: websocket_cluster_load_balancing.md（新增）

**核心发现**:

1. **连接层 vs 消息层分离架构**
   - 连接层管理 TCP 长连接、设备注册、心跳、消息下发；消息层处理消息发送、群发、离线存储
   - 两层通过分布式消息路由（Redis Pub/Sub）解耦

2. **L4 vs L7 负载均衡分层**
   - L4（传输层）：Envoy TCP Proxy / Nginx stream / LVS，基于 IP 一致性哈希，无粘性，性能极高
   - L7（应用层）：Envoy HTTP Router / Nginx，解析 HTTP/WebSocket 协议，支持粘性路由、TLS 卸载
   - IM 推荐架构：L4 作最外层入口 + L7 做 WSS 终止和路径路由

3. **一致性哈希算法族（Ring Hash / Maglev / P2C）**
   - Ring Hash：Envoy 默认，每个节点按 weight 比例在环上出现多次，最小 1024 槽，最大 65536 槽
   - Maglev：Google 2007 论文，O(1) 查表比 Ring Hash 快约 5 倍，节点变化时受影响 key 数量约为 Ring Hash 的 2 倍
   - P2C（Power of Two Choices）：随机选 2 个节点选连接数最少者，Envoy Weighted Least Request 算法，数学证明接近最优
   - IM 推荐：一致性哈希（粘性）+ P2C（动态负载均衡）

4. **粘性会话设计**
   - Cookie 粘性 / Session ID 哈希 / IP+Port 复合哈希三种方案
   - WebSocket Upgrade 时基于 user_id 哈希，同一用户始终路由到同一节点
   - 节点下线：灰度下线（标记 drain）→ 等待连接自然关闭 → 消息补发

5. **分布式消息路由（跨节点传递）**
   - Redis Pub/Sub 最常用：消息写库 → 发布到 Channel "im:user:{userId}" → 订阅节点推送
   - Redis Pub/Sub 非持久化 → 消息持久化优先 + 离线消息兜底 + 客户端 ACK 重试

6. **Netty WebSocket 集群关键配置**
   - ulimit -n ≥ 100000、net.core.somaxconn = 4096、net.core.netdev_max_backlog = 65535
   - Netty Boss 单线程 + Worker = CPU * 2（最多 32）线程，WriteBufferWaterMark 32KB/64KB
   - IdleStateHandler 心跳：读空闲 60s / 写空闲 30s 发送 Ping

7. **健康检查双重机制**
   - 主动：L4 TCP 探测 + L7 HTTP /health 端点（interval=10s，unhealthy_threshold=3）
   - 被动：连续 5 次失败标记不健康，base_ejection_time=30s，max_ejection_percent=50%
   - IM 特殊指标：连接数 > 8万 / P99 延迟 > 500ms / 内存 > 80%

8. **全球负载均衡（GSLB）**
   - DNS GeoIP：根据用户地理位置返回最近节点 IP
   - Anycast：多节点共享同一 IP，路由器自动选最近路径（Cloudflare / AWS Global Accelerator）
   - Envoy locality_weight_aware_routing：优先同城 → 同区域 → 跨区域

9. **推荐架构**
   - GSLB（DNS GeoIP）→ L4（Envoy TCP / NLB）→ L7（Envoy HTTP Router，WSS 终止 + user_id 哈希）→ 网关集群（Netty，每节点 5-10 万连接）→ 分布式消息路由（Redis Pub/Sub）→ 业务服务集群

**技术来源**:
- Envoy 官方文档 - 负载均衡策略（https://www.envoyproxy.io/docs/envoy/latest/intro/arch_overview/upstream/load_balancing/load_balancers）
- NGINX 官方文档 - HTTP 负载均衡（https://docs.nginx.com/nginx/admin-guide/load-balancer/http-load-balancer/）
- Netty 官方用户指南（https://netty.io/wiki/user-guide-for-4.x.html）
- Google Maglev 论文（https://static.googleusercontent.com/media/research.google.com/en//pubs/archive/44824.pdf）

**下一步学习方向**:
1. Sealed Sender / 密封发送者（元数据最小化） ✅ (已完成)
2. 群组加密（Sender Keys 方案）
3. 数据库全文搜索（Elasticsearch）与消息语义检索
4. 边缘计算与 IM 系统（CDN 集成、边缘节点消息路由）
5. 微信/Telegram/WhatsApp 技术架构深度分析

---

## 2026-03-21 09:52 - Sealed Sender 密封发送者（元数据最小化）

### 新增知识文件: sealed_sender.md（新增）

**核心发现**:

1. **Sealed Sender 概念与动机**
   - 将发送者身份加密在消息信封内部，服务器仅知道接收者，不知道发送者
   - 解决 Signal Protocol 的元数据泄露问题（消息内容加密，但服务器知道发送者）
   - 典型场景：举报人保护、敏感通信、私密群组、匿名反馈
   - 威胁模型：保护服务器/运营商/网络监听者不知道发送者，但不保护接收者

2. **三层加密架构**
   - 第1层 Outer Envelope：接收者地址（明文）+ 整个消息密文（服务器转发用）
   - 第2层 Inner Envelope：发送者身份（用接收者 Identity Key 加密）+ 时间戳 + 序列号 + 发送者签名
   - 第3层消息内容：消息正文（Double Ratchet 加密）+ 附件
   - 加密算法：AES-256-GCM（发送者身份加密）+ Ed25519（发送者签名）+ X3DH（密钥交换）

3. **协议流程**
   - 初始化：Bob 生成 Identity Key Pair + Signed PreKey + PreKey 批次，上传到 KeyRegistry
   - 发送：Alice 获取 Bob 公钥包 → 构建 Inner Envelope（用 Bob IK_B 加密 Alice 证书）→ 构建消息内容（X3DH+Double Ratchet）→ 服务器只看到接收者+密文
   - 接收：Bob 收到消息 → 解密 Outer Envelope → 解密 Inner Envelope（用 ikB）→ 验证 Alice 签名（用 IK_A）→ 解密消息内容

4. **服务器端架构设计**
   - KeyRegistry：维护密封发送者公钥注册表（IK_B + SPK_B + PreKey 批次）
   - SealedSenderService：接收密封消息 → 验证格式 → 加入接收者队列 → 在线推送
   - 服务器无法解密内容，也不知道发送者是谁
   - 数据库设计：sealed_sender_keys 表 + prekeys 表 + trusted_identities 表

5. **客户端实现要点**
   - 发送端：获取公钥包 → 构建发送者证书（UUID+Identity Key+过期时间，用 ikA 签名）→ SealedSenderEncrypt 加密
   - 接收端：SealedSenderDecrypt 解密 → 验证发送者签名 → 信任管理（首次需要用户确认）
   - 信任管理：首次密封消息需要用户确认，显示安全码，信任后保存 Identity Key

6. **安全属性**
   - 发送者匿名：服务器不知道发送者身份，无法构建通信关系图
   - 完整性保护：发送者身份不可伪造（Ed25519 签名）+ 消息内容不可篡改（Double Ratchet AEAD）
   - 重放攻击防护：序列号 + 时间戳机制
   - 前向保密：密钥泄露影响有限，Identity Key 泄露不影响已建立会话

7. **限制与注意事项**
   - 首次消息需要公钥包（接收者需先注册公钥到 KeyRegistry）
   - 接收者需在线注册，否则无法密封发送
   - 功能限制：无法回复密封消息、离线消息处理可能不同
   - 安全考虑：信任首次接触、证书过期机制、Rate Limiting 防枚举攻击
   - 性能开销：额外约 100-200 字节 + Ed25519 签名验证

8. **IM 系统集成方案**
   - 可选启用：Sealed Sender 作为可选特性，用户可选择是否启用
   - API 设计：GET /api/v1/sealed-sender/keys/{userId}（获取公钥包）+ POST /api/v1/sealed-sender/messages（发送密封消息）
   - 降级策略：公钥获取失败时自动降级到普通发送模式
   - 与 Signal Protocol 关系：Sealed Sender 在消息信封层面添加发送者匿名性（增强层）

9. **与其他隐私特性的关系**
   - Sealed Sender vs 普通 Signal：普通 Signal 服务器知道发送者+接收者，Sealed Sender 服务器只知道接收者
   - Sealed Sender vs Sender Keys：Sealed Sender 隐藏发送者身份（从服务器角度），Sender Keys 是群组消息加密
   - Sealed Sender vs 阅后即焚：Sealed Sender 隐藏发送者身份，阅后即焚销毁消息内容，可组合使用

**技术来源**:
- Signal Protocol 架构推导（基于 X3DH/Double Ratchet 机制扩展）
- libsignal-protocol-java/c/rust 实现细节
- Signal-Server 架构参考

**下一步学习方向**:
1. 群组加密（Sender Keys 方案）✅ (已完成)
2. 数据库全文搜索（Elasticsearch）与消息语义检索
3. 边缘计算与 IM 系统（CDN 集成、边缘节点消息路由）
4. 微信/Telegram/WhatsApp 技术架构深度分析
5. 隐私评分与信任模型
- - - 
 
 
 
 # #   2 0 2 6 - 0 3 - 2 2   0 9 : 5 2   -   9p�piʕ�[K��hbfp˓�R}W e b R T C ��<NF U / M C U ˓5�/p�? 
 
 # # #   g��-��0v�V"k:   r e a l t i m e _ a u d i o _ v i d e o . m d �X�g�o�p}
 
 
 
 * * ͓?z>~Y� bG^* * : 
 
 
 
 1 .   * * W e b R T C   ͓?z>~˓5�/p* * 
 
       -   * * R T C P e e r C o n n e c t i o n * * :   �~��`5p-W�tig�p4^(��RsrG��Q4^Y�?       -   * * R T C D a t a C h a n n e l * * :   Y�}\�`��HrA]��6l�N��\de\m�^�g`m5g6}Hg? '����o
 
       -   * * �m2 �b4ZzO�%* * :   ��3lC~�m2 �bȓ�]�Yc�$1&l��9�D P \�V[C E J�k� ? 
 
 2 .   * * S T U N / T U R N / I C E   W��_��͓? * 
 
       -   * * S T U N * * :   Y� bG^O��}f�gCo\�h[A T �~�7p
 
       -   * * T U R N * * :   N A T �~WW� �_Q0�t�0i(��RQ�D�g�Y?       -   * * I C E * * :   w�D��Y��Y�Zȓ� cm�[[~���0�w�[? 
 
 3 .   * * �o-l�gYt�Uv�|m3l��˓5�/p* * 
 
       -   * * S F U �+We l e c t i v e   F o r w a r d i n g   U n i t �? * :   ��Y�Z^g �B_˓5�/p�|[o o m / G o o g l e   M e e t ���Vde
 
       -   * * M C U �!Wu l t i p o i n t   C o n t r o l   U n i t �? * :   #Z�\�`+hr˓5�/p�|\6}�qK��hb0}�t��pV"�?       -   * * S I M W�? * :   Y�&b� xO?�R��U����Vfy�r[F U ͓ĉA]ɓ2 "k��Y�Z^g �B_
 
       -   * * S V C �+Wc a l a b l e   V i d e o   C o d i n g �? * :   Y��X^p�fK��h b*}.��O}W��ffy�o,l0w
 
 
 
 4 .   * * ,h.�xO�j����[* * 
 
       -   * * Yt�Uv�* * :   V P 8 �X@~��Y}�� N. 2 6 4 �X@~��Y}��BNP 9 ��NV 1 
 
       -   * * ʕ�bv�* * :   O p u s �X�[4Z�O}��N. 7 1 1   P C M �X6}�qxe�t�o}
 
       -   * * n�o�y���_*X�~<�}D D �? * :   R T P �o�d4Q��A%Mw�~\ne��N2 E E f��p�j
 
 
 
 5 .   * * I M �~d��|ʕ�[K��h#b��t3 �t? * 
 
       -   * * �m^�vYt�R* * :   )�X[4^P 2 P   W e b R T C   +   S T U N / T U R N ȓ�]�Y_�?       -   * * �m^�G0Yt�R* * :   S F U ˓5�/p  +   w�D�T U R N ȓ�]�Yc�?       -   * * �oFI�Y? * :   R��U�z�[�^F U ƕ�UbQ  +   O�'1�`�t�qGmg��V 0
 
       -   * * �[� gZ-a�g�Y? * :   J i t s i ��uNe d i a s o u p ��3Ni o n 
 
       -   * * _��U{ȓ�]�Y* * :   T w i l i o ��Ng o r a ��QNo o m   V i d e o   S D K 
 
 
 
 6 .   * * ��FXQ|m:j�[�~+h�f* * 
 
       -   * *  b�|���P2|��? * :   /u@���|m�bx�  +   ���0#库C�W�WC C �?   �m 2�[�� 22�
 
       -   * * +hr|m:j�[* * :   R��U����V�V���P2|  +   /uE�]��C�W  +   .�zO�]��C�W
 
       -   * * �~��Y�~�-}V�? * :   �~��"k+hr  +   T��q� Cix��? +    b�|R��V2]
 
 
 
 7 .   * * )�b6^�m�^�v�t? * 
 
       -   * * O��bme���V#r* * :   I C E �5�� uO� 7NT T �[�`\~��wO�mV�oT�]��xO+u9pb�S�t?       -   * * �t�Q/v�[�0�S* * :   C h r o m e : / / w e b r t c - i n t e r n a l s ��DNe b R T C   S t a t s ��DNi r e s h a r k 
 
 
 
 8 .   * * 9p
Y�Sp��Q�j* * 
 
       -   * * �~��W�~��Y5p? * :   D T L S - S R T P �%X�|�t�0}��N2 E E ��A%Mw
 
       -   * * �tWW�h��C�W* * :   J W T / O A u t h 2 . 0 엡�$U`i�\	v��yOHoĕ.ax���U� xO4U9pptx�͓? 
 
 9 .   * * ��'1��˓5�/p* * 
 
       -   * * Hg-W-}S F U ��'1��* * :   G S L B   +   Hg-W-}z��PcP�X"{\m? g�Yg�? 	Z�f�SO�,[��/ �~pT���?       -   * * #Z�\�`\mbfp˓? * :   O��An\m/b}S F U �?   �~yOAn\m/b}�m2 �b/ "�&1�W��HrA]�?   C D N 
 
 
 
 1 0 .   * * ȓE�uo�t*[
Z* * 
 
         -   * * W e b T r a n s p o r t * * :   i�p�,|Q U I C �~\?mcm�^"kig?         -   * * W e b C o d e c s   A P I * * :   cm�^��R���*}YtG�rc��P I 
 
         -   * * A I �o�p�]* * :   œ?�XQĕ�]�j��}O�yR��U����V� }O�VT�&1/pe�? 
 
 * * ��� ȓ�uogZ? * : 
 
 -   W e b R T C 9p;j�g�V0�TWt t p s : / / w e b r t c . o r g / �? -   M D N   W e b R T C   A P I �TWt t p s : / / d e v e l o p e r . m o z i l l a . o r g / e n - U S / d o c s / W e b / A P I / W e b R T C _ A P I �? -   R F C   8 8 2 5 :   W e b R T C �Y�P*X
 
 -   J i t s i ˓5�/p�V0�TWt t p s : / / j i t s i . o r g / a r c h i t e c t u r e / �? -   m e d i a s o u p �V0�TWt t p s : / / m e d i a s o u p . o r g / d o c u m e n t a t i o n / �? 
 
 * * �m)[�z�Y�0�m�r�gZ�? * : 
 
 1 .   ZXO��)1� yOGnT�!fp˓�R}A P N s / F C M / X��Pbf��)1� �O}
 
 2 .   R��U�z�[�_�f����1|˓5�/p�1Wi D B / C o c k r o a c h D B �? 3 .   Hg-W-}�t�{u�m�]D N ƕ�U�W
 
 4 .   �[���O/ T e l e g r a m ʕ�[K��hbfp˓�RA~4d@��W˓? 5 .   W e b T r a n s p o r t W��_��m�^2|"�
 
 