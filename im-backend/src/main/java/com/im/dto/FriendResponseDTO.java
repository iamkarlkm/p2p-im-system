package com.im.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 好友响应DTO
 * 功能ID: #5
 */
@Data
public class FriendResponseDTO {
    private String id;
    private String username;
    private String nickname;
    private String avatar;
    private LocalDateTime friendSince;
    private boolean isFriend;
}
