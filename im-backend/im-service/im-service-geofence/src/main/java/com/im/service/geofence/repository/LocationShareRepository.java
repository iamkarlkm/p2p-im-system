package com.im.service.geofence.repository;

import com.im.service.geofence.entity.LocationShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 位置分享数据访问层
 */
@Repository
public interface LocationShareRepository extends JpaRepository<LocationShare, Long> {

    LocationShare findByShareId(String shareId);

    List<LocationShare> findByUserIdAndIsActiveTrue(String userId);

    List<LocationShare> findByRecipientIdAndIsActiveTrue(String recipientId);

    @Query("SELECT ls FROM LocationShare ls WHERE ls.isActive = true AND ls.expiresAt < CURRENT_TIMESTAMP")
    List<LocationShare> findExpiredShares();

    @Modifying
    @Query("UPDATE LocationShare ls SET ls.isActive = false WHERE ls.userId = :userId")
    int deactivateAllByUserId(@Param("userId") String userId);

    @Modifying
    @Query("UPDATE LocationShare ls SET ls.isActive = false WHERE ls.shareId = :shareId")
    int deactivateByShareId(@Param("shareId") String shareId);

    @Query("SELECT COUNT(ls) FROM LocationShare ls WHERE ls.userId = :userId AND ls.isActive = true")
    long countActiveByUserId(@Param("userId") String userId);
}
