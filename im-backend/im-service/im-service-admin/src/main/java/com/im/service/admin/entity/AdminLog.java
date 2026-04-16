package com.im.service.admin.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 管理员操作日志实体类
 * 记录所有管理员的操作行为
 */
@Data
@Entity
@Table(name = "admin_logs", indexes = {
    @Index(name = "idx_admin_id", columnList = "adminId"),
    @Index(name = "idx_operation_type", columnList = "operationType"),
    @Index(name = "idx_target_type", columnList = "targetType"),
    @Index(name = "idx_created_at", columnList = "createdAt"),
    @Index(name = "idx_ip_address", columnList = "ipAddress")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 管理员ID
     */
    @Column(nullable = false)
    private Long adminId;

    /**
     * 管理员用户名
     */
    @Column(nullable = false, length = 50)
    private String adminUsername;

    /**
     * 管理员真实姓名
     */
    @Column(length = 50)
    private String adminRealName;

    /**
     * 操作类型：CREATE, UPDATE, DELETE, QUERY, LOGIN, LOGOUT, EXPORT, IMPORT, etc.
     */
    @Column(nullable = false, length = 20)
    private String operationType;

    /**
     * 操作模块：USER, GROUP, MESSAGE, FILE, SYSTEM, etc.
     */
    @Column(nullable = false, length = 20)
    private String module;

    /**
     * 目标类型：如 User, Group, Message 等
     */
    @Column(length = 50)
    private String targetType;

    /**
     * 目标ID
     */
    @Column(length = 64)
    private String targetId;

    /**
     * 操作描述
     */
    @Column(length = 500)
    private String description;

    /**
     * 请求方法：GET, POST, PUT, DELETE
     */
    @Column(length = 10)
    private String requestMethod;

    /**
     * 请求URL
     */
    @Column(length = 500)
    private String requestUrl;

    /**
     * 请求参数
     */
    @Column(columnDefinition = "TEXT")
    private String requestParams;

    /**
     * 请求体
     */
    @Column(columnDefinition = "TEXT")
    private String requestBody;

    /**
     * 响应状态码
     */
    private Integer responseStatus;

    /**
     * 响应内容
     */
    @Column(columnDefinition = "TEXT")
    private String responseBody;

    /**
     * 客户端IP地址
     */
    @Column(length = 50)
    private String ipAddress;

    /**
     * 用户代理
     */
    @Column(length = 500)
    private String userAgent;

    /**
     * 操作耗时（毫秒）
     */
    private Long duration;

    /**
     * 操作结果：SUCCESS, FAILURE, PARTIAL
     */
    @Column(nullable = false, length = 20)
    private String result;

    /**
     * 错误信息
     */
    @Column(length = 1000)
    private String errorMessage;

    /**
     * 设备信息
     */
    @Column(length = 200)
    private String deviceInfo;

    /**
     * 备注
     */
    @Column(length = 500)
    private String remark;

    /**
     * 创建时间
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (result == null) {
            result = Result.SUCCESS.name();
        }
    }

    /**
     * 操作类型枚举
     */
    public enum OperationType {
        CREATE,     // 创建
        UPDATE,     // 更新
        DELETE,     // 删除
        QUERY,      // 查询
        LOGIN,      // 登录
        LOGOUT,     // 登出
        EXPORT,     // 导出
        IMPORT,     // 导入
        BATCH_OP,   // 批量操作
        APPROVE,    // 审批
        REJECT,     // 拒绝
        ENABLE,     // 启用
        DISABLE,    // 禁用
        RESET,      // 重置
        OTHER       // 其他
    }

    /**
     * 操作模块枚举
     */
    public enum Module {
        USER,           // 用户管理
        GROUP,          // 群组管理
        MESSAGE,        // 消息管理
        FILE,           // 文件管理
        FRIEND,         // 好友管理
        STATISTICS,     // 统计
        SYSTEM,         // 系统管理
        SECURITY,       // 安全设置
        PERMISSION,     // 权限管理
        CONFIG,         // 配置管理
        LOG,            // 日志管理
        OTHER           // 其他
    }

    /**
     * 结果枚举
     */
    public enum Result {
        SUCCESS,    // 成功
        FAILURE,    // 失败
        PARTIAL     // 部分成功
    }

    // 便捷方法
    public boolean isSuccess() {
        return Result.SUCCESS.name().equals(result);
    }

    public boolean isFailure() {
        return Result.FAILURE.name().equals(result);
    }

    public boolean isLoginOperation() {
        return OperationType.LOGIN.name().equals(operationType);
    }

    public boolean isDataModification() {
        return OperationType.CREATE.name().equals(operationType) ||
               OperationType.UPDATE.name().equals(operationType) ||
               OperationType.DELETE.name().equals(operationType);
    }

    // 设置成功的便捷方法
    public void markAsSuccess() {
        this.result = Result.SUCCESS.name();
    }

    // 设置失败的便捷方法
    public void markAsFailure(String errorMsg) {
        this.result = Result.FAILURE.name();
        this.errorMessage = errorMsg;
    }

    // 设置部分成功
    public void markAsPartial(String errorMsg) {
        this.result = Result.PARTIAL.name();
        this.errorMessage = errorMsg;
    }
}
