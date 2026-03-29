package com.im.backend.modules.local_life.review.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 评价详情响应DTO
 * 
 * @author IM Development Team
 * @version 1.0
 */
@Data
public class ReviewDetailDTO {
    
    /** 评价ID */
    private Long reviewId;
    
    /** 商户ID */
    private Long merchantId;
    
    /** 用户信息 */
    private UserBriefDTO user;
    
    /** 总体评分 */
    private Integer overallRating;
    
    /** 口味评分 */
    private Integer tasteRating;
    
    /** 环境评分 */
    private Integer environmentRating;
    
    /** 服务评分 */
    private Integer serviceRating;
    
    /** 性价比评分 */
    private Integer valueRating;
    
    /** 评价内容 */
    private String content;
    
    /** 评价图片列表 */
    private List<String> images;
    
    /** 评价视频 */
    private VideoInfoDTO video;
    
    /** 消费金额 */
    private BigDecimal consumeAmount;
    
    /** 人均消费 */
    private BigDecimal perCapita;
    
    /** 是否匿名 */
    private Boolean anonymous;
    
    /** 是否推荐 */
    private Boolean recommended;
    
    /** 点赞数 */
    private Integer likeCount;
    
    /** 回复数 */
    private Integer replyCount;
    
    /** 当前用户是否已点赞 */
    private Boolean hasLiked;
    
    /** 情感分析结果 */
    private String sentiment;
    
    /** 是否优质评价 */
    private Boolean highQuality;
    
    /** 评价时间 */
    private LocalDateTime reviewTime;
    
    /** 就餐日期 */
    private LocalDateTime diningDate;
    
    /** 商家回复 */
    private MerchantReplyDTO merchantReply;
    
    /** 评价标签 */
    private List<String> tags;
    
    /** 评价来源 */
    private String source;
    
    /**
     * 用户信息简要DTO
     */
    @Data
    public static class UserBriefDTO {
        private Long userId;
        private String nickname;
        private String avatar;
        private Integer level;
    }
    
    /**
     * 视频信息DTO
     */
    @Data
    public static class VideoInfoDTO {
        private String videoUrl;
        private String coverUrl;
        private Integer duration;
    }
    
    /**
     * 商家回复DTO
     */
    @Data
    public static class MerchantReplyDTO {
        private String content;
        private LocalDateTime replyTime;
    }
}
