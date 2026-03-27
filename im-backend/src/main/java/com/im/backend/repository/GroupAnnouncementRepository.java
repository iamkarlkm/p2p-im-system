package com.im.backend.repository;

import com.im.backend.model.GroupAnnouncement;
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
 */
@Repository
public interface GroupAnnouncementRepository extends JpaRepository<GroupAnnouncement, Long> {

    /**
     * 查询群组的所有公告（按创建时间倒序）
     */
    @Query("SELECT a FROM GroupAnnouncement a WHERE a.groupId = :groupId ORDER BY a.createdAt DESC")
    List<GroupAnnouncement> findByGroupId(@Param("groupId") Long groupId);

    /**
     * 分页查询群组公告
     */
    @Query("SELECT a FROM GroupAnnouncement a WHERE a.groupId = :groupId ORDER BY a.createdAt DESC")
    Page<GroupAnnouncement> findByGroupIdPageable(@Param("groupId") Long groupId, Pageable pageable);

    /**
     * 查询群组生效中的公告
     */
    @Query("SELECT a FROM GroupAnnouncement a WHERE a.groupId = :groupId AND a.status = 'ACTIVE' ORDER BY a.createdAt DESC")
    List<GroupAnnouncement> findActiveByGroupId(@Param("groupId") Long groupId);

    /**
     * 查询群组的置顶公告
     */
    @Query("SELECT a FROM GroupAnnouncement a WHERE a.groupId = :groupId AND a.announcementType = 'PINNED' AND a.status = 'ACTIVE' ORDER BY a.createdAt DESC")
    List<GroupAnnouncement> findPinnedByGroupId(@Param("groupId") Long groupId);

    /**
     * 根据ID和群组ID查询公告
     */
    @Query("SELECT a FROM GroupAnnouncement a WHERE a.id = :id AND a.groupId = :groupId")
    Optional<GroupAnnouncement> findByIdAndGroupId(@Param("id") Long id, @Param("groupId") Long groupId);

    /**
     * 统计群组公告数量
     */
    @Query("SELECT COUNT(a) FROM GroupAnnouncement a WHERE a.groupId = :groupId")
    Long countByGroupId(@Param("groupId") Long groupId);

    /**
     * 统计群组生效中的公告数量
     */
    @Query("SELECT COUNT(a) FROM GroupAnnouncement a WHERE a.groupId = :groupId AND a.status = 'ACTIVE'")
    Long countActiveByGroupId(@Param("groupId") Long groupId);

    /**
     * 撤回公告
     */
    @Modifying
    @Query("UPDATE GroupAnnouncement a SET a.status = 'WITHDRAWN', a.withdrawnAt = :withdrawnAt, a.withdrawnBy = :withdrawnBy, a.withdrawReason = :reason WHERE a.id = :id")
    int withdrawAnnouncement(@Param("id") Long id, @Param("withdrawnAt") LocalDateTime withdrawnAt, 
                            @Param("withdrawnBy") Long withdrawnBy, @Param("reason") String reason);

    /**
     * 批量过期公告
     */
    @Modifying
    @Query("UPDATE GroupAnnouncement a SET a.status = 'EXPIRED' WHERE a.effectiveEnd < :now AND a.status = 'ACTIVE'")
    int expireAnnouncements(@Param("now") LocalDateTime now);

    /**
     * 更新阅读数
     */
    @Modifying
    @Query("UPDATE GroupAnnouncement a SET a.readCount = a.readCount + 1 WHERE a.id = :id")
    int incrementReadCount(@Param("id") Long id);

    /**
     * 更新确认数
     */
    @Modifying
    @Query("UPDATE GroupAnnouncement a SET a.confirmCount = a.confirmCount + 1 WHERE a.id = :id")
    int incrementConfirmCount(@Param("id") Long id);

    /**
     * 查询用户发布的所有公告
     */
    @Query("SELECT a FROM GroupAnnouncement a WHERE a.publisherId = :publisherId ORDER BY a.createdAt DESC")
    List<GroupAnnouncement> findByPublisherId(@Param("publisherId") Long publisherId);

    /**
     * 查询最近发布的公告
     */
    @Query("SELECT a FROM GroupAnnouncement a WHERE a.groupId = :groupId AND a.createdAt > :since ORDER BY a.createdAt DESC")
    List<GroupAnnouncement> findRecentByGroupId(@Param("groupId") Long groupId, @Param("since") LocalDateTime since);

    /**
     * 查询指定状态的公告列表
     */
    @Query("SELECT a FROM GroupAnnouncement a WHERE a.groupId = :groupId AND a.status = :status ORDER BY a.createdAt DESC")
    List<GroupAnnouncement> findByGroupIdAndStatus(@Param("groupId") Long groupId, @Param("status") GroupAnnouncement.AnnouncementStatus status);

    /**
     * 删除群组的所有公告
     */
    @Modifying
    @Query("DELETE FROM GroupAnnouncement a WHERE a.groupId = :groupId")
    int deleteByGroupId(@Param("groupId") Long groupId);

    /**
     * 查询需要确认但未确认的成员数
     */
    @Query(value = "SELECT need_confirm_count - confirm_count FROM group_announcements WHERE id = :id", nativeQuery = true)
    Integer getUnconfirmedCount(@Param("id") Long id);

    /**
     * 搜索公告标题
     */
    @Query("SELECT a FROM GroupAnnouncement a WHERE a.groupId = :groupId AND a.title LIKE %:keyword% ORDER BY a.createdAt DESC")
    List<GroupAnnouncement> searchByTitle(@Param("groupId") Long groupId, @Param("keyword") String keyword);
}
