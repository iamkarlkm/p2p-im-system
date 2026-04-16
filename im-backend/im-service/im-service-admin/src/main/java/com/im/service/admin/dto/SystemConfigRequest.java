package com.im.service.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Map;

/**
 * 系统配置请求DTO
 * 管理员更新系统配置时使用
 *
 * @author IM Team
 * @version 1.0
 */
@Data
public class SystemConfigRequest {

    /**
     * 配置键
     */
    @NotBlank(message = "配置键不能为空")
    @Size(max = 100, message = "配置键最多100字符")
    private String configKey;

    /**
     * 配置值
     */
    @NotBlank(message = "配置值不能为空")
    private String configValue;

    /**
     * 配置类型: STRING(字符串), NUMBER(数字), BOOLEAN(布尔), JSON(JSON)
     */
    private String configType = "STRING";

    /**
     * 配置分组
     */
    @Size(max = 50, message = "配置分组最多50字符")
    private String configGroup;

    /**
     * 配置描述
     */
    @Size(max = 200, message = "配置描述最多200字符")
    private String description;

    /**
     * 是否公开 (前端可见)
     */
    private Boolean isPublic = false;

    /**
     * 是否可动态修改
     */
    private Boolean isDynamic = true;

    /**
     * 排序权重
     */
    private Integer sortOrder = 0;

    /**
     * 备注
     */
    @Size(max = 200, message = "备注最多200字符")
    private String remark;

    // ========== 内部类 ==========

    /**
     * 批量更新配置请求
     */
    @Data
    public static class BatchUpdateRequest {
        private Map<String, String> configs;

        private String updateReason;
    }

    /**
     * 配置查询请求
     */
    @Data
    public static class ConfigQueryRequest {
        private String configKey;

        private String configGroup;

        private Boolean isPublic;

        private Integer page = 0;

        private Integer pageSize = 50;
    }

    // ========== 常用配置键常量 ==========

    // 用户相关
    public static final String KEY_USER_REGISTRATION_ENABLED = "user.registration.enabled";
    public static final String KEY_USER_DEFAULT_AVATAR = "user.default.avatar";
    public static final String KEY_USER_MAX_FRIENDS = "user.max.friends";
    public static final String KEY_USER_NAME_MIN_LENGTH = "user.name.min.length";
    public static final String KEY_USER_NAME_MAX_LENGTH = "user.name.max.length";

    // 消息相关
    public static final String KEY_MESSAGE_MAX_LENGTH = "message.max.length";
    public static final String KEY_MESSAGE_RECALL_TIMEOUT = "message.recall.timeout";
    public static final String KEY_MESSAGE_RATE_LIMIT = "message.rate.limit";
    public static final String KEY_MESSAGE_MAX_ATTACHMENTS = "message.max.attachments";

    // 群组相关
    public static final String KEY_GROUP_MAX_MEMBERS = "group.max.members";
    public static final String KEY_GROUP_NAME_MAX_LENGTH = "group.name.max.length";
    public static final String KEY_GROUP_AUDIT_ENABLED = "group.audit.enabled";
    public static final String KEY_GROUP_CREATE_REQUIRE_VERIFY = "group.create.require.verify";

    // 文件相关
    public static final String KEY_FILE_MAX_SIZE = "file.max.size";
    public static final String KEY_FILE_ALLOWED_TYPES = "file.allowed.types";
    public static final String KEY_FILE_STORAGE_TYPE = "file.storage.type";

    // 安全相关
    public static final String KEY_LOGIN_MAX_ATTEMPTS = "login.max.attempts";
    public static final String KEY_LOGIN_LOCK_DURATION = "login.lock.duration";
    public static final String KEY_PASSWORD_MIN_LENGTH = "password.min.length";
    public static final String KEY_PASSWORD_REQUIRE_SPECIAL = "password.require.special";

    // 系统相关
    public static final String KEY_SYSTEM_MAINTENANCE_MODE = "system.maintenance.mode";
    public static final String KEY_SYSTEM_VERSION = "system.version";
    public static final String KEY_SESSION_TIMEOUT = "session.timeout";
}
