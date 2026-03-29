package com.im.backend.modules.merchant.review.service;

import com.im.backend.modules.merchant.review.dto.MerchantReputationResponse;
import com.im.backend.modules.merchant.review.entity.MerchantReputationStats;

import java.util.List;

/**
 * 商户口碑统计服务接口
 */
public interface IMerchantReputationService {

    /**
     * 获取商户口碑统计
     */
    MerchantReputationResponse getMerchantReputation(Long merchantId);

    /**
     * 更新商户口碑统计
     */
    void updateMerchantReputation(Long merchantId);

    /**
     * 获取口碑榜单
     */
    List<MerchantReputationStats> getReputationRank(String rankType, Integer limit);

    /**
     * 获取商户在同商圈的排名
     */
    Integer getMerchantRanking(Long merchantId);
}
