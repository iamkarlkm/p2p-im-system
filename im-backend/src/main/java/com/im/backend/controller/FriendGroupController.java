package com.im.backend.controller;

import com.im.backend.dto.ApiResponse;
import com.im.backend.dto.FriendGroupDTO;
import com.im.backend.dto.FriendGroupResponseDTO;
import com.im.backend.dto.MoveFriendToGroupDTO;
import com.im.backend.service.FriendGroupService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 好友分组控制器
 * 提供好友分组管理、排序、重命名、成员移动等功能
 */
@RestController
@RequestMapping("/api/v1/friend-groups")
public class FriendGroupController {

    @Autowired
    private FriendGroupService friendGroupService;

    /**
     * 创建好友分组
     */
    @PostMapping
    public ResponseEntity<ApiResponse<FriendGroupResponseDTO>> createGroup(
            @RequestAttribute("userId") Long userId,
            @Valid @RequestBody FriendGroupDTO dto) {
        FriendGroupResponseDTO result = friendGroupService.createGroup(userId, dto);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 更新分组信息
     */
    @PutMapping("/{groupId}")
    public ResponseEntity<ApiResponse<FriendGroupResponseDTO>> updateGroup(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long groupId,
            @Valid @RequestBody FriendGroupDTO dto) {
        FriendGroupResponseDTO result = friendGroupService.updateGroup(userId, groupId, dto);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 删除分组
     */
    @DeleteMapping("/{groupId}")
    public ResponseEntity<ApiResponse<Void>> deleteGroup(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long groupId) {
        friendGroupService.deleteGroup(userId, groupId);
        return ResponseEntity.ok(ApiResponse.success(null, "分组删除成功"));
    }

    /**
     * 获取用户的所有分组
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<FriendGroupResponseDTO>>> getUserGroups(
            @RequestAttribute("userId") Long userId) {
        List<FriendGroupResponseDTO> result = friendGroupService.getUserGroups(userId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 获取分组详情
     */
    @GetMapping("/{groupId}")
    public ResponseEntity<ApiResponse<FriendGroupResponseDTO>> getGroupDetail(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long groupId) {
        FriendGroupResponseDTO result = friendGroupService.getGroupDetail(userId, groupId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 重命名分组
     */
    @PutMapping("/{groupId}/rename")
    public ResponseEntity<ApiResponse<FriendGroupResponseDTO>> renameGroup(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long groupId,
            @RequestBody Map<String, String> request) {
        String newName = request.get("newName");
        FriendGroupResponseDTO result = friendGroupService.renameGroup(userId, groupId, newName);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 更新分组排序
     */
    @PutMapping("/sort")
    public ResponseEntity<ApiResponse<Void>> updateGroupSortOrder(
            @RequestAttribute("userId") Long userId,
            @RequestBody Map<String, List<Long>> request) {
        List<Long> groupIds = request.get("groupIds");
        friendGroupService.updateGroupSortOrder(userId, groupIds);
        return ResponseEntity.ok(ApiResponse.success(null, "排序更新成功"));
    }

    /**
     * 添加好友到分组
     */
    @PostMapping("/{groupId}/members/{friendId}")
    public ResponseEntity<ApiResponse<Void>> addFriendToGroup(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long groupId,
            @PathVariable Long friendId) {
        friendGroupService.addFriendToGroup(userId, groupId, friendId);
        return ResponseEntity.ok(ApiResponse.success(null, "好友已添加到分组"));
    }

    /**
     * 从分组移除好友
     */
    @DeleteMapping("/{groupId}/members/{friendId}")
    public ResponseEntity<ApiResponse<Void>> removeFriendFromGroup(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long groupId,
            @PathVariable Long friendId) {
        friendGroupService.removeFriendFromGroup(userId, groupId, friendId);
        return ResponseEntity.ok(ApiResponse.success(null, "好友已从分组移除"));
    }

    /**
     * 移动好友到其他分组
     */
    @PostMapping("/move-friend")
    public ResponseEntity<ApiResponse<Void>> moveFriendToGroup(
            @RequestAttribute("userId") Long userId,
            @Valid @RequestBody MoveFriendToGroupDTO dto) {
        friendGroupService.moveFriendToGroup(userId, dto);
        return ResponseEntity.ok(ApiResponse.success(null, "好友移动成功"));
    }

    /**
     * 更新分组内好友排序
     */
    @PutMapping("/{groupId}/members/sort")
    public ResponseEntity<ApiResponse<Void>> updateFriendSortOrder(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long groupId,
            @RequestBody Map<String, List<Long>> request) {
        List<Long> friendIds = request.get("friendIds");
        friendGroupService.updateFriendSortOrder(userId, groupId, friendIds);
        return ResponseEntity.ok(ApiResponse.success(null, "好友排序更新成功"));
    }

    /**
     * 设置分组成员星标
     */
    @PutMapping("/{groupId}/members/{friendId}/star")
    public ResponseEntity<ApiResponse<Void>> setMemberStarred(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long groupId,
            @PathVariable Long friendId,
            @RequestBody Map<String, Boolean> request) {
        Boolean starred = request.get("starred");
        friendGroupService.setMemberStarred(userId, groupId, friendId, starred);
        return ResponseEntity.ok(ApiResponse.success(null, starred ? "已设为星标" : "已取消星标"));
    }

    /**
     * 设置分组成员静音
     */
    @PutMapping("/{groupId}/members/{friendId}/mute")
    public ResponseEntity<ApiResponse<Void>> setMemberMuted(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long groupId,
            @PathVariable Long friendId,
            @RequestBody Map<String, Boolean> request) {
        Boolean muted = request.get("muted");
        friendGroupService.setMemberMuted(userId, groupId, friendId, muted);
        return ResponseEntity.ok(ApiResponse.success(null, muted ? "已设为静音" : "已取消静音"));
    }

    /**
     * 创建默认分组
     */
    @PostMapping("/default")
    public ResponseEntity<ApiResponse<FriendGroupResponseDTO>> createDefaultGroup(
            @RequestAttribute("userId") Long userId) {
        FriendGroupResponseDTO result = friendGroupService.createDefaultGroup(userId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
