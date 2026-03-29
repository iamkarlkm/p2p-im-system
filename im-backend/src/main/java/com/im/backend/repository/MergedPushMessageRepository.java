package com.im.backend.repository;

import com.im.backend.entity.MergedPushMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface MergedPushMessageRepository extends JpaRepository<MergedPushMessage, Long> {

    List<MergedPushMessage> findByUserIdAndStatusOrderByCreatedAtDesc(String userId, String status);

    Optional<MergedPushMessage> findByUserIdAndConversationIdAndStatus(String userId, String conversationId, String status);

    List<MergedPushMessage> findByStatusAndScheduledAtBefore(String status, Instant time);

    @Query("SELECT COUNT(m) FROM MergedPushMessage m WHERE m.status = 'SENT'")
    Long countSent();

    @Query("SELECT COUNT(m) FROM MergedPushMessage m WHERE m.status = 'FAILED'")
    Long countFailed();

    @Query("SELECT SUM(m.messageCount) FROM MergedPushMessage m WHERE m.status = 'SENT'")
    Long sumMessageCount();

    @Query("SELECT AVG(m.messageCount) FROM MergedPushMessage m WHERE m.status = 'SENT'")
    Double avgMessageCount();

    @Query("SELECT m FROM MergedPushMessage m WHERE m.status = 'PENDING' AND m.scheduledAt <= :now")
    List<MergedPushMessage> findDueMergedMessages(@Param("now") Instant now);

    @Query("SELECT COUNT(m) FROM MergedPushMessage m WHERE m.userId = :userId")
    Long countByUserId(@Param("userId") String userId);
}
