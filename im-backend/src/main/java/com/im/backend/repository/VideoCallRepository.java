package com.im.backend.repository;

import com.im.backend.entity.VideoCall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VideoCallRepository extends JpaRepository<VideoCall, Long> {

    Optional<VideoCall> findByCallId(String callId);

    List<VideoCall> findByCallerIdOrderByInitiatedAtDesc(Long callerId);

    List<VideoCall> findByCalleeIdOrderByInitiatedAtDesc(Long calleeId);

    @Query("SELECT vc FROM VideoCall vc WHERE (vc.callerId = :userId OR vc.calleeId = :userId) ORDER BY vc.initiatedAt DESC")
    List<VideoCall> findUserCalls(Long userId);

    @Query("SELECT vc FROM VideoCall vc WHERE vc.calleeId = :userId AND vc.status = 'RINGING'")
    List<VideoCall> findRingingCalls(Long userId);

    @Query("SELECT vc FROM VideoCall vc WHERE (vc.callerId = :userId OR vc.calleeId = :userId) AND vc.status IN ('INITIATED', 'RINGING')")
    Optional<VideoCall> findActiveCall(Long userId);

    long countByCallerIdAndStatus(Long callerId, String status);

    long countByCalleeIdAndStatus(Long calleeId, String status);

    @Query("SELECT SUM(vc.durationSeconds) FROM VideoCall vc WHERE vc.callerId = :userId OR vc.calleeId = :userId")
    Long getTotalCallDuration(Long userId);

    List<VideoCall> findByInitiatedAtBetween(LocalDateTime start, LocalDateTime end);
}
