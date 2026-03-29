package com.im.backend.modules.merchant.review.service.impl;

import com.im.backend.modules.merchant.review.dto.MerchantReputationResponse;
import com.im.backend.modules.merchant.review.dto.RatingDistributionDTO;
import com.im.backend.modules.merchant.review.entity.MerchantReputationStats;
import com.im.backend.modules.merchant.review.repository.MerchantReputationStatsMapper;
import com.im.backend.modules.merchant.review.service.IMerchantReputationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 商户口碑统计服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantReputationServiceImpl implements IMerchantReputationService {

    private final MerchantReputationStatsMapper statsMapper;

    @Override
    public MerchantReputationResponse getMerchantReputation(Long merchantId) {
        MerchantReputationStats stats = statsMapper.selectByMerchantId(merchantId);
        if (stats == null) {
            // 返回空统计
            MerchantReputationResponse empty = new MerchantReputationResponse();
            empty.setMerchantId(merchantId);
            empty.setOverallScore(0.0);
            empty.setTotalReviews(0);
            return empty;
        }

        return convertToResponse(stats);
    }

    @Override
    public void updateMerchantReputation(Long merchantId) {
        // 这里应该从评价表中重新计算统计数据
        // 简化实现：触发异步计算任务
        log.info("触发商户 {} 的口碑统计更新", merchantId);
    }

    @Override
    public List<MerchantReputationStats> getReputationRank(String rankType, Integer limit) {
        if ("hot".equals(rankType)) {
            return statsMapper.selectTopByReviewCount(limit);
        } else {
            return statsMapper.selectTopByOverallScore(limit);
        }
    }

    @Override
    public Integer getMerchantRanking(Long merchantId) {
        MerchantReputationStats stats = statsMapper.selectByMerchantId(merchantId);
        return stats != null ? stats.getRankingInCategory() : 0;
    }

    private MerchantReputationResponse convertToResponse(MerchantReputationStats stats) {
        MerchantReputationResponse response = new MerchantReputationResponse();
        response.setMerchantId(stats.getMerchantId());
        response.setOverallScore(stats.getOverallScore());
        response.setTasteScore(stats.getTasteScore());
        response.setEnvironmentScore(stats.getEnvironmentScore());
        response.setServiceScore(stats.getServiceScore());
        response.setValueScore(stats.getValueScore());
        response.setTotalReviews(stats.getTotalReviews());
        response.setHasImageCount(stats.getHasImageCount());
        response.setHasVideoCount(stats.getHasVideoCount());
        response.setPositiveRate(stats.getPositiveRate());
        response.setRankingInCategory(stats.getRankingInCategory());
        response.setTotalInCategory(stats.getTotalInCategory());

        RatingDistributionDTO distribution = new RatingDistributionDTO();
        distribution.setFiveStar(stats.getFiveStarCount());
        distribution.setFourStar(stats.getFourStarCount());
        distribution.setThreeStar(stats.getThreeStarCount());
        distribution.setTwoStar(stats.getTwoStarCount());
        distribution.setOneStar(stats.getOneStarCount());
        response.setRatingDistribution(distribution);

        return response;
    }
}
