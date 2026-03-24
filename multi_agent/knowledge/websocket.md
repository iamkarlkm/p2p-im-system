# WebSocket学习笔记

## 概述
WebSocket是一种在单个TCP连接上进行全双工通信的协议，是IM系统实现实时消息传输的核心技术。相比HTTP轮询，WebSocket具有更低延迟和更少资源消耗。

## WebSocket vs HTTP

### HTTP轮询
- 客户端定时发送请求
- 每次请求都需要建立连接
- 延迟高、资源消耗大

### WebSocket
- 建立持久连接
- 服务端可主动推送
- 低延迟、低资源消耗

### 对比
| 特性 | HTTP轮询 | WebSocket |
|-----|---------|----------|
| 连接 | 每次新建 | 持久连接 |
| 延迟 | 高 | 低 |
| 资源 | 高 | 低 |
| 推送 | 不支持 | 支持 |
| 双向 | 半双工 | 全双工 |

## WebSocket协议

### 握手过程
```
Client -> Server:
GET /ws HTTP/1.1
Upgrade: websocket
Connection: Upgrade
Sec-WebSocket-Key: dGhlIHNhbXBsZSBub25jZQ==

Server -> Client:
HTTP/1.1 101 Switching Protocols
Upgrade: websocket
Connection: Upgrade
Sec-WebSocket-Accept: s3pPLMBiTxaQ9kYGzzhZRbK+xOo=
```

### 帧结构
- FIN：消息结束标志
- Opcode：操作码(文本/二进制/关闭)
- Mask：掩码
- Payload：实际数据

## IM系统WebSocket架构

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   客户端    │────▶│  Gateway   │────▶│   消息服务  │
│  (WebSocket)│     │  (网关)    │     │   (业务)   │
└─────────────┘     └─────────────┘     └─────────────┘
                           │
                           ▼
                    ┌─────────────┐
                    │  Redis     │
                    │  (状态/路由) │
                    └─────────────┘
```

## 心跳机制

### 作用
- 检测连接是否存活
- 保持连接活跃
- 防止NAT超时

### 实现
```javascript
// 客户端心跳
setInterval(() => {
    ws.send(JSON.stringify({type: 'ping'}));
}, 30000);

// 服务端响应
ws.on('message', (msg) => {
    if (msg.type === 'ping') {
        ws.send(JSON.stringify({type: 'pong'}));
    }
});
```

## 断线重连

### 原因
- 网络不稳定
- 服务器重启
- 网络切换

### 策略
- 指数退避重连
- 本地消息队列
- 消息去重

## WebSocket安全

### 1. 鉴权
- Token验证
- 短期Token
- 定期刷新

### 2. 加密
- WSS(SSL/TLS)
- 消息加密

### 3. 限流
- 连接数限制
- 消息频率限制

## Netty WebSocket

### 服务端示例
```java
public class WebSocketServer {
    public static void main(String[] args) {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss, worker)
            .channel(NioServerSocketChannel.class)
            .childHandler(new WebSocketInitializer());
        
        bootstrap.bind(8080).sync();
    }
}
```

## 学习资源

### WebSocket vs HTTP:为什么IM系统选WebSocket?
- https://cloud.tencent.com/developer/article/2618728

### 深入WebSocket与IM即时通讯
- https://cloud.tencent.com/developer/article/2212011
