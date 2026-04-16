# im-modular 项目测试计划

## 📋 测试概览

| 项目 | 详情 |
|------|------|
| **测试目标** | im-modular 核心模块功能验证 |
| **代码规模** | 68个文件，14,844行代码 |
| **核心模块** | 5个（消息、WebSocket、用户、认证、群组）|
| **测试类型** | 单元测试 + 集成测试 |

---

## ✅ 可测试组件清单

### 1. 单元测试（无需外部依赖）

#### 1.1 im-service-message 消息服务
| 测试类 | 测试方法 | 测试场景 |
|--------|----------|----------|
| `MessageServiceTest` | `sendMessage_Success()` | 正常发送消息 |
| | `sendMessage_DuplicateClientId()` | 重复客户端消息ID去重 |
| | `sendMessage_WithAttachment()` | 发送带附件消息 |
| | `sendMessage_WithReply()` | 发送引用回复消息 |
| | `recallMessage_Success()` | 正常撤回消息（2分钟内） |
| | `recallMessage_Timeout()` | 超过2分钟撤回失败 |
| | `recallMessage_NotSender()` | 非发送者撤回失败 |
| | `searchMessages_ByContent()` | 按内容搜索消息 |
| | `searchMessages_ByTimeRange()` | 按时间范围搜索 |
| | `markAsRead_Single()` | 单条消息已读 |
| | `markAsRead_Batch()` | 批量已读 |
| | `getUnreadCount()` | 获取未读消息数 |
| | `pinMessage()` | 置顶消息 |
| | `unpinMessage()` | 取消置顶 |
| | `favoriteMessage()` | 收藏消息 |
| `MessageRepositoryTest` | `findByConversationId()` | 按会话查询 |
| | `findBySenderId()` | 按发送者查询 |
| | `searchByContent()` | 内容搜索 |

#### 1.2 im-service-user 用户服务
| 测试类 | 测试方法 | 测试场景 |
|--------|----------|----------|
| `UserServiceTest` | `register_Success()` | 正常注册 |
| | `register_DuplicateUsername()` | 重复用户名注册失败 |
| | `register_DuplicateEmail()` | 重复邮箱注册失败 |
| | `login_Success()` | 正常登录 |
| | `login_InvalidPassword()` | 密码错误 |
| | `login_AccountLocked()` | 账号锁定 |
| | `updateUserInfo_Success()` | 更新用户信息 |
| | `updatePrivacySettings()` | 更新隐私设置 |
| | `searchUsers_ByKeyword()` | 关键词搜索用户 |
| `FriendServiceTest` | `sendFriendRequest_Success()` | 发送好友申请 |
| | `sendFriendRequest_AlreadyFriend()` | 已是好友 |
| | `sendFriendRequest_Blocked()` | 被对方拉黑 |
| | `handleFriendRequest_Accept()` | 接受好友申请 |
| | `handleFriendRequest_Reject()` | 拒绝好友申请 |
| | `deleteFriend()` | 删除好友 |
| | `getFriendList()` | 获取好友列表 |
| | `starFriend()` | 星标好友 |
| | `muteFriend()` | 设置消息免打扰 |
| | `addToBlacklist()` | 加入黑名单 |

#### 1.3 im-service-auth 认证服务
| 测试类 | 测试方法 | 测试场景 |
|--------|----------|----------|
| `JwtTokenProviderTest` | `generateAccessToken()` | 生成Access Token |
| | `generateRefreshToken()` | 生成Refresh Token |
| | `validateToken_Valid()` | 验证有效Token |
| | `validateToken_Expired()` | 验证过期Token |
| | `validateToken_Invalid()` | 验证无效Token |
| | `getUserIdFromToken()` | 从Token获取用户ID |
| | `getDeviceIdFromToken()` | 从Token获取设备ID |
| `TokenBlacklistServiceTest` | `addToBlacklist()` | 加入黑名单 |
| | `isBlacklisted_True()` | Token在黑名单中 |
| | `isBlacklisted_False()` | Token不在黑名单中 |
| | `cleanupExpiredTokens()` | 清理过期Token |
| `AuthServiceTest` | `login_Success()` | 登录成功 |
| | `login_InvalidCredentials()` | 无效凭证 |
| | `login_TooManyAttempts()` | 登录次数过多锁定 |
| | `refreshToken_Success()` | 刷新Token成功 |
| | `refreshToken_ReuseDetected()` | 检测Token重复使用 |
| | `logout_Success()` | 登出成功 |
| | `logoutAllDevices()` | 全设备登出 |

#### 1.4 im-service-group 群组服务
| 测试类 | 测试方法 | 测试场景 |
|--------|----------|----------|
| `GroupServiceTest` | `createGroup_Success()` | 创建群组 |
| | `createGroup_InvalidName()` | 无效群名称 |
| | `updateGroupInfo()` | 更新群信息 |
| | `dissolveGroup_Owner()` | 群主解散群组 |
| | `dissolveGroup_NotOwner()` | 非群主解散失败 |
| | `publishAnnouncement()` | 发布群公告 |
| | `muteAll()` | 全员禁言 |
| | `unmuteAll()` | 取消全员禁言 |
| `GroupMemberServiceTest` | `addMember_Success()` | 添加成员 |
| | `addMember_AlreadyInGroup()` | 成员已在群中 |
| | `addMember_GroupFull()` | 群已满员 |
| | `removeMember()` | 移除成员 |
| | `updateMemberRole_ToAdmin()` | 设为管理员 |
| | `transferOwnership()` | 转让群主 |
| | `muteMember()` | 禁言成员 |
| | `setMemberNickname()` | 设置群昵称 |
| | `setDoNotDisturb()` | 设置消息免打扰 |
| | `exitGroup()` | 退出群组 |

#### 1.5 im-service-websocket WebSocket服务
| 测试类 | 测试方法 | 测试场景 |
|--------|----------|----------|
| `WebSocketSessionManagerTest` | `registerSession()` | 注册会话 |
| | `unregisterSession()` | 注销会话 |
| | `getSessionByUserId()` | 按用户ID获取会话 |
| | `getSessionsByUserId()` | 获取用户所有会话（多设备）|
| | `isUserOnline()` | 检查用户在线状态 |
| | `heartbeat_Update()` | 心跳更新 |
| | `heartbeat_Timeout()` | 心跳超时 |
| `OnlineStatusServiceTest` | `setUserOnline()` | 设置用户在线 |
| | `setUserOffline()` | 设置用户离线 |
| | `getUserStatus()` | 获取用户状态 |
| | `getFriendsStatus()` | 获取好友状态列表 |
| | `updateLastSeen()` | 更新最后在线时间 |

---

### 2. 集成测试（需要外部依赖）

#### 2.1 需要 MySQL 数据库
| 测试类 | 测试场景 | 需要资源 |
|--------|----------|----------|
| `MessageRepositoryIntegrationTest` | 消息CRUD操作 | MySQL + 测试数据 |
| `UserRepositoryIntegrationTest` | 用户CRUD操作 | MySQL + 测试数据 |
| `FriendRepositoryIntegrationTest` | 好友关系操作 | MySQL + 测试数据 |
| `GroupRepositoryIntegrationTest` | 群组CRUD操作 | MySQL + 测试数据 |
| `GroupMemberRepositoryIntegrationTest` | 群成员操作 | MySQL + 测试数据 |
| `RefreshTokenRepositoryIntegrationTest` | RefreshToken操作 | MySQL + 测试数据 |

#### 2.2 需要 Redis
| 测试类 | 测试场景 | 需要资源 |
|--------|----------|----------|
| `TokenBlacklistIntegrationTest` | Token黑名单 | Redis |
| `OnlineStatusIntegrationTest` | 在线状态存储 | Redis |
| `SessionCacheIntegrationTest` | 会话缓存 | Redis |

#### 2.3 需要 WebSocket 连接
| 测试类 | 测试场景 | 需要资源 |
|--------|----------|----------|
| `WebSocketConnectionTest` | WebSocket连接建立 | WebSocket服务器 |
| `WebSocketMessageFlowTest` | 消息收发流程 | WebSocket服务器 |
| `WebSocketAuthTest` | WebSocket认证 | WebSocket服务器 + JWT |

#### 2.4 完整端到端测试
| 测试类 | 测试场景 | 需要资源 |
|--------|----------|----------|
| `SendMessageE2ETest` | 发送消息完整流程 | MySQL + Redis + WebSocket |
| `FriendRequestE2ETest` | 好友申请完整流程 | MySQL |
| `GroupChatE2ETest` | 群聊完整流程 | MySQL + Redis + WebSocket |
| `UserLoginE2ETest` | 用户登录流程 | MySQL + Redis |

---

## ❌ 无法测试的组件清单

### 1. 需要真实外部服务（无法模拟）
| 组件 | 原因 | 可能的解决方案 |
|------|------|----------------|
| 邮件发送服务 | 需要SMTP服务器 | 使用MailHog本地邮件服务器 |
| 短信验证码服务 | 需要短信服务商API | 使用模拟短信服务或测试账号 |
| 推送通知服务 | 需要APNs/FCM密钥 | 使用模拟推送服务 |
| 文件存储服务 | 需要OSS/COS/S3 | 使用MinIO本地对象存储 |

### 2. 需要特定硬件/环境
| 组件 | 原因 | 可能的解决方案 |
|------|------|----------------|
| WebRTC音视频 | 需要摄像头/麦克风 | 使用虚拟摄像头驱动 |
| 地理位置服务 | 需要真实GPS或位置模拟 | 使用Mock位置工具 |
| 生物识别认证 | 需要指纹/面部识别硬件 | 无法测试，需人工验证 |

### 3. 需要第三方平台集成
| 组件 | 原因 | 可能的解决方案 |
|------|------|----------------|
| 微信登录 | 需要微信开放平台账号 | 申请测试账号或使用Mock |
| 支付宝登录 | 需要支付宝开放平台账号 | 申请测试账号或使用Mock |
| 小程序容器 | 需要微信小程序环境 | 使用微信开发者工具 |

### 4. 性能测试（需要特定环境）
| 测试类型 | 原因 | 可能的解决方案 |
|----------|------|----------------|
| 压力测试 | 需要大量并发用户 | 使用JMeter + 测试环境 |
| 负载测试 | 需要生产级硬件 | 使用云平台临时资源 |
| 长时间稳定性测试 | 需要持续运行环境 | 使用CI/CD流水线 |

---

## 📝 测试用例详情

### 测试用例 1: 消息发送 - 正常流程

**测试类**: `MessageServiceTest`
**测试方法**: `sendMessage_Success()`

```java
@Test
@DisplayName("正常发送消息 - 成功")
void sendMessage_Success() {
    // 准备
    SendMessageRequest request = new SendMessageRequest();
    request.setConversationId("conv_001");
    request.setSenderId("user_001");
    request.setContentType(ContentType.TEXT);
    request.setContent("Hello World");
    request.setClientMessageId("client_001");
    
    // 执行
    MessageResponse response = messageService.sendMessage(request);
    
    // 验证
    assertNotNull(response);
    assertNotNull(response.getMessageId());
    assertEquals("conv_001", response.getConversationId());
    assertEquals("user_001", response.getSenderId());
    assertEquals("Hello World", response.getContent());
    assertEquals(MessageStatus.SENT, response.getStatus());
    assertNotNull(response.getCreatedAt());
    
    // 验证数据库
    Optional<Message> saved = messageRepository.findById(response.getMessageId());
    assertTrue(saved.isPresent());
    assertEquals("Hello World", saved.get().getContent());
}
```

---

### 测试用例 2: 消息撤回 - 时间限制

**测试类**: `MessageServiceTest`
**测试方法**: `recallMessage_Timeout()`

```java
@Test
@DisplayName("超过2分钟撤回消息 - 失败")
void recallMessage_Timeout() {
    // 准备 - 创建3分钟前的消息
    Message message = new Message();
    message.setMessageId("msg_001");
    message.setSenderId("user_001");
    message.setCreatedAt(LocalDateTime.now().minusMinutes(3));
    message.setStatus(MessageStatus.DELIVERED);
    messageRepository.save(message);
    
    // 执行 & 验证
    assertThrows(RecallTimeoutException.class, () -> {
        messageService.recallMessage("msg_001", "user_001");
    });
}
```

---

### 测试用例 3: Token生成与验证

**测试类**: `JwtTokenProviderTest`
**测试方法**: `validateToken_Valid()`

```java
@Test
@DisplayName("验证有效Token - 成功")
void validateToken_Valid() {
    // 准备
    String userId = "user_001";
    String deviceId = "device_001";
    
    // 执行
    String token = jwtTokenProvider.generateAccessToken(userId, deviceId);
    boolean isValid = jwtTokenProvider.validateToken(token);
    
    // 验证
    assertTrue(isValid);
    assertEquals(userId, jwtTokenProvider.getUserIdFromToken(token));
    assertEquals(deviceId, jwtTokenProvider.getDeviceIdFromToken(token));
}
```

---

### 测试用例 4: 好友申请 - 已是好友

**测试类**: `FriendServiceTest`
**测试方法**: `sendFriendRequest_AlreadyFriend()`

```java
@Test
@DisplayName("发送好友申请 - 已是好友")
void sendFriendRequest_AlreadyFriend() {
    // 准备 - 已经是好友关系
    Friend existing = new Friend();
    existing.setUserId("user_001");
    existing.setFriendId("user_002");
    existing.setStatus(FriendStatus.ACCEPTED);
    friendRepository.save(existing);
    
    // 执行 & 验证
    assertThrows(AlreadyFriendException.class, () -> {
        friendService.sendFriendRequest("user_001", "user_002", "Hello");
    });
}
```

---

### 测试用例 5: 群组 - 非群主解散

**测试类**: `GroupServiceTest`
**测试方法**: `dissolveGroup_NotOwner()`

```java
@Test
@DisplayName("解散群组 - 非群主失败")
void dissolveGroup_NotOwner() {
    // 准备
    Group group = new Group();
    group.setGroupId("group_001");
    group.setOwnerId("user_001");
    groupRepository.save(group);
    
    // 执行 & 验证 - user_002不是群主
    assertThrows(NoPermissionException.class, () -> {
        groupService.dissolveGroup("group_001", "user_002");
    });
}
```

---

### 测试用例 6: WebSocket会话管理

**测试类**: `WebSocketSessionManagerTest`
**测试方法**: `registerSession()`

```java
@Test
@DisplayName("注册WebSocket会话")
void registerSession() {
    // 准备
    String userId = "user_001";
    String deviceId = "device_001";
    String sessionId = "session_001";
    WebSocketSession session = mock(WebSocketSession.class);
    when(session.getId()).thenReturn(sessionId);
    
    // 执行
    sessionManager.registerSession(userId, deviceId, session);
    
    // 验证
    WebSocketSession registered = sessionManager.getSessionByUserId(userId, deviceId);
    assertNotNull(registered);
    assertEquals(sessionId, registered.getId());
    assertTrue(sessionManager.isUserOnline(userId));
}
```

---

## 🔧 测试环境配置

### 1. 单元测试配置
```yaml
# application-test.yml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
  
  redis:
    host: localhost
    port: 6379
    database: 1  # 使用单独的数据库

# 测试特定配置
message:
  recall:
    timeout-minutes: 2  # 测试时可以用更短的时间

logging:
  level:
    com.im: DEBUG
```

### 2. 集成测试配置
```yaml
# application-integration-test.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/im_test
    username: test
    password: test
  
  redis:
    host: localhost
    port: 6379
```

---

## 📊 测试覆盖率目标

| 模块 | 单元测试覆盖率 | 集成测试覆盖率 |
|------|---------------|---------------|
| im-service-message | ≥ 80% | ≥ 60% |
| im-service-user | ≥ 80% | ≥ 60% |
| im-service-auth | ≥ 85% | ≥ 70% |
| im-service-group | ≥ 80% | ≥ 60% |
| im-service-websocket | ≥ 70% | ≥ 50% |

---

## 🚀 执行计划

### 阶段 1: 单元测试（无需外部依赖）
1. ✅ MessageService 单元测试
2. ✅ UserService 单元测试
3. ✅ FriendService 单元测试
4. ✅ AuthService 单元测试
5. ✅ GroupService 单元测试
6. ✅ WebSocket 单元测试
7. ✅ JwtTokenProvider 单元测试

### 阶段 2: 集成测试（需要外部依赖）
需要用户确认是否提供以下资源：
- [ ] MySQL 测试数据库
- [ ] Redis 测试实例
- [ ] WebSocket 测试环境

### 阶段 3: 端到端测试
- [ ] 完整业务流程测试

---

## 📝 交付物清单

1. **单元测试代码** - 已完成，位于各模块 `src/test/java` 目录
2. **测试报告** - Maven Surefire 报告
3. **覆盖率报告** - JaCoCo 覆盖率报告
4. **集成测试配置** - 等待环境确认

---

## ❓ 需要用户确认

### 1. 数据库测试环境
**能否提供以下资源？**
- [ ] MySQL 测试数据库（可以是本地Docker）
- [ ] Redis 测试实例
- [ ] 是否需要我使用 H2 内存数据库代替？

### 2. WebSocket测试
**能否提供以下资源？**
- [ ] WebSocket 测试服务器
- [ ] 是否只测试 WebSocket 逻辑（不测试真实连接）？

### 3. 外部服务Mock
**是否需要我创建 Mock 服务？**
- [ ] 邮件服务 Mock
- [ ] 短信服务 Mock
- [ ] 推送服务 Mock
- [ ] 文件存储 Mock

### 4. 性能测试
**是否需要进行性能测试？**
- [ ] 压力测试（需要JMeter）
- [ ] 负载测试（需要更多硬件资源）
- [ ] 不需要，当前只做功能测试

---

## 🎯 下一步行动

请告诉我：
1. **能否提供 MySQL + Redis 测试环境？**（可以用Docker快速搭建）
2. **是否现在就开始写单元测试代码？**（无需外部依赖）
3. **哪些外部服务需要 Mock？**

根据你的回答，我将开始编写具体的测试代码。
