package com.im.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 好友推荐响应DTO
 * 
 * @author IM Team
 * @since 2026-03-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "好友推荐响应")
public class FriendRecommendationResponse {

    @Schema(description = "用户ID", example = "1001")
    private Long userId;

    @Schema(description = "用户昵称", example = "张三")
    private String nickname;

    @Schema(description = "用户头像", example = "https://example.com/avatar.jpg")
    private String avatar;

    @Schema(description = "个性签名", example = "生活就像一盒巧克力")
    private String signature;

    @Schema(description = "推荐分数(0-1)", example = "0.85")
    private Double recommendationScore;

    @Schema(description = "推荐原因", example = "你们有5个共同好友")
    private String recommendationReason;

    @Schema(description = "推荐算法类型", example = "MUTUAL_FRIEND")
    private String algorithmType;

    @Schema(description = "共同好友数量", example = "5")
    private Integer mutualFriendCount;

    @Schema(description = "共同好友列表")
    private List<MutualFriendInfo> mutualFriends;

    @Schema(description = "共同群组数量", example = "3")
    private Integer commonGroupCount;

    @Schema(description = "共同兴趣标签")
    private List<String> commonInterestTags;

    @Schema(description = "匹配的兴趣标签数量", example = "4")
    private Integer matchedTagCount;

    @Schema(description = "是否在线", example = "true")
    private Boolean isOnline;

    @Schema(description = "用户等级", example = "5")
    private Integer userLevel;

    @Schema(description = "注册时间")
    private LocalDateTime registerTime;

    @Schema(description = "是否已发送好友请求", example = "false")
    private Boolean hasPendingRequest;

    @Schema(description = "推荐时间")
    private LocalDateTime recommendedAt;

    /**
     * 共同好友信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MutualFriendInfo {
        
        @Schema(description = "好友ID", example = "1002")
        private Long friendId;
        
        @Schema(description = "好友昵称", example = "李四")
        private String friendNickname;
        
        @Schema(description = "好友头像", example = "https://example.com/avatar2.jpg")
        private String friendAvatar;
    }
}
