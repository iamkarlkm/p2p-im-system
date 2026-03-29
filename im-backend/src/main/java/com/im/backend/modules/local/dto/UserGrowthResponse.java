package com.im.backend.modules.local.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 用户等级成长响应DTO
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户等级成长响应")
public class UserGrowthResponse {
    
    @Schema(description = "用户ID", example = "user_123456")
    private String userId;
    
    @Schema(description = "当前等级", example = "5")
    private Integer currentLevel;
    
    @Schema(description = "等级名称", example = "资深玩家")
    private String levelName;
    
    @Schema(description = "当前经验值", example = "12500")
    private Long currentExp;
    
    @Schema(description = "升级所需经验值", example = "20000")
    private Long nextLevelExp;
    
    @Schema(description = "升级进度百分比", example = "62.5")
    private Double progressPercent;
    
    @Schema(description = "本次获得经验值", example = "150")
    private Long gainedExp;
    
    @Schema(description = "是否升级", example = "false")
    private Boolean levelUp;
    
    @Schema(description = "新等级（如升级）", example = "6")
    private Integer newLevel;
    
    @Schema(description = "新等级名称（如升级）", example = "专家")
    private String newLevelName;
    
    @Schema(description = "当前等级权益")
    private List<LevelBenefit> benefits;
    
    @Schema(description = "下一等级权益预览")
    private List<LevelBenefit> nextLevelBenefits;
    
    /**
     * 等级权益
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "等级权益")
    public static class LevelBenefit {
        @Schema(description = "权益类型", example = "coupon")
        private String type;
        
        @Schema(description = "权益名称", example = "每月专属优惠券")
        private String name;
        
        @Schema(description = "权益描述", example = "每月可领取5张满100减20优惠券")
        private String description;
        
        @Schema(description = "权益图标", example = "https://example.com/icon.png")
        private String icon;
    }
}
