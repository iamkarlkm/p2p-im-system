package com.im.local.review.service.impl;

import com.im.local.review.dto.MerchantReputationResponse;
import com.im.local.review.entity.MerchantReputationStats;
import com.im.local.review.repository.*;
import com.im.local.review.service.IMerchantReputationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * 商户口碑统计服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantReputationServiceImpl implements IMerchantReputationService {

    private final MerchantReputationStatsMapper reputationMapper;
    private final MerchantReviewMapper reviewMapper;

    @Override
    public void calculateReputation(Long merchantId) {
        // 统计计算逻辑（简化版）
        log.info("计算商户 {} 的口碑统计", merchantId);

        // 获取评价统计数据
        int totalReviews = reviewMapper.countByMerchantId(merchantId);

        // 更新或创建统计记录
        MerchantReputationStats stats = reputationMapper.selectByMerchantId(merchantId);
        if (stats == null) {
            stats = new MerchantReputationStats();
            stats.setMerchantId(merchantId);
            stats.setCreatedAt(LocalDateTime.now());
        }

        stats.setTotalReviews(totalReviews);
        stats.setStatsUpdatedAt(LocalDateTime.now());
        stats.setUpdatedAt(LocalDateTime.now());

        if (stats.getId() == null) {
            reputationMapper.insert(stats);
        } else {
            reputationMapper.updateById(stats);
        }
    }

    @Override
    public MerchantReputationResponse getReputation(Long merchantId) {
        MerchantReputationStats stats = reputationMapper.selectByMerchantId(merchantId);
        if (stats == null) {
            // 返回默认值
            MerchantReputationResponse response = new MerchantReputationResponse();
            response.setMerchantId(merchantId);
            response.setOverallScore(BigDecimal.ZERO);
            response.setTotalReviews(0);
            return response;
        }

        MerchantReputationResponse response = new MerchantReputationResponse();
        response.setMerchantId(stats.getMerchantId());
        response.setOverallScore(stats.getOverallScore());
        response.setTasteScore(stats.getTasteScore());
        response.setEnvironmentScore(stats.getEnvironmentScore());
        response.setServiceScore(stats.getServiceScore());
        response.setValueScore(stats.getValueScore());
        response.setTotalReviews(stats.getTotalReviews());
        response.setPositiveRate(stats.getPositiveRate());
        response.setDistrictRank(stats.getDistrictRank());
        response.setCategoryRank(stats.getCategoryRank());

        return response;
    }

    @Override
    public void batchUpdateReputation() {
        log.info("执行批量口碑统计更新");
        // 定时任务逻辑
    }

    @Override
    public Object getDistrictRanking(Long districtId, Integer page, Integer size) {
        // 商圈排行逻辑
        return null;
    }

    @Override
    public Object getCategoryRanking(Integer categoryId, Integer page, Integer size) {
        // 分类排行逻辑
        return null;
    }
}
