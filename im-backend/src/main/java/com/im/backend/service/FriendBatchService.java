package com.im.backend.service;

import com.im.backend.dto.BatchMoveFriendRequest;
import com.im.backend.controller.BatchOperationStats;

import java.util.List;

public interface FriendBatchService {

    /**
     * 批量移动好友到分组
     */
    void batchMoveToGroup(Long userId, BatchMoveFriendRequest request);

    /**
     * 批量从分组移除好友
     */
    void batchRemoveFromGroup(Long userId, Long groupId, List<Long> friendIds);

    /**
     * 批量设置星标好友
     */
    void batchSetStar(Long userId, List<Long> friendIds, Boolean isStarred);

    /**
     * 批量设置消息免打扰
     */
    void batchSetMute(Long userId, List<Long> friendIds, Boolean isMuted);

    /**
     * 获取分组中的好友列表
     */
    List<Long> getFriendsInGroup(Long userId, Long groupId);

    /**
     * 获取批量操作统计
     */
    BatchOperationStats getBatchOperationStats(Long userId);
}
