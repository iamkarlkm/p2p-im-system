package com.im.backend.repository;

import com.im.backend.entity.SyncCheckpoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SyncCheckpointRepository extends JpaRepository<SyncCheckpoint, Long> {

    Optional<SyncCheckpoint> findByUserIdAndDeviceIdAndConversationId(Long userId, String deviceId, Long conversationId);

    List<SyncCheckpoint> findByUserIdAndDeviceId(Long userId, String deviceId);

    List<SyncCheckpoint> findByUserId(Long userId);

    void deleteByUserIdAndDeviceId(Long userId, String deviceId);
}
