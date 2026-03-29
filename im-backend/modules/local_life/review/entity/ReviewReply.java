package com.im.backend.modules.local_life.review.entity;

import com.im.backend.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 评价回复实体
 * 支持商家回复和用户互动
 * 
 * @author IM Development Team
 * @version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ReviewReply extends BaseEntity {
    
    /** 回复ID */
    private Long replyId;
    
    /** 评价ID */
    private Long reviewId;
    
    /** 父回复ID (支持二级回复) */
    private Long parentReplyId;
    
    /** 回复用户ID */
    private Long userId;
    
    /** 回复用户类型: USER/MERCHANT/ADMIN */
    private String userType;
    
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
    
    /** 商家ID (如果是商家回复) */
    private Long merchantId;
    
    /** 是否可见 */
    private Boolean visible;
    
    /** 是否被屏蔽 */
    private Boolean blocked;
}
