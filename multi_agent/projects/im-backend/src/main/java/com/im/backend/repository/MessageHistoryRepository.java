package com.im.backend.repository;

import com.im.backend.entity.MessageHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MessageHistoryRepository extends JpaRepository<MessageHistory, Long> {

    Optional<MessageHistory> findByMessageId(Long messageId);

    @Query("SELECT m FROM MessageHistory m WHERE m.userId = :userId AND m.conversationId = :convId AND m.messageId > :lastId ORDER BY m.messageId ASC")
    Page<MessageHistory> findNewMessages(@Param("userId") Long userId, @Param("convId") Long conversationId, @Param("lastId") Long lastId, Pageable pageable);

    @Query("SELECT m FROM MessageHistory m WHERE m.userId = :userId AND m.conversationId = :convId AND m.sentAt > :since ORDER BY m.messageId ASC")
    Page<MessageHistory> findMessagesSince(@Param("userId") Long userId, @Param("convId") Long conversationId, @Param("since") LocalDateTime since, Pageable pageable);

    @Query("SELECT m FROM MessageHistory m WHERE m.userId = :userId AND m.sentAt > :since ORDER BY m.sentAt DESC")
    Page<MessageHistory> findAllMessagesSince(@Param("userId") Long userId, @Param("since") LocalDateTime since, Pageable pageable);

    @Query("SELECT m FROM MessageHistory m WHERE m.conversationId = :convId AND m.sentAt > :since AND m.deleted = false ORDER BY m.messageId ASC")
    List<MessageHistory> findActiveMessages(@Param("convId") Long conversationId, @Param("since") LocalDateTime since);

    long countByUserIdAndConversationId(Long userId, Long conversationId);
}
