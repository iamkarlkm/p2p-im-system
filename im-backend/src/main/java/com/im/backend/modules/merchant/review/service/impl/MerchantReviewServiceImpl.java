package com.im.backend.modules.merchant.review.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.im.backend.common.core.result.PageResult;
import com.im.backend.common.util.SnowflakeIdGenerator;
import com.im.backend.modules.merchant.review.dto.*;
import com.im.backend.modules.merchant.review.entity.MerchantReview;
import com.im.backend.modules.merchant.review.enums.ReviewStatus;
import com.im.backend.modules.merchant.review.repository.MerchantReviewMapper;
import com.im.backend.modules.merchant.review.repository.MerchantReviewLikeMapper;
import com.im.backend.modules.merchant.review.service.IMerchantReviewService;
import com.im.backend.modules.merchant.review.service.IMerchantReputationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商户评价服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantReviewServiceImpl implements IMerchantReviewService {

    private final MerchantReviewMapper reviewMapper;
    private final MerchantReviewLikeMapper likeMapper;
    private final IMerchantReputationService reputationService;
    private final SnowflakeIdGenerator idGenerator;

    @Override
    @Transactional
    public String submitReview(Long userId, SubmitReviewRequest request) {
        // 检查是否已评价
        if (request.getOrderId() != null) {
            int count = reviewMapper.countByUserAndOrder(userId, request.getOrderId());
            if (count > 0) {
                throw new RuntimeException("您已评价过该订单");
            }
        }

        // 创建评价
        MerchantReview review = MerchantReview.builder()
                .reviewId("REV" + idGenerator.nextId())
                .merchantId(request.getMerchantId())
                .userId(userId)
                .orderId(request.getOrderId())
                .overallRating(request.getOverallRating())
                .tasteRating(request.getTasteRating())
                .environmentRating(request.getEnvironmentRating())
                .serviceRating(request.getServiceRating())
                .valueRating(request.getValueRating())
                .content(request.getContent())
                .images(CollectionUtils.isEmpty(request.getImages()) ? null : JSON.toJSONString(request.getImages()))
                .videoUrl(request.getVideoUrl())
                .anonymous(request.getAnonymous())
                .consumeAmount(request.getConsumeAmount())
                .dinerCount(request.getDinerCount())
                .perCapitaAmount(request.getPerCapitaAmount())
                .likeCount(0)
                .replyCount(0)
                .viewCount(0)
                .status(0) // 待审核
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        reviewMapper.insert(review);

        // 异步更新商户口碑统计
        reputationService.updateMerchantReputation(request.getMerchantId());

        log.info("用户 {} 提交评价 {} 给商户 {}", userId, review.getReviewId(), request.getMerchantId());
        return review.getReviewId();
    }

    @Override
    public ReviewDetailResponse getReviewDetail(String reviewId, Long currentUserId) {
        MerchantReview review = reviewMapper.selectByReviewId(reviewId);
        if (review == null || review.getStatus() == ReviewStatus.DELETED.getCode()) {
            throw new RuntimeException("评价不存在");
        }

        // 增加浏览数
        reviewMapper.incrementViewCount(reviewId);

        return convertToDetailResponse(review, currentUserId);
    }

    @Override
    public ReviewListResponse getMerchantReviews(Long merchantId, ReviewListRequest request) {
        Page<MerchantReview> page = new Page<>(request.getPage(), request.getSize());
        IPage<MerchantReview> reviewPage = reviewMapper.selectByMerchantId(page, merchantId);

        List<ReviewDetailResponse> reviews = reviewPage.getRecords().stream()
                .map(r -> convertToDetailResponse(r, null))
                .collect(Collectors.toList());

        ReviewListResponse response = new ReviewListResponse();
        response.setReviews(reviews);
        response.setTotal(reviewPage.getTotal());
        response.setPage(request.getPage());
        response.setSize(request.getSize());
        response.setHasMore(reviewPage.getCurrent() < reviewPage.getPages());

        return response;
    }

    @Override
    @Transactional
    public void deleteReview(String reviewId, Long userId) {
        MerchantReview review = reviewMapper.selectByReviewId(reviewId);
        if (review == null) {
            throw new RuntimeException("评价不存在");
        }
        if (!review.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除该评价");
        }

        review.setStatus(ReviewStatus.DELETED.getCode());
        review.setUpdatedAt(LocalDateTime.now());
        reviewMapper.updateById(review);

        reputationService.updateMerchantReputation(review.getMerchantId());
        log.info("用户 {} 删除评价 {}", userId, reviewId);
    }

    @Override
    @Transactional
    public void likeReview(Long userId, LikeReviewRequest request) {
        String reviewId = request.getReviewId();
        boolean like = request.getLike();

        int count = likeMapper.countByReviewAndUser(reviewId, userId);

        if (like) {
            if (count == 0) {
                // 添加点赞
                MerchantReviewLike likeRecord = new MerchantReviewLike();
                likeRecord.setLikeId("LK" + idGenerator.nextId());
                likeRecord.setReviewId(reviewId);
                likeRecord.setUserId(userId);
                likeRecord.setCreatedAt(LocalDateTime.now());
                likeMapper.insert(likeRecord);

                reviewMapper.updateLikeCount(reviewId, 1);
            }
        } else {
            if (count > 0) {
                likeMapper.deleteByReviewAndUser(reviewId, userId);
                reviewMapper.updateLikeCount(reviewId, -1);
            }
        }
    }

    @Override
    public boolean hasLiked(String reviewId, Long userId) {
        return likeMapper.countByReviewAndUser(reviewId, userId) > 0;
    }

    @Override
    @Transactional
    public void auditReview(Long auditorId, AuditReviewRequest request) {
        MerchantReview review = reviewMapper.selectByReviewId(request.getReviewId());
        if (review == null) {
            throw new RuntimeException("评价不存在");
        }

        review.setStatus(request.getStatus());
        review.setAuditedAt(LocalDateTime.now());
        review.setAuditorId(auditorId);
        reviewMapper.updateById(review);

        if (request.getStatus() == ReviewStatus.APPROVED.getCode()) {
            reputationService.updateMerchantReputation(review.getMerchantId());
        }

        log.info("审核员 {} 审核评价 {} 结果为 {}", auditorId, request.getReviewId(), request.getStatus());
    }

    @Override
    @Transactional
    public void reportReview(Long reporterId, ReportReviewRequest request) {
        // 实现举报逻辑
        log.info("用户 {} 举报评价 {}", reporterId, request.getReviewId());
    }

    @Override
    public PageResult<ReviewDetailResponse> getPendingReviews(Integer page, Integer size) {
        Page<MerchantReview> pageParam = new Page<>(page, size);
        IPage<MerchantReview> reviewPage = reviewMapper.selectPendingReviews(pageParam);

        List<ReviewDetailResponse> list = reviewPage.getRecords().stream()
                .map(r -> convertToDetailResponse(r, null))
                .collect(Collectors.toList());

        return PageResult.of(list, reviewPage.getTotal(), page, size);
    }

    @Override
    public ReviewListResponse getUserReviews(Long userId, Integer page, Integer size) {
        Page<MerchantReview> pageParam = new Page<>(page, size);
        IPage<MerchantReview> reviewPage = reviewMapper.selectByUserId(pageParam, userId);

        List<ReviewDetailResponse> reviews = reviewPage.getRecords().stream()
                .map(r -> convertToDetailResponse(r, userId))
                .collect(Collectors.toList());

        ReviewListResponse response = new ReviewListResponse();
        response.setReviews(reviews);
        response.setTotal(reviewPage.getTotal());
        response.setPage(page);
        response.setSize(size);
        return response;
    }

    private ReviewDetailResponse convertToDetailResponse(MerchantReview review, Long currentUserId) {
        ReviewDetailResponse dto = new ReviewDetailResponse();
        dto.setReviewId(review.getReviewId());
        dto.setMerchantId(review.getMerchantId());
        dto.setUserId(review.getUserId());
        dto.setOverallRating(review.getOverallRating());
        dto.setTasteRating(review.getTasteRating());
        dto.setEnvironmentRating(review.getEnvironmentRating());
        dto.setServiceRating(review.getServiceRating());
        dto.setValueRating(review.getValueRating());
        dto.setContent(review.getContent());
        dto.setVideoUrl(review.getVideoUrl());
        dto.setAnonymous(review.getAnonymous());
        dto.setLikeCount(review.getLikeCount());
        dto.setReplyCount(review.getReplyCount());
        dto.setEliteReview(review.getEliteReview());
        dto.setCreatedAt(review.getCreatedAt());

        // 解析图片
        if (StringUtils.hasText(review.getImages())) {
            dto.setImages(JSON.parseArray(review.getImages(), String.class));
        }

        // 检查是否已点赞
        if (currentUserId != null) {
            dto.setHasLiked(hasLiked(review.getReviewId(), currentUserId));
        }

        return dto;
    }
}
