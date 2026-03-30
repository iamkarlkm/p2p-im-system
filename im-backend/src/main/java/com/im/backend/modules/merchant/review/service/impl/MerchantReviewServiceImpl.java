package com.im.backend.modules.merchant.review.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.im.backend.common.Result;
import com.im.backend.modules.merchant.review.dto.ReviewSubmitRequest;
import com.im.backend.modules.merchant.review.dto.ReviewResponse;
import com.im.backend.modules.merchant.review.dto.ReviewStatsResponse;
import com.im.backend.modules.merchant.review.entity.MerchantReview;
import com.im.backend.modules.merchant.review.entity.MerchantReviewLike;
import com.im.backend.modules.merchant.review.repository.MerchantReviewMapper;
import com.im.backend.modules.merchant.review.repository.MerchantReviewLikeMapper;
import com.im.backend.modules.merchant.review.service.IMerchantReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商户评价服务实现 - 功能#310: 本地商户评价口碑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantReviewServiceImpl extends ServiceImpl<MerchantReviewMapper, MerchantReview> implements IMerchantReviewService {

    private final MerchantReviewMapper reviewMapper;
    private final MerchantReviewLikeMapper likeMapper;

    @Override
    @Transactional
    public Result<ReviewResponse> submitReview(Long userId, ReviewSubmitRequest request) {
        // 检查是否已评价
        MerchantReview existing = reviewMapper.selectByUserAndMerchant(userId, request.getMerchantId());
        if (existing != null) {
            return Result.error("您已对该商户进行过评价");
        }

        MerchantReview review = new MerchantReview();
        BeanUtils.copyProperties(request, review);
        review.setUserId(userId);
        review.setLikeCount(0);
        review.setReplyCount(0);
        review.setViewCount(0);
        review.setRecommended(false);
        review.setStatus(0); // 待审核
        
        if (request.getTags() != null) {
            review.setTags(String.join(",", request.getTags()));
        }
        if (request.getImages() != null) {
            review.setImages(JSON.toJSONString(request.getImages()));
        }

        reviewMapper.insert(review);
        
        log.info("用户{}提交了对商户{}的评价", userId, request.getMerchantId());
        return Result.success(convertToResponse(review, userId));
    }

    @Override
    public IPage<ReviewResponse> getMerchantReviews(Long merchantId, Integer rating, Boolean hasImage, Page<MerchantReview> page) {
        IPage<MerchantReview> reviewPage = reviewMapper.selectByMerchantId(page, merchantId, rating, hasImage);
        return reviewPage.convert(r -> convertToResponse(r, null));
    }

    @Override
    public ReviewResponse getReviewDetail(Long reviewId, Long currentUserId) {
        MerchantReview review = reviewMapper.selectById(reviewId);
        if (review == null || review.getDeleted()) {
            return null;
        }
        reviewMapper.incrementViewCount(reviewId);
        return convertToResponse(review, currentUserId);
    }

    @Override
    public ReviewStatsResponse getReviewStats(Long merchantId) {
        List<java.util.Map<String, Object>> stats = reviewMapper.selectRatingStats(merchantId);
        
        ReviewStatsResponse response = new ReviewStatsResponse();
        response.setMerchantId(merchantId);
        
        int total = 0;
        int weightedSum = 0;
        int fiveStar = 0, fourStar = 0, threeStar = 0, twoStar = 0, oneStar = 0;
        
        for (java.util.Map<String, Object> stat : stats) {
            Integer rating = (Integer) stat.get("rating");
            Long count = ((Number) stat.get("count")).longValue();
            total += count.intValue();
            weightedSum += rating * count.intValue();
            
            switch (rating) {
                case 5: fiveStar = count.intValue(); break;
                case 4: fourStar = count.intValue(); break;
                case 3: threeStar = count.intValue(); break;
                case 2: twoStar = count.intValue(); break;
                case 1: oneStar = count.intValue(); break;
            }
        }
        
        response.setTotalCount(total);
        response.setFiveStarCount(fiveStar);
        response.setFourStarCount(fourStar);
        response.setThreeStarCount(threeStar);
        response.setTwoStarCount(twoStar);
        response.setOneStarCount(oneStar);
        
        if (total > 0) {
            response.setAverageRating(new BigDecimal(weightedSum).divide(new BigDecimal(total), 1, RoundingMode.HALF_UP));
            response.setGoodRate(new BigDecimal(fiveStar + fourStar).multiply(new BigDecimal(100)).divide(new BigDecimal(total), 1, RoundingMode.HALF_UP));
        } else {
            response.setAverageRating(BigDecimal.ZERO);
            response.setGoodRate(BigDecimal.ZERO);
        }
        
        return response;
    }

    @Override
    @Transactional
    public Result<Void> merchantReply(Long merchantId, Long reviewId, String reply) {
        MerchantReview review = reviewMapper.selectById(reviewId);
        if (review == null || !review.getMerchantId().equals(merchantId)) {
            return Result.error("评价不存在");
        }
        reviewMapper.merchantReply(reviewId, reply);
        return Result.success();
    }

    @Override
    @Transactional
    public Result<Boolean> toggleLike(Long userId, Long reviewId) {
        MerchantReviewLike existing = likeMapper.selectByUserAndReview(userId, reviewId, 1);
        
        if (existing != null) {
            // 取消点赞
            likeMapper.deleteById(existing.getId());
            reviewMapper.updateLikeCount(reviewId, -1);
            return Result.success(false);
        } else {
            // 点赞
            MerchantReviewLike like = new MerchantReviewLike();
            like.setUserId(userId);
            like.setReviewId(reviewId);
            like.setLikeType(1);
            likeMapper.insert(like);
            reviewMapper.updateLikeCount(reviewId, 1);
            return Result.success(true);
        }
    }

    @Override
    public IPage<ReviewResponse> getUserReviews(Long userId, Page<MerchantReview> page) {
        IPage<MerchantReview> reviewPage = reviewMapper.selectByUserId(page, userId);
        return reviewPage.convert(r -> convertToResponse(r, userId));
    }

    @Override
    public List<ReviewResponse> getRecommendedReviews(Long merchantId, Integer limit) {
        List<MerchantReview> reviews = reviewMapper.selectRecommended(merchantId, limit);
        return reviews.stream().map(r -> convertToResponse(r, null)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Result<Void> deleteReview(Long userId, Long reviewId) {
        MerchantReview review = reviewMapper.selectById(reviewId);
        if (review == null || !review.getUserId().equals(userId)) {
            return Result.error("评价不存在或无权限");
        }
        reviewMapper.deleteById(reviewId);
        return Result.success();
    }

    @Override
    public Result<Void> auditReview(Long reviewId, Integer status, String rejectReason) {
        MerchantReview review = reviewMapper.selectById(reviewId);
        if (review == null) {
            return Result.error("评价不存在");
        }
        review.setStatus(status);
        review.setRejectReason(rejectReason);
        if (status == 1 && review.getRating() >= 4) {
            review.setRecommended(true);
        }
        reviewMapper.updateById(review);
        return Result.success();
    }

    private ReviewResponse convertToResponse(MerchantReview review, Long currentUserId) {
        ReviewResponse response = new ReviewResponse();
        BeanUtils.copyProperties(review, response);
        
        if (review.getTags() != null) {
            response.setTags(java.util.Arrays.asList(review.getTags().split(",")));
        }
        if (review.getImages() != null) {
            response.setImages(JSON.parseArray(review.getImages(), String.class));
        }
        
        if (currentUserId != null) {
            MerchantReviewLike like = likeMapper.selectByUserAndReview(currentUserId, review.getId(), 1);
            response.setLikedByCurrentUser(like != null);
        }
        
        return response;
    }
}
