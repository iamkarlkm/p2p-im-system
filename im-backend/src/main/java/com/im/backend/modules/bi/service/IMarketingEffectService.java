package com.im.backend.modules.bi.service;

import com.im.backend.modules.bi.dto.MarketingEffectResponse;

import java.time.LocalDate;

/**
 * 营销效果分析服务接口
 */
public interface IMarketingEffectService {

    /**
     * 获取营销效果分析
     */
    MarketingEffectResponse getMarketingEffect(Long merchantId, LocalDate startDate, LocalDate endDate);

    /**
     * 获取实时营销数据
     */
    MarketingEffectResponse getRealtimeMarketingData(Long merchantId);

    /**
     * 计算活动ROI
     */
    void calculateCampaignRoi(Long campaignId);
}
