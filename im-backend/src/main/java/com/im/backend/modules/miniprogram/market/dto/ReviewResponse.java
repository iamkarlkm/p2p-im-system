package com.im.backend.modules.miniprogram.market.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论响应DTO
 */
@Data
public class ReviewResponse {

    private Long id;
    private Long appId;
    private Long userId;

    /**
     * 用户信息
     */
    private UserInfo user;

    private Integer rating;
    private String content;
    private List<String> images;
    private Integer likeCount;

    /**
     * 开发者回复
     */
    private String developerReply;
    private LocalDateTime replyTime;

    private LocalDateTime createTime;

    @Data
    public static class UserInfo {
        private Long id;
        private String nickname;
        private String avatar;
    }
}
