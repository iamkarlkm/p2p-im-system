package com.im.group.service;

import com.im.service.group.dto.AnnouncementResponse;
import com.im.service.group.dto.CreateAnnouncementRequest;
import com.im.service.group.entity.Group;
import com.im.service.group.entity.GroupAnnouncement;
import com.im.service.group.repository.GroupAnnouncementRepository;
import com.im.service.group.repository.GroupRepository;
import com.im.service.group.service.GroupAnnouncementService;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 群公告服务单元测试
 * 
 * @author IM Team
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("群公告服务测试")
class GroupAnnouncementServiceTest {

    @Mock
    private GroupAnnouncementRepository announcementRepository;

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private GroupAnnouncementService announcementService;

    private String userId;
    private String groupId;
    private Group mockGroup;
    private GroupAnnouncement mockAnnouncement;

    @BeforeEach
    void setUp() {
        userId = "user-123";
        groupId = "group-456";
        
        mockGroup = new Group();
        mockGroup.setId(groupId);
        mockGroup.setOwnerId(userId);
        
        mockAnnouncement = new GroupAnnouncement();
        mockAnnouncement.setId("ann-789");
        mockAnnouncement.setGroupId(groupId);
        mockAnnouncement.setCreatorId(userId);
        mockAnnouncement.setTitle("测试公告");
        mockAnnouncement.setContent("测试内容");
        mockAnnouncement.setIsPinned(false);
        mockAnnouncement.setReadCount(0);
        mockAnnouncement.setDeleted(false);
        mockAnnouncement.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("创建公告-成功")
    void createAnnouncement_Success() {
        // Given
        CreateAnnouncementRequest request = CreateAnnouncementRequest.builder()
                .groupId(groupId)
                .title("新公告")
                .content("公告内容")
                .build();

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(mockGroup));
        when(announcementRepository.save(any(GroupAnnouncement.class))).thenReturn(mockAnnouncement);

        // When
        AnnouncementResponse response = announcementService.createAnnouncement(userId, request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("测试公告");
        verify(announcementRepository).save(any(GroupAnnouncement.class));
    }

    @Test
    @DisplayName("创建公告-群组不存在")
    void createAnnouncement_GroupNotFound() {
        // Given
        CreateAnnouncementRequest request = CreateAnnouncementRequest.builder()
                .groupId("non-existent")
                .title("公告")
                .content("内容")
                .build();

        when(groupRepository.findById("non-existent")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> announcementService.createAnnouncement(userId, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("群组不存在");
    }

    @Test
    @DisplayName("创建公告-非群主无权限")
    void createAnnouncement_NotOwner_Fails() {
        // Given
        CreateAnnouncementRequest request = CreateAnnouncementRequest.builder()
                .groupId(groupId)
                .title("公告")
                .content("内容")
                .build();

        Group anotherGroup = new Group();
        anotherGroup.setId(groupId);
        anotherGroup.setOwnerId("other-user");

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(anotherGroup));

        // When & Then
        assertThatThrownBy(() -> announcementService.createAnnouncement(userId, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("只有群主才能发布公告");
    }

    @Test
    @DisplayName("获取公告列表-成功")
    void getAnnouncements_Success() {
        // Given
        GroupAnnouncement a1 = new GroupAnnouncement();
        a1.setId("a1");
        a1.setGroupId(groupId);
        a1.setCreatorId(userId);
        a1.setTitle("公告1");
        a1.setIsPinned(true);
        
        GroupAnnouncement a2 = new GroupAnnouncement();
        a2.setId("a2");
        a2.setGroupId(groupId);
        a2.setCreatorId("user-456");
        a2.setTitle("公告2");
        a2.setIsPinned(false);

        when(announcementRepository.findByGroupIdAndDeletedFalseOrderByIsPinnedDescCreatedAtDesc(groupId))
                .thenReturn(Arrays.asList(a1, a2));

        // When
        List<AnnouncementResponse> results = announcementService.getAnnouncements(groupId, userId);

        // Then
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getIsPinned()).isTrue();
    }

    @Test
    @DisplayName("删除公告-成功")
    void deleteAnnouncement_Success() {
        // Given
        when(announcementRepository.findById("ann-789")).thenReturn(Optional.of(mockAnnouncement));

        // When
        announcementService.deleteAnnouncement("ann-789", userId);

        // Then
        verify(announcementRepository).save(any(GroupAnnouncement.class));
    }

    @Test
    @DisplayName("删除公告-非创建者失败")
    void deleteAnnouncement_NotCreator_Fails() {
        // Given
        when(announcementRepository.findById("ann-789")).thenReturn(Optional.of(mockAnnouncement));

        // When & Then
        assertThatThrownBy(() -> announcementService.deleteAnnouncement("ann-789", "other-user"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("只有创建者可以删除公告");
    }

    @Test
    @DisplayName("置顶公告-成功")
    void pinAnnouncement_Success() {
        // Given
        when(announcementRepository.findById("ann-789")).thenReturn(Optional.of(mockAnnouncement));
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(mockGroup));

        // When
        announcementService.pinAnnouncement("ann-789", userId);

        // Then
        verify(announcementRepository).save(any(GroupAnnouncement.class));
    }

    @Test
    @DisplayName("获取最新公告-成功")
    void getLatestAnnouncement_Success() {
        // Given
        when(announcementRepository.findFirstByGroupIdAndDeletedFalseOrderByIsPinnedDescCreatedAtDesc(groupId))
                .thenReturn(Optional.of(mockAnnouncement));

        // When
        AnnouncementResponse response = announcementService.getLatestAnnouncement(groupId, userId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("ann-789");
    }

    @Test
    @DisplayName("获取最新公告-无公告")
    void getLatestAnnouncement_NoAnnouncement() {
        // Given
        when(announcementRepository.findFirstByGroupIdAndDeletedFalseOrderByIsPinnedDescCreatedAtDesc(groupId))
                .thenReturn(Optional.empty());

        // When
        AnnouncementResponse response = announcementService.getLatestAnnouncement(groupId, userId);

        // Then
        assertThat(response).isNull();
    }
}
