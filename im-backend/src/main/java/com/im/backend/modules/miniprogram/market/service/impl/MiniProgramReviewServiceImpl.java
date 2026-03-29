package com.im.backend.modules.miniprogram.market.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.im.backend.common.api.PageResult;
import com.im.backend.modules.miniprogram.market.dto.ReviewResponse;
import com.im.backend.modules.miniprogram.market.dto.SubmitReviewRequest;
import com.im.backend.modules.miniprogram.market.entity.MiniProgramReview;
import com.im.backend.modules.miniprogram.market.mapper.MiniProgramReviewMapper;
import com.im.backend.modules.miniprogram.market.service.MiniProgramAppService;
import com.im.backend.modules.miniprogram.market.service.MiniProgramReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 小程序评分评论服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MiniProgramReviewServiceImpl extends ServiceImpl<MiniProgramReviewMapper, MiniProgramReview> implements MiniProgramReviewService {

    private final MiniProgramAppService appService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReviewResponse submitReview(Long userId, SubmitReviewRequest request) {
        // 检查用户是否已评论过
        MiniProgramReview existingReview = lambdaQuery()
            .eq(MiniProgramReview::getAppId, request.getAppId())
            .eq(MiniProgramReview::getUserId, userId)
            .one();
        
        MiniProgramReview review;
        if (existingReview != null) {
            // 更新评论
            existingReview.setRating(new BigDecimal(request.getRating()));
            existingReview.setContent(request.getContent());
            if (request.getImages() != null) {
                existingReview.setImages(com.alibaba.fastjson.JSON.toJSONString(request.getImages()));
            }
            existingReview.setStatus(0);
            updateById(existingReview);
            review = existingReview;
        } else {
            // 新建评论
            review = new MiniProgramReview();
            review.setAppId(request.getAppId());
            review.setUserId(userId);
            review.setRating(new BigDecimal(request.getRating()));
            review.setContent(request.getContent());
            if (request.getImages() != null) {
                review.setImages(com.alibaba.fastjson.JSON.toJSONString(request.getImages()));
            }
            review.setLikeCount(0);
            review.setIsTop(false);
            review.setStatus(0);
            save(review);
        }
        
        // 异步更新小程序评分
        updateAppRating(request.getAppId());
        
        return convertToResponse(review);
    }

    @Override
    public PageResult<ReviewResponse> getAppReviews(Long appId, Integer pageNum, Integer pageSize) {
        Page<MiniProgramReview> page = new Page<>(pageNum, pageSize);
        
        lambdaQuery()
            .eq(MiniProgramReview::getAppId, appId)
            .eq(MiniProgramReview::getStatus, 1)
            .orderByDesc(MiniProgramReview::getIsTop, MiniProgramReview::getCreateTime)
            .page(page);
        
        List<ReviewResponse> items = page.getRecords().stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        
        return new PageResult<>(page.getTotal(), items);
    }

    @Override
    public List<ReviewResponse> getUserReviews(Long userId) {
        List<MiniProgramReview> reviews = lambdaQuery()
            .eq(MiniProgramReview::getUserId, userId)
            .orderByDesc(MiniProgramReview::getCreateTime)
            .list();
        
        return reviews.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public boolean replyReview(Long reviewId, Long developerId, String reply) {
        return lambdaUpdate()
            .set(MiniProgramReview::getDeveloperReply, reply)
            .set(MiniProgramReview::getReplyTime, LocalDateTime.now())
            .eq(MiniProgramReview::getId, reviewId)
            .update();
    }

    @Override
    public boolean deleteReview(Long reviewId, Long userId) {
        return lambdaUpdate()
            .set(MiniProgramReview::getStatus, 2)
            .eq(MiniProgramReview::getId, reviewId)
            .eq(MiniProgramReview::getUserId, userId)
            .update();
    }

    @Override
    public boolean likeReview(Long reviewId) {
        return lambdaUpdate()
            .setSql("like_count = like_count + 1")
            .eq(MiniProgramReview::getId, reviewId)
            .update();
    }

    @Override
    public Double getAppAverageRating(Long appId) {
        return baseMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<MiniProgramReview>()
                .eq(MiniProgramReview::getAppId, appId)
                .eq(MiniProgramReview::getStatus, 1)
        ).stream()
            .mapToDouble(r -> r.getRating().doubleValue())
            .average()
            .orElse(0.0);
    }

    private void updateAppRating(Long appId) {
        Double avgRating = getAppAverageRating(appId);
        Integer count = lambdaQuery()
            .eq(MiniProgramReview::getAppId, appId)
            .eq(MiniProgramReview::getStatus, 1)
            .count();
        
        // 更新小程序评分
        com.im.backend.modules.miniprogram.market.entity.MiniProgramApp app = 
            new com.im.backend.modules.miniprogram.market.entity.MiniProgramApp();
        app.setId(appId);
        app.setRating(new BigDecimal(avgRating).setScale(2, RoundingMode.HALF_UP));
        app.setRatingCount(count);
        
        appService.updateById(app);
    }

    private ReviewResponse convertToResponse(MiniProgramReview review) {
        ReviewResponse response = new ReviewResponse();
        BeanUtils.copyProperties(review, response);
        
        if (review.getImages() != null) {
            response.setImages(com.alibaba.fastjson.JSON.parseArray(review.getImages(), String.class));
        }
        
        // TODO: 设置用户信息
        ReviewResponse.UserInfo userInfo = new ReviewResponse.UserInfo();
        userInfo.setId(review.getUserId());
        userInfo.setNickname("用户" + review.getUserId());
        response.setUser(userInfo);
        
        return response;
    }
}
