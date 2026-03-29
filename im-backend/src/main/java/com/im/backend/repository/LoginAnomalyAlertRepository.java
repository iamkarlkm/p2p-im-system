package com.im.backend.repository;

import com.im.backend.entity.LoginAnomalyAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoginAnomalyAlertRepository extends JpaRepository<LoginAnomalyAlert, Long> {
    List<LoginAnomalyAlert> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<LoginAnomalyAlert> findByUserIdAndIsDismissedFalseOrderByCreatedAtDesc(Long userId);
    Optional<LoginAnomalyAlert> findByUserIdAndDeviceIdAndCreatedAtAfter(Long userId, String deviceId, java.time.LocalDateTime after);
    List<LoginAnomalyAlert> findByUserIdAndIsConfirmedFalseOrderByCreatedAtDesc(Long userId);
}
