package com.im.backend.service;

import com.im.backend.entity.*;
import com.im.backend.dto.*;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 用户资料服务
 * 处理用户资料、在线状态、好友备注等业务逻辑
 */
@Service
public class UserProfileService {

    // 模拟数据存储 (实际使用数据库)
    private final Map<Long, UserProfile> profileStore = new HashMap<>();
    private final Map<Long, List<FriendGroup>> groupStore = new HashMap<>();
    private final Map<Long, FriendRemark> remarkStore = new HashMap<>();

    /**
     * 获取用户资料
     */
    public UserProfile getProfile(Long userId) {
        if (!profileStore.containsKey(userId)) {
            profileStore.put(userId, UserProfile.builder()
                    .userId(userId)
                    .nickname("用户" + userId)
                    .onlineStatus("OFFLINE")
                    .updatedAt(LocalDateTime.now())
                    .build());
        }
        return profileStore.get(userId);
    }

    /**
     * 获取多个用户资料 (批量)
     */
    public List<UserProfile> getProfiles(List<Long> userIds) {
        List<UserProfile> profiles = new ArrayList<>();
        for (Long userId : userIds) {
            profiles.add(getProfile(userId));
        }
        return profiles;
    }

    /**
     * 更新用户资料
     */
    public UserProfile updateProfile(Long userId, ProfileUpdateRequest request) {
        UserProfile profile = getProfile(userId);
        if (request.getNickname() != null) profile.setNickname(request.getNickname());
        if (request.getAvatarUrl() != null) profile.setAvatarUrl(request.getAvatarUrl());
        if (request.getBio() != null) profile.setBio(request.getBio());
        if (request.getGender() != null) profile.setGender(request.getGender());
        if (request.getBirthday() != null) profile.setBirthday(request.getBirthday());
        if (request.getEmail() != null) profile.setEmail(request.getEmail());
        if (request.getCountry() != null) profile.setCountry(request.getCountry());
        if (request.getCity() != null) profile.setCity(request.getCity());
        if (request.getLanguage() != null) profile.setLanguage(request.getLanguage());
        if (request.getTimezone() != null) profile.setTimezone(request.getTimezone());
        profile.setUpdatedAt(LocalDateTime.now());
        return profile;
    }

    /**
     * 更新在线状态
     */
    public UserProfile updateOnlineStatus(Long userId, OnlineStatusRequest request) {
        UserProfile profile = getProfile(userId);
        profile.setOnlineStatus(request.getStatus());
        if (request.getStatusText() != null) {
            profile.setStatusText(request.getStatusText());
        }
        profile.setUpdatedAt(LocalDateTime.now());
        return profile;
    }

    /**
     * 上传头像
     */
    public String uploadAvatar(Long userId, String fileData) {
        // 实际应上传到文件存储服务 (MinIO/CDN)
        String avatarUrl = "/uploads/avatars/" + userId + "_" + System.currentTimeMillis() + ".jpg";
        UserProfile profile = getProfile(userId);
        profile.setAvatarUrl(avatarUrl);
        profile.setUpdatedAt(LocalDateTime.now());
        return avatarUrl;
    }

    /**
     * 获取用户的好友分组列表
     */
    public List<FriendGroup> getFriendGroups(Long userId) {
        if (!groupStore.containsKey(userId)) {
            List<FriendGroup> defaultGroups = new ArrayList<>();
            defaultGroups.add(FriendGroup.builder()
                    .id(1L).userId(userId).groupName("默认分组").sortOrder(0).createdAt(LocalDateTime.now()).build());
            defaultGroups.add(FriendGroup.builder()
                    .id(2L).userId(userId).groupName("同事").sortOrder(1).createdAt(LocalDateTime.now()).build());
            defaultGroups.add(FriendGroup.builder()
                    .id(3L).userId(userId).groupName("朋友").sortOrder(2).createdAt(LocalDateTime.now()).build());
            groupStore.put(userId, defaultGroups);
        }
        return groupStore.get(userId);
    }

    /**
     * 创建好友分组
     */
    public FriendGroup createGroup(Long userId, String groupName) {
        List<FriendGroup> groups = getFriendGroups(userId);
        Long newId = groups.stream().map(FriendGroup::getId).max(Long::compare).orElse(0L) + 1;
        FriendGroup group = FriendGroup.builder()
                .id(newId).userId(userId).groupName(groupName)
                .sortOrder(groups.size()).createdAt(LocalDateTime.now()).build();
        groups.add(group);
        return group;
    }

    /**
     * 更新好友备注
     */
    public FriendRemark updateFriendRemark(Long userId, FriendRemarkRequest request) {
        String key = userId + "_" + request.getFriendId();
        FriendRemark remark = remarkStore.getOrDefault(key,
                FriendRemark.builder().userId(userId).friendId(request.getFriendId()).build());
        if (request.getRemarkName() != null) remark.setRemarkName(request.getRemarkName());
        if (request.getGroupId() != null) remark.setGroupId(request.getGroupId());
        if (request.getIsPinned() != null) remark.setIsPinned(request.getIsPinned());
        remark.setUpdatedAt(LocalDateTime.now());
        remarkStore.put(key, remark);
        return remark;
    }

    /**
     * 获取好友备注
     */
    public FriendRemark getFriendRemark(Long userId, Long friendId) {
        String key = userId + "_" + friendId;
        return remarkStore.get(key);
    }
}
