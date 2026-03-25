package com.im.server.service;

import com.im.server.entity.AnnouncementReadStatus;
import com.im.server.entity.GroupAnnouncement;
import com.im.server.repository.GroupAnnouncementRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 群公告服务类
 * 
 * @author IM System
 * @version 1.0
 */
public class GroupAnnouncementService {

    private final GroupAnnouncementRepository repository;
    private final WebSocketNotificationService webSocketService;
    
    // Markdown解析支持
    private static final int MAX_TITLE_LENGTH = 100;
    private static final int MAX_CONTENT_LENGTH = 10000;
    private static final int MAX_SUMMARY_LENGTH = 200;

    public GroupAnnouncementService() {
        this.repository = GroupAnnouncementRepository.getInstance();
        this.webSocketService = new WebSocketNotificationService();
    }

    // ==================== 公告发布 ====================

    /**
     * 发布群公告
     * 
     * @param groupId 群ID
     * @param authorId 发布者ID
     * @param authorName 发布者昵称
     * @param authorAvatar 发布者头像
     * @param title 标题
     * @param content 内容（支持Markdown）
     * @return 发布的公告
     */
    public GroupAnnouncement publishAnnouncement(String groupId, String authorId, 
            String authorName, String authorAvatar, String title, String content) {
        
        // 验证输入
        validateAnnouncement(title, content);
        
        // 创建公告
        GroupAnnouncement announcement = GroupAnnouncement.builder()
                .announcementId(UUID.randomUUID().toString())
                .groupId(groupId)
                .authorId(authorId)
                .authorName(authorName)
                .authorAvatar(authorAvatar)
                .title(title)
                .content(content)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .pinned(false)
                .edited(false)
                .deleted(false)
                .readCount(0)
                .unreadCount(0)
                .commentCount(0)
                .build();
        
        // 保存
        announcement = repository.save(announcement);
        
        // 通知群成员（WebSocket）
        webSocketService.notifyNewAnnouncement(groupId, announcement);
        
        return announcement;
    }

    /**
     * 更新群公告
     */
    public GroupAnnouncement updateAnnouncement(String announcementId, String userId, 
            String title, String content) {
        
        GroupAnnouncement announcement = repository.findById(announcementId)
                .orElseThrow(() -> new IllegalArgumentException("公告不存在: " + announcementId));
        
        // 验证权限（仅发布者和管理员可编辑）
        if (!announcement.getAuthorId().equals(userId)) {
            throw new SecurityException("无权限编辑此公告");
        }
        
        // 验证输入
        validateAnnouncement(title, content);
        
        // 更新内容
        announcement.setTitle(title);
        announcement.setContent(content);
        announcement.setEdited(true);
        announcement.setEditedAt(LocalDateTime.now());
        
        // 保存
        announcement = repository.update(announcement);
        
        // 通知更新
        webSocketService.notifyAnnouncementUpdated(announcement.getGroupId(), announcement);
        
        return announcement;
    }

    /**
     * 删除群公告
     */
    public void deleteAnnouncement(String announcementId, String userId) {
        GroupAnnouncement announcement = repository.findById(announcementId)
                .orElseThrow(() -> new IllegalArgumentException("公告不存在: " + announcementId));
        
        // 验证权限（仅发布者和管理员可删除）
        if (!announcement.getAuthorId().equals(userId)) {
            throw new SecurityException("无权限删除此公告");
        }
        
        // 软删除
        repository.delete(announcementId);
        
        // 通知删除
        webSocketService.notifyAnnouncementDeleted(announcement.getGroupId(), announcementId);
    }

    /**
     * 置顶/取消置顶公告
     */
    public GroupAnnouncement togglePin(String announcementId, String userId) {
        GroupAnnouncement announcement = repository.findById(announcementId)
                .orElseThrow(() -> new IllegalArgumentException("公告不存在: " + announcementId));
        
        // 切换置顶状态
        announcement.setPinned(!announcement.isPinned());
        announcement = repository.update(announcement);
        
        // 通知更新
        webSocketService.notifyAnnouncementUpdated(announcement.getGroupId(), announcement);
        
        return announcement;
    }

    // ==================== 公告查询 ====================

    /**
     * 获取公告详情
     */
    public GroupAnnouncement getAnnouncement(String announcementId) {
        return repository.findById(announcementId)
                .orElseThrow(() -> new IllegalArgumentException("公告不存在: " + announcementId));
    }

    /**
     * 获取群公告列表（分页）
     */
    public List<GroupAnnouncement> getAnnouncements(String groupId, int page, int size) {
        return repository.findByGroupId(groupId, page, size);
    }

    /**
     * 获取群公告列表（不分页）
     */
    public List<GroupAnnouncement> getAllAnnouncements(String groupId) {
        return repository.findByGroupId(groupId);
    }

    /**
     * 获取置顶公告
     */
    public List<GroupAnnouncement> getPinnedAnnouncements(String groupId) {
        return repository.findPinnedByGroupId(groupId);
    }

    /**
     * 获取公告历史记录
     */
    public List<GroupAnnouncement> getAnnouncementHistory(String groupId, int page, int size) {
        return repository.findByGroupId(groupId, page, size);
    }

    /**
     * 搜索公告
     */
    public List<GroupAnnouncement> searchAnnouncements(String groupId, String keyword) {
        return repository.findByGroupId(groupId).stream()
                .filter(a -> a.getTitle().contains(keyword) || 
                            (a.getContent() != null && a.getContent().contains(keyword)))
                .collect(Collectors.toList());
    }

    /**
     * 统计群公告数量
     */
    public long countAnnouncements(String groupId) {
        return repository.countByGroupId(groupId);
    }

    // ==================== 已读状态 ====================

    /**
     * 标记公告已读
     */
    public AnnouncementReadStatus markAsRead(String announcementId, String userId) {
        GroupAnnouncement announcement = repository.findById(announcementId)
                .orElseThrow(() -> new IllegalArgumentException("公告不存在: " + announcementId));
        
        // 检查是否已读
        if (repository.isReadByUser(announcementId, userId)) {
            return repository.getReadStatus(announcementId, userId)
                    .orElseThrow(() -> new IllegalStateException("已读状态异常"));
        }
        
        // 标记已读
        AnnouncementReadStatus status = repository.markAsRead(announcementId, userId);
        
        // 通知已读状态更新
        webSocketService.notifyReadStatusChanged(announcement.getGroupId(), announcementId, 
                repository.getReadCount(announcementId));
        
        return status;
    }

    /**
     * 批量标记已读
     */
    public List<AnnouncementReadStatus> batchMarkAsRead(String groupId, String userId, 
            List<String> announcementIds) {
        
        return announcementIds.stream()
                .map(id -> {
                    try {
                        return markAsRead(id, userId);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 检查是否已读
     */
    public boolean isAnnouncementRead(String announcementId, String userId) {
        return repository.isReadByUser(announcementId, userId);
    }

    /**
     * 批量检查已读状态
     */
    public Map<String, Boolean> batchCheckReadStatus(String userId, List<String> announcementIds) {
        return repository.batchCheckReadStatus(userId, announcementIds);
    }

    /**
     * 获取用户的未读公告数
     */
    public int getUnreadCount(String groupId, String userId) {
        return repository.getUnreadCount(groupId, userId);
    }

    /**
     * 获取用户的未读公告列表
     */
    public List<GroupAnnouncement> getUnreadAnnouncements(String groupId, String userId) {
        return repository.getUnreadAnnouncements(groupId, userId);
    }

    /**
     * 获取公告已读人数
     */
    public int getReadCount(String announcementId) {
        return repository.getReadCount(announcementId);
    }

    /**
     * 获取公告未读人数
     */
    public int getUnreadCountByAnnouncement(String announcementId, int totalMembers) {
        return repository.getUnreadCountByAnnouncement(announcementId, totalMembers);
    }

    // ==================== 辅助方法 ====================

    /**
     * 验证公告输入
     */
    private void validateAnnouncement(String title, String content) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("公告标题不能为空");
        }
        if (title.length() > MAX_TITLE_LENGTH) {
            throw new IllegalArgumentException("公告标题不能超过" + MAX_TITLE_LENGTH + "个字符");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("公告内容不能为空");
        }
        if (content.length() > MAX_CONTENT_LENGTH) {
            throw new IllegalArgumentException("公告内容不能超过" + MAX_CONTENT_LENGTH + "个字符");
        }
    }

    /**
     * 清理过期公告
     */
    public int cleanupOldAnnouncements(int daysToKeep) {
        return repository.cleanupOldAnnouncements(daysToKeep);
    }

    /**
     * 获取公告的阅读详情
     */
    public List<AnnouncementReadStatus> getReadStatuses(String announcementId) {
        return repository.getReadStatuses(announcementId);
    }

    // ==================== 内部类：WebSocket通知服务 ====================

    private static class WebSocketNotificationService {
        
        public void notifyNewAnnouncement(String groupId, GroupAnnouncement announcement) {
            // 发送WebSocket通知
            // 实际实现应该通过WebSocket向群成员推送新公告
            System.out.println("[WebSocket] 发送新公告通知: groupId=" + groupId + 
                    ", announcementId=" + announcement.getAnnouncementId());
        }

        public void notifyAnnouncementUpdated(String groupId, GroupAnnouncement announcement) {
            System.out.println("[WebSocket] 发送公告更新通知: groupId=" + groupId + 
                    ", announcementId=" + announcement.getAnnouncementId());
        }

        public void notifyAnnouncementDeleted(String groupId, String announcementId) {
            System.out.println("[WebSocket] 发送公告删除通知: groupId=" + groupId + 
                    ", announcementId=" + announcementId);
        }

        public void notifyReadStatusChanged(String groupId, String announcementId, int readCount) {
            System.out.println("[WebSocket] 发送已读状态更新: groupId=" + groupId + 
                    ", announcementId=" + announcementId + ", readCount=" + readCount);
        }
    }

    // ==================== 静态工具方法 ====================

    /**
     * 解析Markdown为纯文本（用于摘要）
     */
    public static String parseMarkdownToPlainText(String markdown) {
        if (markdown == null) {
            return "";
        }
        return markdown
                .replaceAll("#+\\s*", "")           // 移除标题标记
                .replaceAll("\\*\\*(.+?)\\*\\*", "$1")  // 移除粗体
                .replaceAll("\\*(.+?)\\*", "$1")        // 移除斜体
                .replaceAll("__(.+?)__", "$1")          // 移除下划线
                .replaceAll("_(.+?)_", "$1")            // 移除下划线
                .replaceAll("```[\\s\\S]*?```", "")     // 移除代码块
                .replaceAll("`(.+?)`", "$1")             // 移除行内代码
                .replaceAll("\\[([^\\]]+)\\]\\([^)]+\\)", "$1")  // 移除链接
                .replaceAll("!\\[([^\\]]*)\\]\\([^)]+\\)", "$1")  // 移除图片
                .replaceAll(">\\s*", "")           // 移除引用
                .replaceAll("[-*+]\\s+", "")       // 移除列表标记
                .replaceAll("\\n+", " ")           // 换行变空格
                .trim();
    }

    /**
     * 生成摘要
     */
    public static String generateSummary(String content, int maxLength) {
        String plainText = parseMarkdownToPlainText(content);
        if (plainText.length() <= maxLength) {
            return plainText;
        }
        return plainText.substring(0, maxLength) + "...";
    }
}
