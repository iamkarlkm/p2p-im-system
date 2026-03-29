package com.im.server.call;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CallRecordRepository extends JpaRepository<CallRecord, String> {

    @Query("SELECT c FROM CallRecord c WHERE c.callerId = :userId OR c.calleeId = :userId ORDER BY c.startTime DESC")
    List<CallRecord> findByUserIdAll(@Param("userId") String userId);

    @Query("SELECT c FROM CallRecord c WHERE (c.callerId = :userId OR c.calleeId = :userId) ORDER BY c.startTime DESC")
    List<CallRecord> findByUserId(@Param("userId") String userId, int offset, int limit);

    List<CallRecord> findByCalleeIdAndStatus(String calleeId, CallRecord.CallStatus status);

    List<CallRecord> findByConversationIdOrderByStartTimeDesc(String conversationId);
}
