package com.im.dto;

import com.im.entity.MessageExpirationEntity;
import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 消息过期策略 DTO
 */
@Data
@Builder
public class MessageExpirationDTO {

    private Long id;
    private String conversationId;
    private String senderId;
    private String receiverId;
    private String expirationType;
    private Long durationSeconds;
    private LocalDateTime expireAt;
    private Boolean enabled;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MessageExpirationDTO fromEntity(MessageExpirationEntity entity) {
        return MessageExpirationDTO.builder()
                .id(entity.getId())
                .conversationId(entity.getConversationId())
                .senderId(entity.getSenderId())
                .receiverId(entity.getReceiverId())
                .expirationType(entity.getExpirationType())
                .durationSeconds(entity.getDurationSeconds())
                .expireAt(entity.getExpireAt())
                .enabled(entity.getEnabled())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
