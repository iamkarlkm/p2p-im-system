package com.im.backend.modules.merchant.assistant.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 客服会话记录
 */
@Data
@TableName("customer_service_session")
public class CustomerServiceSession {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 商户ID
     */
    private Long merchantId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 客服ID(人工客服)
     */
    private Long agentId;
    
    /**
     * 会话状态: INIT-初始化, BOT-机器人服务, QUEUE-排队中, AGENT-人工服务, ENDED-已结束
     */
    private String sessionStatus;
    
    /**
     * 消息数量
     */
    private Integer messageCount;
    
    /**
     * 机器人解决: 0-否, 1-是
     */
    private Integer botResolved;
    
    /**
     * 用户评分
     */
    private Integer userRating;
    
    /**
     * 满意度评价
     */
    private String satisfaction;
    
    /**
     * 会话开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 转人工时间
     */
    private LocalDateTime transferTime;
    
    /**
     * 会话结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 会话来源: MINI_PROGRAM-小程序, APP-应用, WEB-网页
     */
    private String source;
    
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
