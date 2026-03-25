package com.im.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DraftResponse {
    private Long id;
    private Long userId;
    private String conversationId;
    private String content;
    private String mentionIds;
    private String replyMessageId;
    private String messageType;
    private Long updatedAt;
}
