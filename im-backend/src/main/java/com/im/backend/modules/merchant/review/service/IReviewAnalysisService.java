package com.im.backend.modules.merchant.review.service;

import com.im.backend.modules.merchant.review.entity.MerchantReview;

/**
 * 评价AI分析服务接口
 * 用于情感分析、虚假评价识别、质量评分
 */
public interface IReviewAnalysisService {

    /**
     * 分析评价情感
     * @return 情感得分 -1.0 ~ 1.0
     */
    double analyzeSentiment(String content);

    /**
     * 检测是否为虚假评价
     * @return 0-100 的虚假度得分
     */
    int detectFakeReview(MerchantReview review);

    /**
     * 计算评价质量分
     * @return 0-100 的质量得分
     */
    int calculateQualityScore(MerchantReview review);

    /**
     * 批量分析评价
     */
    void batchAnalyzeReviews();
}
