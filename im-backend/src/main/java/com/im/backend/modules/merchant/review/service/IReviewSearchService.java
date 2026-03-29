package com.im.backend.modules.merchant.review.service;

import java.util.List;

/**
 * 评价搜索服务接口
 * 基于Elasticsearch实现全文检索
 */
public interface IReviewSearchService {

    /**
     * 索引评价到ES
     */
    void indexReview(String reviewId);

    /**
     * 批量索引评价
     */
    void batchIndexReviews();

    /**
     * 搜索评价
     */
    List<String> searchReviews(String keyword, Long merchantId, Integer page, Integer size);

    /**
     * 删除索引
     */
    void deleteIndex(String reviewId);

    /**
     * 重建索引
     */
    void rebuildIndex();
}
