package com.im.backend.webrtc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * WebRTC信令数据访问层
 */
@Repository
public interface WebRTCSignalingRepository extends JpaRepository<WebRTCSignalingEntity, String> {

    /**
     * 根据会话ID查找信令消息
     */
    List<WebRTCSignalingEntity> findBySessionIdOrderByCreatedAtAsc(String sessionId);

    /**
     * 查找待处理的信令消息
     */
    List<WebRTCSignalingEntity> findBySessionIdAndStatus(String sessionId, 
                                                          WebRTCSignalingEntity.SignalStatus status);

    /**
     * 查找发送给特定用户的信令
     */
    @Query("SELECT s FROM WebRTCSignalingEntity s WHERE s.sessionId = :sessionId AND (s.toUserId = :userId OR s.toUserId IS NULL) AND s.status = 'PENDING'")
    List<WebRTCSignalingEntity> findPendingSignalsForUser(@Param("sessionId") String sessionId,
                                                           @Param("userId") String userId);

    /**
     * 查找特定类型的信令
     */
    List<WebRTCSignalingEntity> findBySessionIdAndType(String sessionId,
                                                        WebRTCSignalingEntity.SignalType type);

    /**
     * 查找SDP信令
     */
    @Query("SELECT s FROM WebRTCSignalingEntity s WHERE s.sessionId = :sessionId AND s.type IN ('OFFER', 'ANSWER', 'PRANSWER') ORDER BY s.createdAt DESC")
    List<WebRTCSignalingEntity> findSdpSignals(@Param("sessionId") String sessionId);

    /**
     * 查找ICE候选
     */
    @Query("SELECT s FROM WebRTCSignalingEntity s WHERE s.sessionId = :sessionId AND s.type = 'ICE_CANDIDATE' AND s.fromUserId = :fromUserId AND s.status = 'PENDING'")
    List<WebRTCSignalingEntity> findPendingIceCandidates(@Param("sessionId") String sessionId,
                                                          @Param("fromUserId") String fromUserId);

    /**
     * 删除会话的所有信令
     */
    void deleteBySessionId(String sessionId);

    /**
     * 删除过期信令
     */
    void deleteByCreatedAtBefore(LocalDateTime before);

    /**
     * 删除已处理的旧信令
     */
    @Query("DELETE FROM WebRTCSignalingEntity s WHERE s.status IN ('DELIVERED', 'PROCESSED', 'EXPIRED') AND s.createdAt < :before")
    int deleteOldProcessedSignals(@Param("before") LocalDateTime before);

    /**
     * 查找需要重试的失败信令
     */
    @Query("SELECT s FROM WebRTCSignalingEntity s WHERE s.status = 'FAILED' AND s.retryCount < 3 AND s.createdAt > :since")
    List<WebRTCSignalingEntity> findRetryableSignals(@Param("since") LocalDateTime since);

    /**
     * 统计会话的信令数量
     */
    long countBySessionId(String sessionId);

    /**
     * 统计特定类型的信令数量
     */
    long countBySessionIdAndType(String sessionId, WebRTCSignalingEntity.SignalType type);

    /**
     * 查找最新的信令
     */
    @Query("SELECT s FROM WebRTCSignalingEntity s WHERE s.sessionId = :sessionId ORDER BY s.createdAt DESC")
    List<WebRTCSignalingEntity> findLatestSignals(@Param("sessionId") String sessionId,
                                                   org.springframework.data.domain.Pageable pageable);

    /**
     * 查找两个用户之间的信令
     */
    @Query("SELECT s FROM WebRTCSignalingEntity s WHERE s.sessionId = :sessionId AND ((s.fromUserId = :user1 AND s.toUserId = :user2) OR (s.fromUserId = :user2 AND s.toUserId = :user1)) ORDER BY s.createdAt")
    List<WebRTCSignalingEntity> findSignalsBetweenUsers(@Param("sessionId") String sessionId,
                                                         @Param("user1") String user1,
                                                         @Param("user2") String user2);

    /**
     * 查找会话中的所有ICE候选
     */
    @Query("SELECT s FROM WebRTCSignalingEntity s WHERE s.sessionId = :sessionId AND s.type = 'ICE_CANDIDATE' ORDER BY s.createdAt")
    List<WebRTCSignalingEntity> findAllIceCandidates(@Param("sessionId") String sessionId);

    /**
     * 标记过期信令
     */
    @Query("UPDATE WebRTCSignalingEntity s SET s.status = 'EXPIRED' WHERE s.status IN ('PENDING', 'FAILED') AND s.createdAt < :expiryTime")
    int markExpiredSignals(@Param("expiryTime") LocalDateTime expiryTime);

    /**
     * 查找特定连接的信令
     */
    List<WebRTCSignalingEntity> findByConnectionId(String connectionId);

    /**
     * 查找质量统计信令
     */
    @Query("SELECT s FROM WebRTCSignalingEntity s WHERE s.sessionId = :sessionId AND s.type = 'QUALITY_STATS' AND s.createdAt > :since ORDER BY s.createdAt DESC")
    List<WebRTCSignalingEntity> findRecentQualityStats(@Param("sessionId") String sessionId,
                                                        @Param("since") LocalDateTime since);
}
