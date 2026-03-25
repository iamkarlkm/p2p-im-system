package com.im.backend.repository;

import com.im.backend.entity.BotSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BotSessionRepository extends JpaRepository<BotSessionEntity, Long> {

    Optional<BotSessionEntity> findBySessionId(String sessionId);

    List<BotSessionEntity> findByBotIdAndUserIdOrderByCreatedAtDesc(String botId, String userId);

    List<BotSessionEntity> findByBotIdAndStatusOrderByLastMessageAtDesc(String botId, String status);

    List<BotSessionEntity> findByUserIdAndStatusOrderByLastMessageAtDesc(String userId, String status);

    Optional<BotSessionEntity> findByConversationIdAndStatus(String conversationId, String status);

    long countByBotId(String botId);

    long countByBotIdAndStatus(String botId, String status);

    long countByUserId(String userId);

    @Query("SELECT bs FROM BotSessionEntity bs WHERE bs.status = 'ACTIVE' AND bs.lastMessageAt < :cutoff")
    List<BotSessionEntity> findStaleActiveSessions(@Param("cutoff") LocalDateTime cutoff);

    @Query("SELECT bs.userId, COUNT(bs) FROM BotSessionEntity bs WHERE bs.botId = :botId GROUP BY bs.userId ORDER BY COUNT(bs) DESC")
    List<Object[]> countUsersByBot(@Param("botId") String botId);

    @Query("SELECT COALESCE(SUM(bs.totalTokensUsed), 0) FROM BotSessionEntity bs WHERE bs.botId = :botId")
    Long sumTokensByBot(@Param("botId") String botId);

    @Query("SELECT COALESCE(AVG(bs.turnCount), 0) FROM BotSessionEntity bs WHERE bs.botId = :botId")
    Double avgTurnsByBot(@Param("botId") String botId);

    @Query("SELECT bs FROM BotSessionEntity bs WHERE bs.userId = :userId ORDER BY bs.lastMessageAt DESC")
    List<BotSessionEntity> findRecentByUser(@Param("userId") String userId);

    @Query("SELECT bs FROM BotSessionEntity bs WHERE bs.conversationId = :conversationId ORDER BY bs.createdAt ASC")
    List<BotSessionEntity> findByConversation(@Param("conversationId") String conversationId);

    void deleteByBotId(String botId);

    long countByCreatedAtAfter(LocalDateTime time);
}
