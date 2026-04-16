package com.im.user.service;

import com.im.user.dto.FriendRequestRequest;
import com.im.user.dto.FriendResponse;
import com.im.user.entity.Friend;
import com.im.user.entity.User;
import com.im.user.enums.FriendStatus;
import com.im.user.exception.AlreadyFriendException;
import com.im.user.exception.BlockedException;
import com.im.user.repository.FriendRepository;
import com.im.user.repository.UserRepository;
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
 * 好友服务单元测试
 * 测试覆盖: 好友申请、接受/拒绝、删除、黑名单等核心功能
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

    private User user1;
    private User user2;
    private Friend pendingRequest;
    private Friend acceptedFriend;

    @BeforeEach
    void setUp() {
        // 准备用户实体
        user1 = createUser("user_001", "user1");
        user2 = createUser("user_002", "user2");

        // 准备待处理的好友申请
        pendingRequest = new Friend();
        pendingRequest.setFriendId("friend_001");
        pendingRequest.setUserId("user_001");
        pendingRequest.setFriendUserId("user_002");
        pendingRequest.setStatus(FriendStatus.PENDING);
        pendingRequest.setApplyMessage("Hello, let's be friends");
        pendingRequest.setCreatedAt(LocalDateTime.now());

        // 准备已接受的好友关系
        acceptedFriend = new Friend();
        acceptedFriend.setFriendId("friend_002");
        acceptedFriend.setUserId("user_001");
        acceptedFriend.setFriendUserId("user_002");
        acceptedFriend.setStatus(FriendStatus.ACCEPTED);
        acceptedFriend.setCreatedAt(LocalDateTime.now());
        acceptedFriend.setAcceptedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("发送好友申请 - 成功")
    void sendFriendRequest_Success() {
        // Given
        when(userRepository.findById("user_001")).thenReturn(Optional.of(user1));
        when(userRepository.findById("user_002")).thenReturn(Optional.of(user2));
        when(friendRepository.findFriendship("user_001", "user_002")).thenReturn(Optional.empty());
        when(friendRepository.save(any(Friend.class))).thenReturn(pendingRequest);

        // When
        FriendResponse response = friendService.sendFriendRequest("user_001", "user_002", "Hello");

        // Then
        assertNotNull(response);
        assertEquals(FriendStatus.PENDING, response.getStatus());
        verify(friendRepository).save(any(Friend.class));
    }

    @Test
    @DisplayName("发送好友申请 - 已是好友")
    void sendFriendRequest_AlreadyFriend() {
        // Given
        when(userRepository.findById("user_001")).thenReturn(Optional.of(user1));
        when(userRepository.findById("user_002")).thenReturn(Optional.of(user2));
        when(friendRepository.findFriendship("user_001", "user_002"))
            .thenReturn(Optional.of(acceptedFriend));

        // When & Then
        assertThrows(AlreadyFriendException.class, () -> {
            friendService.sendFriendRequest("user_001", "user_002", "Hello");
        });
    }

    @Test
    @DisplayName("发送好友申请 - 被对方拉黑")
    void sendFriendRequest_Blocked() {
        // Given
        Friend blockedRecord = new Friend();
        blockedRecord.setUserId("user_002"); // 对方
        blockedRecord.setFriendUserId("user_001"); // 自己
        blockedRecord.setStatus(FriendStatus.BLOCKED);

        when(userRepository.findById("user_001")).thenReturn(Optional.of(user1));
        when(userRepository.findById("user_002")).thenReturn(Optional.of(user2));
        when(friendRepository.findFriendship("user_001", "user_002")).thenReturn(Optional.empty());
        when(friendRepository.findByUserIdAndFriendUserId("user_002", "user_001"))
            .thenReturn(Optional.of(blockedRecord));

        // When & Then
        assertThrows(BlockedException.class, () -> {
            friendService.sendFriendRequest("user_001", "user_002", "Hello");
        });
    }

    @Test
    @DisplayName("接受好友申请 - 成功")
    void handleFriendRequest_Accept() {
        // Given
        when(friendRepository.findById("friend_001")).thenReturn(Optional.of(pendingRequest));
        when(friendRepository.save(any(Friend.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        FriendResponse response = friendService.handleFriendRequest("friend_001", FriendStatus.ACCEPTED);

        // Then
        assertEquals(FriendStatus.ACCEPTED, response.getStatus());
        assertNotNull(pendingRequest.getAcceptedAt());
        verify(friendRepository, times(2)).save(any(Friend.class)); // 双向好友关系
    }

    @Test
    @DisplayName("拒绝好友申请 - 成功")
    void handleFriendRequest_Reject() {
        // Given
        when(friendRepository.findById("friend_001")).thenReturn(Optional.of(pendingRequest));
        when(friendRepository.save(any(Friend.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        FriendResponse response = friendService.handleFriendRequest("friend_001", FriendStatus.REJECTED);

        // Then
        assertEquals(FriendStatus.REJECTED, response.getStatus());
    }

    @Test
    @DisplayName("删除好友 - 成功")
    void deleteFriend() {
        // Given
        when(friendRepository.findActiveFriendship("user_001", "user_002"))
            .thenReturn(Optional.of(acceptedFriend));

        // When
        friendService.deleteFriend("user_001", "user_002");

        // Then
        verify(friendRepository).delete(acceptedFriend);
        verify(friendRepository).deleteByUserIdAndFriendUserId("user_002", "user_001");
    }

    @Test
    @DisplayName("获取好友列表 - 成功")
    void getFriendList() {
        // Given
        List<Friend> friends = Arrays.asList(acceptedFriend);
        when(friendRepository.findFriendsByUserId("user_001")).thenReturn(friends);

        // When
        List<FriendResponse> results = friendService.getFriendList("user_001");

        // Then
        assertEquals(1, results.size());
    }

    @Test
    @DisplayName("星标好友 - 成功")
    void starFriend() {
        // Given
        when(friendRepository.findActiveFriendship("user_001", "user_002"))
            .thenReturn(Optional.of(acceptedFriend));

        // When
        FriendResponse response = friendService.starFriend("user_001", "user_002");

        // Then
        assertTrue(response.getIsStarred());
        verify(friendRepository).save(acceptedFriend);
    }

    @Test
    @DisplayName("取消星标好友 - 成功")
    void unstarFriend() {
        // Given
        acceptedFriend.setIsStarred(true);
        when(friendRepository.findActiveFriendship("user_001", "user_002"))
            .thenReturn(Optional.of(acceptedFriend));

        // When
        FriendResponse response = friendService.unstarFriend("user_001", "user_002");

        // Then
        assertFalse(response.getIsStarred());
    }

    @Test
    @DisplayName("设置消息免打扰 - 成功")
    void muteFriend() {
        // Given
        when(friendRepository.findActiveFriendship("user_001", "user_002"))
            .thenReturn(Optional.of(acceptedFriend));

        // When
        FriendResponse response = friendService.muteFriend("user_001", "user_002", true);

        // Then
        assertTrue(response.getIsMuted());
        verify(friendRepository).save(acceptedFriend);
    }

    @Test
    @DisplayName("加入黑名单 - 成功")
    void addToBlacklist() {
        // Given
        when(friendRepository.findActiveFriendship("user_001", "user_002"))
            .thenReturn(Optional.of(acceptedFriend));

        // When
        friendService.addToBlacklist("user_001", "user_002");

        // Then
        assertEquals(FriendStatus.BLOCKED, acceptedFriend.getStatus());
        verify(friendRepository).save(acceptedFriend);
    }

    @Test
    @DisplayName("移出黑名单 - 成功")
    void removeFromBlacklist() {
        // Given
        acceptedFriend.setStatus(FriendStatus.BLOCKED);
        when(friendRepository.findByUserIdAndFriendUserId("user_001", "user_002"))
            .thenReturn(Optional.of(acceptedFriend));

        // When
        friendService.removeFromBlacklist("user_001", "user_002");

        // Then
        verify(friendRepository).delete(acceptedFriend);
    }

    @Test
    @DisplayName("获取待处理的好友申请 - 成功")
    void getPendingRequests() {
        // Given
        List<Friend> requests = Arrays.asList(pendingRequest);
        when(friendRepository.findPendingReceivedRequests("user_002")).thenReturn(requests);

        // When
        List<FriendResponse> results = friendService.getPendingRequests("user_002");

        // Then
        assertEquals(1, results.size());
        assertEquals(FriendStatus.PENDING, results.get(0).getStatus());
    }

    @Test
    @DisplayName("修改好友备注 - 成功")
    void updateRemark() {
        // Given
        when(friendRepository.findActiveFriendship("user_001", "user_002"))
            .thenReturn(Optional.of(acceptedFriend));

        // When
        FriendResponse response = friendService.updateRemark("user_001", "user_002", "Best Friend");

        // Then
        assertEquals("Best Friend", response.getRemark());
        verify(friendRepository).save(acceptedFriend);
    }

    @Test
    @DisplayName("检查是否是好友 - 是")
    void areFriends_True() {
        // Given
        when(friendRepository.existsFriendship("user_001", "user_002")).thenReturn(true);

        // When
        boolean result = friendService.areFriends("user_001", "user_002");

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("检查是否是好友 - 否")
    void areFriends_False() {
        // Given
        when(friendRepository.existsFriendship("user_001", "user_002")).thenReturn(false);

        // When
        boolean result = friendService.areFriends("user_001", "user_002");

        // Then
        assertFalse(result);
    }

    private User createUser(String userId, String username) {
        User user = new User();
        user.setUserId(userId);
        user.setUsername(username);
        user.setNickname("Nickname_" + username);
        return user;
    }
}
