package com.im.service.message.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.im.service.message.dto.MessageResponse;
import com.im.service.message.dto.SendMessageRequest;
import com.im.service.message.entity.Message;
import com.im.service.message.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * MessageService 单元测试
 * 
 * 测试覆盖:
 * - 消息发送功能
 * - 消息撤回功能
 * - 消息搜索功能
 * - 已读回执功能
 * - 消息置顶/收藏功能
 * 
 * @author IM Team
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("消息服务单元测试")
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private MessageService messageService;

    private static final String TEST_MESSAGE_ID = "msg_123456";
    private static final String TEST_CONVERSATION_ID = "conv_789";
    private static final String TEST_SENDER_ID = "user_001";
    private static final String TEST_RECEIVER_ID = "user_002";
    private static final String TEST_CLIENT_MESSAGE_ID = "client_msg_001";

    @BeforeEach
    void setUp() {
        // 设置撤回超时时间为2分钟
        ReflectionTestUtils.setField(messageService, "recallTimeoutMinutes", 2);
    }

    // ========== 消息发送测试 ==========

    @Test
    @DisplayName("发送消息成功")
    void sendMessage_Success() {
        // Prepare - 准备测试数据
        SendMessageRequest request = new SendMessageRequest();
        request.setClientMessageId(TEST_CLIENT_MESSAGE_ID);
        request.setConversationId(TEST_CONVERSATION_ID);
        request.setSenderId(TEST_SENDER_ID);
        request.setReceiverId(TEST_RECEIVER_ID);
        request.setType("TEXT");
        request.setContent("Hello, World!");
        request.setConversationType("PRIVATE");

        Message savedMessage = new Message();
        savedMessage.setId(TEST_MESSAGE_ID);
        savedMessage.setClientMessageId(TEST_CLIENT_MESSAGE_ID);
        savedMessage.setContent("Hello, World!");
        savedMessage.setStatus("SENT");
        savedMessage.setCreatedAt(LocalDateTime.now());

        when(messageRepository.findByClientMessageId(TEST_CLIENT_MESSAGE_ID))
                .thenReturn(Optional.empty());
        when(messageRepository.save(any(Message.class)))
                .thenReturn(savedMessage);

        // Act - 执行被测方法
        MessageResponse response = messageService.sendMessage(request);

        // Assert - 验证结果
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(TEST_MESSAGE_ID);
        assertThat(response.getContent()).isEqualTo("Hello, World!");
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    @DisplayName("发送消息-重复客户端ID返回已存在消息")
    void sendMessage_DuplicateClientId() {
        // Prepare
        SendMessageRequest request = new SendMessageRequest();
        request.setClientMessageId(TEST_CLIENT_MESSAGE_ID);
        request.setContent("Hello, World!");

        Message existingMessage = new Message();
        existingMessage.setId(TEST_MESSAGE_ID);
        existingMessage.setClientMessageId(TEST_CLIENT_MESSAGE_ID);
        existingMessage.setContent("Existing Message");
        existingMessage.setStatus("SENT");
        existingMessage.setCreatedAt(LocalDateTime.now());

        when(messageRepository.findByClientMessageId(TEST_CLIENT_MESSAGE_ID))
                .thenReturn(Optional.of(existingMessage));

        // Act
        MessageResponse response = messageService.sendMessage(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(TEST_MESSAGE_ID);
        assertThat(response.getContent()).isEqualTo("Existing Message");
        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    @DisplayName("发送消息-带引用回复")
    void sendMessage_WithReply() throws Exception {
        // Prepare
        String replyToId = "msg_reply_001";
        SendMessageRequest request = new SendMessageRequest();
        request.setClientMessageId(TEST_CLIENT_MESSAGE_ID);
        request.setConversationId(TEST_CONVERSATION_ID);
        request.setSenderId(TEST_SENDER_ID);
        request.setReceiverId(TEST_RECEIVER_ID);
        request.setType("TEXT");
        request.setContent("Reply message");
        request.setReplyToId(replyToId);

        Message replyMessage = new Message();
        replyMessage.setId(replyToId);
        replyMessage.setSenderId("user_003");
        replyMessage.setContentSummary("Original message");

        Message savedMessage = new Message();
        savedMessage.setId(TEST_MESSAGE_ID);
        savedMessage.setReplyToId(replyToId);
        savedMessage.setCreatedAt(LocalDateTime.now());

        when(messageRepository.findByClientMessageId(TEST_CLIENT_MESSAGE_ID))
                .thenReturn(Optional.empty());
        when(messageRepository.findById(replyToId))
                .thenReturn(Optional.of(replyMessage));
        when(messageRepository.save(any(Message.class)))
                .thenReturn(savedMessage);

        // Act
        MessageResponse response = messageService.sendMessage(request);

        // Assert
        assertThat(response).isNotNull();
        verify(messageRepository, times(1)).findById(replyToId);
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    // ========== 消息撤回测试 ==========

    @Test
    @DisplayName("撤回消息成功")
    void recallMessage_Success() {
        // Prepare
        Message message = new Message();
        message.setId(TEST_MESSAGE_ID);
        message.setSenderId(TEST_SENDER_ID);
        message.setCreatedAt(LocalDateTime.now());
        message.setRecalled(false);

        when(messageRepository.findById(TEST_MESSAGE_ID))
                .thenReturn(Optional.of(message));
        when(messageRepository.recallMessage(eq(TEST_MESSAGE_ID), eq(TEST_SENDER_ID), any(LocalDateTime.class)))
                .thenReturn(1);

        // Act
        boolean result = messageService.recallMessage(TEST_MESSAGE_ID, TEST_SENDER_ID);

        // Assert
        assertThat(result).isTrue();
        verify(messageRepository, times(1)).recallMessage(eq(TEST_MESSAGE_ID), eq(TEST_SENDER_ID), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("撤回消息失败-超过2分钟超时")
    void recallMessage_Timeout() {
        // Prepare
        Message message = new Message();
        message.setId(TEST_MESSAGE_ID);
        message.setSenderId(TEST_SENDER_ID);
        message.setCreatedAt(LocalDateTime.now().minusMinutes(5)); // 5分钟前发送
        message.setRecalled(false);

        when(messageRepository.findById(TEST_MESSAGE_ID))
                .thenReturn(Optional.of(message));

        // Act
        boolean result = messageService.recallMessage(TEST_MESSAGE_ID, TEST_SENDER_ID);

        // Assert
        assertThat(result).isFalse();
        verify(messageRepository, never()).recallMessage(anyString(), anyString(), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("撤回消息失败-非发送者尝试撤回")
    void recallMessage_NotSender() {
        // Prepare
        Message message = new Message();
        message.setId(TEST_MESSAGE_ID);
        message.setSenderId(TEST_SENDER_ID);
        message.setCreatedAt(LocalDateTime.now());
        message.setRecalled(false);

        when(messageRepository.findById(TEST_MESSAGE_ID))
                .thenReturn(Optional.of(message));

        // Act - 其他用户尝试撤回
        boolean result = messageService.recallMessage(TEST_MESSAGE_ID, "other_user");

        // Assert
        assertThat(result).isFalse();
        verify(messageRepository, never()).recallMessage(anyString(), anyString(), any(LocalDateTime.class));
    }

    // ========== 消息搜索测试 ==========

    @Test
    @DisplayName("搜索消息-按内容")
    void searchMessages_ByContent() {
        // Prepare
        String keyword = "hello";
        Message message = new Message();
        message.setId(TEST_MESSAGE_ID);
        message.setContent("Hello, how are you?");
        message.setSenderDeleted(false);
        message.setReceiverDeleted(false);
        message.setSenderId(TEST_SENDER_ID);
        message.setReceiverId(TEST_RECEIVER_ID);

        when(messageRepository.searchMessages(TEST_CONVERSATION_ID, keyword, TEST_RECEIVER_ID))
                .thenReturn(List.of(message));

        // Act
        List<MessageResponse> results = messageService.searchMessages(TEST_CONVERSATION_ID, keyword, TEST_RECEIVER_ID);

        // Assert
        assertThat(results).isNotEmpty();
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getContent()).containsIgnoringCase("hello");
    }

    // ========== 已读回执测试 ==========

    @Test
    @DisplayName("标记单条消息已读")
    void markAsRead_Single() {
        // Prepare
        when(messageRepository.markAsRead(eq(TEST_MESSAGE_ID), any(LocalDateTime.class)))
                .thenReturn(1);

        // Act
        boolean result = messageService.markAsRead(TEST_MESSAGE_ID);

        // Assert
        assertThat(result).isTrue();
        verify(messageRepository, times(1)).markAsRead(eq(TEST_MESSAGE_ID), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("批量标记会话消息已读")
    void markAsRead_Batch() {
        // Prepare
        when(messageRepository.markConversationAsRead(eq(TEST_CONVERSATION_ID), eq(TEST_RECEIVER_ID), any(LocalDateTime.class)))
                .thenReturn(5);

        // Act
        int result = messageService.markConversationAsRead(TEST_CONVERSATION_ID, TEST_RECEIVER_ID);

        // Assert
        assertThat(result).isEqualTo(5);
        verify(messageRepository, times(1)).markConversationAsRead(eq(TEST_CONVERSATION_ID), eq(TEST_RECEIVER_ID), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("获取未读消息数量")
    void getUnreadCount() {
        // Prepare
        when(messageRepository.countUnreadByReceiver(TEST_RECEIVER_ID))
                .thenReturn(10L);

        // Act
        long count = messageService.getUnreadCount(TEST_RECEIVER_ID);

        // Assert
        assertThat(count).isEqualTo(10L);
        verify(messageRepository, times(1)).countUnreadByReceiver(TEST_RECEIVER_ID);
    }

    // ========== 消息置顶/收藏测试 ==========

    @Test
    @DisplayName("置顶消息成功")
    void pinMessage() {
        // Prepare
        when(messageRepository.pinMessage(eq(TEST_MESSAGE_ID), eq(TEST_SENDER_ID), any(LocalDateTime.class)))
                .thenReturn(1);

        // Act
        boolean result = messageService.pinMessage(TEST_MESSAGE_ID, TEST_SENDER_ID);

        // Assert
        assertThat(result).isTrue();
        verify(messageRepository, times(1)).pinMessage(eq(TEST_MESSAGE_ID), eq(TEST_SENDER_ID), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("收藏消息成功")
    void favoriteMessage() {
        // Prepare
        when(messageRepository.favoriteMessage(eq(TEST_MESSAGE_ID), eq(TEST_SENDER_ID), any(LocalDateTime.class)))
                .thenReturn(1);

        // Act
        boolean result = messageService.favoriteMessage(TEST_MESSAGE_ID, TEST_SENDER_ID);

        // Assert
        assertThat(result).isTrue();
        verify(messageRepository, times(1)).favoriteMessage(eq(TEST_MESSAGE_ID), eq(TEST_SENDER_ID), any(LocalDateTime.class));
    }
}
