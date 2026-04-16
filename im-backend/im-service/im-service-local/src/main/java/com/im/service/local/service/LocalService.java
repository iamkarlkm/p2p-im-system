package com.im.service.local.service;

import com.im.service.local.entity.Merchant;
import com.im.service.local.entity.MerchantReview;
import com.im.service.local.repository.MerchantRepository;
import com.im.service.local.repository.MerchantReviewRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class LocalService {

    private final MerchantRepository merchantRepository;
    private final MerchantReviewRepository reviewRepository;

    public LocalService(MerchantRepository merchantRepository, MerchantReviewRepository reviewRepository) {
        this.merchantRepository = merchantRepository;
        this.reviewRepository = reviewRepository;
    }

    // ========== 商家管理 ==========

    public Merchant createMerchant(Merchant merchant) {
        return merchantRepository.save(merchant);
    }

    public Optional<Merchant> getMerchant(String merchantId) {
        return merchantRepository.findByIdAndStatus(merchantId, "ACTIVE");
    }

    public List<Merchant> getMerchantsByCategory(String categoryId, int page, int size) {
        return merchantRepository.findByCategoryIdAndStatusOrderByRatingDesc(categoryId, "ACTIVE", PageRequest.of(page, size))
            .getContent();
    }

    public List<Merchant> searchMerchants(String keyword) {
        return merchantRepository.findByNameContainingAndStatus(keyword, "ACTIVE");
    }

    public List<Merchant> getNearbyMerchants(BigDecimal lat, BigDecimal lng, double radiusKm) {
        return merchantRepository.findNearbyMerchants(lat, lng, radiusKm);
    }

    // ========== 评价管理 ==========

    @Transactional
    public MerchantReview createReview(String merchantId, String userId, Integer rating, String content, String images) {
        MerchantReview review = new MerchantReview();
        review.setMerchantId(merchantId);
        review.setUserId(userId);
        review.setRating(rating);
        review.setContent(content);
        review.setImages(images);

        review = reviewRepository.save(review);

        // 更新商家评分
        updateMerchantRating(merchantId);

        return review;
    }

    public List<MerchantReview> getMerchantReviews(String merchantId, int page, int size) {
        return reviewRepository.findByMerchantIdAndDeletedFalseOrderByCreatedAtDesc(merchantId, PageRequest.of(page, size))
            .getContent();
    }

    @Transactional
    public void updateMerchantRating(String merchantId) {
        Optional<Merchant> opt = merchantRepository.findById(merchantId);
        if (opt.isEmpty()) return;

        Merchant merchant = opt.get();
        Double avgRating = reviewRepository.calculateAverageRating(merchantId);
        Long count = reviewRepository.countByMerchant(merchantId);

        if (avgRating != null) {
            merchant.setRating(BigDecimal.valueOf(avgRating).setScale(1, RoundingMode.HALF_UP));
            merchant.setRatingCount(count.intValue());
            merchantRepository.save(merchant);
        }
    }

    @Transactional
    public boolean likeReview(Long reviewId) {
        return reviewRepository.incrementLikeCount(reviewId) > 0;
    }
}
