package com.im.backend.modules.local_life.review.entity;

import com.im.backend.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 评价点赞实体
 * 
 * @author IM Development Team
 * @version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ReviewLike extends BaseEntity {
    
    /** 点赞ID */
    private Long likeId;
    
    /** 评价ID */
    private Long reviewId;
    
    /** 用户ID */
    private Long userId;
    
    /** 点赞时间 */
    private LocalDateTime likeTime;
    
    /** 是否取消 */
    private Boolean cancelled;
    
    /** 取消时间 */
    private LocalDateTime cancelTime;
}
