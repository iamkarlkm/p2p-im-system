package com.im.service.admin.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 日志查询请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogQueryRequest {

    /**
     * 管理员ID（可选）
     */
    private Long adminId;

    /**
     * 模块（可选）
     */
    private String module;

    /**
     * 操作类型（可选）
     */
    private String operationType;

    /**
     * 结果（可选）
     */
    private String result;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 页码
     */
    @Builder.Default
    private int page = 0;

    /**
     * 每页大小
     */
    @Builder.Default
    private int size = 20;

    /**
     * 排序字段
     */
    @Builder.Default
    private String sortBy = "createdAt";

    /**
     * 排序方向
     */
    @Builder.Default
    private String sortDir = "desc";
}
