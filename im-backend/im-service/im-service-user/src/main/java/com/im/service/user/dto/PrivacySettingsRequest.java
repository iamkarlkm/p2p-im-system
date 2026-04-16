package com.im.service.user.dto;

import lombok.Data;

/**
 * 隐私设置请求DTO
 */
@Data
public class PrivacySettingsRequest {

    private Boolean allowSearch;
    private String addFriendPermission;
    private Boolean allowPhoneSearch;
    private Boolean allowEmailSearch;
    private String onlineStatusVisibility;
    private String lastSeenVisibility;
    private String profileVisibility;
}
