package com.im.backend.webrtc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * WebRTC参与者数据访问层
 */
@Repository
public interface WebRTCParticipantRepository extends JpaRepository<WebRTCParticipantEntity, String> {

    /**
     * 根据会话ID查找参与者列表
     */
    List<WebRTCParticipantEntity> findBySessionIdOrderByJoinedAtAsc(String sessionId);

    /**
     * 查找会话中的在线参与者
     */
    @Query("SELECT p FROM WebRTCParticipantEntity p WHERE p.sessionId = :sessionId AND p.status IN ('CONNECTED', 'MUTED', 'SCREEN_SHARING')")
    List<WebRTCParticipantEntity> findOnlineParticipants(@Param("sessionId") String sessionId);

    /**
     * 根据用户ID和会话ID查找参与者
     */
    Optional<WebRTCParticipantEntity> findBySessionIdAndUserId(String sessionId, String userId);

    /**
     * 根据连接ID查找参与者
     */
    Optional<WebRTCParticipantEntity> findByConnectionId(String connectionId);

    /**
     * 统计会话参与者数量
     */
    long countBySessionId(String sessionId);

    /**
     * 统计在线参与者数量
     */
    @Query("SELECT COUNT(p) FROM WebRTCParticipantEntity p WHERE p.sessionId = :sessionId AND p.status IN ('CONNECTED', 'MUTED', 'SCREEN_SHARING')")
    long countOnlineParticipants(@Param("sessionId") String sessionId);

    /**
     * 根据角色查找参与者
     */
    List<WebRTCParticipantEntity> findBySessionIdAndRole(String sessionId, 
                                                          WebRTCParticipantEntity.ParticipantRole role);

    /**
     * 查找主持人
     */
    Optional<WebRTCParticipantEntity> findBySessionIdAndIsHostTrue(String sessionId);

    /**
     * 查找举手的参与者
     */
    List<WebRTCParticipantEntity> findBySessionIdAndIsHandRaisedTrue(String sessionId);

    /**
     * 查找正在屏幕共享的参与者
     */
    List<WebRTCParticipantEntity> findBySessionIdAndIsScreenSharingTrue(String sessionId);

    /**
     * 根据状态查找参与者
     */
    List<WebRTCParticipantEntity> findByStatus(WebRTCParticipantEntity.ParticipantStatus status);

    /**
     * 查找用户的所有参与记录
     */
    List<WebRTCParticipantEntity> findByUserIdOrderByJoinedAtDesc(String userId);

    /**
     * 查找用户在特定时间段内的参与记录
     */
    @Query("SELECT p FROM WebRTCParticipantEntity p WHERE p.userId = :userId AND p.joinedAt BETWEEN :start AND :end")
    List<WebRTCParticipantEntity> findByUserIdAndTimeRange(@Param("userId") String userId,
                                                            @Param("start") LocalDateTime start,
                                                            @Param("end") LocalDateTime end);

    /**
     * 删除会话的所有参与者
     */
    void deleteBySessionId(String sessionId);

    /**
     * 统计用户的总参与时长
     */
    @Query("SELECT SUM(p.duration) FROM WebRTCParticipantEntity p WHERE p.userId = :userId AND p.duration IS NOT NULL")
    Long sumDurationByUserId(@Param("userId") String userId);

    /**
     * 获取用户的会议统计
     */
    @Query("SELECT COUNT(p), SUM(p.duration), AVG(p.duration) FROM WebRTCParticipantEntity p WHERE p.userId = :userId AND p.status = 'LEFT'")
    Object getUserMeetingStats(@Param("userId") String userId);

    /**
     * 查找长时间未活动的参与者
     */
    @Query("SELECT p FROM WebRTCParticipantEntity p WHERE p.status IN ('CONNECTED', 'MUTED', 'SCREEN_SHARING') AND p.lastActiveAt < :before")
    List<WebRTCParticipantEntity> findInactiveParticipants(@Param("before") LocalDateTime before);

    /**
     * 查找需要重连的参与者
     */
    @Query("SELECT p FROM WebRTCParticipantEntity p WHERE p.status = 'RECONNECTING' AND p.lastActiveAt < :before")
    List<WebRTCParticipantEntity> findStalledReconnecting(@Param("before") LocalDateTime before);

    /**
     * 查找用户在特定会话中的状态
     */
    @Query("SELECT p.status FROM WebRTCParticipantEntity p WHERE p.sessionId = :sessionId AND p.userId = :userId")
    Optional<WebRTCParticipantEntity.ParticipantStatus> findStatusBySessionAndUser(
        @Param("sessionId") String sessionId,
        @Param("userId") String userId);

    /**
     * 批量更新参与者状态
     */
    @Query("UPDATE WebRTCParticipantEntity p SET p.status = :newStatus WHERE p.sessionId = :sessionId AND p.status = :oldStatus")
    int updateStatusBySession(@Param("sessionId") String sessionId,
                               @Param("oldStatus") WebRTCParticipantEntity.ParticipantStatus oldStatus,
                               @Param("newStatus") WebRTCParticipantEntity.ParticipantStatus newStatus);

    /**
     * 检查用户是否在会话中
     */
    boolean existsBySessionIdAndUserIdAndStatusIn(
        String sessionId, 
        String userId,
        List<WebRTCParticipantEntity.ParticipantStatus> statuses);

    /**
     * 获取会话中的参与者ID列表
     */
    @Query("SELECT p.userId FROM WebRTCParticipantEntity p WHERE p.sessionId = :sessionId AND p.status IN ('CONNECTED', 'MUTED', 'SCREEN_SHARING')")
    List<String> findOnlineUserIdsBySession(@Param("sessionId") String sessionId);
}
