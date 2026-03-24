package com.im.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Builder;

/**
 * 用户资料更新请求 DTO
 */
@Data
@Builder
public class UserProfileRequest {

    @Size(max = 50, message = "昵称最长50字符")
    private String nickname;

    @Size(max = 100, message = "姓名最长100字符")
    private String realName;

    @Size(max = 500, message = "头像URL最长500字符")
    private String avatarUrl;

    @Size(max = 500, message = "头像缩略图URL最长500字符")
    private String avatarThumbnailUrl;

    @Size(max = 200, message = "签名最长200字符")
    private String bio;

    private Integer gender;

    @Size(max = 10, message = "生日格式错误")
    private String birthday;

    @Size(max = 100, message = "国家最长100字符")
    private String country;

    @Size(max = 100, message = "省份最长100字符")
    private String province;

    @Size(max = 100, message = "城市最长100字符")
    private String city;

    @Size(max = 20, message = "语言代码最长20字符")
    private String language;

    @Size(max = 50, message = "时区最长50字符")
    private String timezone;

    @Size(max = 255, message = "网站URL最长255字符")
    private String website;

    @Size(max = 255, message = "邮箱最长255字符")
    private String email;

    @Size(max = 20, message = "手机号最长20字符")
    private String phone;

    // 隐私设置
    private String onlineStatusVisibility;
    private String lastSeenVisibility;
    private String avatarVisibility;
    private String profileVisibility;
    private String searchableBy;
    private String friendRequestPolicy;
    private Boolean readReceiptEnabled;
    private Boolean showOnlineStatus;
    private Boolean showTypingStatus;
}
