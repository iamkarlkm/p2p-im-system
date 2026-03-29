package com.im.backend.service.impl;

import com.im.backend.dto.BatchMoveFriendRequest;
import com.im.backend.exception.BusinessException;
import com.im.backend.model.FriendGroup;
import com.im.backend.model.FriendGroupMember;
import com.im.backend.model.FriendRelation;
import com.im.backend.repository.FriendGroupMemberRepository;
import com.im.backend.repository.FriendGroupRepository;
import com.im.backend.repository.FriendRelationRepository;
import com.im.backend.service.FriendBatchService;
import com.im.backend.controller.BatchOperationStats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendBatchServiceImpl implements FriendBatchService {

    private final FriendRelationRepository friendRelationRepository;
    private final FriendGroupRepository friendGroupRepository;
    private final FriendGroupMemberRepository groupMemberRepository;

    @Override
    @Transactional
    public void batchMoveToGroup(Long userId, BatchMoveFriendRequest request) {
        List<Long> friendIds = request.getFriendIds();
        Long targetGroupId = request.getTargetGroupId();
        Long sourceGroupId = request.getSourceGroupId();
        Boolean keepOriginal = request.getKeepOriginalGroup();

        // 验证目标分组
        FriendGroup targetGroup = friendGroupRepository.findById(targetGroupId)
                .orElseThrow(() -> new BusinessException("目标分组不存在"));
        
        if (!targetGroup.getUserId().equals(userId)) {
            throw new BusinessException("无权操作该分组");
        }

        // 验证所有好友关系
        List<FriendRelation> friendships = friendRelationRepository
                .findAllByUserIdAndFriendIdIn(userId, friendIds);
        
        if (friendships.size() != friendIds.size()) {
            List<Long> validIds = friendships.stream()
                    .map(FriendRelation::getFriendId)
                    .toList();
            List<Long> invalidIds = friendIds.stream()
                    .filter(id -> !validIds.contains(id))
                    .toList();
            throw new BusinessException("部分好友关系不存在: " + invalidIds);
        }

        // 获取目标分组现有成员
        List<Long> existingMembers = groupMemberRepository
                .findAllByGroupIdAndFriendIdIn(targetGroupId, friendIds)
                .stream()
                .map(FriendGroupMember::getFriendId)
                .toList();

        // 过滤出需要添加的好友
        List<Long> friendsToAdd = friendIds.stream()
                .filter(id -> !existingMembers.contains(id))
                .toList();

        if (!friendsToAdd.isEmpty()) {
            // 批量添加到目标分组
            List<FriendGroupMember> newMembers = friendsToAdd.stream()
                    .map(friendId -> FriendGroupMember.builder()
                            .groupId(targetGroupId)
                            .friendId(friendId)
                            .addedAt(LocalDateTime.now())
                            .build())
                    .toList();
            
            groupMemberRepository.saveAll(newMembers);
            log.info("用户 {} 批量添加 {} 个好友到分组 {}", userId, newMembers.size(), targetGroupId);
        }

        // 如果需要从源分组移除
        if (sourceGroupId != null && !keepOriginal) {
            batchRemoveFromGroup(userId, sourceGroupId, friendIds);
        }

        log.info("用户 {} 批量移动 {} 个好友到分组 {} 完成", userId, friendIds.size(), targetGroupId);
    }

    @Override
    @Transactional
    public void batchRemoveFromGroup(Long userId, Long groupId, List<Long> friendIds) {
        // 验证分组权限
        FriendGroup group = friendGroupRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException("分组不存在"));
        
        if (!group.getUserId().equals(userId)) {
            throw new BusinessException("无权操作该分组");
        }

        // 批量删除
        int deleted = groupMemberRepository.deleteByGroupIdAndFriendIdIn(groupId, friendIds);
        log.info("用户 {} 从分组 {} 移除 {} 个好友", userId, groupId, deleted);
    }

    @Override
    @Transactional
    public void batchSetStar(Long userId, List<Long> friendIds, Boolean isStarred) {
        // 验证好友关系
        List<FriendRelation> friendships = friendRelationRepository
                .findAllByUserIdAndFriendIdIn(userId, friendIds);
        
        if (friendships.size() != friendIds.size()) {
            throw new BusinessException("部分好友关系不存在");
        }

        // 批量更新
        friendships.forEach(friendship -> friendship.setStarred(isStarred));
        friendRelationRepository.saveAll(friendships);
        
        log.info("用户 {} 批量设置 {} 个好友星标状态为 {}", userId, friendIds.size(), isStarred);
    }

    @Override
    @Transactional
    public void batchSetMute(Long userId, List<Long> friendIds, Boolean isMuted) {
        // 验证好友关系
        List<FriendRelation> friendships = friendRelationRepository
                .findAllByUserIdAndFriendIdIn(userId, friendIds);
        
        if (friendships.size() != friendIds.size()) {
            throw new BusinessException("部分好友关系不存在");
        }

        // 批量更新
        friendships.forEach(friendship -> friendship.setMuted(isMuted));
        friendRelationRepository.saveAll(friendships);
        
        log.info("用户 {} 批量设置 {} 个好友免打扰状态为 {}", userId, friendIds.size(), isMuted);
    }

    @Override
    public List<Long> getFriendsInGroup(Long userId, Long groupId) {
        // 验证分组权限
        FriendGroup group = friendGroupRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException("分组不存在"));
        
        if (!group.getUserId().equals(userId)) {
            throw new BusinessException("无权访问该分组");
        }

        return groupMemberRepository.findAllByGroupId(groupId)
                .stream()
                .map(FriendGroupMember::getFriendId)
                .toList();
    }

    @Override
    public BatchOperationStats getBatchOperationStats(Long userId) {
        // 获取所有分组
        List<FriendGroup> groups = friendGroupRepository.findAllByUserId(userId);
        
        // 获取分组统计
        Map<Long, Long> groupMemberCounts = groups.stream()
                .collect(Collectors.toMap(
                        FriendGroup::getId,
                        g -> groupMemberRepository.countByGroupId(g.getId())
                ));

        // 获取好友统计
        long totalFriends = friendRelationRepository.countByUserId(userId);
        long starredFriends = friendRelationRepository.countByUserIdAndStarredTrue(userId);
        long mutedFriends = friendRelationRepository.countByUserIdAndMutedTrue(userId);
        long ungroupedFriends = totalFriends - groupMemberCounts.values().stream()
                .mapToLong(Long::longValue).sum();

        return BatchOperationStats.builder()
                .totalFriends(totalFriends)
                .groupedFriends(totalFriends - ungroupedFriends)
                .ungroupedFriends(Math.max(0, ungroupedFriends))
                .starredFriends(starredFriends)
                .mutedFriends(mutedFriends)
                .groupCount(groups.size())
                .groupStats(groups.stream()
                        .map(g -> GroupStat.builder()
                                .groupId(g.getId())
                                .groupName(g.getName())
                                .memberCount(groupMemberCounts.getOrDefault(g.getId(), 0L))
                                .sortOrder(g.getSortOrder())
                                .build())
                        .toList())
                .build();
    }
}
