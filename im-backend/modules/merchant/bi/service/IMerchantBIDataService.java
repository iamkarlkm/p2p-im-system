package com.im.backend.modules.merchant.bi.service;

import com.im.backend.modules.merchant.bi.dto.*;

/**
 * 商户BI数据服务接口
 */
public interface IMerchantBIDataService {

    /**
     * 获取经营数据看板
     */
    BusinessDashboardResponse getBusinessDashboard(BusinessStatsQueryRequest request);

    /**
     * 获取用户画像分析
     */
    CustomerProfileResponse getCustomerProfile(CustomerProfileQueryRequest request);

    /**
     * 获取营销效果追踪
     */
    MarketingEffectResponse getMarketingEffect(MarketingEffectQueryRequest request);

    /**
     * 获取转化漏斗数据
     */
    ConversionFunnelResponse getConversionFunnel(Long merchantId, String funnelType, String statsDate);

    /**
     * 获取竞品对标数据
     */
    CompetitorBenchmarkResponse getCompetitorBenchmark(Long merchantId, String benchmarkType);
}
