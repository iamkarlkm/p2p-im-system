package com.im.dto;

import lombok.Data;

/**
 * 创建单聊会话请求DTO
 * 功能ID: #6
 */
@Data
public class CreatePrivateChatRequest {
    private String userId1;
    private String userId2;
}
