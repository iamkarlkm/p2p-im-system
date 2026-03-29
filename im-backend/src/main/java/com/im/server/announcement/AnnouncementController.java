package com.im.server.announcement;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 群公告REST API控制器
 */
public class AnnouncementController {
    
    private final AnnouncementService announcementService;
    
    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }
    
    // ============ 创建公告 ============
    
    public Map<String, Object> createAnnouncement(Map<String, Object> request) {
        String groupId = (String) request.get("groupId");
        String authorId = (String) request.get("authorId");
        String authorName = (String) request.get("authorName");
        String title = (String) request.get("title");
        String content = (String) request.get("content");
        String type = (String) request.getOrDefault("type", "NORMAL");
        
        if (groupId == null || title == null || content == null) {
            return error("groupId, title, content不能为空");
        }
        
        if (title.length() > 200) {
            return error("标题不能超过200字符");
        }
        
        if (content.length() > 50000) {
            return error("内容不能超过50000字符");
        }
        
        Announcement announcement = announcementService.createAnnouncement(
            groupId, authorId, authorName, title, content, type
        );
        
        return success(announcement.toMap(), "公告创建成功");
    }
    
    // ============ 获取公告列表 ============
    
    public Map<String, Object> getGroupAnnouncements(String groupId) {
        List<Announcement> announcements = announcementService.getGroupAnnouncements(groupId);
        List<Map<String, Object>> data = announcements.stream()
            .map(Announcement::toMap)
            .collect(Collectors.toList());
        return success(data, "获取成功");
    }
    
    // ============ 获取公告详情 ============
    
    public Map<String, Object> getAnnouncement(String announcementId) {
        Optional<Announcement> opt = announcementService.getAnnouncement(announcementId);
        if (opt.isEmpty()) {
            return error("公告不存在");
        }
        
        Announcement a = opt.get();
        a.incrementViewCount();
        
        Map<String, Object> result = a.toMap();
        return success(result, "获取成功");
    }
    
    // ============ 更新公告 ============
    
    public Map<String, Object> updateAnnouncement(String announcementId, Map<String, Object> request) {
        String title = (String) request.get("title");
        String content = (String) request.get("content");
        String userId = (String) request.get("userId");
        
        if (title == null || content == null) {
            return error("title和content不能为空");
        }
        
        try {
            Optional<Announcement> opt = announcementService.updateAnnouncement(
                announcementId, title, content, userId
            );
            if (opt.isEmpty()) {
                return error("公告不存在");
            }
            return success(opt.get().toMap(), "更新成功");
        } catch (AnnouncementException e) {
            return error(e.getMessage());
        }
    }
    
    // ============ 置顶/取消置顶 ============
    
    public Map<String, Object> pinAnnouncement(String announcementId, String userId) {
        Optional<Announcement> opt = announcementService.pinAnnouncement(announcementId, userId);
        if (opt.isEmpty()) {
            return error("公告不存在");
        }
        return success(opt.get().toMap(), "置顶成功");
    }
    
    public Map<String, Object> unpinAnnouncement(String announcementId, String userId) {
        Optional<Announcement> opt = announcementService.unpinAnnouncement(announcementId, userId);
        if (opt.isEmpty()) {
            return error("公告不存在");
        }
        return success(opt.get().toMap(), "取消置顶成功");
    }
    
    // ============ 删除公告 ============
    
    public Map<String, Object> deleteAnnouncement(String announcementId, String userId) {
        boolean deleted = announcementService.deleteAnnouncement(announcementId, userId);
        if (!deleted) {
            return error("删除失败");
        }
        return success(null, "删除成功");
    }
    
    // ============ 确认阅读 ============
    
    public Map<String, Object> confirmAnnouncement(String announcementId, String userId) {
        boolean confirmed = announcementService.confirmAnnouncement(announcementId, userId);
        return success(confirmed, confirmed ? "已确认" : "已确认过");
    }
    
    // ============ 获取已读用户 ============
    
    public Map<String, Object> getConfirmedUsers(String announcementId) {
        Set<String> users = announcementService.getConfirmedUsers(announcementId);
        return success(users, "获取成功");
    }
    
    // ============ 历史公告 ============
    
    public Map<String, Object> getHistory(String groupId, int page, int pageSize) {
        if (pageSize > 50) pageSize = 50;
        List<Announcement> history = announcementService.getHistory(groupId, page, pageSize);
        List<Map<String, Object>> data = history.stream()
            .map(Announcement::toMap)
            .collect(Collectors.toList());
        return success(data, "获取成功");
    }
    
    // ============ 分页获取 ============
    
    public Map<String, Object> getAnnouncementsPaged(String groupId, int page, int pageSize) {
        if (pageSize > 20) pageSize = 20;
        Map<String, Object> result = announcementService.getAnnouncementsPaged(groupId, page, pageSize);
        return success(result, "获取成功");
    }
    
    // ============ 我的公告 ============
    
    public Map<String, Object> getMyAnnouncements(String authorId) {
        List<Announcement> announcements = announcementService.getMyAnnouncements(authorId);
        List<Map<String, Object>> data = announcements.stream()
            .map(Announcement::toMap)
            .collect(Collectors.toList());
        return success(data, "获取成功");
    }
    
    // ============ 导出 ============
    
    public Map<String, Object> exportAnnouncement(String announcementId) {
        Optional<Announcement> opt = announcementService.getAnnouncement(announcementId);
        if (opt.isEmpty()) {
            return error("公告不存在");
        }
        String text = announcementService.exportAsPlainText(opt.get());
        return success(text, "导出成功");
    }
    
    // ============ 统计 ============
    
    public Map<String, Object> getStatistics(String groupId) {
        List<Announcement> announcements = announcementService.getGroupAnnouncements(groupId);
        int total = announcements.size();
        int pinned = (int) announcements.stream().filter(Announcement::isPinned).count();
        int important = (int) announcements.stream()
            .filter(a -> a.getType() == Announcement.AnnouncementType.IMPORTANT)
            .count();
        long totalViews = announcements.stream().mapToLong(Announcement::getViewCount).sum();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("pinned", pinned);
        stats.put("important", important);
        stats.put("totalViews", totalViews);
        
        return success(stats, "获取成功");
    }
    
    // ============ 工具方法 ============
    
    private Map<String, Object> success(Object data, String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", message);
        result.put("data", data);
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }
    
    private Map<String, Object> error(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", message);
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }
}
