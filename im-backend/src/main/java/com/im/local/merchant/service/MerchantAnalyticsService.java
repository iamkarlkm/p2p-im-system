package com.im.local.merchant.service;

import com.im.local.merchant.dto.*;
import java.util.List;

/**
 * 商户数据分析与经营洞察服务接口
 * 本地生活商户数据分析与经营洞察模块
 * 
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-28
 */
public interface MerchantAnalyticsService {
    
    /**
     * 获取商户经营仪表盘数据
     * 
     * @param request 仪表盘请求参数
     * @return 仪表盘完整数据
     */
    MerchantDashboardResponse getDashboard(MerchantDashboardRequest request);
    
    /**
     * 获取实时经营数据
     * 
     * @param merchantId 商户ID
     * @return 实时统计数据
     */
    MerchantDashboardResponse.RealTimeStats getRealTimeStats(Long merchantId);
    
    /**
     * 获取营收分析数据
     * 
     * @param request 请求参数
     * @return 营收分析结果
     */
    MerchantDashboardResponse.RevenueAnalysis getRevenueAnalysis(MerchantDashboardRequest request);
    
    /**
     * 获取客流分析数据
     * 
     * @param request 请求参数
     * @return 客流分析结果
     */
    MerchantDashboardResponse.TrafficAnalysis getTrafficAnalysis(MerchantDashboardRequest request);
    
    /**
     * 获取评价分析数据
     * 
     * @param request 请求参数
     * @return 评价分析结果
     */
    MerchantDashboardResponse.ReviewAnalysis getReviewAnalysis(MerchantDashboardRequest request);
    
    /**
     * 获取竞品对比数据
     * 
     * @param request 请求参数
     * @return 竞品分析结果
     */
    MerchantDashboardResponse.CompetitorAnalysis getCompetitorAnalysis(MerchantDashboardRequest request);
    
    /**
     * 获取经营洞察建议
     * 
     * @param merchantId 商户ID
     * @param limit 数量限制
     * @return 洞察建议列表
     */
    List<MerchantDashboardResponse.BusinessInsight> getBusinessInsights(Long merchantId, Integer limit);
    
    /**
     * 获取顾客画像分析
     * 
     * @param merchantId 商户ID
     * @param timeRangeType 时间范围
     * @return 顾客画像数据
     */
    CustomerProfileResponse getCustomerProfile(Long merchantId, String timeRangeType);
    
    /**
     * 获取RFM客户分层分析
     * 
     * @param merchantId 商户ID
     * @return RFM分析结果
     */
    RfmAnalysisResponse getRfmAnalysis(Long merchantId);
    
    /**
     * 获取客流高峰预测
     * 
     * @param merchantId 商户ID
     * @param forecastDays 预测天数
     * @return 客流预测数据
     */
    TrafficForecastResponse getTrafficForecast(Long merchantId, Integer forecastDays);
    
    /**
     * 导出经营报表
     * 
     * @param request 导出请求
     * @return 报表文件URL
     */
    String exportReport(ReportExportRequest request);
    
    /**
     * 批量获取多商户数据（平台运营使用）
     * 
     * @param merchantIds 商户ID列表
     * @param request 基础请求参数
     * @return 批量仪表盘数据
     */
    List<MerchantDashboardResponse> getBatchDashboard(List<Long> merchantIds, MerchantDashboardRequest request);
}
