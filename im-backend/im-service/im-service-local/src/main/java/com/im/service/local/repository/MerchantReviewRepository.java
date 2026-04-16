package com.im.service.local.repository;

import com.im.service.local.entity.MerchantReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MerchantReviewRepository extends JpaRepository<MerchantReview, Long> {

    Page<MerchantReview> findByMerchantIdAndDeletedFalseOrderByCreatedAtDesc(String merchantId, Pageable pageable);

    List<MerchantReview> findByUserIdOrderByCreatedAtDesc(String userId);

    @Query("SELECT AVG(r.rating) FROM MerchantReview r WHERE r.merchantId = :merchantId AND r.deleted = false")
    Double calculateAverageRating(@Param("merchantId") String merchantId);

    @Query("SELECT COUNT(r) FROM MerchantReview r WHERE r.merchantId = :merchantId AND r.deleted = false")
    Long countByMerchant(@Param("merchantId") String merchantId);

    @Modifying
    @Query("UPDATE MerchantReview r SET r.likeCount = r.likeCount + 1 WHERE r.id = :id")
    int incrementLikeCount(@Param("id") Long id);
}
