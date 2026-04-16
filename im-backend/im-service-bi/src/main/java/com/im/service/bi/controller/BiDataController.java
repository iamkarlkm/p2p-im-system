package com.im.service.bi.controller;

import com.im.service.bi.dto.*;
import com.im.service.bi.service.IBusinessDataService;
import com.im.service.bi.service.ICustomerPortraitService;
import com.im.service.bi.service.IMarketingEffectService;
import com.im.service.bi.service.ICompetitorBenchmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 商户BI数据智能平台控制器
 * 提供经营数据看板、用户画像、营销效果、竞品对标等分析接口
 */
@RestController
@RequestMapping("/api/v1/bi")
@RequiredArgsConstructor
public class BiDataController {

    private final IBusinessDataService businessDataService;
    private final ICustomerPortraitService customerPortraitService;
    private final IMarketingEffectService marketingEffectService;
    private final ICompetitorBenchmarkService competitorBenchmarkService;

    /**
     * 获取经营数据看板
     */
    @GetMapping("/dashboard/{merchantId}")
    public BusinessDashboardResponse getDashboard(@PathVariable Long merchantId) {
        return businessDataService.getDashboard(merchantId);
    }

    /**
     * 获取实时经营指标
     */
    @GetMapping("/dashboard/{merchantId}/realtime")
    public BusinessDashboardResponse getRealtimeMetrics(@PathVariable Long merchantId) {
        return businessDataService.getRealtimeMetrics(merchantId);
    }

    /**
     * 获取经营报表
     */
    @PostMapping("/report")
    public BusinessReportResponse getBusinessReport(@RequestBody BusinessReportRequest request) {
        return businessDataService.getBusinessReport(request);
    }

    /**
     * 获取用户画像分析
     */
    @GetMapping("/portrait/{merchantId}")
    public CustomerPortraitResponse getCustomerPortrait(@PathVariable Long merchantId) {
        return customerPortraitService.getCustomerPortrait(merchantId);
    }

    /**
     * 刷新用户画像
     */
    @PostMapping("/portrait/{merchantId}/refresh")
    public Void refreshCustomerPortrait(@PathVariable Long merchantId) {
        customerPortraitService.refreshCustomerPortrait(merchantId);
        return null;
    }

    /**
     * 获取地域分布热力图
     */
    @GetMapping("/portrait/{merchantId}/heatmap")
    public byte[] getRegionHeatmap(@PathVariable Long merchantId) {
        return customerPortraitService.getRegionHeatmap(merchantId);
    }

    /**
     * 获取营销效果分析
     */
    @GetMapping("/marketing/{merchantId}")
    public MarketingEffectResponse getMarketingEffect(
            @PathVariable Long merchantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return marketingEffectService.getMarketingEffect(merchantId, startDate, endDate);
    }

    /**
     * 获取实时营销数据
     */
    @GetMapping("/marketing/{merchantId}/realtime")
    public MarketingEffectResponse getRealtimeMarketingData(@PathVariable Long merchantId) {
        return marketingEffectService.getRealtimeMarketingData(merchantId);
    }

    /**
     * 获取竞品对标分析
     */
    @GetMapping("/benchmark/{merchantId}")
    public CompetitorBenchmarkResponse getBenchmarkAnalysis(@PathVariable Long merchantId) {
        return competitorBenchmarkService.getBenchmarkAnalysis(merchantId);
    }

    /**
     * 刷新竞品对标数据
     */
    @PostMapping("/benchmark/{merchantId}/refresh")
    public Void refreshBenchmarkData(@PathVariable Long merchantId) {
        competitorBenchmarkService.refreshBenchmarkData(merchantId);
        return null;
    }

    /**
     * 获取商圈排名
     */
    @GetMapping("/benchmark/district/{districtId}")
    public CompetitorBenchmarkResponse getDistrictRanking(
            @PathVariable Long districtId,
            @RequestParam String category) {
        return competitorBenchmarkService.getDistrictRanking(districtId, category);
    }
}
