# Signal Protocol 端到端加密详解

> 本文档详细解析 Signal Protocol（Signal 协议）的技术原理、密钥管理、会话建立流程，以及在 IM 系统中的实现方案。

## 目录
1. [概述](#1-概述)
2. [核心密码学基础](#2-核心密码学基础)
3. [密钥体系详解](#3-密钥体系详解)
4. [X3DH 密钥协商协议](#4-x3dh-密钥协商协议)
5. [Double Ratchet 算法](#5-double-ratchet-算法)
6. [会话建立流程](#6-会话建立流程)
7. [消息加密流程](#7-消息加密流程)
8. [多设备支持](#8-多设备支持)
9. [服务端角色设计](#9-服务端角色设计)
10. [IM 系统实现方案](#10-im-系统实现方案)
11. [开源库与SDK](#11-开源库与sdk)
12. [安全最佳实践](#12-安全最佳实践)
13. [已知应用案例](#13-已知应用案例)

---

## 1. 概述

### 1.1 什么是 Signal Protocol

Signal Protocol（原称 TextSecure Protocol）是由 Open Whisper Systems（现 Signal Messenger LLC）开发的**前向保密（Forward Secrecy）和棘轮前向保密（Ratcheting Forward Secrecy）**即时通讯加密协议。

它是目前**安全等级最高的 IM 加密协议**，被以下应用采用：
- **Signal** — 协议发明者官方应用
- **WhatsApp** — 2016 年集成，覆盖 20 亿+ 用户
- **Facebook Messenger（Secret Conversations）** — 端到端加密模式
- **Google Allo（已停服）** — 端到端加密模式
- **Skype（Private Conversations）** — 端到端加密模式

### 1.2 核心安全特性

| 特性 | 说明 |
|------|------|
| **前向保密（FS）** | 长期密钥泄露不影响历史消息安全 |
| **棘轮前向保密（RFS）** | 每次消息后密钥自动更新，密钥泄露只影响有限消息 |
| **异步安全 | 接收方离线时仍可建立安全会话 |
| **多设备支持 | 单账户多设备，每个设备独立密钥链 |
| **元数据最小化 | 服务器不存储消息内容，仅处理加密消息 |
| **会话隔离 | 每个对话独立密钥，不会跨会话传播 |
| **不可否认性 | 密钥指纹可验证对方身份 |

### 1.3 协议组成

Signal Protocol 由两大核心组件构成：

```
Signal Protocol
├── X3DH (Extended Triple Diffie-Hellman) — 初始密钥协商
│   └── 用于建立"第一把钥匙"，在异步环境下工作
└── Double Ratchet — 消息加密与密钥更新
    ├── Symmetric Ratchet (对称棘轮) — 每条消息更新密钥
    └── DH Ratchet (DH棘轮) — 定期引入新DH对刷新密钥链
```

---

## 2. 核心密码学基础

### 2.1 椭圆曲线密码学（ECC）

Signal Protocol 使用 **Curve25519**（默认）或 P-256 椭圆曲线：

```java
// 密钥对生成（Java libsignal）
IdentityKeyPair identityKeyPair = KeyHelper.generateIdentityKeyPair();
// identityKeyPair.getPublicKey()  → Curve25519 公钥
// identityKeyPair.getPrivateKey() → Curve25519 私钥
```

**Curve25519 优势**：
- 密钥长度仅 32 字节（256 位安全级别）
- 签名/加密速度快
- 设计时考虑了旁路攻击防护
- 无需椭圆曲线参数，简化实现

### 2.2 Diffie-Hellman 密钥交换（DH）

DH 允许双方在公开信道上建立共享秘密：

```
Alice: 私钥 a, 公钥 A = g^a
Bob:   私钥 b, 公钥 B = g^b
共享密钥 K = B^a = A^b = g^(ab)
```

Signal Protocol 在 Curve25519 上执行 DH，得到 32 字节共享密钥。

### 2.3 HKDF 密钥派生

HKDF（HMAC-based Key Derivation Function）用于从原始 DH 输出派生出各类密钥：

```java
// libsignal 中的 HKDF 实现
HKDFOutput kdf = new HKDFv3(
    inputKeyMaterial,      // DH 输出
    info,                 // 上下文标签（如 "WhisperMessageKeys"）
    64                    // 输出长度
);
byte[] messageKey = kdf.deriveKeys();
```

典型派生用途：
- `WhisperMessageKeys` — 消息加密密钥（32 字节 AEAD 密钥 + 24 字节 nonce）
- `WhisperRatchet` — 棘轮状态密钥
- `WhisperSession` — 会话握手密钥

### 2.4 AES-GCM 消息加密

每条消息使用 **AES-256-GCM** 加密：
- 256 位密钥（由 HKDF 派生）
- 96 位 nonce（每次消息递增，不重复）
- 128 位认证标签（自动包含在密文中）

```
明文消息 → AES-GCM-256(密钥, nonce, 明文) → 密文 || 认证标签
```

---

## 3. 密钥体系详解

### 3.1 三层密钥架构

Signal Protocol 采用**三层密钥体系**，每层有不同生命周期：

```
┌─────────────────────────────────────────────┐
│  L1: Identity Key Pair（身份密钥对）          │
│  - 长期密钥，安装时生成                       │
│  - 用于签名 Signed PreKey                    │
│  - 生命周期: 用户账户存在期间                 │
├─────────────────────────────────────────────┤
│  L2: Signed PreKey（签名预密钥）             │
│  - 中期密钥，每月轮换                        │
│  - 被 Identity Key 签名后上传服务器          │
│  - 生命周期: 约 1 个月                       │
├─────────────────────────────────────────────┤
│  L3: PreKeys（预密钥）                      │
│  - 一次性密钥，100+ 个/设备                  │
│  - 每次建立会话消耗一个                      │
│  - 生命周期: 用完即弃                        │
└─────────────────────────────────────────────┘
```

### 3.2 安装时密钥生成

```java
// libsignal Java - 安装时密钥生成
IdentityKeyPair identityKeyPair = KeyHelper.generateIdentityKeyPair();
// 生成 Curve25519 身份密钥对

int registrationId = KeyHelper.generateRegistrationId();
// 0-16383 之间的随机数，用于标识此设备上的会话

List<PreKeyRecord> preKeys = KeyHelper.generatePreKeys(0, 100);
// 生成 100 个 PreKey，ID 从 0 开始

SignedPreKeyRecord signedPreKey = KeyHelper.generateSignedPreKey(
    identityKeyPair,   // 用身份私钥签名
    5                   // 版本号
);
```

**密钥存储安全要求**：
- Identity Key Pair → **最高安全级别存储**（Keychain/Keystore）
- Registration ID → 本地持久化存储
- PreKeys → 服务器存储 + 本地备份
- Signed PreKey → 服务器存储 + 本地备份

### 3.3 PreKey  replenishment（补充机制）

客户端需要维护 PreKey 池，当剩余量低于阈值（如 20 个）时自动补充：

```java
// 补充 PreKeys
if (preKeyStore.getCount() < 20) {
    List<PreKeyRecord> newPreKeys = KeyHelper.generatePreKeys(currentMaxId + 1, 100);
    preKeyStore.storePreKeys(newPreKeys);
    accountManager.setPreKeys(identityKey.getPublicKey(), lastResortKey, 
                               signedPreKeyRecord, newPreKeys);
}
```

### 3.4 PreKey 类型对比

| 类型 | 数量 | 签名 | 用途 | 生命周期 |
|------|------|------|------|----------|
| Identity Key | 1 对 | 自签名（根信任锚） | 签署 Signed PreKey | 账户生命周期 |
| Signed PreKey | 1 个（当前）+ 历史 | Identity Key 签名 | 中期密钥轮换 | 约 1 个月 |
| PreKey | 100+ 个 | 无 | 异步首次建立会话 | 一次性消耗 |
| Last Resort PreKey | 1 个 | 无 | PreKey 耗尽时的最后手段 | 一次性消耗 |

---

## 4. X3DH 密钥协商协议

### 4.1 X3DH 是什么

X3DH（Extended Triple Diffie-Hellman）是 Signal Protocol 的**初始密钥协商协议**，由 Marlinspike 和 Perrin 在 2016 年提出。其设计目标是：

> 在接收方**离线**的情况下，发送方仍能建立共享密钥。

### 4.2 X3DH 参与的密钥

Alice（发起方）需要 Bob 的以下公钥：
- `IK_B` — Bob 的 Identity Public Key（长期）
- `SPK_B` — Bob 的 Signed PreKey Public（中期）
- `PK_B` — Bob 的一个 PreKey Public（一次性）

Alice 自身的密钥：
- `EK_A` — Alice 的 Ephemeral Key（临时密钥，用完即弃）

### 4.3 X3DH 数学原理

X3DH 通过组合多个 DH 计算来派生共享密钥：

```
DH1 = Curve25519(IK_A, SPK_B)   // Alice身份私钥 × Bob签名预钥公钥
DH2 = Curve25519(EK_A, IK_B)    // Alice临时密钥 × Bob身份公钥
DH3 = Curve25519(EK_A, SPK_B)   // Alice临时密钥 × Bob签名预钥公钥
DH4 = Curve25519(EK_A, PK_B)    // Alice临时密钥 × Bob预钥公钥（如果有）

SK = HKDF(DH1 || DH2 || DH3 || DH4)
```

**为什么这样设计？**
- `DH1` 提供前向保密（即使临时密钥泄露，只要 Identity Key 安全则安全）
- `DH2` 确保双方都有贡献（Alice 用她的 Identity Key，Bob 用他的）
- `DH3` 增加密钥熵
- `DH4` 使每个 PreKey 只能使用一次（一次性预密钥）

### 4.4 X3DH 握手流程

```
Alice（在线，发起方）                    Bob 服务器                         Bob（离线）
     |                                     |                                  |
     |---获取 Bob PreKeyBundle------------>|                                  |
     |<-- IK_B, SPK_B, SPK_B签名, PK_B ---|                                  |
     |                                     |                                  |
     | 生成 EK_A（临时密钥对）              |                                  |
     | 计算 DH1, DH2, DH3, DH4            |                                  |
     | SK = HKDF(DH1||DH2||DH3||DH4)      |                                  |
     |                                     |                                  |
     |----发送预加密消息（含 EK_A, SPK_B签名, PK_B ID）---------------------->|
     |                                     |     (存储等待 Bob 上线)           |
     |                                     |                                  |
     |======== 使用 SK 加密第一条消息 =====|                                  |
     |                                     |                                  |
     |  [此时 Alice 已建立会话，Bob 收到消息后建立相同会话]                   |
```

### 4.5 X3DH 消息结构（Initial Message）

Alice 发送给 Bob 的第一条消息包含：
```
- identity:      Alice 的 Identity Key 公钥（可选，Bob 可能已有）
- ephemeralKey:  Alice 的 Ephemeral Key 公钥
- usedPreKeyId:  使用的 PreKey ID（如果有）
- baseKey:       Alice 的基础公钥（用于 DH Ratchet）
- message:       用 SK 加密的第一条消息密文
```

### 4.6 X3DH 安全分析

| 攻击方式 | X3DH 防护 |
|----------|-----------|
| 长期密钥泄露 | DH1 的 Identity Key 部分提供前向保密 |
| 临时密钥泄露 | DH2、DH3 的 Identity Key 贡献仍保护密钥 |
| PreKey 重放 | 每个 PreKey 仅能使用一次（PK_B 的 DH4 部分） |
| 中间人攻击 | Identity Key + Signed PreKey 签名链验证 |
| 离线发起攻击 | PreKey 机制允许任意发送方建立安全会话 |

---

## 5. Double Ratchet 算法

### 5.1 为什么需要 Double Ratchet

X3DH 建立了一个初始共享密钥 SK，但仅靠 SK 加密所有消息**不够安全**：
- 如果某次消息的密钥泄露，历史消息的密钥也可能被推算
- 需要**持续更新密钥**，使每次通信都使用新密钥

Double Ratchet 的核心思想：
- **对称棘轮（Symmetric Ratchet）**：每条消息后密钥前推
- **DH 棘轮（DH Ratchet）**：定期更换 DH 密钥对，彻底断开密钥链

### 5.2 Symmetric Ratchet（对称棘轮）

每发送/接收一条消息，密钥链前进一步：

```
Ratchet Step (发送方向):
  messageKey_n = HKDF(ratchetKey_n-1, "WhisperMessageKey")
  chainKey_n   = HKDF(chainKey_n-1, "WhisperRatchet")

  发送 messageKey_n 加密第 n 条消息
  更新 chainKey_n 为下一轮使用
```

特点：
- **单向密钥流**：发送和接收使用各自的 chainKey
- **不可逆**：无法从当前 messageKey 反推历史 messageKey
- **轻量**：仅需 HKDF 计算

### 5.3 DH Ratchet（DH 棘轮）

定期引入新的 DH 密钥对，大幅刷新密钥链：

```
Bob 生成新密钥对: (DH_RatchetKey_n-1_private, DH_RatchetKey_n_public)

Alice 计算新 DH:
  DH_output = Curve25519(Alice_RatchetKey_private, Bob_new_RatchetKey_public)
  new_chainKey = HKDF(old_chainKey || DH_output, "WhisperRatchet")
  new_messageKey = HKDF(new_chainKey, "WhisperMessageKey")
```

**关键效果**：
- 即使攻击者获得了某个链密钥，也无法推算未来的密钥
- 每轮 DH Ratchet 都引入新的熵（新鲜随机数）
- 密钥链被"棘轮"卡住，无法倒回

### 5.4 Double Ratchet 组合

```
每条消息:
  1. 从 chainKey 派生出 messageKey
  2. 用 messageKey 加密消息（AES-GCM）
  3. 更新 chainKey（Symmetric Ratchet 一步）
  4. （定期）执行 DH Ratchet，引入新 DH 密钥对

密钥推导路径:
  SK (X3DH初始密钥)
    → chainKey_0
        → messageKey_0 → 消息0密文
        → chainKey_1
            → messageKey_1 → 消息1密文
            → [DH Ratchet] → 新 chainKey
                → messageKey_2 → 消息2密文
                ...
```

### 5.5 密钥派生函数

```java
// Double Ratchet 中使用的 HKDF 派生
HKDFParameters params = new HKDFParameters(
    ratchetChainKey,          // 链密钥
    "WhisperRatchet".getBytes(),  // info 标签
    null                      // salt（可选）
);
SymmetricKey derivedKey = HKDF.deriveKeys(params, 64);

// 消息密钥派生
messageKey = derivedKey.derive(SymmetricKey.Type.MESSAGE);
```

### 5.6 前向保密（Forward Secrecy）保证

| 场景 | 密钥泄露范围 |
|------|-------------|
| 仅消息密钥泄露 | 仅该条消息可解密 |
| 链密钥泄露 | 仅该链上后续消息可解密（至 DH Ratchet） |
| DH 私钥泄露 | 仅该 DH 轮次及之后的会话可解密 |
| Identity Key 泄露 | **不影响历史会话**（历史会话由 DH Ratchet 保护） |

---

## 6. 会话建立流程

### 6.1 完整会话建立步骤

```
步骤1: 客户端安装时（一次性）
├── 生成 Identity Key Pair
├── 生成 Signed PreKey（每月轮换）
├── 生成 100+ 个 PreKeys
├── 注册到服务器（上传公钥）
└── 本地安全存储私钥

步骤2: Alice 发起会话（首次联系 Bob）
├── 从服务器获取 Bob 的 PreKeyBundle
│   └── (IK_B, SPK_B, SPK_B_signature, PK_B)
├── 验证 SPK_B 签名（用 IK_B 验证）
├── 生成 Ephemeral Key Pair (EK_A)
├── 计算 X3DH 共享密钥 SK
├── 初始化 Double Ratchet 状态
├── 用 SK 加密第一条消息
└── 发送 Initial Message（含 EK_A, baseKey, encrypted message）

步骤3: Bob 接收并建立会话（Bob 首次与 Alice 对话）
├── 从 Initial Message 获取 EK_A
├── 从本地存储获取 IK_B, SPK_B, PK_B（私钥）
├── 计算 X3DH 共享密钥 SK（与 Alice 相同）
├── 初始化 Double Ratchet 状态
├── 解密第一条消息
└── 发送回复（自动进入 Double Ratchet 流程）
```

### 6.2 发送加密消息

```java
// libsignal Java - 发送消息
SessionCipher sessionCipher = new SessionCipher(sessionStore, recipientId, deviceId);

// 加密消息
CiphertextMessage message = sessionCipher.encrypt("Hello world!".getBytes("UTF-8"));

// message.serialize() 包含:
// - 消息头（DH Ratchet 公钥、消息编号等）
// - 密文（AES-GCM 加密）
// - 认证标签
```

### 6.3 接收解密消息

```java
// libsignal Java - 接收消息
SignalServiceCipher cipher = new SignalServiceCipher(
    new SignalServiceAddress(USERNAME),
    new MySignalProtocolStore()
);

// 解密
SignalServiceContent content = cipher.decrypt(envelope);
String messageBody = content.getDataMessage().get().getBody().get();
```

### 6.4 会话状态管理

```java
// 会话状态序列化（用于多设备同步）
SessionRecord record = sessionCipher.getSessionRecord();
byte[] serializedState = record.serialize();

// 存储到数据库
sessionStore.storeSession(recipientId, deviceId, serializedState);

// 从数据库恢复
SessionRecord restoredRecord = new SessionRecord(serializedState);
sessionCipher = new SessionCipher(sessionStore, recipientId, deviceId, restoredRecord);
```

---

## 7. 消息加密流程

### 7.1 消息结构

每条加密消息包含两个部分：

```
┌──────────────────────────────────────┐
│ Header（未加密，可被服务器读取）        │
├──────────────────────────────────────┤
│ - DH Ratchet 公钥（当前轮次）          │
│ - 发送方链编号（PN）                   │
│ - 消息编号（MESSAGE_NUMBER）           │
│ - 上一条消息的链编号（PREV_CHAIN_COUNT）│
├──────────────────────────────────────┤
│ Body（加密部分）                      │
├──────────────────────────────────────┤
│ - 密文（加密内容）                     │
│ - AEAD 认证标签                        │
└──────────────────────────────────────┘
```

### 7.2 加密算法选择

| 组件 | 算法 | 说明 |
|------|------|------|
| 密钥交换 | Curve25519 | DH 密钥协商 |
| 签名 | Ed25519 | Signed PreKey 签名 |
| 密钥派生 | HKDF-SHA256 | 从原始 DH 输出派生各类密钥 |
| 消息加密 | AES-256-GCM | 对称加密 + 认证 |
| 链密钥 | HKDF-SHA256 | 链密钥前推 |

### 7.3 消息传输流程

```
发送方 Alice:
  消息文本
    → 消息密钥加密（AES-GCM）
    → 添加消息头（DH 公钥 + 编号）
    → Base64 编码
    → 通过 WebSocket/TLS 发送到服务器

服务器（仅转发，不解密）:
  接收加密消息
    → 查找接收方设备列表
    → 广播到所有目标设备

接收方 Bob (每个设备):
  接收加密消息
    → 解析消息头（获取 DH 公钥 + 编号）
    → 执行 DH Ratchet（如需要）
    → 从链密钥派生出消息密钥
    → AES-GCM 解密验证
    → 获取消息文本
```

---

## 8. 多设备支持

### 8.1 多设备密钥隔离

Signal Protocol 的多设备支持基于**每个设备独立密钥链**：

```
用户 Bob 的设备:
├── 主设备（Device A）
│   ├── Identity Key（主设备持有，其他设备无）
│   ├── Signed PreKey（各设备独立）
│   ├── PreKeys（各设备独立，数量 100+）
│   └── Session State（与各联系人的会话）
│
├── 设备 B
│   ├── Signed PreKey（设备 B 独立生成）
│   ├── PreKeys（设备 B 独立，数量 100+）
│   └── Session State（与各联系人的会话）
│
└── 设备 C（平板）
    ├── Signed PreKey
    ├── PreKeys
    └── Session State
```

### 8.2 服务器端密钥注册

```java
// 每个设备独立注册
accountManager.setPreKeys(
    identityKey.getPublicKey(),    // 设备 Identity 公钥
    lastResortKey,                // Last Resort PreKey
    signedPreKeyRecord,           // 此设备的 Signed PreKey
    oneTimePreKeys                // 此设备的 PreKeys
);
```

### 8.3 消息分发策略

服务器将消息发送到用户的所有设备：
```
Alice 发送消息给 Bob:
  消息 → 服务器 → Bob 的所有设备（Device A, B, C...）
  
  设备 A 解密：用自己的会话状态解密 ✓
  设备 B 解密：用自己的会话状态解密 ✓
  设备 C 解密：用自己的会话状态解密 ✓
```

### 8.4 密钥同步问题

**问题**：新设备加入时，旧设备上的会话无法转移

**解决方案**：
1. 新设备独立建立会话（从 PreKeyBundle 重新执行 X3DH）
2. 历史消息不自动同步（安全性设计）
3. 用户可通过"安全转移"功能手动迁移（需物理接触）

---

## 9. 服务端角色设计

### 9.1 服务器的职责（最小化原则）

Signal Protocol 的服务器**不存储也不处理明文消息**，其职责被严格限制：

| 服务器职责 | 说明 |
|-----------|------|
| **密钥注册** | 存储用户的 Identity Key、PreKeys（均为公钥） |
| **PreKeyBundle 分发** | 按需向请求方提供目标用户的 PreKeyBundle |
| **消息转发** | 存储并转发加密消息（服务器不解密） |
| **设备管理** | 设备注册/注销，消息分发到多设备 |
| **未读消息队列** | 存储离线消息，用户上线后投递 |
| **发送确认** | 确认消息已投递（不暴露消息内容） |

### 9.2 服务器存储的密钥数据

```
Server Storage:
  users/{userId}/devices/{deviceId}/
  ├── identity_key        # Identity Public Key
  ├── signed_prekey       # Signed PreKey Public
  ├── signed_prekey_sig   # Identity Key 对 Signed PreKey 的签名
  └── prekeys/{id}        # PreKey Public（ID → 公钥映射）
```

### 9.3 安全性假设

Signal Protocol 的安全证明基于以下假设：
1. 服务器诚实但好奇（服务器正确执行协议，但会尝试获取信息）
2. 服务器存储加密消息，但无法解密
3. 服务器可以识别消息的发送方和接收方（不可避免的元数据）

---

## 10. IM 系统实现方案

### 10.1 架构设计

```
┌──────────────────────────────────────────────────────────┐
│                      IM 客户端                            │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────┐ │
│  │ Tauri (桌面) │  │ Flutter (移动) │ │ Web (可选)        │ │
│  │ Rust E2EE   │  │ Dart E2EE    │ │ JS E2EE          │ │
│  └──────┬───────┘  └──────┬───────┘  └───────┬──────────┘ │
│         └─────────────────┼──────────────────┘           │
│                           │                               │
│  ┌────────────────────────▼────────────────────────────┐ │
│  │              Signal Protocol Library                 │ │
│  │  - X3DH Key Agreement                                 │ │
│  │  - Double Ratchet (发送 + 接收)                       │ │
│  │  - Session State Management                          │ │
│  │  - Key Storage (Secure Storage)                      │ │
│  └────────────────────────┬────────────────────────────┘ │
└───────────────────────────┼──────────────────────────────┘
                            │ TLS 加密通道
┌───────────────────────────▼────────────────────────────┐
│                   IM Server (Spring Boot)                 │
│  ┌─────────────┐  ┌──────────────┐  ┌─────────────────┐   │
│  │ Key Registry │ │ Message Hub  │ │  Device Manager   │   │
│  │ (公钥存储)   │ │ (消息转发)    │ │ (多设备分发)      │   │
│  └─────────────┘  └──────────────┘  └─────────────────┘   │
│                                                           │
│  ┌─────────────────────────────────────────────────────┐  │
│  │              PostgreSQL / MySQL                      │  │
│  │  - 用户公钥、PreKeys                                 │  │
│  │  - 加密消息存储（服务器不解密）                       │  │
│  │  - 设备注册表                                        │  │
│  └─────────────────────────────────────────────────────┘  │
│                                                           │
│  ┌─────────────────────────────────────────────────────┐  │
│  │              Redis (消息缓存 + 离线队列)             │  │
│  └─────────────────────────────────────────────────────┘  │
└───────────────────────────────────────────────────────────┘
```

### 10.2 密钥生成（客户端启动）

```java
// Spring Boot 客户端启动时
public class E2EESetupService {
    
    public void initializeKeys() {
        // 1. 生成 Identity Key Pair
        IdentityKeyPair identityKey = KeyHelper.generateIdentityKeyPair();
        
        // 2. 生成 Registration ID
        int registrationId = KeyHelper.generateRegistrationId();
        
        // 3. 生成 Signed PreKey
        SignedPreKeyRecord signedPreKey = 
            KeyHelper.generateSignedPreKey(identityKey, currentSignedPreKeyId);
        
        // 4. 生成 PreKeys
        List<PreKeyRecord> preKeys = KeyHelper.generatePreKeys(0, 100);
        
        // 5. 安全存储私钥
        secureStorage.storeIdentityKeyPair(identityKey);
        secureStorage.storeSignedPreKey(signedPreKey);
        secureStorage.storePreKeys(preKeys);
        
        // 6. 上传公钥到服务器
        keyRegistryService.registerKeys(
            identityKey.getPublicKey(),
            signedPreKey,
            preKeys
        );
    }
}
```

### 10.3 会话建立 API

```java
// REST API: 获取用户 PreKeyBundle
@GetMapping("/api/keys/{userId}")
public PreKeyBundleResponse getPreKeyBundle(@PathVariable String userId) {
    // 返回目标用户的 PreKeyBundle（包含 Identity Key + Signed PreKey + PreKey）
    return keyRegistryService.getPreKeyBundle(userId);
}

// REST API: 注册自己的密钥
@PostMapping("/api/keys/register")
public ResponseEntity<Void> registerKeys(@RequestBody KeyRegistrationRequest request) {
    keyRegistryService.storeKeys(currentUserId, request.getDeviceId(), request);
    return.ok();
}
```

### 10.4 消息发送流程

```java
// 发送加密消息
public class MessageService {
    
    public void sendMessage(String recipientId, String plaintextContent) {
        // 1. 获取或创建会话
        SessionCipher cipher = sessionManager.getSessionCipher(recipientId);
        
        // 2. 加密消息
        CiphertextMessage encrypted = cipher.encrypt(
            plaintextContent.getBytes(StandardCharsets.UTF_8)
        );
        
        // 3. 发送密文到服务器
        MessagePayload payload = MessagePayload.builder()
            .recipientId(recipientId)
            .senderId(currentUserId)
            .deviceId(currentDeviceId)
            .encryptedContent(encrypted.serialize())
            .timestamp(System.currentTimeMillis())
            .build();
        
        messageHub.send(payload);
    }
}
```

### 10.5 消息接收流程

```java
// WebSocket 接收消息
@MessageMapping("/message")
public void handleMessage(MessageEnvelope envelope) {
    // 1. 验证发送方身份
    if (!identityStore.isTrustedIdentity(envelope.getSenderIdentityKey())) {
        throw new SecurityException("Untrusted sender");
    }
    
    // 2. 解密消息
    SignalServiceCipher cipher = new SignalServiceCipher(
        new SignalServiceAddress(currentUserId),
        signalProtocolStore
    );
    
    SignalServiceContent content = cipher.decrypt(envelope);
    String plaintext = content.getDataMessage().get().getBody().get();
    
    // 3. 存储解密后消息（应用层）
    messageStorage.store(currentUserId, plaintext, content.getTimestamp());
    
    // 4. 广播到 UI
    eventPublisher.publishEvent(new MessageReceivedEvent(plaintext, envelope));
}
```

### 10.6 数据库设计

```sql
-- 用户公钥注册表
CREATE TABLE user_keys (
    user_id VARCHAR(64) PRIMARY KEY,
    device_id INT NOT NULL,
    identity_key BYTEA NOT NULL,        -- Identity Public Key
    signed_prekey BYTEA NOT NULL,       -- Signed PreKey Public
    signed_prekey_sig BYTEA NOT NULL,   -- 签名
    signed_prekey_id INT NOT NULL,
    registration_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    UNIQUE (user_id, device_id)
);

-- PreKey 表
CREATE TABLE prekeys (
    user_id VARCHAR(64) NOT NULL,
    device_id INT NOT NULL,
    prekey_id INT NOT NULL,
    public_key BYTEA NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    PRIMARY KEY (user_id, device_id, prekey_id)
);

-- 加密消息存储（服务器不存储明文）
CREATE TABLE encrypted_messages (
    message_id BIGSERIAL PRIMARY KEY,
    sender_id VARCHAR(64) NOT NULL,
    recipient_id VARCHAR(64) NOT NULL,
    device_id INT NOT NULL,              -- 目标设备
    encrypted_content BYTEA NOT NULL,    -- 加密消息
    timestamp BIGINT NOT NULL,
    delivered BOOLEAN DEFAULT FALSE,
    delivered_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW(),
    INDEX idx_recipient_unread (recipient_id, device_id, delivered)
);

-- 设备表
CREATE TABLE user_devices (
    user_id VARCHAR(64) NOT NULL,
    device_id INT PRIMARY KEY,
    device_name VARCHAR(128),
    last_seen TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW()
);
```

### 10.7 安全存储设计

```java
// 密钥安全存储接口
public interface SecureStorage {
    // 存储 Identity Key Pair（最高安全级别）
    void storeIdentityKeyPair(IdentityKeyPair keyPair);
    IdentityKeyPair getIdentityKeyPair();
    
    // 存储会话状态
    void storeSession(String recipientId, int deviceId, byte[] sessionState);
    Optional<byte[]> loadSession(String recipientId, int deviceId);
    
    // 存储 PreKey
    void storePreKeys(List<PreKeyRecord> preKeys);
    Optional<PreKeyRecord> loadPreKey(int preKeyId);
    
    // 存储 Signed PreKey
    void storeSignedPreKey(SignedPreKeyRecord signedPreKey);
    SignedPreKeyRecord getSignedPreKey(int signedPreKeyId);
}
```

桌面端（Tauri/Rust）建议使用：
- **Windows**: DPAPI 或 Windows Credential Manager
- **macOS**: Keychain
- **Linux**: libsecret

移动端（Flutter）建议使用：
- **Android**: Android Keystore
- **iOS**: iOS Keychain

---

## 11. 开源库与SDK

### 11.1 官方 Signal 维护库

| 语言 | 库 | 地址 | 状态 |
|------|-----|------|------|
| Java/Android | libsignal-protocol-java | https://github.com/signalapp/libsignal-protocol-java | 归档但可用 |
| C | libsignal-protocol-c | https://github.com/signalapp/libsignal-protocol-c | 活跃 |
| Rust | libsignal-protocol-rust | https://github.com/signalapp/rust-signal | 核心库 |
| Swift | libsignal-protocol-swift | https://github.com/signalapp/libsignal-swift | 活跃 |
| JavaScript | libsignal-protocol-js | https://github.com/signalapp/libsignal-protocol-js | Web 端 |
| Go | libsignal-go | https://github.com/signalapp/libsignal-go | 实验性 |

### 11.2 第三方实现

| 项目 | 语言 | 说明 |
|------|------|------|
| **curve25519-dalek** | Rust | 高性能 Curve25519 实现 |
| **libsignal-service-java** | Java | Signal 服务通信库 |
| **python-signal-protocol** | Python | Python 实现 |
| **libsignal-service-node** | Node.js | Node.js 服务通信 |
| **ChatSDK** | Swift/ObjC | iOS IM SDK 含 E2EE |

### 11.3 依赖引入（Maven）

```xml
<!-- libsignal-protocol-java -->
<dependency>
    <groupId>org.whispersystems</groupId>
    <artifactId>signal-protocol-java</artifactId>
    <version>2.8.1</version>
</dependency>

<!-- 或直接使用 Signal 的 Android 库 -->
<dependency>
    <groupId>org.thoughtcrime.securesms</groupId>
    <artifactId>signal-protocol-android</artifactId>
    <version>最新版本</version>
</dependency>
```

### 11.4 前端集成方案（桌面端 Web）

对于 Tauri 桌面端的 Web 前端，可以使用 **libsignal-protocol-js**：

```javascript
// 前端使用 libsignal-protocol-js
import { KeyHelper, SessionBuilder, SessionCipher } from 'libsignal-protocol-js';

async function initializeE2EE() {
    // 生成密钥
    const identityKeyPair = await KeyHelper.generateIdentityKeyPair();
    const registrationId = await KeyHelper.generateRegistrationId();
    const preKey = await KeyHelper.generatePreKey(identityKeyPair);
    const signedPreKey = await KeyHelper.generateSignedPreKey(
        identityKeyPair, 
        preKey.id
    );
    
    // 注册到服务器
    await api.registerKeys({
        identityKey: identityKeyPair,
        preKey,
        signedPreKey
    });
}
```

---

## 12. 安全最佳实践

### 12.1 密钥安全

| 实践 | 说明 |
|------|------|
| **私钥不离设备** | Identity 私钥、Session 状态仅存储在设备本地 |
| **使用安全硬件** | Android Keystore / iOS Keychain 优先 |
| **PreKey 补充机制** | 低于阈值时自动补充，防止服务中断 |
| **定期轮换 Signed PreKey** | 每月轮换，保留旧密钥用于旧会话解密 |
| **会话状态持久化** | 会话状态丢失 = 历史消息无法解密 |

### 12.2 传输安全

- **传输层 TLS**：所有客户端-服务器通信必须 TLS 1.3
- **证书固定（Certificate Pinning）**：防止 TLS 中间人攻击
- **消息签名**：验证消息未被篡改（AEAD 自动保证）

### 12.3 服务器安全

| 措施 | 说明 |
|------|------|
| **最小权限原则** | 服务器不持有任何私钥 |
| **密钥存储隔离** | 公钥数据库与应用数据库隔离 |
| **消息存储加密** | 消息体已加密，存储时可选再加密层 |
| **审计日志** | 记录密钥注册/请求操作 |

### 12.4 已知威胁与缓解

| 威胁 | 缓解措施 |
|------|---------|
| 中间人攻击 | Identity Key 指纹验证（带外确认） |
| PreKey 耗尽攻击 | 速率限制 + 监控 |
| 重放攻击 | 消息编号去重 + 时间戳检查 |
| 服务器被攻破 | 服务器无会话密钥，无法解密历史消息 |
| 设备丢失 | 无特殊缓解（需要用户主动删除设备） |

---

## 13. 已知应用案例

### 13.1 WhatsApp 集成方式

WhatsApp 于 2016 年宣布集成 Signal Protocol，关键实现差异：

- 使用 **Noise Protocol Framework** 简化握手
- 在 X3DH 之前添加了**身份验证步骤**（基于用户电话号码）
- 支持**群组加密**（Sender Keys 方案）
- 服务器存储加密消息直到接收方拉取

### 13.2 Signal Messenger 官方实现

- **会话管理**：会话一旦建立可长期存在
- **多设备**：完整的多设备支持，包括消息同步
- **密封发送者（Sealed Sender）**：隐藏发送方元数据
- **一次性查看（View Once）**：图片/视频阅后即焚
- **Disappearing Messages**：定时消息自动删除

### 13.3 IM 系统集成路线图

基于 Signal Protocol 实现端到端加密，需要分阶段实施：

| 阶段 | 任务 | 复杂度 |
|------|------|--------|
| 1️⃣ | 客户端密钥生成与安全存储 | HIGH |
| 2️⃣ | X3DH 会话建立流程 | HIGH |
| 3️⃣ | Double Ratchet 消息加密 | HIGH |
| 4️⃣ | 服务器端密钥注册服务 | MEDIUM |
| 5️⃣ | 多设备密钥分发 | HIGH |
| 6️⃣ | 消息发送/接收全流程集成 | HIGH |
| 7️⃣ | 密钥轮换与补充机制 | MEDIUM |
| 8️⃣ | 群组加密（Sender Keys） | VERY HIGH |

---

## 参考资料

1. Signal Protocol 官方文档：https://signal.org/docs/
2. X3DH 论文：https://signal.org/blog/x3dh/
3. Double Ratchet 算法：https://signal.org/blog/doubleratchet/
4. libsignal-protocol-java：https://github.com/signalapp/libsignal-protocol-java
5. libsignal-protocol-c：https://github.com/signalapp/libsignal-protocol-c
6. Signal Server（已不开源，但架构参考价值高）

---

*最后更新: 2026-03-19 15:52*
*来源: GitHub libsignal-protocol-java/c 源码 + Signal 官方博客 + 开源文档*
