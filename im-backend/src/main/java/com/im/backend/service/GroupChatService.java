package com.im.backend.service;

import com.im.backend.dto.*;
import com.im.backend.entity.Group;
import com.im.backend.entity.GroupMember;
import com.im.backend.entity.GroupMessage;
import com.im.backend.repository.GroupRepository;
import com.im.backend.repository.GroupMemberRepository;
import com.im.backend.repository.GroupMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 群聊服务类
 * 对应功能 #15 - 群聊功能
 */
@Service
public class GroupChatService {

    @Autowired
    private GroupRepository groupRepository;
    
    @Autowired
    private GroupMemberRepository groupMemberRepository;
    
    @Autowired
    private GroupMessageRepository groupMessageRepository;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    /**
     * 创建群组
     */
    @Transactional
    public Group createGroup(Long ownerId, CreateGroupRequest request) {
        // 创建群组
        Group group = new Group();
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setAvatar(request.getAvatar());
        group.setOwnerId(ownerId);
        group.setMaxMembers(500);
        
        Group savedGroup = groupRepository.save(group);
        
        // 添加群主
        GroupMember owner = new GroupMember();
        owner.setGroupId(savedGroup.getId());
        owner.setUserId(ownerId);
        owner.setRole(GroupMember.MemberRole.OWNER);
        groupMemberRepository.save(owner);
        
        // 添加其他成员
        if (request.getMemberIds() != null) {
            for (Long memberId : request.getMemberIds()) {
                if (!memberId.equals(ownerId)) {
                    GroupMember member = new GroupMember();
                    member.setGroupId(savedGroup.getId());
                    member.setUserId(memberId);
                    member.setRole(GroupMember.MemberRole.MEMBER);
                    groupMemberRepository.save(member);
                }
            }
        }
        
        return savedGroup;
    }
    
    /**
     * 获取群组信息
     */
    public Optional<Group> getGroup(Long groupId) {
        return groupRepository.findById(groupId);
    }
    
    /**
     * 更新群组信息
     */
    @Transactional
    public Group updateGroup(Long groupId, Long userId, CreateGroupRequest request) {
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new RuntimeException("群组不存在"));
        
        // 检查权限（只有群主和管理员可以修改）
        GroupMember member = groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
            .orElseThrow(() -> new RuntimeException("不是群成员"));
        
        if (member.getRole() == GroupMember.MemberRole.MEMBER) {
            throw new RuntimeException("无权修改群组信息");
        }
        
        if (request.getName() != null) {
            group.setName(request.getName());
        }
        if (request.getDescription() != null) {
            group.setDescription(request.getDescription());
        }
        if (request.getAvatar() != null) {
            group.setAvatar(request.getAvatar());
        }
        
        return groupRepository.save(group);
    }
    
    /**
     * 解散群组
     */
    @Transactional
    public void dissolveGroup(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new RuntimeException("群组不存在"));
        
        if (!group.getOwnerId().equals(userId)) {
            throw new RuntimeException("只有群主可以解散群组");
        }
        
        // 删除所有成员
        groupMemberRepository.deleteAllByGroupId(groupId);
        
        // 删除群组
        groupRepository.delete(group);
    }
    
    /**
     * 邀请成员加入
     */
    @Transactional
    public GroupMember inviteMember(Long groupId, Long inviterId, Long userId) {
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new RuntimeException("群组不存在"));
        
        // 检查邀请者权限
        GroupMember inviter = groupMemberRepository.findByGroupIdAndUserId(groupId, inviterId)
            .orElseThrow(() -> new RuntimeException("不是群成员"));
        
        if (inviter.getRole() == GroupMember.MemberRole.MEMBER) {
            throw new RuntimeException("无权邀请成员");
        }
        
        // 检查成员数量
        long memberCount = groupMemberRepository.countByGroupId(groupId);
        if (memberCount >= group.getMaxMembers()) {
            throw new RuntimeException("群成员已满");
        }
        
        // 检查是否已在群中
        if (groupMemberRepository.existsByGroupIdAndUserId(groupId, userId)) {
            throw new RuntimeException("该用户已在群组中");
        }
        
        GroupMember member = new GroupMember();
        member.setGroupId(groupId);
        member.setUserId(userId);
        member.setRole(GroupMember.MemberRole.MEMBER);
        
        return groupMemberRepository.save(member);
    }
    
    /**
     * 移除成员
     */
    @Transactional
    public void removeMember(Long groupId, Long operatorId, Long userId) {
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new RuntimeException("群组不存在"));
        
        // 群主不能自己被移除
        if (group.getOwnerId().equals(userId)) {
            throw new RuntimeException("不能移除群主");
        }
        
        GroupMember operator = groupMemberRepository.findByGroupIdAndUserId(groupId, operatorId)
            .orElseThrow(() -> new RuntimeException("不是群成员"));
        
        GroupMember target = groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
            .orElseThrow(() -> new RuntimeException("目标用户不在群组中"));
        
        // 权限检查：群主可以移除任何人，管理员可以移除普通成员，自己主动退出
        if (!operatorId.equals(userId)) {
            if (operator.getRole() == GroupMember.MemberRole.MEMBER) {
                throw new RuntimeException("无权移除成员");
            }
            if (operator.getRole() == GroupMember.MemberRole.ADMIN && target.getRole() != GroupMember.MemberRole.MEMBER) {
                throw new RuntimeException("管理员不能移除其他管理员");
            }
        }
        
        groupMemberRepository.deleteByGroupIdAndUserId(groupId, userId);
    }
    
    /**
     * 获取群成员列表
     */
    public List<GroupMember> getGroupMembers(Long groupId) {
        return groupMemberRepository.findByGroupId(groupId);
    }
    
    /**
     * 退出群组
     */
    @Transactional
    public void quitGroup(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new RuntimeException("群组不存在"));
        
        // 群主不能退出，必须先转让群主或解散群组
        if (group.getOwnerId().equals(userId)) {
            throw new RuntimeException("群主不能退出群组，请转让群主或解散群组");
        }
        
        groupMemberRepository.deleteByGroupIdAndUserId(groupId, userId);
    }
    
    /**
     * 发送群消息
     */
    @Transactional
    public GroupMessage sendMessage(Long senderId, GroupMessageRequest request) {
        Long groupId = request.getGroupId();
        
        // 检查是否是群成员
        GroupMember member = groupMemberRepository.findByGroupIdAndUserId(groupId, senderId)
            .orElseThrow(() -> new RuntimeException("不是群成员，无法发送消息"));
        
        // 检查是否被禁言
        if (member.isMuted()) {
            throw new RuntimeException("您已被禁言");
        }
        
        GroupMessage message = new GroupMessage();
        message.setGroupId(groupId);
        message.setSenderId(senderId);
        message.setType(request.getType() != null ? request.getType() : GroupMessage.MessageType.TEXT);
        message.setContent(request.getContent());
        message.setExtra(request.getExtra());
        message.setStatus(GroupMessage.MessageStatus.SENT);
        
        GroupMessage savedMessage = groupMessageRepository.save(message);
        
        // 通过WebSocket推送给所有群成员
        messagingTemplate.convertAndSend("/topic/group/" + groupId, savedMessage);
        
        return savedMessage;
    }
    
    /**
     * 获取群消息历史
     */
    public Page<GroupMessage> getGroupMessages(Long groupId, Pageable pageable) {
        return groupMessageRepository.findByGroupIdOrderByCreatedAtDesc(groupId, pageable);
    }
    
    /**
     * 撤回消息
     */
    @Transactional
    public void recallMessage(Long messageId, Long userId) {
        int updated = groupMessageRepository.recallMessage(messageId, userId, LocalDateTime.now());
        if (updated == 0) {
            throw new RuntimeException("消息不存在或无权限撤回");
        }
    }
    
    /**
     * 获取用户加入的所有群组
     */
    public List<Group> getUserGroups(Long userId) {
        return groupRepository.findGroupsByMemberId(userId);
    }
}
