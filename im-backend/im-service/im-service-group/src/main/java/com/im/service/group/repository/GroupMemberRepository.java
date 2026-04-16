package com.im.service.group.repository;

import com.im.service.group.entity.GroupMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    List<GroupMember> findByGroupIdOrderByRoleAscJoinedAtAsc(String groupId);

    Page<GroupMember> findByGroupIdOrderByRoleAscJoinedAtAsc(String groupId, Pageable pageable);

    Optional<GroupMember> findByGroupIdAndUserId(String groupId, String userId);

    List<GroupMember> findByUserIdOrderByJoinedAtDesc(String userId);

    boolean existsByGroupIdAndUserId(String groupId, String userId);

    long countByGroupId(String groupId);

    @Modifying
    @Query("UPDATE GroupMember gm SET gm.role = :role WHERE gm.groupId = :groupId AND gm.userId = :userId")
    int updateRole(@Param("groupId") String groupId, @Param("userId") String userId, @Param("role") String role);

    @Modifying
    @Query("UPDATE GroupMember gm SET gm.muted = :muted, gm.mutedUntil = :until WHERE gm.groupId = :groupId AND gm.userId = :userId")
    int updateMuteStatus(@Param("groupId") String groupId, @Param("userId") String userId, 
                         @Param("muted") boolean muted, @Param("until") LocalDateTime until);

    @Modifying
    @Query("DELETE FROM GroupMember gm WHERE gm.groupId = :groupId AND gm.userId = :userId")
    int deleteByGroupIdAndUserId(@Param("groupId") String groupId, @Param("userId") String userId);

    @Modifying
    @Query("DELETE FROM GroupMember gm WHERE gm.groupId = :groupId")
    int deleteByGroupId(@Param("groupId") String groupId);

    @Modifying
    @Query("UPDATE GroupMember gm SET gm.muted = false, gm.mutedUntil = null WHERE gm.groupId = :groupId AND gm.muted = true")
    int unmuteAll(@Param("groupId") String groupId);
}
