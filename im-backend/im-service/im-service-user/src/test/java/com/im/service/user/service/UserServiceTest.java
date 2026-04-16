package com.im.service.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.im.common.util.JwtTokenUtil;
import com.im.service.user.dto.*;
import com.im.service.user.entity.Friend;
import com.im.service.user.entity.User;
import com.im.service.user.repository.FriendRepository;
import com.im.service.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * UserService 单元测试
 * 
 * 测试覆盖:
 * - 用户注册功能
 * - 用户登录功能
 * - 用户信息更新
 * - 用户搜索功能
 * 
 * @author IM Team
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("用户服务单元测试")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FriendRepository friendRepository;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private UserService userService;

    private static final Long TEST_USER_ID = 1L;
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_PASSWORD = "password123";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PHONE = "13800138000";

    // ========== 用户注册测试 ==========

    @Test
    @DisplayName("用户注册成功")
    void register_Success() {
        // Prepare
        RegisterRequest request = new RegisterRequest();
        request.setUsername(TEST_USERNAME);
        request.setPassword(TEST_PASSWORD);
        request.setNickname("Test User");
        request.setEmail(TEST_EMAIL);
        request.setPhone(TEST_PHONE);

        when(userRepository.existsByUsername(TEST_USERNAME)).thenReturn(false);
        when(userRepository.existsByPhone(TEST_PHONE)).thenReturn(false);
        when(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(false);
        when(userRepository.insert(any(User.class))).thenReturn(1);

        // Act
        User result = userService.register(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(TEST_USERNAME);
        assertThat(result.getNickname()).isEqualTo("Test User");
        verify(userRepository, times(1)).insert(any(User.class));
    }

    @Test
    @DisplayName("用户注册失败-用户名已存在")
    void register_DuplicateUsername() {
        // Prepare
        RegisterRequest request = new RegisterRequest();
        request.setUsername(TEST_USERNAME);
        request.setPassword(TEST_PASSWORD);

        when(userRepository.existsByUsername(TEST_USERNAME)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("用户名已存在");
        verify(userRepository, never()).insert(any(User.class));
    }

    // ========== 用户登录测试 ==========

    @Test
    @DisplayName("用户登录成功")
    void login_Success() {
        // Prepare
        LoginRequest request = new LoginRequest();
        request.setUsername(TEST_USERNAME);
        request.setPassword(TEST_PASSWORD);

        User user = new User();
        user.setId(TEST_USER_ID);
        user.setUsername(TEST_USERNAME);
        user.setPasswordHash(new BCryptPasswordEncoder().encode(TEST_PASSWORD));
        user.setNickname("Test User");
        user.setStatus(1);
        user.setLoginFailCount(0);
        user.setLoginLockUntil(null);

        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(user);
        when(jwtTokenUtil.generateToken(TEST_USER_ID.toString())).thenReturn("access_token");
        when(jwtTokenUtil.generateRefreshToken(TEST_USER_ID.toString())).thenReturn("refresh_token");
        when(userRepository.updateById(any(User.class))).thenReturn(1);

        // Act
        LoginResponse response = userService.login(request, "127.0.0.1", "Chrome");

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("access_token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh_token");
        assertThat(response.getUserId()).isEqualTo(TEST_USER_ID);
        verify(userRepository, times(1)).updateById(any(User.class));
    }

    @Test
    @DisplayName("用户登录失败-密码错误")
    void login_InvalidPassword() {
        // Prepare
        LoginRequest request = new LoginRequest();
        request.setUsername(TEST_USERNAME);
        request.setPassword("wrong_password");

        User user = new User();
        user.setId(TEST_USER_ID);
        user.setUsername(TEST_USERNAME);
        user.setPasswordHash(new BCryptPasswordEncoder().encode(TEST_PASSWORD));
        user.setStatus(1);
        user.setLoginFailCount(0);

        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(user);
        when(userRepository.updateById(any(User.class))).thenReturn(1);

        // Act & Assert
        assertThatThrownBy(() -> userService.login(request, "127.0.0.1", "Chrome"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("用户名或密码错误");
    }

    // ========== 用户信息更新测试 ==========

    @Test
    @DisplayName("更新用户信息成功")
    void updateUserInfo() {
        // Prepare
        UserUpdateRequest request = new UserUpdateRequest();
        request.setNickname("New Nickname");
        request.setSignature("New signature");
        request.setGender("MALE");

        User user = new User();
        user.setId(TEST_USER_ID);
        user.setUsername(TEST_USERNAME);
        user.setNickname("Old Nickname");

        when(userRepository.findById(TEST_USER_ID)).thenReturn(user);
        when(userRepository.updateById(any(User.class))).thenReturn(1);

        // Act
        User result = userService.updateUser(TEST_USER_ID, request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getNickname()).isEqualTo("New Nickname");
        assertThat(result.getSignature()).isEqualTo("New signature");
        verify(userRepository, times(1)).updateById(any(User.class));
    }

    // ========== 用户搜索测试 ==========

    @Test
    @DisplayName("搜索用户成功")
    void searchUsers() {
        // Prepare
        String keyword = "test";
        Long currentUserId = 2L;

        User user1 = new User();
        user1.setId(3L);
        user1.setUsername("testuser1");
        user1.setNickname("Test User 1");

        User user2 = new User();
        user2.setId(4L);
        user2.setUsername("testuser2");
        user2.setNickname("Test User 2");

        when(userRepository.searchUsers(keyword)).thenReturn(List.of(user1, user2));
        when(friendRepository.findByUserIdAndFriendId(anyLong(), anyLong())).thenReturn(null);

        // Act
        List<UserResponse> results = userService.searchUsers(keyword, currentUserId);

        // Assert
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getUsername()).contains("test");
        verify(userRepository, times(1)).searchUsers(keyword);
    }
}
