package com.im.user.service;

import com.im.user.dto.UserRegisterRequest;
import com.im.user.dto.UserLoginRequest;
import com.im.user.dto.UserResponse;
import com.im.user.entity.User;
import com.im.user.enums.UserStatus;
import com.im.user.exception.UserAlreadyExistsException;
import com.im.user.exception.InvalidCredentialsException;
import com.im.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 用户服务单元测试
 * 测试覆盖: 用户注册、登录、信息更新等核心功能
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("用户服务单元测试")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserRegisterRequest validRegisterRequest;
    private UserLoginRequest validLoginRequest;
    private User savedUser;

    @BeforeEach
    void setUp() {
        // 准备有效的注册请求
        validRegisterRequest = new UserRegisterRequest();
        validRegisterRequest.setUsername("testuser");
        validRegisterRequest.setPassword("password123");
        validRegisterRequest.setPhone("13800138000");
        validRegisterRequest.setEmail("test@example.com");
        validRegisterRequest.setNickname("Test User");

        // 准备有效的登录请求
        validLoginRequest = new UserLoginRequest();
        validLoginRequest.setUsername("testuser");
        validLoginRequest.setPassword("password123");
        validLoginRequest.setDeviceId("device_001");

        // 准备已保存的用户实体
        savedUser = new User();
        savedUser.setUserId("user_001");
        savedUser.setUsername("testuser");
        savedUser.setPassword("encoded_password");
        savedUser.setPhone("13800138000");
        savedUser.setEmail("test@example.com");
        savedUser.setNickname("Test User");
        savedUser.setStatus(UserStatus.ACTIVE);
        savedUser.setCreatedAt(LocalDateTime.now());
        savedUser.setLoginFailCount(0);
        savedUser.setIsLocked(false);
    }

    @Test
    @DisplayName("正常用户注册 - 成功")
    void register_Success() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByPhone("13800138000")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        UserResponse response = userService.register(validRegisterRequest);

        // Then
        assertNotNull(response);
        assertEquals("user_001", response.getUserId());
        assertEquals("testuser", response.getUsername());
        assertEquals("Test User", response.getNickname());

        verify(userRepository).existsByUsername("testuser");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("重复用户名注册 - 失败")
    void register_DuplicateUsername() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // When & Then
        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.register(validRegisterRequest);
        });

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("重复手机号注册 - 失败")
    void register_DuplicatePhone() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByPhone("13800138000")).thenReturn(true);

        // When & Then
        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.register(validRegisterRequest);
        });
    }

    @Test
    @DisplayName("重复邮箱注册 - 失败")
    void register_DuplicateEmail() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByPhone("13800138000")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // When & Then
        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.register(validRegisterRequest);
        });
    }

    @Test
    @DisplayName("正常用户登录 - 成功")
    void login_Success() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(savedUser));
        when(passwordEncoder.matches("password123", "encoded_password")).thenReturn(true);

        // When
        UserResponse response = userService.login(validLoginRequest);

        // Then
        assertNotNull(response);
        assertEquals("user_001", response.getUserId());
        assertEquals("testuser", response.getUsername());

        verify(userRepository).save(savedUser); // 更新最后登录时间
    }

    @Test
    @DisplayName("密码错误登录 - 失败")
    void login_InvalidPassword() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(savedUser));
        when(passwordEncoder.matches("password123", "encoded_password")).thenReturn(false);

        // When & Then
        assertThrows(InvalidCredentialsException.class, () -> {
            userService.login(validLoginRequest);
        });
    }

    @Test
    @DisplayName("账号锁定状态登录 - 失败")
    void login_AccountLocked() {
        // Given
        savedUser.setIsLocked(true);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(savedUser));

        // When & Then
        assertThrows(InvalidCredentialsException.class, () -> {
            userService.login(validLoginRequest);
        });
    }

    @Test
    @DisplayName("更新用户信息 - 成功")
    void updateUserInfo_Success() {
        // Given
        when(userRepository.findById("user_001")).thenReturn(Optional.of(savedUser));

        // When
        UserResponse response = userService.updateUserInfo("user_001", "New Nickname", null, null);

        // Then
        assertNotNull(response);
        assertEquals("New Nickname", savedUser.getNickname());
        verify(userRepository).save(savedUser);
    }

    @Test
    @DisplayName("搜索用户 - 成功")
    void searchUsers_ByKeyword() {
        // Given
        java.util.List<User> users = java.util.Arrays.asList(savedUser);
        when(userRepository.searchByKeyword("test")).thenReturn(users);

        // When
        java.util.List<UserResponse> results = userService.searchUsers("test", 20);

        // Then
        assertEquals(1, results.size());
        assertEquals("testuser", results.get(0).getUsername());
    }

    @Test
    @DisplayName("获取用户信息 - 成功")
    void getUserById_Success() {
        // Given
        when(userRepository.findById("user_001")).thenReturn(Optional.of(savedUser));

        // When
        UserResponse response = userService.getUserById("user_001");

        // Then
        assertNotNull(response);
        assertEquals("user_001", response.getUserId());
        assertEquals("testuser", response.getUsername());
    }

    @Test
    @DisplayName("获取不存在的用户 - 失败")
    void getUserById_NotFound() {
        // Given
        when(userRepository.findById("user_999")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(javax.persistence.EntityNotFoundException.class, () -> {
            userService.getUserById("user_999");
        });
    }

    @Test
    @DisplayName("更新隐私设置 - 成功")
    void updatePrivacySettings() {
        // Given
        when(userRepository.findById("user_001")).thenReturn(Optional.of(savedUser));

        // When
        userService.updatePrivacySettings("user_001", false, false, true);

        // Then
        assertFalse(savedUser.getAllowStrangerAddFriend());
        assertFalse(savedUser.getShowOnlineStatus());
        assertTrue(savedUser.getAllowSearchByPhone());
        verify(userRepository).save(savedUser);
    }

    @Test
    @DisplayName("密码加密存储 - 验证")
    void password_Encoded() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByPhone("13800138000")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            assertEquals("encoded_password", user.getPassword());
            return savedUser;
        });

        // When
        userService.register(validRegisterRequest);

        // Then - 在when中验证
    }
}
