package com.im.backend.repository;

import com.im.backend.entity.ChannelMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChannelMemberRepository extends JpaRepository<ChannelMemberEntity, Long> {

    Optional<ChannelMemberEntity> findByMemberId(String memberId);

    Optional<ChannelMemberEntity> findByChannelIdAndUserId(String channelId, String userId);

    List<ChannelMemberEntity> findByChannelIdAndStatusOrderByRoleAscJoinedAtAsc(String channelId, String status);

    List<ChannelMemberEntity> findByUserIdAndStatusOrderByJoinedAtDesc(String userId, String status);

    List<ChannelMemberEntity> findByChannelIdAndRoleIn(List<String> roles);

    long countByChannelIdAndStatus(String channelId, String status);

    boolean existsByChannelIdAndUserId(String channelId, String userId);

    @Query("SELECT cm.role FROM ChannelMemberEntity cm WHERE cm.channelId = :channelId AND cm.userId = :userId AND cm.status = 'ACTIVE'")
    Optional<String> findRoleByChannelAndUser(@Param("channelId") String channelId, @Param("userId") String userId);

    @Query("SELECT cm.role FROM ChannelMemberEntity cm WHERE cm.channelId = :channelId AND cm.userId = :userId")
    Optional<String> findAnyRoleByChannelAndUser(@Param("channelId") String channelId, @Param("userId") String userId);

    List<ChannelMemberEntity> findByUserIdAndChannelIdIn(List<String> channelIds);

    @Query("SELECT COUNT(cm) > 0 FROM ChannelMemberEntity cm WHERE cm.channelId = :channelId AND cm.userId = :userId AND cm.status = 'ACTIVE'")
    boolean isActiveMember(@Param("channelId") String channelId, @Param("userId") String userId);

    @Query("SELECT cm FROM ChannelMemberEntity cm WHERE cm.channelId = :channelId AND cm.role IN ('OWNER', 'ADMIN', 'MODERATOR') AND cm.status = 'ACTIVE'")
    List<ChannelMemberEntity> findModerators(@Param("channelId") String channelId);

    List<ChannelMemberEntity> findByChannelIdAndNotificationsEnabledTrueAndStatus(String channelId, String status);
}
