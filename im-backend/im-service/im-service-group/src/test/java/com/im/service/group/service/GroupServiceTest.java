package com.im.service.group.service;

import com.im.service.group.dto.CreateGroupRequest;
import com.im.service.group.dto.GroupResponse;
import com.im.service.group.entity.Group;
import com.im.service.group.entity.GroupMember;
import com.im.service.group.repository.GroupMemberRepository;
import com.im.service.group.repository.GroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * GroupService 单元测试
 * 
 * 测试覆盖:
 * - 创建群组
 * - 更新群组信息
 * - 解散群组
 * - 发布群公告
 * - 全员禁言
 * 
 * @author IM Team
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("群组服务单元测试")
class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private GroupMemberRepository memberRepository;

    @InjectMocks
    private GroupService groupService;

    private static final String TEST_GROUP_ID = "group_123";
    private static final String TEST_OWNER_ID = "user_001";
    private static final String TEST_MEMBER_ID = "user_002";

    // ========== 群组创建测试 ==========

    @Test
    @DisplayName("创建群组成功")
    void createGroup_Success() {
        // Prepare
        CreateGroupRequest request = new CreateGroupRequest();
        request.setName("Test Group");
        request.setDescription("A test group");
        request.setType("PUBLIC");

        Group savedGroup = new Group();
        savedGroup.setId(TEST_GROUP_ID);
        savedGroup.setName("Test Group");
        savedGroup.setOwnerId(TEST_OWNER_ID);
        savedGroup.setMemberCount(1);

        when(groupRepository.save(any(Group.class))).thenReturn(savedGroup);
        when(memberRepository.save(any(GroupMember.class))).thenReturn(new GroupMember());

        // Act
        GroupResponse response = groupService.createGroup(request, TEST_OWNER_ID);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(TEST_GROUP_ID);
        assertThat(response.getName()).isEqualTo("Test Group");
        assertThat(response.getOwnerId()).isEqualTo(TEST_OWNER_ID);
        verify(groupRepository, times(1)).save(any(Group.class));
        verify(memberRepository, times(1)).save(any(GroupMember.class));
    }

    // ========== 群组更新测试 ==========

    @Test
    @DisplayName("更新群组信息成功")
    void updateGroupInfo() {
        // Prepare
        CreateGroupRequest request = new CreateGroupRequest();
        request.setName("Updated Group Name");
        request.setDescription("Updated description");

        Group group = new Group();
        group.setId(TEST_GROUP_ID);
        group.setName("Old Name");
        group.setOwnerId(TEST_OWNER_ID);

        when(groupRepository.findById(TEST_GROUP_ID)).thenReturn(Optional.of(group));
        when(groupRepository.save(any(Group.class))).thenReturn(group);

        // Act
        boolean result = groupService.updateGroup(TEST_GROUP_ID, request, TEST_OWNER_ID);

        // Assert
        assertThat(result).isTrue();
        assertThat(group.getName()).isEqualTo("Updated Group Name");
        assertThat(group.getDescription()).isEqualTo("Updated description");
        verify(groupRepository, times(1)).save(any(Group.class));
    }

    @Test
    @DisplayName("更新群组信息失败-非群主")
    void updateGroup_NotOwner() {
        // Prepare
        CreateGroupRequest request = new CreateGroupRequest();
        request.setName("Updated Group Name");

        Group group = new Group();
        group.setId(TEST_GROUP_ID);
        group.setOwnerId(TEST_OWNER_ID);

        when(groupRepository.findById(TEST_GROUP_ID)).thenReturn(Optional.of(group));

        // Act - 其他用户尝试更新
        boolean result = groupService.updateGroup(TEST_GROUP_ID, request, "other_user");

        // Assert
        assertThat(result).isFalse();
        verify(groupRepository, never()).save(any(Group.class));
    }

    // ========== 群组解散测试 ==========

    @Test
    @DisplayName("解散群组成功-群主操作")
    void dissolveGroup_Owner() {
        // Prepare
        Group group = new Group();
        group.setId(TEST_GROUP_ID);
        group.setOwnerId(TEST_OWNER_ID);

        when(groupRepository.findById(TEST_GROUP_ID)).thenReturn(Optional.of(group));
        when(memberRepository.deleteByGroupId(TEST_GROUP_ID)).thenReturn(5);
        when(groupRepository.dissolveGroup(eq(TEST_GROUP_ID), any(LocalDateTime.class))).thenReturn(1);

        // Act
        boolean result = groupService.dissolveGroup(TEST_GROUP_ID, TEST_OWNER_ID);

        // Assert
        assertThat(result).isTrue();
        verify(memberRepository, times(1)).deleteByGroupId(TEST_GROUP_ID);
        verify(groupRepository, times(1)).dissolveGroup(eq(TEST_GROUP_ID), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("解散群组失败-非群主")
    void dissolveGroup_NotOwner() {
        // Prepare
        Group group = new Group();
        group.setId(TEST_GROUP_ID);
        group.setOwnerId(TEST_OWNER_ID);

        when(groupRepository.findById(TEST_GROUP_ID)).thenReturn(Optional.of(group));

        // Act
        boolean result = groupService.dissolveGroup(TEST_GROUP_ID, "other_user");

        // Assert
        assertThat(result).isFalse();
        verify(memberRepository, never()).deleteByGroupId(anyString());
        verify(groupRepository, never()).dissolveGroup(anyString(), any(LocalDateTime.class));
    }

    // ========== 群公告测试 ==========

    @Test
    @DisplayName("发布群公告成功")
    void publishAnnouncement() {
        // Prepare
        String announcement = "New group announcement";
        Group group = new Group();
        group.setId(TEST_GROUP_ID);
        group.setOwnerId(TEST_OWNER_ID);

        when(groupRepository.findById(TEST_GROUP_ID)).thenReturn(Optional.of(group));
        when(groupRepository.updateAnnouncement(eq(TEST_GROUP_ID), eq(announcement), any(LocalDateTime.class))).thenReturn(1);

        // Act
        boolean result = groupService.updateAnnouncement(TEST_GROUP_ID, announcement, TEST_OWNER_ID);

        // Assert
        assertThat(result).isTrue();
        verify(groupRepository, times(1)).updateAnnouncement(eq(TEST_GROUP_ID), eq(announcement), any(LocalDateTime.class));
    }

    // ========== 全员禁言测试 ==========

    @Test
    @DisplayName("全员禁言成功")
    void muteAll() {
        // Prepare
        Group group = new Group();
        group.setId(TEST_GROUP_ID);
        group.setOwnerId(TEST_OWNER_ID);

        when(groupRepository.findById(TEST_GROUP_ID)).thenReturn(Optional.of(group));
        when(groupRepository.muteAll(eq(TEST_GROUP_ID), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(1);

        // Act
        boolean result = groupService.muteAll(TEST_GROUP_ID, TEST_OWNER_ID, 60);

        // Assert
        assertThat(result).isTrue();
        verify(groupRepository, times(1)).muteAll(eq(TEST_GROUP_ID), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("取消全员禁言成功")
    void unmuteAll() {
        // Prepare
        Group group = new Group();
        group.setId(TEST_GROUP_ID);
        group.setOwnerId(TEST_OWNER_ID);

        when(groupRepository.findById(TEST_GROUP_ID)).thenReturn(Optional.of(group));
        when(groupRepository.unmuteAll(eq(TEST_GROUP_ID), any(LocalDateTime.class))).thenReturn(1);

        // Act
        boolean result = groupService.unmuteAll(TEST_GROUP_ID, TEST_OWNER_ID);

        // Assert
        assertThat(result).isTrue();
        verify(groupRepository, times(1)).unmuteAll(eq(TEST_GROUP_ID), any(LocalDateTime.class));
    }

    // ========== 成员管理测试 ==========

    @Test
    @DisplayName("添加成员成功")
    void addMember_Success() {
        // Prepare
        Group group = new Group();
        group.setId(TEST_GROUP_ID);
        group.setMemberCount(5);
        group.setMaxMembers(100);

        when(groupRepository.findByIdAndDissolvedFalse(TEST_GROUP_ID)).thenReturn(Optional.of(group));
        when(memberRepository.existsByGroupIdAndUserId(TEST_GROUP_ID, TEST_MEMBER_ID)).thenReturn(false);
        when(memberRepository.save(any(GroupMember.class))).thenReturn(new GroupMember());
        when(groupRepository.updateMemberCount(eq(TEST_GROUP_ID), eq(1), any(LocalDateTime.class))).thenReturn(1);

        // Act
        boolean result = groupService.addMember(TEST_GROUP_ID, TEST_MEMBER_ID, "MEMBER");

        // Assert
        assertThat(result).isTrue();
        verify(memberRepository, times(1)).save(any(GroupMember.class));
        verify(groupRepository, times(1)).updateMemberCount(eq(TEST_GROUP_ID), eq(1), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("添加成员失败-群已满")
    void addMember_GroupFull() {
        // Prepare
        Group group = new Group();
        group.setId(TEST_GROUP_ID);
        group.setMemberCount(100);
        group.setMaxMembers(100);

        when(groupRepository.findByIdAndDissolvedFalse(TEST_GROUP_ID)).thenReturn(Optional.of(group));

        // Act
        boolean result = groupService.addMember(TEST_GROUP_ID, TEST_MEMBER_ID, "MEMBER");

        // Assert
        assertThat(result).isFalse();
        verify(memberRepository, never()).save(any(GroupMember.class));
    }

    @Test
    @DisplayName("移除成员成功")
    void removeMember() {
        // Prepare
        Group group = new Group();
        group.setId(TEST_GROUP_ID);
        group.setOwnerId(TEST_OWNER_ID);

        when(groupRepository.findById(TEST_GROUP_ID)).thenReturn(Optional.of(group));
        when(memberRepository.deleteByGroupIdAndUserId(TEST_GROUP_ID, TEST_MEMBER_ID)).thenReturn(1);
        when(groupRepository.updateMemberCount(eq(TEST_GROUP_ID), eq(-1), any(LocalDateTime.class))).thenReturn(1);

        // Act
        boolean result = groupService.removeMember(TEST_GROUP_ID, TEST_MEMBER_ID, TEST_OWNER_ID);

        // Assert
        assertThat(result).isTrue();
        verify(memberRepository, times(1)).deleteByGroupIdAndUserId(TEST_GROUP_ID, TEST_MEMBER_ID);
    }

    @Test
    @DisplayName("更新成员角色为管理员成功")
    void updateMemberRole_ToAdmin() {
        // Prepare
        Group group = new Group();
        group.setId(TEST_GROUP_ID);
        group.setOwnerId(TEST_OWNER_ID);

        when(groupRepository.findById(TEST_GROUP_ID)).thenReturn(Optional.of(group));
        when(memberRepository.updateRole(TEST_GROUP_ID, TEST_MEMBER_ID, "ADMIN")).thenReturn(1);

        // Act
        boolean result = groupService.updateMemberRole(TEST_GROUP_ID, TEST_MEMBER_ID, "ADMIN", TEST_OWNER_ID);

        // Assert
        assertThat(result).isTrue();
        verify(memberRepository, times(1)).updateRole(TEST_GROUP_ID, TEST_MEMBER_ID, "ADMIN");
    }

    @Test
    @DisplayName("转让群主成功")
    void transferOwnership() {
        // Prepare
        Group group = new Group();
        group.setId(TEST_GROUP_ID);
        group.setOwnerId(TEST_OWNER_ID);

        when(groupRepository.findById(TEST_GROUP_ID)).thenReturn(Optional.of(group));
        when(memberRepository.existsByGroupIdAndUserId(TEST_GROUP_ID, TEST_MEMBER_ID)).thenReturn(true);
        when(groupRepository.transferOwnership(eq(TEST_GROUP_ID), eq(TEST_MEMBER_ID), any(LocalDateTime.class))).thenReturn(1);
        when(memberRepository.updateRole(TEST_GROUP_ID, TEST_MEMBER_ID, "OWNER")).thenReturn(1);
        when(memberRepository.updateRole(TEST_GROUP_ID, TEST_OWNER_ID, "MEMBER")).thenReturn(1);

        // Act
        boolean result = groupService.transferOwnership(TEST_GROUP_ID, TEST_MEMBER_ID, TEST_OWNER_ID);

        // Assert
        assertThat(result).isTrue();
        verify(groupRepository, times(1)).transferOwnership(eq(TEST_GROUP_ID), eq(TEST_MEMBER_ID), any(LocalDateTime.class));
        verify(memberRepository, times(1)).updateRole(TEST_GROUP_ID, TEST_MEMBER_ID, "OWNER");
    }

    @Test
    @DisplayName("禁言成员成功")
    void muteMember() {
        // Prepare
        Group group = new Group();
        group.setId(TEST_GROUP_ID);
        group.setOwnerId(TEST_OWNER_ID);

        GroupMember operator = new GroupMember();
        operator.setUserId(TEST_OWNER_ID);
        operator.setRole("OWNER");

        when(groupRepository.findById(TEST_GROUP_ID)).thenReturn(Optional.of(group));
        when(memberRepository.findByGroupIdAndUserId(TEST_GROUP_ID, TEST_OWNER_ID)).thenReturn(Optional.of(operator));
        when(memberRepository.updateMuteStatus(eq(TEST_GROUP_ID), eq(TEST_MEMBER_ID), eq(true), any(LocalDateTime.class))).thenReturn(1);

        // Act
        boolean result = groupService.muteMember(TEST_GROUP_ID, TEST_MEMBER_ID, true, 30, TEST_OWNER_ID);

        // Assert
        assertThat(result).isTrue();
        verify(memberRepository, times(1)).updateMuteStatus(eq(TEST_GROUP_ID), eq(TEST_MEMBER_ID), eq(true), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("退出群组成功")
    void exitGroup() {
        // Prepare
        Group group = new Group();
        group.setId(TEST_GROUP_ID);
        group.setOwnerId(TEST_OWNER_ID);

        when(groupRepository.findById(TEST_GROUP_ID)).thenReturn(Optional.of(group));
        when(memberRepository.deleteByGroupIdAndUserId(TEST_GROUP_ID, TEST_MEMBER_ID)).thenReturn(1);
        when(groupRepository.updateMemberCount(eq(TEST_GROUP_ID), eq(-1), any(LocalDateTime.class))).thenReturn(1);

        // Act
        boolean result = groupService.leaveGroup(TEST_GROUP_ID, TEST_MEMBER_ID);

        // Assert
        assertThat(result).isTrue();
        verify(memberRepository, times(1)).deleteByGroupIdAndUserId(TEST_GROUP_ID, TEST_MEMBER_ID);
    }
}
