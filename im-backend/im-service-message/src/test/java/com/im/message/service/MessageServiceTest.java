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

/**
 * 消息服务单元测试
 * 测试覆盖: 消息发送、撤回、已读、置顶、收藏等核心功能
 */
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
        savedMessage.setIsRecalled(false);
        savedMessage.setIsDeleted(false);
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
    @DisplayName("撤回已撤回的消息 - 失败")
    void recallMessage_AlreadyRecalled() {
        // Given
        savedMessage.setCreatedAt(LocalDateTime.now());
        savedMessage.setSenderId("user_001");
        savedMessage.setIsRecalled(true);
        when(messageRepository.findById("msg_001")).thenReturn(Optional.of(savedMessage));

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            messageService.recallMessage("msg_001", "user_001");
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
    @DisplayName("批量消息已读 - 成功")
    void markAsRead_Batch() {
        // Given
        java.util.List<String> messageIds = java.util.Arrays.asList("msg_001", "msg_002", "msg_003");
        
        Message msg2 = createMessage("msg_002");
        Message msg3 = createMessage("msg_003");
        
        when(messageRepository.findAllById(messageIds))
            .thenReturn(java.util.Arrays.asList(savedMessage, msg2, msg3));

        // When
        messageService.markAsReadBatch(messageIds, "user_002");

        // Then
        verify(messageRepository).saveAll(any());
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
        assertNotNull(savedMessage.getPinnedAt());
        verify(messageRepository).save(savedMessage);
    }

    @Test
    @DisplayName("取消置顶消息 - 成功")
    void unpinMessage() {
        // Given
        savedMessage.setIsPinned(true);
        when(messageRepository.findById("msg_001")).thenReturn(Optional.of(savedMessage));

        // When
        messageService.unpinMessage("msg_001", "user_001");

        // Then
        assertFalse(savedMessage.getIsPinned());
        assertNull(savedMessage.getPinnedAt());
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
        assertNotNull(savedMessage.getFavoritedAt());
        verify(messageRepository).save(savedMessage);
    }

    @Test
    @DisplayName("取消收藏消息 - 成功")
    void unfavoriteMessage() {
        // Given
        savedMessage.setIsFavorite(true);
        when(messageRepository.findById("msg_001")).thenReturn(Optional.of(savedMessage));

        // When
        messageService.unfavoriteMessage("msg_001", "user_001");

        // Then
        assertFalse(savedMessage.getIsFavorite());
        assertNull(savedMessage.getFavoritedAt());
        verify(messageRepository).save(savedMessage);
    }

    @Test
    @DisplayName("删除消息 - 成功")
    void deleteMessage() {
        // Given
        when(messageRepository.findById("msg_001")).thenReturn(Optional.of(savedMessage));

        // When
        messageService.deleteMessage("msg_001", "user_001");

        // Then
        assertTrue(savedMessage.getIsDeleted());
        assertNotNull(savedMessage.getDeletedAt());
        verify(messageRepository).save(savedMessage);
    }

    @Test
    @DisplayName("搜索消息 - 成功")
    void searchMessages() {
        // Given
        java.util.List<Message> messages = java.util.Arrays.asList(savedMessage);
        when(messageRepository.searchByContentContainingAndConversationId("Hello", "conv_001"))
            .thenReturn(messages);

        // When
        java.util.List<MessageResponse> results = messageService.searchMessages("Hello", "conv_001", null, null, 20);

        // Then
        assertEquals(1, results.size());
        assertEquals("Hello World", results.get(0).getContent());
    }

    @Test
    @DisplayName("发送引用回复消息 - 成功")
    void sendMessage_WithReply() {
        // Given
        validRequest.setReplyToMessageId("msg_000");
        validRequest.setReplyToSenderId("user_000");
        validRequest.setReplyToContent("Original message");

        when(messageRepository.existsByClientMessageId("client_001")).thenReturn(false);
        when(messageRepository.save(any(Message.class))).thenReturn(savedMessage);

        // When
        MessageResponse response = messageService.sendMessage(validRequest);

        // Then
        assertNotNull(response);
        verify(messageRepository).save(any(Message.class));
    }

    @Test
    @DisplayName("编辑消息 - 成功")
    void editMessage() {
        // Given
        savedMessage.setSenderId("user_001");
        when(messageRepository.findById("msg_001")).thenReturn(Optional.of(savedMessage));

        // When
        MessageResponse response = messageService.editMessage("msg_001", "user_001", "Edited content");

        // Then
        assertNotNull(response);
        assertEquals("Edited content", savedMessage.getContent());
        assertNotNull(savedMessage.getEditedAt());
        verify(messageRepository).save(savedMessage);
    }

    @Test
    @DisplayName("编辑非自己的消息 - 失败")
    void editMessage_NotSender() {
        // Given
        savedMessage.setSenderId("user_001");
        when(messageRepository.findById("msg_001")).thenReturn(Optional.of(savedMessage));

        // When & Then
        assertThrows(SecurityException.class, () -> {
            messageService.editMessage("msg_001", "user_002", "Edited content");
        });
    }

    private Message createMessage(String messageId) {
        Message message = new Message();
        message.setMessageId(messageId);
        message.setConversationId("conv_001");
        message.setSenderId("user_001");
        message.setContent("Test");
        message.setStatus(MessageStatus.DELIVERED);
        message.setCreatedAt(LocalDateTime.now());
        message.setIsRecalled(false);
        message.setIsDeleted(false);
        return message;
    }
}
