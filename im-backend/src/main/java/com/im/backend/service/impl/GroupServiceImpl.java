package com.im.backend.service.impl;

import com.im.backend.dto.GroupCreateRequest;
import com.im.backend.dto.GroupMemberRequest;
import com.im.backend.entity.Group;
import com.im.backend.entity.GroupMember;
import com.im.backend.exception.BusinessException;
import com.im.backend.repository.GroupMapper;
import com.im.backend.repository.GroupMemberMapper;
import com.im.backend.service.GroupService;
import com.im.backend.util.SnowflakeIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 群组服务实现类
 * 功能 #5: 群组管理基础模块
 */
@Slf4j
@Service
public class GroupServiceImpl implements GroupService {

    @Autowired
    private GroupMapper groupMapper;

    @Autowired
    private GroupMemberMapper groupMemberMapper;

    @Autowired
    private SnowflakeIdGenerator idGenerator;

    @Override
    @Transactional
    public Group createGroup(Long ownerId, GroupCreateRequest request) {
        request.validate();
        
        // 检查用户创建的群组数量限制
        Long createdCount = groupMapper.countGroupsByOwner(ownerId);
        if (createdCount >= 100) {
            throw new BusinessException("您创建的群组数量已达上限(100个)");
        }

        // 创建群组
        Group group = new Group();
        group.setGroupId(UUID.randomUUID().toString().replace("-", ""));
        group.setName(request.getName().trim());
        group.setDescription(request.getDescription());
        group.setAvatar(request.getAvatar());
        group.setOwnerId(ownerId);
        group.setGroupType(request.getGroupType());
        group.setMemberCount(1); // 群主
        group.setMaxMemberCount(request.getMaxMemberCount());
        group.setJoinType(request.getJoinType());
        group.setAllMuted(0);
        group.setStatus(0);
        group.setAnnouncement(request.getAnnouncement());
        group.setExtra(request.getExtra());

        groupMapper.insert(group);

        // 添加群主为成员
        GroupMember ownerMember = new GroupMember();
        ownerMember.setGroupId(group.getGroupId());
        ownerMember.setUserId(ownerId);
        ownerMember.setRole(2); // 群主
        ownerMember.setStatus(0);
        ownerMember.setJoinMethod(0); // 邀请
        ownerMember.setLastActiveAt(LocalDateTime.now());
        groupMemberMapper.insert(ownerMember);

        // 添加初始成员
        if (request.getInitialMembers() != null && !request.getInitialMembers().isEmpty()) {
            for (Long memberId : request.getInitialMembers()) {
                if (!memberId.equals(ownerId)) {
                    addMemberToGroup(group.getGroupId(), memberId, 0, ownerId);
                    group.incrementMemberCount();
                }
            }
            // 更新成员数
            groupMapper.updateMemberCount(group.getGroupId(), group.getMemberCount());
        }

        log.info("Group created: {}, owner: {}", group.getGroupId(), ownerId);
        return group;
    }

    @Override
    @Transactional
    public void dissolveGroup(String groupId, Long operatorId) {
        Group group = getGroupInfo(groupId);
        if (group == null) {
            throw new BusinessException("群组不存在");
        }
        if (!group.isOwner(operatorId)) {
            throw new BusinessException("只有群主可以解散群组");
        }

        // 删除所有成员
        groupMemberMapper.deleteAllMembers(groupId);
        
        // 解散群组
        groupMapper.dissolveGroup(groupId);
        
        log.info("Group dissolved: {}, operator: {}", groupId, operatorId);
    }

    @Override
    public Group getGroupInfo(String groupId) {
        Group group = groupMapper.selectByGroupId(groupId);
        if (group != null && group.getDeleted() == 0) {
            // 加载成员列表
            List<GroupMember> members = groupMemberMapper.selectActiveMembers(groupId);
            group.setMembers(members);
        }
        return group;
    }

    @Override
    @Transactional
    public Group updateGroupInfo(String groupId, Long operatorId, GroupCreateRequest request) {
        Group group = getGroupInfo(groupId);
        if (group == null) {
            throw new BusinessException("群组不存在");
        }
        if (!group.isOwner(operatorId) && !isGroupAdmin(groupId, operatorId)) {
            throw new BusinessException("只有群主或管理员可以修改群组信息");
        }

        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setAvatar(request.getAvatar());
        group.setJoinType(request.getJoinType());
        
        groupMapper.updateGroupInfo(group);
        
        log.info("Group info updated: {}, operator: {}", groupId, operatorId);
        return getGroupInfo(groupId);
    }

    @Override
    public List<Group> getMyCreatedGroups(Long userId) {
        return groupMapper.selectByOwnerId(userId);
    }

    @Override
    public List<Group> getMyJoinedGroups(Long userId) {
        return groupMapper.selectJoinedGroups(userId);
    }

    @Override
    public List<Group> searchGroups(String keyword, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 20;
        }
        return groupMapper.searchByName(keyword, limit);
    }

    @Override
    @Transactional
    public void inviteMember(String groupId, Long inviterId, Long targetUserId) {
        Group group = getGroupInfo(groupId);
        if (group == null) {
            throw new BusinessException("群组不存在");
        }
        if (group.isFull()) {
            throw new BusinessException("群组已满员");
        }
        if (!isGroupMember(groupId, inviterId)) {
            throw new BusinessException("您不在该群组中");
        }
        if (isGroupMember(groupId, targetUserId)) {
            throw new BusinessException("该用户已在群组中");
        }

        addMemberToGroup(groupId, targetUserId, 0, inviterId);
        groupMapper.incrementMemberCount(groupId);
        
        log.info("Member invited: group={}, inviter={}, target={}", groupId, inviterId, targetUserId);
    }

    @Override
    @Transactional
    public void joinGroup(String groupId, Long userId) {
        Group group = getGroupInfo(groupId);
        if (group == null) {
            throw new BusinessException("群组不存在");
        }
        if (group.isFull()) {
            throw new BusinessException("群组已满员");
        }
        if (group.getJoinType() == 2) {
            throw new BusinessException("该群组禁止加入");
        }
        if (isGroupMember(groupId, userId)) {
            throw new BusinessException("您已在该群组中");
        }

        // 如果需要验证,需要发送申请(此处简化处理)
        if (group.getJoinType() == 1) {
            // TODO: 发送入群申请
            throw new BusinessException("该群组需要验证,请发送入群申请");
        }

        addMemberToGroup(groupId, userId, 2, null); // 搜索加入
        groupMapper.incrementMemberCount(groupId);
        
        log.info("Member joined: group={}, user={}", groupId, userId);
    }

    @Override
    @Transactional
    public void quitGroup(String groupId, Long userId) {
        if (!isGroupMember(groupId, userId)) {
            throw new BusinessException("您不在该群组中");
        }
        if (isGroupOwner(groupId, userId)) {
            throw new BusinessException("群主不能直接退群,请先转让群主或解散群组");
        }

        groupMemberMapper.quitGroup(groupId, userId);
        groupMapper.decrementMemberCount(groupId);
        
        log.info("Member quit: group={}, user={}", groupId, userId);
    }

    @Override
    @Transactional
    public void kickMember(String groupId, Long operatorId, Long targetUserId, String reason) {
        if (!isGroupAdmin(groupId, operatorId)) {
            throw new BusinessException("只有群主或管理员可以踢出成员");
        }
        if (isGroupOwner(groupId, targetUserId)) {
            throw new BusinessException("不能踢出群主");
        }
        if (operatorId.equals(targetUserId)) {
            throw new BusinessException("不能踢出自己");
        }
        if (!isGroupMember(groupId, targetUserId)) {
            throw new BusinessException("该用户不在群组中");
        }

        groupMemberMapper.kickMember(groupId, targetUserId);
        groupMapper.decrementMemberCount(groupId);
        
        log.info("Member kicked: group={}, operator={}, target={}, reason={}", 
            groupId, operatorId, targetUserId, reason);
    }

    @Override
    @Transactional
    public void setAdmin(String groupId, Long operatorId, Long targetUserId) {
        if (!isGroupOwner(groupId, operatorId)) {
            throw new BusinessException("只有群主可以设置管理员");
        }
        if (!isGroupMember(groupId, targetUserId)) {
            throw new BusinessException("该用户不在群组中");
        }

        groupMemberMapper.setAdmin(groupId, targetUserId);
        
        log.info("Admin set: group={}, operator={}, target={}", groupId, operatorId, targetUserId);
    }

    @Override
    @Transactional
    public void unsetAdmin(String groupId, Long operatorId, Long targetUserId) {
        if (!isGroupOwner(groupId, operatorId)) {
            throw new BusinessException("只有群主可以取消管理员");
        }
        if (!isGroupMember(groupId, targetUserId)) {
            throw new BusinessException("该用户不在群组中");
        }

        groupMemberMapper.unsetAdmin(groupId, targetUserId);
        
        log.info("Admin unset: group={}, operator={}, target={}", groupId, operatorId, targetUserId);
    }

    @Override
    @Transactional
    public void transferOwnership(String groupId, Long currentOwnerId, Long newOwnerId) {
        if (!isGroupOwner(groupId, currentOwnerId)) {
            throw new BusinessException("只有群主可以转让群主身份");
        }
        if (!isGroupMember(groupId, newOwnerId)) {
            throw new BusinessException("该用户不在群组中");
        }
        if (currentOwnerId.equals(newOwnerId)) {
            throw new BusinessException("不能转让给自己");
        }

        // 更新群主
        groupMapper.transferOwnership(groupId, newOwnerId);
        
        // 更新原群主角色为管理员
        groupMemberMapper.updateRole(groupId, currentOwnerId, 1);
        
        // 更新新群主角色
        groupMemberMapper.updateRole(groupId, newOwnerId, 2);
        
        log.info("Ownership transferred: group={}, from={}, to={}", groupId, currentOwnerId, newOwnerId);
    }

    @Override
    @Transactional
    public void muteMember(String groupId, Long operatorId, Long targetUserId, Integer durationMinutes) {
        if (!isGroupAdmin(groupId, operatorId)) {
            throw new BusinessException("只有群主或管理员可以禁言");
        }
        if (isGroupOwner(groupId, targetUserId)) {
            throw new BusinessException("不能禁言群主");
        }
        if (!isGroupMember(groupId, targetUserId)) {
            throw new BusinessException("该用户不在群组中");
        }

        LocalDateTime muteUntil = durationMinutes > 0 
            ? LocalDateTime.now().plusMinutes(durationMinutes) 
            : LocalDateTime.now().plusYears(100); // 永久禁言

        groupMemberMapper.setMute(groupId, targetUserId, muteUntil);
        
        log.info("Member muted: group={}, operator={}, target={}, duration={}", 
            groupId, operatorId, targetUserId, durationMinutes);
    }

    @Override
    @Transactional
    public void unmuteMember(String groupId, Long operatorId, Long targetUserId) {
        if (!isGroupAdmin(groupId, operatorId)) {
            throw new BusinessException("只有群主或管理员可以解除禁言");
        }
        if (!isGroupMember(groupId, targetUserId)) {
            throw new BusinessException("该用户不在群组中");
        }

        groupMemberMapper.unsetMute(groupId, targetUserId);
        
        log.info("Member unmuted: group={}, operator={}, target={}", groupId, operatorId, targetUserId);
    }

    @Override
    public List<GroupMember> getGroupMembers(String groupId) {
        return groupMemberMapper.selectActiveMembers(groupId);
    }

    @Override
    public GroupMember getGroupMember(String groupId, Long userId) {
        return groupMemberMapper.selectByGroupIdAndUserId(groupId, userId);
    }

    @Override
    @Transactional
    public void updateGroupNickname(String groupId, Long userId, String nickname) {
        if (!isGroupMember(groupId, userId)) {
            throw new BusinessException("您不在该群组中");
        }
        groupMemberMapper.updateNickname(groupId, userId, nickname);
    }

    @Override
    public boolean isGroupMember(String groupId, Long userId) {
        Long count = groupMemberMapper.checkUserInGroup(groupId, userId);
        return count != null && count > 0;
    }

    @Override
    public boolean isGroupOwner(String groupId, Long userId) {
        GroupMember member = groupMemberMapper.selectByGroupIdAndUserId(groupId, userId);
        return member != null && member.isOwner();
    }

    @Override
    public boolean isGroupAdmin(String groupId, Long userId) {
        GroupMember member = groupMemberMapper.selectByGroupIdAndUserId(groupId, userId);
        return member != null && member.isAdmin();
    }

    @Override
    @Transactional
    public void handleMemberOperation(Long operatorId, GroupMemberRequest request) {
        request.validate();
        
        switch (request.getOperationType()) {
            case INVITE:
                inviteMember(request.getGroupId(), operatorId, request.getTargetUserId());
                break;
            case JOIN:
                joinGroup(request.getGroupId(), request.getTargetUserId());
                break;
            case KICK:
                kickMember(request.getGroupId(), operatorId, request.getTargetUserId(), request.getReason());
                break;
            case MUTE:
                muteMember(request.getGroupId(), operatorId, request.getTargetUserId(), request.getMuteDuration());
                break;
            case UNMUTE:
                unmuteMember(request.getGroupId(), operatorId, request.getTargetUserId());
                break;
            case ADMIN:
                setAdmin(request.getGroupId(), operatorId, request.getTargetUserId());
                break;
            case UNADMIN:
                unsetAdmin(request.getGroupId(), operatorId, request.getTargetUserId());
                break;
            case TRANSFER:
                transferOwnership(request.getGroupId(), operatorId, request.getTargetUserId());
                break;
            default:
                throw new BusinessException("不支持的操作类型");
        }
    }

    /**
     * 添加成员到群组
     */
    private void addMemberToGroup(String groupId, Long userId, Integer joinMethod, Long inviterId) {
        GroupMember member = new GroupMember();
        member.setGroupId(groupId);
        member.setUserId(userId);
        member.setRole(0); // 普通成员
        member.setStatus(0);
        member.setJoinMethod(joinMethod);
        member.setInviterId(inviterId);
        member.setLastActiveAt(LocalDateTime.now());
        groupMemberMapper.insert(member);
    }
}
