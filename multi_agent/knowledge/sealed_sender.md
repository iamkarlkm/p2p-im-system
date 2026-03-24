# Sealed Sender（密封发送者）- 元数据最小化

> **学习日期**: 2026-03-21
> **知识来源**: Signal Protocol 架构推导 + X3DH/Double Ratchet 机制扩展
> **前置知识**: `end_to_end_encryption.md`（Signal Protocol 基础）

---

## 1. 核心概念与动机

### 1.1 什么是 Sealed Sender？

**Sealed Sender（密封发送者）** 是 Signal Protocol 的进阶隐私特性，旨在**将发送者的身份从元数据中移除**，使服务器无法获知消息的发送者是谁。

**核心目标**: 服务器只知道"谁收到了消息"，但不知道"谁发送了消息"。

### 1.2 解决的问题

在标准 Signal Protocol 中：
- 消息内容是端到端加密的（服务器无法解密）
- **但服务器知道发送者和接收者的身份**（元数据泄露）

```
普通 Signal 消息流（服务器可见）：
Server → [Alice → Bob]: 知道 Alice 发给 Bob
           [消息内容加密]

Sealed Sender 消息流（服务器可见）：
Server → [??? → Bob]: 只知道 Bob 收到了消息
              不知道发送者是谁
              [消息内容加密 + 发送者身份加密]
```

### 1.3 典型应用场景

- **举报人保护**: 举报人向记者发送消息，记者知道举报人是谁，但服务器不知道
- **敏感通信**: 政治异议人士、律师与客户、医疗人员与患者
- **私密群组**: 群组成员向群组发消息，服务器只知道群组，不知道具体谁发了什么
- **匿名反馈**: 用户向企业/客服发送匿名反馈

---

## 2. 威胁模型分析

### 2.1 保护对象

| 威胁方 | 普通 Signal | Sealed Sender |
|--------|-----------|---------------|
| 服务器/运营商 | 知道发送者+接收者+时间 | 只知道接收者+时间 |
| 网络监听者 | 知道发送者+接收者+时间 | 只知道接收者+时间 |
| 第三方 subpoena | 同上 | 同上 |

### 2.2 不保护的内容

- **接收方**: Bob 收到消息后，知道 Alice 发送了（消息本身解密后包含发送者身份）
- **接收方服务器**: 如果接收方配合（罕见），仍可获取信息
- **元数据时间**: 消息发送时间仍然泄露
- **消息大小**: 消息大小模式仍可能泄露信息
- **流量分析**: 高频通信模式仍可能被分析

### 2.3 安全目标

**目标**: 即使 Signal 服务器被完全入侵/被政府强制配合，攻击者也无法获知"谁在和谁通信"。

---

## 3. 加密机制详解

### 3.1 核心加密原理

Sealed Sender 的核心思想：**将发送者身份加密在消息信封内部**。

```
Sealed Sender 消息结构:
┌─────────────────────────────────────────┐
│ Outer Envelope（外层信封）              │
│  - 接收者: Bob（明文，服务器可见）       │
│  - 加密内容:                            │
│    ├─ Inner Envelope（内层信封）        │
│    │   └─ 发送者身份（用接收者公钥加密）│
│    └─ 消息内容（X3DH 会话加密）         │
└─────────────────────────────────────────┘
```

服务器只能看到外层信封（接收者 + 密文），无法解密内层信封。

### 3.2 三层加密架构

```
第1层: Outer Envelope（外层信封）
       - 接收者地址（明文）
       - 整个消息密文（服务器转发用）

第2层: Inner Envelope（内层信封）
       - 发送者身份（用接收者 Identity Key 加密）
       - 发送时间戳
       - 序列号（防重放）
       - 发送者签名

第3层: 消息内容
       - 消息正文（Double Ratchet 加密）
       - 附件（如果存在）
```

### 3.3 加密算法

- **发送者身份加密**: AES-256-GCM（用接收者的 Identity Key）
- **发送者身份签名**: Ed25519（用发送者的 Identity Key）
- **消息内容加密**: AES-256-GCM（Double Ratchet 会话密钥）
- **密钥交换**: X3DH（Extended Triple Diffie-Hellman）

---

## 4. 协议流程

### 4.1 初始化阶段（一次性）

**Bob（接收者）**:
1. 生成 Identity Key Pair `(IK_B, ikB)`（长期，Ed25519）
2. 生成 Signed PreKey `(SPK_B, spkB)`（中期，X25519）
3. 生成 PreKey 批次 `(PK_i, pk_i)`（一次性，X25519）
4. 将公钥上传到服务器 KeyRegistry

**Alice（发送者）**:
1. 生成自己的 Identity Key Pair `(IK_A, ikA)`（长期）
2. 从服务器获取 Bob 的公钥包

### 4.2 Sealed Sender 发送流程

```
Alice                                Server                              Bob
  │                                     │                                  │
  │  1. 从 KeyRegistry 获取 Bob 的公钥   │                                  │
  │     (IK_B, SPK_B, PK_1)            │                                  │
  │                                     │                                  │
  │  2. 构建 Inner Envelope            │                                  │
  │     - 写入: Alice 的 Identity Key  │                                  │
  │     - 用 Bob 的 IK_B 加密           │                                  │
  │     - 用 Alice 的 ikA 签名          │                                  │
  │                                     │                                  │
  │  3. 构建消息内容                   │                                  │
  │     - X3DH 密钥协商                │                                  │
  │     - Double Ratchet 加密           │                                  │
  │                                     │                                  │
  │  4. 构建 Outer Envelope            │                                  │
  │     - 接收者: Bob                   │                                  │
  │     - 内容: Inner Envelope + Message│                                  │
  │                                     │                                  │
  │────────────── 密文消息 ────────────▶│                                  │
  │                                     │                                  │
  │                                服务器只看到:                           │
  │                                - 接收者: Bob                           │
  │                                - 密文内容（无法解密）                  │
  │                                     │                                  │
  │                                     │──────── 密文消息 ────────────────▶│
  │                                     │                                  │
  │                                     │                              5. Bob 收到消息
  │                                     │                              6. 解密 Outer Envelope
  │                                     │                              7. 解密 Inner Envelope（用 ikB）
  │                                     │                              8. 验证 Alice 签名（用 IK_A）
  │                                     │                              9. 解密消息内容
  │                                     │                              10. Bob 知道 Alice 发送了
```

### 4.3 关键步骤详解

**步骤 1-2: 获取公钥包**

```java
// Alice 从服务器获取 Bob 的 Sealed Sender 公钥包
PreKeyBundle bobBundle = server.getPreKeyBundle(bobUserId);
// bobBundle 包含: IK_B, SPK_B, PK_1, 签名
```

**步骤 3: 构建 Inner Envelope**

```java
// Alice 构造发送者身份信息
SenderCertificate certificate = new SenderCertificate();
certificate.setSenderUuid(aliceUuid);           // Alice 的 UUID
certificate.setSenderIdentityKey(IK_A);        // Alice 的 Identity Key
certificate.setExpirationTime(expireTime);     // 证书过期时间

// 用 Bob 的 Identity Key 加密发送者身份
SealedSenderMessage sealedMessage = sealedSenderEncrypt(
    IK_B,           // Bob 的 Identity Key 公钥
    ikA,            // Alice 的 Identity Key 私钥（用于签名）
    certificate     // 发送者证书
);
```

**步骤 5: 解密 Inner Envelope**

```java
// Bob 收到消息后解密
SealedSenderMessageContent content = sealedSenderDecrypt(
    sealedMessage,  // 密封消息
    ikB,            // Bob 的 Identity Key 私钥
    IK_A            // Alice 的 Identity Key 公钥（用于验证）
);

// 验证签名
if (!content.verifySignature(IK_A)) {
    throw new SecurityException("Invalid signature");
}

// Bob 现在知道 Alice 发送了消息
String senderUuid = content.getSenderUuid();
```

---

## 5. 服务器端架构

### 5.1 KeyRegistry 设计

服务器需要维护一个**密封发送者公钥注册表**：

```sql
CREATE TABLE sealed_sender_keys (
    user_id        VARCHAR(64) PRIMARY KEY,
    identity_key   BLOB NOT NULL,          -- IK_B 公钥
    signed_prekey  BLOB NOT NULL,          -- SPK_B 公钥
    signed_prekey_signature BLOB,           -- SPK_B 签名
    prekey_bundle  BLOB NOT NULL,           -- PreKey 批次公钥
    updated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 5.2 SealedSenderService 核心逻辑

```java
public class SealedSenderService {
    
    // 接收密封消息
    public void handleSealedMessage(SealedMessage message) {
        // 1. 验证消息格式
        validateMessageFormat(message);
        
        // 2. 查找接收者
        String recipientId = message.getRecipientId();
        User recipient = userStore.get(recipientId);
        
        // 3. 将消息加入接收者队列
        // 注意: 服务器无法解密内容，也不知道发送者是谁
        messageQueue.enqueue(recipientId, message);
        
        // 4. 如果接收者在线，通过 WebSocket 推送
        if (recipient.isOnline()) {
            websocketService.push(recipientId, message);
        }
    }
    
    // 获取密封发送者公钥包（用于建立密封会话）
    public PreKeyBundle getSealedSenderPublicKeys(String userId) {
        // 返回 IK_B, SPK_B, PreKey 批次
        // 任何人可以获取任何用户的公钥包
        return keyRegistry.getBundle(userId);
    }
}
```

### 5.3 服务器无法做的事

| 操作 | 普通 Signal | Sealed Sender |
|------|-----------|---------------|
| 读取消息内容 | ❌ | ❌ |
| 知道发送者身份 | ✅ | ❌ |
| 知道接收者身份 | ✅ | ✅ |
| 伪造消息 | ❌ | ❌ |
| 阻止消息送达 | ✅ | ✅ |
| 篡改消息 | ❌ | ❌ |

---

## 6. 客户端实现

### 6.1 发送端实现

```java
public class SealedSenderClient {
    
    private final SignalProtocolStore store;
    private final KeyRegistry keyRegistry;
    
    // 发送密封消息
    public SealedMessage sendSealedMessage(String recipientId, byte[] content) 
            throws UntrustedIdentityException {
        
        // 1. 获取接收者的密封发送者公钥包
        PreKeyBundle bundle = keyRegistry.getSealedSenderBundle(recipientId);
        
        // 2. 构建发送者证书
        IdentityKeyPair senderIdentity = store.getIdentityKeyPair();
        SenderCertificate certificate = new SenderCertificate.Builder()
            .setSenderUuid(store.getLocalUuid())
            .setSenderIdentityKey(senderIdentity.getPublicKey())
            .setExpirationTime(System.currentTimeMillis() + 7 * 24 * 3600 * 1000)
            .build(senderIdentity.getPrivateKey());  // 用发送者私钥签名
        
        // 3. 构建密封发送者消息
        // 将发送者身份用接收者的 Identity Key 加密
        SealedSenderEncryptResult encrypted = SealedSenderEncrypt.encrypt(
            bundle.getIdentityKey(),      // Bob 的 IK_B 公钥
            certificate,                  // Alice 的发送者证书
            content                       // 消息内容
        );
        
        // 4. 发送加密消息到服务器
        server.sendMessage(recipientId, encrypted.getSerializedMessage());
        
        return encrypted.getSerializedMessage();
    }
}
```

### 6.2 接收端实现

```java
public class SealedSenderReceiver {
    
    private final SignalProtocolStore store;
    
    // 接收并解密密封消息
    public DecryptedMessage receiveSealedMessage(byte[] sealedData) 
            throws SealedSenderException {
        
        // 1. 解密封发送者消息
        SealedSenderDecryptResult result = SealedSenderDecrypt.decrypt(
            sealedData,
            store.getIdentityKeyPair().getPrivateKey()  // Bob 的 ikB 私钥
        );
        
        // 2. 验证发送者签名
        IdentityKey senderIdentity = result.getSenderIdentityKey();
        if (!store.isTrustedIdentity(senderIdentity)) {
            // 新联系人，需要手动确认信任
            throw new UntrustedIdentityException(senderIdentity);
        }
        
        // 3. 获取发送者身份
        String senderUuid = result.getSenderUuid();
        
        // 4. 解密消息内容
        byte[] plaintext = result.getPlaintext();
        
        return new DecryptedMessage(senderUuid, plaintext);
    }
}
```

### 6.3 信任管理

```java
public class TrustManager {
    
    // 首次密封发送者消息，需要用户确认信任
    public void handleUntrustedSealedSender(IdentityKey identityKey) {
        // 通知用户: "有人向你发送了加密消息，是否信任？"
        // 显示发送者的安全码（Safety Numbers）
        showTrustPrompt(identityKey);
    }
    
    // 用户确认后，保存信任状态
    public void trustIdentity(IdentityKey identityKey) {
        store.saveIdentity(identityKey);
    }
}
```

---

## 7. 安全属性

### 7.1 隐私保护

- **发送者匿名**: 服务器不知道发送者身份
- **通信图隐藏**: 即使服务器被入侵，也无法构建通信关系图
- **元数据最小化**: 仅暴露接收者和时间

### 7.2 完整性保护

- **发送者身份不可伪造**: Ed25519 签名保护
- **消息内容不可篡改**: Double Ratchet AEAD 保护
- **重放攻击防护**: 序列号 + 时间戳机制

### 7.3 前向保密

- **密钥泄露影响有限**: 即使某次会话密钥泄露，历史消息仍安全
- **Identity Key 泄露**: 不影响已建立的会话（Double Ratchet 保护）

---

## 8. 限制与注意事项

### 8.1 功能限制

| 限制项 | 说明 |
|--------|------|
| 首次消息需要公钥包 | 发送密封消息前，需要获取接收者的公钥包 |
| 接收者需在线注册 | 接收者必须先将公钥注册到 KeyRegistry |
| 无法回复密封消息 | 如果没有双向会话建立，可能无法密封回复 |
| 离线消息处理 | 离线消息的 Sealed Sender 机制可能不同 |

### 8.2 安全考虑

- **信任首次接触**: 首次密封消息需要接收者确认信任发送者
- **证书过期**: 发送者证书有过期时间，防止长期追踪
- **Rate Limiting**: 需要防枚举攻击的限流机制
- **前向保密**: Identity Key 泄露会影响密封发送者匿名性

### 8.3 性能考虑

- **额外加密开销**: Inner Envelope 增加约 100-200 字节
- **签名验证**: 每条消息需要 Ed25519 签名验证
- **公钥获取**: 首次发送需要从服务器获取公钥包

---

## 9. IM 系统集成方案

### 9.1 架构设计

```
┌──────────────────────────────────────────────────────────────┐
│                      IM 系统架构                             │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌─────────────┐    ┌──────────────┐    ┌──────────────┐  │
│  │   客户端     │───▶│   API 网关    │───▶│  消息服务    │  │
│  │ (Sealed     │    │  (TLS 终止)   │    │  (Sealed     │  │
│  │  Sender)    │    │              │    │   Sender)    │  │
│  └─────────────┘    └──────────────┘    └──────────────┘  │
│                                                │            │
│                                                ▼            │
│                                        ┌──────────────┐    │
│                                        │  KeyRegistry  │    │
│                                        │   (Redis)     │    │
│                                        └──────────────┘    │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

### 9.2 数据库设计

```sql
-- 密封发送者公钥表
CREATE TABLE sealed_sender_keys (
    user_id              VARCHAR(64) PRIMARY KEY,
    identity_key         VARBINARY(32) NOT NULL,      -- Curve25519 公钥
    identity_key_id      BIGINT NOT NULL,
    signed_prekey        VARBINARY(32) NOT NULL,      -- X25519 公钥
    signed_prekey_id     BIGINT NOT NULL,
    signed_prekey_sig    VARBINARY(64) NOT NULL,      -- Ed25519 签名
    prekey_signature     VARBINARY(64) NOT NULL,      -- 整个包的签名
    created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- PreKey 批次表
CREATE TABLE prekeys (
    user_id              VARCHAR(64) NOT NULL,
    prekey_id            BIGINT NOT NULL,
    prekey               VARBINARY(32) NOT NULL,      -- X25519 公钥
    PRIMARY KEY (user_id, prekey_id)
);

-- 信任关系表
CREATE TABLE trusted_identities (
    user_id              VARCHAR(64) NOT NULL,
    recipient_id         VARCHAR(64) NOT NULL,
    identity_key         VARBINARY(32) NOT NULL,
    trusted_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, recipient_id)
);
```

### 9.3 API 设计

```java
// 获取密封发送者公钥包
GET /api/v1/sealed-sender/keys/{userId}
Response: {
    "identityKey": "base64-encoded-public-key",
    "identityKeyId": 12345,
    "signedPreKey": {
        "key": "base64-encoded-key",
        "id": 1,
        "signature": "base64-encoded-signature"
    },
    "preKey": {
        "key": "base64-encoded-key",
        "id": 100
    }
}

// 发送密封消息
POST /api/v1/sealed-sender/messages
Body: {
    "recipientId": "bob-uuid",
    "sealedMessage": "base64-encoded-sealed-message"
}
```

### 9.4 实现建议

1. **可选启用**: Sealed Sender 作为可选特性，用户可选择是否启用
2. **信任提示**: 首次收到密封消息时，显示信任确认提示
3. **证书轮换**: Identity Key 定期轮换，防止长期追踪
4. **缓存优化**: 公钥包缓存，减少服务器查询
5. **降级策略**: 如果公钥获取失败，自动降级到普通发送模式

---

## 10. 与其他隐私特性的关系

### 10.1 与 Signal Protocol 的关系

- **基础层**: X3DH 密钥协商 + Double Ratchet 消息加密
- **增强层**: Sealed Sender 在消息信封层面添加发送者匿名性
- **组合效果**: 内容加密（机密性）+ 发送者匿名（元数据保护）

### 10.2 与群组加密的关系

- **群组消息**: 使用 Sender Keys 方案（每个群组成员一把密钥）
- **Sealed Sender**: 可以与群组加密组合，隐藏群组中的具体发送者
- **区别**: 
  - Sealed Sender: 隐藏发送者身份（从服务器角度）
  - Sender Keys: 群组消息加密（群成员共享密钥）

### 10.3 与阅后即焚的关系

- **Sealed Sender**: 隐藏发送者身份
- **阅后即焚**: 消息内容在阅读后自动销毁
- **组合**: 高隐私场景下组合使用

---

## 11. 技术来源与参考

- Signal Protocol 官方文档（signal.org/docs）
- libsignal-protocol-java（GitHub: signalapp/libsignal-protocol-java）
- libsignal-protocol-c（GitHub: signalapp/libsignal-protocol-c）
- libsignal-service-java（GitHub: signalapp/libsignal-service-java）
- Signal-Server 架构参考（GitHub: signalapp/Signal-Server）
- Double Ratchet Algorithm（Signal Protocol 规范）
- X3DH Key Agreement Protocol（Signal Protocol 规范）

---

## 12. 下一步学习方向

1. **群组加密（Sender Keys 方案）**: 群组消息的端到端加密，与 Sealed Sender 的关系
2. **隐私评分与信任模型**: 如何在匿名性和可用性之间平衡
3. **数据库全文搜索（Elasticsearch）与消息语义检索**: 加密消息的搜索问题
4. **边缘计算与 IM 系统**: CDN 集成、边缘节点消息路由
5. **微信/Telegram/WhatsApp 技术架构深度分析**: 对比各平台的隐私保护方案
