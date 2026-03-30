package com.im.service;

import com.im.entity.User;
import com.im.dto.LoginRequest;
import com.im.dto.LoginResponse;
import com.im.dto.RegisterRequest;
import com.im.dto.TokenResponse;
import java.util.Optional;

/**
 * 用户认证服务接口
 * 功能 #3: 用户认证与授权模块
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
public interface IAuthService {
    
    /**
     * 用户注册
     */
    User register(RegisterRequest request);
    
    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest request);
    
    /**
     * 用户登出
     */
    void logout(String token);
    
    /**
     * 刷新令牌
     */
    TokenResponse refreshToken(String refreshToken);
    
    /**
     * 验证令牌
     */
    boolean validateToken(String token);
    
    /**
     * 根据令牌获取用户
     */
    Optional<User> getUserFromToken(String token);
    
    /**
     * 修改密码
     */
    boolean changePassword(String userId, String oldPassword, String newPassword);
    
    /**
     * 重置密码
     */
    boolean resetPassword(String email);
}
