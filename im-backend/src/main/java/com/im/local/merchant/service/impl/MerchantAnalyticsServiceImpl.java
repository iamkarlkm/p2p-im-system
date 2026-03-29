package com.im.local.merchant.service.impl;

import com.im.local.merchant.dto.*;
import com.im.local.merchant.service.MerchantAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 商户数据分析与经营洞察服务实现
 * 本地生活商户数据分析与经营洞察模块
 * 
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-28
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantAnalyticsServiceImpl implements MerchantAnalyticsService {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern("HH:00");
    
    @Override
    public MerchantDashboardResponse getDashboard(MerchantDashboardRequest request) {
        log.info("获取商户仪表盘数据, merchantId={}, timeRange={}", request.getMerchantId(), request.getTimeRangeType());
        
        request.calculateDateRange();
        
        MerchantDashboardResponse.BusinessOverview overview = buildOverview(request);
        MerchantDashboardResponse.RevenueAnalysis revenue = buildRevenueAnalysis(request);
        MerchantDashboardResponse.TrafficAnalysis traffic = buildTrafficAnalysis(request);
        MerchantDashboardResponse.ReviewAnalysis reviews = buildReviewAnalysis(request);
        MerchantDashboardResponse.CompetitorAnalysis competitor = buildCompetitorAnalysis(request);
        List<MerchantDashboardResponse.BusinessInsight> insights = generateInsights(request, overview, revenue, traffic, reviews);
        
        return MerchantDashboardResponse.builder()
                .merchantId(request.getMerchantId())
                .merchantName("示例商户") // 实际应从商户服务获取
                .timeRangeLabel(generateTimeRangeLabel(request))
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .generatedAt(LocalDateTime.now())
                .overview(overview)
                .revenue(revenue)
                .traffic(traffic)
                .reviews(reviews)
                .competitor(competitor)
                .insights(insights)
                .build();
    }
    
    @Override
    public MerchantDashboardResponse.RealTimeStats getRealTimeStats(Long merchantId) {
        log.info("获取商户实时统计数据, merchantId={}", merchantId);
        
        // 模拟实时数据
        return MerchantDashboardResponse.RealTimeStats.builder()
                .todayVisitors(new Random().nextInt(500) + 100)
                .todayOrders(new Random().nextInt(80) + 20)
                .todayRevenue(new BigDecimal(new Random().nextInt(5000) + 1000))
                .currentOnline(new Random().nextInt(50) + 10)
                .todayTrend(new Random().nextBoolean() ? "UP" : "FLAT")
                .build();
    }
    
    @Override
    public MerchantDashboardResponse.RevenueAnalysis getRevenueAnalysis(MerchantDashboardRequest request) {
        log.info("获取营收分析数据, merchantId={}", request.getMerchantId());
        request.calculateDateRange();
        return buildRevenueAnalysis(request);
    }
    
    @Override
    public MerchantDashboardResponse.TrafficAnalysis getTrafficAnalysis(MerchantDashboardRequest request) {
        log.info("获取客流分析数据, merchantId={}", request.getMerchantId());
        request.calculateDateRange();
        return buildTrafficAnalysis(request);
    }
    
    @Override
    public MerchantDashboardResponse.ReviewAnalysis getReviewAnalysis(MerchantDashboardRequest request) {
        log.info("获取评价分析数据, merchantId={}", request.getMerchantId());
        request.calculateDateRange();
        return buildReviewAnalysis(request);
    }
    
    @Override
    public MerchantDashboardResponse.CompetitorAnalysis getCompetitorAnalysis(MerchantDashboardRequest request) {
        log.info("获取竞品对比数据, merchantId={}", request.getMerchantId());
        request.calculateDateRange();
        return buildCompetitorAnalysis(request);
    }
    
    @Override
    public List<MerchantDashboardResponse.BusinessInsight> getBusinessInsights(Long merchantId, Integer limit) {
        log.info("获取经营洞察建议, merchantId={}, limit={}", merchantId, limit);
        
        MerchantDashboardRequest request = new MerchantDashboardRequest();
        request.setMerchantId(merchantId);
        request.setTimeRangeType("WEEK");
        request.calculateDateRange();
        
        MerchantDashboardResponse.BusinessOverview overview = buildOverview(request);
        MerchantDashboardResponse.RevenueAnalysis revenue = buildRevenueAnalysis(request);
        MerchantDashboardResponse.TrafficAnalysis traffic = buildTrafficAnalysis(request);
        MerchantDashboardResponse.ReviewAnalysis reviews = buildReviewAnalysis(request);
        
        List<MerchantDashboardResponse.BusinessInsight> insights = generateInsights(request, overview, revenue, traffic, reviews);
        return insights.stream().limit(limit != null ? limit : 10).collect(Collectors.toList());
    }
    
    @Override
    public CustomerProfileResponse getCustomerProfile(Long merchantId, String timeRangeType) {
        log.info("获取顾客画像分析, merchantId={}, timeRange={}", merchantId, timeRangeType);
        // 返回模拟数据
        return CustomerProfileResponse.builder()
                .merchantId(merchantId)
                .timeRangeType(timeRangeType)
                .generatedAt(LocalDateTime.now())
                .genderDistribution(buildGenderDistribution())
                .ageDistribution(buildAgeDistribution())
                .consumptionLevel(buildConsumptionLevel())
                .visitFrequency(buildVisitFrequency())
                .preferenceTags(buildPreferenceTags())
                .build();
    }
    
    @Override
    public RfmAnalysisResponse getRfmAnalysis(Long merchantId) {
        log.info("获取RFM客户分层分析, merchantId={}", merchantId);
        return RfmAnalysisResponse.builder()
                .merchantId(merchantId)
                .generatedAt(LocalDateTime.now())
                .vipCustomers(buildRfmSegment("VIP客户", new Random().nextInt(200) + 50, "最近消费,频率高,金额大"))
                .loyalCustomers(buildRfmSegment("忠诚客户", new Random().nextInt(500) + 100, "消费频率高"))
                .potentialCustomers(buildRfmSegment("潜力客户", new Random().nextInt(800) + 200, "最近消费,金额大"))
                .newCustomers(buildRfmSegment("新客户", new Random().nextInt(1000) + 300, "最近首次消费"))
                .atRiskCustomers(buildRfmSegment("流失风险客户", new Random().nextInt(400) + 100, "较长时间未消费"))
                .lostCustomers(buildRfmSegment("流失客户", new Random().nextInt(300) + 50, "长期未消费"))
                .build();
    }
    
    @Override
    public TrafficForecastResponse getTrafficForecast(Long merchantId, Integer forecastDays) {
        log.info("获取客流高峰预测, merchantId={}, forecastDays={}", merchantId, forecastDays);
        
        List<TrafficForecastResponse.DailyForecast> forecasts = new ArrayList<>();
        LocalDate startDate = LocalDate.now();
        
        for (int i = 0; i < (forecastDays != null ? forecastDays : 7); i++) {
            LocalDate date = startDate.plusDays(i);
            int baseTraffic = 200 + new Random().nextInt(300);
            
            forecasts.add(TrafficForecastResponse.DailyForecast.builder()
                    .date(date)
                    .dayOfWeek(date.getDayOfWeek().toString())
                    .predictedVisitors(baseTraffic)
                    .confidenceLevel("85%")
                    .peakHourStart(12)
                    .peakHourEnd(14)
                    .weatherFactor("sunny")
                    .build());
        }
        
        return TrafficForecastResponse.builder()
                .merchantId(merchantId)
                .forecastDays(forecastDays != null ? forecastDays : 7)
                .generatedAt(LocalDateTime.now())
                .forecasts(forecasts)
                .build();
    }
    
    @Override
    public String exportReport(ReportExportRequest request) {
        log.info("导出经营报表, merchantId={}, format={}", request.getMerchantId(), request.getFormat());
        // 实际应生成文件并返回URL
        return "https://storage.im.local/reports/merchant_" + request.getMerchantId() + "_" + System.currentTimeMillis() + "." + request.getFormat().toLowerCase();
    }
    
    @Override
    public List<MerchantDashboardResponse> getBatchDashboard(List<Long> merchantIds, MerchantDashboardRequest baseRequest) {
        log.info("批量获取商户仪表盘数据, merchantCount={}", merchantIds.size());
        
        List<MerchantDashboardResponse> results = new ArrayList<>();
        for (Long merchantId : merchantIds) {
            MerchantDashboardRequest request = new MerchantDashboardRequest();
            request.setMerchantId(merchantId);
            request.setTimeRangeType(baseRequest.getTimeRangeType());
            request.setStartDate(baseRequest.getStartDate());
            request.setEndDate(baseRequest.getEndDate());
            request.setDataDimensions(baseRequest.getDataDimensions());
            
            results.add(getDashboard(request));
        }
        return results;
    }
    
    // ==================== 私有辅助方法 ====================
    
    private MerchantDashboardResponse.BusinessOverview buildOverview(MerchantDashboardRequest request) {
        Random random = new Random();
        
        BigDecimal totalRevenue = new BigDecimal(random.nextInt(50000) + 10000);
        Integer totalOrders = random.nextInt(1000) + 200;
        Integer totalVisitors = random.nextInt(5000) + 1000;
        String conversionRate = String.format("%.2f%%", 5.0 + random.nextDouble() * 10);
        BigDecimal avgOrderValue = totalRevenue.divide(new BigDecimal(totalOrders), 2, RoundingMode.HALF_UP);
        BigDecimal overallRating = new BigDecimal(3.5 + random.nextDouble() * 1.5).setScale(1, RoundingMode.HALF_UP);
        
        return MerchantDashboardResponse.BusinessOverview.builder()
                .totalRevenue(totalRevenue)
                .revenueGrowth(new BigDecimal(random.nextInt(5000) - 1000))
                .revenueGrowthRate(String.format("%+.1f%%", (random.nextDouble() - 0.3) * 30))
                .totalOrders(totalOrders)
                .orderGrowth(random.nextInt(100) - 20)
                .orderGrowthRate(String.format("%+.1f%%", (random.nextDouble() - 0.3) * 30))
                .totalVisitors(totalVisitors)
                .visitorGrowth(random.nextInt(500) - 100)
                .visitorGrowthRate(String.format("%+.1f%%", (random.nextDouble() - 0.3) * 30))
                .conversionRate(conversionRate)
                .conversionRateChange(String.format("%+.2f%%", (random.nextDouble() - 0.5) * 2))
                .avgOrderValue(avgOrderValue)
                .avgOrderValueChange(new BigDecimal(random.nextInt(20) - 10))
                .overallRating(overallRating)
                .ratingChange(new BigDecimal((random.nextDouble() - 0.5) * 0.4).setScale(1, RoundingMode.HALF_UP))
                .realTimeStats(getRealTimeStats(request.getMerchantId()))
                .build();
    }
    
    private MerchantDashboardResponse.RevenueAnalysis buildRevenueAnalysis(MerchantDashboardRequest request) {
        Random random = new Random();
        
        // 营收构成
        MerchantDashboardResponse.RevenueComposition composition = MerchantDashboardResponse.RevenueComposition.builder()
                .productRevenue(new BigDecimal(random.nextInt(30000) + 5000))
                .serviceRevenue(new BigDecimal(random.nextInt(15000) + 3000))
                .membershipRevenue(new BigDecimal(random.nextInt(5000) + 1000))
                .otherRevenue(new BigDecimal(random.nextInt(2000) + 500))
                .build();
        
        // 营收趋势
        List<MerchantDashboardResponse.TimeSeriesData> revenueTrend = generateTimeSeriesData(request.getStartDate(), request.getEndDate(), "revenue");
        
        // 时段分布
        List<MerchantDashboardResponse.HourlyDistribution> hourlyDistribution = new ArrayList<>();
        for (int hour = 0; hour < 24; hour++) {
            int traffic = (hour >= 11 && hour <= 14) || (hour >= 17 && hour <= 21) ? random.nextInt(100) + 50 : random.nextInt(20);
            hourlyDistribution.add(MerchantDashboardResponse.HourlyDistribution.builder()
                    .hour(hour)
                    .hourLabel(String.format("%02d:00", hour))
                    .revenue(new BigDecimal(traffic * 50))
                    .orders(traffic)
                    .percentage(String.format("%.1f%%", traffic / 10.0))
                    .build());
        }
        
        // 支付渠道
        List<MerchantDashboardResponse.PaymentChannel> paymentChannels = Arrays.asList(
                buildPaymentChannel("WECHAT", "微信支付", 0.6, random),
                buildPaymentChannel("ALIPAY", "支付宝", 0.3, random),
                buildPaymentChannel("CARD", "银行卡", 0.08, random),
                buildPaymentChannel("CASH", "现金", 0.02, random)
        );
        
        // 退款统计
        MerchantDashboardResponse.RefundStats refundStats = MerchantDashboardResponse.RefundStats.builder()
                .refundCount(random.nextInt(20))
                .refundAmount(new BigDecimal(random.nextInt(2000)))
                .refundRate(String.format("%.2f%%", random.nextDouble() * 3))
                .pendingRefunds(random.nextInt(5))
                .build();
        
        return MerchantDashboardResponse.RevenueAnalysis.builder()
                .composition(composition)
                .revenueTrend(revenueTrend)
                .hourlyDistribution(hourlyDistribution)
                .paymentChannels(paymentChannels)
                .refundStats(refundStats)
                .build();
    }
    
    private MerchantDashboardResponse.TrafficAnalysis buildTrafficAnalysis(MerchantDashboardRequest request) {
        Random random = new Random();
        int totalVisitors = random.nextInt(5000) + 1000;
        int newVisitors = (int) (totalVisitors * 0.4);
        int returningVisitors = totalVisitors - newVisitors;
        
        // 客流来源
        List<MerchantDashboardResponse.TrafficSource> sources = Arrays.asList(
                buildTrafficSource("SEARCH", "搜索", 0.35, random),
                buildTrafficSource("RECOMMEND", "推荐", 0.25, random),
                buildTrafficSource("AD", "广告", 0.15, random),
                buildTrafficSource("SHARE", "分享", 0.15, random),
                buildTrafficSource("DIRECT", "直接访问", 0.1, random)
        );
        
        // 地域分布
        List<MerchantDashboardResponse.RegionDistribution> regions = Arrays.asList(
                buildRegionDistribution("AREA_01", "朝阳区", 0.3, random, totalVisitors),
                buildRegionDistribution("AREA_02", "海淀区", 0.25, random, totalVisitors),
                buildRegionDistribution("AREA_03", "东城区", 0.2, random, totalVisitors),
                buildRegionDistribution("AREA_04", "西城区", 0.15, random, totalVisitors),
                buildRegionDistribution("AREA_05", "其他", 0.1, random, totalVisitors)
        );
        
        return MerchantDashboardResponse.TrafficAnalysis.builder()
                .totalVisitors(totalVisitors)
                .newVisitors(newVisitors)
                .returningVisitors(returningVisitors)
                .newVisitorRate(String.format("%.1f%%", (double) newVisitors / totalVisitors * 100))
                .returnRate(String.format("%.1f%%", 20.0 + random.nextDouble() * 30))
                .visitorTrend(generateTimeSeriesData(request.getStartDate(), request.getEndDate(), "traffic"))
                .sources(sources)
                .visitHours(new ArrayList<>())
                .dwellTime(buildDwellTimeDistribution())
                .regions(regions)
                .build();
    }
    
    private MerchantDashboardResponse.ReviewAnalysis buildReviewAnalysis(MerchantDashboardRequest request) {
        Random random = new Random();
        int totalReviews = random.nextInt(500) + 100;
        
        // 评分分布
        List<MerchantDashboardResponse.RatingDistribution> ratingDistribution = Arrays.asList(
                MerchantDashboardResponse.RatingDistribution.builder().rating(5).count((int)(totalReviews * 0.5)).percentage("50%").build(),
                MerchantDashboardResponse.RatingDistribution.builder().rating(4).count((int)(totalReviews * 0.3)).percentage("30%").build(),
                MerchantDashboardResponse.RatingDistribution.builder().rating(3).count((int)(totalReviews * 0.12)).percentage("12%").build(),
                MerchantDashboardResponse.RatingDistribution.builder().rating(2).count((int)(totalReviews * 0.05)).percentage("5%").build(),
                MerchantDashboardResponse.RatingDistribution.builder().rating(1).count((int)(totalReviews * 0.03)).percentage("3%").build()
        );
        
        // 维度评分
        List<MerchantDashboardResponse.DimensionRating> dimensionRatings = Arrays.asList(
                buildDimensionRating("TASTE", "口味", 4.5 + random.nextDouble() * 0.5, random),
                buildDimensionRating("SERVICE", "服务", 4.3 + random.nextDouble() * 0.6, random),
                buildDimensionRating("ENVIRONMENT", "环境", 4.4 + random.nextDouble() * 0.5, random),
                buildDimensionRating("VALUE", "性价比", 4.2 + random.nextDouble() * 0.6, random)
        );
        
        // 关键词云
        List<MerchantDashboardResponse.KeywordTag> keywords = Arrays.asList(
                MerchantDashboardResponse.KeywordTag.builder().keyword("味道好").count(120).sentiment("POSITIVE").fontSize(28).build(),
                MerchantDashboardResponse.KeywordTag.builder().keyword("服务热情").count(95).sentiment("POSITIVE").fontSize(24).build(),
                MerchantDashboardResponse.KeywordTag.builder().keyword("环境不错").count(80).sentiment("POSITIVE").fontSize(22).build(),
                MerchantDashboardResponse.KeywordTag.builder().keyword("排队久").count(45).sentiment("NEGATIVE").fontSize(18).build(),
                MerchantDashboardResponse.KeywordTag.builder().keyword("价格合适").count(70).sentiment("POSITIVE").fontSize(20).build()
        );
        
        // 情感分析
        MerchantDashboardResponse.SentimentAnalysis sentiment = MerchantDashboardResponse.SentimentAnalysis.builder()
                .positiveRate("85%")
                .negativeRate("10%")
                .neutralRate("5%")
                .positiveCount((int)(totalReviews * 0.85))
                .negativeCount((int)(totalReviews * 0.10))
                .neutralCount((int)(totalReviews * 0.05))
                .build();
        
        return MerchantDashboardResponse.ReviewAnalysis.builder()
                .avgRating(new BigDecimal(4.0 + random.nextDouble() * 1.0).setScale(1, RoundingMode.HALF_UP))
                .totalReviews(totalReviews)
                .newReviews(random.nextInt(20))
                .reviewGrowthRate(String.format("%+.1f%%", (random.nextDouble() - 0.3) * 20))
                .ratingDistribution(ratingDistribution)
                .reviewTrend(generateTimeSeriesData(request.getStartDate(), request.getEndDate(), "review"))
                .dimensionRatings(dimensionRatings)
                .keywords(keywords)
                .sentiment(sentiment)
                .recentReviews(new ArrayList<>())
                .build();
    }
    
    private MerchantDashboardResponse.CompetitorAnalysis buildCompetitorAnalysis(MerchantDashboardRequest request) {
        Random random = new Random();
        
        // 对比指标
        List<MerchantDashboardResponse.ComparisonMetric> comparisonMetrics = Arrays.asList(
                buildComparisonMetric("REVENUE", "营业额", random),
                buildComparisonMetric("ORDERS", "订单量", random),
                buildComparisonMetric("VISITORS", "访客数", random),
                buildComparisonMetric("RATING", "评分", random),
                buildComparisonMetric("CONVERSION", "转化率", random)
        );
        
        // Top商户
        List<MerchantDashboardResponse.TopMerchant> topMerchants = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            topMerchants.add(MerchantDashboardResponse.TopMerchant.builder()
                    .rank(i)
                    .merchantId((long) i)
                    .merchantName("商户" + i)
                    .score(new BigDecimal(90 - i * 3 + random.nextDouble() * 5).setScale(1, RoundingMode.HALF_UP))
                    .build());
        }
        
        return MerchantDashboardResponse.CompetitorAnalysis.builder()
                .ranking(random.nextInt(10) + 1)
                .totalMerchants(50)
                .rankingChange(random.nextBoolean() ? "+2" : "-1")
                .comparisonMetrics(comparisonMetrics)
                .districtAverage(buildDistrictAverage(random))
                .topMerchants(topMerchants)
                .build();
    }
    
    private List<MerchantDashboardResponse.BusinessInsight> generateInsights(
            MerchantDashboardRequest request,
            MerchantDashboardResponse.BusinessOverview overview,
            MerchantDashboardResponse.RevenueAnalysis revenue,
            MerchantDashboardResponse.TrafficAnalysis traffic,
            MerchantDashboardResponse.ReviewAnalysis reviews) {
        
        List<MerchantDashboardResponse.BusinessInsight> insights = new ArrayList<>();
        Random random = new Random();
        
        // 基于数据生成洞察
        if (overview.getRevenueGrowth().doubleValue() > 0) {
            insights.add(MerchantDashboardResponse.BusinessInsight.builder()
                    .insightId("INS_" + System.currentTimeMillis())
                    .type("TREND")
                    .level("MEDIUM")
                    .title("营收增长良好")
                    .description("本周营收较上周增长" + overview.getRevenueGrowthRate())
                    .metric("营收增长率")
                    .metricValue(overview.getRevenueGrowthRate())
                    .recommendation("继续保持当前的营销策略")
                    .generatedAt(LocalDateTime.now())
                    .build());
        }
        
        if (reviews.getAvgRating().doubleValue() >= 4.5) {
            insights.add(MerchantDashboardResponse.BusinessInsight.builder()
                    .insightId("INS_" + (System.currentTimeMillis() + 1))
                    .type("OPPORTUNITY")
                    .level("HIGH")
                    .title("好评率优秀")
                    .description("当前评分" + reviews.getAvgRating() + "，位居商圈前列")
                    .metric("用户评分")
                    .metricValue(reviews.getAvgRating().toString())
                    .recommendation("可以申请成为平台优质商户，获取更多流量")
                    .generatedAt(LocalDateTime.now())
                    .build());
        }
        
        insights.add(MerchantDashboardResponse.BusinessInsight.builder()
                .insightId("INS_" + (System.currentTimeMillis() + 2))
                .type("SUGGESTION")
                .level("MEDIUM")
                .title("优化高峰期服务")
                .description("午市和晚市时段订单集中，建议提前备餐")
                .metric("高峰订单占比")
                .metricValue("65%")
                .recommendation("增加高峰时段人手或推出预约服务")
                .generatedAt(LocalDateTime.now())
                .build());
        
        return insights;
    }
    
    // ==================== 辅助构建方法 ====================
    
    private String generateTimeRangeLabel(MerchantDashboardRequest request) {
        switch (request.getTimeRangeType()) {
            case "TODAY": return "今日";
            case "YESTERDAY": return "昨日";
            case "WEEK": return "本周";
            case "MONTH": return "本月";
            case "YEAR": return "本年";
            default: return request.getStartDate() + " 至 " + request.getEndDate();
        }
    }
    
    private List<MerchantDashboardResponse.TimeSeriesData> generateTimeSeriesData(LocalDate start, LocalDate end, String type) {
        List<MerchantDashboardResponse.TimeSeriesData> data = new ArrayList<>();
        LocalDate current = start;
        Random random = new Random();
        
        while (!current.isAfter(end)) {
            BigDecimal value = new BigDecimal(random.nextInt(5000) + 1000);
            data.add(MerchantDashboardResponse.TimeSeriesData.builder()
                    .timeLabel(current.format(DATE_FORMATTER))
                    .timestamp(current.atStartOfDay())
                    .value(value)
                    .compareValue(value.multiply(new BigDecimal(0.9 + random.nextDouble() * 0.2)).setScale(2, RoundingMode.HALF_UP))
                    .growthRate(String.format("%+.1f%%", (random.nextDouble() - 0.5) * 20))
                    .build());
            current = current.plusDays(1);
        }
        return data;
    }
    
    private MerchantDashboardResponse.PaymentChannel buildPaymentChannel(String code, String name, double ratio, Random random) {
        int amount = (int) ((random.nextInt(50000) + 10000) * ratio);
        return MerchantDashboardResponse.PaymentChannel.builder()
                .channelCode(code)
                .channelName(name)
                .revenue(new BigDecimal(amount))
                .count((int) (amount / 100))
                .percentage(String.format("%.0f%%", ratio * 100))
                .build();
    }
    
    private MerchantDashboardResponse.TrafficSource buildTrafficSource(String code, String name, double ratio, Random random) {
        int visitors = (int) (ratio * 1000);
        return MerchantDashboardResponse.TrafficSource.builder()
                .sourceCode(code)
                .sourceName(name)
                .visitors(visitors)
                .percentage(String.format("%.0f%%", ratio * 100))
                .conversionRate(String.format("%.2f%%", 3.0 + random.nextDouble() * 7))
                .build();
    }
    
    private MerchantDashboardResponse.RegionDistribution buildRegionDistribution(String code, String name, double ratio, Random random, int total) {
        int visitors = (int) (total * ratio);
        return MerchantDashboardResponse.RegionDistribution.builder()
                .regionCode(code)
                .regionName(name)
                .visitors(visitors)
                .percentage(String.format("%.0f%%", ratio * 100))
                .build();
    }
    
    private MerchantDashboardResponse.DimensionRating buildDimensionRating(String code, String name, double score, Random random) {
        return MerchantDashboardResponse.DimensionRating.builder()
                .dimensionCode(code)
                .dimensionName(name)
                .score(new BigDecimal(score).setScale(1, RoundingMode.HALF_UP))
                .scoreChange(new BigDecimal((random.nextDouble() - 0.5) * 0.3).setScale(1, RoundingMode.HALF_UP))
                .build();
    }
    
    private MerchantDashboardResponse.ComparisonMetric buildComparisonMetric(String code, String name, Random random) {
        return MerchantDashboardResponse.ComparisonMetric.builder()
                .metricCode(code)
                .metricName(name)
                .myValue(String.valueOf(random.nextInt(10000) + 5000))
                .avgValue(String.valueOf(random.nextInt(8000) + 4000))
                .topValue(String.valueOf(random.nextInt(15000) + 10000))
                .myPercentile(String.format("Top %d%%", random.nextInt(30) + 10))
                .build();
    }
    
    private MerchantDashboardResponse.CompetitorAnalysis.DistrictAverage buildDistrictAverage(Random random) {
        return MerchantDashboardResponse.CompetitorAnalysis.DistrictAverage.builder()
                .avgRevenue(new BigDecimal(random.nextInt(30000) + 5000))
                .avgOrders(random.nextInt(500) + 100)
                .avgVisitors(random.nextInt(3000) + 500)
                .avgRating(new BigDecimal(3.8 + random.nextDouble() * 0.8).setScale(1, RoundingMode.HALF_UP))
                .avgConversionRate(String.format("%.2f%%", 5.0 + random.nextDouble() * 5))
                .build();
    }
    
    private List<MerchantDashboardResponse.TrafficAnalysis.DwellTimeDistribution> buildDwellTimeDistribution() {
        return Arrays.asList(
                MerchantDashboardResponse.TrafficAnalysis.DwellTimeDistribution.builder().timeRange("< 15分钟").visitors(200).percentage("20%").build(),
                MerchantDashboardResponse.TrafficAnalysis.DwellTimeDistribution.builder().timeRange("15-30分钟").visitors(350).percentage("35%").build(),
                MerchantDashboardResponse.TrafficAnalysis.DwellTimeDistribution.builder().timeRange("30-60分钟").visitors(300).percentage("30%").build(),
                MerchantDashboardResponse.TrafficAnalysis.DwellTimeDistribution.builder().timeRange("> 60分钟").visitors(150).percentage("15%").build()
        );
    }
    
    // 顾客画像辅助方法
    private List<CustomerProfileResponse.GenderDistribution> buildGenderDistribution() {
        return Arrays.asList(
                CustomerProfileResponse.GenderDistribution.builder().gender("男").percentage(45).count(450).build(),
                CustomerProfileResponse.GenderDistribution.builder().gender("女").percentage(52).count(520).build(),
                CustomerProfileResponse.GenderDistribution.builder().gender("未知").percentage(3).count(30).build()
        );
    }
    
    private List<CustomerProfileResponse.AgeDistribution> buildAgeDistribution() {
        return Arrays.asList(
                CustomerProfileResponse.AgeDistribution.builder().ageRange("18-24岁").percentage(25).count(250).build(),
                CustomerProfileResponse.AgeDistribution.builder().ageRange("25-34岁").percentage(45).count(450).build(),
                CustomerProfileResponse.AgeDistribution.builder().ageRange("35-44岁").percentage(20).count(200).build(),
                CustomerProfileResponse.AgeDistribution.builder().ageRange("45岁以上").percentage(10).count(100).build()
        );
    }
    
    private List<CustomerProfileResponse.ConsumptionLevel> buildConsumptionLevel() {
        return Arrays.asList(
                CustomerProfileResponse.ConsumptionLevel.builder().level("低消费").percentage(20).avgAmount(new BigDecimal(50)).build(),
                CustomerProfileResponse.ConsumptionLevel.builder().level("中消费").percentage(55).avgAmount(new BigDecimal(150)).build(),
                CustomerProfileResponse.ConsumptionLevel.builder().level("高消费").percentage(25).avgAmount(new BigDecimal(300)).build()
        );
    }
    
    private List<CustomerProfileResponse.VisitFrequency> buildVisitFrequency() {
        return Arrays.asList(
                CustomerProfileResponse.VisitFrequency.builder().frequency("首次").percentage(40).count(400).build(),
                CustomerProfileResponse.VisitFrequency.builder().frequency("2-3次").percentage(35).count(350).build(),
                CustomerProfileResponse.VisitFrequency.builder().frequency("4-10次").percentage(20).count(200).build(),
                CustomerProfileResponse.VisitFrequency.builder().frequency("10次以上").percentage(5).count(50).build()
        );
    }
    
    private List<CustomerProfileResponse.PreferenceTag> buildPreferenceTags() {
        return Arrays.asList(
                CustomerProfileResponse.PreferenceTag.builder().tag("口味偏辣").percentage(35).build(),
                CustomerProfileResponse.PreferenceTag.builder().tag("注重环境").percentage(40).build(),
                CustomerProfileResponse.PreferenceTag.builder().tag("价格敏感").percentage(30).build(),
                CustomerProfileResponse.PreferenceTag.builder().tag("喜欢新品").percentage(25).build(),
                CustomerProfileResponse.PreferenceTag.builder().tag("常带朋友").percentage(45).build()
        );
    }
    
    private RfmAnalysisResponse.Segment buildRfmSegment(String name, int count, String description) {
        return RfmAnalysisResponse.Segment.builder()
                .segmentName(name)
                .customerCount(count)
                .description(description)
                .avgRecencyDays(new Random().nextInt(30) + 1)
                .avgFrequency(new Random().nextInt(10) + 1)
                .avgMonetary(new BigDecimal(new Random().nextInt(1000) + 100))
                .contributionRate(String.format("%.1f%%", count / 100.0))
                .build();
    }
}
