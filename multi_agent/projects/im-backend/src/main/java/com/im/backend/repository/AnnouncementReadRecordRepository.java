package com.im.backend.repository;

import com.im.backend.entity.AnnouncementReadRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnnouncementReadRecordRepository extends JpaRepository<AnnouncementReadRecordEntity, Long> {

    /** 查找指定用户对指定公告的阅读记录 */
    Optional<AnnouncementReadRecordEntity> findByAnnouncementIdAndUserId(Long announcementId, Long userId);

    /** 统计公告已读人数 */
    long countByAnnouncementIdAndConfirmedTrue(Long announcementId);

    /** 批量查询用户对多个公告的阅读状态 */
    @Query("SELECT r.announcementId FROM AnnouncementReadRecordEntity r WHERE r.userId = :userId AND r.announcementId IN :announcementIds")
    List<Long> findReadIdsByUserIdAndAnnouncementIds(@Param("userId") Long userId, @Param("announcementIds") List<Long> announcementIds);

    /** 查询公告所有已读用户 */
    List<AnnouncementReadRecordEntity> findByAnnouncementId(Long announcementId);

    /** 批量插入已读记录 */
    void saveAll(Iterable<AnnouncementReadRecordEntity> records);
}
