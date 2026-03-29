package com.im.entity.customer_service;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 客服代理（人工客服）实体
 * 功能 #319 - 智能客服与工单管理系统
 */
@Data
public class CustomerServiceAgent {
    
    /** 客服ID */
    private Long id;
    
    /** 用户ID（关联系统用户） */
    private Long userId;
    
    /** 客服昵称 */
    private String nickname;
    
    /** 客服工号 */
    private String employeeNo;
    
    /** 客服头像 */
    private String avatar;
    
    /** 客服状态：0-离线 1-在线 2-忙碌 3-休息 */
    private Integer status;
    
    /** 最大会话数 */
    private Integer maxSessions;
    
    /** 当前会话数 */
    private Integer currentSessions;
    
    /** 服务等级：1-初级 2-中级 3-高级 4-专家 */
    private Integer serviceLevel;
    
    /** 服务技能（逗号分隔） */
    private String skills;
    
    /** 今日接待数 */
    private Integer todayReceiveCount;
    
    /** 今日解决数 */
    private Integer todayResolveCount;
    
    /** 平均响应时间（秒） */
    private Integer avgResponseTime;
    
    /** 满意度评分 */
    private Double satisfactionScore;
    
    /** 最后上线时间 */
    private LocalDateTime lastOnlineTime;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
    
    /** 是否删除 */
    private Integer deleted;
}
