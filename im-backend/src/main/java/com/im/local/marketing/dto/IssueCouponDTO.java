package com.im.local.marketing.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Map;

/**
 * 发放优惠券请求DTO
 * 
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueCouponDTO {
    
    @NotBlank(message = "优惠券模板ID不能为空")
    private String templateId;
    
    /**
     * 发放方式
     * SINGLE: 单个用户发放
     * BATCH: 批量发放
     * ALL: 全量用户发放
     * FILTER: 按条件筛选发放
     */
    @NotBlank(message = "发放方式不能为空")
    private String issueType;
    
    /**
     * 目标用户ID列表（SINGLE/BATCH方式）
     */
    private List<String> targetUserIds;
    
    /**
     * 发放数量
     */
    @NotNull(message = "发放数量不能为空")
    @Min(value = 1, message = "发放数量至少为1")
    private Integer quantity;
    
    /**
     * 发放渠道
     */
    @NotBlank(message = "发放渠道不能为空")
    private String issueChannel;
    
    /**
     * 筛选条件（FILTER方式）
     */
    private UserFilterDTO userFilter;
    
    /**
     * 用户筛选条件
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserFilterDTO {
        /**
         * 城市代码
         */
        private String cityCode;
        
        /**
         * 最小会员等级
         */
        private Integer minMemberLevel;
        
        /**
         * 最大会员等级
         */
        private Integer maxMemberLevel;
        
        /**
         * 新用户
         */
        private Boolean newUser;
        
        /**
         * 注册时间范围开始
         */
        private String registerTimeStart;
        
        /**
         * 注册时间范围结束
         */
        private String registerTimeEnd;
        
        /**
         * 最近消费时间范围（天）
         */
        private Integer lastOrderDays;
        
        /**
         * 消费次数范围
         */
        private Integer minOrderCount;
        
        /**
         * 消费金额范围
         */
        private Double minOrderAmount;
        
        /**
         * 用户标签
         */
        private List<String> userTags;
    }
    
    /**
     * 发放原因/备注
     */
    private String remark;
    
    /**
     * 定时发放时间（null表示立即发放）
     */
    private String scheduleTime;
    
    /**
     * 扩展字段
     */
    private Map<String, Object> extraData;
}
