package com.im.backend.service;

import com.im.backend.entity.ChannelEntity;
import com.im.backend.entity.ChannelMemberEntity;
import com.im.backend.repository.ChannelRepository;
import com.im.backend.repository.ChannelMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final ChannelMemberRepository channelMemberRepository;

    public ChannelService(ChannelRepository channelRepository, ChannelMemberRepository channelMemberRepository) {
        this.channelRepository = channelRepository;
        this.channelMemberRepository = channelMemberRepository;
    }

    @Transactional
    public ChannelEntity createChannel(String groupId, String name, String channelType, String userId) {
        if (channelRepository.existsByGroupIdAndNameAndStatusNot(groupId, name, "DELETED")) {
            throw new IllegalStateException("频道名称已存在");
        }

        Integer maxOrder = channelRepository.findMaxRootSortOrder(groupId);

        ChannelEntity channel = new ChannelEntity();
        channel.setChannelId(UUID.randomUUID().toString());
        channel.setGroupId(groupId);
        channel.setName(name);
        channel.setChannelType(channelType != null ? channelType : "GENERAL");
        channel.setSortOrder(maxOrder + 1);
        channel.setIsPublic(true);
        channel.setRequiredRole("MEMBER");
        channel.setMessageCount(0L);
        channel.setCreatedBy(userId);
        channel.setCreatedAt(LocalDateTime.now());
        channel.setStatus("ACTIVE");

        return channelRepository.save(channel);
    }

    @Transactional
    public ChannelEntity updateChannel(String channelId, String name, String description, String icon, String requiredRole, Boolean isPublic) {
        ChannelEntity channel = channelRepository.findByChannelId(channelId)
            .orElseThrow(() -> new IllegalArgumentException("频道不存在"));

        if (name != null) channel.setName(name);
        if (description != null) channel.setDescription(description);
        if (icon != null) channel.setIcon(icon);
        if (requiredRole != null) channel.setRequiredRole(requiredRole);
        if (isPublic != null) channel.setIsPublic(isPublic);
        channel.setUpdatedAt(LocalDateTime.now());

        return channelRepository.save(channel);
    }

    public Optional<ChannelEntity> getChannel(String channelId) {
        return channelRepository.findByChannelId(channelId);
    }

    public List<ChannelEntity> getChannelsByGroup(String groupId) {
        return channelRepository.findByGroupIdAndStatusOrderBySortOrderAsc(groupId, "ACTIVE");
    }

    public List<ChannelEntity> getRootChannels(String groupId) {
        return channelRepository.findRootChannels(groupId);
    }

    public List<ChannelEntity> getChildChannels(String parentChannelId) {
        return channelRepository.findChildChannels(parentChannelId);
    }

    @Transactional
    public ChannelEntity archiveChannel(String channelId) {
        ChannelEntity channel = channelRepository.findByChannelId(channelId)
            .orElseThrow(() -> new IllegalArgumentException("频道不存在"));
        channel.setStatus("ARCHIVED");
        channel.setUpdatedAt(LocalDateTime.now());
        return channelRepository.save(channel);
    }

    @Transactional
    public void deleteChannel(String channelId) {
        ChannelEntity channel = channelRepository.findByChannelId(channelId)
            .orElseThrow(() -> new IllegalArgumentException("频道不存在"));
        channel.setStatus("DELETED");
        channel.setUpdatedAt(LocalDateTime.now());
        channelRepository.save(channel);
    }

    @Transactional
    public void reorderChannels(String groupId, List<String> channelIds) {
        for (int i = 0; i < channelIds.size(); i++) {
            channelRepository.findByChannelId(channelIds.get(i)).ifPresent(channel -> {
                channel.setSortOrder(i);
                channel.setUpdatedAt(LocalDateTime.now());
                channelRepository.save(channel);
            });
        }
    }

    @Transactional
    public void incrementMessageCount(String channelId) {
        channelRepository.findByChannelId(channelId).ifPresent(channel -> {
            channel.setMessageCount(channel.getMessageCount() + 1);
            channel.setLastMessageAt(LocalDateTime.now());
            channelRepository.save(channel);
        });
    }

    @Transactional
    public void addMember(String channelId, String userId, String role, String invitedBy) {
        if (channelMemberRepository.existsByChannelIdAndUserId(channelId, userId)) {
            throw new IllegalStateException("用户已在频道中");
        }

        ChannelMemberEntity member = new ChannelMemberEntity();
        member.setMemberId(UUID.randomUUID().toString());
        member.setChannelId(channelId);
        member.setUserId(userId);
        member.setRole(role != null ? role : "MEMBER");
        member.setNotificationsEnabled(true);
        member.setNotificationLevel("ALL");
        member.setJoinedAt(LocalDateTime.now());
        member.setJoinMethod(invitedBy != null ? "INVITE" : "AUTO");
        member.setInvitedBy(invitedBy);
        member.setUnreadCount(0L);
        member.setStatus("ACTIVE");

        channelMemberRepository.save(member);
    }

    @Transactional
    public void removeMember(String channelId, String userId) {
        channelMemberRepository.findByChannelIdAndUserId(channelId, userId).ifPresent(member -> {
            member.setStatus("DELETED");
            channelMemberRepository.save(member);
        });
    }

    public List<ChannelMemberEntity> getMembers(String channelId) {
        return channelMemberRepository.findByChannelIdAndStatusOrderByRoleAscJoinedAtAsc(channelId, "ACTIVE");
    }

    public boolean hasPermission(String channelId, String userId, String requiredRole) {
        return channelMemberRepository.findRoleByChannelAndUser(channelId, userId)
            .map(role -> {
                int userLevel = getRoleLevel(role);
                int requiredLevel = getRoleLevel(requiredRole);
                return userLevel >= requiredLevel;
            })
            .orElse(false);
    }

    private int getRoleLevel(String role) {
        return switch (role) {
            case "OWNER" -> 5;
            case "ADMIN" -> 4;
            case "MODERATOR" -> 3;
            case "MEMBER" -> 2;
            case "GUEST" -> 1;
            default -> 0;
        };
    }

    public long getMemberCount(String channelId) {
        return channelMemberRepository.countByChannelIdAndStatus(channelId, "ACTIVE");
    }

    public List<ChannelEntity> getUserChannels(String userId) {
        return channelRepository.findByCreatedByOrderByCreatedAtDesc(userId);
    }
}
