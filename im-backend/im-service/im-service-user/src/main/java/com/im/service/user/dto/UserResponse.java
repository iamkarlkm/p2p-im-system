package com.im.service.user.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户响应DTO
 */
@Data
public class UserResponse {

    private Long id;
    private String username;
    private String nickname;
    private String avatarUrl;
    private String phone;
    private String email;
    private String userType;
    private Integer status;
    private String gender;
    private LocalDateTime birthday;
    private String signature;
    private String location;
    private List<String> tags;
    private String onlineStatus;
    private LocalDateTime lastOnlineAt;
    private Boolean allowSearch;
    private String addFriendPermission;
    private Boolean allowPhoneSearch;
    private Boolean allowEmailSearch;
    private String onlineStatusVisibility;
    private String lastSeenVisibility;
    private String profileVisibility;
    private LocalDateTime createdAt;
    private Boolean isFriend;
    private String friendRemark;
}
