package com.im.entity.usergrowth;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 成长值交易流水实体
 * 记录用户成长值的每一笔获取/扣除
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrowthTransactionLog {
    
    /** 流水ID */
    private Long id;
    
    /** 用户ID */
    private Long userId;
    
    /** 交易类型 (EARN/DEDUCT) */
    private String transactionType;
    
    /** 交易金额 (成长值) */
    private Long amount;
    
    /** 交易前余额 */
    private Long balanceBefore;
    
    /** 交易后余额 */
    private Long balanceAfter;
    
    /** 来源类型 */
    private String sourceType;
    
    /** 来源描述 */
    private String sourceDesc;
    
    /** 关联业务类型 */
    private String bizType;
    
    /** 关联业务ID */
    private Long bizId;
    
    /** 关联订单号 */
    private String orderNo;
    
    /** 交易备注 */
    private String remark;
    
    /** 扩展信息JSON */
    private String extraInfo;
    
    /** 交易时间 */
    private LocalDateTime transactionTime;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /**
     * 交易类型常量
     */
    public static class TransactionType {
        public static final String EARN = "EARN";                              // 获取
        public static final String DEDUCT = "DEDUCT";                          // 扣除
        public static final String EXPIRE = "EXPIRE";                          // 过期
        public static final String ADJUST = "ADJUST";                          // 调整
    }
    
    /**
     * 创建获取成长值流水
     */
    public static GrowthTransactionLog createEarnLog(Long userId, Long amount, Long balanceBefore,
                                                      String sourceType, String sourceDesc, String bizType, Long bizId) {
        return GrowthTransactionLog.builder()
            .userId(userId)
            .transactionType(TransactionType.EARN)
            .amount(amount)
            .balanceBefore(balanceBefore)
            .balanceAfter(balanceBefore + amount)
            .sourceType(sourceType)
            .sourceDesc(sourceDesc)
            .bizType(bizType)
            .bizId(bizId)
            .transactionTime(LocalDateTime.now())
            .createTime(LocalDateTime.now())
            .build();
    }
    
    /**
     * 创建扣除成长值流水
     */
    public static GrowthTransactionLog createDeductLog(Long userId, Long amount, Long balanceBefore,
                                                        String sourceType, String sourceDesc, String bizType, Long bizId) {
        return GrowthTransactionLog.builder()
            .userId(userId)
            .transactionType(TransactionType.DEDUCT)
            .amount(amount)
            .balanceBefore(balanceBefore)
            .balanceAfter(Math.max(0, balanceBefore - amount))
            .sourceType(sourceType)
            .sourceDesc(sourceDesc)
            .bizType(bizType)
            .bizId(bizId)
            .transactionTime(LocalDateTime.now())
            .createTime(LocalDateTime.now())
            .build();
    }
}
