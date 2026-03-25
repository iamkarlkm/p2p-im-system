package com.im.backend.repository;

import com.im.backend.entity.ChannelTopicEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChannelTopicRepository extends JpaRepository<ChannelTopicEntity, Long> {

    Optional<ChannelTopicEntity> findByTopicId(String topicId);

    List<ChannelTopicEntity> findByChannelIdAndParentTopicIdIsNullAndStatusOrderBySortOrderDesc(String channelId, String status, Pageable pageable);

    Page<ChannelTopicEntity> findByChannelIdAndParentTopicIdIsNullAndStatusOrderByCreatedAtDesc(String channelId, String status, Pageable pageable);

    List<ChannelTopicEntity> findByChannelIdAndStatusOrderByIsPinnedDescSortOrderAscCreatedAtDesc(String channelId, String status);

    List<ChannelTopicEntity> findByParentTopicIdOrderBySortOrderAsc(String parentTopicId);

    List<ChannelTopicEntity> findByRootTopicIdAndStatusOrderBySortOrderAsc(String rootTopicId, String status);

    @Query("SELECT t FROM ChannelTopicEntity t WHERE t.channelId = :channelId AND t.parentTopicId IS NULL AND t.status = 'OPEN' ORDER BY t.isPinned DESC, t.sortOrder ASC, t.createdAt DESC")
    List<ChannelTopicEntity> findPinnedThenSorted(@Param("channelId") String channelId);

    long countByChannelIdAndStatus(String channelId, String status);

    long countByChannelId(String channelId);

    long countByAuthorId(String authorId);

    @Query("SELECT COALESCE(MAX(t.sortOrder), 0) FROM ChannelTopicEntity t WHERE t.channelId = :channelId AND t.parentTopicId IS NULL")
    Integer findMaxRootSortOrder(@Param("channelId") String channelId);

    @Query("SELECT COALESCE(MAX(t.sortOrder), 0) FROM ChannelTopicEntity t WHERE t.parentTopicId = :parentId")
    Integer findMaxChildSortOrder(@Param("parentId") String parentId);

    @Query("SELECT t FROM ChannelTopicEntity t WHERE t.channelId = :channelId AND t.status = 'OPEN' ORDER BY t.lastReplyAt DESC")
    List<ChannelTopicEntity> findRecentlyActive(@Param("channelId") String channelId, Pageable pageable);

    @Query("SELECT t FROM ChannelTopicEntity t WHERE t.channelId = :channelId AND t.isPinned = true AND t.status = 'OPEN'")
    List<ChannelTopicEntity> findPinnedTopics(@Param("channelId") String channelId);

    List<ChannelTopicEntity> findByAuthorIdOrderByCreatedAtDesc(String authorId, Pageable pageable);

    @Query("SELECT t.tags, COUNT(t) FROM ChannelTopicEntity t WHERE t.channelId = :channelId AND t.tags IS NOT NULL GROUP BY t.tags")
    List<Object[]> countByTags(@Param("channelId") String channelId);

    boolean existsByTopicId(String topicId);
}
