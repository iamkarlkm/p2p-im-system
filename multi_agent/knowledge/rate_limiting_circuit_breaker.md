# 限流与熔断降级 - Sentinel 在 IM 系统中的应用

## 概述

**Sentinel** 是阿里巴巴开源的面向分布式、多语言异构化服务架构的流量治理组件，以流量为切入点，从流量路由、流量控制、流量整形、熔断降级、系统自适应过载保护、热点流量防护等多个维度保障微服务稳定性。

**Sentinel 发展历程：**
- 2012 年：诞生，主要功能为入口流量控制
- 2013-2017 年：阿里巴巴集团内部广泛使用，覆盖所有核心场景
- 2018 年：开源并持续演进
- 2020 年：推出 Sentinel Go 版本
- 2022 年：品牌升级为流量治理，与 OpenSergo 标准整合

---

## 一、核心概念

### 1.1 资源（Resource）

资源是 Sentinel 的核心概念，可以是：
- 应用程序提供的服务
- 应用程序调用的其他服务
- 一段代码，甚至 URL 或服务名称

**在 IM 系统中的资源示例：**
- `/api/message/send` — 消息发送接口
- `/api/user/login` — 用户登录接口
- `ws://gateway/chat` — WebSocket 连接
- `messageConsumer` — 消息消费者

### 1.2 规则（Rule）

围绕资源的实时状态设定的规则，包括：
- **流量控制规则**（FlowRule）
- **熔断降级规则**（DegradeRule）
- **系统保护规则**（SystemRule）
- **热点规则**（ParamFlowRule）

所有规则支持动态实时调整。

---

## 二、流量控制（Flow Control）

### 2.1 限流维度

Sentinel 支持从多个角度进行流量控制：

| 维度 | 说明 |
|------|------|
| **QPS** | 每秒请求数限流 |
| **并发线程数** | 限制同时处理的线程数量 |
| **调用关系** | 根据调用方、调用链路进行控制 |
| **运行指标** | 基于系统负载、RT 等指标 |

### 2.2 限流类型

#### 基于 QPS/并发数的流量控制

**并发线程数限流**：
- 用于保护业务线程数不被耗尽
- 统计当前请求上下文的线程个数
- 超出阈值时新请求被立即拒绝
- 适用于：下游服务不稳定、响应延迟增加的场景

**QPS 限流**：
- 当 QPS 超过阈值时采取措施
- 三种控制效果（controlBehavior）：

| 控制效果 | 说明 | 适用场景 |
|----------|------|----------|
| **直接拒绝** (default) | 超出阈值的新请求立即拒绝 | 系统处理能力已知（压测确定水位） |
| **冷启动** (warm_up) | 流量缓慢增加至阈值上限 | 长期低水位系统突然增加流量 |
| **匀速器** (rate_limiter) | 请求以均匀速度通过（漏桶算法） | 间隔性突发流量（如消息队列） |

### 2.3 基于调用关系的流量控制

#### 根据调用方限流（origin 限流）
- 通过 `ContextUtil.enter(resourceName, origin)` 标记调用方
- `limitApp` 字段支持三种模式：
  - `default`：不区分调用者，全部限流
  - `{origin_name}`：针对特定调用方限流
  - `other`：针对除特定调用方以外的所有请求

#### 链路限流（Chain 策略）
- 只根据某个入口的统计信息对资源限流
- 通过 `ContextUtil.enter(name)` 定义入口
- 适用场景：同一资源被多个入口调用，但需要区分控制

#### 关联流量控制（Relate 策略）
- 当两个资源存在争抢或依赖关系时使用
- 例如：读库限流关联写库，写操作频繁时读请求被限流
- 优先级：写 > 读

### 2.4 IM 系统流量控制规则设计

```java
// 消息发送接口限流 — QPS 限流，直接拒绝
FlowRule sendMessageRule = new FlowRule("messageSend")
    .setGrade(RuleConstant.FLOW_GRADE_QPS)
    .setCount(1000)  // 每秒最多1000次
    .setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT)
    .setResourceType("IM_API");

// WebSocket 连接限流 — 并发线程数限流
FlowRule wsConnectRule = new FlowRule("wsConnect")
    .setGrade(RuleConstant.FLOW_GRADE_THREAD)
    .setCount(10000)  // 最多10000个并发连接
    .setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT);

// 批量导入接口 — 冷启动限流
FlowRule importRule = new FlowRule("userImport")
    .setGrade(RuleConstant.FLOW_GRADE_QPS)
    .setCount(10)
    .setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_WARM_UP)
    .setWarmUpPeriodSec(30);  // 30秒预热期

// 消息推送接口 — 匀速器限流
FlowRule pushRule = new FlowRule("pushNotify")
    .setGrade(RuleConstant.FLOW_GRADE_QPS)
    .setCount(500)
    .setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_RATE_LIMITER)
    .setMaxQueueingTimeMs(500);  // 最多排队500ms
```

---

## 三、熔断降级（Circuit Breaking）

### 3.1 什么是熔断降级

**背景问题**：微服务架构中，服务 A 调用服务 B。如果服务 B 不稳定（响应时间变长），会导致：
1. 服务 A 调用 B 的线程堆积
2. 线程池耗尽
3. 服务 A 也不可用
4. 层层级联，最终整个链路崩溃（雪崩效应）

**Sentinel 方案**：对不稳定的弱依赖服务调用进行熔断降级，暂时切断不稳定调用，避免雪崩。

### 3.2 Sentinel vs Hystrix

| 对比项 | Hystrix | Sentinel |
|--------|---------|----------|
| 隔离方式 | 线程池隔离 | 并发线程数限制 + 响应时间降级 |
| 线程开销 | 有线程切换成本 | 无需预先分配线程池 |
| 灵活性 | 需要预设线程池大小 | 动态统计，按需限制 |
| 控制粒度 | 粗粒度（线程池级别） | 细粒度（资源级别） |

### 3.3 熔断器状态机

Sentinel 熔断降级基于 **Circuit Breaker Pattern**，内部维护三种状态：

```
                    ┌──────────────────────────────────┐
                    │                                  │
                    ▼                                  │
┌──────────┐    ┌────────┐    ┌──────────────┐        │
│  CLOSED  │───▶│ OPEN   │───▶│ HALF-OPEN    │────────┘
│  (闭合)   │    │ (熔断) │    │   (半开)     │
└──────────┘    └────────┘    └──────────────┘
    ▲              │                │
    │              │                │
    │         熔断时长后          探测请求
    │         自动转换            成功/失败
    │              │                │
    └──────────────┴────────────────┘
```

**三种状态详解：**

| 状态 | 说明 | 行为 |
|------|------|------|
| **CLOSED** | 初始状态，熔断器闭合 | 正常放行所有请求 |
| **OPEN** | 熔断开启，所有请求拒绝 | 直接拒绝，等待熔断超时 |
| **HALF-OPEN** | 半开状态，允许探测流量 | 放行一个探测请求，成功则恢复 CLOSED，失败则回 OPEN |

**状态转换规则：**
1. **CLOSED → OPEN**：统计指标触发阈值（慢调用比例/异常比例/异常数）
2. **OPEN → HALF-OPEN**：经过熔断时长（RetryTimeout）后
3. **HALF-OPEN → CLOSED**：探测请求成功完成
4. **HALF-OPEN → OPEN**：探测请求失败

### 3.4 三种熔断策略

Sentinel 支持三种熔断策略（适用于 Java 1.8+，Go 版本同样支持）：

#### 策略一：慢调用比例（SLOW_REQUEST_RATIO）

**触发条件**：
- 单位统计时长内请求数 > 最小请求数（minRequestAmount）
- 慢调用比例 > 阈值（slowRatioThreshold）
- 慢调用 = 响应时间 > 设定 RT 阈值（count）

**熔断行为**：接下来的熔断时长内所有请求被拒绝

**探测恢复**：熔断结束后进入 HALF-OPEN，放行一个请求：
- 响应时间 < 慢调用 RT → 结束熔断，恢复 CLOSED
- 响应时间 >= 慢调用 RT → 再次熔断

#### 策略二：异常比例（ERROR_RATIO）

**触发条件**：
- 单位统计时长内请求数 > 最小请求数（minRequestAmount）
- 异常比例 > 阈值（count，范围 0.0 ~ 1.0）

**熔断行为**：接下来的熔断时长内所有请求被拒绝

**探测恢复**：熔断结束后进入 HALF-OPEN，放行一个请求：
- 请求成功（无错误）→ 结束熔断
- 请求失败 → 再次熔断

#### 策略三：异常数（ERROR_COUNT）

**触发条件**：
- 单位统计时长内异常数目 > 阈值（count）

**熔断行为**：接下来的熔断时长内所有请求被拒绝

**探测恢复**：与异常比例相同

### 3.5 熔断降级规则详解

```java
// DegradeRule 核心属性
public class DegradeRule {
    private String resource;              // 资源名
    private int grade;                     // 熔断策略（0:慢调用比例 1:异常比例 2:异常数）
    private double count;                  // 阈值
    private int timeWindow;               // 熔断时长（秒）
    private int minRequestAmount;         // 最小请求数（静默期，1.7.0引入）
    private int statIntervalMs;           // 统计时长（ms，1.8.0引入）
    private double slowRatioThreshold;    // 慢调用比例阈值（1.8.0引入）
}
```

**静默期（MinRequestAmount）**：
- 一个统计周期内，请求数小于该值时，即使指标异常也不会触发熔断
- 避免统计周期刚开始时的偶发慢请求导致误判
- **默认值**：5（建议根据实际流量设置）

### 3.6 熔断器事件监听

Sentinel 支持注册状态变更监听器：

```java
// Java 版本
EventObserverRegistry.getInstance().addStateChangeObserver("logging",
    (prevState, newState, rule, snapshotValue) -> {
        if (newState == State.OPEN) {
            System.err.println(String.format(
                "%s -> OPEN at %d, snapshotValue=%.2f",
                prevState.name(), TimeUtil.currentTimeMillis(), snapshotValue));
            // 告警通知：发送邮件/钉钉/短信
        } else {
            System.err.println(String.format("%s -> %s at %d",
                prevState.name(), newState.name(), TimeUtil.currentTimeMillis()));
        }
    });

// Go 版本
type StateChangeListener interface {
    OnTransformToClosed(prev State, rule Rule)
    OnTransformToOpen(prev State, rule Rule, snapshot interface{})
    OnTransformToHalfOpen(prev State, rule Rule)
}
```

### 3.7 IM 系统熔断降级规则设计

```java
// 场景1：下游消息存储服务不稳定 — 慢调用比例熔断
// 当 MongoDB 响应时间 > 500ms 的比例超过 30% 时，熔断 30 秒
DegradeRule mongoRule = new DegradeRule("mongoMessageStore")
    .setGrade(RuleConstant.DEGRADE_GRADE_RT)
    .setCount(500)         // 慢调用 RT 阈值 500ms
    .setTimeWindow(30)    // 熔断 30 秒
    .setMinRequestAmount(10)  // 最小 10 个请求
    .setSlowRatioThreshold(0.3);  // 30% 慢调用比例

// 场景2：第三方推送服务异常 — 异常比例熔断
// 当个推 API 异常比例 > 10% 时，熔断 60 秒
DegradeRule pushRule = new DegradeRule("getuiPush")
    .setGrade(RuleConstant.DEGRADE_GRADE_EXCEPTION_RATIO)
    .setCount(0.1)         // 10% 异常比例
    .setTimeWindow(60)    // 熔断 60 秒
    .setMinRequestAmount(5);

// 场景3：文件上传服务异常 — 异常数熔断
// 当 OSS 上传失败次数 > 100 次/分钟 时，熔断 5 分钟
DegradeRule ossRule = new DegradeRule("ossUpload")
    .setGrade(RuleConstant.DEGRADE_GRADE_EXCEPTION_COUNT)
    .setCount(100)        // 100 次异常
    .setTimeWindow(300)   // 熔断 5 分钟
    .setStatIntervalMs(60000)  // 1 分钟统计窗口
    .setMinRequestAmount(20);
```

---

## 四、系统自适应保护

Sentinel 提供系统维度的自适应保护能力，防止系统过载崩溃。

### 4.1 系统保护规则

| 规则 | 说明 | 触发条件 |
|------|------|----------|
| **Load（系统负载）** | 当系统负载超过阈值时拒绝请求 | Linux 系统负载（Load1） |
| **RT（平均响应时间）** | 当所有请求的平均 RT 超过阈值时拒绝 | 全局平均 RT |
| **线程数** | 当所有入口的并发线程数超过阈值时拒绝 | 并发线程总数 |
| **入口 QPS** | 当所有入口的 QPS 超过阈值时拒绝 | 全局入口 QPS |
| **CPU 使用率** | 当 CPU 使用率超过阈值时拒绝 | 系统 CPU 使用率 |
| **最大 RT** | 当最大响应时间超过阈值时拒绝 | 单次最大 RT |

### 4.2 IM 系统系统保护建议

```java
// 系统保护规则
// 当系统 Load > 10 时，开启系统保护
SystemRule systemRule = new SystemRule()
    .setHighestSystemLoad(10.0)
    .setHighestCpuUsage(0.8)    // CPU > 80% 时保护
    .setAvgRt(100)              // 平均 RT > 100ms 时保护
    .setMaxThread(200);        // 并发线程 > 200 时保护
```

---

## 五、IM 系统限流熔断完整设计方案

### 5.1 分层限流架构

```
┌─────────────────────────────────────────────────────────────┐
│                        客户端层                              │
│  本地限流（防止重复点击）+ 智能重试（指数退避）                  │
└─────────────────────────┬───────────────────────────────────┘
                          ▼
┌─────────────────────────────────────────────────────────────┐
│                       网关层 (Gateway)                       │
│  - IP 限流：每 IP 每秒最多 N 个请求                            │
│  - Token 限流：每个用户每秒最多 M 个请求                       │
│  - 端口限流：网关总 QPS 上限                                  │
│  - WebSocket 连接数限制                                      │
└─────────────────────────┬───────────────────────────────────┘
                          ▼
┌─────────────────────────────────────────────────────────────┐
│                      业务服务层 (Service)                     │
│  - 接口级限流：按 API 路径限流                               │
│  - 用户级限流：按 User ID 限流                               │
│  - 群组级限流：群消息广播限流                                 │
│  - 熔断降级：下游依赖服务不稳定时自动熔断                       │
└─────────────────────────┬───────────────────────────────────┘
                          ▼
┌─────────────────────────────────────────────────────────────┐
│                      存储层 (Storage)                         │
│  - MySQL 连接池限流                                          │
│  - Redis 连接数限制                                          │
│  - MongoDB 并发限制                                          │
│  - Kafka 生产者限流                                          │
└─────────────────────────────────────────────────────────────┘
```

### 5.2 核心限流规则清单

| 层级 | 资源名 | 限流类型 | 阈值 | 控制效果 |
|------|--------|----------|------|----------|
| 网关 | `ip:global` | QPS | 1000/IP | 直接拒绝 |
| 网关 | `user:global` | QPS | 100/用户 | 直接拒绝 |
| 网关 | `ws:connect` | 线程数 | 50000 | 直接拒绝 |
| 业务 | `message:send` | QPS | 2000 | 直接拒绝 |
| 业务 | `message:batch` | QPS | 100 | 冷启动(30s) |
| 业务 | `friend:add` | QPS | 20/用户 | 直接拒绝 |
| 业务 | `group:create` | QPS | 10/用户 | 直接拒绝 |
| 业务 | `file:upload` | QPS | 50/用户 | 匀速器 |
| 业务 | `push:send` | QPS | 500 | 匀速器 |

### 5.3 核心熔断规则清单

| 服务 | 资源名 | 策略 | 阈值 | 熔断时长 |
|------|--------|------|------|----------|
| 消息存储 | `mysql:write` | 慢调用比例 | RT>200ms, 比例>30% | 30s |
| 消息存储 | `mysql:write` | 异常比例 | >5% | 60s |
| 缓存服务 | `redis:pub` | 异常数 | >50次/分钟 | 30s |
| 文件服务 | `minio:upload` | 异常比例 | >10% | 60s |
| 推送服务 | `getui:push` | 异常比例 | >15% | 120s |
| 搜索服务 | `elasticsearch:search` | 慢调用比例 | RT>500ms, 比例>50% | 30s |

### 5.4 熔断后的降级处理

```java
// 消息存储熔断降级处理
@SentinelResource(value = "messageStore", blockHandler = "messageStoreBlockHandler")
public Message storeMessage(Message msg) {
    // 正常逻辑
    return messageRepository.save(msg);
}

// 降级处理：当熔断或限流触发时执行
public Message messageStoreBlockHandler(Message msg, BlockException e) {
    if (e instanceof DegradeException) {
        // 熔断降级：写入本地队列，稍后重试
        localMessageQueue.offer(msg);
        log.warn("Message store degraded, queued locally: {}", msg.getId());
        return msg;  // 返回乐观响应
    }
    if (e instanceof FlowException) {
        // 限流：拒绝请求，返回错误码
        throw new RateLimitException("Message send rate limit exceeded");
    }
    return msg;
}
```

### 5.5 限流响应格式

IM 客户端需要识别限流响应并做相应处理：

```json
// HTTP 429 Too Many Requests
{
  "code": 42901,
  "message": "请求过于频繁，请稍后重试",
  "data": null,
  "retryAfter": 5  // 建议等待秒数
}

// WebSocket 限流消息
{
  "type": "RATE_LIMIT",
  "code": 42901,
  "message": "发送频率过高",
  "resource": "message:send",
  "retryAfter": 3
}
```

### 5.6 Sentinel 控制台集成

Sentinel 提供 Dashboard 用于可视化规则配置和实时监控：

```
┌──────────────────────────────────────────────┐
│ Sentinel Dashboard                           │
├──────────────────────────────────────────────┤
│ 实时监控：                                   │
│  - 每台机器的入口流量                         │
│  - resource 的 QPS、响应时间、通过率           │
│  - 限流和熔断触发次数                         │
│                                              │
│ 规则配置：                                   │
│  - 动态下发限流规则                           │
│  - 动态下发熔断规则                           │
│  - 规则版本管理                              │
│                                              │
│ 机器监控：                                   │
│  - JVM 线程数、GC、CPU                       │
│  - 系统 Load、RT                             │
└──────────────────────────────────────────────┘
```

---

## 六、与 Spring Cloud/Spring Boot 集成

### 6.1 Maven 依赖

```xml
<dependency>
    <groupId>com.alibaba.csp</groupId>
    <artifactId>sentinel-core</artifactId>
    <version>1.8.6</version>
</dependency>
<dependency>
    <groupId>com.alibaba.csp</groupId>
    <artifactId>sentinel-transport-simple-http</artifactId>
    <version>1.8.6</version>
</dependency>
<dependency>
    <groupId>com.alibaba.csp</groupId>
    <artifactId>sentinel-datasource-nacos</artifactId>
    <version>1.8.6</version>
</dependency>
<dependency>
    <groupId>com.alibaba.csp</groupId>
    <artifactId>sentinel-spring-webmvc-adapter</artifactId>
    <version>1.8.6</version>
</dependency>
<dependency>
    <groupId>com.alibaba.csp</groupId>
    <artifactId>sentinel-web-servlet</artifactId>
    <version>1.8.6</version>
</dependency>
```

### 6.2 Spring Boot 配置

```yaml
# application.yml
spring:
  cloud:
    sentinel:
      enabled: true
      eager: true  # 启动时即连接 Sentinel
      transport:
        port: 8719  # Agent 端口
        dashboard: localhost:8080  # Dashboard 地址
      datasource:
        nacos:
          server-addr: localhost:8848
          data-id: sentinel-rules
          group-id: IM_SYSTEM
          rule-type: flow
```

### 6.3 注解方式使用

```java
@RestController
@RequestMapping("/api/message")
public class MessageController {

    @SentinelResource(value = "message:send",
        blockHandler = "sendBlockHandler",
        fallback = "sendFallback")
    @PostMapping("/send")
    public Response sendMessage(@RequestBody MessageDTO dto) {
        return messageService.send(dto);
    }

    // 限流/熔断降级处理
    public Response sendBlockHandler(MessageDTO dto, BlockException e) {
        return Response.error(429, "系统繁忙，请稍后重试");
    }

    // 业务异常降级
    public Response sendFallback(MessageDTO dto, Throwable t) {
        log.error("Send message failed", t);
        return Response.error(500, "发送失败");
    }

    // 使用 fallback2 进行参数绑定的异常处理
    public Response sendFallback2(long msgId, BlockException e) {
        return Response.error(429, "请求过于频繁");
    }
}
```

### 6.4 Nacos 配置中心规则推送

```java
@Configuration
public class SentinelRuleConfig {

    @Autowired
    private SentinelProperties properties;

    @Bean
    public InitFunc sentinelRuleInitFunc() {
        return () -> {
            // 从 Nacos 读取限流规则
            ReadableDataSource<String, List<FlowRule>> flowRuleDataSource =
                new NacosDataSource<>(
                    properties.getTransport().getDashboard(),
                    "IM_FLOW_RULES",
                    source -> JSON.parseArray(source, FlowRule.class)
                );
            FlowRuleManager.register2Property(flowRuleDataSource.getProperty());

            // 从 Nacos 读取熔断规则
            ReadableDataSource<String, List<DegradeRule>> degradeRuleDataSource =
                new NacosDataSource<>(
                    properties.getTransport().getDashboard(),
                    "IM_DEGRADE_RULES",
                    source -> JSON.parseArray(source, DegradeRule.class)
                );
            DegradeRuleManager.register2Property(degradeRuleDataSource.getProperty());
        };
    }
}
```

---

## 七、与 Netty WebSocket 集成

### 7.1 Netty Pipeline 集成 Sentinel

```java
public class SentinelChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // 添加 WebSocket 编解码器
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
        pipeline.addLast(new WebSocketFrameAggregator(65536));

        // 添加 Sentinel 限流处理器
        pipeline.addLast("sentinelFlowHandler", new SentinelFlowHandler());
        pipeline.addLast("sentinelDegradeHandler", new SentinelDegradeHandler());

        // 添加业务处理器
        pipeline.addLast("chatHandler", new ChatMessageHandler());
    }
}
```

### 7.2 WebSocket 消息限流

```java
public class SentinelFlowHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof TextWebSocketFrame frame) {
            ChatMessage chatMsg = JSON.parseObject(frame.text(), ChatMessage.class);

            String resourceName = "ws:message:" + chatMsg.getType();
            Entry entry = null;

            try {
                entry = SphU.entry(resourceName);
                // 通过限流检查，正常处理消息
                ctx.fireChannelRead(msg);
            } catch (BlockException e) {
                // 被限流，返回错误消息
                ctx.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(
                    ImmutableMap.of(
                        "type", "RATE_LIMIT",
                        "code", 42901,
                        "message", "发送频率过高",
                        "retryAfter", 3
                    )
                )));
                log.warn("WebSocket message rate limited: {}", resourceName);
            } finally {
                if (entry != null) {
                    entry.exit();
                }
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }
}
```

---

## 八、监控与告警

### 8.1 关键监控指标

| 指标 | 说明 | 告警阈值 |
|------|------|----------|
| `sentinel.flow.pass` | 通过请求数 | - |
| `sentinel.flow.block` | 限流拒绝数 | 突增 100% |
| `sentinel.degrade.triggered` | 熔断触发次数 | > 0 |
| `sentinel.system.load` | 系统 Load | > 10 |
| `sentinel.system.rt` | 平均响应时间 | > 500ms |
| `sentinel.blocked.qps` | 被阻止的 QPS | > 1000 |

### 8.2 与 Prometheus 集成

```yaml
# prometheus.yml
scrape_configs:
  - job_name: 'sentinel'
    metrics_path: '/cnode'
    static_configs:
      - targets: ['localhost:8719']
```

### 8.3 告警通知

```java
@Component
public class SentinelAlarmNotifier {

    @Async
    public void onStateChange(State oldState, State newState, DegradeRule rule, double value) {
        if (newState == State.OPEN) {
            // 发送告警
            notifyOps(
                "🔥 熔断触发",
                String.format("资源 [%s] 触发熔断，策略: %s，触发值: %.2f",
                    rule.getResource(), rule.getGrade(), value)
            );
        }
    }

    @Async
    public void onFlowBlock(String resource, String origin, int count) {
        if (count > 100) {  // 限流次数过多
            notifyOps(
                "⚠️ 限流告警",
                String.format("资源 [%s] 被 [%s] 限流，%d 次/分钟",
                    resource, origin, count)
            );
        }
    }
}
```

---

## 九、最佳实践总结

### 9.1 限流策略选择

| 场景 | 推荐策略 | 说明 |
|------|----------|------|
| API 接口限流 | 直接拒绝 | 明确知道系统处理能力 |
| 批量操作 | 冷启动 | 避免突发流量压垮系统 |
| 消息推送 | 匀速器 | 保护下游消费者平滑处理 |
| WebSocket 连接 | 线程数限流 | 保护网关连接数上限 |

### 9.2 熔断策略选择

| 场景 | 推荐策略 | 说明 |
|------|----------|------|
| 数据库访问 | 慢调用比例 | 慢查询 > 200ms 时熔断 |
| 第三方 API | 异常比例/异常数 | 接口不稳定时熔断 |
| 内部服务调用 | 异常数 | 错误次数超阈值熔断 |
| 消息队列 | 异常比例 | 消费失败率升高时熔断 |

### 9.3 IM 系统关键配置建议

```java
// 推荐配置值（参考）
public class SentinelConfig {

    // 限流配置
    public static final int MESSAGE_SEND_QPS = 1000;      // 消息发送: 1000/s
    public static final int USER_GLOBAL_QPS = 100;         // 用户全局: 100/s
    public static final int IP_GLOBAL_QPS = 500;          // IP全局: 500/s
    public static final int WS_CONNECT_THREAD = 50000;    // WebSocket并发: 5万

    // 熔断配置
    public static final int DB_SLOW_RT = 200;             // 数据库慢调用阈值: 200ms
    public static final double DB_SLOW_RATIO = 0.3;       // 30% 慢调用比例
    public static final int BREAK_TIME = 30;              // 熔断时长: 30s
    public static final int MIN_REQUEST = 10;              // 最小请求数: 10
}
```

### 9.4 避免的常见错误

1. **阈值设置过高**：失去限流保护意义
2. **阈值设置过低**：正常流量被误杀
3. **熔断时长过短**：依赖服务未恢复又熔断
4. **熔断时长过长**：长时间不可用影响用户体验
5. **缺少静默期**：偶发慢请求触发误熔断
6. **未记录业务异常**：异常比例/异常数熔断不生效

---

## 十、学习资源

### 官方文档
- Sentinel 官网：https://sentinelguard.io/zh-cn/
- Sentinel GitHub：https://github.com/alibaba/Sentinel
- Sentinel Go：https://github.com/alibaba/sentinel-golang
- OpenSergo 标准：https://opensergo.io/zh-cn/

### 进阶主题（下一轮学习方向）
1. Sentinel Dashboard 高可用部署
2. Sentinel 规则动态配置（Nacos/ZooKeeper/Apollo）
3. Sentinel 与 Spring Cloud Gateway 集成
4. 多语言限流（Sentinel Go/C++/Rust）
5. 自适应限流与过载保护
6. Sentinel 2.0 云原生高可用决策中心

---

*本文档创建时间：2026-03-19*
*基于 Sentinel 1.8+ 官方文档整理*
