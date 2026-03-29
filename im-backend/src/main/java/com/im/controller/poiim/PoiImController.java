package com.im.controller.poiim;

import com.im.common.Result;
import com.im.entity.poiim.PoiImSession;
import com.im.entity.poiim.PoiImMessage;
import com.im.service.poiim.PoiImService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * POI智能客服IM控制器
 */
@RestController
@RequestMapping("/api/v1/poi-im")
public class PoiImController {
    
    @Autowired
    private PoiImService poiImService;
    
    /**
     * 创建会话
     */
    @PostMapping("/sessions")
    public Result<PoiImSession> createSession(
            @RequestParam String poiId,
            @RequestParam String userId,
            @RequestParam(required = false, defaultValue = "POI_PAGE") String source) {
        PoiImSession session = poiImService.createSession(poiId, userId, source);
        return Result.success(session);
    }
    
    /**
     * 从围栏触发创建会话
     */
    @PostMapping("/sessions/from-fence")
    public Result<PoiImSession> createSessionFromFence(
            @RequestParam String poiId,
            @RequestParam String userId,
            @RequestParam Double longitude,
            @RequestParam Double latitude) {
        PoiImSession session = poiImService.createSessionFromFence(poiId, userId, longitude, latitude);
        return Result.success(session);
    }
    
    /**
     * 发送消息
     */
    @PostMapping("/sessions/{sessionId}/messages")
    public Result<PoiImMessage> sendMessage(
            @PathVariable String sessionId,
            @RequestParam String senderId,
            @RequestParam String senderType,
            @RequestParam String messageType,
            @RequestParam String content) {
        PoiImMessage message = poiImService.sendMessage(sessionId, senderId, senderType, messageType, content);
        return Result.success(message);
    }
    
    /**
     * 获取会话消息历史
     */
    @GetMapping("/sessions/{sessionId}/messages")
    public Result<List<PoiImMessage>> getSessionMessages(
            @PathVariable String sessionId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        List<PoiImMessage> messages = poiImService.getSessionMessages(sessionId, page, size);
        return Result.success(messages);
    }
    
    /**
     * 获取用户的会话列表
     */
    @GetMapping("/users/{userId}/sessions")
    public Result<List<PoiImSession>> getUserSessions(@PathVariable String userId) {
        List<PoiImSession> sessions = poiImService.getUserSessions(userId);
        return Result.success(sessions);
    }
    
    /**
     * 获取商家的会话列表
     */
    @GetMapping("/merchants/{merchantId}/sessions")
    public Result<List<PoiImSession>> getMerchantSessions(
            @PathVariable String merchantId,
            @RequestParam(required = false) String status) {
        List<PoiImSession> sessions = poiImService.getMerchantSessions(merchantId, status);
        return Result.success(sessions);
    }
    
    /**
     * 获取会话详情
     */
    @GetMapping("/sessions/{sessionId}")
    public Result<PoiImSession> getSession(@PathVariable String sessionId) {
        PoiImSession session = poiImService.getSession(sessionId);
        return Result.success(session);
    }
    
    /**
     * 转接客服
     */
    @PostMapping("/sessions/{sessionId}/transfer")
    public Result<Void> transferAgent(
            @PathVariable String sessionId,
            @RequestParam String newAgentId) {
        poiImService.transferAgent(sessionId, newAgentId);
        return Result.success();
    }
    
    /**
     * 关闭会话
     */
    @PostMapping("/sessions/{sessionId}/close")
    public Result<Void> closeSession(@PathVariable String sessionId) {
        poiImService.closeSession(sessionId);
        return Result.success();
    }
    
    /**
     * 标记消息已读
     */
    @PostMapping("/sessions/{sessionId}/read")
    public Result<Void> markMessagesRead(
            @PathVariable String sessionId,
            @RequestParam String userId) {
        poiImService.markMessagesRead(sessionId, userId);
        return Result.success();
    }
    
    /**
     * 获取会话统计
     */
    @GetMapping("/merchants/{merchantId}/stats")
    public Result<Map<String, Object>> getMerchantStats(@PathVariable String merchantId) {
        List<PoiImSession> allSessions = poiImService.getMerchantSessions(merchantId, null);
        List<PoiImSession> activeSessions = poiImService.getMerchantSessions(merchantId, "ACTIVE");
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSessions", allSessions.size());
        stats.put("activeSessions", activeSessions.size());
        stats.put("merchantId", merchantId);
        
        return Result.success(stats);
    }
}
