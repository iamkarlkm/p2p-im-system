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
public class MessageSyncItem {

    private Long messageId;
    private Long conversationId;
    private Long senderId;
    private String content;
    private String contentType;
    private LocalDateTime sentAt;
    private Boolean deleted;
    private String syncAction;
}
