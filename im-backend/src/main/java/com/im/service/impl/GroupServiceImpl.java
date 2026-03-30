package com.im.service.impl;

import com.im.entity.Group;
import com.im.entity.Group.GroupType;
import com.im.entity.GroupMember;
import com.im.entity.GroupMember.MemberRole;
import com.im.service.IGroupService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 群组服务实现类
 * 功能 #5: 群组管理基础模块
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
@Service
public class GroupServiceImpl implements IGroupService {
    
    private final Map<String, Group> groups = new ConcurrentHashMap<>();
    private final Map<String, GroupMember> members = new ConcurrentHashMap<>();
    
    @Override
    public Group createGroup(String ownerId, String groupName, String description) {
        Group group = new Group();
        group.setGroupId(UUID.randomUUID().toString());
        group.setGroupName(groupName);
        group.setDescription(description);
        group.setOwnerId(ownerId);
        group.setCreateTime(LocalDateTime.now());
        
        groups.put(group.getGroupId(), group);
        
        // 添加群主为成员
        GroupMember owner = new GroupMember();
        owner.setMemberId(UUID.randomUUID().toString());
        owner.setGroupId(group.getGroupId());
        owner.setUserId(ownerId);
        owner.setRole(MemberRole.OWNER);
        owner.setJoinTime(LocalDateTime.now());
        members.put(owner.getMemberId(), owner);
        
        return group;
    }
    
    @Override
    public boolean dissolveGroup(String groupId, String operatorId) {
        Group group = groups.get(groupId);
        if (group == null || !group.isOwner(operatorId)) {
            return false;
        }
        
        // 删除所有成员
        members.values().removeIf(m -> m.getGroupId().equals(groupId));
        groups.remove(groupId);
        return true;
    }
    
    @Override
    public boolean inviteMember(String groupId, String inviterId, String userId) {
        Group group = groups.get(groupId);
        if (group == null || group.isFull()) return false;
        
        // 检查权限
        GroupMember inviter = getMember(groupId, inviterId);
        if (inviter == null || !group.hasPermission(inviterId)) return false;
        
        // 检查是否已在群中
        if (isGroupMember(groupId, userId)) return false;
        
        GroupMember member = new GroupMember();
        member.setMemberId(UUID.randomUUID().toString());
        member.setGroupId(groupId);
        member.setUserId(userId);
        member.setRole(MemberRole.MEMBER);
        member.setJoinTime(LocalDateTime.now());
        members.put(member.getMemberId(), member);
        
        group.addMember();
        return true;
    }
    
    @Override
    public boolean kickMember(String groupId, String operatorId, String userId) {
        Group group = groups.get(groupId);
        if (group == null || !group.hasPermission(operatorId)) return false;
        
        // 不能踢出群主
        if (group.isOwner(userId)) return false;
        
        // 管理员不能踢出管理员
        if (group.isAdmin(userId) && !group.isOwner(operatorId)) return false;
        
        members.values().removeIf(m -> 
            m.getGroupId().equals(groupId) && m.getUserId().equals(userId)
        );
        group.removeMember();
        return true;
    }
    
    @Override
    public boolean leaveGroup(String groupId, String userId) {
        Group group = groups.get(groupId);
        if (group == null) return false;
        
        // 群主不能退出，需要先转让
        if (group.isOwner(userId)) return false;
        
        members.values().removeIf(m -> 
            m.getGroupId().equals(groupId) && m.getUserId().equals(userId)
        );
        group.removeMember();
        return true;
    }
    
    @Override
    public Group getGroupInfo(String groupId) {
        return groups.get(groupId);
    }
    
    @Override
    public List<GroupMember> getGroupMembers(String groupId) {
        return members.values().stream()
            .filter(m -> m.getGroupId().equals(groupId))
            .collect(Collectors.toList());
    }
    
    @Override
    public boolean setAdmin(String groupId, String ownerId, String userId) {
        Group group = groups.get(groupId);
        if (group == null || !group.isOwner(ownerId)) return false;
        
        GroupMember member = getMember(groupId, userId);
        if (member == null) return false;
        
        member.setRole(MemberRole.ADMIN);
        group.getAdminIds().add(userId);
        return true;
    }
    
    @Override
    public boolean transferOwner(String groupId, String ownerId, String newOwnerId) {
        Group group = groups.get(groupId);
        if (group == null || !group.isOwner(ownerId)) return false;
        
        GroupMember newOwner = getMember(groupId, newOwnerId);
        if (newOwner == null) return false;
        
        // 原群主降为管理员
        GroupMember oldOwner = getMember(groupId, ownerId);
        oldOwner.setRole(MemberRole.ADMIN);
        
        // 新群主
        newOwner.setRole(MemberRole.OWNER);
        group.setOwnerId(newOwnerId);
        group.getAdminIds().add(ownerId);
        
        return true;
    }
    
    @Override
    public boolean updateGroupInfo(String groupId, String operatorId, String groupName, String description) {
        Group group = groups.get(groupId);
        if (group == null || !group.hasPermission(operatorId)) return false;
        
        if (groupName != null) group.setGroupName(groupName);
        if (description != null) group.setDescription(description);
        group.setUpdateTime(LocalDateTime.now());
        return true;
    }
    
    @Override
    public boolean muteAll(String groupId, String operatorId, boolean mute) {
        Group group = groups.get(groupId);
        if (group == null || !group.hasPermission(operatorId)) return false;
        
        group.setMuteAll(mute);
        return true;
    }
    
    @Override
    public boolean muteMember(String groupId, String operatorId, String userId, int minutes) {
        Group group = groups.get(groupId);
        if (group == null || !group.hasPermission(operatorId)) return false;
        
        GroupMember member = getMember(groupId, userId);
        if (member == null) return false;
        
        member.mute(minutes);
        return true;
    }
    
    @Override
    public List<Group> getUserGroups(String userId) {
        Set<String> groupIds = members.values().stream()
            .filter(m -> m.getUserId().equals(userId))
            .map(GroupMember::getGroupId)
            .collect(Collectors.toSet());
        
        return groupIds.stream()
            .map(groups::get)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
    
    @Override
    public boolean isGroupMember(String groupId, String userId) {
        return members.values().stream()
            .anyMatch(m -> m.getGroupId().equals(groupId) && m.getUserId().equals(userId));
    }
    
    private GroupMember getMember(String groupId, String userId) {
        return members.values().stream()
            .filter(m -> m.getGroupId().equals(groupId) && m.getUserId().equals(userId))
            .findFirst()
            .orElse(null);
    }
}
