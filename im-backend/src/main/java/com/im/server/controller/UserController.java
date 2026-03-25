package com.im.server.controller;

import com.im.server.dto.*;
import com.im.server.entity.User;
import com.im.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public ApiResponse<User> getCurrentUser(@RequestHeader("Authorization") String token) {
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return ApiResponse.error(401, "Token无效");
        }
        
        User user = userService.getUserById(userId);
        if (user == null) {
            return ApiResponse.error(404, "用户不存在");
        }
        
        // 隐藏密码
        user.setPasswordHash(null);
        return ApiResponse.success(user);
    }
    
    /**
     * 根据ID获取用户信息
     */
    @GetMapping("/{userId}")
    public ApiResponse<User> getUserById(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return ApiResponse.error(404, "用户不存在");
        }
        
        user.setPasswordHash(null);
        return ApiResponse.success(user);
    }
    
    /**
     * 根据用户名获取用户信息
     */
    @GetMapping("/username/{username}")
    public ApiResponse<User> getUserByUsername(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return ApiResponse.error(404, "用户不存在");
        }
        
        user.setPasswordHash(null);
        return ApiResponse.success(user);
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/me")
    public ApiResponse<User> updateUser(@RequestHeader("Authorization") String token,
                                         @RequestBody User user) {
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return ApiResponse.error(401, "Token无效");
        }
        
        try {
            User updatedUser = userService.updateUser(userId, user);
            updatedUser.setPasswordHash(null);
            return ApiResponse.success(updatedUser);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 刷新Token
     */
    @PostMapping("/refresh")
    public ApiResponse<String> refreshToken(@RequestBody String refreshToken) {
        try {
            String newToken = userService.refreshToken(refreshToken);
            return ApiResponse.success(newToken);
        } catch (Exception e) {
            return ApiResponse.error("Token刷新失败");
        }
    }
    
    /**
     * 搜索用户
     */
    @GetMapping("/search")
    public ApiResponse<java.util.List<User>> searchUsers(
            @RequestParam String keyword,
            @RequestHeader("Authorization") String token) {
        
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return ApiResponse.error(401, "Token无效");
        }
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return ApiResponse.error(400, "搜索关键词不能为空");
        }
        
        java.util.List<User> users = userService.searchUsers(keyword.trim());
        return ApiResponse.success(users);
    }
    
    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public ApiResponse<Void> changePassword(
            @RequestHeader("Authorization") String token,
            @RequestBody java.util.Map<String, String> request) {
        
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return ApiResponse.error(401, "Token无效");
        }
        
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");
        
        if (oldPassword == null || newPassword == null || oldPassword.isEmpty() || newPassword.isEmpty()) {
            return ApiResponse.error(400, "密码不能为空");
        }
        
        try {
            userService.changePassword(userId, oldPassword, newPassword);
            return ApiResponse.success(null);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 更新用户头像
     */
    @PostMapping("/update-avatar")
    public ApiResponse<User> updateAvatar(
            @RequestHeader("Authorization") String token,
            @RequestBody java.util.Map<String, String> request) {
        
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return ApiResponse.error(401, "Token无效");
        }
        
        String avatarUrl = request.get("avatarUrl");
        if (avatarUrl == null || avatarUrl.isEmpty()) {
            return ApiResponse.error(400, "头像URL不能为空");
        }
        
        try {
            User user = userService.updateAvatar(userId, avatarUrl);
            user.setPasswordHash(null);
            return ApiResponse.success(user);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 获取所有用户列表
     */
    @GetMapping("/all")
    public ApiResponse<java.util.List<User>> getAllUsers(
            @RequestHeader("Authorization") String token) {
        
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return ApiResponse.error(401, "Token无效");
        }
        
        java.util.List<User> users = userService.getAllUsers();
        return ApiResponse.success(users);
    }
}
