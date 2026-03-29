package com.im.server.profile;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 用户资料API控制器
 */
@RestController
@RequestMapping("/api/profile")
public class UserProfileController {

    private final UserProfileService profileService;

    public UserProfileController(UserProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserProfile>> getProfile(@PathVariable String userId) {
        UserProfile profile = profileService.getProfile(userId);
        return ApiResponse.ok(profile);
    }

    @GetMapping("/{userId}/public")
    public ResponseEntity<ApiResponse<UserProfile>> getPublicProfile(@PathVariable String userId) {
        UserProfile pub = profileService.getPublicProfile(userId);
        return ApiResponse.ok(pub);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserProfile>> createProfile(@RequestBody CreateProfileRequest req) {
        UserProfile profile = profileService.createProfile(req.userId);
        return ApiResponse.ok(profile);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserProfile>> updateProfile(
            @PathVariable String userId,
            @RequestBody UserProfileService.ProfileUpdateRequest req) {
        UserProfile updated = profileService.updateProfile(userId, req);
        return ApiResponse.ok(updated);
    }

    @PatchMapping("/{userId}/avatar")
    public ResponseEntity<ApiResponse<UserProfile>> updateAvatar(
            @PathVariable String userId,
            @RequestBody Map<String, String> body) {
        UserProfile updated = profileService.updateAvatar(userId, body.get("avatarUrl"));
        return ApiResponse.ok(updated);
    }

    @PatchMapping("/{userId}/nickname")
    public ResponseEntity<ApiResponse<UserProfile>> updateNickname(
            @PathVariable String userId,
            @RequestBody Map<String, String> body) {
        UserProfile updated = profileService.updateNickname(userId, body.get("nickname"));
        return ApiResponse.ok(updated);
    }

    @PatchMapping("/{userId}/signature")
    public ResponseEntity<ApiResponse<UserProfile>> updateSignature(
            @PathVariable String userId,
            @RequestBody Map<String, String> body) {
        UserProfile updated = profileService.updateSignature(userId, body.get("signature"));
        return ApiResponse.ok(updated);
    }

    @PatchMapping("/{userId}/status")
    public ResponseEntity<ApiResponse<UserProfile>> updateStatus(
            @PathVariable String userId,
            @RequestBody Map<String, String> body) {
        UserProfile.UserStatus status = UserProfile.UserStatus.valueOf(body.get("status"));
        UserProfile updated = profileService.updateStatus(userId, status);
        return ApiResponse.ok(updated);
    }

    @PostMapping("/{userId}/online")
    public ResponseEntity<ApiResponse<Void>> setOnline(@PathVariable String userId) {
        profileService.setUserOnline(userId);
        return ApiResponse.ok(null);
    }

    @PostMapping("/{userId}/offline")
    public ResponseEntity<ApiResponse<Void>> setOffline(@PathVariable String userId) {
        profileService.setUserOffline(userId);
        return ApiResponse.ok(null);
    }

    @GetMapping("/online")
    public ResponseEntity<ApiResponse<List<String>>> getOnlineUsers() {
        return ApiResponse.ok(profileService.getOnlineUsers());
    }

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<List<UserProfile>>> searchUsers(
            @RequestBody Map<String, Object> body) {
        String keyword = (String) body.getOrDefault("keyword", "");
        int limit = ((Number) body.getOrDefault("limit", 20)).intValue();
        return ApiResponse.ok(profileService.searchUsers(keyword, limit));
    }

    @PostMapping("/batch")
    public ResponseEntity<ApiResponse<Map<String, UserProfile>>> getProfiles(
            @RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<String> userIds = (List<String>) body.get("userIds");
        return ApiResponse.ok(profileService.getProfiles(userIds));
    }

    // 好友备注
    @PutMapping("/friend-remark")
    public ResponseEntity<ApiResponse<UserProfile.FriendRemark>> setFriendRemark(
            @RequestBody FriendRemarkRequest req) {
        @SuppressWarnings("unchecked")
        List<String> tags = (List<String>) req.getOrDefault("tags", List.of());
        UserProfile.FriendRemark remark = profileService.setFriendRemark(
                req.userId, req.friendId, req.remark, req.groupName, tags);
        return ApiResponse.ok(remark);
    }

    @GetMapping("/{userId}/friend-remark/{friendId}")
    public ResponseEntity<ApiResponse<UserProfile.FriendRemark>> getFriendRemark(
            @PathVariable String userId, @PathVariable String friendId) {
        return profileService.getFriendRemark(userId, friendId)
                .map(ApiResponse::ok)
                .orElse(ApiResponse.notFound());
    }

    @GetMapping("/{userId}/friend-remarks")
    public ResponseEntity<ApiResponse<List<UserProfile.FriendRemark>>> getAllFriendRemarks(
            @PathVariable String userId) {
        return ApiResponse.ok(profileService.getAllFriendRemarks(userId));
    }

    @DeleteMapping("/{userId}/friend-remark/{friendId}")
    public ResponseEntity<ApiResponse<Void>> removeFriendRemark(
            @PathVariable String userId, @PathVariable String friendId) {
        profileService.removeFriendRemark(userId, friendId);
        return ApiResponse.ok(null);
    }

    // 好友分组
    @PostMapping("/{userId}/friend-group")
    public ResponseEntity<ApiResponse<UserProfile.FriendGroup>> createFriendGroup(
            @PathVariable String userId,
            @RequestBody Map<String, Object> body) {
        String name = (String) body.get("name");
        int sortOrder = ((Number) body.getOrDefault("sortOrder", 0)).intValue();
        return ApiResponse.ok(profileService.createFriendGroup(userId, name, sortOrder));
    }

    @GetMapping("/{userId}/friend-groups")
    public ResponseEntity<ApiResponse<List<UserProfile.FriendGroup>>> getFriendGroups(
            @PathVariable String userId) {
        return ApiResponse.ok(profileService.getFriendGroups(userId));
    }

    @DeleteMapping("/{userId}/friend-group/{groupId}")
    public ResponseEntity<ApiResponse<Void>> deleteFriendGroup(
            @PathVariable String userId, @PathVariable String groupId) {
        profileService.deleteFriendGroup(userId, groupId);
        return ApiResponse.ok(null);
    }

    public static class CreateProfileRequest {
        public String userId;
    }

    public static class FriendRemarkRequest extends java.util.HashMap<String, Object> {
        public String userId;
        public String friendId;
        public String remark;
        public String groupName;
    }

    public static class ApiResponse<T> {
        public boolean success;
        public String message;
        public T data;

        public static <T> ApiResponse<T> ok(T data) {
            ApiResponse<T> r = new ApiResponse<>();
            r.success = true;
            r.data = data;
            return r;
        }

        public static <T> ApiResponse<T> notFound() {
            ApiResponse<T> r = new ApiResponse<>();
            r.success = false;
            r.message = "Not found";
            return r;
        }
    }
}
