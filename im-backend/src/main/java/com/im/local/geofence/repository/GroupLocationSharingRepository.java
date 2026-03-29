package com.im.local.geofence.repository;

import com.im.local.geofence.entity.GroupLocationSharing;
import com.im.local.geofence.enums.SharingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 群组位置共享Repository
 */
@Repository
public interface GroupLocationSharingRepository extends JpaRepository<GroupLocationSharing, Long> {
    
    Optional<GroupLocationSharing> findByGroupId(String groupId);
    
    List<GroupLocationSharing> findByCreatorIdAndStatus(Long creatorId, SharingStatus status);
    
    List<GroupLocationSharing> findByMemberIdsContainingAndStatus(Long memberId, SharingStatus status);
    
    List<GroupLocationSharing> findByExpireTimeBeforeAndStatus(LocalDateTime time, SharingStatus status);
}
