package com.im.service.user.controller;

import com.im.common.base.Result;
import com.im.service.user.dto.*;
import com.im.service.user.entity.User;
import com.im.service.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器 - REST API
 * 
 * @author IM Team
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ========== 用户认证 ==========

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<UserResponse> register(@RequestBody RegisterRequest request) {
        try {
            User user = userService.register(request);
            UserResponse response = new UserResponse();
            response.setId(user.getId());
            response.setUsername(user.getUsername());
            response.setNickname(user.getNickname());
            return Result.success(response);
        } catch (Exception e) {
            log.error("User registration failed: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request,
                                       @RequestHeader(value = "X-Forwarded-For", required = false) String ip,
                                       @RequestHeader(value = "User-Agent", required = false) String device) {
        try {
            LoginResponse response = userService.login(request, ip, device);
            return Result.success(response);
        } catch (Exception e) {
            log.error("User login failed: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 刷新Token
     */
    @PostMapping("/refresh")
    public Result<String> refreshToken(@RequestBody String refreshToken) {
        try {
            String newToken = userService.refreshToken(refreshToken);
            return Result.success(newToken);
        } catch (Exception e) {
            log.error("Token refresh failed: {}", e.getMessage());
            return Result.error("Token刷新失败");
        }
    }

    // ========== 用户信息查询 ==========

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public Result<UserResponse> getCurrentUser(@RequestHeader("Authorization") String token) {
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return Result.error(401, "Token无效");
        }

        try {
            UserResponse user = userService.getUserDetail(userId, userId);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据ID获取用户信息
     */
    @GetMapping("/{userId}")
    public Result<UserResponse> getUserById(@PathVariable Long userId,
                                           @RequestHeader("Authorization") String token) {
        Long currentUserId = userService.verifyToken(token.replace("Bearer ", ""));
        if (currentUserId == null) {
            return Result.error(401, "Token无效");
        }

        try {
            UserResponse user = userService.getUserDetail(userId, currentUserId);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据用户名获取用户信息
     */
    @GetMapping("/username/{username}")
    public Result<UserResponse> getUserByUsername(@PathVariable String username,
                                                  @RequestHeader("Authorization") String token) {
        Long currentUserId = userService.verifyToken(token.replace("Bearer ", ""));
        if (currentUserId == null) {
            return Result.error(401, "Token无效");
        }

        User user = userService.getUserByUsername(username);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        try {
            UserResponse response = userService.getUserDetail(user.getId(), currentUserId);
            return Result.success(response);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // ========== 用户信息更新 ==========

    /**
     * 更新当前用户信息
     */
    @PutMapping("/me")
    public Result<UserResponse> updateUser(@RequestHeader("Authorization") String token,
                                          @RequestBody UserUpdateRequest request) {
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return Result.error(401, "Token无效");
        }

        try {
            User user = userService.updateUser(userId, request);
            UserResponse response = userService.getUserDetail(user.getId(), userId);
            return Result.success(response);
        } catch (Exception e) {
            log.error("Update user failed: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新用户头像
     */
    @PostMapping("/me/avatar")
    public Result<UserResponse> updateAvatar(@RequestHeader("Authorization") String token,
                                            @RequestBody java.util.Map<String, String> request) {
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return Result.error(401, "Token无效");
        }

        String avatarUrl = request.get("avatarUrl");
        if (avatarUrl == null || avatarUrl.isEmpty()) {
            return Result.error(400, "头像URL不能为空");
        }

        try {
            User user = userService.updateAvatar(userId, avatarUrl);
            UserResponse response = new UserResponse();
            response.setId(user.getId());
            response.setUsername(user.getUsername());
            response.setNickname(user.getNickname());
            response.setAvatarUrl(user.getAvatarUrl());
            return Result.success(response);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 修改密码
     */
    @PostMapping("/me/password")
    public Result<Void> changePassword(@RequestHeader("Authorization") String token,
                                       @RequestBody ChangePasswordRequest request) {
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return Result.error(401, "Token无效");
        }

        if (request.getOldPassword() == null || request.getNewPassword() == null ||
            request.getOldPassword().isEmpty() || request.getNewPassword().isEmpty()) {
            return Result.error(400, "密码不能为空");
        }

        try {
            userService.changePassword(userId, request.getOldPassword(), request.getNewPassword());
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // ========== 隐私设置 ==========

    /**
     * 获取隐私设置
     */
    @GetMapping("/me/privacy")
    public Result<PrivacySettingsRequest> getPrivacySettings(@RequestHeader("Authorization") String token) {
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return Result.error(401, "Token无效");
        }

        User user = userService.getUserById(userId);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        PrivacySettingsRequest settings = new PrivacySettingsRequest();
        settings.setAllowSearch(user.getAllowSearch());
        settings.setAddFriendPermission(user.getAddFriendPermission());
        settings.setAllowPhoneSearch(user.getAllowPhoneSearch());
        settings.setAllowEmailSearch(user.getAllowEmailSearch());
        settings.setOnlineStatusVisibility(user.getOnlineStatusVisibility());
        settings.setLastSeenVisibility(user.getLastSeenVisibility());
        settings.setProfileVisibility(user.getProfileVisibility());

        return Result.success(settings);
    }

    /**
     * 更新隐私设置
     */
    @PutMapping("/me/privacy")
    public Result<Void> updatePrivacySettings(@RequestHeader("Authorization") String token,
                                              @RequestBody PrivacySettingsRequest request) {
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return Result.error(401, "Token无效");
        }

        try {
            userService.updatePrivacySettings(userId, request);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // ========== 用户搜索 ==========

    /**
     * 搜索用户
     */
    @GetMapping("/search")
    public Result<List<UserResponse>> searchUsers(@RequestParam String keyword,
                                                  @RequestHeader("Authorization") String token) {
        Long currentUserId = userService.verifyToken(token.replace("Bearer ", ""));
        if (currentUserId == null) {
            return Result.error(401, "Token无效");
        }

        if (keyword == null || keyword.trim().isEmpty()) {
            return Result.error(400, "搜索关键词不能为空");
        }

        List<UserResponse> users = userService.searchUsers(keyword.trim(), currentUserId);
        return Result.success(users);
    }

    // ========== 在线状态 ==========

    /**
     * 获取用户在线状态
     */
    @GetMapping("/{userId}/online-status")
    public Result<String> getOnlineStatus(@PathVariable Long userId,
                                         @RequestHeader("Authorization") String token) {
        Long currentUserId = userService.verifyToken(token.replace("Bearer ", ""));
        if (currentUserId == null) {
            return Result.error(401, "Token无效");
        }

        String status = userService.getOnlineStatus(userId);
        return Result.success(status);
    }

    /**
     * 更新在线状态
     */
    @PutMapping("/me/online-status")
    public Result<Void> updateOnlineStatus(@RequestHeader("Authorization") String token,
                                           @RequestBody java.util.Map<String, String> request) {
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return Result.error(401, "Token无效");
        }

        String status = request.get("status");
        if (status == null || status.isEmpty()) {
            return Result.error(400, "状态不能为空");
        }

        try {
            userService.updateOnlineStatus(userId, status);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // ========== 好友关系管理 ==========

    /**
     * 发送好友申请
     */
    @PostMapping("/friends/requests")
    public Result<FriendResponse> addFriend(@RequestHeader("Authorization") String token,
                                           @RequestBody AddFriendRequest request) {
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return Result.error(401, "Token无效");
        }

        try {
            userService.addFriend(userId, request);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 接受好友申请
     */
    @PostMapping("/friends/requests/{requestId}/accept")
    public Result<Void> acceptFriendRequest(@RequestHeader("Authorization") String token,
                                           @PathVariable Long requestId) {
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return Result.error(401, "Token无效");
        }

        try {
            userService.acceptFriendRequest(requestId, userId);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 拒绝好友申请
     */
    @PostMapping("/friends/requests/{requestId}/reject")
    public Result<Void> rejectFriendRequest(@RequestHeader("Authorization") String token,
                                           @PathVariable Long requestId,
                                           @RequestBody(required = false) java.util.Map<String, String> request) {
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return Result.error(401, "Token无效");
        }

        String reason = request != null ? request.get("reason") : null;

        try {
            userService.rejectFriendRequest(requestId, userId, reason);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取好友列表
     */
    @GetMapping("/friends")
    public Result<List<FriendResponse>> getFriendList(@RequestHeader("Authorization") String token) {
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return Result.error(401, "Token无效");
        }

        List<FriendResponse> friends = userService.getFriendList(userId);
        return Result.success(friends);
    }

    /**
     * 删除好友
     */
    @DeleteMapping("/friends/{friendId}")
    public Result<Void> deleteFriend(@RequestHeader("Authorization") String token,
                                     @PathVariable Long friendId) {
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return Result.error(401, "Token无效");
        }

        try {
            userService.deleteFriend(userId, friendId);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取待处理的好友申请（我收到的）
     */
    @GetMapping("/friends/requests/received")
    public Result<List<FriendResponse>> getPendingReceivedRequests(@RequestHeader("Authorization") String token) {
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return Result.error(401, "Token无效");
        }

        List<FriendResponse> requests = userService.getPendingReceivedRequests(userId);
        return Result.success(requests);
    }

    /**
     * 获取我发出的好友申请
     */
    @GetMapping("/friends/requests/sent")
    public Result<List<FriendResponse>> getPendingSentRequests(@RequestHeader("Authorization") String token) {
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return Result.error(401, "Token无效");
        }

        List<FriendResponse> requests = userService.getPendingSentRequests(userId);
        return Result.success(requests);
    }

    /**
     * 更新好友备注
     */
    @PutMapping("/friends/{friendId}/remark")
    public Result<Void> updateFriendRemark(@RequestHeader("Authorization") String token,
                                          @PathVariable Long friendId,
                                          @RequestBody UpdateFriendRemarkRequest request) {
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return Result.error(401, "Token无效");
        }

        try {
            userService.updateFriendRemark(userId, friendId, request.getRemark());
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 星标好友
     */
    @PostMapping("/friends/{friendId}/star")
    public Result<Void> starFriend(@RequestHeader("Authorization") String token,
                                   @PathVariable Long friendId) {
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return Result.error(401, "Token无效");
        }

        try {
            userService.starFriend(userId, friendId);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 取消星标
     */
    @DeleteMapping("/friends/{friendId}/star")
    public Result<Void> unstarFriend(@RequestHeader("Authorization") String token,
                                     @PathVariable Long friendId) {
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return Result.error(401, "Token无效");
        }

        try {
            userService.unstarFriend(userId, friendId);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 置顶聊天
     */
    @PostMapping("/friends/{friendId}/pin")
    public Result<Void> pinChat(@RequestHeader("Authorization") String token,
                               @PathVariable Long friendId) {
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return Result.error(401, "Token无效");
        }

        try {
            userService.pinChat(userId, friendId);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 取消置顶
     */
    @DeleteMapping("/friends/{friendId}/pin")
    public Result<Void> unpinChat(@RequestHeader("Authorization") String token,
                                  @PathVariable Long friendId) {
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return Result.error(401, "Token无效");
        }

        try {
            userService.unpinChat(userId, friendId);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 屏蔽好友
     */
    @PostMapping("/friends/{friendId}/block")
    public Result<Void> blockFriend(@RequestHeader("Authorization") String token,
                                   @PathVariable Long friendId) {
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return Result.error(401, "Token无效");
        }

        try {
            userService.blockFriend(userId, friendId);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 取消屏蔽
     */
    @DeleteMapping("/friends/{friendId}/block")
    public Result<Void> unblockFriend(@RequestHeader("Authorization") String token,
                                     @PathVariable Long friendId) {
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return Result.error(401, "Token无效");
        }

        try {
            userService.unblockFriend(userId, friendId);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取黑名单列表
     */
    @GetMapping("/friends/blocked")
    public Result<List<FriendResponse>> getBlockedList(@RequestHeader("Authorization") String token) {
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return Result.error(401, "Token无效");
        }

        List<FriendResponse> blocked = userService.getBlockedList(userId);
        return Result.success(blocked);
    }

    /**
     * 切换消息免打扰
     */
    @PutMapping("/friends/{friendId}/mute")
    public Result<Void> toggleMute(@RequestHeader("Authorization") String token,
                                  @PathVariable Long friendId,
                                  @RequestBody java.util.Map<String, Boolean> request) {
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return Result.error(401, "Token无效");
        }

        Boolean mute = request.get("mute");
        if (mute == null) {
            return Result.error(400, "mute参数不能为空");
        }

        try {
            userService.toggleMuteNotifications(userId, friendId, mute);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
