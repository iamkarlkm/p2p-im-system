package com.im.group.service;

import com.im.group.dto.CreateGroupRequest;
import com.im.group.dto.GroupMemberRequest;
import com.im.group.dto.GroupResponse;
import com.im.group.entity.Group;
import com.im.group.entity.GroupMember;
import com.im.group.enums.GroupMemberRole;
import com.im.group.exception.GroupFullException;
import com.im.group.exception.NoPermissionException;
import com.im.group.repository.GroupMemberRepository;
import com.im.group.repository.GroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 群组服务单元测试
 * 测试覆盖: 创建群组、更新信息、解散群组、公告发布等核心功能
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("群组服务单元测试")
class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private GroupMemberRepository groupMemberRepository;

    @InjectMocks
    private GroupService groupService;

    private Group testGroup;
    private GroupMember ownerMember;
    private GroupMember normalMember;

    @BeforeEach
    void setUp() {
        // 准备群组实体
        testGroup = new Group();
        testGroup.setGroupId("group_001");
        testGroup.setGroupName("Test Group");
        testGroup.setDescription("This is a test group");
        testGroup.setOwnerId("user_001");
        testGroup.setMaxMembers(200);
        testGroup.setCurrentMemberCount(2);
        testGroup.setCreatedAt(LocalDateTime.now());
        testGroup.setIsDeleted(false);

        // 准备群主成员
        ownerMember = new GroupMember();
        ownerMember.setMemberId("member_001");
        ownerMember.setGroupId("group_001");
        ownerMember.setUserId("user_001");
        ownerMember.setRole(GroupMemberRole.OWNER);
        ownerMember.setJoinedAt(LocalDateTime.now());

        // 准备普通成员
        normalMember = new GroupMember();
        normalMember.setMemberId("member_002");
        normalMember.setGroupId("group_001");
        normalMember.setUserId("user_002");
        normalMember.setRole(GroupMemberRole.MEMBER);
        normalMember.setJoinedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("创建群组 - 成功")
    void createGroup_Success() {
        // Given
        CreateGroupRequest request = new CreateGroupRequest();
        request.setGroupName("New Group");
        request.setDescription("A new test group");
        request.setOwnerId("user_001");
        request.setMaxMembers(100);

        when(groupRepository.save(any(Group.class))).thenAnswer(inv -> {
            Group g = inv.getArgument(0);
            g.setGroupId("new_group_id");
            return g;
        });
        when(groupMemberRepository.save(any(GroupMember.class))).thenReturn(ownerMember);

        // When
        GroupResponse response = groupService.createGroup(request);

        // Then
        assertNotNull(response);
        assertEquals("New Group", response.getGroupName());
        assertEquals("user_001", response.getOwnerId());
        
        verify(groupRepository).save(any(Group.class));
        verify(groupMemberRepository).save(any(GroupMember.class));
    }

    @Test
    @DisplayName("创建群组 - 群名无效")
    void createGroup_InvalidName() {
        // Given
        CreateGroupRequest request = new CreateGroupRequest();
        request.setGroupName(""); // 空名称
        request.setOwnerId("user_001");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            groupService.createGroup(request);
        });
    }

    @Test
    @DisplayName("更新群组信息 - 群主成功")
    void updateGroupInfo_Owner_Success() {
        // Given
        when(groupRepository.findById("group_001")).thenReturn(Optional.of(testGroup));

        // When
        GroupResponse response = groupService.updateGroupInfo("group_001", "user_001", "Updated Name", "Updated Description");

        // Then
        assertNotNull(response);
        assertEquals("Updated Name", response.getGroupName());
        assertEquals("Updated Description", response.getDescription());
        verify(groupRepository).save(testGroup);
    }

    @Test
    @DisplayName("更新群组信息 - 非群主失败")
    void updateGroupInfo_NotOwner_Fail() {
        // Given
        when(groupRepository.findById("group_001")).thenReturn(Optional.of(testGroup));

        // When & Then
        assertThrows(NoPermissionException.class, () -> {
            groupService.updateGroupInfo("group_001", "user_002", "Updated Name", "Updated Description");
        });
    }

    @Test
    @DisplayName("解散群组 - 群主成功")
    void dissolveGroup_Owner_Success() {
        // Given
        when(groupRepository.findById("group_001")).thenReturn(Optional.of(testGroup));

        // When
        groupService.dissolveGroup("group_001", "user_001");

        // Then
        assertTrue(testGroup.getIsDeleted());
        verify(groupRepository).save(testGroup);
    }

    @Test
    @DisplayName("解散群组 - 非群主失败")
    void dissolveGroup_NotOwner_Fail() {
        // Given
        when(groupRepository.findById("group_001")).thenReturn(Optional.of(testGroup));

        // When & Then
        assertThrows(NoPermissionException.class, () -> {
            groupService.dissolveGroup("group_001", "user_002");
        });
    }

    @Test
    @DisplayName("发布群公告 - 群主成功")
    void publishAnnouncement_Owner_Success() {
        // Given
        when(groupRepository.findById("group_001")).thenReturn(Optional.of(testGroup));

        // When
        GroupResponse response = groupService.publishAnnouncement("group_001", "user_001", "Important announcement");

        // Then
        assertNotNull(response);
        assertEquals("Important announcement", testGroup.getAnnouncement());
        verify(groupRepository).save(testGroup);
    }

    @Test
    @DisplayName("全员禁言 - 群主成功")
    void muteAll_Owner_Success() {
        // Given
        when(groupRepository.findById("group_001")).thenReturn(Optional.of(testGroup));

        // When
        GroupResponse response = groupService.muteAll("group_001", "user_001", true);

        // Then
        assertTrue(testGroup.getIsMuteAll());
        verify(groupRepository).save(testGroup);
    }

    @Test
    @DisplayName("取消全员禁言 - 群主成功")
    void unmuteAll_Owner_Success() {
        // Given
        testGroup.setIsMuteAll(true);
        when(groupRepository.findById("group_001")).thenReturn(Optional.of(testGroup));

        // When
        GroupResponse response = groupService.muteAll("group_001", "user_001", false);

        // Then
        assertFalse(testGroup.getIsMuteAll());
        verify(groupRepository).save(testGroup);
    }

    @Test
    @DisplayName("获取群组信息 - 成功")
    void getGroupInfo_Success() {
        // Given
        when(groupRepository.findById("group_001")).thenReturn(Optional.of(testGroup));

        // When
        GroupResponse response = groupService.getGroupInfo("group_001");

        // Then
        assertNotNull(response);
        assertEquals("group_001", response.getGroupId());
        assertEquals("Test Group", response.getGroupName());
    }

    @Test
    @DisplayName("获取群组列表 - 成功")
    void getUserGroups_Success() {
        // Given
        List<GroupMember> memberships = Arrays.asList(ownerMember);
        when(groupMemberRepository.findByUserId("user_001")).thenReturn(memberships);
        when(groupRepository.findAllById(any())).thenReturn(Arrays.asList(testGroup));

        // When
        List<GroupResponse> responses = groupService.getUserGroups("user_001");

        // Then
        assertEquals(1, responses.size());
    }

    @Test
    @DisplayName("搜索群组 - 成功")
    void searchGroups_Success() {
        // Given
        when(groupRepository.searchByNameContaining("Test")).thenReturn(Arrays.asList(testGroup));

        // When
        List<GroupResponse> responses = groupService.searchGroups("Test", 20);

        // Then
        assertEquals(1, responses.size());
    }

    @Test
    @DisplayName("更新群头像 - 群主成功")
    void updateGroupAvatar_Owner_Success() {
        // Given
        when(groupRepository.findById("group_001")).thenReturn(Optional.of(testGroup));

        // When
        GroupResponse response = groupService.updateGroupAvatar("group_001", "user_001", "https://example.com/avatar.jpg");

        // Then
        assertEquals("https://example.com/avatar.jpg", testGroup.getAvatarUrl());
    }

    @Test
    @DisplayName("设置入群验证 - 群主成功")
    void setJoinValidation_Owner_Success() {
        // Given
        when(groupRepository.findById("group_001")).thenReturn(Optional.of(testGroup));

        // When
        GroupResponse response = groupService.setJoinValidation("group_001", "user_001", true);

        // Then
        assertTrue(testGroup.getRequireValidation());
    }

    @Test
    @DisplayName("转让群主 - 原群主成功")
    void transferOwnership_OldOwner_Success() {
        // Given
        GroupMember newOwner = new GroupMember();
        newOwner.setGroupId("group_001");
        newOwner.setUserId("user_002");
        newOwner.setRole(GroupMemberRole.MEMBER);

        when(groupRepository.findById("group_001")).thenReturn(Optional.of(testGroup));
        when(groupMemberRepository.findByGroupIdAndUserId("group_001", "user_002"))
            .thenReturn(Optional.of(newOwner));

        // When
        GroupResponse response = groupService.transferOwnership("group_001", "user_001", "user_002");

        // Then
        assertEquals("user_002", testGroup.getOwnerId());
        assertEquals(GroupMemberRole.OWNER, newOwner.getRole());
        assertEquals(GroupMemberRole.MEMBER, ownerMember.getRole());
        
        verify(groupRepository).save(testGroup);
        verify(groupMemberRepository, times(2)).save(any(GroupMember.class));
    }

    @Test
    @DisplayName("检查群组是否已满 - 未满")
    void isGroupFull_NotFull() {
        // Given
        testGroup.setCurrentMemberCount(50);
        when(groupRepository.findById("group_001")).thenReturn(Optional.of(testGroup));

        // When
        boolean isFull = groupService.isGroupFull("group_001");

        // Then
        assertFalse(isFull);
    }

    @Test
    @DisplayName("检查群组是否已满 - 已满")
    void isGroupFull_Full() {
        // Given
        testGroup.setCurrentMemberCount(200);
        when(groupRepository.findById("group_001")).thenReturn(Optional.of(testGroup));

        // When
        boolean isFull = groupService.isGroupFull("group_001");

        // Then
        assertTrue(isFull);
    }
}
