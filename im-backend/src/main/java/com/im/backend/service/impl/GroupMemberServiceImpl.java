package com.im.backend.service.impl;

import com.im.backend.dto.AddGroupMemberRequest;
import com.im.backend.dto.GroupMemberDTO;
import com.im.backend.entity.GroupMember;
import com.im.backend.entity.GroupMemberRole;
import com.im.backend.repository.GroupMemberRepository;
import com.im.backend.service.GroupMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 群成员服务实现
 * 功能#29: 群成员管理
 */
@Service
public class GroupMemberServiceImpl implements GroupMemberService {
    
    @Autowired
    private GroupMemberRepository groupMemberRepository;
    
    @Override
    public GroupMemberDTO addMember(Long operatorId, AddGroupMemberRequest request) {
        if (!isAdminOrOwner(request.getGroupId(), operatorId)) {
            throw new RuntimeException("No permission to add member");
        }
        
        GroupMember member = new GroupMember();
        member.setGroupId(request.getGroupId());
        member.setUserId(request.getUserId());
        member.setRole(GroupMemberRole.MEMBER);
        member.setGroupNickname(request.getGroupNickname());
        
        GroupMember saved = groupMemberRepository.save(member);
        return convertToDTO(saved);
    }
    
    @Override
    public void removeMember(Long operatorId, Long groupId, Long userId) {
        if (!isAdminOrOwner(groupId, operatorId)) {
            throw new RuntimeException("No permission to remove member");
        }
        if (isOwner(groupId, userId) && !isOwner(groupId, operatorId)) {
            throw new RuntimeException("Cannot remove owner");
        }
        groupMemberRepository.deleteByGroupIdAndUserId(groupId, userId);
    }
    
    @Override
    public void updateMemberRole(Long operatorId, Long groupId, Long userId, GroupMemberRole role) {
        if (!isOwner(groupId, operatorId)) {
            throw new RuntimeException("Only owner can update roles");
        }
        groupMemberRepository.updateRole(groupId, userId, role);
    }
    
    @Override
    public void updateGroupNickname(Long groupId, Long userId, String nickname) {
        groupMemberRepository.updateNickname(groupId, userId, nickname);
    }
    
    @Override
    public void muteMember(Long operatorId, Long groupId, Long userId, int minutes) {
        if (!isAdminOrOwner(groupId, operatorId)) {
            throw new RuntimeException("No permission to mute member");
        }
        LocalDateTime muteUntil = LocalDateTime.now().plusMinutes(minutes);
        groupMemberRepository.updateMuteStatus(groupId, userId, muteUntil);
    }
    
    @Override
    public void unmuteMember(Long operatorId, Long groupId, Long userId) {
        if (!isAdminOrOwner(groupId, operatorId)) {
            throw new RuntimeException("No permission to unmute member");
        }
        groupMemberRepository.updateMuteStatus(groupId, userId, null);
    }
    
    @Override
    public GroupMemberDTO getMember(Long groupId, Long userId) {
        GroupMember member = groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        return convertToDTO(member);
    }
    
    @Override
    public List<GroupMemberDTO> getGroupMembers(Long groupId) {
        return groupMemberRepository.findByGroupId(groupId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public Page<GroupMemberDTO> getGroupMembers(Long groupId, Pageable pageable) {
        return groupMemberRepository.findByGroupId(groupId, pageable)
                .map(this::convertToDTO);
    }
    
    @Override
    public List<GroupMemberDTO> getUserGroups(Long userId) {
        return groupMemberRepository.findByUserId(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public Long getGroupMemberCount(Long groupId) {
        return groupMemberRepository.countByGroupId(groupId);
    }
    
    @Override
    public boolean isGroupMember(Long groupId, Long userId) {
        return groupMemberRepository.findByGroupIdAndUserId(groupId, userId).isPresent();
    }
    
    @Override
    public boolean isAdminOrOwner(Long groupId, Long userId) {
        return groupMemberRepository.isAdminOrOwner(groupId, userId);
    }
    
    @Override
    public boolean isOwner(Long groupId, Long userId) {
        return groupMemberRepository.isOwner(groupId, userId);
    }
    
    private GroupMemberDTO convertToDTO(GroupMember member) {
        GroupMemberDTO dto = new GroupMemberDTO();
        dto.setId(member.getId());
        dto.setGroupId(member.getGroupId());
        dto.setUserId(member.getUserId());
        dto.setRole(member.getRole());
        dto.setGroupNickname(member.getGroupNickname());
        dto.setMuteUntil(member.getMuteUntil());
        dto.setJoinTime(member.getJoinTime());
        dto.setIsMuted(member.getMuteUntil() != null && member.getMuteUntil().isAfter(LocalDateTime.now()));
        dto.setIsBlocked(member.getIsBlocked());
        return dto;
    }
}
