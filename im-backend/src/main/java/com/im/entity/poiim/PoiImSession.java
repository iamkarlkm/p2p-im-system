package com.im.entity.poiim;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * POI智能客服会话实体
 * 记录用户与商家客服的IM会话
 */
@Data
public class PoiImSession {
    
    /** 会话ID */
    private String sessionId;
    
    /** 关联POI ID */
    private String poiId;
    
    /** POI名称 */
    private String poiName;
    
    /** 商家ID */
    private String merchantId;
    
    /** 用户ID */
    private String userId;
    
    /** 用户昵称 */
    private String userNickname;
    
    /** 用户头像 */
    private String userAvatar;
    
    /** 当前接待客服ID */
    private String currentAgentId;
    
    /** 客服名称 */
    private String agentName;
    
    /** 会话状态: PENDING-待分配, ACTIVE-进行中, CLOSED-已关闭, TRANSFERRED-已转接 */
    private String sessionStatus;
    
    /** 会话来源: POI_PAGE-POI详情页, FENCE_TRIGGER-围栏触发, QR_CODE-扫码, MINI_APP-小程序 */
    private String source;
    
    /** 用户地理位置(围栏触发时记录) */
    private Double userLongitude;
    
    /** 用户纬度 */
    private Double userLatitude;
    
    /** 距离POI距离(米) */
    private Double distanceToPoi;
    
    /** 最后一条消息ID */
    private String lastMessageId;
    
    /** 最后消息时间 */
    private LocalDateTime lastMessageTime;
    
    /** 未读消息数 */
    private Integer unreadCount;
    
    /** 用户提问分类标签 */
    private List<String> queryTags;
    
    /** 会话标签(用于分析) */
    private List<String> sessionTags;
    
    /** 满意度评分(1-5) */
    private Integer satisfactionRating;
    
    /** 满意度评价内容 */
    private String satisfactionComment;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 关闭时间 */
    private LocalDateTime closeTime;
    
    /** 扩展数据 */
    private Map<String, Object> extraData;
}
