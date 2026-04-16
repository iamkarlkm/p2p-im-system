package com.im.service.group.repository;

import com.im.service.group.entity.GroupAnnouncement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 群公告数据访问接口
 * 
 * @author IM Team
 * @version 1.0
 */
@Repository
public interface GroupAnnouncementRepository extends JpaRepository<GroupAnnouncement, String> {

    /**
     * 根据群组ID查询公告列表（不分页）
     */
    List<GroupAnnouncement> findByGroupIdAndDeletedFalseOrderByIsPinnedDescCreatedAtDesc(String groupId);

    /**
     * 根据群组ID查询公告列表（分页）
     */
    Page<GroupAnnouncement> findByGroupIdAndDeletedFalse(String groupId, Pageable pageable);

    /**
     * 查询群组的最新公告
     */
    Optional<GroupAnnouncement> findFirstByGroupIdAndDeletedFalseOrderByIsPinnedDescCreatedAtDesc(String groupId);

    /**
     * 查询群组的置顶公告
     */
    List<GroupAnnouncement> findByGroupIdAndIsPinnedAndDeletedFalseTrueOrderByPinnedAtDesc(String groupId);

    /**
     * 统计群组公告数量
     */
    Long countByGroupIdAndDeletedFalse(String groupId);

    /**
     * 增加阅读次数
     */
    @Modifying
    @Query("UPDATE GroupAnnouncement a SET a.readCount = a.readCount + 1 WHERE a.id = :id")
    void incrementReadCount(@Param("id") String id);

    /**
     * 搜索群公告
     */
    @Query("SELECT a FROM GroupAnnouncement a WHERE a.groupId = :groupId AND a.deleted = false " +
           "AND (a.title LIKE %:keyword% OR a.content LIKE %:keyword%) " +
           "ORDER BY a.isPinned DESC, a.createdAt DESC")
    Page<GroupAnnouncement> searchByGroupId(@Param("groupId") String groupId, 
                                             @Param("keyword") String keyword,
                                             Pageable pageable);

    /**
     * 检查是否是创建者
     */
    boolean existsByIdAndCreatorId(String id, String creatorId);
}
