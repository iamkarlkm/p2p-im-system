package com.im.backend.modules.local_life.review.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评价回复DTO
 * 
 * @author IM Development Team
 * @version 1.0
 */
@Data
public class ReviewReplyDTO {
    
    /** 回复ID */
    private Long replyId;
    
    /** 父回复ID */
    private Long parentReplyId;
    
    /** 用户信息 */
    private UserInfoDTO user;
    
    /** 回复内容 */
    private String content;
    
    /** 回复时间 */
    private LocalDateTime replyTime;
    
    /** 点赞数 */
    private Integer likeCount;
    
    /** 是否官方回复 */
    private Boolean officialReply;
    
    /** 是否商家回复 */
    private Boolean merchantReply;
    
    /** 商家名称 */
    private String merchantName;
    
    /**
     * 用户信息DTO
     */
    @Data
    public static class UserInfoDTO {
        private Long userId;
        private String nickname;
        private String avatar;
        private Integer level;
    }
}
