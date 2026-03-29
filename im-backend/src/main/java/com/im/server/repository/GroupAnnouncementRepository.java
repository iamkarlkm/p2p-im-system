package com.im.server.repository;

import com.im.server.entity.AnnouncementReadStatus;
import com.im.server.entity.GroupAnnouncement;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 群公告仓储（内存实现，生产环境建议使用MySQL/MongoDB）
 * 
 * @author IM System
 * @version 1.0
 */
public class GroupAnnouncementRepository {

    // 存储公告列表: announcementId -> GroupAnnouncement
    private final Map<String, GroupAnnouncement> announcements = new ConcurrentHashMap<>();
    
    // 存储公告的已读状态: announcementId -> Map(userId -> ReadStatus)
    private final Map<String, Map<String, AnnouncementReadStatus>> readStatuses = new ConcurrentHashMap<>();
    
    // 索引: groupId -> List<announcementId> (按时间倒序)
    private final Map<String, List<String>> groupIndex = new ConcurrentHashMap<>();

    private static GroupAnnouncementRepository instance;

    private GroupAnnouncementRepository() {
    }

    public static synchronized GroupAnnouncementRepository getInstance() {
        if (instance == null) {
            instance = new GroupAnnouncementRepository();
        }
        return instance;
    }

    // ==================== 公告CRUD操作 ====================

    /**
     * 保存公告
     */
    public GroupAnnouncement save(GroupAnnouncement announcement) {
        if (announcement.getAnnouncementId() == null) {
            announcement.setAnnouncementId(UUID.randomUUID().toString());
        }
        announcement.setUpdatedAt(LocalDateTime.now());
        announcements.put(announcement.getAnnouncementId(), announcement);
        
        // 更新群索引
        String groupId = announcement.getGroupId();
        groupIndex.computeIfAbsent(groupId, k -> new ArrayList<>());
        List<String> ids = groupIndex.get(groupId);
        if (!ids.contains(announcement.getAnnouncementId())) {
            ids.add(0, announcement.getAnnouncementId()); // 添加到列表开头（最新）
        }
        
        return announcement;
    }

    /**
     * 根据ID查找公告
     */
    public Optional<GroupAnnouncement> findById(String announcementId) {
        GroupAnnouncement announcement = announcements.get(announcementId);
        if (announcement != null && !announcement.isDeleted()) {
            return Optional.of(announcement);
        }
        return Optional.empty();
    }

    /**
     * 根据群ID查找所有公告（分页）
     */
    public List<GroupAnnouncement> findByGroupId(String groupId, int page, int size) {
        List<String> ids = groupIndex.getOrDefault(groupId, new ArrayList<>());
        int start = page * size;
        int end = Math.min(start + size, ids.size());
        
        if (start >= ids.size()) {
            return new ArrayList<>();
        }
        
        return ids.subList(start, end).stream()
                .map(announcements::get)
                .filter(a -> a != null && !a.isDeleted())
                .collect(Collectors.toList());
    }

    /**
     * 根据群ID查找所有公告（不分页）
     */
    public List<GroupAnnouncement> findByGroupId(String groupId) {
        List<String> ids = groupIndex.getOrDefault(groupId, new ArrayList<>());
        return ids.stream()
                .map(announcements::get)
                .filter(a -> a != null && !a.isDeleted())
                .collect(Collectors.toList());
    }

    /**
     * 根据群ID查找已置顶的公告
     */
    public List<GroupAnnouncement> findPinnedByGroupId(String groupId) {
        return findByGroupId(groupId).stream()
                .filter(GroupAnnouncement::isPinned)
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(Collectors.toList());
    }

    /**
     * 根据群ID查找未置顶的公告
     */
    public List<GroupAnnouncement> findUnpinnedByGroupId(String groupId) {
        return findByGroupId(groupId).stream()
                .filter(a -> !a.isPinned())
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(Collectors.toList());
    }

    /**
     * 更新公告
     */
    public GroupAnnouncement update(GroupAnnouncement announcement) {
        announcement.setUpdatedAt(LocalDateTime.now());
        announcement.setEdited(true);
        announcement.setEditedAt(LocalDateTime.now());
        announcements.put(announcement.getAnnouncementId(), announcement);
        return announcement;
    }

    /**
     * 软删除公告
     */
    public void delete(String announcementId) {
        GroupAnnouncement announcement = announcements.get(announcementId);
        if (announcement != null) {
            announcement.setDeleted(true);
            announcement.setUpdatedAt(LocalDateTime.now());
        }
    }

    /**
     * 永久删除公告
     */
    public void permanentDelete(String announcementId) {
        GroupAnnouncement announcement = announcements.get(announcementId);
        if (announcement != null) {
            String groupId = announcement.getGroupId();
            announcements.remove(announcementId);
            
            // 从索引中移除
            List<String> ids = groupIndex.get(groupId);
            if (ids != null) {
                ids.remove(announcementId);
            }
            
            // 删除已读状态
            readStatuses.remove(announcementId);
        }
    }

    /**
     * 统计群公告数量
     */
    public long countByGroupId(String groupId) {
        return findByGroupId(groupId).size();
    }

    // ==================== 已读状态操作 ====================

    /**
     * 标记已读
     */
    public AnnouncementReadStatus markAsRead(String announcementId, String userId) {
        AnnouncementReadStatus status = new AnnouncementReadStatus();
        status.setStatusId(UUID.randomUUID().toString());
        status.setAnnouncementId(announcementId);
        status.setUserId(userId);
        status.setRead(true);
        status.setReadAt(LocalDateTime.now());
        
        readStatuses.computeIfAbsent(announcementId, k -> new ConcurrentHashMap<>())
                .put(userId, status);
        
        // 更新公告的已读统计
        GroupAnnouncement announcement = announcements.get(announcementId);
        if (announcement != null) {
            announcement.setReadCount(announcement.getReadCount() + 1);
        }
        
        return status;
    }

    /**
     * 检查用户是否已读
     */
    public boolean isReadByUser(String announcementId, String userId) {
        Map<String, AnnouncementReadStatus> statuses = readStatuses.get(announcementId);
        if (statuses == null) {
            return false;
        }
        AnnouncementReadStatus status = statuses.get(userId);
        return status != null && status.isRead();
    }

    /**
     * 获取用户的已读状态
     */
    public Optional<AnnouncementReadStatus> getReadStatus(String announcementId, String userId) {
        Map<String, AnnouncementReadStatus> statuses = readStatuses.get(announcementId);
        if (statuses == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(statuses.get(userId));
    }

    /**
     * 获取公告的所有已读用户
     */
    public List<AnnouncementReadStatus> getReadStatuses(String announcementId) {
        Map<String, AnnouncementReadStatus> statuses = readStatuses.get(announcementId);
        if (statuses == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(statuses.values());
    }

    /**
     * 批量获取用户的公告已读状态
     */
    public Map<String, Boolean> batchCheckReadStatus(String userId, List<String> announcementIds) {
        Map<String, Boolean> result = new HashMap<>();
        for (String announcementId : announcementIds) {
            result.put(announcementId, isReadByUser(announcementId, userId));
        }
        return result;
    }

    /**
     * 获取用户的未读公告数量
     */
    public int getUnreadCount(String groupId, String userId) {
        List<GroupAnnouncement> announcements = findByGroupId(groupId);
        int unreadCount = 0;
        for (GroupAnnouncement announcement : announcements) {
            if (!isReadByUser(announcement.getAnnouncementId(), userId)) {
                unreadCount++;
            }
        }
        return unreadCount;
    }

    /**
     * 获取用户未读的公告列表
     */
    public List<GroupAnnouncement> getUnreadAnnouncements(String groupId, String userId) {
        return findByGroupId(groupId).stream()
                .filter(a -> !isReadByUser(a.getAnnouncementId(), userId))
                .collect(Collectors.toList());
    }

    /**
     * 获取公告的已读用户数
     */
    public int getReadCount(String announcementId) {
        GroupAnnouncement announcement = announcements.get(announcementId);
        if (announcement == null) {
            return 0;
        }
        return announcement.getReadCount();
    }

    /**
     * 获取公告的未读用户数
     */
    public int getUnreadCountByAnnouncement(String announcementId, int totalMembers) {
        int readCount = getReadCount(announcementId);
        return totalMembers - readCount;
    }

    // ==================== 统计和清理 ====================

    /**
     * 清理过期公告（超过指定天数）
     */
    public int cleanupOldAnnouncements(int daysToKeep) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(daysToKeep);
        List<String> toDelete = new ArrayList<>();
        
        for (GroupAnnouncement announcement : announcements.values()) {
            if (!announcement.isDeleted() && announcement.getCreatedAt().isBefore(cutoff)) {
                toDelete.add(announcement.getAnnouncementId());
            }
        }
        
        for (String id : toDelete) {
            permanentDelete(id);
        }
        
        return toDelete.size();
    }

    /**
     * 清空所有数据（慎用）
     */
    public void clearAll() {
        announcements.clear();
        readStatuses.clear();
        groupIndex.clear();
    }
}
