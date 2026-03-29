package com.im.backend.repository;

import com.im.backend.entity.ChannelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChannelRepository extends JpaRepository<ChannelEntity, Long> {

    Optional<ChannelEntity> findByChannelId(String channelId);

    List<ChannelEntity> findByGroupIdAndStatusOrderBySortOrderAsc(String groupId, String status);

    List<ChannelEntity> findByGroupIdOrderBySortOrderAsc(String groupId);

    List<ChannelEntity> findByGroupIdAndChannelTypeOrderBySortOrderAsc(String groupId, String channelType);

    List<ChannelEntity> findByGroupIdAndIsPublicTrueAndStatusOrderBySortOrderAsc(String groupId, String status);

    @Query("SELECT c FROM ChannelEntity c WHERE c.groupId = :groupId AND c.status = :status AND c.requiredRole = :role ORDER BY c.sortOrder ASC")
    List<ChannelEntity> findAccessibleChannels(@Param("groupId") String groupId, @Param("status") String status, @Param("role") String role);

    @Query("SELECT c FROM ChannelEntity c WHERE c.parentChannelId IS NULL AND c.groupId = :groupId AND c.status = 'ACTIVE' ORDER BY c.sortOrder ASC")
    List<ChannelEntity> findRootChannels(@Param("groupId") String groupId);

    @Query("SELECT c FROM ChannelEntity c WHERE c.parentChannelId = :parentId AND c.status = 'ACTIVE' ORDER BY c.sortOrder ASC")
    List<ChannelEntity> findChildChannels(@Param("parentId") String parentId);

    long countByGroupIdAndStatus(String groupId, String status);

    @Query("SELECT COALESCE(MAX(c.sortOrder), 0) FROM ChannelEntity c WHERE c.groupId = :groupId AND c.parentChannelId IS NULL")
    Integer findMaxRootSortOrder(@Param("groupId") String groupId);

    @Query("SELECT COALESCE(MAX(c.sortOrder), 0) FROM ChannelEntity c WHERE c.parentChannelId = :parentId")
    Integer findMaxChildSortOrder(@Param("parentId") String parentId);

    boolean existsByGroupIdAndNameAndStatusNot(String groupId, String name, String status);

    boolean existsByChannelId(String channelId);

    List<ChannelEntity> findByCreatedByOrderByCreatedAtDesc(String userId);

    @Query("SELECT c.channelType, COUNT(c) FROM ChannelEntity c WHERE c.groupId = :groupId AND c.status = 'ACTIVE' GROUP BY c.channelType")
    List<Object[]> countByType(@Param("groupId") String groupId);
}
