package com.im.local.review.service;

import com.im.local.review.dto.MerchantReputationResponse;

/**
 * 商户口碑统计服务接口
 */
public interface IMerchantReputationService {

    /**
     * 计算并更新商户口碑统计
     */
    void calculateReputation(Long merchantId);

    /**
     * 获取商户口碑信息
     */
    MerchantReputationResponse getReputation(Long merchantId);

    /**
     * 批量更新口碑统计（定时任务）
     */
    void batchUpdateReputation();

    /**
     * 获取商圈排行榜
     */
    Object getDistrictRanking(Long districtId, Integer page, Integer size);

    /**
     * 获取分类排行榜
     */
    Object getCategoryRanking(Integer categoryId, Integer page, Integer size);
}
