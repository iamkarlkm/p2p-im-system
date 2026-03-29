package com.im.backend.modules.miniprogram.market.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.im.backend.common.api.PageResult;
import com.im.backend.modules.miniprogram.market.dto.*;
import com.im.backend.modules.miniprogram.market.entity.MiniProgramReview;

import java.util.List;

/**
 * 小程序评分评论服务接口
 */
public interface MiniProgramReviewService extends IService<MiniProgramReview> {

    /**
     * 提交评分评论
     */
    ReviewResponse submitReview(Long userId, SubmitReviewRequest request);

    /**
     * 获取小程序的评论列表
     */
    PageResult<ReviewResponse> getAppReviews(Long appId, Integer pageNum, Integer pageSize);

    /**
     * 获取用户评论列表
     */
    List<ReviewResponse> getUserReviews(Long userId);

    /**
     * 开发者回复评论
     */
    boolean replyReview(Long reviewId, Long developerId, String reply);

    /**
     * 删除评论
     */
    boolean deleteReview(Long reviewId, Long userId);

    /**
     * 点赞评论
     */
    boolean likeReview(Long reviewId);

    /**
     * 获取小程序平均评分
     */
    Double getAppAverageRating(Long appId);
}
