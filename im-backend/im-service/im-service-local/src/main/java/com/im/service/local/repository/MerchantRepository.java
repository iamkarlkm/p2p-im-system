package com.im.service.local.repository;

import com.im.service.local.entity.Merchant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, String> {

    Optional<Merchant> findByIdAndStatus(String id, String status);

    Page<Merchant> findByCategoryIdAndStatusOrderByRatingDesc(String categoryId, String status, Pageable pageable);

    Page<Merchant> findByStatusOrderByRatingDesc(String status, Pageable pageable);

    List<Merchant> findByNameContainingAndStatus(String keyword, String status);

    @Query(value = "SELECT * FROM im_local_merchant m WHERE m.status = 'ACTIVE' " +
           "AND (6371 * acos(cos(radians(:lat)) * cos(radians(m.latitude)) * " +
           "cos(radians(m.longitude) - radians(:lng)) + sin(radians(:lat)) * sin(radians(m.latitude)))) < :radius " +
           "ORDER BY rating DESC", nativeQuery = true)
    List<Merchant> findNearbyMerchants(@Param("lat") BigDecimal lat, @Param("lng") BigDecimal lng, @Param("radius") double radiusKm);
}
