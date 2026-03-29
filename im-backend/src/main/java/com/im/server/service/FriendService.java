package com.im.server.service;

import com.im.server.entity.Friend;
import com.im.server.entity.User;
import com.im.server.repository.FriendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 好友服务
 */
@Service
@RequiredArgsConstructor
public class FriendService {
    
    private final FriendRepository friendRepository;
    
    /**
     * 添加好友
     */
    @Transactional
    public Friend addFriend(Long userId, Long friendId, String remark) {
        // 检查是否已经是好友
        if (friendRepository.existsByUserIdAndFriendIdAndStatus(userId, friendId, 1)) {
            throw new RuntimeException("已经是好友了");
        }
        
        // 创建好友关系
        Friend friend = new Friend();
        friend.setUserId(userId);
        friend.setFriendId(friendId);
        friend.setFriendRemark(remark);
        friend.setStatus(1);
        
        return friendRepository.save(friend);
    }
    
    /**
     * 删除好友
     */
    @Transactional
    public void deleteFriend(Long userId, Long friendId) {
        Friend friend = friendRepository.findByUserIdAndFriendId(userId, friendId);
        if (friend != null) {
            friendRepository.delete(friend);
        }
    }
    
    /**
     * 拉黑好友
     */
    @Transactional
    public void blockFriend(Long userId, Long friendId) {
        Friend friend = friendRepository.findByUserIdAndFriendId(userId, friendId);
        if (friend != null) {
            friend.setStatus(2); // 拉黑
            friendRepository.save(friend);
        }
    }
    
    /**
     * 获取好友列表
     */
    public List<Friend> getFriends(Long userId) {
        return friendRepository.findByUserIdAndStatus(userId, 1);
    }
    
    /**
     * 检查是否为好友
     */
    public boolean isFriend(Long userId, Long friendId) {
        return friendRepository.existsByUserIdAndFriendIdAndStatus(userId, friendId, 1);
    }
    
    /**
     * 批量获取好友信息
     */
    public List<Friend> getFriendsByUserIds(Long userId, List<Long> friendIds) {
        return friendRepository.findByUserIdAndStatus(userId, 1).stream()
                .filter(f -> friendIds.contains(f.getFriendId()))
                .toList();
    }
}
