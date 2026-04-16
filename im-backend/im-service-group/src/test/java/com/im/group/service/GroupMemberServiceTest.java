package com.im.group.service;

import com.im.group.dto.GroupMemberResponse;
import com.im.group.entity.Group;
import com.im.group.entity.GroupMember;
import com.im.group.enums.GroupMemberRole;
import com.im.group.exception.AlreadyInGroupException;
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
 * 群成员服务单元测试
 * 测试覆盖: 添加成员、移除成员、设置角色、禁言等核心功能
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("群成员服务单元测试")
class GroupMemberServiceTest {

    @Mock
    private GroupMemberRepository groupMemberRepository;

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private GroupMemberService groupMemberService;

    private Group testGroup;
    private GroupMember ownerMember;
    private GroupMember adminMember;
    private GroupMember normalMember;

    @BeforeEach
    void setUp() {
        // 准备群组
        testGroup = new Group();
        testGroup.setGroupId("group_001");
        testGroup.setGroupName("Test Group");
        testGroup.setOwnerId("user_001");
        testGroup.setMaxMembers(200);
        testGroup.setCurrentMemberCount(3);

        // 准备群主
        ownerMember = new GroupMember();
        ownerMember.setMemberId("member_001");
        ownerMember.setGroupId("group_001");
        ownerMember.setUserId("user_001");
        ownerMember.setRole(GroupMemberRole.OWNER);
        ownerMember.setNicknameInGroup("Owner");
        ownerMember.setJoinedAt(LocalDateTime.now());

        // 准备管理员
        adminMember = new GroupMember();
        adminMember.setMemberId("member_002");
        adminMember.setGroupId("group_001");
        adminMember.setUserId("user_002");
        adminMember.setRole(GroupMemberRole.ADMIN);
        adminMember.setNicknameInGroup("Admin");
        adminMember.setJoinedAt(LocalDateTime.now());

        // 准备普通成员
        normalMember = new GroupMember();
        normalMember.setMemberId("member_003");
        normalMember.setGroupId("group_001");
        normalMember.setUserId("user_003");
        normalMember.setRole(GroupMemberRole.MEMBER);
        normalMember.setNicknameInGroup("Member");
        normalMember.setJoinedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("添加成员 - 成功")
    void addMember_Success() {
        // Given
        when(groupRepository.findById("group_001")).thenReturn(Optional.of(testGroup));
        when(groupMemberRepository.findByGroupIdAndUserId("group_001", "user_004")).thenReturn(Optional.empty());
        when(groupMemberRepository.countByGroupId("group_001")).thenReturn(3L);
        when(groupMemberRepository.save(any(GroupMember.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        GroupMemberResponse response = groupMemberService.addMember("group_001", "user_004", "user_001");

        // Then
        assertNotNull(response);
        assertEquals("user_004", response.getUserId());
        assertEquals(GroupMemberRole.MEMBER, response.getRole());
        verify(groupRepository).save(testGroup); // 成员数+1
    }

    @Test
    @DisplayName("添加成员 - 成员已在群中")
    void addMember_AlreadyInGroup() {
        // Given
        when(groupRepository.findById("group_001")).thenReturn(Optional.of(testGroup));
        when(groupMemberRepository.findByGroupIdAndUserId("group_001", "user_003")).thenReturn(Optional.of(normalMember));

        // When & Then
        assertThrows(AlreadyInGroupException.class, () -> {
            groupMemberService.addMember("group_001", "user_003", "user_001");
        });
    }

    @Test
    @DisplayName("添加成员 - 群已满")
    void addMember_GroupFull() {
        // Given
        testGroup.setCurrentMemberCount(200);
        when(groupRepository.findById("group_001")).thenReturn(Optional.of(testGroup));
        when(groupMemberRepository.findByGroupIdAndUserId("group_001", "user_004")).thenReturn(Optional.empty());
        when(groupMemberRepository.countByGroupId("group_001")).thenReturn(200L);

        // When & Then
        assertThrows(GroupFullException.class, () -> {
            groupMemberService.addMember("group_001", "user_004", "user_001");
        });
    }

    @Test
    @DisplayName("移除成员 - 群主移除普通成员成功")
    void removeMember_OwnerRemoveMember_Success() {
        // Given
        when(groupRepository.findById("group_001")).thenReturn(Optional.of(testGroup));
        when(groupMemberRepository.findByGroupIdAndUserId("group_001", "user_001")).thenReturn(Optional.of(ownerMember));
        when(groupMemberRepository.findByGroupIdAndUserId("group_001", "user_003")).thenReturn(Optional.of(normalMember));

        // When
        groupMemberService.removeMember("group_001", "user_003", "user_001");

        // Then
        verify(groupMemberRepository).delete(normalMember);
        verify(groupRepository).save(testGroup); // 成员数-1
    }

    @Test
    @DisplayName("移除成员 - 普通成员尝试移除他人失败")
    void removeMember_MemberRemoveOthers_Fail() {
        // Given
        when(groupRepository.findById("group_001")).thenReturn(Optional.of(testGroup));
        when(groupMemberRepository.findByGroupIdAndUserId("group_001", "user_003")).thenReturn(Optional.of(normalMember));
        when(groupMemberRepository.findByGroupIdAndUserId("group_001", "user_002")).thenReturn(Optional.of(adminMember));

        // When & Then
        assertThrows(NoPermissionException.class, () -> {
            groupMemberService.removeMember("group_001", "user_002", "user_003");
        });
    }

    @Test
    @DisplayName("设为管理员 - 群主成功")
    void setMemberRole_ToAdmin_Owner_Success() {
        // Given
        when(groupRepository.findById("group_001")).thenReturn(Optional.of(testGroup));
        when(groupMemberRepository.findByGroupIdAndUserId("group_001", "user_001")).thenReturn(Optional.of(ownerMember));
        when(groupMemberRepository.findByGroupIdAndUserId("group_001", "user_003")).thenReturn(Optional.of(normalMember));
        when(groupMemberRepository.save(any(GroupMember.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        GroupMemberResponse response = groupMemberService.setMemberRole("group_001", "user_003", GroupMemberRole.ADMIN, "user_001");

        // Then
        assertEquals(GroupMemberRole.ADMIN, response.getRole());
        verify(groupMemberRepository).save(normalMember);
    }

    @Test
    @DisplayName("设为管理员 - 非群主失败")
    void setMemberRole_ToAdmin_NotOwner_Fail() {
        // Given
        when(groupRepository.findById("group_001")).thenReturn(Optional.of(testGroup));
        when(groupMemberRepository.findByGroupIdAndUserId("group_001", "user_002")).thenReturn(Optional.of(adminMember));

        // When & Then
        assertThrows(NoPermissionException.class, () -> {
            groupMemberService.setMemberRole("group_001", "user_003", GroupMemberRole.ADMIN, "user_002");
        });
    }

    @Test
    @DisplayName("禁言成员 - 管理员成功")
    void muteMember_Admin_Success() {
        // Given
        when(groupRepository.findById("group_001")).thenReturn(Optional.of(testGroup));
        when(groupMemberRepository.findByGroupIdAndUserId("group_001", "user_002")).thenReturn(Optional.of(adminMember));
        when(groupMemberRepository.findByGroupIdAndUserId("group_001", "user_003")).thenReturn(Optional.of(normalMember));
        when(groupMemberRepository.save(any(GroupMember.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        GroupMemberResponse response = groupMemberService.muteMember("group_001", "user_003", true, 60, "user_002");

        // Then
        assertTrue(response.getIsMuted());
        assertNotNull(response.getMuteEndTime());
        verify(groupMemberRepository).save(normalMember);
    }

    @Test
    @DisplayName("禁言管理员 - 群主成功")
    void muteMember_MuteAdmin_Owner_Success() {
        // Given
        when(groupRepository.findById("group_001")).thenReturn(Optional.of(testGroup));
        when(groupMemberRepository.findByGroupIdAndUserId("group_001", "user_001")).thenReturn(Optional.of(ownerMember));
        when(groupMemberRepository.findByGroupIdAndUserId("group_001", "user_002")).thenReturn(Optional.of(adminMember));
        when(groupMemberRepository.save(any(GroupMember.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        GroupMemberResponse response = groupMemberService.muteMember("group_001", "user_002", true, 30, "user_001");

        // Then
        assertTrue(response.getIsMuted());
    }

    @Test
    @DisplayName("禁言群主 - 失败")
    void muteMember_MuteOwner_Fail() {
        // Given
        when(groupRepository.findById("group_001")).thenReturn(Optional.of(testGroup));
        when(groupMemberRepository.findByGroupIdAndUserId("group_001", "user_002")).thenReturn(Optional.of(adminMember));
        when(groupMemberRepository.findByGroupIdAndUserId("group_001", "user_001")).thenReturn(Optional.of(ownerMember));

        // When & Then
        assertThrows(NoPermissionException.class, () -> {
            groupMemberService.muteMember("group_001", "user_001", true, 30, "user_002");
        });
    }

    @Test
    @DisplayName("设置群昵称 - 成功")
    void setMemberNickname_Success() {
        // Given
        when(groupRepository.findById("group_001")).thenReturn(Optional.of(testGroup));
        when(groupMemberRepository.findByGroupIdAndUserId("group_001", "user_003")).thenReturn(Optional.of(normalMember));
        when(groupMemberRepository.save(any(GroupMember.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        GroupMemberResponse response = groupMemberService.setMemberNickname("group_001", "user_003", "New Nickname", "user_003");

        // Then
        assertEquals("New Nickname", response.getNicknameInGroup());
    }

    @Test
    @DisplayName("退出群组 - 普通成员成功")
    void exitGroup_Member_Success() {
        // Given
        when(groupRepository.findById("group_001")).thenReturn(Optional.of(testGroup));
        when(groupMemberRepository.findByGroupIdAndUserId("group_001", "user_003")).thenReturn(Optional.of(normalMember));

        // When
        groupMemberService.exitGroup("group_001", "user_003");

        // Then
        verify(groupMemberRepository).delete(normalMember);
        verify(groupRepository).save(testGroup);
    }

    @Test
    @DisplayName("退出群组 - 群主失败")
    void exitGroup_Owner_Fail() {
        // Given
        when(groupRepository.findById("group_001")).thenReturn(Optional.of(testGroup));
        when(groupMemberRepository.findByGroupIdAndUserId("group_001", "user_001")).thenReturn(Optional.of(ownerMember));

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            groupMemberService.exitGroup("group_001", "user_001");
        });
    }

    @Test
    @DisplayName("获取群成员列表 - 成功")
    void getGroupMembers_Success() {
        // Given
        List<GroupMember> members = Arrays.asList(ownerMember, adminMember, normalMember);
        when(groupMemberRepository.findByGroupIdOrderByRoleDescJoinedAtAsc("group_001")).thenReturn(members);

        // When
        List<GroupMemberResponse> responses = groupMemberService.getGroupMembers("group_001");

        // Then
        assertEquals(3, responses.size());
    }

    @Test
    @DisplayName("设置消息免打扰 - 成功")
    void setDoNotDisturb_Success() {
        // Given
        when(groupMemberRepository.findByGroupIdAndUserId("group_001", "user_003")).thenReturn(Optional.of(normalMember));
        when(groupMemberRepository.save(any(GroupMember.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        GroupMemberResponse response = groupMemberService.setDoNotDisturb("group_001", "user_003", true);

        // Then
        assertTrue(response.getDoNotDisturb());
    }

    @Test
    @DisplayName("检查用户是否在群中 - 是")
    void isMemberInGroup_True() {
        // Given
        when(groupMemberRepository.existsByGroupIdAndUserId("group_001", "user_003")).thenReturn(true);

        // When
        boolean isMember = groupMemberService.isMemberInGroup("group_001", "user_003");

        // Then
        assertTrue(isMember);
    }

    @Test
    @DisplayName("检查用户角色 - 群主")
    void getMemberRole_Owner() {
        // Given
        when(groupMemberRepository.findByGroupIdAndUserId("group_001", "user_001")).thenReturn(Optional.of(ownerMember));

        // When
        GroupMemberRole role = groupMemberService.getMemberRole("group_001", "user_001");

        // Then
        assertEquals(GroupMemberRole.OWNER, role);
    }
}
