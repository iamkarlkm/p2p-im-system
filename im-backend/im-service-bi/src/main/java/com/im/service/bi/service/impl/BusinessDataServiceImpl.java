package com.im.service.bi.service.impl;

import com.im.service.bi.dto.BusinessDashboardResponse;
import com.im.service.bi.dto.BusinessReportRequest;
import com.im.service.bi.dto.BusinessReportResponse;
import com.im.service.bi.entity.MerchantBusinessDaily;
import com.im.service.bi.repository.MerchantBusinessDailyMapper;
import com.im.service.bi.service.IBusinessDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 经营数据服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BusinessDataServiceImpl implements IBusinessDataService {

    private final MerchantBusinessDailyMapper dailyMapper;

    @Override
    public BusinessDashboardResponse getDashboard(Long merchantId) {
        BusinessDashboardResponse response = new BusinessDashboardResponse();
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        // 今日数据
        MerchantBusinessDaily todayData = dailyMapper.selectToday(merchantId, today);
        if (todayData != null) {
            response.setTodayRevenue(todayData.getRevenue());
            response.setTodayOrderCount(todayData.getOrderCount());
            response.setTodayCustomerCount(todayData.getCustomerCount());
            response.setTodayAvgOrderValue(todayData.getAvgOrderValue());
        }

        // 昨日数据计算环比
        MerchantBusinessDaily yesterdayData = dailyMapper.selectYesterday(merchantId, yesterday);
        if (yesterdayData != null && todayData != null) {
            response.setRevenueMom(calculateMom(todayData.getRevenue(), yesterdayData.getRevenue()));
            response.setOrderMom(calculateMom(
                new BigDecimal(todayData.getOrderCount()),
                new BigDecimal(yesterdayData.getOrderCount())));
            response.setCustomerMom(calculateMom(
                new BigDecimal(todayData.getCustomerCount()),
                new BigDecimal(yesterdayData.getCustomerCount())));
        }

        // 近7天趋势
        response.setWeeklyTrend(getTrendData(merchantId, today.minusDays(6), today));
        // 近30天趋势
        response.setMonthlyTrend(getTrendData(merchantId, today.minusDays(29), today));

        return response;
    }

    @Override
    public BusinessReportResponse getBusinessReport(BusinessReportRequest request) {
        List<MerchantBusinessDaily> dataList = dailyMapper.selectByDateRange(
            request.getMerchantId(), request.getStartDate(), request.getEndDate());

        BusinessReportResponse response = new BusinessReportResponse();
        List<BusinessReportResponse.ReportItem> items = new ArrayList<>();

        BigDecimal totalRevenue = BigDecimal.ZERO;
        int totalOrders = 0;
        int totalCustomers = 0;
        BigDecimal totalRefund = BigDecimal.ZERO;

        for (MerchantBusinessDaily data : dataList) {
            BusinessReportResponse.ReportItem item = new BusinessReportResponse.ReportItem();
            item.setDate(data.getStatDate().toString());
            item.setRevenue(data.getRevenue());
            item.setOrderCount(data.getOrderCount());
            item.setCustomerCount(data.getCustomerCount());
            item.setAvgOrderValue(data.getAvgOrderValue());
            item.setNewCustomerCount(data.getNewCustomerCount());
            item.setOldCustomerCount(data.getOldCustomerCount());
            item.setRefundAmount(data.getRefundAmount());
            items.add(item);

            totalRevenue = totalRevenue.add(data.getRevenue());
            totalOrders += data.getOrderCount();
            totalCustomers += data.getCustomerCount();
            totalRefund = totalRefund.add(data.getRefundAmount());
        }

        response.setReportData(items);

        // 汇总数据
        BusinessReportResponse.ReportSummary summary = new BusinessReportResponse.ReportSummary();
        summary.setTotalRevenue(totalRevenue);
        summary.setTotalOrderCount(totalOrders);
        summary.setTotalCustomerCount(totalCustomers);
        if (totalOrders > 0) {
            summary.setAvgOrderValue(totalRevenue.divide(new BigDecimal(totalOrders), 2, RoundingMode.HALF_UP));
        }
        summary.setTotalRefundAmount(totalRefund);
        if (totalRevenue.compareTo(BigDecimal.ZERO) > 0) {
            summary.setRefundRate(totalRefund.divide(totalRevenue, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100)));
        }
        response.setSummary(summary);

        return response;
    }

    @Override
    public BusinessDashboardResponse getRealtimeMetrics(Long merchantId) {
        // 实时数据从缓存或实时计算获取
        return getDashboard(merchantId);
    }

    private BigDecimal calculateMom(BigDecimal today, BigDecimal yesterday) {
        if (yesterday == null || yesterday.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return today.subtract(yesterday)
            .divide(yesterday, 4, RoundingMode.HALF_UP)
            .multiply(new BigDecimal(100));
    }

    private List<BusinessDashboardResponse.DailyTrend> getTrendData(Long merchantId, LocalDate start, LocalDate end) {
        List<MerchantBusinessDaily> dataList = dailyMapper.selectByDateRange(merchantId, start, end);
        return dataList.stream().map(data -> {
            BusinessDashboardResponse.DailyTrend trend = new BusinessDashboardResponse.DailyTrend();
            trend.setDate(data.getStatDate().toString());
            trend.setRevenue(data.getRevenue());
            trend.setOrderCount(data.getOrderCount());
            return trend;
        }).collect(Collectors.toList());
    }
}
