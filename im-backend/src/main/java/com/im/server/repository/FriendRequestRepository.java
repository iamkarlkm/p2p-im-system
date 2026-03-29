package com.im.server.repository;

import com.im.server.entity.FriendRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 好友请求Repository
 */
@Repository
public class FriendRequestRepository {
    
    // 使用内存存储（实际项目中应使用数据库）
    private final java.util.List<FriendRequest> friendRequests = new java.util.ArrayList<>();
    private Long idCounter = 1L;
    
    /**
     * 保存好友请求
     */
    public FriendRequest save(FriendRequest request) {
        if (request.getId() == null) {
            request.setId(idCounter++);
        }
        
        // 检查是否已存在待处理的请求
        for (int i = 0; i < friendRequests.size(); i++) {
            FriendRequest existing = friendRequests.get(i);
            if (existing.getFromUserId().equals(request.getFromUserId()) 
                    && existing.getToUserId().equals(request.getToUserId())
                    && existing.getStatus() == FriendRequest.STATUS_PENDING) {
                // 更新现有请求
                friendRequests.set(i, request);
                return request;
            }
        }
        
        friendRequests.add(request);
        return request;
    }
    
    /**
     * 根据ID查询
     */
    public FriendRequest findById(Long id) {
        return friendRequests.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 查询发送给指定用户的待处理请求
     */
    public List<FriendRequest> findPendingRequests(Long toUserId) {
        return friendRequests.stream()
                .filter(r -> r.getToUserId().equals(toUserId))
                .filter(r -> r.getStatus() == FriendRequest.STATUS_PENDING)
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * 查询用户发送的好友请求
     */
    public List<FriendRequest> findSentRequests(Long fromUserId) {
        return friendRequests.stream()
                .filter(r -> r.getFromUserId().equals(fromUserId))
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * 检查是否已经存在好友关系或待处理请求
     */
    public boolean existsPendingRequest(Long fromUserId, Long toUserId) {
        return friendRequests.stream()
                .anyMatch(r -> 
                    ((r.getFromUserId().equals(fromUserId) && r.getToUserId().equals(toUserId)) ||
                     (r.getFromUserId().equals(toUserId) && r.getToUserId().equals(fromUserId)))
                    && r.getStatus() == FriendRequest.STATUS_PENDING);
    }
    
    /**
     * 更新请求状态
     */
    public FriendRequest updateStatus(Long id, Integer status) {
        FriendRequest request = findById(id);
        if (request != null) {
            request.setStatus(status);
            request.setHandleTime(java.time.LocalDateTime.now());
        }
        return request;
    }
    
    /**
     * 查询所有请求
     */
    public List<FriendRequest> findAll() {
        return new java.util.ArrayList<>(friendRequests);
    }
}
