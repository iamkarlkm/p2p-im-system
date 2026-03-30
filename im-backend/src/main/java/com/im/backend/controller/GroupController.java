package com.im.backend.controller;

import com.im.backend.dto.GroupCreateRequest;
import com.im.backend.dto.GroupMemberRequest;
import com.im.backend.entity.Group;
import com.im.backend.entity.GroupMember;
import com.im.backend.service.GroupService;
import com.im.backend.vo.ApiResponse;
import com.im.backend.vo.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 群组管理控制器
 * 功能 #5: 群组管理基础模块
 * 
 * 提供群组创建、解散、成员管理、权限控制等功能
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/groups")
@Validated
public class GroupController {

    @Autowired
    private GroupService groupService;

    // ==================== 群组基本操作 ====================

    /**
     * 创建群组
     */
    @PostMapping
    public ApiResponse<Group> createGroup(
            @RequestAttribute("userId") Long userId,
            @Valid @RequestBody GroupCreateRequest request) {
        log.info("Creating group: userId={}, name={}", userId, request.getName());
        Group group = groupService.createGroup(userId, request);
        return ApiResponse.success(group);
    }

    /**
     * 获取群组信息
     */
    @GetMapping("/{groupId}")
    public ApiResponse<Group> getGroupInfo(@PathVariable String groupId) {
        Group group = groupService.getGroupInfo(groupId);
        if (group == null) {
            return ApiResponse.error(404, "群组不存在");
        }
        return ApiResponse.success(group);
    }

    /**
     * 更新群组信息
     */
    @PutMapping("/{groupId}")
    public ApiResponse<Group> updateGroupInfo(
            @RequestAttribute("userId") Long userId,
            @PathVariable String groupId,
            @Valid @RequestBody GroupCreateRequest request) {
        log.info("Updating group: groupId={}, userId={}", groupId, userId);
        Group group = groupService.updateGroupInfo(groupId, userId, request);
        return ApiResponse.success(group);
    }

    /**
     * 解散群组
     */
    @DeleteMapping("/{groupId}")
    public ApiResponse<Void> dissolveGroup(
            @RequestAttribute("userId") Long userId,
            @PathVariable String groupId) {
        log.info("Dissolving group: groupId={}, userId={}", groupId, userId);
        groupService.dissolveGroup(groupId, userId);
        return ApiResponse.success(null);
    }

    // ==================== 群组列表查询 ====================

    /**
     * 获取我创建的群组列表
     */
    @GetMapping("/my/created")
    public ApiResponse<List<Group>> getMyCreatedGroups(@RequestAttribute("userId") Long userId) {
        List<Group> groups = groupService.getMyCreatedGroups(userId);
        return ApiResponse.success(groups);
    }

    /**
     * 获取我加入的群组列表
     */
    @GetMapping("/my/joined")
    public ApiResponse<List<Group>> getMyJoinedGroups(@RequestAttribute("userId") Long userId) {
        List<Group> groups = groupService.getMyJoinedGroups(userId);
        return ApiResponse.success(groups);
    }

    /**
     * 搜索群组
     */
    @GetMapping("/search")
    public ApiResponse<List<Group>> searchGroups(
            @RequestParam String keyword,
            @RequestParam(required = false, defaultValue = "20") Integer limit) {
        List<Group> groups = groupService.searchGroups(keyword, limit);
        return ApiResponse.success(groups);
    }

    // ==================== 群组成员管理 ====================

    /**
     * 获取群组成员列表
     */
    @GetMapping("/{groupId}/members")
    public ApiResponse<List<GroupMember>> getGroupMembers(@PathVariable String groupId) {
        List<GroupMember> members = groupService.getGroupMembers(groupId);
        return ApiResponse.success(members);
    }

    /**
     * 获取当前用户在群组中的信息
     */
    @GetMapping("/{groupId}/members/me")
    public ApiResponse<GroupMember> getMyGroupInfo(
            @RequestAttribute("userId") Long userId,
            @PathVariable String groupId) {
        GroupMember member = groupService.getGroupMember(groupId, userId);
        if (member == null) {
            return ApiResponse.error(404, "您不在该群组中");
        }
        return ApiResponse.success(member);
    }

    /**
     * 邀请成员加入群组
     */
    @PostMapping("/{groupId}/members/invite")
    public ApiResponse<Void> inviteMember(
            @RequestAttribute("userId") Long userId,
            @PathVariable String groupId,
            @RequestParam @NotNull Long targetUserId) {
        log.info("Inviting member: groupId={}, inviter={}, target={}", groupId, userId, targetUserId);
        groupService.inviteMember(groupId, userId, targetUserId);
        return ApiResponse.success(null);
    }

    /**
     * 加入群组
     */
    @PostMapping("/{groupId}/members/join")
    public ApiResponse<Void> joinGroup(
            @RequestAttribute("userId") Long userId,
            @PathVariable String groupId) {
        log.info("Joining group: groupId={}, userId={}", groupId, userId);
        groupService.joinGroup(groupId, userId);
        return ApiResponse.success(null);
    }

    /**
     * 退出群组
     */
    @PostMapping("/{groupId}/members/quit")
    public ApiResponse<Void> quitGroup(
            @RequestAttribute("userId") Long userId,
            @PathVariable String groupId) {
        log.info("Quitting group: groupId={}, userId={}", groupId, userId);
        groupService.quitGroup(groupId, userId);
        return ApiResponse.success(null);
    }

    /**
     * 踢出成员
     */
    @PostMapping("/{groupId}/members/kick")
    public ApiResponse<Void> kickMember(
            @RequestAttribute("userId") Long userId,
            @PathVariable String groupId,
            @RequestParam @NotNull Long targetUserId,
            @RequestParam(required = false) String reason) {
        log.info("Kicking member: groupId={}, operator={}, target={}", groupId, userId, targetUserId);
        groupService.kickMember(groupId, userId, targetUserId, reason);
        return ApiResponse.success(null);
    }

    // ==================== 权限管理 ====================

    /**
     * 设置管理员
     */
    @PostMapping("/{groupId}/admins")
    public ApiResponse<Void> setAdmin(
            @RequestAttribute("userId") Long userId,
            @PathVariable String groupId,
            @RequestParam @NotNull Long targetUserId) {
        log.info("Setting admin: groupId={}, operator={}, target={}", groupId, userId, targetUserId);
        groupService.setAdmin(groupId, userId, targetUserId);
        return ApiResponse.success(null);
    }

    /**
     * 取消管理员
     */
    @DeleteMapping("/{groupId}/admins/{targetUserId}")
    public ApiResponse<Void> unsetAdmin(
            @RequestAttribute("userId") Long userId,
            @PathVariable String groupId,
            @PathVariable Long targetUserId) {
        log.info("Unsetting admin: groupId={}, operator={}, target={}", groupId, userId, targetUserId);
        groupService.unsetAdmin(groupId, userId, targetUserId);
        return ApiResponse.success(null);
    }

    /**
     * 转让群主
     */
    @PostMapping("/{groupId}/transfer")
    public ApiResponse<Void> transferOwnership(
            @RequestAttribute("userId") Long userId,
            @PathVariable String groupId,
            @RequestParam @NotNull Long newOwnerId) {
        log.info("Transferring ownership: groupId={}, from={}, to={}", groupId, userId, newOwnerId);
        groupService.transferOwnership(groupId, userId, newOwnerId);
        return ApiResponse.success(null);
    }

    // ==================== 禁言管理 ====================

    /**
     * 禁言成员
     */
    @PostMapping("/{groupId}/members/mute")
    public ApiResponse<Void> muteMember(
            @RequestAttribute("userId") Long userId,
            @PathVariable String groupId,
            @RequestParam @NotNull Long targetUserId,
            @RequestParam(required = false, defaultValue = "0") Integer durationMinutes) {
        log.info("Muting member: groupId={}, operator={}, target={}, duration={}", 
            groupId, userId, targetUserId, durationMinutes);
        groupService.muteMember(groupId, userId, targetUserId, durationMinutes);
        return ApiResponse.success(null);
    }

    /**
     * 解除禁言
     */
    @PostMapping("/{groupId}/members/unmute")
    public ApiResponse<Void> unmuteMember(
            @RequestAttribute("userId") Long userId,
            @PathVariable String groupId,
            @RequestParam @NotNull Long targetUserId) {
        log.info("Unmuting member: groupId={}, operator={}, target={}", groupId, userId, targetUserId);
        groupService.unmuteMember(groupId, userId, targetUserId);
        return ApiResponse.success(null);
    }

    // ==================== 群昵称管理 ====================

    /**
     * 更新群昵称
     */
    @PutMapping("/{groupId}/members/nickname")
    public ApiResponse<Void> updateGroupNickname(
            @RequestAttribute("userId") Long userId,
            @PathVariable String groupId,
            @RequestParam String nickname) {
        log.info("Updating nickname: groupId={}, userId={}, nickname={}", groupId, userId, nickname);
        groupService.updateGroupNickname(groupId, userId, nickname);
        return ApiResponse.success(null);
    }

    // ==================== 成员操作统一入口 ====================

    /**
     * 群组成员操作统一入口
     * 支持: invite/join/kick/mute/unmute/admin/unadmin/transfer
     */
    @PostMapping("/{groupId}/members/operation")
    public ApiResponse<Void> handleMemberOperation(
            @RequestAttribute("userId") Long userId,
            @PathVariable String groupId,
            @Valid @RequestBody GroupMemberRequest request) {
        request.setGroupId(groupId);
        log.info("Member operation: type={}, groupId={}, operator={}", 
            request.getOperationType(), groupId, userId);
        groupService.handleMemberOperation(userId, request);
        return ApiResponse.success(null);
    }

    // ==================== 权限检查 ====================

    /**
     * 检查当前用户是否在群组中
     */
    @GetMapping("/{groupId}/check")
    public ApiResponse<Boolean> checkMembership(
            @RequestAttribute("userId") Long userId,
            @PathVariable String groupId) {
        boolean isMember = groupService.isGroupMember(groupId, userId);
        return ApiResponse.success(isMember);
    }

    /**
     * 获取当前用户在群组的权限信息
     */
    @GetMapping("/{groupId}/permission")
    public ApiResponse<GroupPermissionVO> getPermission(
            @RequestAttribute("userId") Long userId,
            @PathVariable String groupId) {
        GroupPermissionVO permission = new GroupPermissionVO();
        permission.setIsMember(groupService.isGroupMember(groupId, userId));
        permission.setIsAdmin(groupService.isGroupAdmin(groupId, userId));
        permission.setIsOwner(groupService.isGroupOwner(groupId, userId));
        return ApiResponse.success(permission);
    }

    /**
     * 群组权限VO
     */
    public static class GroupPermissionVO {
        private Boolean isMember;
        private Boolean isAdmin;
        private Boolean isOwner;

        public Boolean getIsMember() { return isMember; }
        public void setIsMember(Boolean member) { isMember = member; }
        public Boolean getIsAdmin() { return isAdmin; }
        public void setIsAdmin(Boolean admin) { isAdmin = admin; }
        public Boolean getIsOwner() { return isOwner; }
        public void setIsOwner(Boolean owner) { isOwner = owner; }
    }
}
