package com.im.service.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户封禁请求DTO
 * 管理员封禁/解封用户时使用
 *
 * @author IM Team
 * @version 1.0
 */
@Data
public class UserBanRequest {

    /**
     * 用户ID
     */
    @NotBlank(message = "用户ID不能为空")
    private String userId;

    /**
     * 封禁原因
     */
    @NotBlank(message = "封禁原因不能为空")
    @Size(max = 500, message = "封禁原因最多500字符")
    private String reason;

    /**
     * 封禁时长(分钟)，-1表示永久封禁
     */
    private Integer duration = -1;

    /**
     * 封禁类型: TEMPORARY(临时), PERMANENT(永久)
     */
    private String banType = "PERMANENT";

    /**
     * 是否同时封禁登录
     */
    private Boolean blockLogin = true;

    /**
     * 是否同时封禁发送消息
     */
    private Boolean blockSendMessage = true;

    /**
     * 是否同时封禁添加好友
     */
    private Boolean blockAddFriend = true;

    /**
     * 是否同时封禁加入群组
     */
    private Boolean blockJoinGroup = true;

    /**
     * 备注
     */
    @Size(max = 200, message = "备注最多200字符")
    private String remark;

    // ========== 内部类 ==========

    /**
     * 用户解封请求
     */
    @Data
    public static class UnbanRequest {
        @NotBlank(message = "用户ID不能为空")
        private String userId;

        @Size(max = 200, message = "解封原因最多200字符")
        private String reason;
    }

    /**
     * 用户查询请求
     */
    @Data
    public static class UserQueryRequest {
        private String userId;

        private String username;

        private String phone;

        private String email;

        private String status;

        private Integer page = 0;

        private Integer pageSize = 20;
    }
}
