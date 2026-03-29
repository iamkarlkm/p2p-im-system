package com.im.backend.modules.poi.customer_service.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 用户-商家客服会话实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("poi_cs_session")
public class PoiCustomerServiceSession {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 会话ID(唯一)
     */
    private String sessionId;

    /**
     * POI商户ID
     */
    private Long poiId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 客服ID
     */
    private Long agentId;

    /**
     * 会话来源: POI_PAGE-POI页面, ORDER_DETAIL-订单详情, SEARCH-搜索, RECOMMEND-推荐
     */
    private String source;

    /**
     * 会话状态: PENDING-待分配, ACTIVE-进行中, CLOSED-已关闭, TRANSFERRED-已转接
     */
    private String status;

    /**
     * 咨询类型: CONSULT-商品咨询, ORDER-订单问题, AFTER_SALE-售后服务, COMPLAINT-投诉建议, OTHER-其他
     */
    private String inquiryType;

    /**
     * 关联订单ID
     */
    private Long relatedOrderId;

    /**
     * 会话主题/标题
     */
    private String subject;

    /**
     * 优先级: URGENT-紧急, HIGH-高, NORMAL-普通, LOW-低
     */
    private String priority;

    /**
     * 用户满意度评分(1-5)
     */
    private Integer userRating;

    /**
     * 用户评价内容
     */
    private String userComment;

    /**
     * 用户首次消息预览
     */
    private String firstMessagePreview;

    /**
     * 最后消息内容预览
     */
    private String lastMessagePreview;

    /**
     * 最后消息时间
     */
    private LocalDateTime lastMessageTime;

    /**
     * 最后消息发送者类型: USER-用户, AGENT-客服
     */
    private String lastMessageSender;

    /**
     * 用户未读消息数
     */
    private Integer userUnreadCount;

    /**
     * 客服未读消息数
     */
    private Integer agentUnreadCount;

    /**
     * 会话开始时间
     */
    private LocalDateTime startTime;

    /**
     * 会话结束时间
     */
    private LocalDateTime endTime;

    /**
     * 会话时长(秒)
     */
    private Integer duration;

    /**
     * 会话关闭原因: USER_CLOSE-用户关闭, AGENT_CLOSE-客服关闭, TIMEOUT-超时关闭, TRANSFER-转接关闭
     */
    private String closeReason;

    /**
     * 转接来源会话ID
     */
    private String transferFromSession;

    /**
     * 机器人会话标记
     */
    private Boolean robotHandled;

    /**
     * 机器人转人工标记
     */
    private Boolean robotTransferred;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
