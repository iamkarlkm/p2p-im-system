package com.im.service.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 群组审核请求DTO
 * 管理员审核/解散群组时使用
 *
 * @author IM Team
 * @version 1.0
 */
@Data
public class GroupAuditRequest {

    /**
     * 群组ID
     */
    @NotBlank(message = "群组ID不能为空")
    private String groupId;

    /**
     * 审核操作: APPROVE(通过), REJECT(拒绝), DISBAND(解散)
     */
    @NotBlank(message = "审核操作不能为空")
    private String action;

    /**
     * 审核意见/拒绝原因
     */
    @Size(max = 500, message = "审核意见最多500字符")
    private String comment;

    /**
     * 解散原因 (DISBAND操作时)
     */
    @Size(max = 500, message = "解散原因最多500字符")
    private String disbandReason;

    /**
     * 是否通知群成员
     */
    private Boolean notifyMembers = true;

    /**
     * 是否删除群聊记录
     */
    private Boolean deleteChatHistory = false;

    /**
     * 备注
     */
    @Size(max = 200, message = "备注最多200字符")
    private String remark;

    // ========== 内部类 ==========

    /**
     * 批量审核请求
     */
    @Data
    public static class BatchAuditRequest {
        @NotBlank(message = "群组ID列表不能为空")
        private java.util.List<String> groupIds;

        @NotBlank(message = "审核操作不能为空")
        private String action;

        @Size(max = 500, message = "审核意见最多500字符")
        private String comment;

        private Boolean notifyMembers = true;
    }

    /**
     * 群组查询请求
     */
    @Data
    public static class GroupQueryRequest {
        private String groupId;

        private String groupName;

        private String ownerId;

        private String status;

        private Integer memberCountMin;

        private Integer memberCountMax;

        private Integer page = 0;

        private Integer pageSize = 20;
    }

    // ========== 常量定义 ==========

    public static final String ACTION_APPROVE = "APPROVE";
    public static final String ACTION_REJECT = "REJECT";
    public static final String ACTION_DISBAND = "DISBAND";
}
