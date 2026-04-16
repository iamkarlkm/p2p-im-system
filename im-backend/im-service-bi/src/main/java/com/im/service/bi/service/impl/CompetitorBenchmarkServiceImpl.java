package com.im.service.bi.service.impl;

import com.im.service.bi.dto.CompetitorBenchmarkResponse;
import com.im.service.bi.entity.CompetitorBenchmark;
import com.im.service.bi.repository.CompetitorBenchmarkMapper;
import com.im.service.bi.service.ICompetitorBenchmarkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 竞品对标服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CompetitorBenchmarkServiceImpl implements ICompetitorBenchmarkService {

    private final CompetitorBenchmarkMapper benchmarkMapper;

    @Override
    public CompetitorBenchmarkResponse getBenchmarkAnalysis(Long merchantId) {
        CompetitorBenchmarkResponse response = new CompetitorBenchmarkResponse();

        CompetitorBenchmark latest = benchmarkMapper.selectLatest(merchantId);
        if (latest == null) {
            return response;
        }

        response.setRanking(latest.getRanking());
        response.setTotalMerchants(latest.getTotalMerchants());
        response.setMerchantRating(latest.getMerchantRating());
        response.setAvgRating(latest.getAvgRating());
        response.setMerchantMonthlySales(latest.getMerchantMonthlySales());
        response.setAvgMonthlySales(latest.getAvgMonthlySales());
        response.setMerchantAvgPrice(latest.getMerchantAvgPrice());
        response.setAvgPrice(latest.getAvgPrice());
        response.setReviewCount(latest.getReviewCount());
        response.setPositiveRate(latest.getPositiveRate());

        // 计算超越百分比
        if (latest.getTotalMerchants() != null && latest.getTotalMerchants() > 0) {
            int merchantsBelow = latest.getTotalMerchants() - latest.getRanking();
            response.setRatingPercentile(
                new BigDecimal(merchantsBelow).multiply(new BigDecimal(100))
                    .divide(new BigDecimal(latest.getTotalMerchants()), 2, RoundingMode.HALF_UP));
            response.setSalesPercentile(response.getRatingPercentile());
        }

        // 排名趋势
        response.setRankingTrend(getRankingTrend(merchantId));

        return response;
    }

    @Override
    public void refreshBenchmarkData(Long merchantId) {
        log.info("Refreshing benchmark data for merchant: {}", merchantId);
    }

    @Override
    public CompetitorBenchmarkResponse getDistrictRanking(Long districtId, String category) {
        List<CompetitorBenchmark> list = benchmarkMapper.selectDistrictRanking(districtId, category);
        CompetitorBenchmarkResponse response = new CompetitorBenchmarkResponse();
        if (!list.isEmpty()) {
            CompetitorBenchmark top = list.get(0);
            response.setTotalMerchants(list.size());
        }
        return response;
    }

    private List<CompetitorBenchmarkResponse.RankingTrend> getRankingTrend(Long merchantId) {
        List<Map<String, Object>> list = benchmarkMapper.selectRankingTrend(merchantId);
        List<CompetitorBenchmarkResponse.RankingTrend> result = new ArrayList<>();

        for (Map<String, Object> map : list) {
            CompetitorBenchmarkResponse.RankingTrend trend = new CompetitorBenchmarkResponse.RankingTrend();
            trend.setDate(map.get("date").toString());
            trend.setRanking(((Number) map.get("ranking")).intValue());
            trend.setRating(new BigDecimal(map.get("rating").toString()));
            result.add(trend);
        }
        return result;
    }
}
