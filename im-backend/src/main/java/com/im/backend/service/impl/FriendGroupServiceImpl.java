package com.im.backend.service.impl;

import com.im.backend.dto.FriendGroupDTO;
import com.im.backend.dto.FriendGroupResponseDTO;
import com.im.backend.dto.MoveFriendToGroupDTO;
import com.im.backend.model.UserFriendGroup;
import com.im.backend.model.UserFriendGroupMember;
import com.im.backend.repository.UserFriendGroupMemberRepository;
import com.im.backend.repository.UserFriendGroupRepository;
import com.im.backend.service.FriendGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 好友分组服务实现类
 */
@Service
public class FriendGroupServiceImpl implements FriendGroupService {

    @Autowired
    private UserFriendGroupRepository groupRepository;

    @Autowired
    private UserFriendGroupMemberRepository memberRepository;

    @Override
    @Transactional
    public FriendGroupResponseDTO createGroup(Long userId, FriendGroupDTO dto) {
        if (groupRepository.existsByUserIdAndGroupName(userId, dto.getGroupName())) {
            throw new RuntimeException("分组名称已存在");
        }

        long groupCount = groupRepository.countByUserId(userId);
        if (groupCount >= 50) {
            throw new RuntimeException("分组数量已达上限");
        }

        UserFriendGroup group = new UserFriendGroup();
        group.setUserId(userId);
        group.setGroupName(dto.getGroupName());
        group.setDescription(dto.getDescription());
        group.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : (int) groupCount);
        group.setColorTag(dto.getColorTag());
        group.setIcon(dto.getIcon());
        group.setIsDefault(false);
        group.setMaxMembers(500);

        UserFriendGroup saved = groupRepository.save(group);
        return convertToDTO(saved);
    }

    @Override
    @Transactional
    public FriendGroupResponseDTO updateGroup(Long userId, Long groupId, FriendGroupDTO dto) {
        UserFriendGroup group = groupRepository.findById(groupId)
            .orElseThrow(() -> new RuntimeException("分组不存在"));

        if (!group.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此分组");
        }

        if (!group.getGroupName().equals(dto.getGroupName()) &&
            groupRepository.existsByUserIdAndGroupName(userId, dto.getGroupName())) {
            throw new RuntimeException("分组名称已存在");
        }

        group.setGroupName(dto.getGroupName());
        group.setDescription(dto.getDescription());
        group.setColorTag(dto.getColorTag());
        group.setIcon(dto.getIcon());

        UserFriendGroup updated = groupRepository.save(group);
        return convertToDTO(updated);
    }

    @Override
    @Transactional
    public void deleteGroup(Long userId, Long groupId) {
        UserFriendGroup group = groupRepository.findById(groupId)
            .orElseThrow(() -> new RuntimeException("分组不存在"));

        if (!group.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此分组");
        }

        if (group.getIsDefault()) {
            throw new RuntimeException("默认分组不能删除");
        }

        memberRepository.deleteAllByGroupId(groupId);
        groupRepository.delete(group);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendGroupResponseDTO> getUserGroups(Long userId) {
        List<UserFriendGroup> groups = groupRepository.findByUserIdOrderBySortOrderAsc(userId);
        return groups.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FriendGroupResponseDTO getGroupDetail(Long userId, Long groupId) {
        UserFriendGroup group = groupRepository.findById(groupId)
            .orElseThrow(() -> new RuntimeException("分组不存在"));

        if (!group.getUserId().equals(userId)) {
            throw new RuntimeException("无权查看此分组");
        }

        return convertToDTOWithMembers(group);
    }

    @Override
    @Transactional
    public FriendGroupResponseDTO renameGroup(Long userId, Long groupId, String newName) {
        UserFriendGroup group = groupRepository.findById(groupId)
            .orElseThrow(() -> new RuntimeException("分组不存在"));

        if (!group.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此分组");
        }

        if (groupRepository.existsByUserIdAndGroupName(userId, newName)) {
            throw new RuntimeException("分组名称已存在");
        }

        group.setGroupName(newName);
        UserFriendGroup updated = groupRepository.save(group);
        return convertToDTO(updated);
    }

    @Override
    @Transactional
    public void updateGroupSortOrder(Long userId, List<Long> groupIds) {
        for (int i = 0; i < groupIds.size(); i++) {
            groupRepository.updateSortOrder(groupIds.get(i), userId, i);
        }
    }

    @Override
    @Transactional
    public void addFriendToGroup(Long userId, Long groupId, Long friendId) {
        UserFriendGroup group = groupRepository.findById(groupId)
            .orElseThrow(() -> new RuntimeException("分组不存在"));

        if (!group.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此分组");
        }

        if (memberRepository.existsByGroupIdAndFriendId(groupId, friendId)) {
            throw new RuntimeException("好友已在该分组中");
        }

        long memberCount = memberRepository.countByGroupId(groupId);
        if (memberCount >= group.getMaxMembers()) {
            throw new RuntimeException("分组成员数量已达上限");
        }

        UserFriendGroupMember member = new UserFriendGroupMember();
        member.setGroup(group);
        member.setUserId(userId);
        member.setFriendId(friendId);
        member.setSortOrder((int) memberCount);

        memberRepository.save(member);
        groupRepository.updateMemberCount(groupId);
    }

    @Override
    @Transactional
    public void removeFriendFromGroup(Long userId, Long groupId, Long friendId) {
        UserFriendGroup group = groupRepository.findById(groupId)
            .orElseThrow(() -> new RuntimeException("分组不存在"));

        if (!group.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此分组");
        }

        memberRepository.deleteByGroupIdAndFriendId(groupId, friendId);
        groupRepository.updateMemberCount(groupId);
    }

    @Override
    @Transactional
    public void moveFriendToGroup(Long userId, MoveFriendToGroupDTO dto) {
        UserFriendGroup targetGroup = groupRepository.findById(dto.getTargetGroupId())
            .orElseThrow(() -> new RuntimeException("目标分组不存在"));

        if (!targetGroup.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作目标分组");
        }

        List<UserFriendGroupMember> existing = memberRepository.findByUserIdAndFriendId(userId, dto.getFriendId());
        
        for (UserFriendGroupMember member : existing) {
            if (member.getGroup().getId().equals(dto.getTargetGroupId())) {
                return;
            }
        }

        for (UserFriendGroupMember member : existing) {
            memberRepository.delete(member);
            groupRepository.updateMemberCount(member.getGroup().getId());
        }

        long memberCount = memberRepository.countByGroupId(dto.getTargetGroupId());
        UserFriendGroupMember newMember = new UserFriendGroupMember();
        newMember.setGroup(targetGroup);
        newMember.setUserId(userId);
        newMember.setFriendId(dto.getFriendId());
        newMember.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : (int) memberCount);
        newMember.setDisplayName(dto.getDisplayName());

        memberRepository.save(newMember);
        groupRepository.updateMemberCount(dto.getTargetGroupId());
    }

    @Override
    @Transactional
    public void updateFriendSortOrder(Long userId, Long groupId, List<Long> friendIds) {
        for (int i = 0; i < friendIds.size(); i++) {
            memberRepository.findByGroupIdAndFriendId(groupId, friendIds.get(i))
                .ifPresent(member -> {
                    member.setSortOrder(i);
                    memberRepository.save(member);
                });
        }
    }

    @Override
    @Transactional
    public void setMemberStarred(Long userId, Long groupId, Long friendId, Boolean starred) {
        UserFriendGroupMember member = memberRepository.findByGroupIdAndFriendId(groupId, friendId)
            .orElseThrow(() -> new RuntimeException("成员不存在"));

        if (!member.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作");
        }

        member.setIsStarred(starred);
        memberRepository.save(member);
    }

    @Override
    @Transactional
    public void setMemberMuted(Long userId, Long groupId, Long friendId, Boolean muted) {
        UserFriendGroupMember member = memberRepository.findByGroupIdAndFriendId(groupId, friendId)
            .orElseThrow(() -> new RuntimeException("成员不存在"));

        if (!member.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作");
        }

        member.setIsMuted(muted);
        memberRepository.save(member);
    }

    @Override
    @Transactional
    public FriendGroupResponseDTO createDefaultGroup(Long userId) {
        if (groupRepository.findByUserIdAndIsDefaultTrue(userId).isPresent()) {
            return convertToDTO(groupRepository.findByUserIdAndIsDefaultTrue(userId).get());
        }

        UserFriendGroup group = new UserFriendGroup();
        group.setUserId(userId);
        group.setGroupName("我的好友");
        group.setDescription("默认好友分组");
        group.setSortOrder(0);
        group.setIsDefault(true);
        group.setMaxMembers(1000);

        UserFriendGroup saved = groupRepository.save(group);
        return convertToDTO(saved);
    }

    private FriendGroupResponseDTO convertToDTO(UserFriendGroup group) {
        FriendGroupResponseDTO dto = new FriendGroupResponseDTO();
        dto.setId(group.getId());
        dto.setUserId(group.getUserId());
        dto.setGroupName(group.getGroupName());
        dto.setDescription(group.getDescription());
        dto.setSortOrder(group.getSortOrder());
        dto.setIsDefault(group.getIsDefault());
        dto.setMemberCount(group.getMemberCount());
        dto.setMaxMembers(group.getMaxMembers());
        dto.setColorTag(group.getColorTag());
        dto.setIcon(group.getIcon());
        dto.setIsVisible(group.getIsVisible());
        dto.setCreatedAt(group.getCreatedAt());
        dto.setUpdatedAt(group.getUpdatedAt());
        return dto;
    }

    private FriendGroupResponseDTO convertToDTOWithMembers(UserFriendGroup group) {
        FriendGroupResponseDTO dto = convertToDTO(group);
        
        List<FriendGroupResponseDTO.FriendGroupMemberDTO> memberDTOs = group.getMembers().stream()
            .map(this::convertMemberToDTO)
            .collect(Collectors.toList());
        dto.setMembers(memberDTOs);
        
        return dto;
    }

    private FriendGroupResponseDTO.FriendGroupMemberDTO convertMemberToDTO(UserFriendGroupMember member) {
        FriendGroupResponseDTO.FriendGroupMemberDTO dto = new FriendGroupResponseDTO.FriendGroupMemberDTO();
        dto.setId(member.getId());
        dto.setFriendId(member.getFriendId());
        dto.setSortOrder(member.getSortOrder());
        dto.setDisplayName(member.getDisplayName());
        dto.setIsStarred(member.getIsStarred());
        dto.setIsMuted(member.getIsMuted());
        dto.setRemark(member.getRemark());
        dto.setAddedAt(member.getAddedAt());
        return dto;
    }
}
