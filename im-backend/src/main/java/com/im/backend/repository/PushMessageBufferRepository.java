package com.im.backend.repository;

import com.im.backend.entity.PushMessageBuffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface PushMessageBufferRepository extends JpaRepository<PushMessageBuffer, Long> {

    Optional<PushMessageBuffer> findByBufferKey(String bufferKey);

    List<PushMessageBuffer> findByIsMergedFalseAndExpiresAtBefore(Instant now);

    @Query("SELECT COUNT(b) FROM PushMessageBuffer b WHERE b.isMerged = false")
    Long countActiveBuffers();

    @Query("SELECT SUM(b.messageCount) FROM PushMessageBuffer b WHERE b.isMerged = false")
    Long sumBufferedMessageCount();

    @Modifying
    @Query("DELETE FROM PushMessageBuffer b WHERE b.expiresAt < :now OR b.isMerged = true")
    void deleteExpiredOrMerged(@Param("now") Instant now);

    Optional<PushMessageBuffer> findByUserIdAndConversationIdAndIsMergedFalse(
        String userId, String conversationId);
}
