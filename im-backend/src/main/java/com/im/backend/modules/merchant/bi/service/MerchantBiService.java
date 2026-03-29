package com.im.backend.modules.merchant.bi.service;

import com.im.backend.modules.merchant.bi.dto.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 商家BI数据智能平台服务接口
 */
public interface MerchantBiService {
    
    /**
     * 获取商家经营数据看板
     */
    MerchantDashboardResponse getDashboard(Long merchantId);
    
    /**
     * 获取实时经营指标
     */
    MerchantDashboardResponse.RealtimeMetricsDTO getRealtimeMetrics(Long merchantId);
    
    /**
     * 获取经营趋势数据
     */
    List<MerchantDashboardResponse.TrendDataDTO> getTrendData(Long merchantId, LocalDate startDate, LocalDate endDate);
    
    /**
     * 获取顾客地域分布
     */
    CustomerGeoDistributionResponse getCustomerGeoDistribution(Long merchantId, String regionLevel);
    
    /**
     * 获取顾客画像分析
     */
    CustomerPortraitResponse getCustomerPortrait(Long merchantId);
    
    /**
     * 获取优惠券效果分析
     */
    CouponAnalyticsResponse getCouponAnalytics(Long merchantId, LocalDate startDate, LocalDate endDate);
    
    /**
     * 获取营销活动效果分析
     */
    CampaignAnalyticsResponse getCampaignAnalytics(Long merchantId, LocalDate startDate, LocalDate endDate);
    
    /**
     * 获取异常预警列表
     */
    List<MerchantDashboardResponse.AlertDTO> getAlerts(Long merchantId);
    
    /**
     * 标记预警已读
     */
    void markAlertAsRead(Long alertId);
    
    /**
     * 处理预警
     */
    void processAlert(Long alertId, String remark);
    
    /**
     * 刷新实时数据
     */
    void refreshRealtimeMetrics(Long merchantId);
    
    /**
     * 生成日报表
     */
    void generateDailyReport(Long merchantId, LocalDate reportDate);
    
    /**
     * 对比分析
     */
    Object getCompareAnalysis(Long merchantId, String compareType, LocalDate date);
}
