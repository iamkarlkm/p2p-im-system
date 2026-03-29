// 商户数据分析服务
package com.im.service.merchantanalytics;

import com.im.entity.merchantanalytics.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface MerchantAnalyticsService {
    
    // ==================== 经营仪表盘 ====================
    
    /**
     * 获取商户实时经营仪表盘
     */
    MerchantTrafficDashboard getTrafficDashboard(Long merchantId, LocalDate date);
    
    /**
     * 获取经营趋势（7日/30日）
     */
    List<MerchantTrafficDashboard> getTrafficTrend(Long merchantId, int days);
    
    /**
     * 获取实时客流
     */
    Map<String, Object> getRealTimeTraffic(Long merchantId);
    
    /**
     * 获取客户来源分析
     */
    Map<String, Object> getCustomerSourceAnalysis(Long merchantId, LocalDate startDate, LocalDate endDate);
    
    // ==================== 营收分析 ====================
    
    /**
     * 获取营收分析
     */
    MerchantRevenueAnalysis getRevenueAnalysis(Long merchantId, LocalDate date);
    
    /**
     * 获取营收趋势
     */
    List<MerchantRevenueAnalysis> getRevenueTrend(Long merchantId, int days);
    
    /**
     * 获取支付渠道分析
     */
    Map<String, Object> getPaymentChannelAnalysis(Long merchantId, LocalDate startDate, LocalDate endDate);
    
    /**
     * 获取营收预测
     */
    Map<String, Object> predictRevenue(Long merchantId, int days);
    
    // ==================== 评价分析 ====================
    
    /**
     * 获取评价分析
     */
    MerchantReviewAnalysis getReviewAnalysis(Long merchantId, LocalDate date);
    
    /**
     * 获取评价趋势
     */
    List<MerchantReviewAnalysis> getReviewTrend(Long merchantId, int days);
    
    /**
     * 获取评价关键词
     */
    Map<String, Object> getReviewKeywords(Long merchantId, String sentiment, int limit);
    
    /**
     * 获取待回复评价
     */
    List<Map<String, Object>> getPendingReplyReviews(Long merchantId, int limit);
    
    /**
     * 获取差评预警
     */
    Map<String, Object> getNegativeReviewAlert(Long merchantId);
    
    // ==================== 经营洞察 ====================
    
    /**
     * 获取智能经营洞察
     */
    List<MerchantBusinessInsight> getBusinessInsights(Long merchantId, String level, int limit);
    
    /**
     * 获取未读洞察数量
     */
    Integer getUnreadInsightCount(Long merchantId);
    
    /**
     * 标记洞察已读
     */
    void markInsightAsRead(Long insightId);
    
    /**
     * 执行洞察建议行动
     */
    void executeInsightAction(Long insightId, String actionResult);
    
    /**
     * 客流高峰预测
     */
    Map<String, Object> predictPeakHours(Long merchantId, LocalDate date);
    
    /**
     * 爆款商品识别
     */
    List<Map<String, Object>> identifyHotProducts(Long merchantId, int limit);
    
    /**
     * 营销活动效果分析
     */
    Map<String, Object> analyzeMarketingEffect(Long merchantId, Long activityId);
    
    /**
     * 异常经营预警
     */
    List<Map<String, Object>> getBusinessAlerts(Long merchantId);
    
    // ==================== 顾客画像 ====================
    
    /**
     * 获取顾客画像
     */
    CustomerProfileAnalysis getCustomerProfile(Long merchantId, LocalDate date);
    
    /**
     * 获取RFM客群分析
     */
    Map<String, Object> getRFMSegments(Long merchantId);
    
    /**
     * 获取顾客地域分布
     */
    List<Map<String, Object>> getCustomerGeoDistribution(Long merchantId);
    
    /**
     * 获取顾客消费频次
     */
    Map<String, Object> getCustomerFrequencyDistribution(Long merchantId);
    
    /**
     * 获取流失风险客户
     */
    List<Map<String, Object>> getChurnRiskCustomers(Long merchantId, int limit);
    
    /**
     * 获取高价值客户
     */
    List<Map<String, Object>> getHighValueCustomers(Long merchantId, int limit);
    
    // ==================== 竞品分析 ====================
    
    /**
     * 获取竞品列表
     */
    List<CompetitorAnalysis> getCompetitors(Long merchantId);
    
    /**
     * 获取竞品对比分析
     */
    Map<String, Object> getCompetitorComparison(Long merchantId, Long competitorId);
    
    /**
     * 获取市场排名
     */
    Map<String, Object> getMarketRanking(Long merchantId);
    
    /**
     * 获取市场份额
     */
    Map<String, Object> getMarketShare(Long merchantId);
    
    // ==================== 综合报表 ====================
    
    /**
     * 生成日报
     */
    Map<String, Object> generateDailyReport(Long merchantId, LocalDate date);
    
    /**
     * 生成周报
     */
    Map<String, Object> generateWeeklyReport(Long merchantId, LocalDate weekEndDate);
    
    /**
     * 生成月报
     */
    Map<String, Object> generateMonthlyReport(Long merchantId, int year, int month);
    
    /**
     * 获取核心指标概览
     */
    Map<String, Object> getKPIOverview(Long merchantId);
}
