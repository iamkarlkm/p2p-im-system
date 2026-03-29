package com.im.controller;

import com.im.entity.GroupSenderKeyEntity;
import com.im.service.GroupSenderKeyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 群组Sender Key REST API控制器
 * Signal Protocol群组加密的HTTP接口
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/group-sender-keys")
@RequiredArgsConstructor
public class GroupSenderKeyController {
    
    private final GroupSenderKeyService senderKeyService;
    
    // ==================== 密钥生成与分发 ====================
    
    /**
     * 生成并分发Sender Key给指定接收者
     * POST /api/v1/group-sender-keys/generate
     */
    @PostMapping("/generate")
    public ResponseEntity<GroupSenderKeyEntity> generateSenderKey(@RequestBody GenerateRequest request) {
        GroupSenderKeyEntity senderKey = senderKeyService.generateSenderKey(
            request.groupId(),
            request.senderId(),
            request.receiverId()
        );
        return ResponseEntity.ok(senderKey);
    }
    
    /**
     * 批量生成分发给所有群成员
     * POST /api/v1/group-sender-keys/distribute
     */
    @PostMapping("/distribute")
    public ResponseEntity<List<GroupSenderKeyEntity>> distributeToMembers(@RequestBody DistributeRequest request) {
        List<GroupSenderKeyEntity> keys = senderKeyService.generateAndDistributeToMembers(
            request.groupId(),
            request.senderId(),
            request.memberIds()
        );
        return ResponseEntity.ok(keys);
    }
    
    // ==================== 密钥状态管理 ====================
    
    /**
     * 激活Sender Key
     * PUT /api/v1/group-sender-keys/{senderKeyId}/activate
     */
    @PutMapping("/{senderKeyId}/activate")
    public ResponseEntity<GroupSenderKeyEntity> activateSenderKey(@PathVariable String senderKeyId) {
        GroupSenderKeyEntity senderKey = senderKeyService.activateSenderKey(senderKeyId);
        return ResponseEntity.ok(senderKey);
    }
    
    /**
     * 确认收到Sender Key
     * PUT /api/v1/group-sender-keys/{senderKeyId}/acknowledge
     */
    @PutMapping("/{senderKeyId}/acknowledge")
    public ResponseEntity<GroupSenderKeyEntity> acknowledgeSenderKey(@PathVariable String senderKeyId) {
        GroupSenderKeyEntity senderKey = senderKeyService.acknowledgeSenderKey(senderKeyId);
        return ResponseEntity.ok(senderKey);
    }
    
    /**
     * 批量确认Sender Key
     * PUT /api/v1/group-sender-keys/batch-acknowledge
     */
    @PutMapping("/batch-acknowledge")
    public ResponseEntity<Map<String, Object>> batchAcknowledge(@RequestBody Map<String, List<String>> request) {
        List<String> senderKeyIds = request.get("senderKeyIds");
        int count = senderKeyService.batchAcknowledge(senderKeyIds);
        return ResponseEntity.ok(Map.of("acknowledged", count));
    }
    
    // ==================== 消息密钥操作 ====================
    
    /**
     * 获取下一条消息的密钥
     * GET /api/v1/group-sender-keys/{senderKeyId}/message-key
     */
    @GetMapping("/{senderKeyId}/message-key")
    public ResponseEntity<Map<String, Object>> getNextMessageKey(@PathVariable String senderKeyId) {
        String messageKey = senderKeyService.getNextMessageKey(senderKeyId);
        return ResponseEntity.ok(Map.of(
            "senderKeyId", senderKeyId,
            "messageKey", messageKey
        ));
    }
    
    /**
     * 派生消息密钥（不推进链）
     * GET /api/v1/group-sender-keys/{senderKeyId}/derive/{index}
     */
    @GetMapping("/{senderKeyId}/derive/{index}")
    public ResponseEntity<Map<String, String>> deriveMessageKey(
            @PathVariable String senderKeyId, @PathVariable long index) {
        Optional<GroupSenderKeyEntity> optKey = senderKeyService.getSenderKey(senderKeyId);
        if (optKey.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        String messageKey = senderKeyService.deriveMessageKey(optKey.get().getChainKey(), index);
        return ResponseEntity.ok(Map.of("messageKey", messageKey));
    }
    
    // ==================== 密钥轮换 ====================
    
    /**
     * 开始密钥轮换
     * POST /api/v1/group-sender-keys/{groupId}/{senderId}/rotation/start
     */
    @PostMapping("/{groupId}/{senderId}/rotation/start")
    public ResponseEntity<Map<String, Object>> startRotation(
            @PathVariable String groupId, @PathVariable String senderId) {
        GroupSenderKeyEntity key = senderKeyService.startRotation(groupId, senderId);
        return ResponseEntity.ok(Map.of(
            "groupId", groupId,
            "senderId", senderId,
            "status", "RATCHETING",
            "senderKeyId", key != null ? key.getSenderKeyId() : ""
        ));
    }
    
    /**
     * 完成密钥轮换
     * POST /api/v1/group-sender-keys/{groupId}/{senderId}/rotation/complete
     */
    @PostMapping("/{groupId}/{senderId}/rotation/complete")
    public ResponseEntity<List<GroupSenderKeyEntity>> completeRotation(
            @PathVariable String groupId,
            @PathVariable String senderId,
            @RequestBody Map<String, List<String>> request) {
        List<String> receiverIds = request.get("receiverIds");
        List<GroupSenderKeyEntity> newKeys = senderKeyService.completeRotation(groupId, senderId, receiverIds);
        return ResponseEntity.ok(newKeys);
    }
    
    // ==================== 密钥撤销 ====================
    
    /**
     * 撤销单个Sender Key
     * DELETE /api/v1/group-sender-keys/{senderKeyId}
     */
    @DeleteMapping("/{senderKeyId}")
    public ResponseEntity<Map<String, Object>> revokeSenderKey(@PathVariable String senderKeyId) {
        int count = senderKeyService.revokeSenderKey(senderKeyId);
        return ResponseEntity.ok(Map.of("revoked", count));
    }
    
    /**
     * 撤销发送者的所有Sender Key
     * DELETE /api/v1/group-sender-keys/{groupId}/sender/{senderId}
     */
    @DeleteMapping("/{groupId}/sender/{senderId}")
    public ResponseEntity<Map<String, Object>> revokeAllBySender(
            @PathVariable String groupId, @PathVariable String senderId) {
        int count = senderKeyService.revokeAllBySender(groupId, senderId);
        return ResponseEntity.ok(Map.of("revoked", count, "groupId", groupId, "senderId", senderId));
    }
    
    /**
     * 撤销接收者的所有Sender Key
     * DELETE /api/v1/group-sender-keys/{groupId}/receiver/{receiverId}
     */
    @DeleteMapping("/{groupId}/receiver/{receiverId}")
    public ResponseEntity<Map<String, Object>> revokeAllByReceiver(
            @PathVariable String groupId, @PathVariable String receiverId) {
        int count = senderKeyService.revokeAllByReceiver(groupId, receiverId);
        return ResponseEntity.ok(Map.of("revoked", count, "groupId", groupId, "receiverId", receiverId));
    }
    
    // ==================== 查询接口 ====================
    
    /**
     * 获取Sender Key详情
     * GET /api/v1/group-sender-keys/{senderKeyId}
     */
    @GetMapping("/{senderKeyId}")
    public ResponseEntity<GroupSenderKeyEntity> getSenderKey(@PathVariable String senderKeyId) {
        Optional<GroupSenderKeyEntity> senderKey = senderKeyService.getSenderKey(senderKeyId);
        return senderKey.map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 获取群组中所有激活的Sender Key
     * GET /api/v1/group-sender-keys/group/{groupId}/active
     */
    @GetMapping("/group/{groupId}/active")
    public ResponseEntity<List<GroupSenderKeyEntity>> getActiveKeysInGroup(@PathVariable String groupId) {
        List<GroupSenderKeyEntity> keys = senderKeyService.getActiveKeysInGroup(groupId);
        return ResponseEntity.ok(keys);
    }
    
    /**
     * 获取发送者发给接收者的最新Sender Key
     * GET /api/v1/group-sender-keys/{groupId}/{senderId}/{receiverId}/latest
     */
    @GetMapping("/{groupId}/{senderId}/{receiverId}/latest")
    public ResponseEntity<GroupSenderKeyEntity> getLatestKey(
            @PathVariable String groupId,
            @PathVariable String senderId,
            @PathVariable String receiverId) {
        Optional<GroupSenderKeyEntity> key = senderKeyService.getLatestKey(groupId, senderId, receiverId);
        return key.map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 获取用户在群组中持有的所有Sender Key
     * GET /api/v1/group-sender-keys/{groupId}/receiver/{receiverId}
     */
    @GetMapping("/{groupId}/receiver/{receiverId}")
    public ResponseEntity<List<GroupSenderKeyEntity>> getKeysForReceiver(
            @PathVariable String groupId, @PathVariable String receiverId) {
        List<GroupSenderKeyEntity> keys = senderKeyService.getKeysForReceiver(groupId, receiverId);
        return ResponseEntity.ok(keys);
    }
    
    // ==================== 统计接口 ====================
    
    /**
     * 获取群组Sender Key统计信息
     * GET /api/v1/group-sender-keys/{groupId}/stats
     */
    @GetMapping("/{groupId}/stats")
    public ResponseEntity<GroupSenderKeyService.SenderKeyStats> getStats(@PathVariable String groupId) {
        GroupSenderKeyService.SenderKeyStats stats = senderKeyService.getStats(groupId);
        return ResponseEntity.ok(stats);
    }
    
    /**
     * 统计群组成员数量
     * GET /api/v1/group-sender-keys/{groupId}/members/count
     */
    @GetMapping("/{groupId}/members/count")
    public ResponseEntity<Map<String, Long>> countMembers(@PathVariable String groupId) {
        long count = senderKeyService.countMembersInGroup(groupId);
        return ResponseEntity.ok(Map.of("memberCount", count));
    }
    
    // ==================== 清理接口 ====================
    
    /**
     * 删除群组的所有Sender Key
     * DELETE /api/v1/group-sender-keys/group/{groupId}
     */
    @DeleteMapping("/group/{groupId}")
    public ResponseEntity<Map<String, Object>> deleteGroupKeys(@PathVariable String groupId) {
        int count = senderKeyService.deleteGroupKeys(groupId);
        return ResponseEntity.ok(Map.of("deleted", count, "groupId", groupId));
    }
    
    /**
     * 清理过期的Sender Key记录
     * DELETE /api/v1/group-sender-keys/cleanup?daysOld=30
     */
    @DeleteMapping("/cleanup")
    public ResponseEntity<Map<String, Object>> cleanupStaleKeys(
            @RequestParam(defaultValue = "30") int daysOld) {
        int count = senderKeyService.cleanupStaleKeys(daysOld);
        return ResponseEntity.ok(Map.of("cleaned", count, "daysOld", daysOld));
    }
    
    // ==================== 请求DTO ====================
    
    public record GenerateRequest(String groupId, String senderId, String receiverId) {}
    public record DistributeRequest(String groupId, String senderId, List<String> memberIds) {}
}
