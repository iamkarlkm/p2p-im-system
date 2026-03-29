package com.im.entity.poiim;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * POI客服消息实体
 */
@Data
public class PoiImMessage {
    
    /** 消息ID */
    private String messageId;
    
    /** 会话ID */
    private String sessionId;
    
    /** 发送者ID */
    private String senderId;
    
    /** 发送者类型: USER-用户, AGENT-客服, SYSTEM-系统, BOT-机器人 */
    private String senderType;
    
    /** 发送者名称 */
    private String senderName;
    
    /** 发送者头像 */
    private String senderAvatar;
    
    /** 消息类型: TEXT-文本, IMAGE-图片, VOICE-语音, VIDEO-视频, LOCATION-位置, CARD-卡片, RICH-富文本 */
    private String messageType;
    
    /** 消息内容 */
    private String content;
    
    /** 媒体URL(图片/语音/视频) */
    private String mediaUrl;
    
    /** 卡片数据(商品/优惠券/预约卡片) */
    private Map<String, Object> cardData;
    
    /** 位置数据 */
    private Map<String, Object> locationData;
    
    /** 消息状态: SENDING-发送中, SENT-已发送, DELIVERED-已送达, READ-已读, FAILED-失败 */
    private String messageStatus;
    
    /** 是否是自动回复 */
    private Boolean autoReply;
    
    /** 自动回复触发关键词 */
    private String triggerKeyword;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
}
