# Webhook & Callback 系统设计 — 即时通讯系统扩展集成

## 1. 概念区分

在 IM 系统中，有两种不同的扩展集成机制常被混淆：

| 机制 | 定义 | 调用方向 | 典型用途 |
|------|------|----------|----------|
| **Webhook** | 服务端向外部第三方系统主动推送事件通知 | IM系统 → 外部系统 | 业务集成、内容审核、日志归档 |
| **Callback** | IM系统内部的事件钩子，在关键节点插入自定义逻辑 | IM系统内部 | 消息过滤、内容审核、计费统计 |

**核心区别：**
- **Webhook**：对外主动 HTTP 调用，通知外部系统
- **Callback**：对内同步/异步执行，可修改处理行为或只做监控

---

## 2. Webhook 系统设计

### 2.1 核心架构

```
┌──────────────────────────────────────────────────────────────┐
│                      IM 系统核心                              │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐ │
│  │ 消息服务  │  │ 用户服务  │  │ 群组服务  │  │ 推送服务  │ │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘ │
│       │              │              │              │        │
│       └──────────────┴──────────────┴──────────────┘        │
│                              │                                │
│                    ┌─────────▼─────────┐                    │
│                    │   Webhook 调度器   │                    │
│                    │  (WebhookDispatcher) │                  │
│                    └─────────┬─────────┘                    │
└──────────────────────────────┼───────────────────────────────┘
                               │
              ┌────────────────┼────────────────┐
              │                │                │
              ▼                ▼                ▼
         ┌─────────┐    ┌───────────┐   ┌────────────┐
         │ 业务系统 │    │ 内容审核  │   │ 日志归档   │
         │ Webhook │    │ Webhook   │   │ Webhook    │
         └─────────┘    └───────────┘   └────────────┘
```

### 2.2 事件类型定义

```json
// IM 系统 Webhook 事件类型
{
  "events": [
    // 消息事件
    { "type": "im.message.send", "desc": "消息发送前（Callback前）" },
    { "type": "im.message.send.after", "desc": "消息发送后（Webhook）" },
    { "type": "im.message.recall", "desc": "消息撤回" },
    { "type": "im.message.reaction", "desc": "消息表情反应" },
    
    // 用户事件
    { "type": "im.user.register", "desc": "用户注册" },
    { "type": "im.user.login", "desc": "用户登录" },
    { "type": "im.user.logout", "desc": "用户登出" },
    { "type": "im.user.offline", "desc": "用户离线" },
    { "type": "im.user.online", "desc": "用户上线" },
    
    // 好友事件
    { "type": "im.friend.request", "desc": "好友请求" },
    { "type": "im.friend.accept", "desc": "接受好友" },
    { "type": "im.friend.reject", "desc": "拒绝好友" },
    { "type": "im.friend.delete", "desc": "删除好友" },
    
    // 群组事件
    { "type": "im.group.create", "desc": "创建群组" },
    { "type": "im.group.member.join", "desc": "加入群组" },
    { "type": "im.group.member.leave", "desc": "离开群组" },
    { "type": "im.group.dismiss", "desc": "解散群组" },
    
    // 文件事件
    { "type": "im.file.upload", "desc": "文件上传" },
    { "type": "im.file.download", "desc": "文件下载" },
    
    // 推送事件
    { "type": "im.push.send", "desc": "推送发送" },
    { "type": "im.push.receive", "desc": "推送送达" }
  ]
}
```

### 2.3 Webhook 投递格式

```json
{
  "id": "wh_evt_abc123xyz",
  "event": "im.message.send.after",
  "timestamp": 1679932800000,
  "signature": "sha256=...",
  "data": {
    "msgId": "msg_uuid_12345",
    "fromUserId": 10001,
    "toUserId": 10002,
    "chatType": 1,
    "msgType": 1,
    "content": "Hello, world!",
    "createTime": 1679932800000
  }
}
```

### 2.4 安全机制

#### 2.4.1 签名验证

参考 Stripe/GitHub 最佳实践，使用 HMAC-SHA256 签名：

```
signature = HMAC-SHA256(secret_key, timestamp + "." + payload)
header: X-IM-Signature: t={timestamp},v1={signature}
```

```java
// Java 实现示例
public class WebhookSignatureVerifier {
    private static final String SECRET = "webhook_secret_key";
    
    public boolean verify(String payload, String timestamp, String signature) {
        // 1. 检查时间戳（防止重放攻击，5分钟内有效）
        long ts = Long.parseLong(timestamp);
        if (Math.abs(System.currentTimeMillis() - ts) > 5 * 60 * 1000) {
            return false;
        }
        
        // 2. 计算签名
        String expected = "t=" + timestamp + ",v1=" + 
            HMAC_SHA256(SECRET, timestamp + "." + payload);
        
        // 3. 时序安全比较
        return MessageDigest.isEqual(
            expected.getBytes(), 
            ("t=" + timestamp + ",v1=" + signature).getBytes()
        );
    }
}
```

#### 2.4.2 重放攻击防护

- **时间戳验证**：只接受 5 分钟内的请求
- **唯一事件ID**：使用 `X-IM-Delivery` 头，防止同一事件重复投递
- **幂等存储**：在数据库中记录已处理的 `event_id`，拒绝重复

### 2.5 可靠性设计

#### 2.5.1 异步投递 + 重试队列

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│  事件触发   │───▶│  投递队列   │───▶│  HTTP 调用  │
└─────────────┘    │  (Redis)    │    │  (外部系统) │
                   └─────────────┘    └──────┬──────┘
                          ▲                   │
                          │              ┌────▼────┐
                          │              │ 成功?   │
                   ┌──────┴──────┐       └────┬────┘
                   │  重试延迟队 │            │ 失败
                   │  列 (RocketMQ)│◀─────────┘
                   └─────────────┘
                   
重试策略：1s → 5s → 30s → 2min → 10min → 1h
最大重试次数：6 次
```

#### 2.5.2 投递超时与响应要求

参考 GitHub 最佳实践：
- **超时时间**：外部 Webhook 服务端必须在 **10秒** 内返回 2xx
- **快速响应**：在处理逻辑之前先返回 200，避免超时

```java
// Spring Boot Webhook 端点
@PostMapping("/webhook")
public ResponseEntity<?> handleWebhook(
    @RequestHeader("X-IM-Signature") String signature,
    @RequestHeader("X-IM-Timestamp") String timestamp,
    @RequestBody String payload
) {
    // 1. 快速验证签名
    if (!verifier.verify(payload, timestamp, signature)) {
        return ResponseEntity.status(401).body("Invalid signature");
    }
    
    // 2. 立即返回 200（GitHub 要求 10秒内）
    CompletableFuture.runAsync(() -> processWebhook(payload));
    
    return ResponseEntity.ok().build();
}
```

### 2.6 Webhook 配置管理

```yaml
# Webhook 配置文件示例
webhook:
  enabled: true
  endpoints:
    - name: business-system
      url: https://biz.example.com/webhook
      secret: ${WEBHOOK_SECRET_BIZ}
      events:
        - im.message.send.after
        - im.user.login
      timeout: 5000
      retry:
        maxAttempts: 6
        backoffMultiplier: 3
    - name: content-moderation
      url: https://mod.example.com/webhook
      secret: ${WEBHOOK_SECRET_MOD}
      events:
        - im.message.send.before  # Callback 类型，只做检测
      timeout: 3000
      retry:
        maxAttempts: 3
```

---

## 3. Internal Callback 机制设计

### 3.1 概念

Callback（内部回调钩子）与 Webhook 的核心区别在于：
- **Callback 可以在事件处理流程中修改行为**（同步阻塞）
- **Callback 可以拒绝或修改消息内容**
- 通常用于内容审核、计费、特殊业务逻辑注入

### 3.2 回调类型

| 类型 | 触发时机 | 能否修改行为 | 典型场景 |
|------|----------|-------------|----------|
| **Before Callback** | 事件处理前 | ✅ 可拒绝/修改 | 内容审核、敏感词过滤、频率限制 |
| **After Callback** | 事件处理后 | ❌ 只读 | 事件通知、计费、日志 |
| **Replace Callback** | 替代默认处理 | ✅ 完全替代 | 自定义消息处理逻辑 |

### 3.3 实现架构

```java
// 回调机制核心接口
public interface IMCallback {
    CallbackType type();           // BEFORE / AFTER / REPLACE
    String[] events();            // 监听的事件类型
    int order();                  // 执行顺序（数字越小越先执行）
    
    // Before 回调：返回 null 表示继续处理，返回 CallbackResult 拒绝/修改
    CallbackResult onBefore(CallbackContext ctx);
    
    // After 回调：仅通知，不影响主流程
    void onAfter(CallbackContext ctx);
}

// 消息发送前的回调示例（内容审核）
@Component
@CallbackEvent(type = CallbackType.BEFORE, event = "im.message.send")
public class MessageModerationCallback implements IMCallback {
    
    @Override
    public CallbackResult onBefore(CallbackContext ctx) {
        MessageContext msg = ctx.getData("message");
        
        // 1. 敏感词检测
        if (moderationService.containsSensitiveWords(msg.getContent())) {
            return CallbackResult.reject("消息包含敏感内容");
        }
        
        // 2. 频率限制检查
        if (rateLimitService.isRateLimited(msg.getFromUserId())) {
            return CallbackResult.reject("发送频率超限");
        }
        
        // 3. 检查通过，继续处理
        return CallbackResult.allow();
    }
}

// 消息发送后的回调示例（计费）
@Component
@CallbackEvent(type = CallbackType.AFTER, event = "im.message.send")
public class MessageBillingCallback implements IMCallback {
    
    @Override
    public void onAfter(CallbackContext ctx) {
        MessageContext msg = ctx.getData("message");
        billingService.recordMessage(msg);  // 异步计费，不阻塞
    }
}
```

### 3.4 回调调度器

```java
@Component
public class CallbackDispatcher {
    
    @Autowired
    private List<IMCallback> callbacks;
    
    // 触发 Before 回调（串行执行，遇到拒绝则中断）
    public CallbackResult dispatchBefore(String event, Object data) {
        CallbackContext ctx = new CallbackContext(event, data);
        CallbackResult result = CallbackResult.allow();
        
        for (IMCallback cb : getSortedCallbacks(event, CallbackType.BEFORE)) {
            try {
                CallbackResult r = cb.onBefore(ctx);
                if (r.isRejected()) {
                    return r;  // 遇到拒绝，立即返回
                }
            } catch (Exception e) {
                log.error("Callback {} failed: {}", cb.getClass().getSimpleName(), e);
            }
        }
        return result;
    }
    
    // 触发 After 回调（并行执行，不阻塞主流程）
    public void dispatchAfter(String event, Object data) {
        CallbackContext ctx = new CallbackContext(event, data);
        
        for (IMCallback cb : getSortedCallbacks(event, CallbackType.AFTER)) {
            CompletableFuture.runAsync(() -> {
                try {
                    cb.onAfter(ctx);
                } catch (Exception e) {
                    log.error("After callback {} failed", cb.getClass().getSimpleName(), e);
                }
            });
        }
    }
}
```

---

## 4. 实际应用场景

### 4.1 内容审核集成

**场景**：IM 系统对接第三方内容审核服务，在消息发送前进行检测。

```
用户发送消息 → Before Callback → 审核服务 → 通过/拒绝 → 消息发送/拒绝
```

```java
// 内容审核 Callback
@Component
public class ContentModerationCallback implements IMCallback {
    
    @Override
    public CallbackResult onBefore(CallbackContext ctx) {
        MessageContext msg = ctx.getData("message");
        
        // 调用审核服务（带超时）
        try {
            ModerationResult result = moderationService.check(
                msg.getContent(),
                msg.getMsgType()
            );
            
            if (!result.isPass()) {
                return CallbackResult.builder()
                    .allowed(false)
                    .reason(result.getReason())
                    .build();
            }
        } catch (Exception e) {
            // 审核服务异常时，降级为允许通过
            log.warn("Moderation service unavailable, allow message");
        }
        
        return CallbackResult.allow();
    }
}
```

### 4.2 业务系统集成

**场景**：IM 消息发送后，自动通知 ERP/CRM 系统。

```java
// 消息发送后的 Webhook 通知
@Component
public class MessageSendWebhook implements IMCallback {
    
    @Autowired
    private WebhookDispatcher webhookDispatcher;
    
    @Override
    public void onAfter(CallbackContext ctx) {
        MessageContext msg = ctx.getData("message");
        
        // 触发 Webhook
        webhookDispatcher.dispatch("im.message.send.after", Map.of(
            "msgId", msg.getMsgId(),
            "fromUserId", msg.getFromUserId(),
            "toUserId", msg.getToUserId(),
            "content", msg.getContent(),
            "timestamp", System.currentTimeMillis()
        ));
    }
}
```

### 4.3 消息归档

**场景**：所有消息自动归档到数据湖或日志系统（满足合规要求）。

```java
// 消息归档 Webhook
@Component
public class MessageArchiveWebhook {
    
    @Autowired
    private WebhookDispatcher dispatcher;
    
    @PostConstruct
    public void init() {
        dispatcher.register("im.message.send.after", this::archive);
    }
    
    private void archive(Map<String, Object> payload) {
        // 归档到对象存储
        archiveService.save(
            "messages/" + LocalDate.now() + "/" + payload.get("msgId") + ".json",
            JSON.toJSONString(payload)
        );
    }
}
```

### 4.4 计费与统计

**场景**：根据消息发送量进行计费，实时统计 DAU/MAU。

```java
// 计费 Callback
@Component
public class BillingCallback implements IMCallback {
    
    @Override
    public void onAfter(CallbackContext ctx) {
        MessageContext msg = ctx.getData("message");
        
        // 异步计费（不影响主流程）
        CompletableFuture.runAsync(() -> {
            billingService.incrementMessageCount(
                msg.getFromUserId(),
                msg.getChatType(),
                1
            );
        });
    }
}
```

---

## 5. 安全性最佳实践

### 5.1 Webhook 安全

1. **HTTPS 强制**：所有 Webhook 端点必须使用 HTTPS
2. **签名验证**：每次请求必须验证 HMAC 签名
3. **时间戳检查**：拒绝超过 5 分钟的请求（防止重放）
4. **幂等处理**：使用 event_id 防止重复处理
5. **IP 白名单**：可选地限制 Webhook 来源 IP
6. **敏感信息脱敏**：不在 URL 中传递敏感信息，只用 POST Body

### 5.2 Callback 安全

1. **超时控制**：每个 Callback 都有最大执行时间
2. **熔断保护**：Callback 失败时自动跳过，不影响主流程
3. **执行顺序**：通过 order 字段控制执行优先级
4. **权限隔离**：Callback 配置与主系统配置分离

---

## 6. 性能优化

### 6.1 投递性能

| 优化手段 | 说明 |
|----------|------|
| **HTTP 连接池** | 复用连接，减少 TLS 握手开销 |
| **异步投递** | Webhook 投递完全异步化 |
| **批量投递** | 多个事件可合并为一个 POST |
| **CDN 加速** | Webhook 接收端使用 CDN 优化接入 |
| **超时控制** | 设置合理超时（建议 5-10 秒） |

### 6.2 回调性能

| 优化手段 | 说明 |
|----------|------|
| **并行执行** | After Callback 并行执行 |
| **熔断降级** | 回调失败时自动跳过 |
| **超时熔断** | 单个回调超时自动中断 |
| **缓存结果** | 频繁调用的审核结果缓存 |

---

## 7. 监控与告警

### 7.1 关键指标

```yaml
# Webhook 监控指标
metrics:
  webhook:
    dispatch_total: "投递总数"
    dispatch_success: "投递成功数"
    dispatch_failed: "投递失败数"
    dispatch_timeout: "投递超时数"
    retry_total: "重试总数"
    avg_latency_ms: "平均延迟(ms)"
    
  callback:
    execution_total: "执行总数"
    execution_rejected: "执行拒绝数"
    execution_failed: "执行失败数"
    avg_execution_ms: "平均执行时间(ms)"
```

### 7.2 告警规则

```yaml
alerts:
  - name: webhook_delivery_failure_rate
    condition: failure_rate > 0.05  # 失败率 > 5%
    severity: warning
  - name: webhook_delivery_timeout
    condition: timeout_rate > 0.01  # 超时率 > 1%
    severity: critical
  - name: callback_execution_slow
    condition: avg_execution_ms > 1000  # 平均执行 > 1秒
    severity: warning
```

---

## 8. 实现建议

### 8.1 技术选型

| 组件 | 推荐方案 |
|------|----------|
| 投递队列 | RocketMQ / Kafka（用于重试队列） |
| 投递线程池 | Guava ThreadPoolTaskExecutor |
| 签名算法 | HMAC-SHA256 |
| 配置管理 | Nacos / Apollo（支持热更新） |
| 监控 | Prometheus + Grafana |

### 8.2 部署架构

```
┌─────────────────────────────────────────────────────────┐
│                    IM Server 集群                       │
│   ┌─────────────────────────────────────────────────┐  │
│   │  WebhookDispatcher + CallbackDispatcher          │  │
│   │  (每个 IM Server 实例内置)                        │  │
│   └─────────────────────────────────────────────────┘  │
└─────────────────────────┬───────────────────────────────┘
                          │
         ┌────────────────┼────────────────┐
         ▼                ▼                ▼
    ┌──────────┐   ┌───────────┐    ┌───────────┐
    │  Kafka   │   │  Kafka    │    │  Redis    │
    │ (投递队列)│   │ (重试队列) │    │ (幂等存储) │
    └──────────┘   └───────────┘    └───────────┘
```

### 8.3 关键文件

```
im-server/
├── config/
│   └── webhook.yml           # Webhook 配置
├── callback/
│   ├── Callback.java        # 回调接口
│   ├── CallbackContext.java # 上下文
│   ├── CallbackDispatcher.java
│   └── impl/
│       ├── ContentModerationCallback.java
│       ├── BillingCallback.java
│       └── ArchiveCallback.java
├── webhook/
│   ├── WebhookEvent.java    # 事件定义
│   ├── WebhookDispatcher.java
│   ├── WebhookSignatureVerifier.java
│   └── WebhookRetryHandler.java
└── service/
    └── WebhookDeliveryService.java  # 投递服务
```

---

## 9. 参考资料

- **Stripe Webhooks**: https://stripe.com/docs/webhooks
- **GitHub Webhooks Best Practices**: https://docs.github.com/en/webhooks/using-webhooks/best-practices-for-using-webhooks
- **Ably Webhooks**: https://ably.com/topic/webhooks
- **OpenIM Server** (开源 IM 参考实现): https://github.com/OpenIMSDK/Open-Im-Server

---

*文档版本: 1.0*
*创建时间: 2026-03-19*
*类型: 扩展集成机制*
