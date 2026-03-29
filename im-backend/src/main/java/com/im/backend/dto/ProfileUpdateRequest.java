package com.im.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * 用户资料更新请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequest {

    private String nickname;

    private String avatarUrl;

    private String bio;

    private Integer gender;

    private LocalDateTime birthday;

    private String email;

    private String country;

    private String city;

    private String language;

    private String timezone;
}
