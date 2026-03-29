package com.im.entity.usergrowth;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * 积分交易流水实体
 * 记录用户积分的每一笔获取/消耗
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointsTransactionLog {
    
    /** 流水ID */
    private Long id;
    
    /** 用户ID */
    private Long userId;
    
    /** 交易类型 (EARN/SPEND/FREEZE/UNFREEZE/EXPIRE) */
    private String transactionType;
    
    /** 交易积分数量 */
    private Long points;
    
    /** 交易前可用余额 */
    private Long availableBefore;
    
    /** 交易后可用余额 */
    private Long availableAfter;
    
    /** 交易前冻结金额 */
    private Long frozenBefore;
    
    /** 交易后冻结金额 */
    private Long frozenAfter;
    
    /** 来源/用途类型 */
    private String sourceType;
    
    /** 来源/用途描述 */
    private String sourceDesc;
    
    /** 关联业务类型 */
    private String bizType;
    
    /** 关联业务ID */
    private Long bizId;
    
    /** 关联订单号 */
    private String orderNo;
    
    /** 交易备注 */
    private String remark;
    
    /** 积分过期时间 (获取时设置) */
    private LocalDateTime expireTime;
    
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
        public static final String SPEND = "SPEND";                            // 消耗
        public static final String FREEZE = "FREEZE";                          // 冻结
        public static final String UNFREEZE = "UNFREEZE";                      // 解冻
        public static final String CONFIRM = "CONFIRM";                        // 确认消耗(冻结转消耗)
        public static final String EXPIRE = "EXPIRE";                          // 过期
        public static final String ADJUST = "ADJUST";                          // 调整
        public static final String REFUND = "REFUND";                          // 退还
    }
    
    /**
     * 创建获取积分流水
     */
    public static PointsTransactionLog createEarnLog(Long userId, Long points, Long availableBefore,
                                                      String sourceType, String sourceDesc, 
                                                      String bizType, Long bizId, LocalDateTime expireTime) {
        return PointsTransactionLog.builder()
            .userId(userId)
            .transactionType(TransactionType.EARN)
            .points(points)
            .availableBefore(availableBefore)
            .availableAfter(availableBefore + points)
            .frozenBefore(0L)
            .frozenAfter(0L)
            .sourceType(sourceType)
            .sourceDesc(sourceDesc)
            .bizType(bizType)
            .bizId(bizId)
            .expireTime(expireTime)
            .transactionTime(LocalDateTime.now())
            .createTime(LocalDateTime.now())
            .build();
    }
    
    /**
     * 创建消耗积分流水
     */
    public static PointsTransactionLog createSpendLog(Long userId, Long points, Long availableBefore,
                                                       String sourceType, String sourceDesc,
                                                       String bizType, Long bizId) {
        return PointsTransactionLog.builder()
            .userId(userId)
            .transactionType(TransactionType.SPEND)
            .points(points)
            .availableBefore(availableBefore)
            .availableAfter(Math.max(0, availableBefore - points))
            .frozenBefore(0L)
            .frozenAfter(0L)
            .sourceType(sourceType)
            .sourceDesc(sourceDesc)
            .bizType(bizType)
            .bizId(bizId)
            .transactionTime(LocalDateTime.now())
            .createTime(LocalDateTime.now())
            .build();
    }
    
    /**
     * 创建冻结积分流水
     */
    public static PointsTransactionLog createFreezeLog(Long userId, Long points, 
                                                        Long availableBefore, Long frozenBefore,
                                                        String sourceType, String sourceDesc) {
        return PointsTransactionLog.builder()
            .userId(userId)
            .transactionType(TransactionType.FREEZE)
            .points(points)
            .availableBefore(availableBefore)
            .availableAfter(Math.max(0, availableBefore - points))
            .frozenBefore(frozenBefore)
            .frozenAfter(frozenBefore + points)
            .sourceType(sourceType)
            .sourceDesc(sourceDesc)
            .transactionTime(LocalDateTime.now())
            .createTime(LocalDateTime.now())
            .build();
    }
    
    /**
     * 创建解冻积分流水
     */
    public static PointsTransactionLog createUnfreezeLog(Long userId, Long points,
                                                          Long availableBefore, Long frozenBefore,
                                                          String sourceType, String sourceDesc) {
        return PointsTransactionLog.builder()
            .userId(userId)
            .transactionType(TransactionType.UNFREEZE)
            .points(points)
            .availableBefore(availableBefore)
            .availableAfter(availableBefore + points)
            .frozenBefore(frozenBefore)
            .frozenAfter(Math.max(0, frozenBefore - points))
            .sourceType(sourceType)
            .sourceDesc(sourceDesc)
            .transactionTime(LocalDateTime.now())
            .createTime(LocalDateTime.now())
            .build();
    }
}
