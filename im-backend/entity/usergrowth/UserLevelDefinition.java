package com.im.entity.usergrowth;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 用户等级定义实体
 * 定义系统中所有用户等级规则、成长值阈值、权益配置
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLevelDefinition {
    
    /** 等级ID */
    private Long id;
    
    /** 等级编码 (LV1, LV2, ...) */
    private String levelCode;
    
    /** 等级名称 */
    private String levelName;
    
    /** 等级图标URL */
    private String levelIcon;
    
    /** 等级颜色 */
    private String levelColor;
    
    /** 等级排序 (1-10) */
    private Integer levelOrder;
    
    /** 升级所需最小成长值 */
    private Long minGrowthValue;
    
    /** 升级所需最大成长值 */
    private Long maxGrowthValue;
    
    /** 保级所需最低成长值 */
    private Long retainMinGrowthValue;
    
    /** 等级有效期 (天) */
    private Integer validityDays;
    
    /** 等级特权列表 */
    private List<LevelPrivilege> privileges;
    
    /** 等级权益配置JSON */
    private String privilegeConfig;
    
    /** 是否启用 */
    private Boolean enabled;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
    
    /**
     * 等级特权内部类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LevelPrivilege {
        
        /** 特权类型 */
        private String privilegeType;
        
        /** 特权名称 */
        private String privilegeName;
        
        /** 特权图标 */
        private String privilegeIcon;
        
        /** 特权描述 */
        private String privilegeDesc;
        
        /** 特权值 (如折扣率、积分数) */
        private String privilegeValue;
        
        /** 特权配置 */
        private Map<String, Object> privilegeConfig;
        
        /** 特权生效范围 (ALL/SELECTED) */
        private String scopeType;
        
        /** 特权生效范围ID列表 */
        private List<Long> scopeIds;
        
        /** 每日使用上限 */
        private Integer dailyLimit;
        
        /** 每月使用上限 */
        private Integer monthlyLimit;
        
        /** 每年使用上限 */
        private Integer yearlyLimit;
        
        /** 是否可叠加 */
        private Boolean stackable;
        
        /** 优先级 */
        private Integer priority;
    }
    
    /**
     * 特权类型常量
     */
    public static class PrivilegeType {
        public static final String POINTS_MULTIPLIER = "POINTS_MULTIPLIER";      // 积分倍率
        public static final String DISCOUNT_RATE = "DISCOUNT_RATE";              // 折扣率
        public static final String FREE_SHIPPING = "FREE_SHIPPING";              // 免运费
        public static final String PRIORITY_SERVICE = "PRIORITY_SERVICE";        // 优先服务
        public static final String EXCLUSIVE_COUPON = "EXCLUSIVE_COUPON";        // 专属优惠券
        public static final String BIRTHDAY_GIFT = "BIRTHDAY_GIFT";              // 生日礼物
        public static final String EXCLUSIVE_EVENT = "EXCLUSIVE_EVENT";          // 专属活动
        public static final String VIP_CUSTOMER_SERVICE = "VIP_CUSTOMER_SERVICE"; // VIP客服
        public static final String EXTENDED_RETURN = "EXTENDED_RETURN";          // 延长退换货
        public static final String EARLY_ACCESS = "EARLY_ACCESS";                // 优先购买权
        public static final String FREE_PARKING = "FREE_PARKING";                // 免费停车
        public static final String RESERVED_SEATING = "RESERVED_SEATING";        // 预留座位
    }
    
    /**
     * 判断指定成长值是否满足当前等级
     */
    public boolean matchesGrowthValue(Long growthValue) {
        if (growthValue == null) return false;
        return growthValue >= minGrowthValue && growthValue <= maxGrowthValue;
    }
    
    /**
     * 判断是否满足保级条件
     */
    public boolean meetsRetainCondition(Long growthValue) {
        if (growthValue == null) return false;
        return growthValue >= retainMinGrowthValue;
    }
    
    /**
     * 获取下一级所需成长值
     */
    public Long getNextLevelGrowthValue() {
        return maxGrowthValue + 1;
    }
    
    /**
     * 获取当前等级权益总价值
     */
    public int getTotalPrivilegeValue() {
        if (privileges == null) return 0;
        return privileges.stream()
            .mapToInt(p -> {
                try {
                    return Integer.parseInt(p.getPrivilegeValue());
                } catch (Exception e) {
                    return 0;
                }
            })
            .sum();
    }
    
    /**
     * 获取指定类型的特权
     */
    public LevelPrivilege getPrivilegeByType(String type) {
        if (privileges == null || type == null) return null;
        return privileges.stream()
            .filter(p -> type.equals(p.getPrivilegeType()))
            .findFirst()
            .orElse(null);
    }
}
