package com.im.backend.modules.local_life.checkin.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 积分交易记录DTO
 */
@Data
public class PointTransactionDTO {

    /**
     * 交易ID
     */
    private Long id;

    /**
     * 交易类型: EARN-获得, USE-使用
     */
    private String transactionType;

    /**
     * 积分变动数量
     */
    private Integer points;

    /**
     * 变动后余额
     */
    private Integer balanceAfter;

    /**
     * 积分类型
     */
    private String pointType;

    /**
     * 积分类型名称
     */
    private String pointTypeName;

    /**
     * 交易描述
     */
    private String description;

    /**
     * 交易时间
     */
    private LocalDateTime transactionTime;
}
