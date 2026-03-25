package com.im.server.profile;

import java.time.Instant;
import java.util.*;
import org.springframework.stereotype.Service;

/**
 * 用户资料服务
 */
@Service
public class UserProfileService {

    private final UserProfileRepository repository;
    private final Set<String> onlineUsers = Collections.synchronizedSet(new HashSet<>());

    public UserProfileService(UserProfileRepository repository) {
        this.repository = repository;
    }

    public UserProfile createProfile(String userId) {
        if (repository.findById(userId).isPresent()) {
            throw new ProfileException("Profile already exists for user: " + userId);
        }
        UserProfile profile = new UserProfile(userId);
        return repository.save(profile);
    }

    public UserProfile getProfile(String userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new ProfileException("Profile not found: " + userId));
    }

    public UserProfile getOrCreateProfile(String userId) {
        return repository.findById(userId).orElseGet(() -> createProfile(userId));
    }

    public UserProfile updateProfile(String userId, ProfileUpdateRequest request) {
        UserProfile profile = getProfile(userId);
        if (request.nickname != null) profile.setNickname(request.nickname);
        if (request.avatarUrl != null) profile.setAvatarUrl(request.avatarUrl);
        if (request.signature != null) profile.setSignature(request.signature);
        if (request.email != null) profile.setEmail(request.email);
        if (request.phone != null) profile.setPhone(request.phone);
        if (request.gender != null) profile.setGender(request.gender);
        if (request.birthday != null) profile.setBirthday(request.birthday);
        if (request.region != null) profile.setRegion(request.region);
        if (request.language != null) profile.setLanguage(request.language);
        if (request.status != null) profile.setStatus(request.status);
        if (request.customStatus != null) profile.setCustomStatus(request.customStatus);
        profile.setUpdatedAt(Instant.now());
        return repository.save(profile);
    }

    public UserProfile updateAvatar(String userId, String avatarUrl) {
        UserProfile profile = getProfile(userId);
        profile.setAvatarUrl(avatarUrl);
        profile.setUpdatedAt(Instant.now());
        return repository.save(profile);
    }

    public UserProfile updateNickname(String userId, String nickname) {
        UserProfile profile = getProfile(userId);
        profile.setNickname(nickname);
        profile.setUpdatedAt(Instant.now());
        return repository.save(profile);
    }

    public UserProfile updateSignature(String userId, String signature) {
        UserProfile profile = getProfile(userId);
        profile.setSignature(signature);
        profile.setUpdatedAt(Instant.now());
        return repository.save(profile);
    }

    public UserProfile updateStatus(String userId, UserProfile.UserStatus status) {
        UserProfile profile = getProfile(userId);
        profile.setStatus(status);
        profile.setUpdatedAt(Instant.now());
        return repository.save(profile);
    }

    public void setUserOnline(String userId) {
        onlineUsers.add(userId);
    }

    public void setUserOffline(String userId) {
        onlineUsers.remove(userId);
    }

    public boolean isUserOnline(String userId) {
        return onlineUsers.contains(userId);
    }

    public List<String> getOnlineUsers() {
        return new ArrayList<>(onlineUsers);
    }

    // 好友备注
    public UserProfile.FriendRemark setFriendRemark(String userId, String friendId, String remark, String groupName, List<String> tags) {
        UserProfile.FriendRemark fr = new UserProfile.FriendRemark(friendId, remark, groupName);
        if (tags != null) fr.setTags(tags);
        fr.setUpdatedAt(Instant.now());
        return repository.saveFriendRemark(userId, fr);
    }

    public Optional<UserProfile.FriendRemark> getFriendRemark(String userId, String friendId) {
        return repository.findFriendRemark(userId, friendId);
    }

    public List<UserProfile.FriendRemark> getAllFriendRemarks(String userId) {
        return repository.findAllFriendRemarks(userId);
    }

    public void removeFriendRemark(String userId, String friendId) {
        repository.deleteFriendRemark(userId, friendId);
    }

    // 好友分组
    public UserProfile.FriendGroup createFriendGroup(String userId, String name, int sortOrder) {
        UserProfile.FriendGroup group = new UserProfile.FriendGroup(UUID.randomUUID().toString(), name, sortOrder);
        return repository.saveFriendGroup(userId, group);
    }

    public List<UserProfile.FriendGroup> getFriendGroups(String userId) {
        return repository.findFriendGroups(userId);
    }

    public void deleteFriendGroup(String userId, String groupId) {
        repository.deleteFriendGroup(userId, groupId);
    }

    // 搜索用户
    public List<UserProfile> searchUsers(String keyword, int limit) {
        return repository.searchByNickname(keyword, limit > 0 ? limit : 20);
    }

    // 批量获取用户资料
    public Map<String, UserProfile> getProfiles(List<String> userIds) {
        Map<String, UserProfile> result = new HashMap<>();
        for (String uid : userIds) {
            repository.findById(uid).ifPresent(p -> result.put(uid, p));
        }
        return result;
    }

    // 资料差异（隐私保护）
    public UserProfile getPublicProfile(String userId) {
        UserProfile full = getProfile(userId);
        UserProfile pub = new UserProfile(userId);
        pub.setNickname(full.getNickname());
        pub.setAvatarUrl(full.getAvatarUrl());
        pub.setSignature(full.getSignature());
        pub.setStatus(full.getStatus());
        return pub;
    }

    public static class ProfileUpdateRequest {
        public String nickname;
        public String avatarUrl;
        public String signature;
        public String email;
        public String phone;
        public String gender;
        public String birthday;
        public String region;
        public String language;
        public UserProfile.UserStatus status;
        public Map<String, Object> customStatus;
    }
}
