package com.im.backend.modules.local.service;

import com.im.backend.modules.local.dto.UserGrowthRequest;
import com.im.backend.modules.local.dto.UserGrowthResponse;

import java.util.List;

/**
 * 用户成长体系服务接口
 * 提供用户等级成长、经验值管理、等级权益等功能
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
public interface UserGrowthService {
    
    /**
     * 记录用户行为并计算成长值
     * 
     * @param request 成长请求
     * @return 成长响应
     */
    UserGrowthResponse recordAction(UserGrowthRequest request);
    
    /**
     * 获取用户等级信息
     * 
     * @param userId 用户ID
     * @return 等级信息
     */
    UserGrowthResponse getUserLevel(String userId);
    
    /**
     * 获取等级列表
     * 
     * @return 所有等级配置
     */
    List<LevelConfig> getLevelList();
    
    /**
     * 获取用户等级权益
     * 
     * @param userId 用户ID
     * @return 权益列表
     */
    List<UserGrowthResponse.LevelBenefit> getUserBenefits(String userId);
    
    /**
     * 领取等级权益
     * 
     * @param userId 用户ID
     * @param benefitType 权益类型
     * @return 领取结果
     */
    boolean claimBenefit(String userId, String benefitType);
    
    /**
     * 等级配置
     */
    class LevelConfig {
        private Integer level;
        private String name;
        private Long minExp;
        private Long maxExp;
        private String icon;
        private List<String> benefits;
        
        // Getters and setters
        public Integer getLevel() { return level; }
        public void setLevel(Integer level) { this.level = level; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public Long getMinExp() { return minExp; }
        public void setMinExp(Long minExp) { this.minExp = minExp; }
        
        public Long getMaxExp() { return maxExp; }
        public void setMaxExp(Long maxExp) { this.maxExp = maxExp; }
        
        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
        
        public List<String> getBenefits() { return benefits; }
        public void setBenefits(List<String> benefits) { this.benefits = benefits; }
    }
}
