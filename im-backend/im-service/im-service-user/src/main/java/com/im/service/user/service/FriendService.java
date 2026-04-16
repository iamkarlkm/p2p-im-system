package com.im.service.user.service;

import com.im.service.user.dto.*;
import com.im.service.user.entity.Friend;
import com.im.service.user.entity.User;
import com.im.service.user.repository.FriendRepository;
import com.im.service.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 好友关系服务 - 专注好友关系管理
 * 
 * @author IM Team
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    /**
     * 获取好友统计信息
     */
    public Map<String, Long> getFriendStats(Long userId) {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", friendRepository.countFriends(userId));
        stats.put("pendingReceived", friendRepository.countPendingReceived(userId));
        stats.put("starred", friendRepository.countStarredFriends(userId));
        stats.put("blocked", friendRepository.countBlocked(userId));
        return stats;
    }

    /**
     * 获取置顶好友列表
     */
    public List<Friend> getPinnedFriends(Long userId) {
        return friendRepository.findPinnedFriends(userId);
    }

    /**
     * 获取星标好友列表
     */
    public List<Friend> getStarredFriends(Long userId) {
        return friendRepository.findStarredFriends(userId);
    }

    /**
     * 检查是否为好友
     */
    public boolean areFriends(Long userId1, Long userId2) {
        return friendRepository.areFriends(userId1, userId2);
    }

    /**
     * 检查是否被屏蔽
     */
    public boolean isBlocked(Long userId, Long friendId) {
        return friendRepository.isBlocked(userId, friendId);
    }

    /**
     * 更新最后聊天时间
     */
    @Transactional
    public void updateLastChatTime(Long userId, Long friendId) {
        Friend friend = friendRepository.findByUserIdAndFriendId(userId, friendId);
        if (friend != null) {
            friend.updateLastChatAt();
            friendRepository.updateById(friend);
        }
    }

    /**
     * 批量获取好友关系
     */
    public List<Friend> getFriendsByIds(Long userId, List<Long> friendIds) {
        return friendRepository.findByFriendIds(userId, friendIds);
    }
}
