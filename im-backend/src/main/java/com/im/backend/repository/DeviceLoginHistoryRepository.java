package com.im.backend.repository;

import com.im.backend.entity.DeviceLoginHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DeviceLoginHistoryRepository extends JpaRepository<DeviceLoginHistory, Long> {

    List<DeviceLoginHistory> findByUserIdOrderByLoginTimeDesc(String userId, Pageable pageable);

    List<DeviceLoginHistory> findByUserIdAndDeviceIdOrderByLoginTimeDesc(String userId, Long deviceId, Pageable pageable);

    @Query("SELECT COUNT(h) FROM DeviceLoginHistory h WHERE h.userId = :userId")
    Long countByUserId(@Param("userId") String userId);

    @Query("SELECT COUNT(h) FROM DeviceLoginHistory h WHERE h.userId = :userId AND h.loginStatus = 'SUCCESS'")
    Long countSuccessfulByUserId(@Param("userId") String userId);

    @Query("SELECT COUNT(h) FROM DeviceLoginHistory h WHERE h.userId = :userId AND h.loginStatus = 'FAILED'")
    Long countFailedByUserId(@Param("userId") String userId);

    @Query("SELECT h FROM DeviceLoginHistory h WHERE h.userId = :userId AND h.action = 'LOGOUT' ORDER BY h.loginTime DESC")
    List<DeviceLoginHistory> findLogoutHistory(@Param("userId") String userId, Pageable pageable);
}
