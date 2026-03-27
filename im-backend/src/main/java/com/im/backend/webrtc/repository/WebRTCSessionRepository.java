package com.im.backend.webrtc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * WebRTC会话数据访问层
 */
@Repository
public interface WebRTCSessionRepository extends JpaRepository<WebRTCSessionEntity, String> {

    /**
     * 根据房间ID查找会话
     */
    Optional<WebRTCSessionEntity> findByRoomId(String roomId);

    /**
     * 根据主机ID查找会话列表
     */
    List<WebRTCSessionEntity> findByHostIdOrderByCreatedAtDesc(String hostId);

    /**
     * 根据状态查找会话
     */
    List<WebRTCSessionEntity> findByStatus(WebRTCSessionEntity.SessionStatus status);

    /**
     * 查找活跃的会话
     */
    @Query("SELECT s FROM WebRTCSessionEntity s WHERE s.status IN ('PENDING', 'CONNECTING', 'ACTIVE')")
    List<WebRTCSessionEntity> findActiveSessions();

    /**
     * 查找特定类型的会话
     */
    List<WebRTCSessionEntity> findBySessionType(WebRTCSessionEntity.SessionType sessionType);

    /**
     * 查找公开的会话
     */
    List<WebRTCSessionEntity> findByIsPublicTrueAndStatus(WebRTCSessionEntity.SessionStatus status);

    /**
     * 根据SFU节点ID查找会话
     */
    List<WebRTCSessionEntity> findBySfuNodeId(String sfuNodeId);

    /**
     * 统计特定状态的会话数量
     */
    long countByStatus(WebRTCSessionEntity.SessionStatus status);

    /**
     * 统计时间段内创建的会话
     */
    @Query("SELECT COUNT(s) FROM WebRTCSessionEntity s WHERE s.createdAt BETWEEN :start AND :end")
    long countByCreatedAtBetween(@Param("start") LocalDateTime start, 
                                  @Param("end") LocalDateTime end);

    /**
     * 查找需要清理的过期会话
     */
    @Query("SELECT s FROM WebRTCSessionEntity s WHERE s.status = 'ENDED' AND s.endedAt < :before")
    List<WebRTCSessionEntity> findEndedSessionsBefore(@Param("before") LocalDateTime before);

    /**
     * 查找长时间无活动的会话
     */
    @Query("SELECT s FROM WebRTCSessionEntity s WHERE s.status IN ('PENDING', 'CONNECTING', 'ACTIVE') AND s.updatedAt < :before")
    List<WebRTCSessionEntity> findInactiveSessions(@Param("before") LocalDateTime before);

    /**
     * 查找正在录制的会话
     */
    List<WebRTCSessionEntity> findByIsRecordingTrue();

    /**
     * 根据预定会议ID查找会话
     */
    Optional<WebRTCSessionEntity> findByScheduledMeetingId(String scheduledMeetingId);

    /**
     * 搜索会话
     */
    @Query("SELECT s FROM WebRTCSessionEntity s WHERE s.roomName LIKE %:keyword% OR s.description LIKE %:keyword%")
    List<WebRTCSessionEntity> searchByKeyword(@Param("keyword") String keyword);

    /**
     * 获取会话统计信息
     */
    @Query("SELECT s.sessionType, COUNT(s), AVG(s.duration) FROM WebRTCSessionEntity s GROUP BY s.sessionType")
    List<Object[]> getSessionStatisticsByType();

    /**
     * 获取每日会话数量统计
     */
    @Query("SELECT DATE(s.createdAt), COUNT(s) FROM WebRTCSessionEntity s WHERE s.createdAt BETWEEN :start AND :end GROUP BY DATE(s.createdAt)")
    List<Object[]> getDailySessionCount(@Param("start") LocalDateTime start, 
                                         @Param("end") LocalDateTime end);

    /**
     * 查找用户的活跃会话
     */
    @Query("SELECT s FROM WebRTCSessionEntity s WHERE s.hostId = :userId AND s.status IN ('PENDING', 'CONNECTING', 'ACTIVE', 'PAUSED')")
    List<WebRTCSessionEntity> findUserActiveSessions(@Param("userId") String userId);

    /**
     * 检查房间ID是否已存在
     */
    boolean existsByRoomId(String roomId);

    /**
     * 删除指定时间之前的已结束会话
     */
    void deleteByStatusAndEndedAtBefore(WebRTCSessionEntity.SessionStatus status, 
                                        LocalDateTime before);
}
