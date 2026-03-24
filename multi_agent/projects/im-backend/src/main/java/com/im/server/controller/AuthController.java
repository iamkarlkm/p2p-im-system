package com.im.server.controller;

import com.im.server.entity.User;
import com.im.server.service.UserService;
import com.im.server.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        
        User user = userService.login(username, password);
        
        if (user != null) {
            String token = jwtTokenUtil.generateToken(user.getId().toString());
            String refreshToken = jwtTokenUtil.generateRefreshToken(user.getId().toString());
            
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("refreshToken", refreshToken);
            result.put("userId", user.getId());
            result.put("username", user.getUsername());
            result.put("nickname", user.getNickname());
            
            return ResponseEntity.ok(result);
        }
        
        return ResponseEntity.status(401).body(Map.of("error", "用户名或密码错误"));
    }
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        try {
            User newUser = userService.register(user);
            
            String token = jwtTokenUtil.generateToken(newUser.getId().toString());
            String refreshToken = jwtTokenUtil.generateRefreshToken(newUser.getId().toString());
            
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("refreshToken", refreshToken);
            result.put("userId", newUser.getId());
            result.put("username", newUser.getUsername());
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 刷新Token
     */
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        
        if (jwtTokenUtil.validateToken(refreshToken)) {
            String userId = jwtTokenUtil.getUserIdFromToken(refreshToken);
            String newToken = jwtTokenUtil.generateToken(userId);
            
            return ResponseEntity.ok(Map.of("token", newToken));
        }
        
        return ResponseEntity.status(401).body(Map.of("error", "Token已过期"));
    }
}
