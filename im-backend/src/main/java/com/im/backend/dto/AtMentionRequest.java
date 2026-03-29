package com.im.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * @提及请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AtMentionRequest {

    /** 消息ID */
    @NotNull(message = "消息ID不能为空")
    private Long messageId;

    /** 被@提及的用户ID列表 */
    private List<Long> mentionedUserIds;

    /** 是否@所有人 */
    private Boolean isAtAll = false;

    /** 会话ID */
    private String conversationId;

    /** 群聊房间ID（0表示单聊） */
    private Long roomId;

    /** 发送者用户ID */
    @NotNull(message = "发送者ID不能为空")
    private Long senderUserId;
}
