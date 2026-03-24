package com.im.server.profile;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * 用户资料实体
 */
public class UserProfile {
    private String userId;
    private String nickname;
    private String avatarUrl;
    private String signature;
    private String email;
    private String phone;
    private String gender;
    private String birthday;
    private String region;
    private String language;
    private UserStatus status;
    private Map<String, Object> customStatus;
    private Instant createdAt;
    private Instant updatedAt;

    public enum UserStatus {
        ONLINE, AWAY, BUSY, DO_NOT_DISTURB, INVISIBLE, OFFLINE
    }

    public static class FriendRemark {
        private String friendId;
        private String remark;
        private String groupName;
        private List<String> tags;
        private Instant updatedAt;

        public FriendRemark() {}

        public FriendRemark(String friendId, String remark, String groupName) {
            this.friendId = friendId;
            this.remark = remark;
            this.groupName = groupName;
            this.tags = List.of();
            this.updatedAt = Instant.now();
        }

        public String getFriendId() { return friendId; }
        public void setFriendId(String friendId) { this.friendId = friendId; }
        public String getRemark() { return remark; }
        public void setRemark(String remark) { this.remark = remark; }
        public String getGroupName() { return groupName; }
        public void setGroupName(String groupName) { this.groupName = groupName; }
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
        public Instant getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    }

    public static class FriendGroup {
        private String groupId;
        private String name;
        private int sortOrder;
        private int memberCount;

        public FriendGroup() {}

        public FriendGroup(String groupId, String name, int sortOrder) {
            this.groupId = groupId;
            this.name = name;
            this.sortOrder = sortOrder;
            this.memberCount = 0;
        }

        public String getGroupId() { return groupId; }
        public void setGroupId(String groupId) { this.groupId = groupId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getSortOrder() { return sortOrder; }
        public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
        public int getMemberCount() { return memberCount; }
        public void setMemberCount(int memberCount) { this.memberCount = memberCount; }
    }

    public UserProfile() {}

    public UserProfile(String userId) {
        this.userId = userId;
        this.nickname = "";
        this.avatarUrl = "";
        this.signature = "";
        this.status = UserStatus.ONLINE;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getBirthday() { return birthday; }
    public void setBirthday(String birthday) { this.birthday = birthday; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }
    public Map<String, Object> getCustomStatus() { return customStatus; }
    public void setCustomStatus(Map<String, Object> customStatus) { this.customStatus = customStatus; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
