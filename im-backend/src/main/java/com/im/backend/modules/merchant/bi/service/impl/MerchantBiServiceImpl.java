package com.im.backend.modules.merchant.bi.service.impl;

import com.im.backend.common.Result;
import com.im.backend.modules.merchant.bi.dto.BusinessOverviewResponse;
import com.im.backend.modules.merchant.bi.dto.DailyReportResponse;
import com.im.backend.modules.merchant.bi.entity.MerchantBiDailyReport;
import com.im.backend.modules.merchant.bi.repository.MerchantBiDailyReportMapper;
import com.im.backend.modules.merchant.bi.service.IMerchantBiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商户BI数据服务实现 - 功能#312: 商家BI数据智能平台
 */
@Service
@RequiredArgsConstructor
public class MerchantBiServiceImpl implements IMerchantBiService {

    private final MerchantBiDailyReportMapper reportMapper;

    @Override
    public Result<BusinessOverviewResponse> getBusinessOverview(Long merchantId) {
        BusinessOverviewResponse overview = new BusinessOverviewResponse();
        
        // 今日数据
        MerchantBiDailyReport today = reportMapper.selectByDate(merchantId, LocalDate.now());
        if (today != null) {
            overview.setTodayOrderCount(today.getOrderCount());
            overview.setTodayRevenue(today.getActualAmount());
            overview.setTodayVisitors(today.getVisitorCount());
        }
        
        // 近7天
        List<MerchantBiDailyReport> weekReports = reportMapper.selectByDateRange(
            merchantId, LocalDate.now().minusDays(7), LocalDate.now());
        overview.setWeekOrderCount(weekReports.stream().mapToInt(MerchantBiDailyReport::getOrderCount).sum());
        overview.setWeekRevenue(weekReports.stream()
            .map(MerchantBiDailyReport::getActualAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add));
        
        // 近30天
        List<MerchantBiDailyReport> monthReports = reportMapper.selectByDateRange(
            merchantId, LocalDate.now().minusDays(30), LocalDate.now());
        overview.setMonthOrderCount(monthReports.stream().mapToInt(MerchantBiDailyReport::getOrderCount).sum());
        overview.setMonthRevenue(monthReports.stream()
            .map(MerchantBiDailyReport::getActualAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add));
        
        // 好评率计算
        int totalReviews = monthReports.stream()
            .mapToInt(r -> r.getGoodReviewCount() + r.getBadReviewCount()).sum();
        int goodReviews = monthReports.stream().mapToInt(MerchantBiDailyReport::getGoodReviewCount).sum();
        if (totalReviews > 0) {
            overview.setGoodReviewRate(new BigDecimal(goodReviews * 100)
                .divide(new BigDecimal(totalReviews), 1, BigDecimal.ROUND_HALF_UP));
        } else {
            overview.setGoodReviewRate(BigDecimal.ZERO);
        }
        
        overview.setPendingOrders(0); // 从订单服务获取
        
        return Result.success(overview);
    }

    @Override
    public List<DailyReportResponse> getDailyReports(Long merchantId, LocalDate startDate, LocalDate endDate) {
        List<MerchantBiDailyReport> reports = reportMapper.selectByDateRange(merchantId, startDate, endDate);
        return reports.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    public DailyReportResponse getTodayRealtime(Long merchantId) {
        MerchantBiDailyReport report = reportMapper.selectByDate(merchantId, LocalDate.now());
        return report != null ? convertToResponse(report) : new DailyReportResponse();
    }

    private DailyReportResponse convertToResponse(MerchantBiDailyReport report) {
        DailyReportResponse response = new DailyReportResponse();
        BeanUtils.copyProperties(report, response);
        return response;
    }
}
