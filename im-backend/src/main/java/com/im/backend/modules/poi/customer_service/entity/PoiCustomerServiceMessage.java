package com.im.backend.modules.poi.customer_service.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 客服会话消息实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("poi_cs_message")
public class PoiCustomerServiceMessage {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 消息ID(全局唯一)
     */
    private String messageId;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 发送者ID
     */
    private Long senderId;

    /**
     * 发送者类型: USER-用户, AGENT-客服, ROBOT-机器人, SYSTEM-系统
     */
    private String senderType;

    /**
     * 发送者名称
     */
    private String senderName;

    /**
     * 发送者头像
     */
    private String senderAvatar;

    /**
     * 消息类型: TEXT-文本, IMAGE-图片, VOICE-语音, VIDEO-视频, FILE-文件, 
     * LOCATION-位置, PRODUCT-商品卡片, ORDER-订单卡片, FAQ-常见问题
     */
    private String messageType;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息内容(JSON格式,包含图片URL、语音时长等)
     */
    private String contentExtra;

    /**
     * 引用消息ID
     */
    private String quoteMessageId;

    /**
     * 消息状态: SENDING-发送中, SENT-已发送, DELIVERED-已送达, READ-已读, FAILED-发送失败
     */
    private String status;

    /**
     * 是否机器人发送
     */
    private Boolean robotSent;

    /**
     * 机器人回复置信度(0-100)
     */
    private Integer robotConfidence;

    /**
     * 匹配的知识库FAQ ID
     */
    private Long matchedFaqId;

    /**
     * 已读时间
     */
    private LocalDateTime readTime;

    /**
     * 已读标记
     */
    private Boolean read;

    /**
     * 撤回时间
     */
    private LocalDateTime recallTime;

    /**
     * 是否已撤回
     */
    private Boolean recalled;

    /**
     * 客户端消息ID(用于幂等)
     */
    private String clientMessageId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
