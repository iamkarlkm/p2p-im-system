package com.im.backend.repository;

import com.im.backend.model.AnnouncementReadRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 公告阅读记录数据访问层
 */
@Repository
public interface AnnouncementReadRecordRepository extends JpaRepository<AnnouncementReadRecord, Long> {

    /**
     * 根据公告ID和用户ID查询阅读记录
     */
    @Query("SELECT r FROM AnnouncementReadRecord r WHERE r.announcementId = :announcementId AND r.userId = :userId")
    Optional<AnnouncementReadRecord> findByAnnouncementIdAndUserId(@Param("announcementId") Long announcementId, @Param("userId") Long userId);

    /**
     * 查询公告的所有阅读记录
     */
    @Query("SELECT r FROM AnnouncementReadRecord r WHERE r.announcementId = :announcementId ORDER BY r.readAt DESC")
    List<AnnouncementReadRecord> findByAnnouncementId(@Param("announcementId") Long announcementId);

    /**
     * 查询用户的所有阅读记录
     */
    @Query("SELECT r FROM AnnouncementReadRecord r WHERE r.userId = :userId ORDER BY r.readAt DESC")
    List<AnnouncementReadRecord> findByUserId(@Param("userId") Long userId);

    /**
     * 查询公告的阅读用户ID集合
     */
    @Query("SELECT r.userId FROM AnnouncementReadRecord r WHERE r.announcementId = :announcementId")
    Set<Long> findUserIdsByAnnouncementId(@Param("announcementId") Long announcementId);

    /**
     * 统计公告阅读数
     */
    @Query("SELECT COUNT(r) FROM AnnouncementReadRecord r WHERE r.announcementId = :announcementId")
    Long countByAnnouncementId(@Param("announcementId") Long announcementId);

    /**
     * 检查用户是否已读
     */
    @Query("SELECT COUNT(r) > 0 FROM AnnouncementReadRecord r WHERE r.announcementId = :announcementId AND r.userId = :userId")
    boolean existsByAnnouncementIdAndUserId(@Param("announcementId") Long announcementId, @Param("userId") Long userId);

    /**
     * 删除公告的所有阅读记录
     */
    @Modifying
    @Query("DELETE FROM AnnouncementReadRecord r WHERE r.announcementId = :announcementId")
    int deleteByAnnouncementId(@Param("announcementId") Long announcementId);

    /**
     * 批量查询用户已读的公告ID
     */
    @Query("SELECT r.announcementId FROM AnnouncementReadRecord r WHERE r.userId = :userId AND r.announcementId IN :announcementIds")
    Set<Long> findReadAnnouncementIdsByUserId(@Param("userId") Long userId, @Param("announcementIds") List<Long> announcementIds);

    /**
     * 查询用户未读的公告ID（在指定公告列表中）
     */
    @Query(value = "SELECT a.id FROM group_announcements a WHERE a.id IN :announcementIds AND a.id NOT IN (SELECT r.announcement_id FROM announcement_read_records r WHERE r.user_id = :userId)", nativeQuery = true)
    Set<Long> findUnreadAnnouncementIds(@Param("userId") Long userId, @Param("announcementIds") List<Long> announcementIds);

    /**
     * 查询最近阅读记录
     */
    @Query("SELECT r FROM AnnouncementReadRecord r WHERE r.readAt > :since ORDER BY r.readAt DESC")
    List<AnnouncementReadRecord> findRecentRecords(@Param("since") LocalDateTime since);
}
