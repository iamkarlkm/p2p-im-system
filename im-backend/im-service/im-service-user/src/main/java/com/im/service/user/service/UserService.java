package com.im.service.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.im.service.user.dto.*;
import com.im.service.user.entity.Friend;
import com.im.service.user.entity.User;
import com.im.service.user.repository.FriendRepository;
import com.im.service.user.repository.UserRepository;
import com.im.common.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务 - 核心业务逻辑实现
 * 包含用户管理、好友关系管理
 * 
 * @author IM Team
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final ObjectMapper objectMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ========== 用户注册/登录 ==========

    /**
     * 用户注册
     */
    @Transactional
    public User register(RegisterRequest request) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查手机号是否已存在
        if (StringUtils.hasText(request.getPhone()) && userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("手机号已被注册");
        }

        // 检查邮箱是否已存在
        if (StringUtils.hasText(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("邮箱已被注册");
        }

        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setNickname(StringUtils.hasText(request.getNickname()) ? request.getNickname() : request.getUsername());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setStatus(1);
        user.setUserType("NORMAL");
        user.setOnlineStatus("OFFLINE");

        userRepository.insert(user);
        log.info("User registered successfully: username={}", user.getUsername());
        return user;
    }

    /**
     * 用户登录
     */
    @Transactional
    public LoginResponse login(LoginRequest request, String ip, String device) {
        User user = userRepository.findByUsername(request.getUsername());
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 检查账号是否被锁定
        if (user.isLocked()) {
            throw new RuntimeException("账号已被锁定，请稍后重试");
        }

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            user.incrementLoginFail();
            if (user.getLoginFailCount() >= 5) {
                user.lockAccount(30);  // 锁定30分钟
                userRepository.updateById(user);
                throw new RuntimeException("密码错误次数过多，账号已锁定30分钟");
            }
            userRepository.updateById(user);
            throw new RuntimeException("用户名或密码错误");
        }

        // 检查账号状态
        if (user.getStatus() != 1) {
            throw new RuntimeException("账号已被禁用或锁定");
        }

        // 重置登录失败次数
        user.resetLoginFail();
        user.updateLastLogin(ip, device);
        userRepository.updateById(user);

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

        log.info("User logged in successfully: userId={}, ip={}", user.getId(), ip);
        return response;
    }

    /**
     * 根据ID查询用户
     */
    public User getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * 根据用户名查询用户
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // ========== 用户信息更新 ==========

    /**
     * 更新用户信息
     */
    @Transactional
    public User updateUser(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (StringUtils.hasText(request.getNickname())) {
            user.setNickname(request.getNickname());
        }
        if (StringUtils.hasText(request.getAvatarUrl())) {
            user.setAvatarUrl(request.getAvatarUrl());
        }
        if (StringUtils.hasText(request.getPhone())) {
            // 检查手机号是否被其他用户使用
            User existingByPhone = userRepository.findByPhone(request.getPhone());
            if (existingByPhone != null && !existingByPhone.getId().equals(userId)) {
                throw new RuntimeException("手机号已被其他用户使用");
            }
            user.setPhone(request.getPhone());
        }
        if (StringUtils.hasText(request.getEmail())) {
            // 检查邮箱是否被其他用户使用
            User existingByEmail = userRepository.findByEmail(request.getEmail());
            if (existingByEmail != null && !existingByEmail.getId().equals(userId)) {
                throw new RuntimeException("邮箱已被其他用户使用");
            }
            user.setEmail(request.getEmail());
        }
        if (StringUtils.hasText(request.getGender())) {
            user.setGender(request.getGender());
        }
        if (StringUtils.hasText(request.getSignature())) {
            user.setSignature(request.getSignature());
        }
        if (StringUtils.hasText(request.getLocation())) {
            user.setLocation(request.getLocation());
        }
        if (StringUtils.hasText(request.getTags())) {
            user.setTags(request.getTags());
        }

        userRepository.updateById(user);
        return user;
    }

    /**
     * 修改密码
     */
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new RuntimeException("原密码错误");
        }

        // 更新密码
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.updateById(user);
        log.info("User changed password: userId={}", userId);
    }

    /**
     * 更新用户头像
     */
    @Transactional
    public User updateAvatar(Long userId, String avatarUrl) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        user.setAvatarUrl(avatarUrl);
        userRepository.updateById(user);
        return user;
    }

    // ========== 隐私设置 ==========

    /**
     * 更新隐私设置
     */
    @Transactional
    public void updatePrivacySettings(Long userId, PrivacySettingsRequest request) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (request.getAllowSearch() != null) {
            user.setAllowSearch(request.getAllowSearch());
        }
        if (StringUtils.hasText(request.getAddFriendPermission())) {
            user.setAddFriendPermission(request.getAddFriendPermission());
        }
        if (request.getAllowPhoneSearch() != null) {
            user.setAllowPhoneSearch(request.getAllowPhoneSearch());
        }
        if (request.getAllowEmailSearch() != null) {
            user.setAllowEmailSearch(request.getAllowEmailSearch());
        }
        if (StringUtils.hasText(request.getOnlineStatusVisibility())) {
            user.setOnlineStatusVisibility(request.getOnlineStatusVisibility());
        }
        if (StringUtils.hasText(request.getLastSeenVisibility())) {
            user.setLastSeenVisibility(request.getLastSeenVisibility());
        }
        if (StringUtils.hasText(request.getProfileVisibility())) {
            user.setProfileVisibility(request.getProfileVisibility());
        }

        userRepository.updateById(user);
    }

    // ========== 用户搜索 ==========

    /**
     * 搜索用户
     */
    public List<UserResponse> searchUsers(String keyword, Long currentUserId) {
        List<User> users = userRepository.searchUsers(keyword);
        return users.stream()
                .filter(u -> !u.getId().equals(currentUserId))  // 排除自己
                .map(u -> toUserResponse(u, currentUserId))
                .collect(Collectors.toList());
    }

    /**
     * 获取用户详情（带好友关系判断）
     */
    public UserResponse getUserDetail(Long targetUserId, Long currentUserId) {
        User user = userRepository.findById(targetUserId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return toUserResponse(user, currentUserId);
    }

    // ========== Token相关 ==========

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

    // ========== 在线状态 ==========

    /**
     * 更新用户在线状态
     */
    @Transactional
    public void updateOnlineStatus(Long userId, String status) {
        userRepository.updateOnlineStatus(userId, status);
    }

    /**
     * 获取用户在线状态
     */
    public String getOnlineStatus(Long userId) {
        User user = userRepository.findById(userId);
        return user != null ? user.getOnlineStatus() : "OFFLINE";
    }

    // ========== 好友关系管理 ==========

    /**
     * 发送好友申请
     */
    @Transactional
    public Friend addFriend(Long userId, AddFriendRequest request) {
        Long targetUserId = request.getTargetUserId();

        // 不能添加自己
        if (userId.equals(targetUserId)) {
            throw new RuntimeException("不能添加自己为好友");
        }

        // 检查目标用户是否存在
        User targetUser = userRepository.findById(targetUserId);
        if (targetUser == null) {
            throw new RuntimeException("用户不存在");
        }

        // 检查目标用户的隐私设置
        if (!targetUser.canAddFriend()) {
            throw new RuntimeException("该用户不允许添加好友");
        }

        // 检查是否已经是好友
        if (friendRepository.areFriends(userId, targetUserId)) {
            throw new RuntimeException("已经是好友关系");
        }

        // 检查是否已发送过申请
        Friend existing = friendRepository.findByUserIdAndFriendId(userId, targetUserId);
        if (existing != null && existing.isPending()) {
            throw new RuntimeException("已发送过好友申请，请等待对方确认");
        }

        // 检查对方是否已发送申请给我
        Friend reverse = friendRepository.findByUserIdAndFriendId(targetUserId, userId);
        if (reverse != null && reverse.isPending()) {
            // 如果对方已经发了申请，直接通过
            acceptFriendRequest(reverse.getId(), userId);
            // 创建我的好友记录
            Friend myFriend = new Friend();
            myFriend.setUserId(userId);
            myFriend.setFriendId(targetUserId);
            myFriend.setSource(request.getSource());
            myFriend.setStatus("ACCEPTED");
            myFriend.setBecameFriendsAt(LocalDateTime.now());
            friendRepository.insert(myFriend);
            return myFriend;
        }

        // 创建好友申请
        Friend friend = new Friend();
        friend.setUserId(userId);
        friend.setFriendId(targetUserId);
        friend.setSource(request.getSource());
        friend.setApplyMessage(request.getApplyMessage());
        friend.setStatus("PENDING");

        friendRepository.insert(friend);
        log.info("Friend request sent: userId={}, targetUserId={}", userId, targetUserId);
        return friend;
    }

    /**
     * 接受好友申请
     */
    @Transactional
    public void acceptFriendRequest(Long requestId, Long currentUserId) {
        Friend friend = friendRepository.selectById(requestId);
        if (friend == null) {
            throw new RuntimeException("好友申请不存在");
        }

        // 验证是否为接收方
        if (!friend.getFriendId().equals(currentUserId)) {
            throw new RuntimeException("无权处理该申请");
        }

        if (!friend.isPending()) {
            throw new RuntimeException("该申请已被处理");
        }

        // 更新申请状态
        friend.accept();
        friendRepository.updateById(friend);

        // 创建双向关系
        Friend reverse = friendRepository.findByUserIdAndFriendId(currentUserId, friend.getUserId());
        if (reverse == null) {
            reverse = new Friend();
            reverse.setUserId(currentUserId);
            reverse.setFriendId(friend.getUserId());
            reverse.setSource(friend.getSource());
            reverse.setStatus("ACCEPTED");
            reverse.setBecameFriendsAt(LocalDateTime.now());
            friendRepository.insert(reverse);
        } else {
            reverse.setStatus("ACCEPTED");
            reverse.setBecameFriendsAt(LocalDateTime.now());
            friendRepository.updateById(reverse);
        }

        log.info("Friend request accepted: requestId={}, userId={}", requestId, currentUserId);
    }

    /**
     * 拒绝好友申请
     */
    @Transactional
    public void rejectFriendRequest(Long requestId, Long currentUserId, String reason) {
        Friend friend = friendRepository.selectById(requestId);
        if (friend == null) {
            throw new RuntimeException("好友申请不存在");
        }

        // 验证是否为接收方
        if (!friend.getFriendId().equals(currentUserId)) {
            throw new RuntimeException("无权处理该申请");
        }

        if (!friend.isPending()) {
            throw new RuntimeException("该申请已被处理");
        }

        friend.reject(reason);
        friendRepository.updateById(friend);
        log.info("Friend request rejected: requestId={}, userId={}", requestId, currentUserId);
    }

    /**
     * 删除好友
     */
    @Transactional
    public void deleteFriend(Long userId, Long friendId) {
        Friend friend = friendRepository.findByUserIdAndFriendId(userId, friendId);
        if (friend == null || !friend.isMutualFriend()) {
            throw new RuntimeException("不是好友关系");
        }

        // 删除双向关系
        friendRepository.physicalDelete(userId, friendId);
        friendRepository.physicalDelete(friendId, userId);

        log.info("Friend deleted: userId={}, friendId={}", userId, friendId);
    }

    /**
     * 获取好友列表
     */
    public List<FriendResponse> getFriendList(Long userId) {
        List<Friend> friends = friendRepository.findFriendsWithStarPriority(userId);
        return friends.stream()
                .map(f -> toFriendResponse(f, userId))
                .collect(Collectors.toList());
    }

    /**
     * 获取待处理的好友申请（我收到的）
     */
    public List<FriendResponse> getPendingReceivedRequests(Long userId) {
        List<Friend> requests = friendRepository.findPendingReceivedRequests(userId);
        return requests.stream()
                .map(f -> toFriendResponse(f, userId))
                .collect(Collectors.toList());
    }

    /**
     * 获取我发出的好友申请
     */
    public List<FriendResponse> getPendingSentRequests(Long userId) {
        List<Friend> requests = friendRepository.findPendingSentRequests(userId);
        return requests.stream()
                .map(f -> toFriendResponse(f, userId))
                .collect(Collectors.toList());
    }

    /**
     * 更新好友备注
     */
    @Transactional
    public void updateFriendRemark(Long userId, Long friendId, String remark) {
        Friend friend = friendRepository.findByUserIdAndFriendId(userId, friendId);
        if (friend == null || !friend.isMutualFriend()) {
            throw new RuntimeException("不是好友关系");
        }

        friend.setRemark(remark);
        friendRepository.updateById(friend);
    }

    /**
     * 星标好友
     */
    @Transactional
    public void starFriend(Long userId, Long friendId) {
        Friend friend = friendRepository.findByUserIdAndFriendId(userId, friendId);
        if (friend == null || !friend.isMutualFriend()) {
            throw new RuntimeException("不是好友关系");
        }

        friend.star();
        friendRepository.updateById(friend);
    }

    /**
     * 取消星标
     */
    @Transactional
    public void unstarFriend(Long userId, Long friendId) {
        Friend friend = friendRepository.findByUserIdAndFriendId(userId, friendId);
        if (friend == null || !friend.isMutualFriend()) {
            throw new RuntimeException("不是好友关系");
        }

        friend.unstar();
        friendRepository.updateById(friend);
    }

    /**
     * 置顶聊天
     */
    @Transactional
    public void pinChat(Long userId, Long friendId) {
        Friend friend = friendRepository.findByUserIdAndFriendId(userId, friendId);
        if (friend == null || !friend.isMutualFriend()) {
            throw new RuntimeException("不是好友关系");
        }

        friend.pin();
        friendRepository.updateById(friend);
    }

    /**
     * 取消置顶
     */
    @Transactional
    public void unpinChat(Long userId, Long friendId) {
        Friend friend = friendRepository.findByUserIdAndFriendId(userId, friendId);
        if (friend == null || !friend.isMutualFriend()) {
            throw new RuntimeException("不是好友关系");
        }

        friend.unpin();
        friendRepository.updateById(friend);
    }

    /**
     * 屏蔽好友
     */
    @Transactional
    public void blockFriend(Long userId, Long friendId) {
        Friend friend = friendRepository.findByUserIdAndFriendId(userId, friendId);
        if (friend == null) {
            // 创建屏蔽关系
            friend = new Friend();
            friend.setUserId(userId);
            friend.setFriendId(friendId);
            friend.setStatus("BLOCKED");
            friend.setBlocked(true);
            friend.setBlockedAt(LocalDateTime.now());
            friendRepository.insert(friend);
        } else {
            friend.block();
            friendRepository.updateById(friend);
        }
    }

    /**
     * 取消屏蔽
     */
    @Transactional
    public void unblockFriend(Long userId, Long friendId) {
        Friend friend = friendRepository.findByUserIdAndFriendId(userId, friendId);
        if (friend == null) {
            throw new RuntimeException("无此关系记录");
        }

        friend.unblock();
        friendRepository.updateById(friend);
    }

    /**
     * 获取黑名单列表
     */
    public List<FriendResponse> getBlockedList(Long userId) {
        List<Friend> blocked = friendRepository.findBlockedFriends(userId);
        return blocked.stream()
                .map(f -> toFriendResponse(f, userId))
                .collect(Collectors.toList());
    }

    /**
     * 切换消息免打扰
     */
    @Transactional
    public void toggleMuteNotifications(Long userId, Long friendId, Boolean mute) {
        Friend friend = friendRepository.findByUserIdAndFriendId(userId, friendId);
        if (friend == null || !friend.isMutualFriend()) {
            throw new RuntimeException("不是好友关系");
        }

        friend.setMuteNotifications(mute);
        friendRepository.updateById(friend);
    }

    // ========== 转换方法 ==========

    /**
     * 实体转UserResponse
     */
    private UserResponse toUserResponse(User user, Long currentUserId) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setAvatarUrl(user.getAvatarUrl());
        response.setUserType(user.getUserType());
        response.setStatus(user.getStatus());
        response.setGender(user.getGender());
        response.setSignature(user.getSignature());
        response.setLocation(user.getLocation());
        response.setOnlineStatus(user.getOnlineStatus());
        response.setCreatedAt(user.getCreatedAt());

        // 解析标签
        if (StringUtils.hasText(user.getTags())) {
            try {
                List<String> tags = objectMapper.readValue(user.getTags(), new TypeReference<List<String>>() {});
                response.setTags(tags);
            } catch (JsonProcessingException e) {
                log.error("Failed to parse user tags", e);
            }
        }

        // 判断是否为好友关系
        if (currentUserId != null && !currentUserId.equals(user.getId())) {
            Friend friend = friendRepository.findByUserIdAndFriendId(currentUserId, user.getId());
            response.setIsFriend(friend != null && friend.isMutualFriend());
            if (friend != null) {
                response.setFriendRemark(friend.getRemark());
            }
        }

        // 隐私控制 - 只有好友或自己才能看到部分信息
        boolean isSelf = currentUserId != null && currentUserId.equals(user.getId());
        boolean isFriend = response.getIsFriend() != null && response.getIsFriend();

        if (isSelf || isFriend || "ALL".equals(user.getProfileVisibility())) {
            response.setPhone(user.getPhone());
            response.setEmail(user.getEmail());
            response.setBirthday(user.getBirthday());
        }

        // 在线状态可见性
        if (isSelf || isFriend || "ALL".equals(user.getOnlineStatusVisibility())) {
            response.setOnlineStatus(user.getOnlineStatus());
        } else {
            response.setOnlineStatus("OFFLINE");
        }

        // 最后在线时间可见性
        if (isSelf || isFriend || "ALL".equals(user.getLastSeenVisibility())) {
            response.setLastOnlineAt(user.getLastOnlineAt());
        }

        return response;
    }

    /**
     * 实体转FriendResponse
     */
    private FriendResponse toFriendResponse(Friend friend, Long currentUserId) {
        FriendResponse response = new FriendResponse();
        response.setId(friend.getId());
        response.setUserId(friend.getUserId());
        response.setFriendId(friend.getFriendId());
        response.setStatus(friend.getStatus());
        response.setSource(friend.getSource());
        response.setApplyMessage(friend.getApplyMessage());
        response.setRejectReason(friend.getRejectReason());
        response.setRemark(friend.getRemark());
        response.setStarred(friend.getStarred());
        response.setStarredAt(friend.getStarredAt());
        response.setPinned(friend.getPinned());
        response.setPinnedAt(friend.getPinnedAt());
        response.setMuteNotifications(friend.getMuteNotifications());
        response.setBlocked(friend.getBlocked());
        response.setBlockedAt(friend.getBlockedAt());
        response.setBecameFriendsAt(friend.getBecameFriendsAt());
        response.setLastChatAt(friend.getLastChatAt());
        response.setCreatedAt(friend.getCreatedAt());

        // 解析标签
        if (StringUtils.hasText(friend.getTags())) {
            try {
                List<String> tags = objectMapper.readValue(friend.getTags(), new TypeReference<List<String>>() {});
                response.setTags(tags);
            } catch (JsonProcessingException e) {
                log.error("Failed to parse friend tags", e);
            }
        }

        // 查询好友用户信息
        Long friendUserId = friend.getUserId().equals(currentUserId) ? friend.getFriendId() : friend.getUserId();
        User friendUser = userRepository.findById(friendUserId);
        if (friendUser != null) {
            response.setFriendUsername(friendUser.getUsername());
            response.setFriendNickname(friendUser.getNickname());
            response.setFriendAvatarUrl(friendUser.getAvatarUrl());
            response.setFriendOnlineStatus(friendUser.getOnlineStatus());
            response.setFriendLastOnlineAt(friendUser.getLastOnlineAt());
        }

        return response;
    }
}
