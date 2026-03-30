package com.im.controller;

import com.im.dto.ApiResponse;
import com.im.dto.MessageDTO;
import com.im.dto.SendMessageRequest;
import com.im.entity.Message;
import com.im.repository.MessageRepository;
import com.im.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 消息控制器 - 基础文本消息收发
 * 功能ID: #4
 * @author developer-agent
 * @since 2026-03-30
 */
@RestController
@RequestMapping("/api/message")
@CrossOrigin(origins = "*")
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private WebSocketService webSocketService;

    /**
     * 发送消息 (REST API)
     */
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<MessageDTO>> sendMessage(@RequestBody SendMessageRequest request) {
        // 创建消息实体
        Message message = new Message();
        message.setId(UUID.randomUUID().toString());
        message.setFromUserId(request.getFromUserId());
        message.setToUserId(request.getToUserId());
        message.setContent(request.getContent());
        message.setMessageType(request.getMessageType() != null ? request.getMessageType() : "TEXT");
        message.setStatus(0); // 0:未读 1:已读
        message.setCreatedAt(LocalDateTime.now());

        // 保存到数据库
        messageRepository.save(message);

        return ResponseEntity.ok(ApiResponse.success(toMessageDTO(message)));
    }

    /**
     * 获取消息历史
     */
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<MessageDTO>>> getMessageHistory(
            @RequestParam String userId1,
            @RequestParam String userId2,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Page<Message> messages = messageRepository.findConversation(
            userId1, userId2, 
            PageRequest.of(page, size, Sort.by("createdAt").descending())
        );

        List<MessageDTO> dtoList = messages.getContent().stream()
            .map(this::toMessageDTO)
            .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(dtoList));
    }

    /**
     * 标记消息已读
     */
    @PostMapping("/read/{messageId}")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable String messageId) {
        Message message = messageRepository.findById(messageId).orElse(null);
        if (message != null) {
            message.setStatus(1);
            messageRepository.save(message);
        }
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 批量标记已读
     */
    @PostMapping("/read/batch")
    public ResponseEntity<ApiResponse<Void>> markBatchAsRead(
            @RequestParam String fromUserId,
            @RequestParam String toUserId) {
        messageRepository.markConversationAsRead(fromUserId, toUserId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 获取未读消息数
     */
    @GetMapping("/unread/count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(@RequestParam String userId) {
        long count = messageRepository.countUnreadByToUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    /**
     * 获取未读消息列表
     */
    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<List<MessageDTO>>> getUnreadMessages(@RequestParam String userId) {
        List<Message> messages = messageRepository.findUnreadByToUserId(userId);
        List<MessageDTO> dtoList = messages.stream()
            .map(this::toMessageDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(dtoList));
    }

    /**
     * 撤回消息
     */
    @PostMapping("/recall/{messageId}")
    public ResponseEntity<ApiResponse<Void>> recallMessage(@PathVariable String messageId) {
        Message message = messageRepository.findById(messageId).orElse(null);
        if (message != null) {
            // 只能撤回2分钟内的消息
            if (message.getCreatedAt().plusMinutes(2).isAfter(LocalDateTime.now())) {
                message.setContent("[消息已撤回]");
                message.setRecalled(true);
                messageRepository.save(message);
                return ResponseEntity.ok(ApiResponse.success());
            } else {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "消息超过2分钟，无法撤回"));
            }
        }
        return ResponseEntity.badRequest()
            .body(ApiResponse.error(404, "消息不存在"));
    }

    /**
     * 删除消息
     */
    @DeleteMapping("/{messageId}")
    public ResponseEntity<ApiResponse<Void>> deleteMessage(@PathVariable String messageId) {
        messageRepository.deleteById(messageId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    // ============== 辅助方法 ==============

    private MessageDTO toMessageDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setFromUserId(message.getFromUserId());
        dto.setToUserId(message.getToUserId());
        dto.setContent(message.getContent());
        dto.setMessageType(message.getMessageType());
        dto.setStatus(message.getStatus());
        dto.setCreatedAt(message.getCreatedAt());
        dto.setRecalled(message.isRecalled());
        return dto;
    }
}
