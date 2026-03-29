package com.im.backend.modules.merchant.bi.service.impl;

import com.im.backend.modules.merchant.bi.dto.*;
import com.im.backend.modules.merchant.bi.entity.*;
import com.im.backend.modules.merchant.bi.enums.*;
import com.im.backend.modules.merchant.bi.repository.*;
import com.im.backend.modules.merchant.bi.service.IMerchantBIDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商户BI数据服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantBIDataServiceImpl implements IMerchantBIDataService {

    private final MerchantBusinessStatsMapper businessStatsMapper;
    private final MerchantCustomerProfileMapper customerProfileMapper;
    private final MerchantMarketingEffectMapper marketingEffectMapper;
    private final MerchantConversionFunnelMapper conversionFunnelMapper;
    private final MerchantCompetitorBenchmarkMapper competitorBenchmarkMapper;

    @Override
    public BusinessDashboardResponse getBusinessDashboard(BusinessStatsQueryRequest request) {
        Long merchantId = request.getMerchantId();
        BusinessDashboardResponse response = new BusinessDashboardResponse();
        response.setMerchantId(merchantId);
        response.setPeriod(request.getPeriodType());

        // 解析日期范围
        LocalDate[] dateRange = parseDateRange(request.getPeriodType(), request.getStartDate(), request.getEndDate());
        LocalDate startDate = dateRange[0];
        LocalDate endDate = dateRange[1];
        response.setStartDate(startDate.toString());
        response.setEndDate(endDate.toString());

        // 查询统计数据
        List<MerchantBusinessStats> statsList = businessStatsMapper.selectByDateRange(merchantId, startDate, endDate);

        // 计算概览指标
        response.setOverview(calculateOverview(statsList, merchantId, endDate));

        // 趋势数据
        if (Boolean.TRUE.equals(request.getIncludeTrend())) {
            response.setTrendData(calculateTrendData(statsList));
        }

        // 时段分布
        if (Boolean.TRUE.equals(request.getIncludeHourly())) {
            response.setHourlyDistribution(calculateHourlyDistribution(merchantId, endDate));
        }

        // 对比数据
        if (Boolean.TRUE.equals(request.getIncludeComparison())) {
            response.setComparison(calculateComparison(merchantId, endDate));
        }

        return response;
    }

    @Override
    public CustomerProfileResponse getCustomerProfile(CustomerProfileQueryRequest request) {
        Long merchantId = request.getMerchantId();
        CustomerProfileResponse response = new CustomerProfileResponse();
        response.setMerchantId(merchantId);
        response.setPeriod(request.getPeriodType());

        // 解析日期范围
        LocalDate[] dateRange = parseDateRange(request.getPeriodType(), request.getStartDate(), request.getEndDate());
        String startDate = dateRange[0].toString();
        String endDate = dateRange[1].toString();

        // 年龄分布
        response.setAgeDistribution(getAgeDistribution(merchantId, startDate, endDate));

        // 性别分布
        response.setGenderDistribution(getGenderDistribution(merchantId, startDate, endDate));

        // 城市分布
        response.setCityTop10(getCityDistribution(merchantId, startDate, endDate));

        // 消费频次分布
        response.setFrequencyDistribution(getFrequencyDistribution(merchantId, startDate, endDate));

        // 地理热力图
        response.setGeoHeatmapData(getGeoHeatmapData(merchantId, startDate, endDate));

        // 偏好标签（模拟数据）
        response.setPreferenceTags(getPreferenceTags());

        // 计算总数
        int totalCustomers = response.getAgeDistribution().stream()
                .mapToInt(CustomerProfileResponse.AgeDistribution::getCount)
                .sum();
        response.setTotalCustomers(totalCustomers);

        return response;
    }

    @Override
    public MarketingEffectResponse getMarketingEffect(MarketingEffectQueryRequest request) {
        Long merchantId = request.getMerchantId();
        MarketingEffectResponse response = new MarketingEffectResponse();
        response.setMerchantId(merchantId);
        response.setPeriod(request.getPeriodType());

        // 解析日期范围
        LocalDate[] dateRange = parseDateRange(request.getPeriodType(), request.getStartDate(), request.getEndDate());
        LocalDate startDate = dateRange[0];
        LocalDate endDate = dateRange[1];

        // 查询营销效果列表
        List<MerchantMarketingEffect> effectList;
        if (request.getMarketingType() != null) {
            effectList = marketingEffectMapper.selectByTypeAndDateRange(
                    merchantId, request.getMarketingType(), startDate, endDate);
        } else {
            effectList = marketingEffectMapper.selectByDateRange(merchantId, startDate, endDate);
        }

        // 转换为DTO
        List<MarketingEffectResponse.MarketingItem> items = effectList.stream()
                .map(this::convertToMarketingItem)
                .collect(Collectors.toList());
        response.setMarketingList(items);

        // 计算概览
        response.setOverview(calculateMarketingOverview(items));

        return response;
    }

    @Override
    public ConversionFunnelResponse getConversionFunnel(Long merchantId, String funnelType, String statsDate) {
        ConversionFunnelResponse response = new ConversionFunnelResponse();
        response.setFunnelType(funnelType);
        response.setFunnelName(FunnelType.valueOf(funnelType).getDesc());
        response.setStatsDate(statsDate);

        // 查询漏斗数据
        List<MerchantConversionFunnel> funnelList = conversionFunnelMapper.selectByTypeAndDate(
                merchantId, funnelType, statsDate);

        // 构建漏斗步骤
        List<ConversionFunnelResponse.FunnelStep> steps = new ArrayList<>();
        int firstStepUsers = funnelList.isEmpty() ? 0 : funnelList.get(0).getExposureUsers();

        for (int i = 0; i < funnelList.size(); i++) {
            MerchantConversionFunnel funnel = funnelList.get(i);
            ConversionFunnelResponse.FunnelStep step = new ConversionFunnelResponse.FunnelStep();
            step.setStepName(getStepName(i));
            step.setStepOrder(i + 1);
            step.setUserCount(funnel.getExposureUsers());

            // 计算转化率
            if (i > 0) {
                int prevUsers = funnelList.get(i - 1).getExposureUsers();
                BigDecimal conversionRate = prevUsers == 0 ? BigDecimal.ZERO :
                        BigDecimal.valueOf(funnel.getExposureUsers())
                                .multiply(BigDecimal.valueOf(100))
                                .divide(BigDecimal.valueOf(prevUsers), 2, RoundingMode.HALF_UP);
                step.setConversionRate(conversionRate);
            } else {
                step.setConversionRate(BigDecimal.valueOf(100));
            }

            // 总转化率
            BigDecimal totalConversionRate = firstStepUsers == 0 ? BigDecimal.ZERO :
                    BigDecimal.valueOf(funnel.getExposureUsers())
                            .multiply(BigDecimal.valueOf(100))
                            .divide(BigDecimal.valueOf(firstStepUsers), 2, RoundingMode.HALF_UP);
            step.setTotalConversionRate(totalConversionRate);

            steps.add(step);
        }

        response.setSteps(steps);

        // 总转化率和流失率
        if (!steps.isEmpty()) {
            response.setTotalConversionRate(steps.get(steps.size() - 1).getTotalConversionRate());
            response.setChurnRate(BigDecimal.valueOf(100).subtract(response.getTotalConversionRate()));
        }

        return response;
    }

    @Override
    public CompetitorBenchmarkResponse getCompetitorBenchmark(Long merchantId, String benchmarkType) {
        CompetitorBenchmarkResponse response = new CompetitorBenchmarkResponse();
        response.setMerchantId(merchantId);
        response.setBenchmarkType(benchmarkType);

        // 查询对标数据
        MerchantCompetitorBenchmark benchmark = competitorBenchmarkMapper.selectLatestByType(merchantId, benchmarkType);

        if (benchmark != null) {
            response.setBenchmarkTargetName(benchmark.getBenchmarkTargetName());
            response.setMerchantRank(benchmark.getMerchantRank());
            response.setTotalMerchantCount(benchmark.getTotalMerchantCount());
            response.setCompositeScore(benchmark.getCompositeScore());

            // 构建对标项
            List<CompetitorBenchmarkResponse.BenchmarkItem> items = new ArrayList<>();
            items.add(createBenchmarkItem("评分", benchmark.getMerchantRating(), benchmark.getAvgRating()));
            items.add(createBenchmarkItem("月销量", new BigDecimal(benchmark.getMerchantMonthlySales()),
                    new BigDecimal(benchmark.getAvgMonthlySales())));
            items.add(createBenchmarkItem("客单价", benchmark.getMerchantAvgOrderValue(), benchmark.getAvgOrderValue()));
            items.add(createBenchmarkItem("访客数", new BigDecimal(benchmark.getMerchantVisitorCount()),
                    new BigDecimal(benchmark.getAvgVisitorCount())));
            response.setBenchmarkItems(items);
        }

        return response;
    }

    // ==================== 私有辅助方法 ====================

    private LocalDate[] parseDateRange(String periodType, String startDate, String endDate) {
        LocalDate end = LocalDate.now();
        LocalDate start;

        if (StatsPeriodType.CUSTOM.getCode().equals(periodType) && startDate != null && endDate != null) {
            return new LocalDate[]{LocalDate.parse(startDate), LocalDate.parse(endDate)};
        }

        switch (StatsPeriodType.valueOf(periodType.toUpperCase())) {
            case TODAY:
                start = end;
                break;
            case YESTERDAY:
                start = end.minusDays(1);
                end = start;
                break;
            case LAST_7_DAYS:
                start = end.minusDays(6);
                break;
            case LAST_30_DAYS:
                start = end.minusDays(29);
                break;
            case THIS_MONTH:
                start = end.withDayOfMonth(1);
                break;
            default:
                start = end.minusDays(6);
        }

        return new LocalDate[]{start, end};
    }

    private BusinessDashboardResponse.KeyMetricsOverview calculateOverview(
            List<MerchantBusinessStats> statsList, Long merchantId, LocalDate endDate) {
        BusinessDashboardResponse.KeyMetricsOverview overview = new BusinessDashboardResponse.KeyMetricsOverview();

        // 汇总数据
        BigDecimal totalRevenue = statsList.stream()
                .map(MerchantBusinessStats::getRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int totalOrders = statsList.stream().mapToInt(MerchantBusinessStats::getOrderCount).sum();
        int totalVisitors = statsList.stream().mapToInt(MerchantBusinessStats::getVisitorCount).sum();
        int totalNewCustomers = statsList.stream().mapToInt(MerchantBusinessStats::getNewCustomerCount).sum();

        overview.setRevenue(totalRevenue);
        overview.setOrderCount(totalOrders);
        overview.setAvgOrderValue(totalOrders == 0 ? BigDecimal.ZERO :
                totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP));
        overview.setVisitorCount(totalVisitors);
        overview.setNewCustomerCount(totalNewCustomers);

        return overview;
    }

    private List<BusinessDashboardResponse.TrendData> calculateTrendData(List<MerchantBusinessStats> statsList) {
        return statsList.stream()
                .map(stats -> {
                    BusinessDashboardResponse.TrendData trend = new BusinessDashboardResponse.TrendData();
                    trend.setDate(stats.getStatsDate().toString());
                    trend.setRevenue(stats.getRevenue());
                    trend.setOrderCount(stats.getOrderCount());
                    trend.setVisitorCount(stats.getVisitorCount());
                    return trend;
                })
                .collect(Collectors.toList());
    }

    private List<BusinessDashboardResponse.HourlyDistribution> calculateHourlyDistribution(
            Long merchantId, LocalDate date) {
        List<MerchantBusinessStats> hourlyList = businessStatsMapper.selectHourlyByDate(merchantId, date);

        BigDecimal totalRevenue = hourlyList.stream()
                .map(MerchantBusinessStats::getRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return hourlyList.stream()
                .map(stats -> {
                    BusinessDashboardResponse.HourlyDistribution dist = new BusinessDashboardResponse.HourlyDistribution();
                    dist.setHour(stats.getStatsHour());
                    dist.setRevenue(stats.getRevenue());
                    dist.setOrderCount(stats.getOrderCount());
                    dist.setPercentage(totalRevenue.compareTo(BigDecimal.ZERO) == 0 ? 0.0 :
                            stats.getRevenue().multiply(BigDecimal.valueOf(100))
                                    .divide(totalRevenue, 2, RoundingMode.HALF_UP).doubleValue());
                    return dist;
                })
                .collect(Collectors.toList());
    }

    private BusinessDashboardResponse.ComparisonData calculateComparison(Long merchantId, LocalDate date) {
        BusinessDashboardResponse.ComparisonData comparison = new BusinessDashboardResponse.ComparisonData();
        // 这里简化处理，实际应该查询同比环比数据
        comparison.setRevenueYoY(new BigDecimal("15.5"));
        comparison.setRevenueMoM(new BigDecimal("8.2"));
        comparison.setOrderCountYoY(new BigDecimal("12.3"));
        comparison.setOrderCountMoM(new BigDecimal("5.6"));
        return comparison;
    }

    private List<CustomerProfileResponse.AgeDistribution> getAgeDistribution(
            Long merchantId, String startDate, String endDate) {
        List<Map<String, Object>> list = customerProfileMapper.selectAgeDistribution(merchantId, startDate, endDate);
        int total = list.stream().mapToInt(m -> ((Number) m.get("count")).intValue()).sum();

        return list.stream()
                .map(m -> {
                    CustomerProfileResponse.AgeDistribution dist = new CustomerProfileResponse.AgeDistribution();
                    dist.setAgeGroup((String) m.get("age_group"));
                    dist.setCount(((Number) m.get("count")).intValue());
                    dist.setPercentage(total == 0 ? 0.0 :
                            ((Number) m.get("count")).doubleValue() * 100 / total);
                    return dist;
                })
                .collect(Collectors.toList());
    }

    private List<CustomerProfileResponse.GenderDistribution> getGenderDistribution(
            Long merchantId, String startDate, String endDate) {
        List<Map<String, Object>> list = customerProfileMapper.selectGenderDistribution(merchantId, startDate, endDate);
        int total = list.stream().mapToInt(m -> ((Number) m.get("count")).intValue()).sum();

        return list.stream()
                .map(m -> {
                    CustomerProfileResponse.GenderDistribution dist = new CustomerProfileResponse.GenderDistribution();
                    dist.setGender((String) m.get("gender"));
                    dist.setCount(((Number) m.get("count")).intValue());
                    dist.setPercentage(total == 0 ? 0.0 :
                            ((Number) m.get("count")).doubleValue() * 100 / total);
                    return dist;
                })
                .collect(Collectors.toList());
    }

    private List<CustomerProfileResponse.CityDistribution> getCityDistribution(
            Long merchantId, String startDate, String endDate) {
        List<Map<String, Object>> list = customerProfileMapper.selectCityDistribution(merchantId, startDate, endDate);
        int total = list.stream().mapToInt(m -> ((Number) m.get("count")).intValue()).sum();

        return list.stream()
                .map(m -> {
                    CustomerProfileResponse.CityDistribution dist = new CustomerProfileResponse.CityDistribution();
                    dist.setCity((String) m.get("city"));
                    dist.setCount(((Number) m.get("count")).intValue());
                    dist.setPercentage(total == 0 ? 0.0 :
                            ((Number) m.get("count")).doubleValue() * 100 / total);
                    return dist;
                })
                .collect(Collectors.toList());
    }

    private List<CustomerProfileResponse.FrequencyDistribution> getFrequencyDistribution(
            Long merchantId, String startDate, String endDate) {
        List<Map<String, Object>> list = customerProfileMapper.selectFrequencyDistribution(merchantId, startDate, endDate);
        int total = list.stream().mapToInt(m -> ((Number) m.get("count")).intValue()).sum();

        return list.stream()
                .map(m -> {
                    CustomerProfileResponse.FrequencyDistribution dist = new CustomerProfileResponse.FrequencyDistribution();
                    dist.setFrequency((String) m.get("consumption_frequency"));
                    dist.setCount(((Number) m.get("count")).intValue());
                    dist.setPercentage(total == 0 ? 0.0 :
                            ((Number) m.get("count")).doubleValue() * 100 / total);
                    return dist;
                })
                .collect(Collectors.toList());
    }

    private List<CustomerProfileResponse.GeoHeatmapData> getGeoHeatmapData(
            Long merchantId, String startDate, String endDate) {
        List<Map<String, Object>> list = customerProfileMapper.selectGeoHeatmapData(merchantId, startDate, endDate);

        return list.stream()
                .map(m -> {
                    CustomerProfileResponse.GeoHeatmapData data = new CustomerProfileResponse.GeoHeatmapData();
                    data.setLongitude(((Number) m.get("longitude")).doubleValue());
                    data.setLatitude(((Number) m.get("latitude")).doubleValue());
                    data.setIntensity(((Number) m.get("intensity")).intValue());
                    data.setDistrict((String) m.get("district"));
                    return data;
                })
                .collect(Collectors.toList());
    }

    private List<CustomerProfileResponse.PreferenceTag> getPreferenceTags() {
        // 模拟数据
        List<CustomerProfileResponse.PreferenceTag> tags = new ArrayList<>();
        tags.add(createTag("美食爱好者", 1250, 35.2));
        tags.add(createTag("夜宵党", 890, 25.1));
        tags.add(createTag("家庭聚餐", 756, 21.3));
        tags.add(createTag("商务宴请", 432, 12.2));
        tags.add(createTag("健康养生", 215, 6.1));
        return tags;
    }

    private CustomerProfileResponse.PreferenceTag createTag(String tag, int count, double percentage) {
        CustomerProfileResponse.PreferenceTag t = new CustomerProfileResponse.PreferenceTag();
        t.setTag(tag);
        t.setCount(count);
        t.setPercentage(percentage);
        return t;
    }

    private MarketingEffectResponse.MarketingItem convertToMarketingItem(MerchantMarketingEffect effect) {
        MarketingEffectResponse.MarketingItem item = new MarketingEffectResponse.MarketingItem();
        item.setMarketingId(effect.getMarketingId());
        item.setMarketingName(effect.getMarketingName());
        item.setMarketingType(effect.getMarketingType());
        item.setExposureCount(effect.getExposureCount());
        item.setReceiveCount(effect.getReceiveCount());
        item.setUseCount(effect.getUseCount());
        item.setReceiveRate(effect.getReceiveRate());
        item.setUseRate(effect.getUseRate());
        item.setConversionOrderCount(effect.getConversionOrderCount());
        item.setConversionOrderAmount(effect.getConversionOrderAmount());
        item.setMarketingCost(effect.getMarketingCost());
        item.setMarketingRevenue(effect.getMarketingRevenue());
        item.setRoi(effect.getRoi());
        item.setNewUserCount(effect.getNewUserCount());
        item.setNewUserCost(effect.getNewUserCost());
        item.setStatus("ACTIVE");
        return item;
    }

    private MarketingEffectResponse.MarketingOverview calculateMarketingOverview(
            List<MarketingEffectResponse.MarketingItem> items) {
        MarketingEffectResponse.MarketingOverview overview = new MarketingEffectResponse.MarketingOverview();

        int totalExposure = items.stream().mapToInt(MarketingEffectResponse.MarketingItem::getExposureCount).sum();
        int totalReceive = items.stream().mapToInt(MarketingEffectResponse.MarketingItem::getReceiveCount).sum();
        int totalUse = items.stream().mapToInt(MarketingEffectResponse.MarketingItem::getUseCount).sum();
        BigDecimal totalCost = items.stream()
                .map(MarketingEffectResponse.MarketingItem::getMarketingCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalRevenue = items.stream()
                .map(MarketingEffectResponse.MarketingItem::getMarketingRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int totalNewUsers = items.stream().mapToInt(MarketingEffectResponse.MarketingItem::getNewUserCount).sum();

        overview.setTotalExposure(totalExposure);
        overview.setTotalReceive(totalReceive);
        overview.setTotalUse(totalUse);
        overview.setTotalCost(totalCost);
        overview.setTotalRevenue(totalRevenue);
        overview.setTotalNewUsers(totalNewUsers);

        // 计算率
        overview.setOverallReceiveRate(totalExposure == 0 ? BigDecimal.ZERO :
                BigDecimal.valueOf(totalReceive).multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(totalExposure), 2, RoundingMode.HALF_UP));
        overview.setOverallUseRate(totalReceive == 0 ? BigDecimal.ZERO :
                BigDecimal.valueOf(totalUse).multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(totalReceive), 2, RoundingMode.HALF_UP));
        overview.setOverallRoi(totalCost.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO :
                totalRevenue.divide(totalCost, 2, RoundingMode.HALF_UP));

        return overview;
    }

    private String getStepName(int stepIndex) {
        String[] stepNames = {"曝光", "点击", "访问", "下单", "支付", "完成"};
        return stepIndex < stepNames.length ? stepNames[stepIndex] : "步骤" + (stepIndex + 1);
    }

    private CompetitorBenchmarkResponse.BenchmarkItem createBenchmarkItem(
            String metricName, BigDecimal merchantValue, BigDecimal avgValue) {
        CompetitorBenchmarkResponse.BenchmarkItem item = new CompetitorBenchmarkResponse.BenchmarkItem();
        item.setMetricName(metricName);
        item.setMerchantValue(merchantValue);
        item.setAvgValue(avgValue);

        if (avgValue.compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal gap = merchantValue.subtract(avgValue)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(avgValue, 2, RoundingMode.HALF_UP);
            item.setGapPercentage(gap);
            item.setIsLeading(gap.compareTo(BigDecimal.ZERO) > 0);
        } else {
            item.setGapPercentage(BigDecimal.ZERO);
            item.setIsLeading(false);
        }

        return item;
    }
}
