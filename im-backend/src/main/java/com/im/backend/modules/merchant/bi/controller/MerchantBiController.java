package com.im.backend.modules.merchant.bi.controller;

import com.im.backend.common.Result;
import com.im.backend.modules.merchant.bi.dto.BusinessOverviewResponse;
import com.im.backend.modules.merchant.bi.dto.DailyReportResponse;
import com.im.backend.modules.merchant.bi.service.IMerchantBiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 商户BI数据控制器 - 功能#312: 商家BI数据智能平台
 */
@Tag(name = "商家BI数据", description = "商家BI数据智能平台相关接口")
@RestController
@RequestMapping("/api/merchant/bi")
@RequiredArgsConstructor
public class MerchantBiController {

    private final IMerchantBiService biService;

    @Operation(summary = "获取经营概览")
    @GetMapping("/overview/{merchantId}")
    public Result<BusinessOverviewResponse> getBusinessOverview(@PathVariable Long merchantId) {
        return biService.getBusinessOverview(merchantId);
    }

    @Operation(summary = "获取日报列表")
    @GetMapping("/daily-reports/{merchantId}")
    public Result<List<DailyReportResponse>> getDailyReports(
            @PathVariable Long merchantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.success(biService.getDailyReports(merchantId, startDate, endDate));
    }

    @Operation(summary = "获取今日实时数据")
    @GetMapping("/today/{merchantId}")
    public Result<DailyReportResponse> getTodayRealtime(@PathVariable Long merchantId) {
        return Result.success(biService.getTodayRealtime(merchantId));
    }
}
