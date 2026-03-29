package com.im.local.merchant.controller;

import com.im.local.merchant.dto.*;
import com.im.local.merchant.service.MerchantAnalyticsService;
import com.im.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商户数据分析与经营洞察控制器
 * 本地生活商户数据分析与经营洞察模块
 * 
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-28
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/local/merchant/analytics")
@RequiredArgsConstructor
public class MerchantAnalyticsController {
    
    private final MerchantAnalyticsService analyticsService;
    
    /**
     * 获取商户经营仪表盘
     * 
     * @param request 仪表盘请求参数
     * @return 仪表盘完整数据
     */
    @PostMapping("/dashboard")
    public ApiResponse<MerchantDashboardResponse> getDashboard(@RequestBody MerchantDashboardRequest request) {
        log.info("获取商户仪表盘, merchantId={}", request.getMerchantId());
        MerchantDashboardResponse response = analyticsService.getDashboard(request);
        return ApiResponse.success(response);
    }
    
    /**
     * 获取实时经营数据
     * 
     * @param merchantId 商户ID
     * @return 实时统计数据
     */
    @GetMapping("/realtime/{merchantId}")
    public ApiResponse<MerchantDashboardResponse.RealTimeStats> getRealTimeStats(
            @PathVariable Long merchantId) {
        log.info("获取实时统计数据, merchantId={}", merchantId);
        MerchantDashboardResponse.RealTimeStats stats = analyticsService.getRealTimeStats(merchantId);
        return ApiResponse.success(stats);
    }
    
    /**
     * 获取营收分析
     * 
     * @param request 请求参数
     * @return 营收分析结果
     */
    @PostMapping("/revenue")
    public ApiResponse<MerchantDashboardResponse.RevenueAnalysis> getRevenueAnalysis(
            @RequestBody MerchantDashboardRequest request) {
        log.info("获取营收分析, merchantId={}", request.getMerchantId());
        MerchantDashboardResponse.RevenueAnalysis analysis = analyticsService.getRevenueAnalysis(request);
        return ApiResponse.success(analysis);
    }
    
    /**
     * 获取客流分析
     * 
     * @param request 请求参数
     * @return 客流分析结果
     */
    @PostMapping("/traffic")
    public ApiResponse<MerchantDashboardResponse.TrafficAnalysis> getTrafficAnalysis(
            @RequestBody MerchantDashboardRequest request) {
        log.info("获取客流分析, merchantId={}", request.getMerchantId());
        MerchantDashboardResponse.TrafficAnalysis analysis = analyticsService.getTrafficAnalysis(request);
        return ApiResponse.success(analysis);
    }
    
    /**
     * 获取评价分析
     * 
     * @param request 请求参数
     * @return 评价分析结果
     */
    @PostMapping("/reviews")
    public ApiResponse<MerchantDashboardResponse.ReviewAnalysis> getReviewAnalysis(
            @RequestBody MerchantDashboardRequest request) {
        log.info("获取评价分析, merchantId={}", request.getMerchantId());
        MerchantDashboardResponse.ReviewAnalysis analysis = analyticsService.getReviewAnalysis(request);
        return ApiResponse.success(analysis);
    }
    
    /**
     * 获取竞品对比
     * 
     * @param request 请求参数
     * @return 竞品对比结果
     */
    @PostMapping("/competitor")
    public ApiResponse<MerchantDashboardResponse.CompetitorAnalysis> getCompetitorAnalysis(
            @RequestBody MerchantDashboardRequest request) {
        log.info("获取竞品对比, merchantId={}", request.getMerchantId());
        MerchantDashboardResponse.CompetitorAnalysis analysis = analyticsService.getCompetitorAnalysis(request);
        return ApiResponse.success(analysis);
    }
    
    /**
     * 获取经营洞察建议
     * 
     * @param merchantId 商户ID
     * @param limit 数量限制（默认10条）
     * @return 洞察建议列表
     */
    @GetMapping("/insights/{merchantId}")
    public ApiResponse<List<MerchantDashboardResponse.BusinessInsight>> getBusinessInsights(
            @PathVariable Long merchantId,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        log.info("获取经营洞察, merchantId={}, limit={}", merchantId, limit);
        List<MerchantDashboardResponse.BusinessInsight> insights = analyticsService.getBusinessInsights(merchantId, limit);
        return ApiResponse.success(insights);
    }
    
    /**
     * 获取顾客画像分析
     * 
     * @param merchantId 商户ID
     * @param timeRangeType 时间范围（TODAY/WEEK/MONTH/YEAR）
     * @return 顾客画像数据
     */
    @GetMapping("/customer-profile/{merchantId}")
    public ApiResponse<CustomerProfileResponse> getCustomerProfile(
            @PathVariable Long merchantId,
            @RequestParam(required = false, defaultValue = "MONTH") String timeRangeType) {
        log.info("获取顾客画像, merchantId={}, timeRange={}", merchantId, timeRangeType);
        CustomerProfileResponse response = analyticsService.getCustomerProfile(merchantId, timeRangeType);
        return ApiResponse.success(response);
    }
    
    /**
     * 获取RFM客户分层分析
     * 
     * @param merchantId 商户ID
     * @return RFM分析结果
     */
    @GetMapping("/rfm/{merchantId}")
    public ApiResponse<RfmAnalysisResponse> getRfmAnalysis(@PathVariable Long merchantId) {
        log.info("获取RFM分析, merchantId={}", merchantId);
        RfmAnalysisResponse response = analyticsService.getRfmAnalysis(merchantId);
        return ApiResponse.success(response);
    }
    
    /**
     * 获取客流高峰预测
     * 
     * @param merchantId 商户ID
     * @param forecastDays 预测天数（默认7天）
     * @return 客流预测数据
     */
    @GetMapping("/forecast/{merchantId}")
    public ApiResponse<TrafficForecastResponse> getTrafficForecast(
            @PathVariable Long merchantId,
            @RequestParam(required = false, defaultValue = "7") Integer forecastDays) {
        log.info("获取客流预测, merchantId={}, forecastDays={}", merchantId, forecastDays);
        TrafficForecastResponse response = analyticsService.getTrafficForecast(merchantId, forecastDays);
        return ApiResponse.success(response);
    }
    
    /**
     * 导出经营报表
     * 
     * @param request 导出请求参数
     * @return 报表文件URL
     */
    @PostMapping("/export")
    public ApiResponse<String> exportReport(@RequestBody ReportExportRequest request) {
        log.info("导出报表, merchantId={}, format={}", request.getMerchantId(), request.getFormat());
        String reportUrl = analyticsService.exportReport(request);
        return ApiResponse.success(reportUrl);
    }
    
    /**
     * 批量获取商户仪表盘（平台运营使用）
     * 
     * @param request 批量请求参数
     * @return 批量仪表盘数据
     */
    @PostMapping("/batch/dashboard")
    public ApiResponse<List<MerchantDashboardResponse>> getBatchDashboard(
            @RequestBody BatchDashboardRequest request) {
        log.info("批量获取仪表盘, merchantCount={}", request.getMerchantIds().size());
        List<MerchantDashboardResponse> responses = analyticsService.getBatchDashboard(
                request.getMerchantIds(), request.getBaseRequest());
        return ApiResponse.success(responses);
    }
}
