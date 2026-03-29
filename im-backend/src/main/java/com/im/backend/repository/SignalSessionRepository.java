package com.im.backend.repository;

import com.im.backend.entity.SignalSession;
import com.im.backend.entity.SignalSession.SignalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SignalSessionRepository extends JpaRepository<SignalSession, Long> {

    Optional<SignalSession> findByRoomId(String roomId);

    List<SignalSession> findByCallerIdAndStatusIn(Long callerId, List<SignalStatus> statuses);

    List<SignalSession> findByCalleeIdAndStatusIn(Long calleeId, List<SignalStatus> statuses);

    @Query("SELECT s FROM SignalSession s WHERE " +
           "(s.callerId = :userId OR s.calleeId = :userId) " +
           "AND s.status IN :statuses ORDER BY s.createdAt DESC")
    List<SignalSession> findActiveSessionsByUserId(
        @Param("userId") Long userId,
        @Param("statuses") List<SignalStatus> statuses
    );

    @Query("SELECT s FROM SignalSession s WHERE " +
           "s.roomId = :roomId AND s.status IN :statuses")
    Optional<SignalSession> findActiveByRoomId(
        @Param("roomId") String roomId,
        @Param("statuses") List<SignalStatus> statuses
    );

    @Query("SELECT COUNT(s) FROM SignalSession s WHERE " +
           "(s.callerId = :userId OR s.calleeId = :userId) " +
           "AND s.status IN :statuses")
    long countActiveSessions(
        @Param("userId") Long userId,
        @Param("statuses") List<SignalStatus> statuses
    );
}
