package com.im.backend.controller;

import com.im.backend.dto.AnnouncementCreateRequest;
import com.im.backend.dto.AnnouncementDTO;
import com.im.backend.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    /** 发布公告 */
    @PostMapping
    public ResponseEntity<AnnouncementDTO> publish(@Valid @RequestBody AnnouncementCreateRequest request) {
        return ResponseEntity.ok(announcementService.publish(request));
    }

    /** 获取群公告列表 */
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<AnnouncementDTO>> getGroupAnnouncements(
            @PathVariable Long groupId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        return ResponseEntity.ok(announcementService.getGroupAnnouncements(groupId, userId));
    }

    /** 分页获取群公告 */
    @GetMapping("/group/{groupId}/page")
    public ResponseEntity<Page<AnnouncementDTO>> getGroupAnnouncementsPaged(
            @PathVariable Long groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        return ResponseEntity.ok(announcementService.getGroupAnnouncementsPaged(groupId, page, size, userId));
    }

    /** 标记已读 */
    @PostMapping("/{announcementId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long announcementId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader(value = "X-Device-Type", required = false) String deviceType) {
        announcementService.markAsRead(announcementId, userId, deviceType);
        return ResponseEntity.ok().build();
    }

    /** 确认紧急公告 */
    @PostMapping("/{announcementId}/confirm")
    public ResponseEntity<Void> confirmAnnouncement(
            @PathVariable Long announcementId,
            @RequestHeader("X-User-Id") Long userId) {
        announcementService.confirmAnnouncement(announcementId, userId);
        return ResponseEntity.ok().build();
    }

    /** 获取已读统计 */
    @GetMapping("/{announcementId}/stats")
    public ResponseEntity<Map<String, Object>> getReadStats(
            @PathVariable Long announcementId,
            @RequestParam(defaultValue = "0") int totalMembers) {
        return ResponseEntity.ok(announcementService.getReadStats(announcementId, totalMembers));
    }

    /** 撤销公告 */
    @DeleteMapping("/{announcementId}")
    public ResponseEntity<Void> revokeAnnouncement(
            @PathVariable Long announcementId,
            @RequestHeader("X-User-Id") Long userId) {
        announcementService.revokeAnnouncement(announcementId, userId);
        return ResponseEntity.noContent().build();
    }
}
