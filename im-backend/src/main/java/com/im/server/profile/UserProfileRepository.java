package com.im.server.profile;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

/**
 * 用户资料仓库（内存实现）
 */
@Repository
public class UserProfileRepository {

    private final Map<String, UserProfile> profiles = new ConcurrentHashMap<>();
    private final Map<String, Map<String, UserProfile.FriendRemark>> friendRemarks = new ConcurrentHashMap<>();
    private final Map<String, List<UserProfile.FriendGroup>> friendGroups = new ConcurrentHashMap<>();

    public UserProfile save(UserProfile profile) {
        profile.setUpdatedAt(java.time.Instant.now());
        profiles.put(profile.getUserId(), profile);
        return profile;
    }

    public Optional<UserProfile> findById(String userId) {
        return Optional.ofNullable(profiles.get(userId));
    }

    public Optional<UserProfile> findByEmail(String email) {
        return profiles.values().stream()
                .filter(p -> email.equals(p.getEmail()))
                .findFirst();
    }

    public List<UserProfile> findByIds(List<String> userIds) {
        List<UserProfile> result = new ArrayList<>();
        for (String id : userIds) {
            profiles.get(id).ifPresent(result::add);
        }
        return result;
    }

    public void deleteById(String userId) {
        profiles.remove(userId);
        friendRemarks.remove(userId);
    }

    public UserProfile.FriendRemark saveFriendRemark(String userId, UserProfile.FriendRemark remark) {
        friendRemarks.computeIfAbsent(userId, k -> new ConcurrentHashMap<>())
                .put(remark.getFriendId(), remark);
        return remark;
    }

    public Optional<UserProfile.FriendRemark> findFriendRemark(String userId, String friendId) {
        Map<String, UserProfile.FriendRemark> remarks = friendRemarks.get(userId);
        if (remarks == null) return Optional.empty();
        return Optional.ofNullable(remarks.get(friendId));
    }

    public List<UserProfile.FriendRemark> findAllFriendRemarks(String userId) {
        Map<String, UserProfile.FriendRemark> remarks = friendRemarks.get(userId);
        if (remarks == null) return List.of();
        return new ArrayList<>(remarks.values());
    }

    public void deleteFriendRemark(String userId, String friendId) {
        Map<String, UserProfile.FriendRemark> remarks = friendRemarks.get(userId);
        if (remarks != null) remarks.remove(friendId);
    }

    public UserProfile.FriendGroup saveFriendGroup(String userId, UserProfile.FriendGroup group) {
        friendGroups.computeIfAbsent(userId, k -> new ArrayList<>()).add(group);
        return group;
    }

    public List<UserProfile.FriendGroup> findFriendGroups(String userId) {
        return friendGroups.getOrDefault(userId, List.of());
    }

    public void deleteFriendGroup(String userId, String groupId) {
        List<UserProfile.FriendGroup> groups = friendGroups.get(userId);
        if (groups != null) {
            groups.removeIf(g -> groupId.equals(g.getGroupId()));
        }
    }

    public List<UserProfile> searchByNickname(String keyword, int limit) {
        return profiles.values().stream()
                .filter(p -> p.getNickname().toLowerCase().contains(keyword.toLowerCase()))
                .limit(limit)
                .toList();
    }
}
