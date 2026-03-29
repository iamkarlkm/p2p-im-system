package com.im.backend.controller;

import com.im.backend.dto.AnnouncementDTO;
import com.im.backend.dto.AnnouncementResponseDTO;
import com.im.backend.dto.ApiResponse;
import com.im.backend.security.CurrentUser;
import com.im.backend.security.UserPrincipal;
import com.im.backend.service.GroupAnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 群公告控制器
 */
@RestController
@RequestMapping("/api/groups/{groupId}/announcements")
@Tag(name = "群公告管理", description = "群组公告的发布、编辑、撤回、阅读回执等")
public class GroupAnnouncementController {

    private static final Logger logger = LoggerFactory.getLogger(GroupAnnouncementController.class);

    @Autowired
    private GroupAnnouncementService announcementService;

    /**
     * 发布公告
     */
    @PostMapping
    @Operation(summary = "发布公告", description = "在指定群组发布新公告")
    public ResponseEntity<ApiResponse<AnnouncementResponseDTO>> publishAnnouncement(
            @Parameter(description = "群组ID") @PathVariable Long groupId,
            @Valid @RequestBody AnnouncementDTO dto,
            @CurrentUser UserPrincipal currentUser) {
        
        logger.info("发布公告请求: groupId={}, userId={}", groupId, currentUser.getId());
        dto.setGroupId(groupId);
        
        AnnouncementResponseDTO result = announcementService.publishAnnouncement(currentUser.getId(), dto);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 编辑公告
     */
    @PutMapping("/{announcementId}")
    @Operation(summary = "编辑公告", description = "编辑指定公告的内容")
    public ResponseEntity<ApiResponse<AnnouncementResponseDTO>> editAnnouncement(
            @Parameter(description = "群组ID") @PathVariable Long groupId,
            @Parameter(description = "公告ID") @PathVariable Long announcementId,
            @Valid @RequestBody AnnouncementDTO dto,
            @CurrentUser UserPrincipal currentUser) {
        
        logger.info("编辑公告请求: groupId={}, announcementId={}, userId={}", 
                groupId, announcementId, currentUser.getId());
        dto.setGroupId(groupId);
        
        AnnouncementResponseDTO result = announcementService.editAnnouncement(announcementId, currentUser.getId(), dto);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 撤回公告
     */
    @PostMapping("/{announcementId}/withdraw")
    @Operation(summary = "撤回公告", description = "撤回已发布的公告")
    public ResponseEntity<ApiResponse<Void>> withdrawAnnouncement(
            @Parameter(description = "群组ID") @PathVariable Long groupId,
            @Parameter(description = "公告ID") @PathVariable Long announcementId,
            @RequestBody(required = false) WithdrawRequest request,
            @CurrentUser UserPrincipal currentUser) {
        
        logger.info("撤回公告请求: groupId={}, announcementId={}, userId={}", 
                groupId, announcementId, currentUser.getId());
        
        String reason = request != null ? request.getReason() : null;
        announcementService.withdrawAnnouncement(announcementId, currentUser.getId(), reason);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 获取公告详情
     */
    @GetMapping("/{announcementId}")
    @Operation(summary = "获取公告详情", description = "获取指定公告的详细信息")
    public ResponseEntity<ApiResponse<AnnouncementResponseDTO>> getAnnouncementDetail(
            @Parameter(description = "群组ID") @PathVariable Long groupId,
            @Parameter(description = "公告ID") @PathVariable Long announcementId,
            @CurrentUser UserPrincipal currentUser) {
        
        AnnouncementResponseDTO result = announcementService.getAnnouncementDetail(announcementId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 获取群组公告列表
     */
    @GetMapping
    @Operation(summary = "获取公告列表", description = "获取群组的所有公告列表")
    public ResponseEntity<ApiResponse<List<AnnouncementResponseDTO>>> getGroupAnnouncements(
            @Parameter(description = "群组ID") @PathVariable Long groupId,
            @CurrentUser UserPrincipal currentUser) {
        
        List<AnnouncementResponseDTO> result = announcementService.getGroupAnnouncements(groupId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 分页获取群组公告
     */
    @GetMapping("/page")
    @Operation(summary = "分页获取公告", description = "分页获取群组公告列表")
    public ResponseEntity<ApiResponse<Page<AnnouncementResponseDTO>>> getGroupAnnouncementsPageable(
            @Parameter(description = "群组ID") @PathVariable Long groupId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @CurrentUser UserPrincipal currentUser) {
        
        Page<AnnouncementResponseDTO> result = announcementService.getGroupAnnouncementsPageable(
                groupId, currentUser.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 标记公告为已读
     */
    @PostMapping("/{announcementId}/read")
    @Operation(summary = "标记已读", description = "将公告标记为已读")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @Parameter(description = "群组ID") @PathVariable Long groupId,
            @Parameter(description = "公告ID") @PathVariable Long announcementId,
            @CurrentUser UserPrincipal currentUser) {
        
        announcementService.markAsRead(announcementId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 批量标记已读
     */
    @PostMapping("/read/batch")
    @Operation(summary = "批量标记已读", description = "批量将公告标记为已读")
    public ResponseEntity<ApiResponse<Void>> markMultipleAsRead(
            @Parameter(description = "群组ID") @PathVariable Long groupId,
            @RequestBody BatchReadRequest request,
            @CurrentUser UserPrincipal currentUser) {
        
        announcementService.markMultipleAsRead(request.getAnnouncementIds(), currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 确认公告
     */
    @PostMapping("/{announcementId}/confirm")
    @Operation(summary = "确认公告", description = "确认已阅读公告")
    public ResponseEntity<ApiResponse<Void>> confirmAnnouncement(
            @Parameter(description = "群组ID") @PathVariable Long groupId,
            @Parameter(description = "公告ID") @PathVariable Long announcementId,
            @CurrentUser UserPrincipal currentUser) {
        
        announcementService.confirmAnnouncement(announcementId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 获取已读用户列表
     */
    @GetMapping("/{announcementId}/read-users")
    @Operation(summary = "获取已读用户", description = "获取已阅读公告的用户列表（管理员）")
    public ResponseEntity<ApiResponse<Set<Long>>> getReadUserIds(
            @Parameter(description = "群组ID") @PathVariable Long groupId,
            @Parameter(description = "公告ID") @PathVariable Long announcementId,
            @CurrentUser UserPrincipal currentUser) {
        
        Set<Long> result = announcementService.getReadUserIds(announcementId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 获取确认用户列表
     */
    @GetMapping("/{announcementId}/confirm-users")
    @Operation(summary = "获取确认用户", description = "获取已确认公告的用户列表（管理员）")
    public ResponseEntity<ApiResponse<Set<Long>>> getConfirmedUserIds(
            @Parameter(description = "群组ID") @PathVariable Long groupId,
            @Parameter(description = "公告ID") @PathVariable Long announcementId,
            @CurrentUser UserPrincipal currentUser) {
        
        Set<Long> result = announcementService.getConfirmedUserIds(announcementId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 获取未读公告数
     */
    @GetMapping("/unread-count")
    @Operation(summary = "获取未读数", description = "获取当前用户未读的公告数量")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(
            @Parameter(description = "群组ID") @PathVariable Long groupId,
            @CurrentUser UserPrincipal currentUser) {
        
        Long count = announcementService.getUnreadCount(groupId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    /**
     * 获取生效中的公告
     */
    @GetMapping("/active")
    @Operation(summary = "获取生效公告", description = "获取群组当前生效中的公告")
    public ResponseEntity<ApiResponse<List<AnnouncementResponseDTO>>> getActiveAnnouncements(
            @Parameter(description = "群组ID") @PathVariable Long groupId,
            @CurrentUser UserPrincipal currentUser) {
        
        List<AnnouncementResponseDTO> result = announcementService.getActiveAnnouncements(groupId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 获取置顶公告
     */
    @GetMapping("/pinned")
    @Operation(summary = "获取置顶公告", description = "获取群组的置顶公告")
    public ResponseEntity<ApiResponse<List<AnnouncementResponseDTO>>> getPinnedAnnouncements(
            @Parameter(description = "群组ID") @PathVariable Long groupId,
            @CurrentUser UserPrincipal currentUser) {
        
        List<AnnouncementResponseDTO> result = announcementService.getPinnedAnnouncements(groupId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 搜索公告
     */
    @GetMapping("/search")
    @Operation(summary = "搜索公告", description = "按标题搜索公告")
    public ResponseEntity<ApiResponse<List<AnnouncementResponseDTO>>> searchAnnouncements(
            @Parameter(description = "群组ID") @PathVariable Long groupId,
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @CurrentUser UserPrincipal currentUser) {
        
        List<AnnouncementResponseDTO> result = announcementService.searchAnnouncements(groupId, keyword, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 删除公告（管理员）
     */
    @DeleteMapping("/{announcementId}")
    @Operation(summary = "删除公告", description = "永久删除公告（仅群主）")
    public ResponseEntity<ApiResponse<Void>> deleteAnnouncement(
            @Parameter(description = "群组ID") @PathVariable Long groupId,
            @Parameter(description = "公告ID") @PathVariable Long announcementId,
            @CurrentUser UserPrincipal currentUser) {
        
        announcementService.deleteAnnouncement(announcementId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // ========== 内部请求类 ==========

    /**
     * 撤回请求
     */
    public static class WithdrawRequest {
        private String reason;

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }

    /**
     * 批量阅读请求
     */
    public static class BatchReadRequest {
        private List<Long> announcementIds;

        public List<Long> getAnnouncementIds() {
            return announcementIds;
        }

        public void setAnnouncementIds(List<Long> announcementIds) {
            this.announcementIds = announcementIds;
        }
    }
}
