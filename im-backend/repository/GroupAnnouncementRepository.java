package com.im.repository;

import com.im.entity.GroupAnnouncement;
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
 * 群公告数据访问层
 * 功能ID: #30
 * 功能名称: 群公告
 * 
 * @author developer-agent
 * @since 2026-03-30
 */
@Repository
public interface GroupAnnouncementRepository extends JpaRepository<GroupAnnouncement, Long> {

    /**
     * 查询群组的公告列表 (未删除)
     */
    @Query("SELECT ga FROM GroupAnnouncement ga WHERE ga.groupId = :groupId AND ga.deleted = false ORDER BY ga.pinned DESC, ga.createdAt DESC")
    List<GroupAnnouncement> findByGroupId(@Param("groupId") Long groupId);

    /**
     * 分页查询群组公告
     */
    @Query("SELECT ga FROM GroupAnnouncement ga WHERE ga.groupId = :groupId AND ga.deleted = false ORDER BY ga.pinned DESC, ga.createdAt DESC")
    Page<GroupAnnouncement> findByGroupIdPaged(@Param("groupId") Long groupId, Pageable pageable);

    /**
     * 获取最新公告
     */
    @Query("SELECT ga FROM GroupAnnouncement ga WHERE ga.groupId = :groupId AND ga.deleted = false ORDER BY ga.createdAt DESC")
    List<GroupAnnouncement> findLatestByGroupId(@Param("groupId") Long groupId, Pageable pageable);

    /**
     * 获取置顶公告
     */
    @Query("SELECT ga FROM GroupAnnouncement ga WHERE ga.groupId = :groupId AND ga.pinned = true AND ga.deleted = false ORDER BY ga.pinnedAt DESC")
    List<GroupAnnouncement> findPinnedByGroupId(@Param("groupId") Long groupId);

    /**
     * 统计群组公告数量
     */
    @Query("SELECT COUNT(ga) FROM GroupAnnouncement ga WHERE ga.groupId = :groupId AND ga.deleted = false")
    Long countByGroupId(@Param("groupId") Long groupId);

    /**
     * 软删除公告
     */
    @Modifying
    @Query("UPDATE GroupAnnouncement ga SET ga.deleted = true, ga.deletedAt = :deletedAt WHERE ga.id = :id")
    void softDelete(@Param("id") Long id, @Param("deletedAt") LocalDateTime deletedAt);

    /**
     * 批量删除群组的全部公告
     */
    @Modifying
    @Query("UPDATE GroupAnnouncement ga SET ga.deleted = true, ga.deletedAt = :deletedAt WHERE ga.groupId = :groupId")
    void softDeleteByGroupId(@Param("groupId") Long groupId, @Param("deletedAt") LocalDateTime deletedAt);

    /**
     * 更新已读人数
     */
    @Modifying
    @Query("UPDATE GroupAnnouncement ga SET ga.readCount = ga.readCount + 1 WHERE ga.id = :id")
    void incrementReadCount(@Param("id") Long id);

    /**
     * 设置确认状态
     */
    @Modifying
    @Query("UPDATE GroupAnnouncement ga SET ga.confirmed = true WHERE ga.id = :id")
    void markAsConfirmed(@Param("id") Long id);

    /**
     * 置顶/取消置顶公告
     */
    @Modifying
    @Query("UPDATE GroupAnnouncement ga SET ga.pinned = :pinned, ga.pinnedAt = :pinnedAt WHERE ga.id = :id")
    void updatePinnedStatus(@Param("id") Long id, @Param("pinned") Boolean pinned, @Param("pinnedAt") LocalDateTime pinnedAt);

    /**
     * 取消群组所有置顶公告 (用于切换置顶时)
     */
    @Modifying
    @Query("UPDATE GroupAnnouncement ga SET ga.pinned = false, ga.pinnedAt = null WHERE ga.groupId = :groupId AND ga.pinned = true")
    void clearAllPinned(@Param("groupId") Long groupId);

    /**
     * 搜索公告内容
     */
    @Query("SELECT ga FROM GroupAnnouncement ga WHERE ga.groupId = :groupId AND ga.deleted = false AND (ga.title LIKE %:keyword% OR ga.content LIKE %:keyword%) ORDER BY ga.createdAt DESC")
    List<GroupAnnouncement> searchByKeyword(@Param("groupId") Long groupId, @Param("keyword") String keyword);

    /**
     * 获取用户创建的所有公告
     */
    @Query("SELECT ga FROM GroupAnnouncement ga WHERE ga.creatorId = :creatorId AND ga.deleted = false ORDER BY ga.createdAt DESC")
    List<GroupAnnouncement> findByCreatorId(@Param("creatorId") Long creatorId);

    /**
     * 检查用户是否为公告创建者
     */
    @Query("SELECT CASE WHEN COUNT(ga) > 0 THEN true ELSE false END FROM GroupAnnouncement ga WHERE ga.id = :id AND ga.creatorId = :creatorId")
    boolean isCreator(@Param("id") Long id, @Param("creatorId") Long creatorId);

    /**
     * 获取指定时间范围内的公告
     */
    @Query("SELECT ga FROM GroupAnnouncement ga WHERE ga.groupId = :groupId AND ga.deleted = false AND ga.createdAt BETWEEN :startTime AND :endTime ORDER BY ga.createdAt DESC")
    List<GroupAnnouncement> findByTimeRange(@Param("groupId") Long groupId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 获取未确认的公告列表
     */
    @Query("SELECT ga FROM GroupAnnouncement ga WHERE ga.groupId = :groupId AND ga.confirmed = false AND ga.deleted = false ORDER BY ga.createdAt DESC")
    List<GroupAnnouncement> findUnconfirmedByGroupId(@Param("groupId") Long groupId);
}
