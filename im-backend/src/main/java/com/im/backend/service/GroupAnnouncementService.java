package com.im.backend.service;

import com.im.backend.dto.AnnouncementDTO;
import com.im.backend.dto.AnnouncementResponseDTO;
import com.im.backend.model.GroupAnnouncement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

/**
 * 群公告服务接口
 */
public interface GroupAnnouncementService {

    /**
     * 发布公告
     */
    AnnouncementResponseDTO publishAnnouncement(Long publisherId, AnnouncementDTO dto);

    /**
     * 编辑公告
     */
    AnnouncementResponseDTO editAnnouncement(Long announcementId, Long operatorId, AnnouncementDTO dto);

    /**
     * 撤回公告
     */
    void withdrawAnnouncement(Long announcementId, Long operatorId, String reason);

    /**
     * 获取公告详情
     */
    AnnouncementResponseDTO getAnnouncementDetail(Long announcementId, Long userId);

    /**
     * 获取群组公告列表
     */
    List<AnnouncementResponseDTO> getGroupAnnouncements(Long groupId, Long userId);

    /**
     * 分页获取群组公告
     */
    Page<AnnouncementResponseDTO> getGroupAnnouncementsPageable(Long groupId, Long userId, Pageable pageable);

    /**
     * 标记公告为已读
     */
    void markAsRead(Long announcementId, Long userId);

    /**
     * 确认公告
     */
    void confirmAnnouncement(Long announcementId, Long userId);

    /**
     * 批量标记已读
     */
    void markMultipleAsRead(List<Long> announcementIds, Long userId);

    /**
     * 获取已读用户列表
     */
    Set<Long> getReadUserIds(Long announcementId);

    /**
     * 获取确认用户列表
     */
    Set<Long> getConfirmedUserIds(Long announcementId);

    /**
     * 获取用户未读公告数
     */
    Long getUnreadCount(Long groupId, Long userId);

    /**
     * 获取群组生效中的公告
     */
    List<AnnouncementResponseDTO> getActiveAnnouncements(Long groupId, Long userId);

    /**
     * 获取群组置顶公告
     */
    List<AnnouncementResponseDTO> getPinnedAnnouncements(Long groupId, Long userId);

    /**
     * 检查用户是否已读
     */
    boolean hasRead(Long announcementId, Long userId);

    /**
     * 检查用户是否已确认
     */
    boolean hasConfirmed(Long announcementId, Long userId);

    /**
     * 搜索公告
     */
    List<AnnouncementResponseDTO> searchAnnouncements(Long groupId, String keyword, Long userId);

    /**
     * 删除过期公告
     */
    int cleanExpiredAnnouncements();

    /**
     * 删除公告（管理员）
     */
    void deleteAnnouncement(Long announcementId, Long operatorId);
}
