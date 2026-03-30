package com.im.backend.service;

import com.im.backend.dto.AddGroupMemberRequest;
import com.im.backend.dto.GroupMemberDTO;
import com.im.backend.entity.GroupMemberRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 群成员服务接口
 * 功能#29: 群成员管理
 */
public interface GroupMemberService {
    
    GroupMemberDTO addMember(Long operatorId, AddGroupMemberRequest request);
    
    void removeMember(Long operatorId, Long groupId, Long userId);
    
    void updateMemberRole(Long operatorId, Long groupId, Long userId, GroupMemberRole role);
    
    void updateGroupNickname(Long groupId, Long userId, String nickname);
    
    void muteMember(Long operatorId, Long groupId, Long userId, int minutes);
    
    void unmuteMember(Long operatorId, Long groupId, Long userId);
    
    GroupMemberDTO getMember(Long groupId, Long userId);
    
    List<GroupMemberDTO> getGroupMembers(Long groupId);
    
    Page<GroupMemberDTO> getGroupMembers(Long groupId, Pageable pageable);
    
    List<GroupMemberDTO> getUserGroups(Long userId);
    
    Long getGroupMemberCount(Long groupId);
    
    boolean isGroupMember(Long groupId, Long userId);
    
    boolean isAdminOrOwner(Long groupId, Long userId);
    
    boolean isOwner(Long groupId, Long userId);
}
