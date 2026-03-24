# 多设备同步与冲突处理（CRDT / OT）

## 概述

多设备同步（Multi-Device Sync）是现代即时通讯系统的核心技术之一。当同一用户在手机、平板、桌面端等多个设备上使用 IM 应用时，需要解决：

1. **消息一致性**：所有设备看到相同的消息历史
2. **操作同步**：在一个设备上的操作（发送、删除、编辑）能正确同步到其他设备
3. **离线处理**：设备离线后重新连接，能正确追赶（catch-up）错过的变更
4. **冲突解决**：多个设备同时操作同一数据时，能自动合并冲突

## 核心问题：乐观复制与冲突

在分布式系统中，多设备同步本质上是**乐观复制（Optimistic Replication）**问题：

- **强一致性复制**：需要中心服务器协调，设备离线时无法工作
- **乐观复制**：设备可独立离线修改，重新连接时自动合并，**可能产生冲突**

IM 系统选择乐观复制的原因：
- 移动设备网络不稳定（地铁、电梯）
- 用户期望随时可用
- 离线优先（Local-First）体验

## 冲突处理的两大流派

### 1. OT（Operational Transformation）操作转换

**核心思想**：将操作（Operation）作为基本单元，通过**变换函数（Transform Function）**将并发操作转换为等效序列。

**典型场景**（Google Docs 编辑）：
```
设备A: 在位置5插入"Hello"  →  操作 O_A = [insert, pos=5, text="Hello"]
设备B: 在位置5插入"World"  →  操作 O_B = [insert, pos=5, text="World"]

服务器收到 O_A 后执行，然后需要将 O_B 变换：
Transform(O_B, O_A) = [insert, pos=10, text="World"]  // 位置向右偏移

最终结果: "HelloWorld"（无论操作顺序如何）
```

**OT 关键变换函数**：
- `transform(O1, O2)`：给定 O2 先于 O1 执行，返回 O1 的等效操作
- 必须满足**转换正确性**（Convergence）：无论操作顺序，最终状态相同

**OT 的局限性**：
- 正确性证明困难，需要精心设计变换函数
- 难以支持复杂数据结构（如树、嵌套对象）
- 服务器必须维护操作历史的"文档状态"

**经典 OT 系统**：
- Google Wave（OT 的集大成者）
- Apache Wave（Google Wave 开源分支）
- ShareDB（基于 OT 的 JSON 文档同步，Node.js）

### 2. CRDT（Conflict-free Replicated Data Types）无冲突复制数据类型

**核心思想**：设计**数学上保证最终一致**的数据结构，无需中央协调。

**关键特性**：
- **交换律**：`merge(A, B) = merge(B, A)` — 合并顺序无关
- **幂等性**：`merge(A, A) = A` — 重复合并无影响
- **单调性**：状态只能前进，不会回退

**结果**：无论操作顺序如何、是否重复、是否离线，最终状态**必然收敛**。

**CRDT 两大类别**：

#### CmRDT（基于操作）
- 操作本身满足交换律、幂等性
- 需要可靠广播（确保所有操作最终送达所有副本）
- 适合：消息同步（追加操作天然满足交换律）

#### CvRDT（基于状态）
- 状态本身可比较（Partial Order）
- 合并 = 取上界（Join）
- 副本通过定期同步完整状态（快照 + 增量）
- 适合：配置同步、计数器

### OT vs CRDT 对比

| 维度 | OT | CRDT |
|------|-----|------|
| 设计难度 | 高（变换函数正确性难证明） | 低（数学属性天然保证） |
| 数据结构 | 受限（需可变换操作） | 丰富（Map/Array/Set/Register） |
| 内存开销 | 中（需维护操作历史） | 中（需存储向量时钟/版本向量） |
| 同步模式 | 需中央服务器协调 | 去中心化/点对点均可 |
| 收敛保证 | 需要严格正确变换函数 | 数学上天然保证 |
| 离线支持 | 需操作重放 | 天然支持 |
| 代表系统 | Google Docs, ShareDB | Yjs, Antidote, Riak |

### CRDT 的 IM 系统适用性

**IM 消息流的核心操作**：
1. **消息追加**（最常见）：Append-only，天然满足交换律 → **CmRDT**
2. **消息删除**：逻辑删除（软删除）而非物理删除 → Tombstone + 墓碑合并
3. **消息编辑**：Last-Write-Wins 或 自定义策略
4. **草稿同步**：Yjs Y.Text 或 G-Set

**推荐方案**：
- 消息列表 → G-Counter + 墓碑集（只追加，不修改已有消息）
- 消息草稿 → Yjs Y.Text（支持增量同步）
- 消息状态（已读/未读）→ Last-Write-Wins Register
- 联系人列表 → OR-Set（可移除集合）

## Yjs：最高性能的 CRDT 实现

### 核心架构

Yjs 是当前**最快**的 CRDT 实现（benchmark：https://github.com/dmonad/crdt-benchmarks），被以下产品使用：
- AFFiNE（本地优先知识库）
- Gitbook、Evernote、Linear
- JupyterLab（协作笔记本）
- NextCloud（内容协作平台）
- AWS SageMaker

### Yjs 核心 API

```javascript
import * as Y from 'yjs'

// 创建文档（Shared Document）
const ydoc = new Y.Doc()

// 共享数据类型
const ytext = ydoc.getText('message')           // 文本（用于草稿）
const yarray = ydoc.getArray('message_ids')    // 消息 ID 列表
const ymap = ydoc.getMap('conversation')       // 会话属性

// 本地修改
ytext.insert(0, 'Hello')
ymap.set('unreadCount', 5)

// 同步状态更新（二进制格式）
const update = Y.encodeStateAsUpdate(ydoc)       // 导出
Y.applyUpdate(ydoc, update)                      // 导入

// 增量同步（Diff）
const update = Y.encodeStateAsUpdate(ydoc)
const diff = Y.diffUpdate(update, baseline)      // 只同步差异

// 观察变化
ytext.observe(event => {
  console.log(event.target.toString())  // "Hello"
})

// 撤销/重做
const undoManager = new Y.UndoManager(ytext)
undoManager.undo()
```

### Yjs 共享数据类型

| 类型 | 描述 | IM 场景 |
|------|------|---------|
| `Y.Text` | 协同文本编辑器 | 消息草稿、群公告 |
| `Y.Array` | 有序列表 | 消息列表、群成员列表 |
| `Y.Map` | 键值对 | 会话元数据、用户配置 |
| `Y.Doc` | 文档容器 | 整体数据容器 |

### Yjs 网络层抽象

Yjs **不依赖任何网络协议**，只关心更新（Update）的传递：

```javascript
// 抽象 Provider 接口
class WebSocketProvider {
  constructor(serverUrl, ydoc) {
    this.ydoc = ydoc
    this.connect(serverUrl)
  }
  
  // 发送本地更新到服务器
  ydoc.on('update', (update) => {
    this.send(update)
  })
  
  // 接收远程更新
  onMessage(update) {
    Y.applyUpdate(this.ydoc, update)
  }
}

// 可选 Provider：
// - y-websocket: WebSocket
// - y-webrtc: WebRTC (P2P)
// - y-indexeddb: 本地持久化
// - y-redis: Redis 跨进程同步
```

**IM 系统中的 Yjs 应用**：
- 消息草稿实时同步（Yjs Y.Text）
- 协作文档编辑（群公告、共享笔记）
- 实时光标/选择同步（Show typing cursor）

## IM 系统多设备同步架构

### 同步层次

```
┌─────────────────────────────────────────────────┐
│                  客户端（多设备）                    │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐     │
│  │  手机端   │  │  平板端   │  │  桌面端   │     │
│  │  (主设备) │  │ (从设备) │  │ (从设备) │     │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘     │
│       │              │              │           │
│       └──────────────┼──────────────┘           │
│                      │ Yjs CRDT Sync             │
│              ┌───────┴────────┐                  │
│              │   Local-First   │                  │
│              │  Storage (SQLite │                  │
│              │  / IndexedDB)   │                  │
│              └─────────────────┘                  │
└───────────────────────┬─────────────────────────┘
                        │ WebSocket 长连接
                        │ + 增量同步协议
                        ▼
┌───────────────────────────────────────────────────┐
│                  IM 服务器                          │
│  ┌──────────────────────────────────────────────┐ │
│  │            同步协调服务 (Sync Service)         │ │
│  │  ┌────────────┐  ┌────────────────────────┐  │ │
│  │  │ 设备注册表   │  │ 增量更新队列 (Pending │  │ │
│  │  │ Device     │  │ Updates)              │  │ │
│  │  │ Registry   │  │  - device_id → updates│  │ │
│  │  └────────────┘  └────────────────────────┘  │ │
│  │  ┌────────────┐  ┌────────────────────────┐  │ │
│  │  │ 同步检查点  │  │ 消息广播器             │  │ │
│  │  │ Sync       │  │ (多设备消息分发)       │  │ │
│  │  │ Checkpoint │  │                        │  │ │
│  │  └────────────┘  └────────────────────────┘  │ │
│  └──────────────────────────────────────────────┘ │
│  ┌──────────────────────────────────────────────┐ │
│  │            消息持久化服务                       │ │
│  │  MySQL (消息) + Redis (会话缓存)               │ │
│  └──────────────────────────────────────────────┘ │
└───────────────────────────────────────────────────┘
```

### 同步协议设计

#### 1. 设备注册与身份

```
每个设备拥有唯一设备标识：device_token = HMAC(user_id + device_type + timestamp)
每个用户最多 N 个设备（建议：5-10 个）
设备类型：mobile/tablet/desktop/web
```

#### 2. 同步检查点（Sync Checkpoint）

```
客户端存储最后同步位置：
{
  "sync_token": "s_12345",      // 服务器同步令牌
  "device_id": "device_abc",
  "last_msg_id": 98765,
  "last_update_ts": 1710800000
}

服务器按 sync_token 增量返回更新：
GET /sync?since=s_12345&device_id=device_abc
→ 返回 { sync_token: "s_12350", updates: [...] }
```

#### 3. 消息同步（基于时间戳 + 版本向量）

```
方案A：基于消息 ID 序列
- 消息 ID = snowflake（时间+机器+序号）
- 设备按消息 ID 区间拉取：GET /messages?after=98765&limit=100
- 简单高效，适合追加为主的场景

方案B：基于同步令牌（Matrix 协议）
- 服务器维护每个设备的同步令牌
- 令牌 = 服务器处理的消息事件流的位置
- 客户端定期轮询或 WebSocket 推送

方案C：基于向量时钟（向量版本号）
- 每个设备维护本地向量时钟 VC[device_id] = counter
- 消息携带向量时钟
- 设备间通过向量时钟判断因果顺序
```

#### 4. 冲突处理策略

```
IM 消息冲突处理策略（按场景）：

场景1: 消息发送冲突（两设备同时发送消息）
→ 策略：乐观追加，各自生成唯一 msg_id
→ 结果：两条消息都显示，无冲突（Append-only 天然无冲突）

场景2: 消息删除冲突（A设备删除，B设备已读了该消息）
→ 策略：逻辑删除（Tombstone），记录删除者+时间戳
→ 结果：若B设备已读，删除通知仅作为"消息被撤回"提示

场景3: 草稿同步冲突（两设备同时编辑草稿）
→ 策略：Yjs CRDT 自动合并
→ 结果：两设备草稿内容自动合并（"Hello" + "World" → "HelloWorld"）

场景4: 会话状态冲突（已读计数、置顶状态）
→ 策略：Last-Write-Wins（LWW）+ 逻辑时钟
→ 结果：时间戳最新者覆盖

场景5: 消息编辑冲突（两设备同时编辑同一消息）
→ 策略：Last-Write-Wins + 编辑历史（显示"已编辑"）
→ 或：采用 OT 对话式编辑（复杂，IM 场景很少需要）

场景6: 联系人/群组变更冲突
→ 策略：事件溯源（Event Sourcing）+ 幂等合并
→ 结果：所有事件最终都会执行，只是顺序可能不同
```

### 离线同步与追赶机制

```
设备重新上线时的同步流程：

1. 连接建立
   设备 → 服务器: WebSocket connect + device_token

2. 拉取缺失消息
   设备 → 服务器: GET /sync?since=s_12345
   服务器 → 设备: { updates: [...], sync_token: "s_12400" }

3. 发送离线操作
   设备 → 服务器: POST /send_messages  (包含离线期间的消息)
   服务器 → 设备: ACK + msg_ids

4. 服务器广播
   服务器 → 所有设备: 广播新消息

5. 客户端合并
   客户端: 按时间戳排序 + 去重（msg_id 唯一索引）
```

### 多设备消息分发

```
场景：用户A 在手机发送消息，希望同步到平板和桌面

1. 手机发送消息 → 服务器
2. 服务器持久化 → MySQL
3. 服务器推送到手机（实时）
4. 服务器推送到平板 + 桌面（WebSocket）
5. 若设备离线 → 存入 Pending Queue
6. 设备上线 → 从 Pending Queue 拉取

优化：设备优先级
- 主设备（手机）：实时推送
- 从设备（平板/桌面）：可以批量推送（减少连接数）
```

## 实现方案：为本 IM 项目设计的多设备同步

### 架构选择

基于 IM 的特点（消息追加为主、实时性要求高、离线支持重要），采用**混合方案**：

1. **消息同步**：基于消息 ID 序列的增量拉取（简单可靠）
2. **草稿同步**：Yjs Y.Text CRDT（支持冲突自动合并）
3. **会话状态**：Last-Write-Wins + sync_token
4. **冲突策略**：Append-only 优先，尽量避免锁

### 关键 API 设计

```
# 同步检查点
GET /sync/status
→ { sync_token, last_msg_id, device_count, last_active }

# 增量同步
GET /sync/updates?since={sync_token}&device_id={device_id}
→ { sync_token, messages: [...], deletions: [...], drafts: [...] }

# 草稿同步（基于 Yjs）
POST /sync/draft
Body: { conversation_id, update: <binary> }
→ { ack: true, version: 42 }

# 设备管理
GET /devices
→ [{ device_id, type, name, last_active, is_current }]
DELETE /devices/{device_id}   # 登出设备

# 消息发送
POST /messages
Body: { conversation_id, content, client_msg_id }
→ { msg_id, server_ts }
```

### 核心数据表设计

```sql
-- 设备表
CREATE TABLE devices (
  id BIGINT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  device_token VARCHAR(64) UNIQUE NOT NULL,
  device_type ENUM('mobile','tablet','desktop','web'),
  device_name VARCHAR(64),
  last_active_at TIMESTAMP,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_user_devices (user_id, last_active_at)
);

-- 同步检查点
CREATE TABLE sync_checkpoints (
  user_id BIGINT PRIMARY KEY,
  device_id VARCHAR(64) NOT NULL,
  sync_token VARCHAR(64) NOT NULL,
  last_msg_id BIGINT NOT NULL,
  last_update_ts TIMESTAMP,
  UNIQUE KEY uk_device (device_id)
);

-- 待推送消息队列（设备离线时）
CREATE TABLE pending_messages (
  id BIGINT PRIMARY KEY,
  target_device_id VARCHAR(64) NOT NULL,
  msg_id BIGINT NOT NULL,
  payload JSON,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_device_pending (target_device_id, created_at)
);

-- 消息草稿（Yjs 二进制存储）
CREATE TABLE message_drafts (
  user_id BIGINT,
  conversation_id BIGINT,
  device_id VARCHAR(64),
  yjs_state BLOB,           -- Yjs encodeStateAsUpdate
  version INT DEFAULT 1,
  updated_at TIMESTAMP,
  PRIMARY KEY (user_id, conversation_id)
);
```

### 客户端实现

```
桌面端（Tauri）:
- 消息本地存储: SQLite (via rusqlite)
- 草稿同步: Yjs (WASM) + y-indexeddb
- WebSocket: 自动重连 + 增量同步

移动端（Flutter）:
- 消息本地存储: sqflite
- 草稿同步: Yjs (via yjs package) + drift/sqflite
- WebSocket: flutter_socket_io 或 web_socket_channel
```

### 防冲突最佳实践

1. **消息 ID 使用 Snowflake**：时间+机器+序号，保证全局唯一且有序
2. **客户端消息 ID（client_msg_id）**：幂等发送，重复不重复
3. **设备锁定**：敏感操作（删除会话）需要主设备授权
4. **版本向量**：追踪每个消息的来源设备
5. **冲突日志**：记录冲突发生次数和类型，用于产品优化

## 相关开源项目参考

| 项目 | 技术栈 | 多设备同步方案 |
|------|--------|--------------|
| **Tinode** | Go | 消息序列 + 增量拉取（类似 Matrix）|
| **Matrix/Element** | Python (Synapse) | 事件溯源 + 同步令牌 + 增量拉取 |
| **Yjs** | JavaScript | CRDT（任意数据结构的通用方案）|
| **ShareDB** | Node.js | OT（JSON 文档协同编辑）|
| **OpenIM** | Go | Kafka 消息队列 + 增量同步 |

## 技术来源

- CRDT 官方站点: https://crdt.tech/
- Yjs 文档: https://docs.yjs.dev/
- Yjs GitHub: https://github.com/yjs/yjs
- CRDT 论文: "Conflict-free Replicated Data Types" (Preguiça et al., 2018)
- Automerge: https://automerge.org/ (另一主流 CRDT 实现，Rust + JS)
- Tinode IM: https://github.com/tinode/chat (Go 实现的 IM 服务器)

## 下一步学习方向

1. 服务网格（Service Mesh）与 IM 微服务治理（Istio/Linkerd）
2. 大规模 WebSocket 集群架构与负载均衡
3. 数据库全文搜索（Elasticsearch）与消息语义检索
4. Sealed Sender / 密封发送者（元数据最小化）
5. 群组加密（Sender Keys 方案）
