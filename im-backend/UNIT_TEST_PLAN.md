# im-modular 单元测试 - 消息服务
# 创建时间: 2026-04-08
# 测试框架: JUnit 5 + Mockito + H2

## 测试文件清单

### 1. MessageServiceTest.java
路径: `im-service-message/src/test/java/com/im/message/service/MessageServiceTest.java`

```java
package com.im.message.service;

import com.im.message.dto.SendMessageRequest;
import com.im.message.dto.MessageResponse;
import com.im.message.entity.Message;
import com.im.message.enums.ContentType;
import com.im.message.enums.MessageStatus;
import com.im.message.exception.RecallTimeoutException;
import com.im.message.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("消息服务单元测试")
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MessageService messageService;

    private SendMessageRequest validRequest;
    private Message savedMessage;

    @BeforeEach
    void setUp() {
        // 准备有效的发送消息请求
        validRequest = new SendMessageRequest();
        validRequest.setConversationId("conv_001");
        validRequest.setSenderId("user_001");
        validRequest.setContentType(ContentType.TEXT);
        validRequest.setContent("Hello World");
        validRequest.setClientMessageId("client_001");

        // 准备已保存的消息实体
        savedMessage = new Message();
        savedMessage.setMessageId("msg_001");
        savedMessage.setConversationId("conv_001");
        savedMessage.setSenderId("user_001");
        savedMessage.setContent("Hello World");
        savedMessage.setStatus(MessageStatus.SENT);
        savedMessage.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("正常发送消息 - 成功")
    void sendMessage_Success() {
        // Given
        when(messageRepository.existsByClientMessageId("client_001")).thenReturn(false);
        when(messageRepository.save(any(Message.class))).thenReturn(savedMessage);

        // When
        MessageResponse response = messageService.sendMessage(validRequest);

        // Then
        assertNotNull(response);
        assertEquals("msg_001", response.getMessageId());
        assertEquals("conv_001", response.getConversationId());
        assertEquals("user_001", response.getSenderId());
        assertEquals("Hello World", response.getContent());
        assertEquals(MessageStatus.SENT, response.getStatus());

        verify(messageRepository).existsByClientMessageId("client_001");
        verify(messageRepository).save(any(Message.class));
    }

    @Test
    @DisplayName("重复客户端消息ID - 返回已存在消息")
    void sendMessage_DuplicateClientId() {
        // Given
        when(messageRepository.existsByClientMessageId("client_001")).thenReturn(true);
        when(messageRepository.findByClientMessageId("client_001")).thenReturn(Optional.of(savedMessage));

        // When
        MessageResponse response = messageService.sendMessage(validRequest);

        // Then
        assertNotNull(response);
        assertEquals("msg_001", response.getMessageId());
        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    @DisplayName("发送带附件消息 - 成功")
    void sendMessage_WithAttachment() {
        // Given
        SendMessageRequest.AttachmentDTO attachment = new SendMessageRequest.AttachmentDTO();
        attachment.setFileName("test.jpg");
        attachment.setFileUrl("https://example.com/test.jpg");
        attachment.setFileType("image/jpeg");
        attachment.setFileSize(1024L);
        validRequest.setAttachments(java.util.Collections.singletonList(attachment));
        validRequest.setContentType(ContentType.IMAGE);

        when(messageRepository.existsByClientMessageId("client_001")).thenReturn(false);
        when(messageRepository.save(any(Message.class))).thenReturn(savedMessage);

        // When
        MessageResponse response = messageService.sendMessage(validRequest);

        // Then
        assertNotNull(response);
        verify(messageRepository).save(any(Message.class));
    }

    @Test
    @DisplayName("正常撤回消息 - 成功")
    void recallMessage_Success() {
        // Given
        savedMessage.setCreatedAt(LocalDateTime.now()); // 刚刚创建的消息
        savedMessage.setSenderId("user_001");
        when(messageRepository.findById("msg_001")).thenReturn(Optional.of(savedMessage));

        // When
        messageService.recallMessage("msg_001", "user_001");

        // Then
        assertTrue(savedMessage.getIsRecalled());
        verify(messageRepository).save(savedMessage);
    }

    @Test
    @DisplayName("超过2分钟撤回消息 - 失败")
    void recallMessage_Timeout() {
        // Given - 3分钟前创建的消息
        savedMessage.setCreatedAt(LocalDateTime.now().minusMinutes(3));
        savedMessage.setSenderId("user_001");
        when(messageRepository.findById("msg_001")).thenReturn(Optional.of(savedMessage));

        // When & Then
        assertThrows(RecallTimeoutException.class, () -> {
            messageService.recallMessage("msg_001", "user_001");
        });
    }

    @Test
    @DisplayName("非发送者撤回消息 - 失败")
    void recallMessage_NotSender() {
        // Given
        savedMessage.setCreatedAt(LocalDateTime.now());
        savedMessage.setSenderId("user_001"); // 实际发送者
        when(messageRepository.findById("msg_001")).thenReturn(Optional.of(savedMessage));

        // When & Then
        assertThrows(SecurityException.class, () -> {
            messageService.recallMessage("msg_001", "user_002"); // 尝试撤回的用户
        });
    }

    @Test
    @DisplayName("单条消息已读 - 成功")
    void markAsRead_Single() {
        // Given
        savedMessage.setStatus(MessageStatus.DELIVERED);
        when(messageRepository.findById("msg_001")).thenReturn(Optional.of(savedMessage));

        // When
        messageService.markAsRead("msg_001", "user_002");

        // Then
        assertEquals(MessageStatus.READ, savedMessage.getStatus());
        assertNotNull(savedMessage.getReadAt());
        verify(messageRepository).save(savedMessage);
    }

    @Test
    @DisplayName("获取未读消息数 - 成功")
    void getUnreadCount() {
        // Given
        when(messageRepository.countUnreadByConversationIdAndUserId("conv_001", "user_002"))
            .thenReturn(5L);

        // When
        long count = messageService.getUnreadCount("conv_001", "user_002");

        // Then
        assertEquals(5L, count);
    }

    @Test
    @DisplayName("置顶消息 - 成功")
    void pinMessage() {
        // Given
        when(messageRepository.findById("msg_001")).thenReturn(Optional.of(savedMessage));

        // When
        messageService.pinMessage("msg_001", "user_001");

        // Then
        assertTrue(savedMessage.getIsPinned());
        verify(messageRepository).save(savedMessage);
    }

    @Test
    @DisplayName("收藏消息 - 成功")
    void favoriteMessage() {
        // Given
        when(messageRepository.findById("msg_001")).thenReturn(Optional.of(savedMessage));

        // When
        messageService.favoriteMessage("msg_001", "user_001");

        // Then
        assertTrue(savedMessage.getIsFavorite());
        verify(messageRepository).save(savedMessage);
    }
}
```

### 2. MessageRepositoryTest.java
路径: `im-service-message/src/test/java/com/im/message/repository/MessageRepositoryTest.java`

```java
package com.im.message.repository;

import com.im.message.entity.Message;
import com.im.message.enums.MessageStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("消息仓库单元测试")
class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    @Test
    @DisplayName("按会话ID查询消息 - 成功")
    void findByConversationId() {
        // Given
        Message message1 = createMessage("conv_001", "user_001", "Hello");
        Message message2 = createMessage("conv_001", "user_002", "Hi");
        Message message3 = createMessage("conv_002", "user_001", "Other");
        messageRepository.saveAll(List.of(message1, message2, message3));

        // When
        List<Message> results = messageRepository.findByConversationIdOrderByCreatedAtDesc("conv_001");

        // Then
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(m -> m.getConversationId().equals("conv_001")));
    }

    @Test
    @DisplayName("按发送者查询消息 - 成功")
    void findBySenderId() {
        // Given
        Message message = createMessage("conv_001", "user_001", "Test");
        messageRepository.save(message);

        // When
        List<Message> results = messageRepository.findBySenderId("user_001");

        // Then
        assertEquals(1, results.size());
        assertEquals("user_001", results.get(0).getSenderId());
    }

    @Test
    @DisplayName("内容搜索 - 成功")
    void searchByContent() {
        // Given
        Message message1 = createMessage("conv_001", "user_001", "Hello World");
        Message message2 = createMessage("conv_001", "user_002", "Goodbye");
        messageRepository.saveAll(List.of(message1, message2));

        // When
        List<Message> results = messageRepository.searchByContentContaining("Hello");

        // Then
        assertEquals(1, results.size());
        assertEquals("Hello World", results.get(0).getContent());
    }

    @Test
    @DisplayName("统计未读消息数 - 成功")
    void countUnread() {
        // Given
        Message readMessage = createMessage("conv_001", "user_001", "Read");
        readMessage.setStatus(MessageStatus.READ);
        
        Message unreadMessage = createMessage("conv_001", "user_001", "Unread");
        unreadMessage.setStatus(MessageStatus.DELIVERED);
        
        messageRepository.saveAll(List.of(readMessage, unreadMessage));

        // When
        long count = messageRepository.countUnreadByConversationIdAndUserId("conv_001", "user_002");

        // Then - 返回1条未读
        assertEquals(1, count);
    }

    @Test
    @DisplayName("检查客户端消息ID存在性 - 成功")
    void existsByClientMessageId() {
        // Given
        Message message = createMessage("conv_001", "user_001", "Test");
        message.setClientMessageId("client_001");
        messageRepository.save(message);

        // When & Then
        assertTrue(messageRepository.existsByClientMessageId("client_001"));
        assertFalse(messageRepository.existsByClientMessageId("client_999"));
    }

    private Message createMessage(String conversationId, String senderId, String content) {
        Message message = new Message();
        message.setConversationId(conversationId);
        message.setSenderId(senderId);
        message.setContent(content);
        message.setStatus(MessageStatus.SENT);
        message.setCreatedAt(LocalDateTime.now());
        return message;
    }
}
```

### 3. application-test.yml
路径: `im-service-message/src/test/resources/application-test.yml`

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    driver-class-name: org.h2.Driver
    username: sa
    password:
  
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.H2Dialect
  
  h2:
    console:
      enabled: true

logging:
  level:
    com.im: DEBUG
    org.hibernate.SQL: DEBUG
```

### 4. pom.xml 测试依赖
添加到 `im-service-message/pom.xml`:

```xml
<dependencies>
    <!-- 原有依赖... -->
    
    <!-- 测试依赖 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>test</scope>
    </dependency>
    
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <scope>test</scope>
    </dependency>
    
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <scope>test</scope>
    </dependency>
    
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-junit-jupiter</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

---

## 运行测试

```bash
# 进入模块目录
cd multi_agent/projects/im-modular/im-service-message

# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=MessageServiceTest

# 运行特定测试方法
mvn test -Dtest=MessageServiceTest#sendMessage_Success

# 生成测试报告
mvn surefire-report:report
```

---

## 测试覆盖目标

| 类 | 方法数 | 测试覆盖 |
|-----|--------|----------|
| MessageService | 15+ | 80%+ |
| MessageRepository | 20+ | 60%+ |

---

## 下一步

1. 完成消息服务单元测试
2. 创建用户服务单元测试 (UserServiceTest, FriendServiceTest)
3. 创建认证服务单元测试 (AuthServiceTest, JwtTokenProviderTest)
4. 创建群组服务单元测试 (GroupServiceTest)
5. 创建WebSocket服务单元测试

