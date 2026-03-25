package com.im.server.announcement;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 群公告服务层
 */
public class AnnouncementService {
    
    private final AnnouncementRepository repository;
    private final Set<String> adminGroupIds = ConcurrentHashMap.newKeySet();
    
    public AnnouncementService(AnnouncementRepository repository) {
        this.repository = repository;
    }
    
    /**
     * 创建公告
     */
    public Announcement createAnnouncement(String groupId, String authorId, String authorName,
                                          String title, String content, String type) {
        Announcement.AnnouncementType announcementType = Announcement.AnnouncementType.NORMAL;
        if ("IMPORTANT".equalsIgnoreCase(type)) {
            announcementType = Announcement.AnnouncementType.IMPORTANT;
        }
        
        Announcement announcement = Announcement.builder()
            .groupId(groupId)
            .authorId(authorId)
            .authorName(authorName)
            .title(title)
            .content(content)
            .type(announcementType)
            .build();
        
        return repository.save(announcement);
    }
    
    /**
     * 获取群公告列表
     */
    public List<Announcement> getGroupAnnouncements(String groupId) {
        return repository.findByGroupId(groupId);
    }
    
    /**
     * 获取公告详情
     */
    public Optional<Announcement> getAnnouncement(String announcementId) {
        return repository.findById(announcementId);
    }
    
    /**
     * 更新公告
     */
    public Optional<Announcement> updateAnnouncement(String announcementId, String title,
                                                    String content, String userId) {
        Optional<Announcement> opt = repository.findById(announcementId);
        if (opt.isEmpty()) {
            return Optional.empty();
        }
        
        Announcement announcement = opt.get();
        
        // 只有作者可以编辑
        if (!userId.equals(announcement.getAuthorId())) {
            throw new AnnouncementException("只有作者才能编辑公告");
        }
        
        announcement.updateContent(title, content);
        return Optional.of(repository.update(announcement));
    }
    
    /**
     * 置顶公告
     */
    public Optional<Announcement> pinAnnouncement(String announcementId, String userId) {
        Optional<Announcement> opt = repository.findById(announcementId);
        if (opt.isEmpty()) {
            return Optional.empty();
        }
        
        Announcement announcement = opt.get();
        announcement.pin();
        
        // 同一群最多置顶3条
        List<Announcement> pinned = repository.findPinnedByGroupId(announcement.getGroupId());
        if (pinned.size() > 3) {
            Announcement oldest = pinned.get(pinned.size() - 1);
            oldest.unpin();
            repository.update(oldest);
        }
        
        return Optional.of(repository.update(announcement));
    }
    
    /**
     * 取消置顶
     */
    public Optional<Announcement> unpinAnnouncement(String announcementId, String userId) {
        Optional<Announcement> opt = repository.findById(announcementId);
        if (opt.isEmpty()) {
            return Optional.empty();
        }
        
        Announcement announcement = opt.get();
        announcement.unpin();
        return Optional.of(repository.update(announcement));
    }
    
    /**
     * 删除公告
     */
    public boolean deleteAnnouncement(String announcementId, String userId) {
        Optional<Announcement> opt = repository.findById(announcementId);
        if (opt.isEmpty()) {
            return false;
        }
        
        Announcement announcement = opt.get();
        
        // 只有作者可以删除
        if (!userId.equals(announcement.getAuthorId())) {
            throw new AnnouncementException("只有作者才能删除公告");
        }
        
        return repository.delete(announcementId);
    }
    
    /**
     * 确认阅读公告
     */
    public boolean confirmAnnouncement(String announcementId, String userId) {
        Optional<Announcement> opt = repository.findById(announcementId);
        if (opt.isEmpty()) {
            return false;
        }
        
        Announcement announcement = opt.get();
        boolean confirmed = announcement.confirm(userId);
        if (confirmed) {
            repository.update(announcement);
        }
        return confirmed;
    }
    
    /**
     * 获取已读用户列表
     */
    public Set<String> getConfirmedUsers(String announcementId) {
        return repository.findById(announcementId)
            .map(Announcement::getConfirmedUserIds)
            .orElse(Collections.emptySet());
    }
    
    /**
     * 增加查看次数
     */
    public void incrementViewCount(String announcementId) {
        repository.findById(announcementId).ifPresent(a -> {
            a.incrementViewCount();
            repository.update(a);
        });
    }
    
    /**
     * 获取历史公告
     */
    public List<Announcement> getHistory(String groupId, int page, int pageSize) {
        return repository.findHistoryByGroupId(groupId, page, pageSize);
    }
    
    /**
     * 获取我的公告
     */
    public List<Announcement> getMyAnnouncements(String authorId) {
        return repository.findByAuthorId(authorId);
    }
    
    /**
     * 分页获取群公告
     */
    public Map<String, Object> getAnnouncementsPaged(String groupId, int page, int pageSize) {
        List<Announcement> all = repository.findByGroupId(groupId);
        int total = all.size();
        int totalPages = (int) Math.ceil((double) total / pageSize);
        
        List<Announcement> pageData = all.stream()
            .sorted(Comparator
                .comparing(Announcement::isPinned).reversed()
                .thenComparing(Announcement::getCreatedAt).reversed())
            .skip((long) (page - 1) * pageSize)
            .limit(pageSize)
            .collect(Collectors.toList());
        
        Map<String, Object> result = new HashMap<>();
        result.put("announcements", pageData);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        result.put("totalPages", totalPages);
        
        return result;
    }
    
    /**
     * 导出公告为纯文本
     */
    public String exportAsPlainText(Announcement announcement) {
        StringBuilder sb = new StringBuilder();
        sb.append("【").append(announcement.getTitle()).append("】\n");
        sb.append("发布人: ").append(announcement.getAuthorName()).append("\n");
        sb.append("时间: ").append(announcement.getCreatedAt()).append("\n");
        sb.append("类型: ").append(announcement.getType()).append("\n");
        if (announcement.isEdited()) {
            sb.append("编辑于: ").append(announcement.getUpdatedAt()).append("\n");
        }
        sb.append("\n");
        sb.append(stripMarkdown(announcement.getContent()));
        return sb.toString();
    }
    
    /**
     * 简单去除Markdown格式
     */
    private String stripMarkdown(String content) {
        if (content == null) return "";
        return content
            .replaceAll("#+\\s*", "")
            .replaceAll("\\*\\*(.+?)\\*\\*", "$1")
            .replaceAll("\\*(.+?)\\*", "$1")
            .replaceAll("__(.+?)__", "$1")
            .replaceAll("_(.+?)_", "$1")
            .replaceAll("~~(.+?)~~", "$1")
            .replaceAll("`(.+?)`", "$1")
            .replaceAll("```[\\s\\S]*?```", "")
            .replaceAll("\\[(.+?)\\]\\(.+?\\)", "$1");
    }
}
