package com.im.backend.modules.local_life.review.service;

import com.im.backend.common.PageResult;
import com.im.backend.modules.local_life.review.dto.*;

import java.util.List;

/**
 * 商户评价服务接口
 * 
 * @author IM Development Team
 * @version 1.0
 */
public interface MerchantReviewService {
    
    /**
     * 提交评价
     * 
     * @param userId 用户ID
     * @param request 评价请求
     * @return 评价ID
     */
    Long submitReview(Long userId, SubmitReviewRequestDTO request);
    
    /**
     * 获取评价详情
     * 
     * @param reviewId 评价ID
     * @param currentUserId 当前用户ID
     * @return 评价详情
     */
    ReviewDetailDTO getReviewDetail(Long reviewId, Long currentUserId);
    
    /**
     * 搜索评价列表
     * 
     * @param request 搜索请求
     * @param currentUserId 当前用户ID
     * @return 分页结果
     */
    PageResult<ReviewDetailDTO> searchReviews(ReviewSearchRequestDTO request, Long currentUserId);
    
    /**
     * 点赞/取消点赞
     * 
     * @param userId 用户ID
     * @param request 点赞请求
     * @return 当前点赞数
     */
    Integer likeReview(Long userId, LikeReviewRequestDTO request);
    
    /**
     * 回复评价
     * 
     * @param userId 用户ID
     * @param userType 用户类型
     * @param request 回复请求
     * @return 回复ID
     */
    Long replyReview(Long userId, String userType, ReplyReviewRequestDTO request);
    
    /**
     * 获取评价回复列表
     * 
     * @param reviewId 评价ID
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 回复列表
     */
    PageResult<ReviewReplyDTO> getReviewReplies(Long reviewId, Integer pageNum, Integer pageSize);
    
    /**
     * 删除评价 (用户自己删除)
     * 
     * @param userId 用户ID
     * @param reviewId 评价ID
     */
    void deleteReview(Long userId, Long reviewId);
    
    /**
     * 举报评价
     * 
     * @param userId 举报用户ID
     * @param reviewId 评价ID
     * @param reason 举报原因
     * @param description 描述
     */
    void reportReview(Long userId, Long reviewId, String reason, String description);
    
    /**
     * 获取商户的评价统计
     * 
     * @param merchantId 商户ID
     * @return 评价统计
     */
    ReviewStatisticsDTO getReviewStatistics(Long merchantId);
    
    /**
     * 获取用户的历史评价
     * 
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 评价列表
     */
    PageResult<ReviewDetailDTO> getUserReviews(Long userId, Integer pageNum, Integer pageSize);
}
