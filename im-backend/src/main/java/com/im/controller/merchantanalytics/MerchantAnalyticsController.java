// 商户数据分析控制器
package com.im.controller.merchantanalytics;

import com.im.dto.merchantanalytics.*;
import com.im.entity.merchantanalytics.*;
import com.im.service.merchantanalytics.MerchantAnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/merchant-analytics")
public class MerchantAnalyticsController {
    
    @Autowired
    private MerchantAnalyticsService analyticsService;
    
    // ==================== 经营仪表盘 ====================
    
    @GetMapping("/traffic/dashboard/{merchantId}")
    public MerchantTrafficDashboard getTrafficDashboard(
            @PathVariable Long merchantId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (date == null) date = LocalDate.now();
        return analyticsService.getTrafficDashboard(merchantId, date);
    }
    
    @GetMapping("/traffic/trend/{merchantId}")
    public List<MerchantTrafficDashboard> getTrafficTrend(
            @PathVariable Long merchantId,
            @RequestParam(defaultValue = "7") int days) {
        return analyticsService.getTrafficTrend(merchantId, days);
    }
    
    @GetMapping("/traffic/realtime/{merchantId}")
    public Map<String, Object> getRealTimeTraffic(@PathVariable Long merchantId) {
        return analyticsService.getRealTimeTraffic(merchantId);
    }
    
    @GetMapping("/traffic/source/{merchantId}")
    public Map<String, Object> getCustomerSourceAnalysis(
            @PathVariable Long merchantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return analyticsService.getCustomerSourceAnalysis(merchantId, startDate, endDate);
    }
    
    // ==================== 营收分析 ====================
    
    @GetMapping("/revenue/analysis/{merchantId}")
    public MerchantRevenueAnalysis getRevenueAnalysis(
            @PathVariable Long merchantId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (date == null) date = LocalDate.now();
        return analyticsService.getRevenueAnalysis(merchantId, date);
    }
    
    @GetMapping("/revenue/trend/{merchantId}")
    public List<MerchantRevenueAnalysis> getRevenueTrend(
            @PathVariable Long merchantId,
            @RequestParam(defaultValue = "7") int days) {
        return analyticsService.getRevenueTrend(merchantId, days);
    }
    
    @GetMapping("/revenue/payment-channel/{merchantId}")
    public Map<String, Object> getPaymentChannelAnalysis(
            @PathVariable Long merchantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return analyticsService.getPaymentChannelAnalysis(merchantId, startDate, endDate);
    }
    
    @GetMapping("/revenue/predict/{merchantId}")
    public Map<String, Object> predictRevenue(
            @PathVariable Long merchantId,
            @RequestParam(defaultValue = "7") int days) {
        return analyticsService.predictRevenue(merchantId, days);
    }
    
    // ==================== 评价分析 ====================
    
    @GetMapping("/review/analysis/{merchantId}")
    public MerchantReviewAnalysis getReviewAnalysis(
            @PathVariable Long merchantId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (date == null) date = LocalDate.now();
        return analyticsService.getReviewAnalysis(merchantId, date);
    }
    
    @GetMapping("/review/trend/{merchantId}")
    public List<MerchantReviewAnalysis> getReviewTrend(
            @PathVariable Long merchantId,
            @RequestParam(defaultValue = "7") int days) {
        return analyticsService.getReviewTrend(merchantId, days);
    }
    
    @GetMapping("/review/keywords/{merchantId}")
    public Map<String, Object> getReviewKeywords(
            @PathVariable Long merchantId,
            @RequestParam(defaultValue = "positive") String sentiment,
            @RequestParam(defaultValue = "10") int limit) {
        return analyticsService.getReviewKeywords(merchantId, sentiment, limit);
    }
    
    @GetMapping("/review/pending/{merchantId}")
    public List<Map<String, Object>> getPendingReplyReviews(
            @PathVariable Long merchantId,
            @RequestParam(defaultValue = "20") int limit) {
        return analyticsService.getPendingReplyReviews(merchantId, limit);
    }
    
    @GetMapping("/review/alert/{merchantId}")
    public Map<String, Object> getNegativeReviewAlert(@PathVariable Long merchantId) {
        return analyticsService.getNegativeReviewAlert(merchantId);
    }
    
    // ==================== 经营洞察 ====================
    
    @GetMapping("/insights/{merchantId}")
    public List<MerchantBusinessInsight> getBusinessInsights(
            @PathVariable Long merchantId,
            @RequestParam(required = false) String level,
            @RequestParam(defaultValue = "20") int limit) {
        return analyticsService.getBusinessInsights(merchantId, level, limit);
    }
    
    @GetMapping("/insights/unread-count/{merchantId}")
    public Integer getUnreadInsightCount(@PathVariable Long merchantId) {
        return analyticsService.getUnreadInsightCount(merchantId);
    }
    
    @PostMapping("/insights/read/{insightId}")
    public void markInsightAsRead(@PathVariable Long insightId) {
        analyticsService.markInsightAsRead(insightId);
    }
    
    @PostMapping("/insights/execute/{insightId}")
    public void executeInsightAction(
            @PathVariable Long insightId,
            @RequestBody Map<String, String> request) {
        analyticsService.executeInsightAction(insightId, request.get("actionResult"));
    }
    
    @GetMapping("/insights/peak-prediction/{merchantId}")
    public Map<String, Object> predictPeakHours(
            @PathVariable Long merchantId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (date == null) date = LocalDate.now();
        return analyticsService.predictPeakHours(merchantId, date);
    }
    
    @GetMapping("/insights/hot-products/{merchantId}")
    public List<Map<String, Object>> identifyHotProducts(
            @PathVariable Long merchantId,
            @RequestParam(defaultValue = "10") int limit) {
        return analyticsService.identifyHotProducts(merchantId, limit);
    }
    
    @GetMapping("/insights/marketing-effect/{merchantId}")
    public Map<String, Object> analyzeMarketingEffect(
            @PathVariable Long merchantId,
            @RequestParam Long activityId) {
        return analyticsService.analyzeMarketingEffect(merchantId, activityId);
    }
    
    @GetMapping("/insights/alerts/{merchantId}")
    public List<Map<String, Object>> getBusinessAlerts(@PathVariable Long merchantId) {
        return analyticsService.getBusinessAlerts(merchantId);
    }
    
    // ==================== 顾客画像 ====================
    
    @GetMapping("/customer/profile/{merchantId}")
    public CustomerProfileAnalysis getCustomerProfile(
            @PathVariable Long merchantId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (date == null) date = LocalDate.now();
        return analyticsService.getCustomerProfile(merchantId, date);
    }
    
    @GetMapping("/customer/rfm/{merchantId}")
    public Map<String, Object> getRFMSegments(@PathVariable Long merchantId) {
        return analyticsService.getRFMSegments(merchantId);
    }
    
    @GetMapping("/customer/geo-distribution/{merchantId}")
    public List<Map<String, Object>> getCustomerGeoDistribution(@PathVariable Long merchantId) {
        return analyticsService.getCustomerGeoDistribution(merchantId);
    }
    
    @GetMapping("/customer/frequency/{merchantId}")
    public Map<String, Object> getCustomerFrequencyDistribution(@PathVariable Long merchantId) {
        return analyticsService.getCustomerFrequencyDistribution(merchantId);
    }
    
    @GetMapping("/customer/churn-risk/{merchantId}")
    public List<Map<String, Object>> getChurnRiskCustomers(
            @PathVariable Long merchantId,
            @RequestParam(defaultValue = "20") int limit) {
        return analyticsService.getChurnRiskCustomers(merchantId, limit);
    }
    
    @GetMapping("/customer/high-value/{merchantId}")
    public List<Map<String, Object>> getHighValueCustomers(
            @PathVariable Long merchantId,
            @RequestParam(defaultValue = "20") int limit) {
        return analyticsService.getHighValueCustomers(merchantId, limit);
    }
    
    // ==================== 竞品分析 ====================
    
    @GetMapping("/competitor/list/{merchantId}")
    public List<CompetitorAnalysis> getCompetitors(@PathVariable Long merchantId) {
        return analyticsService.getCompetitors(merchantId);
    }
    
    @GetMapping("/competitor/comparison/{merchantId}")
    public Map<String, Object> getCompetitorComparison(
            @PathVariable Long merchantId,
            @RequestParam Long competitorId) {
        return analyticsService.getCompetitorComparison(merchantId, competitorId);
    }
    
    @GetMapping("/competitor/ranking/{merchantId}")
    public Map<String, Object> getMarketRanking(@PathVariable Long merchantId) {
        return analyticsService.getMarketRanking(merchantId);
    }
    
    @GetMapping("/competitor/market-share/{merchantId}")
    public Map<String, Object> getMarketShare(@PathVariable Long merchantId) {
        return analyticsService.getMarketShare(merchantId);
    }
    
    // ==================== 综合报表 ====================
    
    @GetMapping("/report/daily/{merchantId}")
    public Map<String, Object> generateDailyReport(
            @PathVariable Long merchantId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (date == null) date = LocalDate.now();
        return analyticsService.generateDailyReport(merchantId, date);
    }
    
    @GetMapping("/report/weekly/{merchantId}")
    public Map<String, Object> generateWeeklyReport(
            @PathVariable Long merchantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekEndDate) {
        return analyticsService.generateWeeklyReport(merchantId, weekEndDate);
    }
    
    @GetMapping("/report/monthly/{merchantId}")
    public Map<String, Object> generateMonthlyReport(
            @PathVariable Long merchantId,
            @RequestParam int year,
            @RequestParam int month) {
        return analyticsService.generateMonthlyReport(merchantId, year, month);
    }
    
    @GetMapping("/kpi/overview/{merchantId}")
    public Map<String, Object> getKPIOverview(@PathVariable Long merchantId) {
        return analyticsService.getKPIOverview(merchantId);
    }
}
