package com.im.server.repository;

import com.im.server.entity.SelfDestructMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 阅后即焚消息仓库
 */
@Repository
public interface SelfDestructMessageRepository extends JpaRepository<SelfDestructMessage, Long> {

    Optional<SelfDestructMessage> findByMessageId(Long messageId);

    List<SelfDestructMessage> findBySenderId(Long senderId);

    List<SelfDestructMessage> findByReceiverId(Long receiverId);

    List<SelfDestructMessage> findByStatus(String status);

    @Modifying
    @Query("UPDATE SelfDestructMessage s SET s.status = :status WHERE s.messageId = :messageId")
    void updateStatus(@Param("messageId") Long messageId, @Param("status") String status);

    @Modifying
    @Query("UPDATE SelfDestructMessage s SET s.readAt = CURRENT_TIMESTAMP WHERE s.messageId = :messageId")
    void updateReadAt(@Param("messageId") Long messageId);

    @Modifying
    @Query("UPDATE SelfDestructMessage s SET s.destroyedAt = CURRENT_TIMESTAMP, s.destroyReason = :reason WHERE s.messageId = :messageId")
    void updateDestroyed(@Param("messageId") Long messageId, @Param("reason") String reason);

    List<SelfDestructMessage> findByReceiverIdAndStatus(Long receiverId, String status);

    List<SelfDestructMessage> findBySenderIdAndStatus(Long senderId, String status);
}
