package com.im.entity.fencemessage;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 场景化消息触发记录实体
 * 记录每次围栏触发消息的详细日志
 */
@Data
public class FenceMessageTrigger {
    
    /** 触发记录ID */
    private String triggerId;
    
    /** 围栏ID */
    private String fenceId;
    
    /** 消息模板ID */
    private String templateId;
    
    /** 用户ID */
    private String userId;
    
    /** 触发场景: ENTER-进入, DWELL-停留, EXIT-离开, SCHEDULE-定时 */
    private String triggerScene;
    
    /** 触发时的用户经度 */
    private Double triggerLongitude;
    
    /** 触发时的用户纬度 */
    private Double triggerLatitude;
    
    /** 触发时距离围栏中心距离(米) */
    private Double triggerDistance;
    
    /** 消息标题(实际渲染后) */
    private String messageTitle;
    
    /** 消息内容(实际渲染后) */
    private String messageContent;
    
    /** 实际卡片数据 */
    private Map<String, Object> cardData;
    
    /** 推送渠道 */
    private String pushChannel;
    
    /** 触发时间 */
    private LocalDateTime triggerTime;
    
    /** 发送时间 */
    private LocalDateTime sendTime;
    
    /** 送达时间 */
    private LocalDateTime deliveryTime;
    
    /** 读取时间 */
    private LocalDateTime readTime;
    
    /** 发送状态: PENDING-待发送, SENDING-发送中, SENT-已发送, DELIVERED-已送达, READ-已读, FAILED-失败, SKIPPED-已跳过(去重) */
    private String sendStatus;
    
    /** 失败原因 */
    private String failReason;
    
    /** 是否去重跳过 */
    private Boolean dedupSkipped;
    
    /** 去重Key */
    private String dedupKey;
    
    /** 是否命中AB测试 */
    private Boolean abTestHit;
    
    /** AB测试分组 */
    private String abTestGroup;
    
    /** 扩展数据 */
    private Map<String, Object> extraData;
    
    /** 创建时间 */
    private LocalDateTime createTime;
}
