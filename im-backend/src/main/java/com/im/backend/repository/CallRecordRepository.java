package com.im.backend.repository;

import com.im.backend.entity.CallRecordEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CallRecordRepository extends JpaRepository<CallRecordEntity, Long> {

    Optional<CallRecordEntity> findByCallId(String callId);

    /** 查询用户所有通话记录(分页) */
    Page<CallRecordEntity> findByCallerIdOrCalleeIdOrderByStartTimeDesc(
            String callerId, String calleeId, Pageable pageable);

    /** 查询用户在某会话的通话记录 */
    List<CallRecordEntity> findByConversationIdOrderByStartTimeDesc(String conversationId);

    /** 查询某用户的未接来电 */
    @Query("SELECT c FROM CallRecordEntity c WHERE (c.calleeId = :userId OR c.callerId = :userId) AND c.status = 'MISSED' ORDER BY c.startTime DESC")
    List<CallRecordEntity> findMissedByUserId(@Param("userId") String userId);

    /** 统计某用户某月的通话时长 */
    @Query("SELECT COALESCE(SUM(c.duration), 0) FROM CallRecordEntity c WHERE (c.callerId = :userId OR c.calleeId = :userId) AND c.status = 'ENDED' AND c.startTime >= :monthStart")
    Long sumDurationThisMonth(@Param("userId") String userId, @Param("monthStart") java.time.LocalDateTime monthStart);
}
