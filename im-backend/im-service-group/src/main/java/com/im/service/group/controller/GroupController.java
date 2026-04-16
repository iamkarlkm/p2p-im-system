package com.im.service.group.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.im.service.group.dto.*;
import com.im.service.group.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 群组控制器
 * 提供群组管理和成员管理的REST API端点
 *
 * @author IM System
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    // ==================== 群组管理 ====================

    /**
     * 创建群组
     *
     * @param request 创建请求
     * @param userId  当前用户ID（从Token获取，这里简化处理）
     * @return 创建的群组信息
     */
    @PostMapping
    public ResponseEntity<?> createGroup(@Valid @RequestBody CreateGroupRequest request,
                                         @RequestAttribute("userId") Long userId) {
        log.info("Creating group: {}, by user: {}", request.getName(), userId);
        try {
            GroupResponse response = groupService.createGroup(request, userId);
            return ResponseEntity.ok(createSuccessResponse(response));
        } catch (Exception e) {
            log.error("Failed to create group: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 获取群组详情
     *
     * @param groupId 群组ID
     * @param userId  当前用户ID
     * @return 群组详情
     */
    @GetMapping("/{groupId}")
    public ResponseEntity<?> getGroupDetail(@PathVariable Long groupId,
                                            @RequestAttribute("userId") Long userId) {
        log.info("Getting group detail: {}, by user: {}", groupId, userId);
        try {
            GroupResponse response = groupService.getGroupDetail(groupId, userId);
            return ResponseEntity.ok(createSuccessResponse(response));
        } catch (Exception e) {
            log.error("Failed to get group detail: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 更新群组信息
     *
     * @param groupId 群组ID
     * @param request 更新请求
     * @param userId  当前用户ID
     * @return 更新后的群组信息
     */
    @PutMapping("/{groupId}")
    public ResponseEntity<?> updateGroup(@PathVariable Long groupId,
                                         @Valid @RequestBody UpdateGroupRequest request,
                                         @RequestAttribute("userId") Long userId) {
        log.info("Updating group: {}, by user: {}", groupId, userId);
        try {
            GroupResponse response = groupService.updateGroup(groupId, request, userId);
            return ResponseEntity.ok(createSuccessResponse(response));
        } catch (Exception e) {
            log.error("Failed to update group: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 解散群组
     *
     * @param groupId 群组ID
     * @param userId  当前用户ID
     * @return 操作结果
     */
    @DeleteMapping("/{groupId}")
    public ResponseEntity<?> dissolveGroup(@PathVariable Long groupId,
                                           @RequestAttribute("userId") Long userId) {
        log.info("Dissolving group: {}, by user: {}", groupId, userId);
        try {
            groupService.dissolveGroup(groupId, userId);
            return ResponseEntity.ok(createSuccessResponse("群组已解散"));
        } catch (Exception e) {
            log.error("Failed to dissolve group: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 搜索群组
     *
     * @param keyword  搜索关键字
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 搜索结果
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchGroups(@RequestParam String keyword,
                                          @RequestParam(defaultValue = "1") int pageNum,
                                          @RequestParam(defaultValue = "20") int pageSize) {
        log.info("Searching groups: keyword={}, page={}", keyword, pageNum);
        try {
            IPage<GroupResponse> page = groupService.searchGroups(keyword, pageNum, pageSize);
            return ResponseEntity.ok(createSuccessResponse(page));
        } catch (Exception e) {
            log.error("Failed to search groups: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 获取我创建的群组
     *
     * @param userId   当前用户ID
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 群组列表
     */
    @GetMapping("/my/created")
    public ResponseEntity<?> getMyCreatedGroups(@RequestAttribute("userId") Long userId,
                                                @RequestParam(defaultValue = "1") int pageNum,
                                                @RequestParam(defaultValue = "20") int pageSize) {
        log.info("Getting my created groups: userId={}, page={}", userId, pageNum);
        try {
            IPage<GroupResponse> page = groupService.getOwnedGroups(userId, pageNum, pageSize);
            return ResponseEntity.ok(createSuccessResponse(page));
        } catch (Exception e) {
            log.error("Failed to get my created groups: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 获取我加入的群组
     *
     * @param userId   当前用户ID
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 群组列表
     */
    @GetMapping("/my/joined")
    public ResponseEntity<?> getMyJoinedGroups(@RequestAttribute("userId") Long userId,
                                               @RequestParam(defaultValue = "1") int pageNum,
                                               @RequestParam(defaultValue = "20") int pageSize) {
        log.info("Getting my joined groups: userId={}, page={}", userId, pageNum);
        try {
            IPage<GroupResponse> page = groupService.getJoinedGroups(userId, pageNum, pageSize);
            return ResponseEntity.ok(createSuccessResponse(page));
        } catch (Exception e) {
            log.error("Failed to get my joined groups: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // ==================== 公告管理 ====================

    /**
     * 更新群公告
     *
     * @param groupId    群组ID
     * @param content    公告内容
     * @param userId     当前用户ID
     * @return 更新后的群组信息
     */
    @PostMapping("/{groupId}/announcement")
    public ResponseEntity<?> updateAnnouncement(@PathVariable Long groupId,
                                                @RequestParam String content,
                                                @RequestAttribute("userId") Long userId) {
        log.info("Updating group announcement: groupId={}, by user: {}", groupId, userId);
        try {
            GroupResponse response = groupService.updateAnnouncement(groupId, content, userId);
            return ResponseEntity.ok(createSuccessResponse(response));
        } catch (Exception e) {
            log.error("Failed to update announcement: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 清除群公告
     *
     * @param groupId 群组ID
     * @param userId  当前用户ID
     * @return 操作结果
     */
    @DeleteMapping("/{groupId}/announcement")
    public ResponseEntity<?> clearAnnouncement(@PathVariable Long groupId,
                                               @RequestAttribute("userId") Long userId) {
        log.info("Clearing group announcement: groupId={}, by user: {}", groupId, userId);
        try {
            GroupResponse response = groupService.updateAnnouncement(groupId, null, userId);
            return ResponseEntity.ok(createSuccessResponse(response));
        } catch (Exception e) {
            log.error("Failed to clear announcement: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // ==================== 禁言管理 ====================

    /**
     * 全员禁言
     *
     * @param groupId         群组ID
     * @param durationMinutes 禁言时长（分钟）
     * @param userId          当前用户ID
     * @return 操作结果
     */
    @PostMapping("/{groupId}/mute-all")
    public ResponseEntity<?> muteAll(@PathVariable Long groupId,
                                     @RequestParam(required = false) Integer durationMinutes,
                                     @RequestAttribute("userId") Long userId) {
        log.info("Muting all members: groupId={}, duration={}, by user: {}", groupId, durationMinutes, userId);
        try {
            groupService.muteAll(groupId, durationMinutes, userId);
            return ResponseEntity.ok(createSuccessResponse("全员禁言已开启"));
        } catch (Exception e) {
            log.error("Failed to mute all: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 取消全员禁言
     *
     * @param groupId 群组ID
     * @param userId  当前用户ID
     * @return 操作结果
     */
    @DeleteMapping("/{groupId}/mute-all")
    public ResponseEntity<?> unmuteAll(@PathVariable Long groupId,
                                       @RequestAttribute("userId") Long userId) {
        log.info("Unmuting all members: groupId={}, by user: {}", groupId, userId);
        try {
            groupService.unmuteAll(groupId, userId);
            return ResponseEntity.ok(createSuccessResponse("全员禁言已取消"));
        } catch (Exception e) {
            log.error("Failed to unmute all: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // ==================== 成员管理 ====================

    /**
     * 添加群成员
     *
     * @param groupId 群组ID
     * @param request 添加请求
     * @param userId  当前用户ID
     * @return 添加的成员列表
     */
    @PostMapping("/{groupId}/members")
    public ResponseEntity<?> addMembers(@PathVariable Long groupId,
                                        @Valid @RequestBody AddGroupMemberRequest request,
                                        @RequestAttribute("userId") Long userId) {
        log.info("Adding members to group: groupId={}, count={}, by user: {}",
                groupId, request.getUserIds().size(), userId);
        try {
            List<GroupMemberResponse> responses = groupService.addMembers(groupId, request, userId);
            return ResponseEntity.ok(createSuccessResponse(responses));
        } catch (Exception e) {
            log.error("Failed to add members: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 移除群成员
     *
     * @param groupId 群组ID
     * @param userId  要移除的用户ID
     * @param operatorId 当前用户ID
     * @return 操作结果
     */
    @DeleteMapping("/{groupId}/members/{userId}")
    public ResponseEntity<?> removeMember(@PathVariable Long groupId,
                                          @PathVariable Long userId,
                                          @RequestAttribute("userId") Long operatorId) {
        log.info("Removing member from group: groupId={}, userId={}, by user: {}",
                groupId, userId, operatorId);
        try {
            groupService.removeMember(groupId, userId, operatorId);
            return ResponseEntity.ok(createSuccessResponse("成员已移除"));
        } catch (Exception e) {
            log.error("Failed to remove member: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 退出群组
     *
     * @param groupId 群组ID
     * @param userId  当前用户ID
     * @return 操作结果
     */
    @PostMapping("/{groupId}/leave")
    public ResponseEntity<?> leaveGroup(@PathVariable Long groupId,
                                        @RequestAttribute("userId") Long userId) {
        log.info("Leaving group: groupId={}, user: {}", groupId, userId);
        try {
            groupService.leaveGroup(groupId, userId);
            return ResponseEntity.ok(createSuccessResponse("已退出群组"));
        } catch (Exception e) {
            log.error("Failed to leave group: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 获取群成员列表
     *
     * @param groupId  群组ID
     * @param userId   当前用户ID
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 成员列表
     */
    @GetMapping("/{groupId}/members")
    public ResponseEntity<?> getGroupMembers(@PathVariable Long groupId,
                                             @RequestAttribute("userId") Long userId,
                                             @RequestParam(defaultValue = "1") int pageNum,
                                             @RequestParam(defaultValue = "20") int pageSize) {
        log.info("Getting group members: groupId={}, page={}", groupId, pageNum);
        try {
            IPage<GroupMemberResponse> page = groupService.getGroupMembers(groupId, userId, pageNum, pageSize);
            return ResponseEntity.ok(createSuccessResponse(page));
        } catch (Exception e) {
            log.error("Failed to get group members: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 获取群成员详情
     *
     * @param groupId 群组ID
     * @param userId  用户ID
     * @param operatorId 当前用户ID
     * @return 成员详情
     */
    @GetMapping("/{groupId}/members/{userId}")
    public ResponseEntity<?> getMemberDetail(@PathVariable Long groupId,
                                             @PathVariable Long userId,
                                             @RequestAttribute("userId") Long operatorId) {
        log.info("Getting member detail: groupId={}, userId={}", groupId, userId);
        try {
            GroupMemberResponse response = groupService.getMemberInfo(groupId, userId);
            return ResponseEntity.ok(createSuccessResponse(response));
        } catch (Exception e) {
            log.error("Failed to get member detail: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 更新成员角色
     *
     * @param groupId   群组ID
     * @param userId    用户ID
     * @param request   角色更新请求
     * @param operatorId 当前用户ID
     * @return 更新后的成员信息
     */
    @PutMapping("/{groupId}/members/{userId}/role")
    public ResponseEntity<?> updateMemberRole(@PathVariable Long groupId,
                                              @PathVariable Long userId,
                                              @Valid @RequestBody UpdateMemberRoleRequest request,
                                              @RequestAttribute("userId") Long operatorId) {
        log.info("Updating member role: groupId={}, userId={}, role={}, by user: {}",
                groupId, userId, request.getRole(), operatorId);
        try {
            GroupMemberResponse response = groupService.updateMemberRole(groupId, userId, request, operatorId);
            return ResponseEntity.ok(createSuccessResponse(response));
        } catch (Exception e) {
            log.error("Failed to update member role: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 转让群主
     *
     * @param groupId    群组ID
     * @param request    包含新群主ID的请求体
     * @param operatorId 当前用户ID
     * @return 操作结果
     */
    @PostMapping("/{groupId}/transfer")
    public ResponseEntity<?> transferOwnership(@PathVariable Long groupId,
                                               @RequestBody Map<String, Long> request,
                                               @RequestAttribute("userId") Long operatorId) {
        Long newOwnerId = request.get("newOwnerId");
        log.info("Transferring group ownership: groupId={}, from={}, to={}",
                groupId, operatorId, newOwnerId);
        try {
            groupService.transferOwnership(groupId, newOwnerId, operatorId);
            return ResponseEntity.ok(createSuccessResponse("群主身份已转让"));
        } catch (Exception e) {
            log.error("Failed to transfer ownership: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // ==================== 成员禁言管理 ====================

    /**
     * 禁言成员
     *
     * @param groupId    群组ID
     * @param userId     用户ID
     * @param request    禁言请求
     * @param operatorId 当前用户ID
     * @return 操作结果
     */
    @PostMapping("/{groupId}/members/{userId}/mute")
    public ResponseEntity<?> muteMember(@PathVariable Long groupId,
                                        @PathVariable Long userId,
                                        @Valid @RequestBody MuteMemberRequest request,
                                        @RequestAttribute("userId") Long operatorId) {
        log.info("Muting member: groupId={}, userId={}, duration={}, by user: {}",
                groupId, userId, request.getDurationMinutes(), operatorId);
        try {
            groupService.muteMember(groupId, userId, request, operatorId);
            return ResponseEntity.ok(createSuccessResponse("成员已禁言"));
        } catch (Exception e) {
            log.error("Failed to mute member: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 解除禁言
     *
     * @param groupId    群组ID
     * @param userId     用户ID
     * @param operatorId 当前用户ID
     * @return 操作结果
     */
    @DeleteMapping("/{groupId}/members/{userId}/mute")
    public ResponseEntity<?> unmuteMember(@PathVariable Long groupId,
                                          @PathVariable Long userId,
                                          @RequestAttribute("userId") Long operatorId) {
        log.info("Unmuting member: groupId={}, userId={}, by user: {}",
                groupId, userId, operatorId);
        try {
            groupService.unmuteMember(groupId, userId, operatorId);
            return ResponseEntity.ok(createSuccessResponse("成员已解除禁言"));
        } catch (Exception e) {
            log.error("Failed to unmute member: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // ==================== 个人设置 ====================

    /**
     * 更新我的群昵称
     *
     * @param groupId  群组ID
     * @param nickname 昵称
     * @param userId   当前用户ID
     * @return 更新后的成员信息
     */
    @PutMapping("/{groupId}/my/nickname")
    public ResponseEntity<?> updateMyNickname(@PathVariable Long groupId,
                                              @RequestParam String nickname,
                                              @RequestAttribute("userId") Long userId) {
        log.info("Updating my nickname: groupId={}, nickname={}, user: {}",
                groupId, nickname, userId);
        try {
            GroupMemberResponse response = groupService.updateMemberNickname(groupId, userId, nickname, userId);
            return ResponseEntity.ok(createSuccessResponse(response));
        } catch (Exception e) {
            log.error("Failed to update nickname: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 设置消息免打扰
     *
     * @param groupId 群组ID
     * @param mute    true-开启免打扰，false-关闭免打扰
     * @param userId  当前用户ID
     * @return 操作结果
     */
    @PutMapping("/{groupId}/my/mute-notifications")
    public ResponseEntity<?> setMuteNotifications(@PathVariable Long groupId,
                                                  @RequestParam boolean mute,
                                                  @RequestAttribute("userId") Long userId) {
        log.info("Setting mute notifications: groupId={}, mute={}, user: {}",
                groupId, mute, userId);
        try {
            groupService.setMuteNotifications(groupId, userId, mute);
            return ResponseEntity.ok(createSuccessResponse(mute ? "已开启消息免打扰" : "已关闭消息免打扰"));
        } catch (Exception e) {
            log.error("Failed to set mute notifications: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 置顶/取消置顶群聊
     *
     * @param groupId 群组ID
     * @param pin     true-置顶，false-取消置顶
     * @param userId  当前用户ID
     * @return 操作结果
     */
    @PutMapping("/{groupId}/my/pin")
    public ResponseEntity<?> setPinned(@PathVariable Long groupId,
                                       @RequestParam boolean pin,
                                       @RequestAttribute("userId") Long userId) {
        log.info("Setting pinned: groupId={}, pin={}, user: {}",
                groupId, pin, userId);
        try {
            groupService.setPinned(groupId, userId, pin);
            return ResponseEntity.ok(createSuccessResponse(pin ? "已置顶群聊" : "已取消置顶"));
        } catch (Exception e) {
            log.error("Failed to set pinned: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // ==================== 辅助方法 ====================

    private Map<String, Object> createSuccessResponse(Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("success", true);
        response.put("data", data);
        return response;
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 400);
        response.put("success", false);
        response.put("message", message);
        return response;
    }
}
