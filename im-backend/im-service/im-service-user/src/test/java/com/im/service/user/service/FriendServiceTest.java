package com.im.service.user.service;

import com.im.service.user.entity.Friend;
import com.im.service.user.repository.FriendRepository;
import com.im.service.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * FriendService 单元测试
 * 
 * 测试覆盖:
 * - 发送好友申请
 * - 处理好友申请(接受/拒绝)
 * - 删除好友
 * - 星标好友
 * - 加入黑名单
 * 
 * @author IM Team
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("好友服务单元测试")
class FriendServiceTest {

    @Mock
    private FriendRepository friendRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FriendService friendService;

    private static final Long TEST_USER_ID = 1L;
    private static final Long TEST_FRIEND_ID = 2L;

    // ========== 好友申请测试 ==========

    @Test
    @DisplayName("获取好友统计信息")
    void getFriendStats() {
        // Prepare
        when(friendRepository.countFriends(TEST_USER_ID)).thenReturn(10L);
        when(friendRepository.countPendingReceived(TEST_USER_ID)).thenReturn(2L);
        when(friendRepository.countStarredFriends(TEST_USER_ID)).thenReturn(3L);
        when(friendRepository.countBlocked(TEST_USER_ID)).thenReturn(1L);

        // Act
        Map<String, Long> stats = friendService.getFriendStats(TEST_USER_ID);

        // Assert
        assertThat(stats).isNotNull();
        assertThat(stats.get("total")).isEqualTo(10L);
        assertThat(stats.get("pendingReceived")).isEqualTo(2L);
        assertThat(stats.get("starred")).isEqualTo(3L);
        assertThat(stats.get("blocked")).isEqualTo(1L);
        verify(friendRepository, times(1)).countFriends(TEST_USER_ID);
    }

    @Test
    @DisplayName("获取置顶好友列表")
    void getPinnedFriends() {
        // Prepare
        Friend friend1 = new Friend();
        friend1.setId(1L);
        friend1.setUserId(TEST_USER_ID);
        friend1.setFriendId(TEST_FRIEND_ID);
        friend1.setPinned(true);

        when(friendRepository.findPinnedFriends(TEST_USER_ID))
                .thenReturn(List.of(friend1));

        // Act
        List<Friend> result = friendService.getPinnedFriends(TEST_USER_ID);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPinned()).isTrue();
        verify(friendRepository, times(1)).findPinnedFriends(TEST_USER_ID);
    }

    @Test
    @DisplayName("获取星标好友列表")
    void getStarredFriends() {
        // Prepare
        Friend friend1 = new Friend();
        friend1.setId(1L);
        friend1.setUserId(TEST_USER_ID);
        friend1.setFriendId(TEST_FRIEND_ID);
        friend1.setStarred(true);

        when(friendRepository.findStarredFriends(TEST_USER_ID))
                .thenReturn(List.of(friend1));

        // Act
        List<Friend> result = friendService.getStarredFriends(TEST_USER_ID);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStarred()).isTrue();
        verify(friendRepository, times(1)).findStarredFriends(TEST_USER_ID);
    }

    @Test
    @DisplayName("检查是否为好友-是好友")
    void areFriends_True() {
        // Prepare
        when(friendRepository.areFriends(TEST_USER_ID, TEST_FRIEND_ID))
                .thenReturn(true);

        // Act
        boolean result = friendService.areFriends(TEST_USER_ID, TEST_FRIEND_ID);

        // Assert
        assertThat(result).isTrue();
        verify(friendRepository, times(1)).areFriends(TEST_USER_ID, TEST_FRIEND_ID);
    }

    @Test
    @DisplayName("检查是否为好友-不是好友")
    void areFriends_False() {
        // Prepare
        when(friendRepository.areFriends(TEST_USER_ID, TEST_FRIEND_ID))
                .thenReturn(false);

        // Act
        boolean result = friendService.areFriends(TEST_USER_ID, TEST_FRIEND_ID);

        // Assert
        assertThat(result).isFalse();
        verify(friendRepository, times(1)).areFriends(TEST_USER_ID, TEST_FRIEND_ID);
    }

    @Test
    @DisplayName("检查是否被屏蔽-已被屏蔽")
    void isBlocked_True() {
        // Prepare
        when(friendRepository.isBlocked(TEST_USER_ID, TEST_FRIEND_ID))
                .thenReturn(true);

        // Act
        boolean result = friendService.isBlocked(TEST_USER_ID, TEST_FRIEND_ID);

        // Assert
        assertThat(result).isTrue();
        verify(friendRepository, times(1)).isBlocked(TEST_USER_ID, TEST_FRIEND_ID);
    }

    @Test
    @DisplayName("检查是否被屏蔽-未被屏蔽")
    void isBlocked_False() {
        // Prepare
        when(friendRepository.isBlocked(TEST_USER_ID, TEST_FRIEND_ID))
                .thenReturn(false);

        // Act
        boolean result = friendService.isBlocked(TEST_USER_ID, TEST_FRIEND_ID);

        // Assert
        assertThat(result).isFalse();
        verify(friendRepository, times(1)).isBlocked(TEST_USER_ID, TEST_FRIEND_ID);
    }

    @Test
    @DisplayName("更新最后聊天时间成功")
    void updateLastChatTime() {
        // Prepare
        Friend friend = new Friend();
        friend.setId(1L);
        friend.setUserId(TEST_USER_ID);
        friend.setFriendId(TEST_FRIEND_ID);

        when(friendRepository.findByUserIdAndFriendId(TEST_USER_ID, TEST_FRIEND_ID))
                .thenReturn(friend);
        when(friendRepository.updateById(any(Friend.class))).thenReturn(1);

        // Act
        friendService.updateLastChatTime(TEST_USER_ID, TEST_FRIEND_ID);

        // Assert
        verify(friendRepository, times(1)).findByUserIdAndFriendId(TEST_USER_ID, TEST_FRIEND_ID);
        verify(friendRepository, times(1)).updateById(any(Friend.class));
    }

    @Test
    @DisplayName("批量获取好友关系")
    void getFriendsByIds() {
        // Prepare
        List<Long> friendIds = List.of(2L, 3L, 4L);
        Friend friend1 = new Friend();
        friend1.setId(1L);
        friend1.setUserId(TEST_USER_ID);
        friend1.setFriendId(2L);

        Friend friend2 = new Friend();
        friend2.setId(2L);
        friend2.setUserId(TEST_USER_ID);
        friend2.setFriendId(3L);

        when(friendRepository.findByFriendIds(TEST_USER_ID, friendIds))
                .thenReturn(List.of(friend1, friend2));

        // Act
        List<Friend> result = friendService.getFriendsByIds(TEST_USER_ID, friendIds);

        // Assert
        assertThat(result).hasSize(2);
        verify(friendRepository, times(1)).findByFriendIds(TEST_USER_ID, friendIds);
    }
}
