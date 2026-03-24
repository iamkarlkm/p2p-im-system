# 功能变更日志

## 2026-03-19

### ✅ 新增：基础文本消息收发
**模块**: im-backend (Java Spring Boot)

**实现内容**:
1. 创建了 `MessageController.java` - REST API 消息控制器，包含以下接口：
   - `POST /api/messages/send` - 发送消息（HTTP API，作为WebSocket的补充）
   - `GET /api/messages/chat/{friendId}` - 获取与某人的聊天记录（分页）
   - `GET /api/messages/group/{groupId}` - 获取群聊消息记录（分页）
   - `GET /api/messages/conversations` - 获取最近会话列表（包含最后一条消息）
   - `GET /api/messages/unread/count` - 获取未读消息总数
   - `PUT /api/messages/read/{senderId}` - 标记与某人的消息为已读
   - `DELETE /api/messages/{messageId}` - 撤回消息
   - `DELETE /api/messages/{messageId}/soft` - 软删除消息

2. 更新了 `WebSocketController.java` - 集成 MessageService 实现消息持久化：
   - 私聊消息发送到 `/app/chat/private/{toUser}` 时，消息自动持久化到数据库
   - 群聊消息发送到 `/app/chat/group/{groupId}` 时，消息自动持久化到数据库
   - 消息通过 WebSocket 实时推送给接收方
   - 支持消息类型解析（text/image/audio/video/file）

3. 更新了 `WebSocketService.java` - 增强会话管理：
   - 新增 `getUserIdByUsername()` 方法，支持用户名到用户ID的查询与缓存
   - 用户上线时自动缓存用户ID，避免频繁查询数据库
   - 用户下线时清理缓存
   - 在线用户列表增加 userId 字段

4. 更新了 `MessageService.java` - 完善消息服务：
   - 新增 `getRecentConversations()` 方法，获取最近会话列表
   - 集成 FriendRepository 获取好友列表
   - 消息按时间倒序排列

**技术特性**:
- REST API + WebSocket 双通道消息发送
- 消息持久化到 MySQL 数据库
- 实时 WebSocket 推送
- 支持多设备同时在线
- 消息状态管理（发送中/已发送/已读/撤回）
- 软删除机制

**相关文件**:
- `projects/im-backend/src/main/java/com/im/backend/controller/MessageController.java` (新增)
- `projects/im-backend/src/main/java/com/im/backend/controller/WebSocketController.java` (更新)
- `projects/im-backend/src/main/java/com/im/backend/service/WebSocketService.java` (更新)
- `projects/im-backend/src/main/java/com/im/backend/service/MessageService.java` (更新)

## 2026-03-19

### ✅ 新增：WebSocket长连接服务
**模块**: im-backend (Java Spring Boot)

**实现内容**:
1. 创建了 `WebSocketController.java` - STOMP消息控制器，包含以下功能：
   - `/app/online` - 用户上线通知
   - `/app/chat/private/{toUser}` - 私聊消息发送
   - `/app/chat/group/{groupId}` - 群聊消息发送
   - `/app/ping` - 心跳检测
   - `/app/online/list` - 获取在线用户列表

2. 创建了 `WebSocketService.java` - 会话管理服务：
   - 维护用户-会话映射（支持多设备登录）
   - 在线/离线状态管理
   - 在线用户列表查询
   - 线程安全的ConcurrentHashMap实现

3. 创建了 `WebSocketEventListener.java` - 事件监听器：
   - 监听SessionConnectEvent（连接建立）
   - 监听SessionDisconnectEvent（连接断开）
   - 监听SessionSubscribeEvent（订阅事件）

4. 创建了 `WsMessageDTO.java` - WebSocket消息格式：
   - 支持消息类型：chat, group_chat, online, offline, ping, pong
   - 包含from, to, content, msgId, timestamp等字段

**技术特性**:
- 使用STOMP协议（Spring WebSocket内置）
- 支持SockJS降级方案
- 心跳保活机制
- 多设备同时在线支持
- 实时消息推送

**相关文件**:
- `projects/im-backend/src/main/java/com/im/backend/controller/WebSocketController.java` (新增)
- `projects/im-backend/src/main/java/com/im/backend/controller/WebSocketEventListener.java` (新增)
- `projects/im-backend/src/main/java/com/im/backend/service/WebSocketService.java` (新增)
- `projects/im-backend/src/main/java/com/im/backend/dto/WsMessageDTO.java` (新增)

**WebSocket连接端点**:
- STOMP over WebSocket: `/ws` (with SockJS)
- STOMP direct: `/ws` (no SockJS)

**前端订阅示例**:
- 用户订阅: `/user/queue/msg` (接收私信)
- 群组订阅: `/topic/group/{groupId}`
- 广播订阅: `/topic/online` (上下线通知)

## 2026-03-18

### ✅ 新增：用户注册与登录API
**模块**: im-backend (Java Spring Boot)

**实现内容**:
1. 创建了 `controller` 目录，完善项目架构
2. 实现了 `AuthController.java`，包含以下API接口：
   - `POST /api/auth/register` - 用户注册
   - `POST /api/auth/login` - 用户登录
   - `GET /api/auth/me` - 获取当前用户信息
   - `POST /api/auth/refresh` - 刷新Token
   - `GET /api/auth/validate` - 验证Token有效性

**技术特性**:
- 使用JWT进行身份认证
- 支持Token刷新机制
- 统一的API响应格式 (ApiResponse<T>)
- 输入参数验证 (Jakarta Validation)
- 完整的错误处理

**相关文件**:
- `projects/im-backend/src/main/java/com/im/backend/controller/AuthController.java` (新增)

**依赖服务**:
- MySQL数据库 (jdbc:mysql://localhost:3306/im_db)
- Redis缓存 (localhost:6379)

---

### ✅ 完成：项目结构初始化
**模块**: im-backend (Java Spring Boot)

**项目架构**:
```
im-backend/
├── src/main/java/com/im/backend/
│   ├── ImBackendApplication.java          # 主启动类
│   ├── config/
│   │   ├── SecurityConfig.java             # Spring Security配置
│   │   └── WebSocketConfig.java            # WebSocket配置
│   ├── controller/                         # API控制器层 (新增)
│   │   └── AuthController.java             # 认证控制器
│   ├── dto/                                # 数据传输对象
│   │   ├── ApiResponse.java
│   │   ├── AuthResponse.java
│   │   ├── FriendDTO.java
│   │   ├── LoginRequest.java
│   │   ├── MessageDTO.java
│   │   ├── RegisterRequest.java
│   │   ├── SendMessageRequest.java
│   │   └── UserDTO.java
│   ├── model/                              # 实体类
│   │   ├── Friend.java
│   │   ├── Group.java
│   │   ├── GroupMember.java
│   │   ├── Message.java
│   │   └── User.java
│   ├── repository/                         # 数据仓库层
│   │   ├── FriendRepository.java
│   │   ├── GroupMemberRepository.java
│   │   ├── GroupRepository.java
│   │   ├── MessageRepository.java
│   │   └── UserRepository.java
│   ├── security/                           # 安全认证
│   │   └── JwtAuthFilter.java
│   └── service/                           # 业务服务层
│       ├── FriendService.java
│       ├── MessageService.java
│       └── UserService.java
├── src/main/resources/
│   └── application.yml                     # 应用配置
└── pom.xml                                 # Maven依赖配置
```

**技术栈**:
- Spring Boot 3.2.0
- Spring Security
- Spring Data JPA
- MySQL
- Redis
- JWT (jjwt 0.12.3)
- Lombok
- Jakarta Validation

**配置信息**:
- 服务端口: 8080
- 数据库: MySQL (localhost:3306/im_db)
- 缓存: Redis (localhost:6379)
- JWT密钥: im-secret-key-2026-very-long-secret-key-for-jwt-signing
- Token有效期: 24小时
- Refresh Token有效期: 7天

---

## 历史记录
（暂无更早的记录）
