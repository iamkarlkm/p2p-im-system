package com.im.server.service;

import com.im.server.entity.Group;
import com.im.server.entity.GroupMember;
import com.im.server.repository.GroupMemberRepository;
import com.im.server.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 群组服务
 */
@Service
@RequiredArgsConstructor
public class GroupService {
    
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    
    /**
     * 创建群组
     */
    @Transactional
    public Group createGroup(Long ownerId, String groupName, String avatarUrl, String notice) {
        Group group = new Group();
        group.setGroupId(UUID.randomUUID().toString());
        group.setGroupName(groupName);
        group.setAvatarUrl(avatarUrl);
        group.setOwnerId(ownerId);
        group.setNotice(notice);
        group.setMemberCount(1);
        
        group = groupRepository.save(group);
        
        // 创建者自动成为群主
        GroupMember member = new GroupMember();
        member.setGroupId(group.getId());
        member.setUserId(ownerId);
        member.setRole(3); // 群主
        member.setNickname(groupName);
        
        groupMemberRepository.save(member);
        
        return group;
    }
    
    /**
     * 加入群组
     */
    @Transactional
    public GroupMember joinGroup(Long groupId, Long userId, String nickname) {
        if (groupMemberRepository.existsByGroupIdAndUserId(groupId, userId)) {
            throw new RuntimeException("已经在群组中");
        }
        
        GroupMember member = new GroupMember();
        member.setGroupId(groupId);
        member.setUserId(userId);
        member.setRole(1); // 成员
        member.setNickname(nickname);
        
        member = groupMemberRepository.save(member);
        
        // 更新群成员数量
        Group group = groupRepository.findById(groupId).orElse(null);
        if (group != null) {
            group.setMemberCount(group.getMemberCount() + 1);
            groupRepository.save(group);
        }
        
        return member;
    }
    
    /**
     * 退出群组
     */
    @Transactional
    public void leaveGroup(Long groupId, Long userId) {
        GroupMember member = groupMemberRepository.findByGroupIdAndUserId(groupId, userId);
        if (member != null) {
            // 检查是否为群主
            if (member.getRole() == 3) {
                throw new RuntimeException("群主不能退出群组，请先转让群主");
            }
            
            groupMemberRepository.delete(member);
            
            // 更新群成员数量
            Group group = groupRepository.findById(groupId).orElse(null);
            if (group != null) {
                group.setMemberCount(Math.max(0, group.getMemberCount() - 1));
                groupRepository.save(group);
            }
        }
    }
    
    /**
     * 获取群组信息
     */
    public Group getGroupById(Long groupId) {
        return groupRepository.findById(groupId).orElse(null);
    }
    
    /**
     * 获取群组信息（根据groupId）
     */
    public Group getGroupByGroupId(String groupId) {
        return groupRepository.findByGroupId(groupId);
    }
    
    /**
     * 获取群成员列表
     */
    public List<GroupMember> getGroupMembers(Long groupId) {
        return groupMemberRepository.findByGroupId(groupId);
    }
    
    /**
     * 获取用户加入的群组列表
     */
    public List<GroupMember> getUserGroups(Long userId) {
        return groupMemberRepository.findByUserId(userId);
    }
    
    /**
     * 更新群组信息
     */
    public Group updateGroup(Long groupId, Long userId, String groupName, String avatarUrl, String notice) {
        Group group = groupRepository.findById(groupId).orElse(null);
        if (group == null) {
            throw new RuntimeException("群组不存在");
        }
        
        // 检查权限（只有群主和管理员可以修改）
        GroupMember member = groupMemberRepository.findByGroupIdAndUserId(groupId, userId);
        if (member == null || (member.getRole() != 2 && member.getRole() != 3)) {
            throw new RuntimeException("没有权限修改群组信息");
        }
        
        if (groupName != null) {
            group.setGroupName(groupName);
        }
        if (avatarUrl != null) {
            group.setAvatarUrl(avatarUrl);
        }
        if (notice != null) {
            group.setNotice(notice);
        }
        
        return groupRepository.save(group);
    }
    
    /**
     * 设置群成员角色
     */
    @Transactional
    public void setMemberRole(Long groupId, Long userId, Long operatorId, Integer role) {
        // 检查操作者权限
        GroupMember operator = groupMemberRepository.findByGroupIdAndUserId(groupId, operatorId);
        if (operator == null || operator.getRole() != 3) {
            throw new RuntimeException("只有群主可以设置管理员");
        }
        
        GroupMember member = groupMemberRepository.findByGroupIdAndUserId(groupId, userId);
        if (member != null) {
            member.setRole(role);
            groupMemberRepository.save(member);
        }
    }
}
