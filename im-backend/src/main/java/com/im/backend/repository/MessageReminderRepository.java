package com.im.backend.repository;

import com.im.backend.entity.MessageReminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageReminderRepository extends JpaRepository<MessageReminder, Long> {

    List<MessageReminder> findByUserIdOrderByReminderTimeDesc(Long userId);

    List<MessageReminder> findByUserIdAndIsTriggeredFalseAndIsDismissedFalse(Long userId);

    @Query("SELECT r FROM MessageReminder r WHERE r.isTriggered = false AND r.reminderTime <= :time")
    List<MessageReminder> findRemindersDueBefore(LocalDateTime time);

    void deleteByMessageId(Long messageId);

    int countByUserIdAndIsTriggeredFalse(Long userId);

    boolean existsByMessageIdAndUserId(Long messageId, Long userId);
}
