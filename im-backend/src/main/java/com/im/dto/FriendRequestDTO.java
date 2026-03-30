package com.im.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 好友申请DTO
 * 功能ID: #5
 */
@Data
public class FriendRequestDTO {
    private String id;
    private String fromUserId;
    private String toUserId;
    private String fromUsername;
    private String fromNickname;
    private String fromAvatar;
    private String message;
    private Integer status;
    private LocalDateTime createdAt;
}
