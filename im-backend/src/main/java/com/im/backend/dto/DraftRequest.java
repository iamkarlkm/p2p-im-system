package com.im.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DraftRequest {
    private Long userId;
    private String conversationId;
    private String content;
    private String mentionIds;
    private String replyMessageId;
    private String messageType;
}
