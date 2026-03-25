package com.im.backend.repository;

import com.im.backend.entity.ScheduledMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduledMessageRepository extends JpaRepository<ScheduledMessage, Long> {

    Optional<ScheduledMessage> findByScheduleId(String scheduleId);

    List<ScheduledMessage> findBySenderIdOrderByScheduledTimeDesc(Long senderId);

    List<ScheduledMessage> findBySenderIdAndStatusOrderByScheduledTimeDesc(Long senderId, String status);

    @Query("SELECT sm FROM ScheduledMessage sm WHERE sm.status = 'PENDING' AND sm.scheduledTime <= :now ORDER BY sm.scheduledTime ASC")
    List<ScheduledMessage> findPendingToSend(LocalDateTime now);

    @Query("SELECT sm FROM ScheduledMessage sm WHERE sm.senderId = :senderId AND sm.status = 'PENDING' ORDER BY sm.scheduledTime DESC")
    List<ScheduledMessage> findPendingBySender(Long senderId);

    long countBySenderIdAndStatus(Long senderId, String status);

    void deleteByScheduleId(String scheduleId);
}
