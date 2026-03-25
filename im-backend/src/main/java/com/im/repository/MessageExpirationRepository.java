package com.im.repository;

import com.im.entity.MessageExpirationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 消息过期策略仓储层
 */
@Repository
public interface MessageExpirationRepository extends JpaRepository<MessageExpirationEntity, Long> {

    Optional<MessageExpirationEntity> findByConversationId(String conversationId);

    List<MessageExpirationEntity> findBySenderIdAndReceiverId(String senderId, String receiverId);

    List<MessageExpirationEntity> findByConversationIdAndEnabledTrue(String conversationId);

    @Modifying
    @Query("DELETE FROM MessageExpirationEntity m WHERE m.conversationId = :conversationId")
    void deleteByConversationId(@Param("conversationId") String conversationId);

    @Query("SELECT m FROM MessageExpirationEntity m WHERE m.expireAt <= :now AND m.expirationType = 'SCHEDULE'")
    List<MessageExpirationEntity> findExpiredSchedules(@Param("now") LocalDateTime now);

    boolean existsByConversationIdAndEnabledTrue(String conversationId);
}
