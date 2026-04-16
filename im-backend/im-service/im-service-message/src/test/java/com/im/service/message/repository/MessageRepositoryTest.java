package com.im.service.message.repository;

import com.im.service.message.entity.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * MessageRepository 单元测试
 * 
 * 使用@Mock测试Repository方法调用
 * 
 * @author IM Team
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("消息仓库单元测试")
class MessageRepositoryTest {

    @Mock
    private MessageRepository messageRepository;

    private static final String TEST_MESSAGE_ID = "msg_123456";
    private static final String TEST_CONVERSATION_ID = "conv_789";
    private static final String TEST_USER_ID = "user_001";
    private static final String TEST_CLIENT_MESSAGE_ID = "client_msg_001";

    @Test
    @DisplayName("根据ID查询消息")
    void findById() {
        // Prepare
        Message message = new Message();
        message.setId(TEST_MESSAGE_ID);
        message.setContent("Test content");

        when(messageRepository.findById(TEST_MESSAGE_ID))
                .thenReturn(Optional.of(message));

        // Act
        Optional<Message> result = messageRepository.findById(TEST_MESSAGE_ID);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(TEST_MESSAGE_ID);
        verify(messageRepository, times(1)).findById(TEST_MESSAGE_ID);
    }

    @Test
    @DisplayName("根据客户端消息ID查询-用于去重")
    void findByClientMessageId() {
        // Prepare
        Message message = new Message();
        message.setId(TEST_MESSAGE_ID);
        message.setClientMessageId(TEST_CLIENT_MESSAGE_ID);

        when(messageRepository.findByClientMessageId(TEST_CLIENT_MESSAGE_ID))
                .thenReturn(Optional.of(message));

        // Act
        Optional<Message> result = messageRepository.findByClientMessageId(TEST_CLIENT_MESSAGE_ID);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getClientMessageId()).isEqualTo(TEST_CLIENT_MESSAGE_ID);
        verify(messageRepository, times(1)).findByClientMessageId(TEST_CLIENT_MESSAGE_ID);
    }

    @Test
    @DisplayName("分页查询会话消息")
    void findByConversationIdAndUserId() {
        // Prepare
        Pageable pageable = PageRequest.of(0, 20);
        Message message = new Message();
        message.setId(TEST_MESSAGE_ID);
        message.setConversationId(TEST_CONVERSATION_ID);

        Page<Message> page = new PageImpl<>(List.of(message), pageable, 1);

        when(messageRepository.findByConversationIdAndUserId(TEST_CONVERSATION_ID, TEST_USER_ID, pageable))
                .thenReturn(page);

        // Act
        Page<Message> result = messageRepository.findByConversationIdAndUserId(TEST_CONVERSATION_ID, TEST_USER_ID, pageable);

        // Assert
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getConversationId()).isEqualTo(TEST_CONVERSATION_ID);
        verify(messageRepository, times(1)).findByConversationIdAndUserId(TEST_CONVERSATION_ID, TEST_USER_ID, pageable);
    }

    @Test
    @DisplayName("查询会话中指定时间之后的消息")
    void findByConversationIdSince() {
        // Prepare
        LocalDateTime since = LocalDateTime.now().minusHours(1);
        Message message = new Message();
        message.setId(TEST_MESSAGE_ID);
        message.setCreatedAt(LocalDateTime.now());

        when(messageRepository.findByConversationIdSince(TEST_CONVERSATION_ID, since, TEST_USER_ID))
                .thenReturn(List.of(message));

        // Act
        List<Message> result = messageRepository.findByConversationIdSince(TEST_CONVERSATION_ID, since, TEST_USER_ID);

        // Assert
        assertThat(result).hasSize(1);
        verify(messageRepository, times(1)).findByConversationIdSince(TEST_CONVERSATION_ID, since, TEST_USER_ID);
    }

    @Test
    @DisplayName("搜索消息内容")
    void searchMessages() {
        // Prepare
        String keyword = "hello";
        Message message = new Message();
        message.setId(TEST_MESSAGE_ID);
        message.setContent("Hello world");

        when(messageRepository.searchMessages(TEST_CONVERSATION_ID, keyword, TEST_USER_ID))
                .thenReturn(List.of(message));

        // Act
        List<Message> result = messageRepository.searchMessages(TEST_CONVERSATION_ID, keyword, TEST_USER_ID);

        // Assert
        assertThat(result).hasSize(1);
        verify(messageRepository, times(1)).searchMessages(TEST_CONVERSATION_ID, keyword, TEST_USER_ID);
    }

    @Test
    @DisplayName("标记消息为已读")
    void markAsRead() {
        // Prepare
        when(messageRepository.markAsRead(eq(TEST_MESSAGE_ID), any(LocalDateTime.class)))
                .thenReturn(1);

        // Act
        int result = messageRepository.markAsRead(TEST_MESSAGE_ID, LocalDateTime.now());

        // Assert
        assertThat(result).isEqualTo(1);
        verify(messageRepository, times(1)).markAsRead(eq(TEST_MESSAGE_ID), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("标记会话中所有消息为已读")
    void markConversationAsRead() {
        // Prepare
        when(messageRepository.markConversationAsRead(eq(TEST_CONVERSATION_ID), eq(TEST_USER_ID), any(LocalDateTime.class)))
                .thenReturn(5);

        // Act
        int result = messageRepository.markConversationAsRead(TEST_CONVERSATION_ID, TEST_USER_ID, LocalDateTime.now());

        // Assert
        assertThat(result).isEqualTo(5);
        verify(messageRepository, times(1)).markConversationAsRead(eq(TEST_CONVERSATION_ID), eq(TEST_USER_ID), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("撤回消息")
    void recallMessage() {
        // Prepare
        when(messageRepository.recallMessage(eq(TEST_MESSAGE_ID), eq(TEST_USER_ID), any(LocalDateTime.class)))
                .thenReturn(1);

        // Act
        int result = messageRepository.recallMessage(TEST_MESSAGE_ID, TEST_USER_ID, LocalDateTime.now());

        // Assert
        assertThat(result).isEqualTo(1);
        verify(messageRepository, times(1)).recallMessage(eq(TEST_MESSAGE_ID), eq(TEST_USER_ID), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("发送者删除消息")
    void deleteBySender() {
        // Prepare
        when(messageRepository.deleteBySender(eq(TEST_MESSAGE_ID), any(LocalDateTime.class)))
                .thenReturn(1);

        // Act
        int result = messageRepository.deleteBySender(TEST_MESSAGE_ID, LocalDateTime.now());

        // Assert
        assertThat(result).isEqualTo(1);
        verify(messageRepository, times(1)).deleteBySender(eq(TEST_MESSAGE_ID), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("置顶消息")
    void pinMessage() {
        // Prepare
        when(messageRepository.pinMessage(eq(TEST_MESSAGE_ID), eq(TEST_USER_ID), any(LocalDateTime.class)))
                .thenReturn(1);

        // Act
        int result = messageRepository.pinMessage(TEST_MESSAGE_ID, TEST_USER_ID, LocalDateTime.now());

        // Assert
        assertThat(result).isEqualTo(1);
        verify(messageRepository, times(1)).pinMessage(eq(TEST_MESSAGE_ID), eq(TEST_USER_ID), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("收藏消息")
    void favoriteMessage() {
        // Prepare
        when(messageRepository.favoriteMessage(eq(TEST_MESSAGE_ID), eq(TEST_USER_ID), any(LocalDateTime.class)))
                .thenReturn(1);

        // Act
        int result = messageRepository.favoriteMessage(TEST_MESSAGE_ID, TEST_USER_ID, LocalDateTime.now());

        // Assert
        assertThat(result).isEqualTo(1);
        verify(messageRepository, times(1)).favoriteMessage(eq(TEST_MESSAGE_ID), eq(TEST_USER_ID), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("统计未读消息数")
    void countUnreadByReceiver() {
        // Prepare
        when(messageRepository.countUnreadByReceiver(TEST_USER_ID))
                .thenReturn(10L);

        // Act
        long result = messageRepository.countUnreadByReceiver(TEST_USER_ID);

        // Assert
        assertThat(result).isEqualTo(10L);
        verify(messageRepository, times(1)).countUnreadByReceiver(TEST_USER_ID);
    }

    @Test
    @DisplayName("编辑消息")
    void editMessage() {
        // Prepare
        String newContent = "Updated content";
        when(messageRepository.editMessage(eq(TEST_MESSAGE_ID), eq(newContent), any(LocalDateTime.class)))
                .thenReturn(1);

        // Act
        int result = messageRepository.editMessage(TEST_MESSAGE_ID, newContent, LocalDateTime.now());

        // Assert
        assertThat(result).isEqualTo(1);
        verify(messageRepository, times(1)).editMessage(eq(TEST_MESSAGE_ID), eq(newContent), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("保存消息")
    void save() {
        // Prepare
        Message message = new Message();
        message.setId(TEST_MESSAGE_ID);
        message.setContent("Test content");

        when(messageRepository.save(any(Message.class)))
                .thenReturn(message);

        // Act
        Message result = messageRepository.save(message);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(TEST_MESSAGE_ID);
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    @DisplayName("查询会话最新消息")
    void findLatestMessage() {
        // Prepare
        Pageable pageable = PageRequest.of(0, 1);
        Message message = new Message();
        message.setId(TEST_MESSAGE_ID);
        message.setCreatedAt(LocalDateTime.now());

        when(messageRepository.findLatestMessage(TEST_CONVERSATION_ID, TEST_USER_ID, pageable))
                .thenReturn(List.of(message));

        // Act
        List<Message> result = messageRepository.findLatestMessage(TEST_CONVERSATION_ID, TEST_USER_ID, pageable);

        // Assert
        assertThat(result).hasSize(1);
        verify(messageRepository, times(1)).findLatestMessage(TEST_CONVERSATION_ID, TEST_USER_ID, pageable);
    }
}
