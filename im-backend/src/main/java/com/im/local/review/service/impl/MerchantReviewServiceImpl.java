package com.im.local.review.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.im.local.review.dto.*;
import com.im.local.review.entity.*;
import com.im.local.review.enums.*;
import com.im.local.review.repository.*;
import com.im.local.review.service.IMerchantReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商户评价服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantReviewServiceImpl extends ServiceImpl<MerchantReviewMapper, MerchantReview>
        implements IMerchantReviewService {

    private final MerchantReviewMapper reviewMapper;
    private final MerchantReviewMediaMapper mediaMapper;
    private final MerchantReviewLikeMapper likeMapper;
    private final MerchantReputationStatsMapper reputationMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReviewResponse createReview(Long userId, CreateReviewRequest request) {
        // 创建评价实体
        MerchantReview review = new MerchantReview();
        BeanUtils.copyProperties(request, review);
        review.setUserId(userId);
        review.setStatus(ReviewStatus.PENDING_AUDIT.getCode());
        review.setLikeCount(0);
        review.setReplyCount(0);
        review.setViewCount(0);
        review.setIsRecommended(0);
        review.setCreatedAt(LocalDateTime.now());

        // 保存评价
        reviewMapper.insert(review);

        // 保存媒体文件
        if (!CollectionUtils.isEmpty(request.getMediaList())) {
            List<MerchantReviewMedia> mediaList = request.getMediaList().stream()
                    .map(dto -> {
                        MerchantReviewMedia media = new MerchantReviewMedia();
                        BeanUtils.copyProperties(dto, media);
                        media.setReviewId(review.getId());
                        media.setCreatedAt(LocalDateTime.now());
                        return media;
                    }).collect(Collectors.toList());
            mediaMapper.batchInsert(mediaList);
        }

        log.info("用户 {} 创建评价成功，商户ID: {}", userId, request.getMerchantId());
        return convertToResponse(review, userId);
    }

    @Override
    public Page<ReviewResponse> queryReviews(ReviewQueryRequest request) {
        Page<MerchantReview> page = new Page<>(request.getPageNum(), request.getPageSize());

        // 构建查询条件
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<MerchantReview> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();

        if (request.getMerchantId() != null) {
            wrapper.eq("merchant_id", request.getMerchantId());
        }
        if (request.getUserId() != null) {
            wrapper.eq("user_id", request.getUserId());
        }
        if (request.getMinRating() != null) {
            wrapper.ge("overall_rating", request.getMinRating());
        }
        if (request.getMaxRating() != null) {
            wrapper.le("overall_rating", request.getMaxRating());
        }
        if (request.getHasImage() != null && request.getHasImage() == 1) {
            wrapper.eq("review_type", ReviewType.IMAGE.getCode());
        }

        wrapper.eq("status", ReviewStatus.PUBLISHED.getCode());
        wrapper.eq("deleted", 0);

        // 排序
        ReviewSortType sortType = ReviewSortType.fromCode(request.getSortType());
        switch (sortType) {
            case HIGHEST_RATING:
                wrapper.orderByDesc("overall_rating");
                break;
            case LOWEST_RATING:
                wrapper.orderByAsc("overall_rating");
                break;
            case MOST_LIKED:
                wrapper.orderByDesc("like_count");
                break;
            case RECOMMENDED:
                wrapper.orderByDesc("is_recommended");
                break;
            case LATEST:
            default:
                wrapper.orderByDesc("created_at");
        }

        Page<MerchantReview> result = reviewMapper.selectPage(page, wrapper);

        // 转换结果
        List<ReviewResponse> records = result.getRecords().stream()
                .map(review -> convertToResponse(review, null))
                .collect(Collectors.toList());

        Page<ReviewResponse> responsePage = new Page<>();
        BeanUtils.copyProperties(result, responsePage);
        responsePage.setRecords(records);

        return responsePage;
    }

    @Override
    public ReviewResponse getReviewDetail(Long reviewId, Long currentUserId) {
        MerchantReview review = reviewMapper.selectById(reviewId);
        if (review == null || review.getDeleted() == 1) {
            throw new RuntimeException("评价不存在");
        }
        return convertToResponse(review, currentUserId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void likeReview(Long userId, LikeReviewRequest request) {
        Long reviewId = request.getReviewId();

        if (request.getAction() == 1) {
            // 点赞
            MerchantReviewLike like = new MerchantReviewLike();
            like.setReviewId(reviewId);
            like.setUserId(userId);
            like.setCreatedAt(LocalDateTime.now());
            likeMapper.insert(like);
            reviewMapper.incrementLikeCount(reviewId);
        } else {
            // 取消点赞
            likeMapper.deleteByUser(reviewId, userId);
            reviewMapper.decrementLikeCount(reviewId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void merchantReply(Long merchantId, MerchantReplyRequest request) {
        MerchantReview review = reviewMapper.selectById(request.getReviewId());
        if (review == null) {
            throw new RuntimeException("评价不存在");
        }

        // 更新商家回复
        review.setMerchantReply(request.getContent());
        review.setMerchantReplyTime(LocalDateTime.now());
        reviewMapper.updateById(review);

        log.info("商户 {} 回复评价 {}", merchantId, request.getReviewId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteReview(Long reviewId, Long operatorId, Integer operatorType) {
        MerchantReview review = reviewMapper.selectById(reviewId);
        if (review == null) {
            throw new RuntimeException("评价不存在");
        }

        // 权限检查
        if (operatorType == 1) {
            // 用户只能删除自己的评价
            if (!review.getUserId().equals(operatorId)) {
                throw new RuntimeException("无权删除该评价");
            }
        }

        reviewMapper.deleteById(reviewId);
        log.info("评价 {} 被删除，操作人: {}", reviewId, operatorId);
    }

    @Override
    public MerchantReputationResponse getMerchantReputation(Long merchantId) {
        MerchantReputationStats stats = reputationMapper.selectByMerchantId(merchantId);
        if (stats == null) {
            return new MerchantReputationResponse();
        }

        MerchantReputationResponse response = new MerchantReputationResponse();
        BeanUtils.copyProperties(stats, response);

        // 计算评分分布
        List<RatingDistributionDTO> distribution = new ArrayList<>();
        int total = stats.getTotalReviews();
        if (total > 0) {
            addDistribution(distribution, 5, stats.getFiveStarCount(), total);
            addDistribution(distribution, 4, stats.getFourStarCount(), total);
            addDistribution(distribution, 3, stats.getThreeStarCount(), total);
            addDistribution(distribution, 2, stats.getTwoStarCount(), total);
            addDistribution(distribution, 1, stats.getOneStarCount(), total);
        }
        response.setRatingDistribution(distribution);

        return response;
    }

    @Override
    public void auditReview(Long reviewId, Integer status, String reason) {
        MerchantReview review = reviewMapper.selectById(reviewId);
        if (review == null) {
            throw new RuntimeException("评价不存在");
        }

        review.setStatus(status);
        reviewMapper.updateById(review);

        log.info("评价 {} 审核状态更新为 {}", reviewId, status);
    }

    /**
     * 转换为响应DTO
     */
    private ReviewResponse convertToResponse(MerchantReview review, Long currentUserId) {
        ReviewResponse response = new ReviewResponse();
        BeanUtils.copyProperties(review, response);

        // 设置状态描述
        response.setStatusDesc(ReviewStatus.fromCode(review.getStatus()).getDesc());
        response.setReviewTypeDesc(ReviewType.fromCode(review.getReviewType()).getDesc());

        // 格式化时间
        response.setTimeDesc(formatTime(review.getCreatedAt()));

        // 查询媒体列表
        List<MerchantReviewMedia> mediaList = mediaMapper.selectByReviewId(review.getId());
        if (!CollectionUtils.isEmpty(mediaList)) {
            response.setMediaList(mediaList.stream()
                    .map(media -> {
                        ReviewMediaDTO dto = new ReviewMediaDTO();
                        BeanUtils.copyProperties(media, dto);
                        return dto;
                    }).collect(Collectors.toList()));
        }

        // 检查当前用户是否点赞
        if (currentUserId != null) {
            int count = likeMapper.countByUser(review.getId(), currentUserId);
            response.setHasLiked(count > 0);
        }

        return response;
    }

    /**
     * 格式化时间
     */
    private String formatTime(LocalDateTime time) {
        LocalDateTime now = LocalDateTime.now();
        long days = java.time.Duration.between(time, now).toDays();

        if (days == 0) {
            return "今天 " + time.format(DateTimeFormatter.ofPattern("HH:mm"));
        } else if (days == 1) {
            return "昨天 " + time.format(DateTimeFormatter.ofPattern("HH:mm"));
        } else if (days < 7) {
            return days + "天前";
        } else if (days < 30) {
            return (days / 7) + "周前";
        } else {
            return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
    }

    /**
     * 添加评分分布
     */
    private void addDistribution(List<RatingDistributionDTO> list, int star, int count, int total) {
        RatingDistributionDTO dto = new RatingDistributionDTO();
        dto.setStar(star);
        dto.setCount(count);
        dto.setPercentage(total > 0 ? Math.round(count * 100.0 / total * 100) / 100.0 : 0.0);
        list.add(dto);
    }
}
