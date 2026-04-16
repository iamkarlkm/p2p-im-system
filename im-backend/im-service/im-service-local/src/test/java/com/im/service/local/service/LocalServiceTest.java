package com.im.service.local.service;

import com.im.service.local.entity.Merchant;
import com.im.service.local.entity.MerchantReview;
import com.im.service.local.repository.MerchantRepository;
import com.im.service.local.repository.MerchantReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * LocalService 单元测试 - 本地生活服务
 *
 * @author IM Team
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("本地生活服务单元测试")
class LocalServiceTest {

    @Mock
    private MerchantRepository merchantRepository;

    @Mock
    private MerchantReviewRepository reviewRepository;

    @InjectMocks
    private LocalService localService;

    private static final String TEST_MERCHANT_ID = "merchant_001";
    private static final String TEST_USER_ID = "user_001";
    private static final String TEST_CATEGORY_ID = "category_001";

    private Merchant testMerchant;

    @BeforeEach
    void setUp() {
        testMerchant = new Merchant();
        testMerchant.setId(TEST_MERCHANT_ID);
        testMerchant.setName("Test Merchant");
        testMerchant.setCategoryId(TEST_CATEGORY_ID);
        testMerchant.setStatus("ACTIVE");
        testMerchant.setRating(new BigDecimal("4.5"));
        testMerchant.setRatingCount(10);
    }

    @Test
    @DisplayName("创建商家 - 成功")
    void createMerchant_Success() {
        // Given
        when(merchantRepository.save(any(Merchant.class))).thenReturn(testMerchant);

        // When
        Merchant result = localService.createMerchant(testMerchant);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(TEST_MERCHANT_ID);
        assertThat(result.getName()).isEqualTo("Test Merchant");
        verify(merchantRepository, times(1)).save(testMerchant);
    }

    @Test
    @DisplayName("获取商家详情 - 成功")
    void getMerchant_Success() {
        // Given
        when(merchantRepository.findByIdAndStatus(TEST_MERCHANT_ID, "ACTIVE"))
            .thenReturn(Optional.of(testMerchant));

        // When
        Optional<Merchant> result = localService.getMerchant(TEST_MERCHANT_ID);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(TEST_MERCHANT_ID);
        assertThat(result.get().getName()).isEqualTo("Test Merchant");
    }

    @Test
    @DisplayName("获取商家详情 - 不存在")
    void getMerchant_NotFound() {
        // Given
        when(merchantRepository.findByIdAndStatus(TEST_MERCHANT_ID, "ACTIVE"))
            .thenReturn(Optional.empty());

        // When
        Optional<Merchant> result = localService.getMerchant(TEST_MERCHANT_ID);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("按分类获取商家列表 - 成功")
    void getMerchantsByCategory_Success() {
        // Given
        Merchant merchant2 = new Merchant();
        merchant2.setId("merchant_002");
        merchant2.setName("Merchant 2");
        merchant2.setCategoryId(TEST_CATEGORY_ID);

        Page<Merchant> page = new PageImpl<>(Arrays.asList(testMerchant, merchant2));
        when(merchantRepository.findByCategoryIdAndStatusOrderByRatingDesc(
            eq(TEST_CATEGORY_ID), eq("ACTIVE"), any(PageRequest.class)))
            .thenReturn(page);

        // When
        List<Merchant> result = localService.getMerchantsByCategory(TEST_CATEGORY_ID, 0, 10);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(TEST_MERCHANT_ID);
        assertThat(result.get(1).getId()).isEqualTo("merchant_002");
    }

    @Test
    @DisplayName("搜索商家 - 成功")
    void searchMerchants_Success() {
        // Given
        String keyword = "Test";
        when(merchantRepository.findByNameContainingAndStatus(keyword, "ACTIVE"))
            .thenReturn(Arrays.asList(testMerchant));

        // When
        List<Merchant> result = localService.searchMerchants(keyword);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).contains("Test");
    }

    @Test
    @DisplayName("搜索商家 - 无结果")
    void searchMerchants_NoResults() {
        // Given
        String keyword = "NonExistent";
        when(merchantRepository.findByNameContainingAndStatus(keyword, "ACTIVE"))
            .thenReturn(Collections.emptyList());

        // When
        List<Merchant> result = localService.searchMerchants(keyword);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("获取附近商家 - 成功")
    void getNearbyMerchants_Success() {
        // Given
        BigDecimal lat = new BigDecimal("39.9042");
        BigDecimal lng = new BigDecimal("116.4074");
        double radiusKm = 5.0;

        when(merchantRepository.findNearbyMerchants(lat, lng, radiusKm))
            .thenReturn(Arrays.asList(testMerchant));

        // When
        List<Merchant> result = localService.getNearbyMerchants(lat, lng, radiusKm);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(TEST_MERCHANT_ID);
    }

    @Test
    @DisplayName("创建评价 - 成功")
    void createReview_Success() {
        // Given
        Integer rating = 5;
        String content = "Great service!";
        String images = "image1.jpg,image2.jpg";

        MerchantReview review = new MerchantReview();
        review.setId(1L);
        review.setMerchantId(TEST_MERCHANT_ID);
        review.setUserId(TEST_USER_ID);
        review.setRating(rating);
        review.setContent(content);
        review.setImages(images);

        when(reviewRepository.save(any(MerchantReview.class))).thenReturn(review);
        when(merchantRepository.findById(TEST_MERCHANT_ID)).thenReturn(Optional.of(testMerchant));
        when(reviewRepository.calculateAverageRating(TEST_MERCHANT_ID)).thenReturn(4.8);
        when(reviewRepository.countByMerchant(TEST_MERCHANT_ID)).thenReturn(11L);
        when(merchantRepository.save(any(Merchant.class))).thenReturn(testMerchant);

        // When
        MerchantReview result = localService.createReview(TEST_MERCHANT_ID, TEST_USER_ID, rating, content, images);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getMerchantId()).isEqualTo(TEST_MERCHANT_ID);
        assertThat(result.getUserId()).isEqualTo(TEST_USER_ID);
        assertThat(result.getRating()).isEqualTo(rating);
        assertThat(result.getContent()).isEqualTo(content);
        verify(reviewRepository, times(1)).save(any(MerchantReview.class));
    }

    @Test
    @DisplayName("获取商家评价列表 - 成功")
    void getMerchantReviews_Success() {
        // Given
        MerchantReview review1 = new MerchantReview();
        review1.setId(1L);
        review1.setMerchantId(TEST_MERCHANT_ID);
        review1.setRating(5);

        MerchantReview review2 = new MerchantReview();
        review2.setId(2L);
        review2.setMerchantId(TEST_MERCHANT_ID);
        review2.setRating(4);

        Page<MerchantReview> page = new PageImpl<>(Arrays.asList(review1, review2));
        when(reviewRepository.findByMerchantIdAndDeletedFalseOrderByCreatedAtDesc(
            eq(TEST_MERCHANT_ID), any(PageRequest.class)))
            .thenReturn(page);

        // When
        List<MerchantReview> result = localService.getMerchantReviews(TEST_MERCHANT_ID, 0, 10);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getRating()).isEqualTo(5);
        assertThat(result.get(1).getRating()).isEqualTo(4);
    }

    @Test
    @DisplayName("更新商家评分 - 成功")
    void updateMerchantRating_Success() {
        // Given
        when(merchantRepository.findById(TEST_MERCHANT_ID)).thenReturn(Optional.of(testMerchant));
        when(reviewRepository.calculateAverageRating(TEST_MERCHANT_ID)).thenReturn(4.7);
        when(reviewRepository.countByMerchant(TEST_MERCHANT_ID)).thenReturn(15L);
        when(merchantRepository.save(any(Merchant.class))).thenReturn(testMerchant);

        // When
        localService.updateMerchantRating(TEST_MERCHANT_ID);

        // Then
        assertThat(testMerchant.getRating()).isEqualTo(new BigDecimal("4.7"));
        assertThat(testMerchant.getRatingCount()).isEqualTo(15);
        verify(merchantRepository, times(1)).save(testMerchant);
    }

    @Test
    @DisplayName("更新商家评分 - 商家不存在")
    void updateMerchantRating_MerchantNotFound() {
        // Given
        when(merchantRepository.findById(TEST_MERCHANT_ID)).thenReturn(Optional.empty());

        // When
        localService.updateMerchantRating(TEST_MERCHANT_ID);

        // Then
        verify(reviewRepository, never()).calculateAverageRating(anyString());
        verify(merchantRepository, never()).save(any(Merchant.class));
    }

    @Test
    @DisplayName("更新商家评分 - 无评价数据")
    void updateMerchantRating_NoReviews() {
        // Given
        when(merchantRepository.findById(TEST_MERCHANT_ID)).thenReturn(Optional.of(testMerchant));
        when(reviewRepository.calculateAverageRating(TEST_MERCHANT_ID)).thenReturn(null);

        // When
        localService.updateMerchantRating(TEST_MERCHANT_ID);

        // Then
        verify(merchantRepository, never()).save(any(Merchant.class));
    }

    @Test
    @DisplayName("点赞评价 - 成功")
    void likeReview_Success() {
        // Given
        Long reviewId = 1L;
        when(reviewRepository.incrementLikeCount(reviewId)).thenReturn(1);

        // When
        boolean result = localService.likeReview(reviewId);

        // Then
        assertThat(result).isTrue();
        verify(reviewRepository, times(1)).incrementLikeCount(reviewId);
    }

    @Test
    @DisplayName("点赞评价 - 评价不存在")
    void likeReview_NotFound() {
        // Given
        Long reviewId = 999L;
        when(reviewRepository.incrementLikeCount(reviewId)).thenReturn(0);

        // When
        boolean result = localService.likeReview(reviewId);

        // Then
        assertThat(result).isFalse();
        verify(reviewRepository, times(1)).incrementLikeCount(reviewId);
    }
}
