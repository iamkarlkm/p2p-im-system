package com.im.message.repository;

import com.im.message.entity.Message;
import com.im.message.enums.MessageStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 消息仓库单元测试
 * 使用H2内存数据库进行集成测试
 */
@DataJpaTest
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
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
        readMessage.setReadAt(LocalDateTime.now());
        
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

    @Test
    @DisplayName("按客户端消息ID查找 - 成功")
    void findByClientMessageId() {
        // Given
        Message message = createMessage("conv_001", "user_001", "Test");
        message.setClientMessageId("client_001");
        messageRepository.save(message);

        // When
        Optional<Message> result = messageRepository.findByClientMessageId("client_001");

        // Then
        assertTrue(result.isPresent());
        assertEquals("client_001", result.get().getClientMessageId());
    }

    @Test
    @DisplayName("分页查询会话消息 - 成功")
    void findByConversationIdWithPagination() {
        // Given
        for (int i = 0; i < 10; i++) {
            Message msg = createMessage("conv_001", "user_001", "Message " + i);
            messageRepository.save(msg);
        }

        // When
        org.springframework.data.domain.Pageable pageable = 
            org.springframework.data.domain.PageRequest.of(0, 5);
        org.springframework.data.domain.Page<Message> page = 
            messageRepository.findByConversationId("conv_001", pageable);

        // Then
        assertEquals(5, page.getContent().size());
        assertEquals(10, page.getTotalElements());
    }

    @Test
    @DisplayName("按时间范围查询消息 - 成功")
    void findByTimeRange() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        
        Message oldMessage = createMessage("conv_001", "user_001", "Old");
        oldMessage.setCreatedAt(now.minusDays(2));
        
        Message recentMessage = createMessage("conv_001", "user_002", "Recent");
        recentMessage.setCreatedAt(now);
        
        messageRepository.saveAll(List.of(oldMessage, recentMessage));

        // When
        List<Message> results = messageRepository.findByConversationIdAndCreatedAtBetween(
            "conv_001", now.minusDays(1), now.plusDays(1)
        );

        // Then
        assertEquals(1, results.size());
        assertEquals("Recent", results.get(0).getContent());
    }

    @Test
    @DisplayName("查询置顶消息 - 成功")
    void findPinnedMessages() {
        // Given
        Message normalMessage = createMessage("conv_001", "user_001", "Normal");
        normalMessage.setIsPinned(false);
        
        Message pinnedMessage = createMessage("conv_001", "user_002", "Pinned");
        pinnedMessage.setIsPinned(true);
        pinnedMessage.setPinnedAt(LocalDateTime.now());
        
        messageRepository.saveAll(List.of(normalMessage, pinnedMessage));

        // When
        List<Message> results = messageRepository.findByConversationIdAndIsPinnedTrue("conv_001");

        // Then
        assertEquals(1, results.size());
        assertEquals("Pinned", results.get(0).getContent());
    }

    @Test
    @DisplayName("批量更新消息状态 - 成功")
    void updateStatusBatch() {
        // Given
        Message msg1 = createMessage("conv_001", "user_001", "Test1");
        Message msg2 = createMessage("conv_001", "user_001", "Test2");
        msg1 = messageRepository.save(msg1);
        msg2 = messageRepository.save(msg2);

        // When
        int updated = messageRepository.updateStatusByIds(
            MessageStatus.READ, 
            List.of(msg1.getMessageId(), msg2.getMessageId())
        );

        // Then
        assertEquals(2, updated);
        
        Message updated1 = messageRepository.findById(msg1.getMessageId()).orElseThrow();
        assertEquals(MessageStatus.READ, updated1.getStatus());
    }

    @Test
    @DisplayName("统计消息数量 - 成功")
    void countMessages() {
        // Given
        Message msg1 = createMessage("conv_001", "user_001", "Test1");
        Message msg2 = createMessage("conv_001", "user_002", "Test2");
        msg1.setStatus(MessageStatus.SENT);
        msg2.setStatus(MessageStatus.DELIVERED);
        messageRepository.saveAll(List.of(msg1, msg2));

        // When
        long totalCount = messageRepository.countByConversationId("conv_001");
        long sentCount = messageRepository.countByConversationIdAndStatus("conv_001", MessageStatus.SENT);

        // Then
        assertEquals(2, totalCount);
        assertEquals(1, sentCount);
    }

    @Test
    @DisplayName("查询最新消息 - 成功")
    void findLatestMessage() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        
        Message oldMessage = createMessage("conv_001", "user_001", "Old");
        oldMessage.setCreatedAt(now.minusHours(1));
        
        Message latestMessage = createMessage("conv_001", "user_002", "Latest");
        latestMessage.setCreatedAt(now);
        
        messageRepository.saveAll(List.of(oldMessage, latestMessage));

        // When
        Optional<Message> result = messageRepository.findFirstByConversationIdOrderByCreatedAtDesc("conv_001");

        // Then
        assertTrue(result.isPresent());
        assertEquals("Latest", result.get().getContent());
    }

    @Test
    @DisplayName("查询收藏消息 - 成功")
    void findFavoriteMessages() {
        // Given
        Message normalMessage = createMessage("conv_001", "user_001", "Normal");
        Message favoriteMessage = createMessage("conv_001", "user_001", "Favorite");
        favoriteMessage.setIsFavorite(true);
        favoriteMessage.setFavoritedAt(LocalDateTime.now());
        
        messageRepository.saveAll(List.of(normalMessage, favoriteMessage));

        // When
        List<Message> results = messageRepository.findBySenderIdAndIsFavoriteTrue("user_001");

        // Then
        assertEquals(1, results.size());
        assertEquals("Favorite", results.get(0).getContent());
    }

    private Message createMessage(String conversationId, String senderId, String content) {
        Message message = new Message();
        message.setConversationId(conversationId);
        message.setSenderId(senderId);
        message.setContent(content);
        message.setContentType(com.im.message.enums.ContentType.TEXT);
        message.setStatus(MessageStatus.SENT);
        message.setCreatedAt(LocalDateTime.now());
        message.setIsRecalled(false);
        message.setIsDeleted(false);
        message.setIsPinned(false);
        message.setIsFavorite(false);
        return message;
    }
}
