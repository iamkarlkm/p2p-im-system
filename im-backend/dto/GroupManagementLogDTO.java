package com.im.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.im.backend.entity.GroupManagementLogEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * 群管理日志数据传输对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "群管理日志数据")
public class GroupManagementLogDTO {

    @Schema(description = "日志ID (创建时不需要)", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @NotNull(message = "群组ID不能为空")
    @Schema(description = "群组ID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID groupId;

    @NotNull(message = "操作者ID不能为空")
    @Schema(description = "操作者ID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID operatorId;

    @NotBlank(message = "操作者类型不能为空")
    @Schema(description = "操作者类型: SYSTEM=系统, USER=用户, ADMIN=管理员, BOT=机器人", 
            required = true, example = "ADMIN")
    private String operatorType;

    @Schema(description = "目标用户ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID targetUserId;

    @NotBlank(message = "操作类型不能为空")
    @Schema(description = "操作类型", required = true, example = "MEMBER_ADD")
    private String actionType;

    @Schema(description = "操作子类型", example = "INVITE_LINK")
    private String actionSubType;

    @Schema(description = "操作描述", example = "通过邀请链接添加新成员")
    private String description;

    @Schema(description = "操作详情 (JSON格式)", example = "{\"inviteLinkId\": \"abc123\", \"expiresIn\": 86400}")
    private Map<String, Object> details;

    @Schema(description = "操作前状态 (JSON格式)", example = "{\"memberCount\": 10}")
    private Map<String, Object> beforeState;

    @Schema(description = "操作后状态 (JSON格式)", example = "{\"memberCount\": 11}")
    private Map<String, Object> afterState;

    @Schema(description = "IP地址", example = "192.168.1.100")
    private String ipAddress;

    @Schema(description = "用户代理", example = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
    private String userAgent;

    @Schema(description = "设备信息", example = "Windows 10, Chrome 120.0.0.0")
    private String deviceInfo;

    @NotBlank(message = "操作结果不能为空")
    @Schema(description = "操作结果: SUCCESS=成功, FAILED=失败, PARTIAL=部分成功", 
            required = true, example = "SUCCESS")
    private String result;

    @Schema(description = "错误信息", example = "用户已存在")
    private String errorMessage;

    @Schema(description = "是否重要操作", example = "false")
    private Boolean important = false;

    @Schema(description = "是否需要通知相关人员", example = "true")
    private Boolean needNotification = false;

    @Schema(description = "租户ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID tenantId;

    @Schema(description = "版本号", example = "1")
    private Long version;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间", example = "2026-03-22 10:00:00")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间", example = "2026-03-22 10:00:00")
    private LocalDateTime updatedAt;

    /**
     * 从实体转换为DTO
     */
    public static GroupManagementLogDTO fromEntity(GroupManagementLogEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return GroupManagementLogDTO.builder()
            .id(entity.getId())
            .groupId(entity.getGroupId())
            .operatorId(entity.getOperatorId())
            .operatorType(entity.getOperatorType())
            .targetUserId(entity.getTargetUserId())
            .actionType(entity.getActionType())
            .actionSubType(entity.getActionSubType())
            .description(entity.getDescription())
            .ipAddress(entity.getIpAddress())
            .userAgent(entity.getUserAgent())
            .deviceInfo(entity.getDeviceInfo())
            .result(entity.getResult())
            .errorMessage(entity.getErrorMessage())
            .important(entity.getImportant())
            .needNotification(entity.getNeedNotification())
            .tenantId(entity.getTenantId())
            .version(entity.getVersion())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }

    /**
     * 转换为实体
     */
    public GroupManagementLogEntity toEntity() {
        return GroupManagementLogEntity.builder()
            .id(this.id)
            .groupId(this.groupId)
            .operatorId(this.operatorId)
            .operatorType(this.operatorType)
            .targetUserId(this.targetUserId)
            .actionType(this.actionType)
            .actionSubType(this.actionSubType)
            .description(this.description)
            .ipAddress(this.ipAddress)
            .userAgent(this.userAgent)
            .deviceInfo(this.deviceInfo)
            .result(this.result)
            .errorMessage(this.errorMessage)
            .important(this.important)
            .needNotification(this.needNotification)
            .tenantId(this.tenantId)
            .build();
    }

    /**
     * 更新实体
     */
    public void updateEntity(GroupManagementLogEntity entity) {
        if (entity == null) {
            return;
        }
        
        entity.setGroupId(this.groupId);
        entity.setOperatorId(this.operatorId);
        entity.setOperatorType(this.operatorType);
        entity.setTargetUserId(this.targetUserId);
        entity.setActionType(this.actionType);
        entity.setActionSubType(this.actionSubType);
        entity.setDescription(this.description);
        entity.setIpAddress(this.ipAddress);
        entity.setUserAgent(this.userAgent);
        entity.setDeviceInfo(this.deviceInfo);
        entity.setResult(this.result);
        entity.setErrorMessage(this.errorMessage);
        entity.setImportant(this.important);
        entity.setNeedNotification(this.needNotification);
        entity.setTenantId(this.tenantId);
    }

    /**
     * 验证操作者类型
     */
    public boolean isValidOperatorType() {
        if (operatorType == null) {
            return false;
        }
        try {
            GroupManagementLogEntity.OperatorType.valueOf(operatorType.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 验证操作类型
     */
    public boolean isValidActionType() {
        if (actionType == null) {
            return false;
        }
        try {
            GroupManagementLogEntity.ActionType.valueOf(actionType.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 验证操作结果
     */
    public boolean isValidResult() {
        if (result == null) {
            return false;
        }
        try {
            GroupManagementLogEntity.Result.valueOf(result.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 获取标准化的操作者类型
     */
    public String getNormalizedOperatorType() {
        if (!isValidOperatorType()) {
            return GroupManagementLogEntity.OperatorType.USER.name();
        }
        return operatorType.toUpperCase();
    }

    /**
     * 获取标准化的操作类型
     */
    public String getNormalizedActionType() {
        if (!isValidActionType()) {
            return GroupManagementLogEntity.ActionType.CUSTOM.name();
        }
        return actionType.toUpperCase();
    }

    /**
     * 获取标准化的操作结果
     */
    public String getNormalizedResult() {
        if (!isValidResult()) {
            return GroupManagementLogEntity.Result.SUCCESS.name();
        }
        return result.toUpperCase();
    }

    /**
     * 创建添加成员日志DTO
     */
    public static GroupManagementLogDTO createMemberAddLog(UUID groupId, UUID operatorId, UUID targetUserId, 
                                                          String operatorType, Map<String, Object> details) {
        return GroupManagementLogDTO.builder()
            .groupId(groupId)
            .operatorId(operatorId)
            .operatorType(operatorType)
            .targetUserId(targetUserId)
            .actionType(GroupManagementLogEntity.ActionType.MEMBER_ADD.name())
            .description("添加新成员")
            .details(details)
            .result(GroupManagementLogEntity.Result.SUCCESS.name())
            .important(false)
            .needNotification(true)
            .build();
    }

    /**
     * 创建移除成员日志DTO
     */
    public static GroupManagementLogDTO createMemberRemoveLog(UUID groupId, UUID operatorId, UUID targetUserId,
                                                             String operatorType, String reason, Map<String, Object> details) {
        return GroupManagementLogDTO.builder()
            .groupId(groupId)
            .operatorId(operatorId)
            .operatorType(operatorType)
            .targetUserId(targetUserId)
            .actionType(GroupManagementLogEntity.ActionType.MEMBER_REMOVE.name())
            .description("移除成员" + (reason != null ? " (" + reason + ")" : ""))
            .details(details)
            .result(GroupManagementLogEntity.Result.SUCCESS.name())
            .important(true)
            .needNotification(true)
            .build();
    }

    /**
     * 创建角色变更日志DTO
     */
    public static GroupManagementLogDTO createRoleChangeLog(UUID groupId, UUID operatorId, UUID targetUserId,
                                                           String operatorType, String fromRole, String toRole, 
                                                           Map<String, Object> details) {
        return GroupManagementLogDTO.builder()
            .groupId(groupId)
            .operatorId(operatorId)
            .operatorType(operatorType)
            .targetUserId(targetUserId)
            .actionType(GroupManagementLogEntity.ActionType.ROLE_CHANGE.name())
            .description("角色变更: " + fromRole + " → " + toRole)
            .details(details)
            .result(GroupManagementLogEntity.Result.SUCCESS.name())
            .important(true)
            .needNotification(true)
            .build();
    }

    /**
     * 创建群组设置变更日志DTO
     */
    public static GroupManagementLogDTO createGroupSettingsLog(UUID groupId, UUID operatorId, String operatorType,
                                                              String settingName, Object oldValue, Object newValue,
                                                              Map<String, Object> details) {
        return GroupManagementLogDTO.builder()
            .groupId(groupId)
            .operatorId(operatorId)
            .operatorType(operatorType)
            .actionType(GroupManagementLogEntity.ActionType.GROUP_SETTINGS.name())
            .description("群组设置变更: " + settingName)
            .beforeState(Map.of(settingName, oldValue))
            .afterState(Map.of(settingName, newValue))
            .details(details)
            .result(GroupManagementLogEntity.Result.SUCCESS.name())
            .important(false)
            .needNotification(false)
            .build();
    }

    /**
     * 创建公告操作日志DTO
     */
    public static GroupManagementLogDTO createAnnouncementLog(UUID groupId, UUID operatorId, String operatorType,
                                                             String actionType, String announcementTitle,
                                                             Map<String, Object> details) {
        return GroupManagementLogDTO.builder()
            .groupId(groupId)
            .operatorId(operatorId)
            .operatorType(operatorType)
            .actionType(actionType)
            .description("公告操作: " + announcementTitle)
            .details(details)
            .result(GroupManagementLogEntity.Result.SUCCESS.name())
            .important(true)
            .needNotification(true)
            .build();
    }

    /**
     * 创建失败操作日志DTO
     */
    public static GroupManagementLogDTO createFailedLog(UUID groupId, UUID operatorId, String operatorType,
                                                       String actionType, UUID targetUserId, String errorMessage,
                                                       Map<String, Object> details) {
        return GroupManagementLogDTO.builder()
            .groupId(groupId)
            .operatorId(operatorId)
            .operatorType(operatorType)
            .targetUserId(targetUserId)
            .actionType(actionType)
            .description("操作失败")
            .details(details)
            .result(GroupManagementLogEntity.Result.FAILED.name())
            .errorMessage(errorMessage)
            .important(true)
            .needNotification(true)
            .build();
    }
}