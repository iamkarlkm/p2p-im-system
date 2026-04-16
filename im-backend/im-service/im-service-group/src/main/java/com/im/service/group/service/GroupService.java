package com.im.service.group.service;

import com.im.service.group.dto.CreateGroupRequest;
import com.im.service.group.dto.GroupMemberResponse;
import com.im.service.group.dto.GroupResponse;
import com.im.service.group.entity.Group;
import com.im.service.group.entity.GroupMember;
import com.im.service.group.repository.GroupMemberRepository;
import com.im.service.group.repository.GroupRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository memberRepository;

    public GroupService(GroupRepository groupRepository, GroupMemberRepository memberRepository) {
        this.groupRepository = groupRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public GroupResponse createGroup(CreateGroupRequest request, String ownerId) {
        Group group = new Group();
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setType(request.getType() != null ? request.getType() : "PUBLIC");
        group.setAvatar(request.getAvatar());
        group.setOwnerId(ownerId);
        group.setMemberCount(1);
        if (request.getMaxMembers() != null) {
            group.setMaxMembers(request.getMaxMembers());
        }

        group = groupRepository.save(group);

        // 添加群主为成员
        GroupMember owner = new GroupMember();
        owner.setGroupId(group.getId());
        owner.setUserId(ownerId);
        owner.setRole("OWNER");
        owner.setJoinedAt(LocalDateTime.now());
        memberRepository.save(owner);

        return toGroupResponse(group);
    }

    public Optional<GroupResponse> getGroup(String groupId) {
        return groupRepository.findByIdAndDissolvedFalse(groupId)
            .map(this::toGroupResponse);
    }

    public List<GroupResponse> getUserGroups(String userId) {
        // 获取用户加入的所有群组
        return memberRepository.findByUserIdOrderByJoinedAtDesc(userId).stream()
            .map(m -> groupRepository.findByIdAndDissolvedFalse(m.getGroupId()).orElse(null))
            .filter(g -> g != null)
            .map(this::toGroupResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public boolean dissolveGroup(String groupId, String userId) {
        Optional<Group> opt = groupRepository.findById(groupId);
        if (opt.isEmpty()) return false;

        Group group = opt.get();
        // 只有群主可以解散
        if (!group.getOwnerId().equals(userId)) return false;

        // 删除所有成员
        memberRepository.deleteByGroupId(groupId);
        // 解散群组
        groupRepository.dissolveGroup(groupId, LocalDateTime.now());
        return true;
    }

    @Transactional
    public boolean updateGroup(String groupId, CreateGroupRequest request, String userId) {
        Optional<Group> opt = groupRepository.findById(groupId);
        if (opt.isEmpty()) return false;

        Group group = opt.get();
        // 只有群主可以更新
        if (!group.getOwnerId().equals(userId)) return false;

        if (request.getName() != null) group.setName(request.getName());
        if (request.getDescription() != null) group.setDescription(request.getDescription());
        if (request.getAvatar() != null) group.setAvatar(request.getAvatar());
        group.setUpdatedAt(LocalDateTime.now());

        groupRepository.save(group);
        return true;
    }

    // ========== 成员管理 ==========

    @Transactional
    public boolean addMember(String groupId, String userId, String role) {
        Optional<Group> opt = groupRepository.findByIdAndDissolvedFalse(groupId);
        if (opt.isEmpty()) return false;

        Group group = opt.get();
        if (group.getMemberCount() >= group.getMaxMembers()) return false;
        if (memberRepository.existsByGroupIdAndUserId(groupId, userId)) return false;

        GroupMember member = new GroupMember();
        member.setGroupId(groupId);
        member.setUserId(userId);
        member.setRole(role != null ? role : "MEMBER");
        memberRepository.save(member);

        groupRepository.updateMemberCount(groupId, 1, LocalDateTime.now());
        return true;
    }

    @Transactional
    public boolean removeMember(String groupId, String userId, String operatorId) {
        Optional<Group> opt = groupRepository.findById(groupId);
        if (opt.isEmpty()) return false;

        Group group = opt.get();
        // 群主可以移除任何人，其他人只能移除自己
        if (!group.getOwnerId().equals(operatorId) && !userId.equals(operatorId)) {
            return false;
        }

        int deleted = memberRepository.deleteByGroupIdAndUserId(groupId, userId);
        if (deleted > 0) {
            groupRepository.updateMemberCount(groupId, -1, LocalDateTime.now());
        }
        return deleted > 0;
    }

    /**
     * 成员主动退出群组
     */
    @Transactional
    public boolean leaveGroup(String groupId, String userId) {
        Optional<Group> opt = groupRepository.findById(groupId);
        if (opt.isEmpty()) return false;

        Group group = opt.get();
        // 群主不能退出，只能解散
        if (group.getOwnerId().equals(userId)) return false;

        int deleted = memberRepository.deleteByGroupIdAndUserId(groupId, userId);
        if (deleted > 0) {
            groupRepository.updateMemberCount(groupId, -1, LocalDateTime.now());
        }
        return deleted > 0;
    }

    @Transactional
    public boolean updateMemberRole(String groupId, String userId, String newRole, String operatorId) {
        Optional<Group> opt = groupRepository.findById(groupId);
        if (opt.isEmpty()) return false;

        Group group = opt.get();
        // 只有群主可以设置管理员
        if (!group.getOwnerId().equals(operatorId)) return false;

        int updated = memberRepository.updateRole(groupId, userId, newRole);
        return updated > 0;
    }

    /**
     * 转让群主
     */
    @Transactional
    public boolean transferOwnership(String groupId, String newOwnerId, String operatorId) {
        Optional<Group> opt = groupRepository.findById(groupId);
        if (opt.isEmpty()) return false;

        Group group = opt.get();
        // 只有群主可以转让
        if (!group.getOwnerId().equals(operatorId)) return false;
        // 不能转让给自己
        if (operatorId.equals(newOwnerId)) return false;

        // 检查新群主是否是成员
        if (!memberRepository.existsByGroupIdAndUserId(groupId, newOwnerId)) return false;

        // 更新群主
        groupRepository.transferOwnership(groupId, newOwnerId, LocalDateTime.now());
        // 原群主变为普通成员
        memberRepository.updateRole(groupId, operatorId, "MEMBER");
        // 新群主变为OWNER
        memberRepository.updateRole(groupId, newOwnerId, "OWNER");

        return true;
    }

    @Transactional
    public boolean muteMember(String groupId, String userId, boolean muted, int minutes, String operatorId) {
        Optional<Group> opt = groupRepository.findById(groupId);
        if (opt.isEmpty()) return false;

        Group group = opt.get();
        // 群主或管理员可以禁言
        Optional<GroupMember> operator = memberRepository.findByGroupIdAndUserId(groupId, operatorId);
        if (!group.getOwnerId().equals(operatorId) && 
            (operator.isEmpty() || !"ADMIN".equals(operator.get().getRole()))) {
            return false;
        }

        LocalDateTime until = muted ? LocalDateTime.now().plusMinutes(minutes) : null;
        int updated = memberRepository.updateMuteStatus(groupId, userId, muted, until);
        return updated > 0;
    }

    /**
     * 解除全群禁言
     */
    @Transactional
    public boolean unmuteAll(String groupId, String operatorId) {
        Optional<Group> opt = groupRepository.findById(groupId);
        if (opt.isEmpty()) return false;

        Group group = opt.get();
        // 只有群主可以解除全群禁言
        if (!group.getOwnerId().equals(operatorId)) return false;

        memberRepository.unmuteAll(groupId);
        groupRepository.unmuteAll(groupId, LocalDateTime.now());
        return true;
    }

    /**
     * 更新群公告
     */
    @Transactional
    public boolean updateAnnouncement(String groupId, String announcement, String operatorId) {
        Optional<Group> opt = groupRepository.findById(groupId);
        if (opt.isEmpty()) return false;

        Group group = opt.get();
        // 群主或管理员可以更新公告
        Optional<GroupMember> operator = memberRepository.findByGroupIdAndUserId(groupId, operatorId);
        if (!group.getOwnerId().equals(operatorId) && 
            (operator.isEmpty() || !"ADMIN".equals(operator.get().getRole()))) {
            return false;
        }

        groupRepository.updateAnnouncement(groupId, announcement, LocalDateTime.now());
        return true;
    }

    /**
     * 全员禁言
     */
    @Transactional
    public boolean muteAll(String groupId, String operatorId, int minutes) {
        Optional<Group> opt = groupRepository.findById(groupId);
        if (opt.isEmpty()) return false;

        Group group = opt.get();
        // 只有群主可以全员禁言
        if (!group.getOwnerId().equals(operatorId)) return false;

        LocalDateTime until = LocalDateTime.now().plusMinutes(minutes);
        groupRepository.muteAll(groupId, until, LocalDateTime.now());
        return true;
    }

    public List<GroupMemberResponse> getGroupMembers(String groupId, int page, int size) {
        return memberRepository.findByGroupIdOrderByRoleAscJoinedAtAsc(groupId, PageRequest.of(page, size))
            .getContent().stream()
            .map(this::toMemberResponse)
            .collect(Collectors.toList());
    }

    public Optional<GroupMemberResponse> getMember(String groupId, String userId) {
        return memberRepository.findByGroupIdAndUserId(groupId, userId)
            .map(this::toMemberResponse);
    }

    private GroupResponse toGroupResponse(Group group) {
        GroupResponse response = new GroupResponse();
        response.setId(group.getId());
        response.setName(group.getName());
        response.setDescription(group.getDescription());
        response.setAvatar(group.getAvatar());
        response.setType(group.getType());
        response.setOwnerId(group.getOwnerId());
        response.setMemberCount(group.getMemberCount());
        response.setMaxMembers(group.getMaxMembers());
        response.setCreatedAt(group.getCreatedAt());
        return response;
    }

    private GroupMemberResponse toMemberResponse(GroupMember member) {
        GroupMemberResponse response = new GroupMemberResponse();
        response.setId(member.getId());
        response.setGroupId(member.getGroupId());
        response.setUserId(member.getUserId());
        response.setNickname(member.getNickname());
        response.setRole(member.getRole());
        response.setMuted(member.getMuted());
        response.setMutedUntil(member.getMutedUntil());
        response.setJoinedAt(member.getJoinedAt());
        response.setLastActiveAt(member.getLastActiveAt());
        return response;
    }
}
