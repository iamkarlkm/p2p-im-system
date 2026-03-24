package com.im.server.service;

import com.im.server.entity.Friend;
import com.im.server.entity.FriendRequest;
import com.im.server.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 好友请求服务
 */
@Service
@RequiredArgsConstructor
public class FriendRequestService {

    private final FriendRequestRepository friendRequestRepository;
    private final FriendService friendService;
    private final UserService userService;

    /**
     * 发送好友请求
     */
    public FriendRequest sendRequest(Long fromUserId, Long toUserId, String message) {
        // 不能添加自己为好友
        if (fromUserId.equals(toUserId)) {
            throw new RuntimeException("不能添加自己为好友");
        }
        
        // 检查目标用户是否存在
        User toUser = userService.getUserById(toUserId);
        if (toUser == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 检查是否已经是好友
        if (friendService.isFriend(fromUserId, toUserId)) {
            throw new RuntimeException("你们已经是好友了");
        }
        
        // 检查是否已存在待处理的请求
        if (friendRequestRepository.existsPendingRequest(fromUserId, toUserId)) {
            throw new RuntimeException("已存在待处理的好友请求");
        }
        
        // 创建好友请求
        FriendRequest request = new FriendRequest();
        request.setFromUserId(fromUserId);
        request.setToUserId(toUserId);
        request.setMessage(message);
        request.setStatus(FriendRequest.STATUS_PENDING);
        request.setCreateTime(LocalDateTime.now());
        
        return friendRequestRepository.save(request);
    }

    /**
     * 获取接收者的待处理请求列表
     */
    public List<FriendRequest> getPendingRequests(Long userId) {
        List<FriendRequest> requests = friendRequestRepository.findPendingRequests(userId);
        
        // 填充用户信息
        for (FriendRequest request : requests) {
            request.setFromUser(userService.getUserById(request.getFromUserId()));
        }
        
        return requests;
    }

    /**
     * 获取发送的好友请求列表
     */
    public List<FriendRequest> getSentRequests(Long userId) {
        List<FriendRequest> requests = friendRequestRepository.findSentRequests(userId);
        
        // 填充用户信息
        for (FriendRequest request : requests) {
            request.setToUser(userService.getUserById(request.getToUserId()));
        }
        
        return requests;
    }

    /**
     * 同意好友请求
     */
    public FriendRequest acceptRequest(Long requestId, Long userId) {
        FriendRequest request = friendRequestRepository.findById(requestId);
        
        if (request == null) {
            throw new RuntimeException("请求不存在");
        }
        
        // 只有接收者可以同意
        if (!request.getToUserId().equals(userId)) {
            throw new RuntimeException("无权操作");
        }
        
        // 检查请求状态
        if (request.getStatus() != FriendRequest.STATUS_PENDING) {
            throw new RuntimeException("请求已处理");
        }
        
        // 创建双向好友关系
        friendService.addFriend(request.getFromUserId(), request.getToUserId(), null);
        friendService.addFriend(request.getToUserId(), request.getFromUserId(), null);
        
        // 更新请求状态
        request.setStatus(FriendRequest.STATUS_ACCEPTED);
        request.setHandleTime(LocalDateTime.now());
        
        return friendRequestRepository.save(request);
    }

    /**
     * 拒绝好友请求
     */
    public FriendRequest rejectRequest(Long requestId, Long userId) {
        FriendRequest request = friendRequestRepository.findById(requestId);
        
        if (request == null) {
            throw new RuntimeException("请求不存在");
        }
        
        // 只有接收者可以拒绝
        if (!request.getToUserId().equals(userId)) {
            throw new RuntimeException("无权操作");
        }
        
        // 检查请求状态
        if (request.getStatus() != FriendRequest.STATUS_PENDING) {
            throw new RuntimeException("请求已处理");
        }
        
        // 更新请求状态
        request.setStatus(FriendRequest.STATUS_REJECTED);
        request.setHandleTime(LocalDateTime.now());
        
        return friendRequestRepository.save(request);
    }

    /**
     * 取消好友请求
     */
    public void cancelRequest(Long requestId, Long userId) {
        FriendRequest request = friendRequestRepository.findById(requestId);
        
        if (request == null) {
            throw new RuntimeException("请求不存在");
        }
        
        // 只有发送者可以取消
        if (!request.getFromUserId().equals(userId)) {
            throw new RuntimeException("无权操作");
        }
        
        // 删除请求
        friendRequests.removeIf(r -> r.getId().equals(requestId));
    }

    /**
     * 获取好友请求详情
     */
    public FriendRequest getRequestById(Long requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId);
        
        if (request != null) {
            request.setFromUser(userService.getUserById(request.getFromUserId()));
            request.setToUser(userService.getUserById(request.getToUserId()));
        }
        
        return request;
    }

    /**
     * 获取未读请求数量
     */
    public int getUnreadCount(Long userId) {
        return (int) friendRequestRepository.findPendingRequests(userId).size();
    }

    // 辅助引用，用于删除操作
    private final java.util.List<FriendRequest> friendRequests = new java.util.ArrayList<>();
}
