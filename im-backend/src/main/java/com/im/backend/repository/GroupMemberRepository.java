package com.im.backend.repository;

import com.im.backend.entity.GroupMember;
import com.im.backend.entity.GroupMemberRole;
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

/**
 * 群成员数据访问层
 * 功能#29: 群成员管理
 */
@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    
    Optional<GroupMember> findByGroupIdAndUserId(Long groupId, Long userId);
    
    List<GroupMember> findByGroupId(Long groupId);
    
    Page<GroupMember> findByGroupId(Long groupId, Pageable pageable);
    
    List<GroupMember> findByUserId(Long userId);
    
    @Query("SELECT gm FROM GroupMember gm WHERE gm.groupId = :groupId AND gm.role = :role")
    List<GroupMember> findByGroupIdAndRole(@Param("groupId") Long groupId, @Param("role") GroupMemberRole role);
    
    @Query("SELECT COUNT(gm) FROM GroupMember gm WHERE gm.groupId = :groupId")
    Long countByGroupId(@Param("groupId") Long groupId);
    
    @Modifying
    @Query("UPDATE GroupMember gm SET gm.role = :role WHERE gm.groupId = :groupId AND gm.userId = :userId")
    void updateRole(@Param("groupId") Long groupId, @Param("userId") Long userId, @Param("role") GroupMemberRole role);
    
    @Modifying
    @Query("UPDATE GroupMember gm SET gm.muteUntil = :muteUntil WHERE gm.groupId = :groupId AND gm.userId = :userId")
    void updateMuteStatus(@Param("groupId") Long groupId, @Param("userId") Long userId, @Param("muteUntil") LocalDateTime muteUntil);
    
    @Modifying
    @Query("UPDATE GroupMember gm SET gm.groupNickname = :nickname WHERE gm.groupId = :groupId AND gm.userId = :userId")
    void updateNickname(@Param("groupId") Long groupId, @Param("userId") Long userId, @Param("nickname") String nickname);
    
    @Modifying
    @Query("DELETE FROM GroupMember gm WHERE gm.groupId = :groupId AND gm.userId = :userId")
    void deleteByGroupIdAndUserId(@Param("groupId") Long groupId, @Param("userId") Long userId);
    
    @Query("SELECT CASE WHEN COUNT(gm) > 0 THEN true ELSE false END FROM GroupMember gm WHERE gm.groupId = :groupId AND gm.userId = :userId AND (gm.role = 'OWNER' OR gm.role = 'ADMIN')")
    boolean isAdminOrOwner(@Param("groupId") Long groupId, @Param("userId") Long userId);
    
    @Query("SELECT CASE WHEN COUNT(gm) > 0 THEN true ELSE false END FROM GroupMember gm WHERE gm.groupId = :groupId AND gm.userId = :userId AND gm.role = 'OWNER'")
    boolean isOwner(@Param("groupId") Long groupId, @Param("userId") Long userId);
    
    @Query("SELECT gm.userId FROM GroupMember gm WHERE gm.groupId = :groupId AND gm.muteUntil > :now")
    List<Long> findMutedMembers(@Param("groupId") Long groupId, @Param("now") LocalDateTime now);
}
