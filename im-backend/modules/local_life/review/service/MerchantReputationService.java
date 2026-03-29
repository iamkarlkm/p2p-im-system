package com.im.backend.modules.local_life.review.service;

import com.im.backend.modules.local_life.review.dto.MerchantReputationDTO;
import com.im.backend.modules.local_life.review.dto.ReputationRankingRequestDTO;
import com.im.backend.modules.local_life.review.dto.ReputationRankingDTO;

import java.util.List;

/**
 * 商户口碑服务接口
 * 
 * @author IM Development Team
 * @version 1.0
 */
public interface MerchantReputationService {
    
    /**
     * 获取商户口碑统计
     * 
     * @param merchantId 商户ID
     * @return 口碑统计
     */
    MerchantReputationDTO getMerchantReputation(Long merchantId);
    
    /**
     * 批量获取商户口碑
     * 
     * @param merchantIds 商户ID列表
     * @return 口碑列表
     */
    List<MerchantReputationDTO> getBatchReputation(List<Long> merchantIds);
    
    /**
     * 获取口碑榜单
     * 
     * @param request 榜单请求
     * @return 榜单列表
     */
    List<ReputationRankingDTO> getReputationRanking(ReputationRankingRequestDTO request);
    
    /**
     * 更新商户口碑统计
     * 
     * @param merchantId 商户ID
     */
    void updateReputationStatistics(Long merchantId);
    
    /**
     * 计算所有商户口碑排名
     * 
     * @param districtId 商圈ID (可选)
     * @param categoryId 类目ID (可选)
     */
    void calculateAllRankings(Long districtId, Long categoryId);
    
    /**
     * 获取商户在榜单中的排名
     * 
     * @param merchantId 商户ID
     * @param listType 榜单类型
     * @return 排名 (null表示未上榜)
     */
    Integer getMerchantRanking(Long merchantId, String listType);
}
