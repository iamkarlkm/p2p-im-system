package com.im.backend.controller;

import com.im.backend.dto.ApiResponse;
import com.im.backend.dto.MessagePinDTO;
import com.im.backend.model.PinnedMessage;
import com.im.backend.model.User;
import com.im.backend.service.MessagePinService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 消息置顶控制器
 * 提供消息置顶的RESTful API
 */
@RestController
@RequestMapping("/api/v1/conversations/{conversationId}/pins")
public class MessagePinController {
    
    private static final Logger logger = LoggerFactory.getLogger(MessagePinController.class);
    
    @Autowired
    private MessagePinService messagePinService;
    
    /**
     * 置顶消息
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PinnedMessage>> pinMessage(
            @PathVariable Long conversationId,
            @Valid @RequestBody MessagePinDTO dto,
            @AuthenticationPrincipal User currentUser) {
        
        logger.info("Request to pin message in conversation {}, user={}", conversationId, currentUser.getId());
        
        dto.setConversationId(conversationId);
        PinnedMessage pinnedMessage = messagePinService.pinMessage(dto, currentUser.getId());
        
        return ResponseEntity.ok(ApiResponse.success("消息置顶成功", pinnedMessage));
    }
    
    /**
     * 获取会话置顶消息列表
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PinnedMessage>>> getPinnedMessages(
            @PathVariable Long conversationId,
            @AuthenticationPrincipal User currentUser) {
        
        logger.info("Request to get pinned messages for conversation {}, user={}", 
                    conversationId, currentUser.getId());
        
        List<PinnedMessage> pins = messagePinService.getPinnedMessages(conversationId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(pins));
    }
    
    /**
     * 分页获取置顶消息
     */
    @GetMapping("/page")
    public ResponseEntity<ApiResponse<Page<PinnedMessage>>> getPinnedMessagesPageable(
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal User currentUser) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "pinOrder"));
        Page<PinnedMessage> pins = messagePinService.getPinnedMessagesPageable(
            conversationId, currentUser.getId(), pageable);
        
        return ResponseEntity.ok(ApiResponse.success(pins));
    }
    
    /**
     * 取消置顶（通过置顶ID）
     */
    @DeleteMapping("/{pinId}")
    public ResponseEntity<ApiResponse<Void>> unpinMessage(
            @PathVariable Long conversationId,
            @PathVariable Long pinId,
            @AuthenticationPrincipal User currentUser) {
        
        logger.info("Request to unpin message {}, conversation={}, user={}", 
                    pinId, conversationId, currentUser.getId());
        
        messagePinService.unpinMessage(pinId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("取消置顶成功", null));
    }
    
    /**
     * 取消置顶（通过消息ID）
     */
    @DeleteMapping("/message/{messageId}")
    public ResponseEntity<ApiResponse<Void>> unpinByMessageId(
            @PathVariable Long conversationId,
            @PathVariable Long messageId,
            @AuthenticationPrincipal User currentUser) {
        
        logger.info("Request to unpin message {} in conversation {}, user={}", 
                    messageId, conversationId, currentUser.getId());
        
        messagePinService.unpinByMessageId(messageId, conversationId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("取消置顶成功", null));
    }
    
    /**
     * 更新置顶排序
     */
    @PutMapping("/{pinId}/order")
    public ResponseEntity<ApiResponse<Void>> updatePinOrder(
            @PathVariable Long conversationId,
            @PathVariable Long pinId,
            @RequestParam Integer newOrder,
            @AuthenticationPrincipal User currentUser) {
        
        logger.info("Request to update pin order: pinId={}, newOrder={}, user={}", 
                    pinId, newOrder, currentUser.getId());
        
        messagePinService.updatePinOrder(pinId, newOrder, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("排序更新成功", null));
    }
    
    /**
     * 批量更新置顶排序
     */
    @PutMapping("/order/batch")
    public ResponseEntity<ApiResponse<Void>> batchUpdatePinOrder(
            @PathVariable Long conversationId,
            @Valid @RequestBody List<MessagePinDTO.PinOrderUpdateDTO> updates,
            @AuthenticationPrincipal User currentUser) {
        
        logger.info("Request to batch update pin orders, count={}, user={}", 
                    updates.size(), currentUser.getId());
        
        messagePinService.batchUpdatePinOrder(updates, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("批量排序更新成功", null));
    }
    
    /**
     * 更新置顶备注
     */
    @PutMapping("/{pinId}/note")
    public ResponseEntity<ApiResponse<PinnedMessage>> updatePinNote(
            @PathVariable Long conversationId,
            @PathVariable Long pinId,
            @RequestParam String note,
            @AuthenticationPrincipal User currentUser) {
        
        logger.info("Request to update pin note: pinId={}, user={}", pinId, currentUser.getId());
        
        PinnedMessage updated = messagePinService.updatePinNote(pinId, note, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("备注更新成功", updated));
    }
    
    /**
     * 取消会话所有置顶
     */
    @DeleteMapping("/all")
    public ResponseEntity<ApiResponse<Void>> unpinAllMessages(
            @PathVariable Long conversationId,
            @AuthenticationPrincipal User currentUser) {
        
        logger.info("Request to unpin all messages in conversation {}, user={}", 
                    conversationId, currentUser.getId());
        
        messagePinService.unpinAllMessages(conversationId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("已取消所有置顶", null));
    }
    
    /**
     * 检查消息是否已置顶
     */
    @GetMapping("/check")
    public ResponseEntity<ApiResponse<Boolean>> isMessagePinned(
            @PathVariable Long conversationId,
            @RequestParam Long messageId,
            @AuthenticationPrincipal User currentUser) {
        
        boolean isPinned = messagePinService.isMessagePinned(messageId, conversationId);
        return ResponseEntity.ok(ApiResponse.success(isPinned));
    }
    
    /**
     * 获取用户所有置顶消息
     */
    @GetMapping("/user/all")
    public ResponseEntity<ApiResponse<List<PinnedMessage>>> getUserPinnedMessages(
            @AuthenticationPrincipal User currentUser) {
        
        logger.info("Request to get all pinned messages for user {}", currentUser.getId());
        
        List<PinnedMessage> pins = messagePinService.getUserPinnedMessages(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(pins));
    }
}
