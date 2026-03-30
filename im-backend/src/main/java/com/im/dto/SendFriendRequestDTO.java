package com.im.dto;

import lombok.Data;

/**
 * 发送好友申请请求DTO
 * 功能ID: #5
 */
@Data
public class SendFriendRequestDTO {
    private String fromUserId;
    private String toUserId;
    private String message;
}
