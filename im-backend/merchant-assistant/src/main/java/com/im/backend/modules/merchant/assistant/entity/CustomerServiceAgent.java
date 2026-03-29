package com.im.backend.modules.merchant.assistant.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 人工客服信息
 */
@Data
@TableName("customer_service_agent")
public class CustomerServiceAgent {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 商户ID
     */
    private Long merchantId;
    
    /**
     * 用户ID(系统用户)
     */
    private Long userId;
    
    /**
     * 客服昵称
     */
    private String nickname;
    
    /**
     * 客服头像
     */
    private String avatar;
    
    /**
     * 在线状态: ONLINE-在线, BUSY-忙碌, OFFLINE-离线
     */
    private String onlineStatus;
    
    /**
     * 最大接待数
     */
    private Integer maxSessions;
    
    /**
     * 当前接待数
     */
    private Integer currentSessions;
    
    /**
     * 服务评分
     */
    private Double rating;
    
    /**
     * 服务次数
     */
    private Integer serviceCount;
    
    /**
     * 是否启用
     */
    private Boolean enabled;
    
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
