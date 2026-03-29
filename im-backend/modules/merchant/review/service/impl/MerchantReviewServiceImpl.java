package com.im.backend.modules.merchant.review.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.im.backend.common.exception.BusinessException;
import com.im.backend.common.result.Result;
import com.im.backend.common.result.ResultCode;
import com.im.backend.modules.merchant.review.dto.CreateReviewRequest;
import com.im.backend.modules.merchant.review.dto.ReviewResponse;
import com.im.backend.modules.merchant.review.entity.*;
import com.im.backend.modules.merchant.review.mapper.MerchantReviewMapper;
import com.im.backend.modules.merchant.review.service.MerchantReviewService;
import com.im.backend.modules.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 商户评价服务实现类
 * @author IM Development Team
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantReviewServiceImpl extends ServiceImpl<MerchantReviewMapper, MerchantReview>
        implements MerchantReviewService {

    private final MerchantReviewMapper reviewMapper;
    private final StringRedisTemplate redisTemplate;
    private final MessageService messageService;

    private static final String REVIEW_LIKE_KEY_PREFIX = "review:like:";
    private static final String REVIEW_VIEW_KEY_PREFIX = "review:view:";
    private static final String MERCHANT_RATING_KEY_PREFIX = "merchant:rating:";

    /**
     * 创建评价
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<ReviewResponse> createReview(Long userId, CreateReviewRequest request) {
        // 检查用户是否已评价过该订单
        if (request.getOrderId() != null && hasReviewedOrder(userId, request.getOrderId())) {
            return Result.error(ResultCode.REPEAT_OPERATION, "您已评价过该订单");
        }

        // 创建评价实体
        MerchantReview review = new MerchantReview();
        review.setMerchantId(request.getMerchantId());
        review.setPoiId(request.getPoiId());
        review.setUserId(userId);
        review.setOrderId(request.getOrderId());
        review.setOverallRating(request.getOverallRating());
        review.setTitle(request.getTitle());
        review.setContent(request.getContent());
        review.setReviewType(request.getReviewType());
        review.setConsumptionAmount(request.getConsumptionAmount());
        review.setConsumptionTime(request.getConsumptionTime());
        review.setPerCapitaAmount(request.getPerCapitaAmount());
        review.setIsRecommended(request.getIsRecommended());
        review.setIsAnonymous(request.getIsAnonymous());
        review.setStatus(MerchantReview.STATUS_PENDING); // 待审核
        review.setLikeCount(0);
        review.setReplyCount(0);
        review.setViewCount(0);
        review.setIsTop(0);
        review.setIsQuality(0);
        review.setFakeFlag(0);
        review.setAppealStatus(0);

        // 保存评价
        save(review);

        // 保存评价维度
        if (!CollectionUtils.isEmpty(request.getDimensionList())) {
            List<ReviewDimension> dimensions = request.getDimensionList().stream()
                    .map(dto -> {
                        ReviewDimension dim = new ReviewDimension();
                        dim.setReviewId(review.getId());
                        dim.setMerchantId(review.getMerchantId());
                        dim.setPoiId(review.getPoiId());
                        dim.setDimensionCode(dto.getDimensionCode());
                        dim.setDimensionName(dto.getDimensionName());
                        dim.setRating(dto.getRating());
                        dim.setTags(dto.getTags());
                        return dim;
                    }).collect(Collectors.toList());
            review.setDimensionList(dimensions);
        }

        // 保存媒体文件
        if (!CollectionUtils.isEmpty(request.getMediaList())) {
            List<ReviewMedia> mediaList = request.getMediaList().stream()
                    .map(dto -> {
                        ReviewMedia media = new ReviewMedia();
                        media.setReviewId(review.getId());
                        media.setMerchantId(review.getMerchantId());
                        media.setUserId(userId);
                        media.setMediaType(dto.getMediaType());
                        media.setMediaUrl(dto.getMediaUrl());
                        media.setThumbnailUrl(dto.getThumbnailUrl());
                        media.setDescription(dto.getDescription());
                        media.setDuration(dto.getDuration());
                        media.setVideoCover(dto.getVideoCover());
                        media.setWidth(dto.getWidth());
                        media.setHeight(dto.getHeight());
                        media.setAiAuditStatus(0);
                        media.setManualAuditStatus(0);
                        return media;
                    }).collect(Collectors.toList());
            review.setMediaList(mediaList);
        }

        // 计算优质评分
        review.calculateQualityScore();

        // 检测虚假评价
        review.detectFakeReview();

        // 更新评价
        updateById(review);

        // 清除商户评分缓存
        clearMerchantRatingCache(request.getMerchantId());

        // 发送消息通知商户
        messageService.sendMerchantReviewNotification(review.getMerchantId(), review.getId());

        log.info("用户{}创建评价成功，评价ID：{}", userId, review.getId());

        return Result.success(convertToResponse(review, userId));
    }

    /**
     * 获取评价详情
     */
    @Override
    public ReviewResponse getReviewDetail(Long reviewId, Long currentUserId) {
        MerchantReview review = getById(reviewId);
        if (review == null || review.getDeleted() == 1) {
            throw new BusinessException(ResultCode.NOT_FOUND, "评价不存在");
        }

        // 增加浏览量
        incrementViewCount(reviewId);

        return convertToResponse(review, currentUserId);
    }

    /**
     * 分页查询商户评价列表
     */
    @Override
    public IPage<ReviewResponse> getMerchantReviews(Long merchantId, Integer page, Integer size,
                                                    BigDecimal ratingMin, BigDecimal ratingMax,
                                                    Integer reviewType, Integer sortType) {
        Page<MerchantReview> pageParam = new Page<>(page, size);
        IPage<MerchantReview> reviewPage = reviewMapper.selectReviewPage(
                pageParam, merchantId, null, null, ratingMin, ratingMax,
                reviewType, null, MerchantReview.STATUS_APPROVED, null, sortType);

        List<ReviewResponse> records = reviewPage.getRecords().stream()
                .map(r -> convertToResponse(r, null))
                .collect(Collectors.toList());

        IPage<ReviewResponse> resultPage = new Page<>();
        BeanUtils.copyProperties(reviewPage, resultPage);
        resultPage.setRecords(records);
        return resultPage;
    }

    /**
     * 分页查询POI评价列表
     */
    @Override
    public IPage<ReviewResponse> getPoiReviews(Long poiId, Integer page, Integer size, Integer sortType) {
        Page<MerchantReview> pageParam = new Page<>(page, size);
        IPage<MerchantReview> reviewPage = reviewMapper.selectReviewPage(
                pageParam, null, poiId, null, null, null,
                null, null, MerchantReview.STATUS_APPROVED, null, sortType);

        List<ReviewResponse> records = reviewPage.getRecords().stream()
                .map(r -> convertToResponse(r, null))
                .collect(Collectors.toList());

        IPage<ReviewResponse> resultPage = new Page<>();
        BeanUtils.copyProperties(reviewPage, resultPage);
        resultPage.setRecords(records);
        return resultPage;
    }

    /**
     * 查询用户评价列表
     */
    @Override
    public List<ReviewResponse> getUserReviews(Long userId, Integer limit) {
        List<MerchantReview> reviews = reviewMapper.selectUserReviews(userId, limit);
        return reviews.stream()
                .map(r -> convertToResponse(r, userId))
                .collect(Collectors.toList());
    }

    /**
     * 点赞评价
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> likeReview(Long userId, Long reviewId) {
        String likeKey = REVIEW_LIKE_KEY_PREFIX + reviewId + ":" + userId;

        // 检查是否已点赞
        Boolean hasLiked = redisTemplate.hasKey(likeKey);
        if (Boolean.TRUE.equals(hasLiked)) {
            return Result.error(ResultCode.REPEAT_OPERATION, "已点赞过该评价");
        }

        // 设置点赞标记
        redisTemplate.opsForValue().set(likeKey, "1", 30, TimeUnit.DAYS);

        // 增加点赞数
        reviewMapper.incrementLikeCount(reviewId);

        // 发送消息通知评价作者
        MerchantReview review = getById(reviewId);
        if (review != null) {
            messageService.sendReviewLikeNotification(review.getUserId(), reviewId, userId);
        }

        return Result.success();
    }

    /**
     * 取消点赞
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> unlikeReview(Long userId, Long reviewId) {
        String likeKey = REVIEW_LIKE_KEY_PREFIX + reviewId + ":" + userId;

        // 删除点赞标记
        redisTemplate.delete(likeKey);

        // 减少点赞数
        reviewMapper.decrementLikeCount(reviewId);

        return Result.success();
    }

    /**
     * 回复评价
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<ReviewResponse.ReplyResponse> replyReview(Long userId, Integer userType,
                                                            Long reviewId, String content, Long parentId) {
        MerchantReview review = getById(reviewId);
        if (review == null || review.getDeleted() == 1) {
            return Result.error(ResultCode.NOT_FOUND, "评价不存在");
        }

        // 创建回复
        ReviewReply reply = new ReviewReply();
        reply.setReviewId(reviewId);
        reply.setMerchantId(review.getMerchantId());
        reply.setParentId(parentId);
        reply.setReplyUserId(userId);
        reply.setReplyUserType(userType);
        reply.setContent(content);
        reply.setStatus(ReviewReply.STATUS_APPROVED);
        reply.setLikeCount(0);
        reply.setIsOfficial(userType == ReviewReply.REPLY_TYPE_MERCHANT ? 1 : 0);
        reply.setIsHidden(0);

        // 设置被回复者
        if (parentId != null) {
            // 回复的回复
            // TODO: 查询父回复获取被回复者信息
        } else {
            // 直接回复评价
            reply.setToUserId(review.getUserId());
            reply.setToUserType(ReviewReply.REPLY_TYPE_USER);
        }

        // TODO: 保存回复

        // 增加评价的回复数
        reviewMapper.incrementReplyCount(reviewId);

        // 发送消息通知
        if (userType == ReviewReply.REPLY_TYPE_MERCHANT) {
            messageService.sendReviewReplyNotification(review.getUserId(), reviewId, content);
        }

        ReviewResponse.ReplyResponse response = new ReviewResponse.ReplyResponse();
        BeanUtils.copyProperties(reply, response);
        return Result.success(response);
    }

    /**
     * 删除评价
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteReview(Long userId, Long reviewId) {
        MerchantReview review = getById(reviewId);
        if (review == null) {
            return Result.error(ResultCode.NOT_FOUND, "评价不存在");
        }

        // 只能删除自己的评价，或管理员删除
        if (!review.getUserId().equals(userId)) {
            return Result.error(ResultCode.FORBIDDEN, "无权删除该评价");
        }

        // 逻辑删除
        removeById(reviewId);

        // 清除缓存
        clearMerchantRatingCache(review.getMerchantId());

        return Result.success();
    }

    /**
     * 置顶评价
     */
    @Override
    public Result<Void> topReview(Long reviewId, Integer weight, Long operatorId) {
        MerchantReview review = getById(reviewId);
        if (review == null) {
            return Result.error(ResultCode.NOT_FOUND, "评价不存在");
        }

        review.setTop(weight);
        updateById(review);

        log.info("操作员{}置顶评价{}，权重{}", operatorId, reviewId, weight);
        return Result.success();
    }

    /**
     * 取消置顶
     */
    @Override
    public Result<Void> cancelTopReview(Long reviewId, Long operatorId) {
        MerchantReview review = getById(reviewId);
        if (review == null) {
            return Result.error(ResultCode.NOT_FOUND, "评价不存在");
        }

        review.cancelTop();
        updateById(review);

        log.info("操作员{}取消置顶评价{}", operatorId, reviewId);
        return Result.success();
    }

    /**
     * 审核评价
     */
    @Override
    public Result<Void> auditReview(Long reviewId, Integer status, Long auditorId, String remark) {
        MerchantReview review = getById(reviewId);
        if (review == null) {
            return Result.error(ResultCode.NOT_FOUND, "评价不存在");
        }

        if (status == MerchantReview.STATUS_APPROVED) {
            review.approve(auditorId);
        } else if (status == MerchantReview.STATUS_REJECTED) {
            review.reject(auditorId, remark);
        } else {
            return Result.error(ResultCode.PARAM_ERROR, "无效的审核状态");
        }

        updateById(review);

        // 清除缓存
        clearMerchantRatingCache(review.getMerchantId());

        // 发送审核结果通知
        messageService.sendReviewAuditNotification(review.getUserId(), reviewId, status == MerchantReview.STATUS_APPROVED);

        log.info("审核员{}审核评价{}为状态{}", auditorId, reviewId, status);
        return Result.success();
    }

    /**
     * 批量审核评价
     */
    @Override
    public Result<Void> batchAuditReviews(List<Long> reviewIds, Integer status, Long auditorId) {
        if (CollectionUtils.isEmpty(reviewIds)) {
            return Result.error(ResultCode.PARAM_ERROR, "评价ID列表不能为空");
        }

        reviewMapper.batchUpdateStatus(reviewIds, status, auditorId);

        log.info("批量审核{}条评价为状态{}", reviewIds.size(), status);
        return Result.success();
    }

    /**
     * 申诉评价
     */
    @Override
    public Result<Void> appealReview(Long userId, Long reviewId, String reason) {
        MerchantReview review = getById(reviewId);
        if (review == null) {
            return Result.error(ResultCode.NOT_FOUND, "评价不存在");
        }

        // 只能申诉自己的评价
        if (!review.getUserId().equals(userId)) {
            return Result.error(ResultCode.FORBIDDEN, "只能申诉自己的评价");
        }

        if (!review.canAppeal()) {
            return Result.error(ResultCode.BUSINESS_ERROR, "当前评价状态不可申诉");
        }

        review.submitAppeal(reason);
        updateById(review);

        log.info("用户{}申诉评价{}，理由：{}", userId, reviewId, reason);
        return Result.success();
    }

    /**
     * 处理申诉
     */
    @Override
    public Result<Void> processAppeal(Long reviewId, Boolean approved, String remark, Long operatorId) {
        MerchantReview review = getById(reviewId);
        if (review == null) {
            return Result.error(ResultCode.NOT_FOUND, "评价不存在");
        }

        review.processAppeal(approved, remark);
        updateById(review);

        // 清除缓存
        clearMerchantRatingCache(review.getMerchantId());

        // 发送申诉结果通知
        messageService.sendAppealResultNotification(review.getUserId(), reviewId, approved);

        log.info("操作员{}处理评价{}申诉，结果：{}", operatorId, reviewId, approved);
        return Result.success();
    }

    /**
     * 获取商户评价统计
     */
    @Override
    public Map<String, Object> getMerchantReviewStats(Long merchantId) {
        String cacheKey = MERCHANT_RATING_KEY_PREFIX + merchantId + ":stats";

        // 从缓存获取
        // TODO: 实现缓存读取

        Integer totalCount = reviewMapper.countMerchantReviews(merchantId);
        BigDecimal averageRating = reviewMapper.selectMerchantAverageRating(merchantId);
        Integer qualityCount = reviewMapper.countQualityReviews(merchantId);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCount", totalCount);
        stats.put("averageRating", averageRating != null ? averageRating.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        stats.put("qualityCount", qualityCount);

        // 设置缓存
        // TODO: 实现缓存写入

        return stats;
    }

    /**
     * 获取商户评分分布
     */
    @Override
    public Map<String, Integer> getMerchantRatingDistribution(Long merchantId) {
        List<Map<String, Object>> distribution = reviewMapper.selectMerchantRatingDistribution(merchantId);

        Map<String, Integer> result = new HashMap<>();
        result.put("5", 0);
        result.put("4", 0);
        result.put("3", 0);
        result.put("2", 0);
        result.put("1", 0);

        for (Map<String, Object> item : distribution) {
            BigDecimal rating = (BigDecimal) item.get("rating");
            Long count = (Long) item.get("count");
            if (rating != null) {
                result.put(String.valueOf(rating.intValue()), count.intValue());
            }
        }

        return result;
    }

    /**
     * 获取推荐评价（优质+高互动）
     */
    @Override
    public List<ReviewResponse> getRecommendedReviews(Long merchantId, Integer limit) {
        List<MerchantReview> reviews = reviewMapper.selectRecommendedReviews(merchantId, limit);
        return reviews.stream()
                .map(r -> convertToResponse(r, null))
                .collect(Collectors.toList());
    }

    /**
     * 获取置顶评价
     */
    @Override
    public List<ReviewResponse> getTopReviews(Long merchantId, Integer limit) {
        List<MerchantReview> reviews = reviewMapper.selectTopReviews(merchantId, limit);
        return reviews.stream()
                .map(r -> convertToResponse(r, null))
                .collect(Collectors.toList());
    }

    /**
     * 搜索评价
     */
    @Override
    public List<ReviewResponse> searchReviews(String keyword, Long merchantId, Integer limit) {
        if (!StringUtils.hasText(keyword)) {
            return Collections.emptyList();
        }
        List<MerchantReview> reviews = reviewMapper.searchReviews(keyword, merchantId, limit);
        return reviews.stream()
                .map(r -> convertToResponse(r, null))
                .collect(Collectors.toList());
    }

    /**
     * 获取待审核评价列表
     */
    @Override
    public List<ReviewResponse> getPendingReviews(Integer limit) {
        List<MerchantReview> reviews = reviewMapper.selectPendingReviews(limit);
        return reviews.stream()
                .map(r -> convertToResponse(r, null))
                .collect(Collectors.toList());
    }

    /**
     * 获取疑似虚假评价
     */
    @Override
    public List<ReviewResponse> getSuspectedFakeReviews(BigDecimal confidenceMin, Integer limit) {
        List<MerchantReview> reviews = reviewMapper.selectSuspectedFakeReviews(confidenceMin, limit);
        return reviews.stream()
                .map(r -> convertToResponse(r, null))
                .collect(Collectors.toList());
    }

    /**
     * 标记虚假评价
     */
    @Override
    public Result<Void> markFakeReview(Long reviewId, Integer fakeFlag, Long operatorId) {
        MerchantReview review = getById(reviewId);
        if (review == null) {
            return Result.error(ResultCode.NOT_FOUND, "评价不存在");
        }

        review.setFakeFlag(fakeFlag);
        updateById(review);

        log.info("操作员{}标记评价{}为虚假状态{}", operatorId, reviewId, fakeFlag);
        return Result.success();
    }

    /**
     * 增加浏览量
     */
    @Override
    public void incrementViewCount(Long reviewId) {
        String viewKey = REVIEW_VIEW_KEY_PREFIX + reviewId;
        Long views = redisTemplate.opsForValue().increment(viewKey);

        // 每100次浏览，同步到数据库
        if (views != null && views % 100 == 0) {
            reviewMapper.incrementViewCount(reviewId);
            redisTemplate.delete(viewKey);
        }
    }

    /**
     * 检查用户是否已评价过订单
     */
    @Override
    public Boolean hasReviewedOrder(Long userId, Long orderId) {
        if (orderId == null) {
            return false;
        }
        Integer count = reviewMapper.countReviewByOrder(orderId, userId);
        return count != null && count > 0;
    }

    /**
     * 获取POI评价统计
     */
    @Override
    public Map<String, Object> getPoiReviewStats(Long poiId) {
        return reviewMapper.selectPoiReviewStats(poiId);
    }

    /**
     * 清除商户评分缓存
     */
    private void clearMerchantRatingCache(Long merchantId) {
        String cacheKey = MERCHANT_RATING_KEY_PREFIX + merchantId + ":*";
        // TODO: 实现缓存清除
    }

    /**
     * 转换为响应DTO
     */
    private ReviewResponse convertToResponse(MerchantReview review, Long currentUserId) {
        ReviewResponse response = new ReviewResponse();
        BeanUtils.copyProperties(review, response);

        // 设置维度列表
        if (review.getDimensionList() != null) {
            List<ReviewResponse.DimensionResponse> dimResponses = review.getDimensionList().stream()
                    .map(dim -> {
                        ReviewResponse.DimensionResponse d = new ReviewResponse.DimensionResponse();
                        BeanUtils.copyProperties(dim, d);
                        d.setRatingLabel(dim.getRatingLabel());
                        return d;
                    }).collect(Collectors.toList());
            response.setDimensionList(dimResponses);
        }

        // 设置媒体列表
        if (review.getMediaList() != null) {
            List<ReviewResponse.MediaResponse> mediaResponses = review.getMediaList().stream()
                    .map(media -> {
                        ReviewResponse.MediaResponse m = new ReviewResponse.MediaResponse();
                        BeanUtils.copyProperties(media, m);
                        return m;
                    }).collect(Collectors.toList());
            response.setMediaList(mediaResponses);
        }

        // 检查当前用户是否点赞
        if (currentUserId != null) {
            String likeKey = REVIEW_LIKE_KEY_PREFIX + review.getId() + ":" + currentUserId;
            Boolean hasLiked = redisTemplate.hasKey(likeKey);
            response.setHasLiked(Boolean.TRUE.equals(hasLiked));
        }

        return response;
    }
}
