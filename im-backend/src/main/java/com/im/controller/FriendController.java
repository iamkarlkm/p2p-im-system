package com.im.controller;

import com.im.dto.ApiResponse;
import com.im.dto.FriendRequestDTO;
import com.im.dto.FriendResponseDTO;
import com.im.dto.SendFriendRequestDTO;
import com.im.entity.FriendRelation;
import com.im.entity.FriendRequest;
import com.im.entity.User;
import com.im.repository.FriendRelationRepository;
import com.im.repository.FriendRequestRepository;
import com.im.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 好友关系控制器
 * 功能ID: #5
 * @author developer-agent
 * @since 2026-03-30
 */
@RestController
@RequestMapping("/api/friend")
@CrossOrigin(origins = "*")
public class FriendController {

    @Autowired
    private FriendRelationRepository friendRelationRepository;

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 发送好友申请
     */
    @PostMapping("/request")
    public ResponseEntity<ApiResponse<Void>> sendFriendRequest(@RequestBody SendFriendRequestDTO request) {
        // 检查是否已经是好友
        if (friendRelationRepository.existsByUserIdAndFriendId(request.getFromUserId(), request.getToUserId())) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, "已经是好友"));
        }

        // 检查是否已有待处理的申请
        if (friendRequestRepository.existsPendingRequest(request.getFromUserId(), request.getToUserId())) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, "已发送过好友申请"));
        }

        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setId(UUID.randomUUID().toString());
        friendRequest.setFromUserId(request.getFromUserId());
        friendRequest.setToUserId(request.getToUserId());
        friendRequest.setMessage(request.getMessage());
        friendRequest.setStatus(0); // 0:待处理 1:已同意 2:已拒绝
        friendRequest.setCreatedAt(LocalDateTime.now());

        friendRequestRepository.save(friendRequest);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 处理好友申请
     */
    @PostMapping("/request/{requestId}/handle")
    public ResponseEntity<ApiResponse<Void>> handleFriendRequest(
            @PathVariable String requestId,
            @RequestParam int status) {
        
        FriendRequest request = friendRequestRepository.findById(requestId).orElse(null);
        if (request == null) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(404, "申请记录不存在"));
        }

        request.setStatus(status);
        request.setHandledAt(LocalDateTime.now());
        friendRequestRepository.save(request);

        // 如果同意，建立双向好友关系
        if (status == 1) {
            createBidirectionalFriendship(request.getFromUserId(), request.getToUserId());
        }

        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 获取好友列表
     */
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<FriendResponseDTO>>> getFriendList(@RequestParam String userId) {
        List<FriendRelation> friends = friendRelationRepository.findByUserId(userId);
        
        List<FriendResponseDTO> dtoList = friends.stream()
            .map(f -> {
                User friend = userRepository.findById(f.getFriendId()).orElse(null);
                return toFriendResponseDTO(f, friend);
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(dtoList));
    }

    /**
     * 获取收到的好友申请
     */
    @GetMapping("/requests/received")
    public ResponseEntity<ApiResponse<List<FriendRequestDTO>>> getReceivedRequests(@RequestParam String userId) {
        List<FriendRequest> requests = friendRequestRepository.findByToUserIdAndStatus(userId, 0);
        
        List<FriendRequestDTO> dtoList = requests.stream()
            .map(this::toFriendRequestDTO)
            .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(dtoList));
    }

    /**
     * 获取发送的好友申请
     */
    @GetMapping("/requests/sent")
    public ResponseEntity<ApiResponse<List<FriendRequestDTO>>> getSentRequests(@RequestParam String userId) {
        List<FriendRequest> requests = friendRequestRepository.findByFromUserId(userId);
        
        List<FriendRequestDTO> dtoList = requests.stream()
            .map(this::toFriendRequestDTO)
            .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(dtoList));
    }

    /**
     * 删除好友
     */
    @DeleteMapping("/{friendId}")
    public ResponseEntity<ApiResponse<Void>> deleteFriend(
            @RequestParam String userId,
            @PathVariable String friendId) {
        
        friendRelationRepository.deleteByUserIdAndFriendId(userId, friendId);
        friendRelationRepository.deleteByUserIdAndFriendId(friendId, userId);
        
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 检查是否为好友
     */
    @GetMapping("/check")
    public ResponseEntity<ApiResponse<Boolean>> checkFriendship(
            @RequestParam String userId1,
            @RequestParam String userId2) {
        
        boolean isFriend = friendRelationRepository.existsByUserIdAndFriendId(userId1, userId2);
        return ResponseEntity.ok(ApiResponse.success(isFriend));
    }

    /**
     * 搜索好友
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<FriendResponseDTO>>> searchFriends(
            @RequestParam String userId,
            @RequestParam String keyword) {
        
        List<User> users = userRepository.searchByKeyword(keyword);
        
        List<FriendResponseDTO> dtoList = users.stream()
            .filter(u -> !u.getId().equals(userId))
            .map(u -> {
                boolean isFriend = friendRelationRepository.existsByUserIdAndFriendId(userId, u.getId());
                FriendResponseDTO dto = new FriendResponseDTO();
                dto.setId(u.getId());
                dto.setUsername(u.getUsername());
                dto.setNickname(u.getNickname());
                dto.setAvatar(u.getAvatar());
                dto.setFriend(isFriend);
                return dto;
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(dtoList));
    }

    // ============== 辅助方法 ==============

    private void createBidirectionalFriendship(String userId1, String userId2) {
        FriendRelation relation1 = new FriendRelation();
        relation1.setId(UUID.randomUUID().toString());
        relation1.setUserId(userId1);
        relation1.setFriendId(userId2);
        relation1.setCreatedAt(LocalDateTime.now());
        friendRelationRepository.save(relation1);

        FriendRelation relation2 = new FriendRelation();
        relation2.setId(UUID.randomUUID().toString());
        relation2.setUserId(userId2);
        relation2.setFriendId(userId1);
        relation2.setCreatedAt(LocalDateTime.now());
        friendRelationRepository.save(relation2);
    }

    private FriendResponseDTO toFriendResponseDTO(FriendRelation relation, User friend) {
        FriendResponseDTO dto = new FriendResponseDTO();
        if (friend != null) {
            dto.setId(friend.getId());
            dto.setUsername(friend.getUsername());
            dto.setNickname(friend.getNickname());
            dto.setAvatar(friend.getAvatar());
        }
        dto.setFriendSince(relation.getCreatedAt());
        dto.setFriend(true);
        return dto;
    }

    private FriendRequestDTO toFriendRequestDTO(FriendRequest request) {
        FriendRequestDTO dto = new FriendRequestDTO();
        dto.setId(request.getId());
        dto.setFromUserId(request.getFromUserId());
        dto.setToUserId(request.getToUserId());
        dto.setMessage(request.getMessage());
        dto.setStatus(request.getStatus());
        dto.setCreatedAt(request.getCreatedAt());
        
        User fromUser = userRepository.findById(request.getFromUserId()).orElse(null);
        if (fromUser != null) {
            dto.setFromUsername(fromUser.getUsername());
            dto.setFromNickname(fromUser.getNickname());
            dto.setFromAvatar(fromUser.getAvatar());
        }
        
        return dto;
    }
}
