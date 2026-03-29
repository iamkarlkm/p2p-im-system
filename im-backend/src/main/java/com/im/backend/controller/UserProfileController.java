package com.im.backend.controller;

import com.im.backend.entity.*;
import com.im.backend.dto.*;
import com.im.backend.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * 用户资料控制器
 * 提供用户资料、在线状态、好友备注等REST API
 */
@RestController
@RequestMapping("/api/v1/profile")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    /**
     * 获取当前用户资料
     */
    @GetMapping("/me")
    public Map<String, Object> getMyProfile(@RequestHeader("X-User-Id") Long userId) {
        UserProfile profile = userProfileService.getProfile(userId);
        return Map.of("code", 0, "data", profile);
    }

    /**
     * 获取指定用户资料
     */
    @GetMapping("/{targetUserId}")
    public Map<String, Object> getProfile(@PathVariable Long targetUserId) {
        UserProfile profile = userProfileService.getProfile(targetUserId);
        return Map.of("code", 0, "data", profile);
    }

    /**
     * 批量获取用户资料
     */
    @PostMapping("/batch")
    public Map<String, Object> getProfiles(@RequestBody Map<String, List<Long>> request) {
        List<Long> userIds = request.get("userIds");
        List<UserProfile> profiles = userProfileService.getProfiles(userIds);
        return Map.of("code", 0, "data", profiles);
    }

    /**
     * 更新个人资料
     */
    @PutMapping("/me")
    public Map<String, Object> updateProfile(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody ProfileUpdateRequest request) {
        UserProfile profile = userProfileService.updateProfile(userId, request);
        return Map.of("code", 0, "data", profile);
    }

    /**
     * 更新在线状态
     */
    @PutMapping("/me/status")
    public Map<String, Object> updateStatus(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody OnlineStatusRequest request) {
        UserProfile profile = userProfileService.updateOnlineStatus(userId, request);
        return Map.of("code", 0, "data", profile);
    }

    /**
     * 上传头像
     */
    @PostMapping("/me/avatar")
    public Map<String, Object> uploadAvatar(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody Map<String, String> request) {
        String fileData = request.get("fileData");
        String avatarUrl = userProfileService.uploadAvatar(userId, fileData);
        return Map.of("code", 0, "data", Map.of("avatarUrl", avatarUrl));
    }

    /**
     * 获取好友分组列表
     */
    @GetMapping("/me/friend-groups")
    public Map<String, Object> getFriendGroups(@RequestHeader("X-User-Id") Long userId) {
        List<FriendGroup> groups = userProfileService.getFriendGroups(userId);
        return Map.of("code", 0, "data", groups);
    }

    /**
     * 创建好友分组
     */
    @PostMapping("/me/friend-groups")
    public Map<String, Object> createFriendGroup(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody Map<String, String> request) {
        String groupName = request.get("groupName");
        FriendGroup group = userProfileService.createGroup(userId, groupName);
        return Map.of("code", 0, "data", group);
    }

    /**
     * 更新好友备注
     */
    @PutMapping("/me/friend-remarks/{friendId}")
    public Map<String, Object> updateFriendRemark(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long friendId,
            @RequestBody FriendRemarkRequest request) {
        request.setFriendId(friendId);
        FriendRemark remark = userProfileService.updateFriendRemark(userId, request);
        return Map.of("code", 0, "data", remark);
    }

    /**
     * 获取好友备注
     */
    @GetMapping("/me/friend-remarks/{friendId}")
    public Map<String, Object> getFriendRemark(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long friendId) {
        FriendRemark remark = userProfileService.getFriendRemark(userId, friendId);
        return Map.of("code", 0, "data", remark != null ? remark : new FriendRemark());
    }
}
