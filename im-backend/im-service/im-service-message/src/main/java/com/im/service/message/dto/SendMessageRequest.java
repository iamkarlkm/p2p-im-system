package com.im.service.message.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 发送消息请求 DTO
 * 
 * @author IM Team
 * @version 1.0
 */
@Data
public class SendMessageRequest {

    /**
     * 客户端消息ID - 用于幂等性控制和消息去重
     * 建议格式: UUID 或 客户端生成的唯一标识
     */
    @Size(max = 64, message = "客户端消息ID长度不能超过64")
    private String clientMessageId;

    /**
     * 会话ID - 消息所属的会话
     */
    @NotBlank(message = "会话ID不能为空")
    @Size(max = 36, message = "会话ID长度不能超过36")
    private String conversationId;

    /**
     * 发送者ID - 发送消息的用户ID
     */
    @NotBlank(message = "发送者ID不能为空")
    @Size(max = 36, message = "发送者ID长度不能超过36")
    private String senderId;

    /**
     * 接收者ID - 私聊时为对方用户ID，群聊时为群ID
     */
    @NotBlank(message = "接收者ID不能为空")
    @Size(max = 36, message = "接收者ID长度不能超过36")
    private String receiverId;

    /**
     * 会话类型: PRIVATE(私聊), GROUP(群聊), CHANNEL(频道)
     * 默认为 PRIVATE
     */
    @Size(max = 20, message = "会话类型长度不能超过20")
    private String conversationType = "PRIVATE";

    /**
     * 消息类型: 
     * - TEXT: 文本消息
     * - IMAGE: 图片消息
     * - FILE: 文件消息
     * - VOICE: 语音消息
     * - VIDEO: 视频消息
     * - LOCATION: 位置消息
     * - SYSTEM: 系统消息
     * - CUSTOM: 自定义消息
     * - CARD: 名片消息
     * - MERGE: 合并转发消息
     */
    @NotBlank(message = "消息类型不能为空")
    @Size(max = 20, message = "消息类型长度不能超过20")
    private String type;

    /**
     * 消息内容 - 文本内容或富文本描述
     * 对于媒体消息，可以填写媒体描述
     */
    @Size(max = 10000, message = "消息内容长度不能超过10000")
    private String content;

    /**
     * 内容摘要 - 用于预览显示，如推送通知
     * 如果不提供，服务端会自动生成
     */
    @Size(max = 200, message = "内容摘要长度不能超过200")
    private String contentSummary;

    /**
     * 引用消息ID - 回复/引用的消息ID
     */
    @Size(max = 36, message = "引用消息ID长度不能超过36")
    private String replyToId;

    /**
     * 根消息ID - 用于消息链，标识整个回复链的根消息
     */
    @Size(max = 36, message = "根消息ID长度不能超过36")
    private String rootMessageId;

    /**
     * 附件列表 - 媒体消息使用
     * 支持多附件上传
     */
    private List<AttachmentDTO> attachments;

    /**
     * 单个文件信息 - 兼容旧版单附件上传
     * @deprecated 请使用 attachments 字段
     */
    @Deprecated
    private Map<String, Object> attachment;

    /**
     * 文件大小(字节)
     */
    private Long fileSize;

    /**
     * 文件类型 - MIME类型
     * 如: image/jpeg, audio/mp4, video/mp4
     */
    @Size(max = 50, message = "文件类型长度不能超过50")
    private String mimeType;

    /**
     * 文件URL - 上传完成后返回的URL
     */
    @Size(max = 500, message = "文件URL长度不能超过500")
    private String fileUrl;

    /**
     * 缩略图URL - 图片/视频消息的缩略图
     */
    @Size(max = 500, message = "缩略图URL长度不能超过500")
    private String thumbnailUrl;

    /**
     * 位置信息
     */
    private LocationDTO location;

    /**
     * @提及的用户ID列表
     */
    private List<String> mentions;

    /**
     * 是否@所有人 - 群聊使用
     */
    private Boolean mentionAll = false;

    /**
     * 是否加密消息
     */
    private Boolean encrypted = false;

    /**
     * 加密类型 - 如: E2EE(端到端加密), AES
     */
    @Size(max = 20, message = "加密类型长度不能超过20")
    private String encryptionType;

    /**
     * 是否阅后即焚
     */
    private Boolean selfDestruct = false;

    /**
     * 阅后即焚倒计时(秒) - 默认为10秒
     */
    private Integer selfDestructTime = 10;

    /**
     * 扩展数据 - 自定义业务数据
     * 用于存储额外的业务字段
     */
    private Map<String, Object> extraData;

    /**
     * 消息优先级 - 用于消息排序
     * 数值越大优先级越高
     */
    private Integer priority = 0;

    /**
     * 发送序号 - 客户端生成的本地序号，用于排序
     */
    private Long sequence;

    /**
     * 来源设备类型
     * 如: IOS, ANDROID, WEB, WINDOWS, MAC
     */
    @Size(max = 20, message = "设备类型长度不能超过20")
    private String deviceType;

    // ========== 内部DTO类 ==========

    /**
     * 附件DTO
     */
    @Data
    public static class AttachmentDTO {
        /**
         * 附件ID
         */
        private String id;

        /**
         * 附件类型: IMAGE, FILE, VOICE, VIDEO
         */
        private String type;

        /**
         * 附件名称
         */
        private String name;

        /**
         * 附件URL
         */
        private String url;

        /**
         * 缩略图URL
         */
        private String thumbnailUrl;

        /**
         * 文件大小(字节)
         */
        private Long size;

        /**
         * MIME类型
         */
        private String mimeType;

        /**
         * 宽度 - 图片/视频使用
         */
        private Integer width;

        /**
         * 高度 - 图片/视频使用
         */
        private Integer height;

        /**
         * 时长(秒) - 语音/视频使用
         */
        private Integer duration;

        /**
         * 扩展数据
         */
        private Map<String, Object> extra;
    }

    /**
     * 位置信息DTO
     */
    @Data
    public static class LocationDTO {
        /**
         * 纬度
         */
        private Double latitude;

        /**
         * 经度
         */
        private Double longitude;

        /**
         * 地址描述
         */
        private String address;

        /**
         * 位置名称
         */
        private String name;

        /**
         * 地图缩放级别
         */
        private Integer zoom = 15;
    }
}
