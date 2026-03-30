package com.im.service.impl;

import com.im.entity.Friendship;
import com.im.entity.Friendship.FriendshipStatus;
import com.im.service.IFriendshipService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 好友关系服务实现类
 * 功能 #4: 好友关系管理系统
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
@Service
public class FriendshipServiceImpl implements IFriendshipService {
    
    // 内存存储，实际项目使用数据库
    private final Map<String, Friendship> friendships = new ConcurrentHashMap<>();
    
    @Override
    public Friendship sendFriendRequest(String userId, String friendId, String message) {
        // 检查是否已经是好友
        if (isFriend(userId, friendId)) {
            throw new RuntimeException("Already friends");
        }
        
        Friendship friendship = new Friendship();
        friendship.setFriendshipId(UUID.randomUUID().toString());
        friendship.setUserId(userId);
        friendship.setFriendId(friendId);
        friendship.setRequestMessage(message);
        friendship.setStatus(FriendshipStatus.PENDING);
        friendship.setCreateTime(LocalDateTime.now());
        
        friendships.put(friendship.getFriendshipId(), friendship);
        return friendship;
    }
    
    @Override
    public boolean acceptFriendRequest(String friendshipId) {
        Friendship friendship = friendships.get(friendshipId);
        if (friendship == null || friendship.getStatus() != FriendshipStatus.PENDING) {
            return false;
        }
        
        friendship.accept();
        
        // 创建双向关系
        Friendship reverse = new Friendship();
        reverse.setFriendshipId(UUID.randomUUID().toString());
        reverse.setUserId(friendship.getFriendId());
        reverse.setFriendId(friendship.getUserId());
        reverse.setStatus(FriendshipStatus.ACCEPTED);
        reverse.setAcceptTime(LocalDateTime.now());
        reverse.setCreateTime(LocalDateTime.now());
        friendships.put(reverse.getFriendshipId(), reverse);
        
        return true;
    }
    
    @Override
    public boolean rejectFriendRequest(String friendshipId) {
        Friendship friendship = friendships.get(friendshipId);
        if (friendship == null) return false;
        
        friendship.reject();
        return true;
    }
    
    @Override
    public boolean deleteFriend(String userId, String friendId) {
        friendships.values().removeIf(f -> 
            (f.getUserId().equals(userId) && f.getFriendId().equals(friendId)) ||
            (f.getUserId().equals(friendId) && f.getFriendId().equals(userId))
        );
        return true;
    }
    
    @Override
    public List<Friendship> getFriendList(String userId) {
        return friendships.values().stream()
            .filter(f -> f.getUserId().equals(userId) && f.getStatus() == FriendshipStatus.ACCEPTED)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Friendship> getPendingRequests(String userId) {
        return friendships.values().stream()
            .filter(f -> f.getFriendId().equals(userId) && f.getStatus() == FriendshipStatus.PENDING)
            .collect(Collectors.toList());
    }
    
    @Override
    public boolean updateRemark(String userId, String friendId, String remark) {
        Friendship friendship = friendships.values().stream()
            .filter(f -> f.getUserId().equals(userId) && f.getFriendId().equals(friendId))
            .findFirst()
            .orElse(null);
        
        if (friendship != null) {
            friendship.setRemark(remark);
            friendship.setUpdateTime(LocalDateTime.now());
            return true;
        }
        return false;
    }
    
    @Override
    public boolean moveToGroup(String userId, String friendId, String groupId) {
        Friendship friendship = friendships.values().stream()
            .filter(f -> f.getUserId().equals(userId) && f.getFriendId().equals(friendId))
            .findFirst()
            .orElse(null);
        
        if (friendship != null) {
            friendship.setGroupId(groupId);
            friendship.setUpdateTime(LocalDateTime.now());
            return true;
        }
        return false;
    }
    
    @Override
    public boolean isFriend(String userId, String friendId) {
        return friendships.values().stream()
            .anyMatch(f -> 
                f.getUserId().equals(userId) && 
                f.getFriendId().equals(friendId) && 
                f.getStatus() == FriendshipStatus.ACCEPTED
            );
    }
    
    @Override
    public boolean blockFriend(String userId, String friendId) {
        Friendship friendship = friendships.values().stream()
            .filter(f -> f.getUserId().equals(userId) && f.getFriendId().equals(friendId))
            .findFirst()
            .orElse(null);
        
        if (friendship != null) {
            friendship.block();
            return true;
        }
        return false;
    }
}
