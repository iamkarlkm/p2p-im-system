package com.im.backend.modules.merchant.bi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.im.backend.modules.merchant.bi.dto.*;
import com.im.backend.modules.merchant.bi.entity.*;
import com.im.backend.modules.merchant.bi.mapper.*;
import com.im.backend.modules.merchant.bi.service.MerchantBiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 商家BI数据智能平台服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantBiServiceImpl implements MerchantBiService {

    private final MerchantBusinessDailyReportMapper dailyReportMapper;
    private final MerchantRealtimeMetricsMapper realtimeMetricsMapper;
    private final MerchantCustomerGeoDistributionMapper geoDistributionMapper;
    private final MerchantCustomerPortraitMapper customerPortraitMapper;
    private final MerchantCouponAnalyticsMapper couponAnalyticsMapper;
    private final MerchantCampaignAnalyticsMapper campaignAnalyticsMapper;
    private final MerchantBiAlertMapper biAlertMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String REALTIME_METRICS_KEY = "bi:realtime:%d";
    private static final String DASHBOARD_CACHE_KEY = "bi:dashboard:%d";

    @Override
    public MerchantDashboardResponse getDashboard(Long merchantId) {
        String cacheKey = String.format(DASHBOARD_CACHE_KEY, merchantId);
        
        // 查询实时数据
        MerchantRealtimeMetrics realtimeMetrics = realtimeMetricsMapper.selectById(merchantId);
        
        // 查询今日数据
        LocalDate today = LocalDate.now();
        MerchantBusinessDailyReport todayReport = dailyReportMapper.selectByMerchantAndDate(merchantId, today);
        
        // 查询昨日数据
        LocalDate yesterday = today.minusDays(1);
        MerchantBusinessDailyReport yesterdayReport = dailyReportMapper.selectByMerchantAndDate(merchantId, yesterday);
        
        // 构建响应
        return MerchantDashboardResponse.builder()
                .merchantId(merchantId)
                .reportDate(today)
                .realtime(buildRealtimeMetrics(realtimeMetrics))
                .today(buildDailyMetrics(todayReport))
                .yesterday(buildDailyMetrics(yesterdayReport))
                .thisWeek(buildPeriodMetrics(merchantId, today.minusDays(6), today))
                .thisMonth(buildPeriodMetrics(merchantId, today.withDayOfMonth(1), today))
                .trends(getTrendData(merchantId, today.minusDays(29), today))
                .alerts(getAlerts(merchantId))
                .build();
    }

    private MerchantDashboardResponse.RealtimeMetricsDTO buildRealtimeMetrics(MerchantRealtimeMetrics metrics) {
        if (metrics == null) {
            return MerchantDashboardResponse.RealtimeMetricsDTO.builder().build();
        }
        return MerchantDashboardResponse.RealtimeMetricsDTO.builder()
                .todayRevenue(metrics.getTodayRevenue())
                .todayOrders(metrics.getTodayOrders())
                .currentFootTraffic(metrics.getCurrentFootTraffic())
                .diningCustomers(metrics.getDiningCustomers())
                .waitingCustomers(metrics.getWaitingCustomers())
                .avgWaitTime(metrics.getAvgWaitTime())
                .realtimeRating(metrics.getRealtimeRating())
                .turnoverRate(metrics.getTurnoverRate())
                .seatUtilization(metrics.getSeatUtilization())
                .build();
    }

    private MerchantDashboardResponse.DailyMetricsDTO buildDailyMetrics(MerchantBusinessDailyReport report) {
        if (report == null) {
            return MerchantDashboardResponse.DailyMetricsDTO.builder().build();
        }
        return MerchantDashboardResponse.DailyMetricsDTO.builder()
                .totalRevenue(report.getTotalRevenue())
                .totalOrders(report.getTotalOrders())
                .validOrders(report.getValidOrders())
                .cancelledOrders(report.getCancelledOrders())
                .refundAmount(report.getRefundAmount())
                .averageOrderValue(report.getAverageOrderValue())
                .footTraffic(report.getFootTraffic())
                .newCustomers(report.getNewCustomers())
                .returningCustomers(report.getReturningCustomers())
                .returningRate(report.getReturningRate())
                .build();
    }

    private MerchantDashboardResponse.PeriodMetricsDTO buildPeriodMetrics(Long merchantId, LocalDate startDate, LocalDate endDate) {
        List<MerchantBusinessDailyReport> reports = dailyReportMapper.selectByDateRange(merchantId, startDate, endDate);
        
        BigDecimal totalRevenue = reports.stream()
                .map(MerchantBusinessDailyReport::getTotalRevenue)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Integer totalOrders = reports.stream()
                .mapToInt(r -> r.getTotalOrders() != null ? r.getTotalOrders() : 0)
                .sum();
        
        Integer footTraffic = reports.stream()
                .mapToInt(r -> r.getFootTraffic() != null ? r.getFootTraffic() : 0)
                .sum();
        
        Integer newCustomers = reports.stream()
                .mapToInt(r -> r.getNewCustomers() != null ? r.getNewCustomers() : 0)
                .sum();
        
        BigDecimal avgOrderValue = totalOrders > 0 
                ? totalRevenue.divide(new BigDecimal(totalOrders), 2, RoundingMode.HALF_UP) 
                : BigDecimal.ZERO;
        
        return MerchantDashboardResponse.PeriodMetricsDTO.builder()
                .totalRevenue(totalRevenue)
                .totalOrders(totalOrders)
                .averageOrderValue(avgOrderValue)
                .footTraffic(footTraffic)
                .newCustomers(newCustomers)
                .build();
    }

    @Override
    public MerchantDashboardResponse.RealtimeMetricsDTO getRealtimeMetrics(Long merchantId) {
        MerchantRealtimeMetrics metrics = realtimeMetricsMapper.selectById(merchantId);
        return buildRealtimeMetrics(metrics);
    }

    @Override
    public List<MerchantDashboardResponse.TrendDataDTO> getTrendData(Long merchantId, LocalDate startDate, LocalDate endDate) {
        List<MerchantBusinessDailyReport> reports = dailyReportMapper.selectByDateRange(merchantId, startDate, endDate);
        
        return reports.stream()
                .map(r -> MerchantDashboardResponse.TrendDataDTO.builder()
                        .date(r.getReportDate())
                        .revenue(r.getTotalRevenue())
                        .orders(r.getTotalOrders())
                        .footTraffic(r.getFootTraffic())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public CustomerGeoDistributionResponse getCustomerGeoDistribution(Long merchantId, String regionLevel) {
        LocalDate today = LocalDate.now();
        
        LambdaQueryWrapper<MerchantCustomerGeoDistribution> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MerchantCustomerGeoDistribution::getMerchantId, merchantId)
                .eq(MerchantCustomerGeoDistribution::getReportDate, today)
                .orderByDesc(MerchantCustomerGeoDistribution::getCustomerCount);
        
        List<MerchantCustomerGeoDistribution> distributions = geoDistributionMapper.selectList(wrapper);
        
        Map<String, List<MerchantCustomerGeoDistribution>> grouped = distributions.stream()
                .collect(Collectors.groupingBy(MerchantCustomerGeoDistribution::getRegionType));
        
        return CustomerGeoDistributionResponse.builder()
                .merchantId(merchantId)
                .provinceDistribution(buildGeoDistributionList(grouped.getOrDefault("province", Collections.emptyList())))
                .cityDistribution(buildGeoDistributionList(grouped.getOrDefault("city", Collections.emptyList())))
                .districtDistribution(buildGeoDistributionList(grouped.getOrDefault("district", Collections.emptyList())))
                .build();
    }

    private List<CustomerGeoDistributionResponse.GeoDistributionDTO> buildGeoDistributionList(List<MerchantCustomerGeoDistribution> list) {
        return list.stream()
                .map(d -> CustomerGeoDistributionResponse.GeoDistributionDTO.builder()
                        .regionCode(d.getRegionCode())
                        .regionName(d.getRegionName())
                        .customerCount(d.getCustomerCount())
                        .orderCount(d.getOrderCount())
                        .revenue(d.getRevenue())
                        .avgOrderValue(d.getAvgOrderValue())
                        .percentage(d.getPercentage())
                        .growthRate(d.getGrowthRate())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public CustomerPortraitResponse getCustomerPortrait(Long merchantId) {
        LocalDate today = LocalDate.now();
        
        LambdaQueryWrapper<MerchantCustomerPortrait> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MerchantCustomerPortrait::getMerchantId, merchantId)
                .eq(MerchantCustomerPortrait::getReportDate, today);
        
        List<MerchantCustomerPortrait> portraits = customerPortraitMapper.selectList(wrapper);
        
        Map<String, List<MerchantCustomerPortrait>> grouped = portraits.stream()
                .collect(Collectors.groupingBy(MerchantCustomerPortrait::getPortraitType));
        
        return CustomerPortraitResponse.builder()
                .merchantId(merchantId)
                .ageDistribution(buildPortraitList(grouped.getOrDefault("AGE", Collections.emptyList())))
                .genderDistribution(buildPortraitList(grouped.getOrDefault("GENDER", Collections.emptyList())))
                .consumptionLevelDistribution(buildPortraitList(grouped.getOrDefault("CONSUMPTION_LEVEL", Collections.emptyList())))
                .preferenceDistribution(buildPortraitList(grouped.getOrDefault("PREFERENCE", Collections.emptyList())))
                .loyaltyDistribution(buildPortraitList(grouped.getOrDefault("LOYALTY", Collections.emptyList())))
                .build();
    }

    private List<CustomerPortraitResponse.PortraitItemDTO> buildPortraitList(List<MerchantCustomerPortrait> list) {
        return list.stream()
                .map(p -> CustomerPortraitResponse.PortraitItemDTO.builder()
                        .dimension(p.getDimensionValue())
                        .dimensionName(p.getDimensionName())
                        .customerCount(p.getCustomerCount())
                        .orderCount(p.getOrderCount())
                        .totalRevenue(p.getTotalRevenue())
                        .avgOrderValue(p.getAvgOrderValue())
                        .percentage(p.getPercentage())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public CouponAnalyticsResponse getCouponAnalytics(Long merchantId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<MerchantCouponAnalytics> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MerchantCouponAnalytics::getMerchantId, merchantId)
                .between(MerchantCouponAnalytics::getReportDate, startDate, endDate);
        
        List<MerchantCouponAnalytics> analyticsList = couponAnalyticsMapper.selectList(wrapper);
        
        // 汇总计算
        Integer totalIssued = analyticsList.stream().mapToInt(MerchantCouponAnalytics::getTotalIssued).sum();
        Integer totalClaimed = analyticsList.stream().mapToInt(MerchantCouponAnalytics::getTotalClaimed).sum();
        Integer totalUsed = analyticsList.stream().mapToInt(MerchantCouponAnalytics::getTotalUsed).sum();
        
        BigDecimal totalDiscount = analyticsList.stream()
                .map(MerchantCouponAnalytics::getTotalDiscountAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal drivenRevenue = analyticsList.stream()
                .map(MerchantCouponAnalytics::getDrivenRevenue)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal roi = totalDiscount.compareTo(BigDecimal.ZERO) > 0 
                ? drivenRevenue.divide(totalDiscount, 2, RoundingMode.HALF_UP) 
                : BigDecimal.ZERO;
        
        return CouponAnalyticsResponse.builder()
                .merchantId(merchantId)
                .startDate(startDate)
                .endDate(endDate)
                .overview(CouponAnalyticsResponse.OverviewDTO.builder()
                        .totalIssued(totalIssued)
                        .totalClaimed(totalClaimed)
                        .totalUsed(totalUsed)
                        .claimRate(calcRate(totalClaimed, totalIssued))
                        .usageRate(calcRate(totalUsed, totalClaimed))
                        .totalDiscountAmount(totalDiscount)
                        .drivenRevenue(drivenRevenue)
                        .roi(roi)
                        .build())
                .build();
    }

    private BigDecimal calcRate(Integer numerator, Integer denominator) {
        if (denominator == null || denominator == 0) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(numerator * 100).divide(new BigDecimal(denominator), 2, RoundingMode.HALF_UP);
    }

    @Override
    public CampaignAnalyticsResponse getCampaignAnalytics(Long merchantId, LocalDate startDate, LocalDate endDate) {
        // 简化实现
        return CampaignAnalyticsResponse.builder()
                .merchantId(merchantId)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }

    @Override
    public List<MerchantDashboardResponse.AlertDTO> getAlerts(Long merchantId) {
        LambdaQueryWrapper<MerchantBiAlert> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MerchantBiAlert::getMerchantId, merchantId)
                .eq(MerchantBiAlert::getIsRead, false)
                .orderByDesc(MerchantBiAlert::getTriggerTime)
                .last("LIMIT 10");
        
        List<MerchantBiAlert> alerts = biAlertMapper.selectList(wrapper);
        
        return alerts.stream()
                .map(a -> MerchantDashboardResponse.AlertDTO.builder()
                        .alertType(a.getAlertType())
                        .alertLevel(a.getAlertLevel())
                        .title(a.getTitle())
                        .content(a.getContent())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAlertAsRead(Long alertId) {
        MerchantBiAlert alert = new MerchantBiAlert();
        alert.setId(alertId);
        alert.setIsRead(true);
        biAlertMapper.updateById(alert);
    }

    @Override
    @Transactional
    public void processAlert(Long alertId, String remark) {
        MerchantBiAlert alert = new MerchantBiAlert();
        alert.setId(alertId);
        alert.setStatus("processed");
        alert.setProcessTime(LocalDateTime.now());
        alert.setProcessRemark(remark);
        alert.setIsRead(true);
        biAlertMapper.updateById(alert);
    }

    @Override
    public void refreshRealtimeMetrics(Long merchantId) {
        // 从数据库刷新实时指标到Redis缓存
        MerchantRealtimeMetrics metrics = realtimeMetricsMapper.selectById(merchantId);
        if (metrics != null) {
            String key = String.format(REALTIME_METRICS_KEY, merchantId);
            redisTemplate.opsForValue().set(key, metrics);
        }
    }

    @Override
    @Transactional
    public void generateDailyReport(Long merchantId, LocalDate reportDate) {
        // 生成日报表逻辑
        log.info("Generating daily report for merchant {} on {}", merchantId, reportDate);
        // 实际实现中需要查询订单、支付等数据进行汇总计算
    }

    @Override
    public Object getCompareAnalysis(Long merchantId, String compareType, LocalDate date) {
        // 对比分析实现
        return Collections.emptyMap();
    }
}
