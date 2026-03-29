package com.im.entity.poiim;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 商家客服信息实体
 */
@Data
public class MerchantAgent {
    
    /** 客服ID */
    private String agentId;
    
    /** 所属商家ID */
    private String merchantId;
    
    /** 关联POI IDs(一个客服可服务多个门店) */
    private List<String> poiIds;
    
    /** 客服名称 */
    private String agentName;
    
    /** 客服头像 */
    private String avatarUrl;
    
    /** 在线状态: ONLINE-在线, BUSY-忙碌, OFFLINE-离线, AWAY-离开 */
    private String onlineStatus;
    
    /** 最大并发会话数 */
    private Integer maxConcurrentSessions;
    
    /** 当前会话数 */
    private Integer currentSessionCount;
    
    /** 技能标签(用于智能分配) */
    private List<String> skillTags;
    
    /** 服务评分(1-5分) */
    private Double rating;
    
    /** 服务次数 */
    private Integer totalServiceCount;
    
    /** 平均响应时间(秒) */
    private Integer avgResponseTime;
    
    /** 满意度 */
    private Double satisfactionRate;
    
    /** 最后活跃时间 */
    private LocalDateTime lastActiveTime;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 扩展配置 */
    private Map<String, Object> config;
}
