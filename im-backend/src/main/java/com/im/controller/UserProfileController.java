package com.im.controller;

import com.im.dto.UserProfileDTO;
import com.im.dto.UserProfileRequest;
import com.im.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * 用户资料 REST API 控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    /** 获取当前用户完整资料 */
    @GetMapping("/me")
    public ResponseEntity<UserProfileDTO> getMyProfile(@RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(userProfileService.getProfile(userId));
    }

    /** 更新当前用户资料 */
    @PutMapping("/me")
    public ResponseEntity<UserProfileDTO> updateMyProfile(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody UserProfileRequest request) {
        return ResponseEntity.ok(userProfileService.createOrUpdateProfile(userId, request));
    }

    /** 获取指定用户的公开资料 */
    @GetMapping("/{targetUserId}")
    public ResponseEntity<UserProfileDTO> getPublicProfile(@PathVariable String targetUserId) {
        return ResponseEntity.ok(userProfileService.getPublicProfile(targetUserId));
    }

    /** 批量获取用户资料 */
    @PostMapping("/batch")
    public ResponseEntity<List<UserProfileDTO>> getBatchProfiles(@RequestBody Map<String, List<String>> body) {
        List<String> userIds = body.get("userIds");
        return ResponseEntity.ok(userProfileService.getProfilesByUserIds(userIds));
    }

    /** 搜索用户资料 */
    @GetMapping("/search")
    public ResponseEntity<List<String>> searchProfiles(@RequestParam String keyword) {
        return ResponseEntity.ok(userProfileService.searchProfiles(keyword));
    }

    /** 删除我的资料 */
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyProfile(@RequestHeader("X-User-Id") String userId) {
        userProfileService.deleteProfile(userId);
        return ResponseEntity.noContent().build();
    }

    /** 检查资料是否存在 */
    @GetMapping("/{userId}/exists")
    public ResponseEntity<Map<String, Boolean>> profileExists(@PathVariable String userId) {
        return ResponseEntity.ok(Map.of("exists", userProfileService.hasProfile(userId)));
    }
}
