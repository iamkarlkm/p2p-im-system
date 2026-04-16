package com.im.service.group.controller;

import com.im.common.base.Result;
import com.im.service.group.dto.AnnouncementResponse;
import com.im.service.group.dto.CreateAnnouncementRequest;
import com.im.service.group.service.GroupAnnouncementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 群公告控制器
 * 提供群公告的 REST API 接口
 * 
 * @author IM Team
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/group-announcement")
@RequiredArgsConstructor
public class GroupAnnouncementController {

    private final GroupAnnouncementService announcementService;

    /**
     * 创建群公告
     * POST /api/group-announcement
     */
    @PostMapping
    public ResponseEntity<Result<AnnouncementResponse>> createAnnouncement(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody CreateAnnouncementRequest request) {
        log.info("用户 {} 创建群公告: 群 {} 标题 {}", userId, request.getGroupId(), request.getTitle());
        AnnouncementResponse response = announcementService.createAnnouncement(userId, request);
        return ResponseEntity.ok(Result.success(response));
    }

    /**
     * 获取群公告列表
     * GET /api/group-announcement/group/{groupId}
     */
    @GetMapping("/group/{groupId}")
    public ResponseEntity<Result<List<AnnouncementResponse>>> getAnnouncements(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String groupId) {
        log.info("用户 {} 获取群 {} 的公告列表", userId, groupId);
        List<AnnouncementResponse> announcements = announcementService.getAnnouncements(groupId, userId);
        return ResponseEntity.ok(Result.success(announcements));
    }

    /**
     * 分页获取群公告
     * GET /api/group-announcement/group/{groupId}/paged
     */
    @GetMapping("/group/{groupId}/paged")
    public ResponseEntity<Result<Page<AnnouncementResponse>>> getAnnouncementsPaged(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("用户 {} 分页获取群 {} 的公告, page={}", userId, groupId, page);
        Page<AnnouncementResponse> announcements = announcementService.getAnnouncementsPaged(groupId, userId, page, size);
        return ResponseEntity.ok(Result.success(announcements));
    }

    /**
     * 获取群最新公告
     * GET /api/group-announcement/group/{groupId}/latest
     */
    @GetMapping("/group/{groupId}/latest")
    public ResponseEntity<Result<AnnouncementResponse>> getLatestAnnouncement(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String groupId) {
        log.info("用户 {} 获取群 {} 的最新公告", userId, groupId);
        AnnouncementResponse announcement = announcementService.getLatestAnnouncement(groupId, userId);
        return ResponseEntity.ok(Result.success(announcement));
    }

    /**
     * 获取置顶公告
     * GET /api/group-announcement/group/{groupId}/pinned
     */
    @GetMapping("/group/{groupId}/pinned")
    public ResponseEntity<Result<List<AnnouncementResponse>>> getPinnedAnnouncements(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String groupId) {
        log.info("用户 {} 获取群 {} 的置顶公告", userId, groupId);
        List<AnnouncementResponse> announcements = announcementService.getPinnedAnnouncements(groupId, userId);
        return ResponseEntity.ok(Result.success(announcements));
    }

    /**
     * 获取公告详情
     * GET /api/group-announcement/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Result<AnnouncementResponse>> getAnnouncementById(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String id) {
        log.info("用户 {} 获取公告 {}", userId, id);
        AnnouncementResponse announcement = announcementService.getAnnouncementById(id, userId);
        return ResponseEntity.ok(Result.success(announcement));
    }

    /**
     * 删除公告
     * DELETE /api/group-announcement/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Result<Void>> deleteAnnouncement(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String id) {
        log.info("用户 {} 删除公告 {}", userId, id);
        announcementService.deleteAnnouncement(id, userId);
        return ResponseEntity.ok(Result.success());
    }

    /**
     * 置顶公告
     * POST /api/group-announcement/{id}/pin
     */
    @PostMapping("/{id}/pin")
    public ResponseEntity<Result<Void>> pinAnnouncement(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String id) {
        log.info("用户 {} 置顶公告 {}", userId, id);
        announcementService.pinAnnouncement(id, userId);
        return ResponseEntity.ok(Result.success());
    }

    /**
     * 取消置顶
     * DELETE /api/group-announcement/{id}/pin
     */
    @DeleteMapping("/{id}/pin")
    public ResponseEntity<Result<Void>> unpinAnnouncement(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String id) {
        log.info("用户 {} 取消置顶公告 {}", userId, id);
        announcementService.unpinAnnouncement(id, userId);
        return ResponseEntity.ok(Result.success());
    }

    /**
     * 标记已读
     * POST /api/group-announcement/{id}/read
     */
    @PostMapping("/{id}/read")
    public ResponseEntity<Result<Void>> markAsRead(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String id) {
        log.info("用户 {} 标记公告 {} 已读", userId, id);
        announcementService.markAsRead(id);
        return ResponseEntity.ok(Result.success());
    }

    /**
     * 获取未读数量
     * GET /api/group-announcement/group/{groupId}/unread-count
     */
    @GetMapping("/group/{groupId}/unread-count")
    public ResponseEntity<Result<Map<String, Long>>> getUnreadCount(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String groupId) {
        log.info("用户 {} 获取群 {} 的公告未读数", userId, groupId);
        Long count = announcementService.getUnreadCount(groupId, userId);
        return ResponseEntity.ok(Result.success(Map.of("count", count)));
    }
}
