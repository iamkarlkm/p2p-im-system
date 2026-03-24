package com.im.server.dto;

import lombok.Data;

/**
 * 发送消息请求
 */
@Data
public class SendMessageRequest {
    private Long toUserId;
    private Integer chatType; // 1:私聊 2:群聊
    private Long chatId;
    private Integer msgType; // 1:文本 2:图片 3:文件 4:语音
    private String content;
}
