package com.im.service;

import com.im.dto.GroupAnnouncementRequest;
import com.im.dto.GroupAnnouncementResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 群公告服务接口
 * 功能ID: #30
 * 功能名称: 群公告
 * 
 * @author developer-agent
 * @since 2026-03-30
 */
public interface GroupAnnouncementService {

    /**
     * 创建群公告
     *
     * @param request 公告请求
     * @param creatorId 创建者ID
     * @return 公告响应
     */
    GroupAnnouncementResponse createAnnouncement(GroupAnnouncementRequest request, Long creatorId);

    /**
     * 更新群公告
     *
     * @param id 公告ID
     * @param request 更新请求
     * @param operatorId 操作者ID
     * @return 更新后的公告
     */
    GroupAnnouncementResponse updateAnnouncement(Long id, GroupAnnouncementRequest request, Long operatorId);

    /**
     * 删除群公告
     *
     * @param id 公告ID
     * @param operatorId 操作者ID
     */
    void deleteAnnouncement(Long id, Long operatorId);

    /**
     * 获取公告详情
     *
     * @param id 公告ID
     * @param userId 当前用户ID (用于判断是否已读)
     * @return 公告详情
     */
    GroupAnnouncementResponse getAnnouncement(Long id, Long userId);

    /**
     * 获取群组的公告列表
     *
     * @param groupId 群组ID
     * @param userId 当前用户ID
     * @return 公告列表
     */
    List<GroupAnnouncementResponse> getGroupAnnouncements(Long groupId, Long userId);

    /**
     * 分页获取群公告
     *
     * @param groupId 群组ID
     * @param userId 当前用户ID
     * @param pageable 分页参数
     * @return 分页公告列表
     */
    Page<GroupAnnouncementResponse> getGroupAnnouncementsPaged(Long groupId, Long userId, Pageable pageable);

    /**
     * 获取最新公告
     *
     * @param groupId 群组ID
     * @param userId 当前用户ID
     * @return 最新公告
     */
    GroupAnnouncementResponse getLatestAnnouncement(Long groupId, Long userId);

    /**
     * 获取置顶公告
     *
     * @param groupId 群组ID
     * @param userId 当前用户ID
     * @return 置顶公告列表
     */
    List<GroupAnnouncementResponse> getPinnedAnnouncements(Long groupId, Long userId);

    /**
     * 标记公告已读
     *
     * @param id 公告ID
     * @param userId 用户ID
     */
    void markAsRead(Long id, Long userId);

    /**
     * 批量标记已读
     *
     * @param groupId 群组ID
     * @param userId 用户ID
     */
    void markAllAsRead(Long groupId, Long userId);

    /**
     * 置顶/取消置顶公告
     *
     * @param id 公告ID
     * @param pinned 是否置顶
     * @param operatorId 操作者ID
     */
    void pinAnnouncement(Long id, Boolean pinned, Long operatorId);

    /**
     * 获取已读人数
     *
     * @param id 公告ID
     * @return 已读人数
     */
    Integer getReadCount(Long id);

    /**
     * 搜索公告
     *
     * @param groupId 群组ID
     * @param keyword 关键词
     * @param userId 当前用户ID
     * @return 公告列表
     */
    List<GroupAnnouncementResponse> searchAnnouncements(Long groupId, String keyword, Long userId);

    /**
     * 获取未读公告数量
     *
     * @param groupId 群组ID
     * @param userId 用户ID
     * @return 未读数量
     */
    Long getUnreadCount(Long groupId, Long userId);

    /**
     * 检查用户是否为公告创建者
     *
     * @param id 公告ID
     * @param userId 用户ID
     * @return 是否创建者
     */
    boolean isCreator(Long id, Long userId);
}
