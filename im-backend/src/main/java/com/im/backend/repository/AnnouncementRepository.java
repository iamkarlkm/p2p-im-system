package com.im.backend.repository;

import com.im.backend.entity.AnnouncementEntity;
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
public interface AnnouncementRepository extends JpaRepository<AnnouncementEntity, Long> {

    /** 查询群组的所有有效公告 */
    @Query("SELECT a FROM AnnouncementEntity a WHERE a.groupId = :groupId AND a.deleted = false " +
           "AND (a.expireTime IS NULL OR a.expireTime > :now) ORDER BY a.pinned DESC, a.publishTime DESC")
    List<AnnouncementEntity> findActiveByGroupId(@Param("groupId") Long groupId, @Param("now") LocalDateTime now);

    /** 分页查询群组公告 */
    @Query("SELECT a FROM AnnouncementEntity a WHERE a.groupId = :groupId AND a.deleted = false " +
           "AND (a.expireTime IS NULL OR a.expireTime > :now) ORDER BY a.pinned DESC, a.publishTime DESC")
    Page<AnnouncementEntity> findPageByGroupId(@Param("groupId") Long groupId, @Param("now") LocalDateTime now, Pageable pageable);

    /** 查询置顶公告 */
    @Query("SELECT a FROM AnnouncementEntity a WHERE a.groupId = :groupId AND a.pinned = true AND a.deleted = false " +
           "AND (a.expireTime IS NULL OR a.expireTime > :now)")
    List<AnnouncementEntity> findPinnedByGroupId(@Param("groupId") Long groupId, @Param("now") LocalDateTime now);

    /** 统计群组公告总数 */
    @Query("SELECT COUNT(a) FROM AnnouncementEntity a WHERE a.groupId = :groupId AND a.deleted = false")
    long countByGroupId(@Param("groupId") Long groupId);

    /** 清理过期公告（软删除） */
    @Modifying
    @Query("UPDATE AnnouncementEntity a SET a.deleted = true, a.deletedTime = :now " +
           "WHERE a.expireTime IS NOT NULL AND a.expireTime < :now AND a.deleted = false")
    int deleteExpiredAnnouncements(@Param("now") LocalDateTime now);

    /** 查询用户已读的公告ID列表 */
    @Query("SELECT r.announcementId FROM AnnouncementReadRecordEntity r WHERE r.userId = :userId")
    List<Long> findReadAnnouncementIdsByUserId(@Param("userId") Long userId);
}
