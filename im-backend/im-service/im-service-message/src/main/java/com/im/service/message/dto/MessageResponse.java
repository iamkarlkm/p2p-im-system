package com.im.service.message.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 消息响应 DTO
 * 
 * @author IM Team
 * @version 1.0
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)  // 只包含非null字段
public class MessageResponse {

    /**
     * 消息ID
     */
    private String id;

    /**
     * 客户端消息ID
     */
    private String clientMessageId;

    /**
     * 会话ID
     */
    private String conversationId;

    /**
     * 会话类型: PRIVATE, GROUP, CHANNEL
     */
    private String conversationType;

    /**
     * 发送者ID
     */
    private String senderId;

    /**
     * 发送者信息 - 包含昵称、头像等
     */
    private Map<String, Object> senderInfo;

    /**
     * 接收者ID
     */
    private String receiverId;

    /**
     * 消息类型: TEXT, IMAGE, FILE, VOICE, VIDEO, LOCATION, SYSTEM, CUSTOM
     */
    private String type;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 内容摘要
     */
    private String contentSummary;

    /**
     * 消息状态: SENDING, SENT, DELIVERED, READ, FAILED, RECALLED
     */
    private String status;

    /**
     * 发送序号
     */
    private Long sequence;

    // ========== 删除相关字段 ==========

    /**
     * 发送者是否删除
     */
    private Boolean senderDeleted;

    /**
     * 接收者是否删除
     */
    private Boolean receiverDeleted;

    // ========== 撤回相关字段 ==========

    /**
     * 是否已撤回
     */
    private Boolean recalled;

    /**
     * 撤回时间
     */
    private LocalDateTime recalledAt;

    /**
     * 撤回者ID
     */
    private String recalledBy;

    // ========== 收藏相关字段 ==========

    /**
     * 是否已收藏
     */
    private Boolean favorited;

    /**
     * 收藏时间
     */
    private LocalDateTime favoritedAt;

    // ========== 置顶相关字段 ==========

    /**
     * 是否置顶
     */
    private Boolean pinned;

    /**
     * 置顶时间
     */
    private LocalDateTime pinnedAt;

    /**
     * 置顶者ID
     */
    private String pinnedBy;

    // ========== 引用/回复相关字段 ==========

    /**
     * 引用消息ID
     */
    private String replyToId;

    /**
     * 引用消息发送者ID
     */
    private String replyToSenderId;

    /**
     * 引用消息内容摘要
     */
    private String replyToContentSummary;

    /**
     * 引用消息信息 - 完整信息
     */
    private Map<String, Object> replyToMessage;

    /**
     * 根消息ID - 用于消息链
     */
    private String rootMessageId;

    // ========== 媒体/附件相关字段 ==========

    /**
     * 附件列表
     */
    private List<AttachmentResponse> attachments;

    /**
     * 附件数量
     */
    private Integer attachmentCount;

    /**
     * 文件大小(字节)
     */
    private Long fileSize;

    /**
     * 文件类型
     */
    private String mimeType;

    /**
     * 文件URL
     */
    private String fileUrl;

    /**
     * 缩略图URL
     */
    private String thumbnailUrl;

    // ========== 位置相关字段 ==========

    /**
     * 位置信息
     */
    private LocationResponse location;

    // ========== 已读相关字段 ==========

    /**
     * 已读人数 - 群聊使用
     */
    private Integer readCount;

    /**
     * 未读人数 - 群聊使用
     */
    private Integer unreadCount;

    /**
     * 已读成员列表
     */
    private List<Map<String, Object>> readMembers;

    // ========== 安全/加密相关字段 ==========

    /**
     * 是否加密消息
     */
    private Boolean encrypted;

    /**
     * 加密类型
     */
    private String encryptionType;

    /**
     * 是否阅后即焚
     */
    private Boolean selfDestruct;

    /**
     * 阅后即焚倒计时(秒)
     */
    private Integer selfDestructTime;

    /**
     * 是否已被销毁
     */
    private Boolean destroyed;

    /**
     * 销毁时间
     */
    private LocalDateTime destroyedAt;

    // ========== 编辑相关字段 ==========

    /**
     * 是否已编辑
     */
    private Boolean edited;

    /**
     * 编辑时间
     */
    private LocalDateTime editedAt;

    // ========== 表情相关字段 ==========

    /**
     * 表情回应列表
     */
    private List<ReactionResponse> reactions;

    /**
     * @提及的用户ID列表
     */
    private List<String> mentions;

    /**
     * 是否@所有人
     */
    private Boolean mentionAll;

    /**
     * 是否被@提及(当前用户)
     */
    private Boolean mentioned;

    // ========== 时间相关字段 ==========

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 发送时间
     */
    private LocalDateTime sentAt;

    /**
     * 送达时间
     */
    private LocalDateTime deliveredAt;

    /**
     * 已读时间
     */
    private LocalDateTime readAt;

    // ========== 扩展数据字段 ==========

    /**
     * 扩展数据
     */
    private Map<String, Object> extraData;

    // ========== 内部响应类 ==========

    /**
     * 附件响应
     */
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AttachmentResponse {
        private String id;
        private String type;
        private String name;
        private String url;
        private String thumbnailUrl;
        private Long size;
        private String mimeType;
        private Integer width;
        private Integer height;
        private Integer duration;
        private Map<String, Object> extra;
    }

    /**
     * 位置响应
     */
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class LocationResponse {
        private Double latitude;
        private Double longitude;
        private String address;
        private String name;
        private Integer zoom;
    }

    /**
     * 表情回应响应
     */
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ReactionResponse {
        /**
         * 表情ID或emoji字符
         */
        private String reaction;

        /**
         * 表情数量
         */
        private Integer count;

        /**
         * 是否当前用户已添加
         */
        private Boolean self;

        /**
         * 添加此表情的用户列表
         */
        private List<Map<String, Object>> users;
    }
}
