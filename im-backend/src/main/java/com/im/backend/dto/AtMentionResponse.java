package com.im.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * @提及响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AtMentionResponse {

    private Long id;
    private Long messageId;
    private Long senderUserId;
    private String senderNickname;
    private Long mentionedUserId;
    private Long roomId;
    private Boolean isRead;
    private Boolean isAtAll;
    private Boolean notified;
    private LocalDateTime mentionedAt;
    private String messagePreview;
    private String conversationId;
    private String roomName;
}
