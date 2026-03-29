package com.im.backend.modules.merchant.bi.controller;

import com.im.backend.common.response.Result;
import com.im.backend.modules.merchant.bi.dto.*;
import com.im.backend.modules.merchant.bi.service.MerchantBiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 商家BI数据智能平台控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/merchant/bi")
@RequiredArgsConstructor
@Tag(name = "商家BI数据智能平台", description = "经营数据看板、用户画像、营销效果分析")
@Validated
public class MerchantBiController {

    private final MerchantBiService merchantBiService;

    /**
     * 获取经营数据看板
     */
    @GetMapping("/dashboard/{merchantId}")
    @Operation(summary = "获取经营数据看板", description = "获取商家实时经营数据、今日/昨日/本周/本月数据概览")
    public Result<MerchantDashboardResponse> getDashboard(@PathVariable Long merchantId) {
        return Result.success(merchantBiService.getDashboard(merchantId));
    }

    /**
     * 获取实时经营指标
     */
    @GetMapping("/realtime/{merchantId}")
    @Operation(summary = "获取实时经营指标", description = "获取商家实时营业额、订单量、客流量等指标")
    public Result<MerchantDashboardResponse.RealtimeMetricsDTO> getRealtimeMetrics(@PathVariable Long merchantId) {
        return Result.success(merchantBiService.getRealtimeMetrics(merchantId));
    }

    /**
     * 获取经营趋势数据
     */
    @GetMapping("/trends/{merchantId}")
    @Operation(summary = "获取经营趋势数据", description = "获取指定日期范围内的经营趋势数据")
    public Result<List<MerchantDashboardResponse.TrendDataDTO>> getTrendData(
            @PathVariable Long merchantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.success(merchantBiService.getTrendData(merchantId, startDate, endDate));
    }

    /**
     * 获取顾客地域分布
     */
    @GetMapping("/geo-distribution/{merchantId}")
    @Operation(summary = "获取顾客地域分布", description = "获取顾客来源地域分布数据，支持省/市/区三级")
    public Result<CustomerGeoDistributionResponse> getCustomerGeoDistribution(
            @PathVariable Long merchantId,
            @RequestParam(defaultValue = "city") String regionLevel) {
        return Result.success(merchantBiService.getCustomerGeoDistribution(merchantId, regionLevel));
    }

    /**
     * 获取顾客画像分析
     */
    @GetMapping("/customer-portrait/{merchantId}")
    @Operation(summary = "获取顾客画像分析", description = "获取顾客年龄段、性别、消费水平、偏好等画像数据")
    public Result<CustomerPortraitResponse> getCustomerPortrait(@PathVariable Long merchantId) {
        return Result.success(merchantBiService.getCustomerPortrait(merchantId));
    }

    /**
     * 获取优惠券效果分析
     */
    @GetMapping("/coupon-analytics/{merchantId}")
    @Operation(summary = "获取优惠券效果分析", description = "获取优惠券发放、领取、使用情况统计及ROI分析")
    public Result<CouponAnalyticsResponse> getCouponAnalytics(
            @PathVariable Long merchantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.success(merchantBiService.getCouponAnalytics(merchantId, startDate, endDate));
    }

    /**
     * 获取营销活动效果分析
     */
    @GetMapping("/campaign-analytics/{merchantId}")
    @Operation(summary = "获取营销活动效果分析", description = "获取营销活动曝光、点击、转化等效果数据")
    public Result<CampaignAnalyticsResponse> getCampaignAnalytics(
            @PathVariable Long merchantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.success(merchantBiService.getCampaignAnalytics(merchantId, startDate, endDate));
    }

    /**
     * 获取异常预警列表
     */
    @GetMapping("/alerts/{merchantId}")
    @Operation(summary = "获取异常预警列表", description = "获取商家经营异常预警信息")
    public Result<List<MerchantDashboardResponse.AlertDTO>> getAlerts(@PathVariable Long merchantId) {
        return Result.success(merchantBiService.getAlerts(merchantId));
    }

    /**
     * 标记预警已读
     */
    @PutMapping("/alerts/{alertId}/read")
    @Operation(summary = "标记预警已读", description = "将指定预警标记为已读状态")
    public Result<Void> markAlertAsRead(@PathVariable Long alertId) {
        merchantBiService.markAlertAsRead(alertId);
        return Result.success();
    }

    /**
     * 处理预警
     */
    @PutMapping("/alerts/{alertId}/process")
    @Operation(summary = "处理预警", description = "处理预警并添加处理备注")
    public Result<Void> processAlert(@PathVariable Long alertId, @RequestParam String remark) {
        merchantBiService.processAlert(alertId, remark);
        return Result.success();
    }

    /**
     * 刷新实时数据
     */
    @PostMapping("/refresh/{merchantId}")
    @Operation(summary = "刷新实时数据", description = "手动触发实时数据刷新")
    public Result<Void> refreshRealtimeMetrics(@PathVariable Long merchantId) {
        merchantBiService.refreshRealtimeMetrics(merchantId);
        return Result.success();
    }

    /**
     * 生成日报表
     */
    @PostMapping("/daily-report/{merchantId}")
    @Operation(summary = "生成日报表", description = "生成指定日期的经营日报表")
    public Result<Void> generateDailyReport(
            @PathVariable Long merchantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate reportDate) {
        merchantBiService.generateDailyReport(merchantId, reportDate);
        return Result.success();
    }

    /**
     * 对比分析
     */
    @GetMapping("/compare/{merchantId}")
    @Operation(summary = "对比分析", description = "获取对比分析数据(同比/环比)")
    public Result<Object> getCompareAnalysis(
            @PathVariable Long merchantId,
            @RequestParam String compareType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return Result.success(merchantBiService.getCompareAnalysis(merchantId, compareType, date));
    }
}
