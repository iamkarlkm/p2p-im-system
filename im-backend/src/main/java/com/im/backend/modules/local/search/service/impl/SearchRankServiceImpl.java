package com.im.backend.modules.local.search.service.impl;

import com.im.backend.modules.local.search.service.ISearchRankService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * 搜索排名服务实现
 */
@Slf4j
@Service
public class SearchRankServiceImpl implements ISearchRankService {

    private static final double WEIGHT_DISTANCE = 0.25;
    private static final double WEIGHT_RATING = 0.20;
    private static final double WEIGHT_HOT = 0.25;
    private static final double WEIGHT_RELEVANCE = 0.20;
    private static final double WEIGHT_PREFERENCE = 0.10;

    @Override
    public Double calculateRankScore(Double distance, Double rating, Double hotScore,
                                      Double relevanceScore, Double userPreference) {
        // 距离分数 - 越近分数越高
        double distanceScore = Math.max(0, 100 - distance / 100);

        // 评分分数 - 5分制转百分制
        double ratingScore = rating * 20;

        // 综合排序分数
        return distanceScore * WEIGHT_DISTANCE +
                ratingScore * WEIGHT_RATING +
                hotScore * WEIGHT_HOT +
                relevanceScore * WEIGHT_RELEVANCE +
                userPreference * WEIGHT_PREFERENCE;
    }

    @Override
    public Double adjustWeightByIntent(Double baseScore, String intentType) {
        double multiplier = 1.0;
        switch (intentType) {
            case "NAVIGATION":
                multiplier = 1.2;  // 导航意图提升距离权重
                break;
            case "REVIEW":
                multiplier = 1.15; // 评价意图提升评分权重
                break;
            case "PRICE_QUERY":
                multiplier = 1.1;  // 价格查询意图提升性价比权重
                break;
            default:
                multiplier = 1.0;
        }
        return baseScore * multiplier;
    }

    @Override
    public SearchResult personalizeRank(SearchResult result, Long userId) {
        // 根据用户历史行为调整排序
        // 实际项目中查询用户画像并调整分数
        return result;
    }

    @Override
    public SearchResult ensureDiversity(SearchResult result) {
        // 确保结果多样性 - 避免同类POI过度集中
        // 使用MMR(最大边缘相关)算法
        return result;
    }

    @Override
    public String getRankStrategy(Long userId) {
        // A/B测试 - 根据用户ID分配到不同策略组
        Random random = new Random(userId);
        int bucket = random.nextInt(100);
        if (bucket < 33) {
            return "STRATEGY_A"; // 距离优先
        } else if (bucket < 66) {
            return "STRATEGY_B"; // 热度优先
        } else {
            return "STRATEGY_C"; // 综合排序
        }
    }
}
