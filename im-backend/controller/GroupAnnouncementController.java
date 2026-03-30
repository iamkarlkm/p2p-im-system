package com.im.controller;

import com.im.dto.GroupAnnouncementRequest;
import com.im.dto.GroupAnnouncementResponse;
import com.im.service.GroupAnnouncementService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 群公告控制器
 * 功能ID: #30
 * 功能名称: 群公告
 * 
 * @author developer-agent
 * @since 2026-03-30
 */
@RestController
@RequestMapping("/api/group-announcement")
public class GroupAnnouncementController {

    private static final Logger logger = LoggerFactory.getLogger(GroupAnnouncementController.class);

    @Autowired
    private GroupAnnouncementService announcementService;

    /**
     * 创建群公告
     * POST /api/group-announcement/create
     */
    @PostMapping("/create")
    public ResponseEntity<GroupAnnouncementResponse> createAnnouncement(
            @Valid @RequestBody GroupAnnouncementRequest request,
            @RequestAttribute("userId") Long userId) {
        logger.info("Creating announcement for group: {}, by user: {}", request.getGroupId(), userId);
        GroupAnnouncementResponse response = announcementService.createAnnouncement(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 更新群公告
     * PUT /api/group-announcement/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<GroupAnnouncementResponse> updateAnnouncement(
            @PathVariable Long id,
            @Valid @RequestBody GroupAnnouncementRequest request,
            @RequestAttribute("userId") Long userId) {
        logger.info("Updating announcement: {}, by user: {}", id, userId);
        GroupAnnouncementResponse response = announcementService.updateAnnouncement(id, request, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 删除群公告
     * DELETE /api/group-announcement/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnnouncement(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        logger.info("Deleting announcement: {}, by user: {}", id, userId);
        announcementService.deleteAnnouncement(id, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 获取公告详情
     * GET /api/group-announcement/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<GroupAnnouncementResponse> getAnnouncement(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        logger.info("Getting announcement: {}, by user: {}", id, userId);
        GroupAnnouncementResponse response = announcementService.getAnnouncement(id, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取群组的公告列表
     * GET /api/group-announcement/group/{groupId}
     */
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<GroupAnnouncementResponse>> getGroupAnnouncements(
            @PathVariable Long groupId,
            @RequestAttribute("userId") Long userId) {
        logger.info("Getting announcements for group: {}, by user: {}", groupId, userId);
        List<GroupAnnouncementResponse> responses = announcementService.getGroupAnnouncements(groupId, userId);
        return ResponseEntity.ok(responses);
    }

    /**
     * 分页获取群公告
     * GET /api/group-announcement/group/{groupId}/paged
     */
    @GetMapping("/group/{groupId}/paged")
    public ResponseEntity<Page<GroupAnnouncementResponse>> getGroupAnnouncementsPaged(
            @PathVariable Long groupId,
            @RequestAttribute("userId") Long userId,
            @PageableDefault(size = 20) Pageable pageable) {
        logger.info("Getting paged announcements for group: {}, by user: {}", groupId, userId);
        Page<GroupAnnouncementResponse> page = announcementService.getGroupAnnouncementsPaged(groupId, userId, pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * 获取最新公告
     * GET /api/group-announcement/group/{groupId}/latest
     */
    @GetMapping("/group/{groupId}/latest")
    public ResponseEntity<GroupAnnouncementResponse> getLatestAnnouncement(
            @PathVariable Long groupId,
            @RequestAttribute("userId") Long userId) {
        logger.info("Getting latest announcement for group: {}, by user: {}", groupId, userId);
        GroupAnnouncementResponse response = announcementService.getLatestAnnouncement(groupId, userId);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    /**
     * 获取置顶公告
     * GET /api/group-announcement/group/{groupId}/pinned
     */
    @GetMapping("/group/{groupId}/pinned")
    public ResponseEntity<List<GroupAnnouncementResponse>> getPinnedAnnouncements(
            @PathVariable Long groupId,
            @RequestAttribute("userId") Long userId) {
        logger.info("Getting pinned announcements for group: {}, by user: {}", groupId, userId);
        List<GroupAnnouncementResponse> responses = announcementService.getPinnedAnnouncements(groupId, userId);
        return ResponseEntity.ok(responses);
    }

    /**
     * 标记公告已读
     * POST /api/group-announcement/{id}/read
     */
    @PostMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        logger.info("Marking announcement {} as read by user {}", id, userId);
        announcementService.markAsRead(id, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * 批量标记已读
     * POST /api/group-announcement/group/{groupId}/read-all
     */
    @PostMapping("/group/{groupId}/read-all")
    public ResponseEntity<Void> markAllAsRead(
            @PathVariable Long groupId,
            @RequestAttribute("userId") Long userId) {
        logger.info("Marking all announcements as read for group {} by user {}", groupId, userId);
        announcementService.markAllAsRead(groupId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * 置顶/取消置顶公告
     * POST /api/group-announcement/{id}/pin
     */
    @PostMapping("/{id}/pin")
    public ResponseEntity<Void> pinAnnouncement(
            @PathVariable Long id,
            @RequestParam Boolean pinned,
            @RequestAttribute("userId") Long userId) {
        logger.info("Pinning announcement: {}, pinned: {}, by user: {}", id, pinned, userId);
        announcementService.pinAnnouncement(id, pinned, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * 获取已读人数
     * GET /api/group-announcement/{id}/read-count
     */
    @GetMapping("/{id}/read-count")
    public ResponseEntity<Map<String, Object>> getReadCount(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        logger.info("Getting read count for announcement: {}", id);
        Integer count = announcementService.getReadCount(id);
        Map<String, Object> result = new HashMap<>();
        result.put("readCount", count);
        return ResponseEntity.ok(result);
    }

    /**
     * 搜索公告
     * GET /api/group-announcement/group/{groupId}/search
     */
    @GetMapping("/group/{groupId}/search")
    public ResponseEntity<List<GroupAnnouncementResponse>> searchAnnouncements(
            @PathVariable Long groupId,
            @RequestParam String keyword,
            @RequestAttribute("userId") Long userId) {
        logger.info("Searching announcements for group: {}, keyword: {}", groupId, keyword);
        List<GroupAnnouncementResponse> responses = announcementService.searchAnnouncements(groupId, keyword, userId);
        return ResponseEntity.ok(responses);
    }

    /**
     * 获取未读公告数量
     * GET /api/group-announcement/group/{groupId}/unread-count
     */
    @GetMapping("/group/{groupId}/unread-count")
    public ResponseEntity<Map<String, Object>> getUnreadCount(
            @PathVariable Long groupId,
            @RequestAttribute("userId") Long userId) {
        logger.info("Getting unread count for group: {} by user {}", groupId, userId);
        Long count = announcementService.getUnreadCount(groupId, userId);
        Map<String, Object> result = new HashMap<>();
        result.put("unreadCount", count);
        return ResponseEntity.ok(result);
    }

    /**
     * 检查用户是否为创建者
     * GET /api/group-announcement/{id}/is-creator
     */
    @GetMapping("/{id}/is-creator")
    public ResponseEntity<Map<String, Object>> isCreator(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        boolean isCreator = announcementService.isCreator(id, userId);
        Map<String, Object> result = new HashMap<>();
        result.put("isCreator", isCreator);
        return ResponseEntity.ok(result);
    }
}
