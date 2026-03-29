package com.im.dto;

import com.im.entity.UserProfileEntity;
import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 用户资料 DTO - 对外暴露的用户资料数据
 */
@Data
@Builder
public class UserProfileDTO {

    private String userId;
    private String nickname;
    private String realName;
    private String avatarUrl;
    private String avatarThumbnailUrl;
    private String bio;
    private Integer gender;
    private String birthday;
    private String country;
    private String province;
    private String city;
    private String language;
    private String timezone;
    private String website;
    private String email;
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

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UserProfileDTO fromEntity(UserProfileEntity entity) {
        return UserProfileDTO.builder()
                .userId(entity.getUserId())
                .nickname(entity.getNickname())
                .realName(entity.getRealName())
                .avatarUrl(entity.getAvatarUrl())
                .avatarThumbnailUrl(entity.getAvatarThumbnailUrl())
                .bio(entity.getBio())
                .gender(entity.getGender())
                .birthday(entity.getBirthday())
                .country(entity.getCountry())
                .province(entity.getProvince())
                .city(entity.getCity())
                .language(entity.getLanguage())
                .timezone(entity.getTimezone())
                .website(entity.getWebsite())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .onlineStatusVisibility(entity.getOnlineStatusVisibility())
                .lastSeenVisibility(entity.getLastSeenVisibility())
                .avatarVisibility(entity.getAvatarVisibility())
                .profileVisibility(entity.getProfileVisibility())
                .searchableBy(entity.getSearchableBy())
                .friendRequestPolicy(entity.getFriendRequestPolicy())
                .readReceiptEnabled(entity.getReadReceiptEnabled())
                .showOnlineStatus(entity.getShowOnlineStatus())
                .showTypingStatus(entity.getShowTypingStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /** 公开资料（最小化字段，用于陌生人查看） */
    public static UserProfileDTO publicProfile(UserProfileEntity entity) {
        return UserProfileDTO.builder()
                .userId(entity.getUserId())
                .nickname(entity.getNickname())
                .avatarUrl(entity.getAvatarUrl())
                .avatarThumbnailUrl(entity.getAvatarThumbnailUrl())
                .bio(entity.getBio())
                .country(entity.getCountry())
                .build();
    }
}
