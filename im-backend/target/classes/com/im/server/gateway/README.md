# 限流和熔断功能 (Rate Limiting & Circuit Breaker)

## 功能描述
网关限流和熔断机制，保护系统稳定性。

## 实现方案

### 限流策略
1. **Token Bucket (令牌桶)** - 平滑限流，支持突发流量
2. **Sliding Window (滑动窗口)** - Redis实现，精确限流
3. **固定窗口** - 简单计数器

### 限流维度
- Per-User: 每个用户单独限流
- Per-IP: 每个IP限流 (防攻击)
- Global: 全局限流

### 限流配置
| 限流类型 | 阈值 | 窗口 |
|----------|------|------|
| WebSocket连接 | 1次/10秒 | 连接建立 |
| 消息发送 | 60次/分钟 | 用户级 |
| API调用 | 100次/分钟 | IP级 |
| 登录尝试 | 5次/分钟 | IP级 |

### 熔断策略
- **CLOSED**: 正常状态，请求通过，失败计数
- **OPEN**: 熔断状态，所有请求拒绝，快速失败
- **HALF_OPEN**: 半开状态，允许测试请求

### 熔断配置
| 参数 | 默认值 |
|------|--------|
| 失败阈值 | 50次 |
| 熔断时长 | 30秒 |
| 半开恢复请求数 | 3次 |
| 半开成功率阈值 | 50% |

## 文件清单

### im-backend
- `com.im.server.gateway.RateLimitService` - 限流服务
- `com.im.server.gateway.CircuitBreaker` - 熔断器
- `com.im.server.gateway.CircuitBreakerRegistry` - 熔断器注册表
- `com.im.server.gateway.RateLimitFilter` - Netty限流过滤器
- `com.im.server.gateway.RateLimitConfig` - 限流配置

## 实现时间
- 开始: 2026-03-18 16:32
- 完成: 2026-03-18 16:50
