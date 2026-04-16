package com.im.service.user.dto;

import lombok.Data;

/**
 * 用户更新请求DTO
 */
@Data
public class UserUpdateRequest {

    private String nickname;
    private String avatarUrl;
    private String phone;
    private String email;
    private String gender;
    private String birthday;
    private String signature;
    private String location;
    private String tags;
}
