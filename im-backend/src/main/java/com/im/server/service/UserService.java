package com.im.server.service;

import com.im.server.entity.User;
import com.im.server.repository.UserRepository;
import com.im.server.util.JwtTokenUtil;
import com.im.server.dto.LoginRequest;
import com.im.server.dto.LoginResponse;
import com.im.server.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 用户服务
 */
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    /**
     * 用户注册
     */
    @Transactional
    public User register(RegisterRequest request) {
        // 检查用户名是否已存在
        if (userRepository.findByUsername(request.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setStatus(1);
        
        return userRepository.save(user);
    }
    
    /**
     * 用户登录
     */
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername());
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        if (user.getStatus() != 1) {
            throw new RuntimeException("账号已被禁用");
        }
        
        // 生成Token
        String token = jwtTokenUtil.generateToken(user.getId().toString());
        String refreshToken = jwtTokenUtil.generateRefreshToken(user.getId().toString());
        
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setRefreshToken(refreshToken);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setAvatarUrl(user.getAvatarUrl());
        
        return response;
    }
    
    /**
     * 根据ID查询用户
     */
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    
    /**
     * 根据用户名查询用户
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    /**
     * 更新用户信息
     */
    public User updateUser(Long userId, User user) {
        User existingUser = userRepository.findById(userId).orElse(null);
        if (existingUser == null) {
            throw new RuntimeException("用户不存在");
        }
        
        if (user.getNickname() != null) {
            existingUser.setNickname(user.getNickname());
        }
        if (user.getAvatarUrl() != null) {
            existingUser.setAvatarUrl(user.getAvatarUrl());
        }
        if (user.getPhone() != null) {
            existingUser.setPhone(user.getPhone());
        }
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        
        return userRepository.save(existingUser);
    }
    
    /**
     * 验证Token并获取用户ID
     */
    public Long verifyToken(String token) {
        try {
            String userId = jwtTokenUtil.getUserIdFromToken(token);
            return Long.parseLong(userId);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 刷新Token
     */
    public String refreshToken(String refreshToken) {
        try {
            String userId = jwtTokenUtil.getUserIdFromToken(refreshToken);
            return jwtTokenUtil.generateToken(userId);
        } catch (Exception e) {
            throw new RuntimeException("Token刷新失败");
        }
    }
    
    /**
     * 搜索用户
     */
    public java.util.List<User> searchUsers(String keyword) {
        return userRepository.findAll().stream()
                .filter(u -> u.getUsername().contains(keyword) || 
                           (u.getNickname() != null && u.getNickname().contains(keyword)) ||
                           (u.getEmail() != null && u.getEmail().contains(keyword)) ||
                           (u.getPhone() != null && u.getPhone().contains(keyword)))
                .peek(u -> u.setPasswordHash(null))  // 隐藏密码
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * 修改密码
     */
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new RuntimeException("原密码错误");
        }
        
        // 更新密码
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
    /**
     * 重置密码（管理员或忘记密码时使用）
     */
    @Transactional
    public void resetPassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
    /**
     * 更新用户头像
     */
    @Transactional
    public User updateAvatar(Long userId, String avatarUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        user.setAvatarUrl(avatarUrl);
        return userRepository.save(user);
    }
    
    /**
     * 更新用户状态（在线/离线）
     */
    @Transactional
    public void updateUserStatus(Long userId, Integer status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        user.setStatus(status);
        userRepository.save(user);
    }
    
    /**
     * 获取所有用户列表
     */
    public java.util.List<User> getAllUsers() {
        return userRepository.findAll().stream()
                .peek(u -> u.setPasswordHash(null))
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * 检查用户名是否存在
     */
    public boolean usernameExists(String username) {
        return userRepository.findByUsername(username) != null;
    }
}
