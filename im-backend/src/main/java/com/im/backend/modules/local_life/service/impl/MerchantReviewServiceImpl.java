package com.im.backend.modules.local_life.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.im.backend.common.PageResult;
import com.im.backend.common.exception.BusinessException;
import com.im.backend.modules.local_life.dto.*;
import com.im.backend.modules.local_life.entity.MerchantReview;
import com.im.backend.modules.local_life.entity.MerchantReviewLike;
import com.im.backend.modules.local_life.entity.MerchantReviewStatistic;
import com.im.backend.modules.local_life.enums.ReviewSortType;
import com.im.backend.modules.local_life.enums.ReviewStatus;
import com.im.backend.modules.local_life.repository.MerchantReviewLikeRepository;
import com.im.backend.modules.local_life.repository.MerchantReviewRepository;
import com.im.backend.modules.local_life.repository.MerchantReviewStatisticRepository;
import com.im.backend.modules.local_life.service.MerchantReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商户评价 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantReviewServiceImpl extends ServiceImpl<MerchantReviewRepository, MerchantReview>
        implements MerchantReviewService {

    private final MerchantReviewRepository reviewRepository;
    private final MerchantReviewLikeRepository likeRepository;
    private final MerchantReviewStatisticRepository statisticRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MerchantReviewDTO createReview(Long userId, CreateReviewRequestDTO request) {
        // 创建评价实体
        MerchantReview review = new MerchantReview();
        BeanUtils.copyProperties(request, review);
        review.setUserId(userId);
        review.setStatus(0); // 待审核
        review.setLikeCount(0);
        review.setReplyCount(0);
        review.setViewCount(0);
        review.setRecommended(false);
        review.setPinned(false);
        review.setSyncedToEs(false);
        
        // 处理图片
        if (!CollectionUtils.isEmpty(request.getImages())) {
            review.setImages(JSON.toJSONString(request.getImages()));
        }
        
        // 处理标签
        if (!CollectionUtils.isEmpty(request.getTags())) {
            review.setTags(JSON.toJSONString(request.getTags()));
        }
        
        // 判断评价类型
        if (request.getVideoUrl() != null && !request.getVideoUrl().isEmpty()) {
            review.setReviewType(2); // 视频评价
        } else {
            review.setReviewType(1); // 图文评价
        }
        
        review.setCreateTime(LocalDateTime.now());
        review.setUpdateTime(LocalDateTime.now());
        
        // 保存评价
        reviewRepository.insert(review);
        
        // 更新商户统计
        updateMerchantStatistic(review);
        
        log.info("用户{}创建了商户{}的评价", userId, request.getMerchantId());
        
        return convertToDTO(review, userId);
    }

    @Override
    public MerchantReviewDTO getReviewDetail(Long reviewId, Long currentUserId) {
        MerchantReview review = reviewRepository.selectById(reviewId);
        if (review == null || review.getDeleted()) {
            throw new BusinessException("评价不存在");
        }
        
        // 增加浏览数（异步处理更佳）
        reviewRepository.incrementViewCount(reviewId);
        
        return convertToDTO(review, currentUserId);
    }

    @Override
    public PageResult<MerchantReviewDTO> getMerchantReviews(Long merchantId, String sortType, Integer page, Integer size) {
        ReviewSortType sort = ReviewSortType.fromCode(sortType);
        int offset = (page - 1) * size;
        
        // 根据排序类型查询
        List<MerchantReview> reviews = new ArrayList<>();
        
        switch (sort) {
            case LATEST:
                reviews = reviewRepository.selectByMerchantId(merchantId, 1, offset, size);
                break;
            case WITH_IMAGE:
                reviews = reviewRepository.selectWithMediaReviews(merchantId, false, offset, size);
                break;
            case WITH_VIDEO:
                reviews = reviewRepository.selectWithMediaReviews(merchantId, true, offset, size);
                break;
            case RECOMMENDED:
                reviews = reviewRepository.selectRecommendedReviews(merchantId, size);
                break;
            default:
                reviews = reviewRepository.selectByMerchantId(merchantId, 1, offset, size);
        }
        
        Integer total = reviewRepository.countByMerchantId(merchantId, 1);
        
        List<MerchantReviewDTO> dtoList = reviews.stream()
                .map(r -> convertToDTO(r, null))
                .collect(Collectors.toList());
        
        return PageResult.of(dtoList, total, page, size);
    }

    @Override
    public PageResult<MerchantReviewDTO> getUserReviews(Long userId, Integer page, Integer size) {
        int offset = (page - 1) * size;
        List<MerchantReview> reviews = reviewRepository.selectByUserId(userId, offset, size);
        
        // 统计用户总评价数（这里简化处理）
        Integer total = reviews.size();
        
        List<MerchantReviewDTO> dtoList = reviews.stream()
                .map(r -> convertToDTO(r, userId))
                .collect(Collectors.toList());
        
        return PageResult.of(dtoList, total, page, size);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void likeReview(Long reviewId, Long userId) {
        // 检查是否已点赞
        Integer count = likeRepository.checkUserLiked(reviewId, userId);
        if (count != null && count > 0) {
            throw new BusinessException("已经点赞过了");
        }
        
        // 创建点赞记录
        MerchantReviewLike like = new MerchantReviewLike();
        like.setReviewId(reviewId);
        like.setUserId(userId);
        like.setCreateTime(LocalDateTime.now());
        likeRepository.insert(like);
        
        // 增加点赞数
        reviewRepository.incrementLikeCount(reviewId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unlikeReview(Long reviewId, Long userId) {
        likeRepository.deleteByReviewAndUser(reviewId, userId);
        reviewRepository.decrementLikeCount(reviewId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteReview(Long reviewId, Long userId) {
        MerchantReview review = reviewRepository.selectById(reviewId);
        if (review == null || !review.getUserId().equals(userId)) {
            throw new BusinessException("无权删除该评价");
        }
        
        reviewRepository.deleteById(reviewId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditReview(Long reviewId, Integer status, String remark, Long auditBy) {
        MerchantReview review = new MerchantReview();
        review.setId(reviewId);
        review.setStatus(status);
        review.setAuditRemark(remark);
        review.setAuditBy(auditBy);
        review.setAuditTime(LocalDateTime.now());
        
        reviewRepository.updateById(review);
    }

    @Override
    public MerchantReviewStatisticDTO getMerchantStatistic(Long merchantId) {
        MerchantReviewStatistic stat = statisticRepository.selectByMerchantId(merchantId);
        if (stat == null) {
            // 返回空统计
            return new MerchantReviewStatisticDTO();
        }
        
        MerchantReviewStatisticDTO dto = new MerchantReviewStatisticDTO();
        BeanUtils.copyProperties(stat, dto);
        
        // 计算星级分布
        MerchantReviewStatisticDTO.StarDistributionDTO distribution = 
                new MerchantReviewStatisticDTO.StarDistributionDTO();
        int total = stat.getTotalCount() != null ? stat.getTotalCount() : 0;
        if (total > 0) {
            distribution.setFiveStarPercent(
                    calcPercent(stat.getFiveStarCount(), total));
            distribution.setFourStarPercent(
                    calcPercent(stat.getFourStarCount(), total));
            distribution.setThreeStarPercent(
                    calcPercent(stat.getThreeStarCount(), total));
            distribution.setTwoStarPercent(
                    calcPercent(stat.getTwoStarCount(), total));
            distribution.setOneStarPercent(
                    calcPercent(stat.getOneStarCount(), total));
        }
        dto.setStarDistribution(distribution);
        
        // 标签统计
        MerchantReviewStatisticDTO.TagStatisticDTO tags = 
                new MerchantReviewStatisticDTO.TagStatisticDTO();
        tags.setTasteGoodCount(stat.getTagTasteGoodCount());
        tags.setEnvGoodCount(stat.getTagEnvGoodCount());
        tags.setServiceGoodCount(stat.getTagServiceGoodCount());
        tags.setValueGoodCount(stat.getTagValueGoodCount());
        tags.setReturningCount(stat.getTagReturningCount());
        dto.setTagStatistic(tags);
        
        return dto;
    }

    @Override
    public PageResult<MerchantReviewDTO> getReviewsWithMedia(Long merchantId, Boolean hasVideo, 
                                                              Integer page, Integer size) {
        int offset = (page - 1) * size;
        List<MerchantReview> reviews = reviewRepository.selectWithMediaReviews(
                merchantId, hasVideo, offset, size);
        
        List<MerchantReviewDTO> dtoList = reviews.stream()
                .map(r -> convertToDTO(r, null))
                .collect(Collectors.toList());
        
        return PageResult.of(dtoList, reviews.size(), page, size);
    }

    @Override
    public List<MerchantReviewDTO> getRecommendedReviews(Long merchantId, Integer limit) {
        List<MerchantReview> reviews = reviewRepository.selectRecommendedReviews(merchantId, limit);
        return reviews.stream()
                .map(r -> convertToDTO(r, null))
                .collect(Collectors.toList());
    }

    @Override
    public void incrementViewCount(Long reviewId) {
        reviewRepository.incrementViewCount(reviewId);
    }

    /**
     * 更新商户统计
     */
    private void updateMerchantStatistic(MerchantReview review) {
        MerchantReviewStatistic stat = statisticRepository.selectByMerchantId(review.getMerchantId());
        
        if (stat == null) {
            // 创建新统计
            stat = new MerchantReviewStatistic();
            stat.setMerchantId(review.getMerchantId());
            stat.setPoiId(review.getPoiId());
            stat.setTotalCount(1);
            stat.setOverallRating(review.getOverallRating());
            stat.setTasteRating(review.getTasteRating());
            stat.setEnvironmentRating(review.getEnvironmentRating());
            stat.setServiceRating(review.getServiceRating());
            stat.setValueRating(review.getValueRating());
            stat.setDailyNewCount(1);
            stat.setWeeklyNewCount(1);
            stat.setMonthlyNewCount(1);
            stat.setLatestReviewTime(LocalDateTime.now());
            
            // 统计星级
            stat.updateStarCount(review.getOverallRating(), true);
            
            // 媒体统计
            if (review.getImages() != null && !review.getImages().isEmpty()) {
                stat.setWithImageCount(1);
            }
            if (review.getVideoUrl() != null && !review.getVideoUrl().isEmpty()) {
                stat.setWithVideoCount(1);
            }
            
            statisticRepository.insert(stat);
        } else {
            // 更新统计
            statisticRepository.incrementCount(review.getMerchantId());
            
            if (review.getImages() != null && !review.getImages().isEmpty()) {
                statisticRepository.incrementImageCount(review.getMerchantId());
            }
            if (review.getVideoUrl() != null && !review.getVideoUrl().isEmpty()) {
                statisticRepository.incrementVideoCount(review.getMerchantId());
            }
        }
    }

    /**
     * 转换为DTO
     */
    private MerchantReviewDTO convertToDTO(MerchantReview review, Long currentUserId) {
        MerchantReviewDTO dto = new MerchantReviewDTO();
        BeanUtils.copyProperties(review, dto);
        
        // 解析图片
        if (review.getImages() != null && !review.getImages().isEmpty()) {
            dto.setImages(JSON.parseArray(review.getImages(), String.class));
        }
        
        // 解析标签
        if (review.getTags() != null && !review.getTags().isEmpty()) {
            dto.setTags(JSON.parseArray(review.getTags(), String.class));
        }
        
        // 检查是否已点赞
        if (currentUserId != null) {
            Integer liked = likeRepository.checkUserLiked(review.getId(), currentUserId);
            dto.setHasLiked(liked != null && liked > 0);
        }
        
        // 星级文本
        dto.setRatingText(review.getRatingText());
        
        // 优质评价
        dto.setHighQuality(review.isHighQuality());
        
        return dto;
    }

    private Double calcPercent(Integer count, Integer total) {
        if (total == 0 || count == null) return 0.0;
        return new BigDecimal(count * 100)
                .divide(new BigDecimal(total), 1, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
