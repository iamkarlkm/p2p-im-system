package com.im.server.controller;

import com.im.server.dto.ApiResponse;
import com.im.server.dto.SendMessageRequest;
import com.im.server.entity.Message;
import com.im.server.service.MessageService;
import com.im.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 消息控制器
 */
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    
    private final MessageService messageService;
    private final UserService userService;
    
    /**
     * 发送消息
     */
    @PostMapping
    public ApiResponse<Message> sendMessage(@RequestHeader("Authorization") String token,
                                              @RequestBody SendMessageRequest request) {
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return ApiResponse.error(401, "Token无效");
        }
        
        try {
            Message message = messageService.sendMessage(
                    userId,
                    request.getToUserId(),
                    request.getChatType(),
                    request.getChatId(),
                    request.getMsgType(),
                    request.getContent()
            );
            return ApiResponse.success(message);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 获取私聊消息历史
     */
    @GetMapping("/private/{userId}")
    public ApiResponse<List<Message>> getPrivateChatHistory(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        Long currentUserId = userService.verifyToken(token.replace("Bearer ", ""));
        if (currentUserId == null) {
            return ApiResponse.error(401, "Token无效");
        }
        
        List<Message> messages = messageService.getPrivateChatHistory(currentUserId, userId, page, size);
        return ApiResponse.success(messages);
    }
    
    /**
     * 获取群聊消息历史
     */
    @GetMapping("/group/{groupId}")
    public ApiResponse<List<Message>> getGroupChatHistory(
            @RequestHeader("Authorization") String token,
            @PathVariable Long groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return ApiResponse.error(401, "Token无效");
        }
        
        List<Message> messages = messageService.getGroupChatHistory(groupId, page, size);
        return ApiResponse.success(messages);
    }
    
    /**
     * 标记消息为已读
     */
    @PostMapping("/read/{fromUserId}")
    public ApiResponse<Void> markAsRead(@RequestHeader("Authorization") String token,
                                          @PathVariable Long fromUserId) {
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return ApiResponse.error(401, "Token无效");
        }
        
        messageService.markAsRead(userId, fromUserId);
        return ApiResponse.success(null);
    }
    
    /**
     * 获取未读消息数
     */
    @GetMapping("/unread")
    public ApiResponse<Integer> getUnreadCount(@RequestHeader("Authorization") String token) {
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return ApiResponse.error(401, "Token无效");
        }
        
        int count = messageService.getUnreadCount(userId);
        return ApiResponse.success(count);
    }
    
    /**
     * 撤回消息
     */
    @PostMapping("/recall/{msgId}")
    public ApiResponse<Message> recallMessage(@RequestHeader("Authorization") String token,
                                             @PathVariable String msgId) {
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return ApiResponse.error(401, "Token无效");
        }
        
        try {
            Message message = messageService.recallMessage(msgId, userId);
            return ApiResponse.success(message);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 获取会话的最近消息
     */
    @GetMapping("/latest")
    public ApiResponse<Message> getLatestMessage(
            @RequestHeader("Authorization") String token,
            @RequestParam Long chatId,
            @RequestParam Integer chatType) {
        
        Long userId = userService.verifyToken(token.replace("Bearer ", ""));
        if (userId == null) {
            return ApiResponse.error(401, "Token无效");
        }
        
        Message message = messageService.getLatestMessage(chatId, chatType);
        return ApiResponse.success(message);
    }
}
