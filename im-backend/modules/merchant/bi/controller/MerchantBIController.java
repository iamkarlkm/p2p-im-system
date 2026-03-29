package com.im.backend.modules.merchant.bi.controller;

import com.im.backend.common.Result;
import com.im.backend.modules.merchant.bi.dto.*;
import com.im.backend.modules.merchant.bi.service.IMerchantBIDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 商户BI数据控制器
 * 提供经营数据看板、用户画像、营销效果等数据接口
 */
@RestController
@RequestMapping("/api/v1/merchant/bi")
@RequiredArgsConstructor
public class MerchantBIController {

    private final IMerchantBIDataService biDataService;

    /**
     * 获取经营数据看板
     */
    @PostMapping("/dashboard")
    public Result<BusinessDashboardResponse> getBusinessDashboard(@RequestBody BusinessStatsQueryRequest request) {
        return Result.success(biDataService.getBusinessDashboard(request));
    }

    /**
     * 获取用户画像分析
     */
    @PostMapping("/customer-profile")
    public Result<CustomerProfileResponse> getCustomerProfile(@RequestBody CustomerProfileQueryRequest request) {
        return Result.success(biDataService.getCustomerProfile(request));
    }

    /**
     * 获取营销效果追踪
     */
    @PostMapping("/marketing-effect")
    public Result<MarketingEffectResponse> getMarketingEffect(@RequestBody MarketingEffectQueryRequest request) {
        return Result.success(biDataService.getMarketingEffect(request));
    }

    /**
     * 获取转化漏斗
     */
    @GetMapping("/conversion-funnel")
    public Result<ConversionFunnelResponse> getConversionFunnel(
            @RequestParam Long merchantId,
            @RequestParam String funnelType,
            @RequestParam(required = false) String statsDate) {
        if (statsDate == null) {
            statsDate = java.time.LocalDate.now().toString();
        }
        return Result.success(biDataService.getConversionFunnel(merchantId, funnelType, statsDate));
    }

    /**
     * 获取竞品对标
     */
    @GetMapping("/competitor-benchmark")
    public Result<CompetitorBenchmarkResponse> getCompetitorBenchmark(
            @RequestParam Long merchantId,
            @RequestParam String benchmarkType) {
        return Result.success(biDataService.getCompetitorBenchmark(merchantId, benchmarkType));
    }
}
