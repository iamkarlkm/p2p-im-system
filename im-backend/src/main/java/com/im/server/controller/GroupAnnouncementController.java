package com.im.server.controller;

import com.im.server.entity.AnnouncementReadStatus;
import com.im.server.entity.GroupAnnouncement;
import com.im.server.service.GroupAnnouncementService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 群公告控制器
 * 
 * @author IM System
 * @version 1.0
 */
public class GroupAnnouncementController {

    private final GroupAnnouncementService announcementService;

    public GroupAnnouncementController() {
        this.announcementService = new GroupAnnouncementService();
    }

    // ==================== 公告CRUD ====================

    /**
     * 发布群公告
     * 
     * POST /api/announcement
     * 
     * Request Body:
     * {
     *   "groupId": "群ID",
     *   "authorId": "发布者ID",
     *   "authorName": "发布者昵称",
     *   "authorAvatar": "发布者头像",
     *   "title": "公告标题",
     *   "content": "公告内容（支持Markdown）"
     * }
     */
    public Map<String, Object> publishAnnouncement(Map<String, Object> request) {
        try {
            String groupId = (String) request.get("groupId");
            String authorId = (String) request.get("authorId");
            String authorName = (String) request.get("authorName");
            String authorAvatar = (String) request.get("authorAvatar");
            String title = (String) request.get("title");
            String content = (String) request.get("content");

            GroupAnnouncement announcement = announcementService.publishAnnouncement(
                    groupId, authorId, authorName, authorAvatar, title, content);

            return successResponse(announcement, "发布成功");
        } catch (IllegalArgumentException e) {
            return errorResponse(400, e.getMessage());
        } catch (Exception e) {
            return errorResponse(500, "发布失败: " + e.getMessage());
        }
    }

    /**
     * 更新群公告
     * 
     * PUT /api/announcement/{announcementId}
     * 
     * Request Body:
     * {
     *   "userId": "用户ID",
     *   "title": "新标题",
     *   "content": "新内容"
     * }
     */
    public Map<String, Object> updateAnnouncement(String announcementId, 
            Map<String, Object> request) {
        try {
            String userId = (String) request.get("userId");
            String title = (String) request.get("title");
            String content = (String) request.get("content");

            GroupAnnouncement announcement = announcementService.updateAnnouncement(
                    announcementId, userId, title, content);

            return successResponse(announcement, "更新成功");
        } catch (IllegalArgumentException e) {
            return errorResponse(404, e.getMessage());
        } catch (SecurityException e) {
            return errorResponse(403, e.getMessage());
        } catch (Exception e) {
            return errorResponse(500, "更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除群公告
     * 
     * DELETE /api/announcement/{announcementId}?userId=用户ID
     */
    public Map<String, Object> deleteAnnouncement(String announcementId, String userId) {
        try {
            announcementService.deleteAnnouncement(announcementId, userId);
            return successResponse(null, "删除成功");
        } catch (IllegalArgumentException e) {
            return errorResponse(404, e.getMessage());
        } catch (SecurityException e) {
            return errorResponse(403, e.getMessage());
        } catch (Exception e) {
            return errorResponse(500, "删除失败: " + e.getMessage());
        }
    }

    /**
     * 置顶/取消置顶公告
     * 
     * POST /api/announcement/{announcementId}/pin?userId=用户ID
     */
    public Map<String, Object> togglePin(String announcementId, String userId) {
        try {
            GroupAnnouncement announcement = announcementService.togglePin(announcementId, userId);
            String message = announcement.isPinned() ? "置顶成功" : "取消置顶成功";
            return successResponse(announcement, message);
        } catch (IllegalArgumentException e) {
            return errorResponse(404, e.getMessage());
        } catch (SecurityException e) {
            return errorResponse(403, e.getMessage());
        } catch (Exception e) {
            return errorResponse(500, "操作失败: " + e.getMessage());
        }
    }

    // ==================== 公告查询 ====================

    /**
     * 获取公告详情
     * 
     * GET /api/announcement/{announcementId}
     */
    public Map<String, Object> getAnnouncement(String announcementId) {
        try {
            GroupAnnouncement announcement = announcementService.getAnnouncement(announcementId);
            return successResponse(announcement, "获取成功");
        } catch (IllegalArgumentException e) {
            return errorResponse(404, e.getMessage());
        } catch (Exception e) {
            return errorResponse(500, "获取失败: " + e.getMessage());
        }
    }

    /**
     * 获取群公告列表（分页）
     * 
     * GET /api/announcement/group/{groupId}?page=0&size=20
     */
    public Map<String, Object> getAnnouncements(String groupId, int page, int size) {
        try {
            List<GroupAnnouncement> announcements = announcementService.getAnnouncements(groupId, page, size);
            long total = announcementService.countAnnouncements(groupId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("announcements", announcements);
            result.put("total", total);
            result.put("page", page);
            result.put("size", size);
            
            return successResponse(result, "获取成功");
        } catch (Exception e) {
            return errorResponse(500, "获取失败: " + e.getMessage());
        }
    }

    /**
     * 获取置顶公告
     * 
     * GET /api/announcement/group/{groupId}/pinned
     */
    public Map<String, Object> getPinnedAnnouncements(String groupId) {
        try {
            List<GroupAnnouncement> announcements = announcementService.getPinnedAnnouncements(groupId);
            return successResponse(announcements, "获取成功");
        } catch (Exception e) {
            return errorResponse(500, "获取失败: " + e.getMessage());
        }
    }

    /**
     * 获取公告历史记录
     * 
     * GET /api/announcement/group/{groupId}/history?page=0&size=20
     */
    public Map<String, Object> getAnnouncementHistory(String groupId, int page, int size) {
        try {
            List<GroupAnnouncement> announcements = announcementService.getAnnouncementHistory(groupId, page, size);
            return successResponse(announcements, "获取成功");
        } catch (Exception e) {
            return errorResponse(500, "获取失败: " + e.getMessage());
        }
    }

    /**
     * 搜索公告
     * 
     * GET /api/announcement/group/{groupId}/search?keyword=关键词
     */
    public Map<String, Object> searchAnnouncements(String groupId, String keyword) {
        try {
            List<GroupAnnouncement> announcements = announcementService.searchAnnouncements(groupId, keyword);
            return successResponse(announcements, "搜索成功");
        } catch (Exception e) {
            return errorResponse(500, "搜索失败: " + e.getMessage());
        }
    }

    // ==================== 已读状态 ====================

    /**
     * 标记公告已读
     * 
     * POST /api/announcement/{announcementId}/read
     * 
     * Request Body:
     * {
     *   "userId": "用户ID"
     * }
     */
    public Map<String, Object> markAsRead(String announcementId, Map<String, Object> request) {
        try {
            String userId = (String) request.get("userId");
            AnnouncementReadStatus status = announcementService.markAsRead(announcementId, userId);
            return successResponse(status, "标记成功");
        } catch (IllegalArgumentException e) {
            return errorResponse(404, e.getMessage());
        } catch (Exception e) {
            return errorResponse(500, "标记失败: " + e.getMessage());
        }
    }

    /**
     * 批量标记已读
     * 
     * POST /api/announcement/batch-read
     * 
     * Request Body:
     * {
     *   "groupId": "群ID",
     *   "userId": "用户ID",
     *   "announcementIds": ["公告ID1", "公告ID2"]
     * }
     */
    public Map<String, Object> batchMarkAsRead(Map<String, Object> request) {
        try {
            String groupId = (String) request.get("groupId");
            String userId = (String) request.get("userId");
            List<String> announcementIds = (List<String>) request.get("announcementIds");
            
            List<AnnouncementReadStatus> statuses = announcementService.batchMarkAsRead(
                    groupId, userId, announcementIds);
            return successResponse(statuses, "批量标记成功");
        } catch (Exception e) {
            return errorResponse(500, "批量标记失败: " + e.getMessage());
        }
    }

    /**
     * 检查公告是否已读
     * 
     * GET /api/announcement/{announcementId}/read?userId=用户ID
     */
    public Map<String, Object> checkReadStatus(String announcementId, String userId) {
        try {
            boolean isRead = announcementService.isAnnouncementRead(announcementId, userId);
            Map<String, Object> result = new HashMap<>();
            result.put("announcementId", announcementId);
            result.put("userId", userId);
            result.put("read", isRead);
            return successResponse(result, "查询成功");
        } catch (Exception e) {
            return errorResponse(500, "查询失败: " + e.getMessage());
        }
    }

    /**
     * 批量检查已读状态
     * 
     * POST /api/announcement/batch-check-read
     * 
     * Request Body:
     * {
     *   "userId": "用户ID",
     *   "announcementIds": ["公告ID1", "公告ID2"]
     * }
     */
    public Map<String, Object> batchCheckReadStatus(Map<String, Object> request) {
        try {
            String userId = (String) request.get("userId");
            List<String> announcementIds = (List<String>) request.get("announcementIds");
            
            Map<String, Boolean> statuses = announcementService.batchCheckReadStatus(userId, announcementIds);
            return successResponse(statuses, "查询成功");
        } catch (Exception e) {
            return errorResponse(500, "查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取未读公告数
     * 
     * GET /api/announcement/group/{groupId}/unread?userId=用户ID
     */
    public Map<String, Object> getUnreadCount(String groupId, String userId) {
        try {
            int count = announcementService.getUnreadCount(groupId, userId);
            Map<String, Object> result = new HashMap<>();
            result.put("groupId", groupId);
            result.put("userId", userId);
            result.put("unreadCount", count);
            return successResponse(result, "获取成功");
        } catch (Exception e) {
            return errorResponse(500, "获取失败: " + e.getMessage());
        }
    }

    /**
     * 获取未读公告列表
     * 
     * GET /api/announcement/group/{groupId}/unread/list?userId=用户ID
     */
    public Map<String, Object> getUnreadAnnouncements(String groupId, String userId) {
        try {
            List<GroupAnnouncement> announcements = announcementService.getUnreadAnnouncements(groupId, userId);
            return successResponse(announcements, "获取成功");
        } catch (Exception e) {
            return errorResponse(500, "获取失败: " + e.getMessage());
        }
    }

    /**
     * 获取公告已读统计
     * 
     * GET /api/announcement/{announcementId}/stats?totalMembers=总人数
     */
    public Map<String, Object> getReadStats(String announcementId, int totalMembers) {
        try {
            int readCount = announcementService.getReadCount(announcementId);
            int unreadCount = announcementService.getUnreadCountByAnnouncement(announcementId, totalMembers);
            
            Map<String, Object> result = new HashMap<>();
            result.put("announcementId", announcementId);
            result.put("readCount", readCount);
            result.put("unreadCount", unreadCount);
            result.put("totalMembers", totalMembers);
            
            return successResponse(result, "获取成功");
        } catch (Exception e) {
            return errorResponse(500, "获取失败: " + e.getMessage());
        }
    }

    // ==================== 响应辅助方法 ====================

    private Map<String, Object> successResponse(Object data, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);
        response.put("message", message);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    private Map<String, Object> errorResponse(int code, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("code", code);
        response.put("message", message);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}
