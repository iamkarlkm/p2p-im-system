package com.im.message.service;

import com.im.service.message.dto.ReactionRequest;
import com.im.service.message.dto.ReactionResponse;
import com.im.service.message.entity.MessageReaction;
import com.im.service.message.repository.MessageReactionRepository;
import com.im.service.message.service.MessageReactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 消息反应服务单元测试
 * 
 * @author IM Team
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("消息反应服务测试")
class MessageReactionServiceTest {

    @Mock
    private MessageReactionRepository reactionRepository;

    @InjectMocks
    private MessageReactionService reactionService;

    private String userId;
    private String messageId;
    private String reactionType;
    private MessageReaction mockReaction;

    @BeforeEach
    void setUp() {
        userId = "user-123";
        messageId = "msg-456";
        reactionType = "👍";
        
        mockReaction = new MessageReaction();
        mockReaction.setId("reaction-789");
        mockReaction.setMessageId(messageId);
        mockReaction.setUserId(userId);
        mockReaction.setReactionType(reactionType);
        mockReaction.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("添加消息反应-成功")
    void addReaction_Success() {
        // Given
        ReactionRequest request = ReactionRequest.builder()
                .messageId(messageId)
                .reactionType(reactionType)
                .build();

        when(reactionRepository.findByMessageIdAndUserIdAndReactionType(messageId, userId, reactionType))
                .thenReturn(Optional.empty());
        when(reactionRepository.findByMessageIdAndUserId(messageId, userId))
                .thenReturn(Optional.empty());
        when(reactionRepository.save(any(MessageReaction.class)))
                .thenReturn(mockReaction);

        // When
        ReactionResponse response = reactionService.addReaction(userId, request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getMessageId()).isEqualTo(messageId);
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getReactionType()).isEqualTo(reactionType);
        assertThat(response.getIsCurrentUser()).isTrue();
        verify(reactionRepository).save(any(MessageReaction.class));
    }

    @Test
    @DisplayName("添加消息反应-重复反应返回已有")
    void addReaction_Duplicate_ReturnsExisting() {
        // Given
        ReactionRequest request = ReactionRequest.builder()
                .messageId(messageId)
                .reactionType(reactionType)
                .build();

        when(reactionRepository.findByMessageIdAndUserIdAndReactionType(messageId, userId, reactionType))
                .thenReturn(Optional.of(mockReaction));

        // When
        ReactionResponse response = reactionService.addReaction(userId, request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("reaction-789");
        verify(reactionRepository, never()).save(any(MessageReaction.class));
    }

    @Test
    @DisplayName("添加消息反应-切换反应类型")
    void addReaction_SwitchType_DeletesOld() {
        // Given
        String newReactionType = "❤️";
        ReactionRequest request = ReactionRequest.builder()
                .messageId(messageId)
                .reactionType(newReactionType)
                .build();

        MessageReaction oldReaction = new MessageReaction();
        oldReaction.setId("reaction-old");
        oldReaction.setMessageId(messageId);
        oldReaction.setUserId(userId);
        oldReaction.setReactionType("👍");

        when(reactionRepository.findByMessageIdAndUserIdAndReactionType(messageId, userId, newReactionType))
                .thenReturn(Optional.empty());
        when(reactionRepository.findByMessageIdAndUserId(messageId, userId))
                .thenReturn(Optional.of(oldReaction));
        when(reactionRepository.save(any(MessageReaction.class)))
                .thenReturn(mockReaction);

        // When
        reactionService.addReaction(userId, request);

        // Then
        verify(reactionRepository).delete(oldReaction);
        verify(reactionRepository).save(any(MessageReaction.class));
    }

    @Test
    @DisplayName("移除消息反应-成功")
    void removeReaction_Success() {
        // Given
        when(reactionRepository.findByMessageIdAndUserIdAndReactionType(messageId, userId, reactionType))
                .thenReturn(Optional.of(mockReaction));

        // When
        reactionService.removeReaction(userId, messageId, reactionType);

        // Then
        verify(reactionRepository).delete(mockReaction);
    }

    @Test
    @DisplayName("移除消息反应-不存在")
    void removeReaction_NotExists_DoesNothing() {
        // Given
        when(reactionRepository.findByMessageIdAndUserIdAndReactionType(messageId, userId, reactionType))
                .thenReturn(Optional.empty());

        // When
        reactionService.removeReaction(userId, messageId, reactionType);

        // Then
        verify(reactionRepository, never()).delete(any());
    }

    @Test
    @DisplayName("获取消息反应列表-成功")
    void getReactionsByMessageId_Success() {
        // Given
        MessageReaction reaction1 = new MessageReaction();
        reaction1.setId("r1");
        reaction1.setMessageId(messageId);
        reaction1.setUserId(userId);
        reaction1.setReactionType("👍");
        reaction1.setCreatedAt(LocalDateTime.now());

        MessageReaction reaction2 = new MessageReaction();
        reaction2.setId("r2");
        reaction2.setMessageId(messageId);
        reaction2.setUserId("user-456");
        reaction2.setReactionType("❤️");
        reaction2.setCreatedAt(LocalDateTime.now());

        when(reactionRepository.findByMessageId(messageId))
                .thenReturn(Arrays.asList(reaction1, reaction2));

        // When
        List<ReactionResponse> responses = reactionService.getReactionsByMessageId(messageId, userId);

        // Then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getIsCurrentUser()).isTrue();
        assertThat(responses.get(1).getIsCurrentUser()).isFalse();
    }

    @Test
    @DisplayName("获取消息反应统计-成功")
    void getReactionStats_Success() {
        // Given
        List<Object[]> stats = Arrays.asList(
                new Object[]{"👍", 5L},
                new Object[]{"❤️", 3L},
                new Object[]{"😂", 2L}
        );
        when(reactionRepository.countByReactionTypeGroup(messageId)).thenReturn(stats);
        when(reactionRepository.countByMessageId(messageId)).thenReturn(10L);

        // When
        Map<String, Object> result = reactionService.getReactionStats(messageId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.get("messageId")).isEqualTo(messageId);
        assertThat(result.get("totalReactions")).isEqualTo(10L);
        assertThat(result.get("reactionsByType")).isNotNull();
    }
}
