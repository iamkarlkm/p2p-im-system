package com.im.local.poi.service;

import com.im.core.websocket.WebSocketPushService;
import com.im.local.poi.dto.*;
import com.im.local.poi.entity.*;
import com.im.local.poi.enums.*;
import com.im.local.poi.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * POI智能客服服务
 * 商家智能客服、IM订单履约、用户咨询处理
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PoiCustomerService {

    private final PoiMerchantRepository merchantRepository;
    private final PoiChatSessionRepository sessionRepository;
    private final PoiChatMessageRepository messageRepository;
    private final PoiChatbotRepository chatbotRepository;
    private final PoiQuickReplyRepository quickReplyRepository;
    private final PoiConsultationRecordRepository consultationRepository;
    private final WebSocketPushService webSocketPushService;
    private final RedisTemplate<String, Object> redisTemplate;
    
    // Redis Key 前缀
    private static final String SESSION_KEY = "poi:session:";
    private static final String USER_SESSIONS_KEY = "poi:user:sessions:";
    private static final String MERCHANT_SESSIONS_KEY = "poi:merchant:sessions:";
    private static final String CHATBOT_CONTEXT_KEY = "poi:chatbot:context:";
    
    // 会话超时时间
    private static final long SESSION_TIMEOUT_MINUTES = 30;
    private static final long MESSAGE_CACHE_MINUTES = 60;
    
    /**
     * 创建客服会话
     * 用户发起咨询时调用
     */
    @Transactional
    public ChatSession createChatSession(CreateSessionRequest request) {
        log.info("创建客服会话，用户: {}, 商家: {}", request.getUserId(), request.getMerchantId());
        
        // 检查是否已存在活跃会话
        Optional<PoiChatSession> existingSession = sessionRepository
            .findByUserIdAndMerchantIdAndStatus(
                request.getUserId(), 
                request.getMerchantId(), 
                SessionStatus.ACTIVE
            );
        
        if (existingSession.isPresent()) {
            log.info("返回已有活跃会话，SessionID: {}", existingSession.get().getId());
            return convertToSessionDTO(existingSession.get());
        }
        
        // 创建新会话
        PoiChatSession session = PoiChatSession.builder()
            .sessionId(generateSessionId(request.getMerchantId()))
            .userId(request.getUserId())
            .merchantId(request.getMerchantId())
            .type(request.getType())
            .status(SessionStatus.ACTIVE)
            .source(request.getSource())
            .relatedOrderId(request.getRelatedOrderId())
            .relatedProductId(request.getRelatedProductId())
            .createTime(LocalDateTime.now())
            .lastActivityTime(LocalDateTime.now())
            .unreadCount(0)
            .build();
        
        sessionRepository.save(session);
        
        // 缓存会话
        cacheSession(session);
        
        // 发送欢迎消息（如果是智能客服模式）
        if (session.getType() == SessionType.AI_CHATBOT) {
            sendWelcomeMessage(session);
        }
        
        // 通知商家有新会话
        notifyMerchantNewSession(session);
        
        log.info("客服会话创建成功，SessionID: {}", session.getSessionId());
        
        return convertToSessionDTO(session);
    }
    
    /**
     * 发送消息
     */
    @Transactional
    public ChatMessage sendMessage(SendMessageRequest request) {
        log.debug("发送消息，Session: {}, 发送者: {}", 
            request.getSessionId(), request.getSenderId());
        
        PoiChatSession session = sessionRepository.findBySessionId(request.getSessionId())
            .orElseThrow(() -> new PoiServiceException("会话不存在"));
        
        // 检查会话状态
        if (session.getStatus() != SessionStatus.ACTIVE) {
            throw new PoiServiceException("会话已结束");
        }
        
        // 创建消息
        PoiChatMessage message = PoiChatMessage.builder()
            .sessionId(request.getSessionId())
            .senderId(request.getSenderId())
            .senderType(request.getSenderType())
            .contentType(request.getContentType())
            .content(request.getContent())
            .mediaUrl(request.getMediaUrl())
            .sendTime(LocalDateTime.now())
            .read(false)
            .build();
        
        messageRepository.save(message);
        
        // 更新会话最后活动时间
        session.setLastActivityTime(LocalDateTime.now());
        if (request.getSenderType() == SenderType.USER) {
            session.setUnreadCount(session.getUnreadCount() + 1);
        }
        sessionRepository.save(session);
        
        // 推送消息给接收方
        pushMessageToReceiver(session, message);
        
        // 如果是用户消息且会话是智能客服模式，触发AI回复
        if (request.getSenderType() == SenderType.USER && 
            session.getType() == SessionType.AI_CHATBOT) {
            processAIReply(session, message);
        }
        
        return convertToMessageDTO(message);
    }
    
    /**
     * 处理AI智能客服回复
     */
    private void processAIReply(PoiChatSession session, PoiChatMessage userMessage) {
        try {
            // 获取商家智能客服配置
            PoiChatbot chatbot = chatbotRepository.findByMerchantId(session.getMerchantId())
                .orElse(null);
            
            if (chatbot == null || !chatbot.isEnabled()) {
                return;
            }
            
            // 构建上下文
            List<PoiChatMessage> recentMessages = messageRepository
                .findTop10BySessionIdOrderBySendTimeDesc(session.getSessionId());
            
            // 调用AI处理
            String aiReply = generateAIReply(chatbot, userMessage, recentMessages);
            
            if (aiReply != null && !aiReply.isEmpty()) {
                // 创建AI回复消息
                PoiChatMessage replyMessage = PoiChatMessage.builder()
                    .sessionId(session.getSessionId())
                    .senderId(0L) // AI系统发送者ID
                    .senderType(SenderType.AI)
                    .contentType(ContentType.TEXT)
                    .content(aiReply)
                    .sendTime(LocalDateTime.now())
                    .read(false)
                    .build();
                
                messageRepository.save(replyMessage);
                
                // 更新会话
                session.setUnreadCount(session.getUnreadCount() + 1);
                session.setLastActivityTime(LocalDateTime.now());
                sessionRepository.save(session);
                
                // 推送给用户
                pushMessageToUser(session.getUserId(), replyMessage);
                
                // 检查是否需要转人工
                if (shouldTransferToHuman(chatbot, userMessage, aiReply)) {
                    transferToHuman(session);
                }
            }
        } catch (Exception e) {
            log.error("AI回复处理失败", e);
        }
    }
    
    /**
     * 生成AI回复
     */
    private String generateAIReply(PoiChatbot chatbot, PoiChatMessage userMessage, 
                                    List<PoiChatMessage> context) {
        String userQuestion = userMessage.getContent();
        
        // 1. 尝试匹配知识库
        Optional<PoiQuickReply> quickReply = quickReplyRepository
            .findByMerchantIdAndQuestionContaining(chatbot.getMerchantId(), userQuestion);
        
        if (quickReply.isPresent()) {
            return quickReply.get().getAnswer();
        }
        
        // 2. 使用预设回复模板
        if (userQuestion.contains("营业时间")) {
            return "我们的营业时间是每天 9:00-22:00，节假日正常营业~";
        } else if (userQuestion.contains("地址") || userQuestion.contains("在哪")) {
            return "您好，我们的地址在店铺详情页面可以查看，支持导航到店哦~";
        } else if (userQuestion.contains("优惠") || userQuestion.contains("折扣")) {
            return "目前店铺有满减活动，您可以点击优惠券查看详情~";
        } else if (userQuestion.contains("配送") || userQuestion.contains("多久")) {
            return "一般情况下30-45分钟送达，高峰期可能会有延迟，请耐心等待~";
        }
        
        // 3. 默认回复
        return "您好，请问有什么可以帮您的？您可以问我营业时间、地址、优惠活动等问题~";
    }
    
    /**
     * 判断是否转人工
     */
    private boolean shouldTransferToHuman(PoiChatbot chatbot, PoiChatMessage userMessage, 
                                          String aiReply) {
        // 如果用户明确请求人工
        String content = userMessage.getContent();
        if (content.contains("人工") || content.contains("客服") || content.contains("转人工")) {
            return true;
        }
        
        // 如果连续多次无法回答
        // 这里简化处理，实际应该统计连续未命中次数
        
        return false;
    }
    
    /**
     * 转人工客服
     */
    @Transactional
    public void transferToHuman(PoiChatSession session) {
        log.info("会话转人工，SessionID: {}", session.getSessionId());
        
        session.setType(SessionType.HUMAN_SERVICE);
        sessionRepository.save(session);
        
        // 发送转人工提示
        PoiChatMessage transferMessage = PoiChatMessage.builder()
            .sessionId(session.getSessionId())
            .senderId(0L)
            .senderType(SenderType.SYSTEM)
            .contentType(ContentType.TEXT)
            .content("已为您转接人工客服，请稍等...")
            .sendTime(LocalDateTime.now())
            .build();
        
        messageRepository.save(transferMessage);
        
        // 通知商家
        notifyMerchantTransferToHuman(session);
        
        // 推送转人工消息
        pushMessageToUser(session.getUserId(), transferMessage);
    }
    
    /**
     * 商家客服接入会话
     */
    @Transactional
    public void merchantJoinSession(Long merchantId, String sessionId, Long staffId) {
        log.info("商家客服接入，商家: {}, Session: {}, 客服: {}", 
            merchantId, sessionId, staffId);
        
        PoiChatSession session = sessionRepository.findBySessionId(sessionId)
            .orElseThrow(() -> new PoiServiceException("会话不存在"));
        
        if (!session.getMerchantId().equals(merchantId)) {
            throw new PoiServiceException("无权操作此会话");
        }
        
        session.setStaffId(staffId);
        session.setStatus(SessionStatus.ACTIVE);
        sessionRepository.save(session);
        
        // 发送客服接入提示
        PoiChatMessage joinMessage = PoiChatMessage.builder()
            .sessionId(sessionId)
            .senderId(staffId)
            .senderType(SenderType.MERCHANT)
            .contentType(ContentType.TEXT)
            .content("您好，我是店铺客服，很高兴为您服务~")
            .sendTime(LocalDateTime.now())
            .build();
        
        messageRepository.save(joinMessage);
        pushMessageToUser(session.getUserId(), joinMessage);
    }
    
    /**
     * 获取会话消息列表
     */
    public List<ChatMessage> getSessionMessages(String sessionId, Pageable pageable) {
        Page<PoiChatMessage> messages = messageRepository
            .findBySessionIdOrderBySendTimeDesc(sessionId, pageable);
        
        return messages.getContent().stream()
            .map(this::convertToMessageDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * 获取用户会话列表
     */
    public List<ChatSession> getUserSessions(Long userId, SessionStatus status) {
        List<PoiChatSession> sessions;
        if (status == null) {
            sessions = sessionRepository.findByUserIdOrderByLastActivityTimeDesc(userId);
        } else {
            sessions = sessionRepository.findByUserIdAndStatusOrderByLastActivityTimeDesc(
                userId, status);
        }
        
        return sessions.stream()
            .map(this::convertToSessionDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * 获取商家会话列表
     */
    public List<ChatSession> getMerchantSessions(Long merchantId, SessionStatus status) {
        List<PoiChatSession> sessions;
        if (status == null) {
            sessions = sessionRepository.findByMerchantIdOrderByLastActivityTimeDesc(merchantId);
        } else {
            sessions = sessionRepository.findByMerchantIdAndStatusOrderByLastActivityTimeDesc(
                merchantId, status);
        }
        
        return sessions.stream()
            .map(this::convertToSessionDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * 标记消息已读
     */
    @Transactional
    public void markMessagesAsRead(String sessionId, Long readerId) {
        List<PoiChatMessage> unreadMessages = messageRepository
            .findBySessionIdAndReadFalseAndSenderIdNot(sessionId, readerId);
        
        for (PoiChatMessage message : unreadMessages) {
            message.setRead(true);
            message.setReadTime(LocalDateTime.now());
        }
        
        messageRepository.saveAll(unreadMessages);
        
        // 更新会话未读数
        PoiChatSession session = sessionRepository.findBySessionId(sessionId)
            .orElse(null);
        if (session != null) {
            session.setUnreadCount(0);
            sessionRepository.save(session);
        }
    }
    
    /**
     * 结束会话
     */
    @Transactional
    public void closeSession(String sessionId, Long operatorId, String reason) {
        log.info("结束会话，Session: {}, 操作人: {}", sessionId, operatorId);
        
        PoiChatSession session = sessionRepository.findBySessionId(sessionId)
            .orElseThrow(() -> new PoiServiceException("会话不存在"));
        
        session.setStatus(SessionStatus.CLOSED);
        session.setCloseTime(LocalDateTime.now());
        session.setCloseReason(reason);
        sessionRepository.save(session);
        
        // 发送会话结束提示
        PoiChatMessage closeMessage = PoiChatMessage.builder()
            .sessionId(sessionId)
            .senderId(0L)
            .senderType(SenderType.SYSTEM)
            .contentType(ContentType.TEXT)
            .content("会话已结束，感谢您的咨询~")
            .sendTime(LocalDateTime.now())
            .build();
        
        messageRepository.save(closeMessage);
        
        // 推送结束消息
        pushMessageToUser(session.getUserId(), closeMessage);
        if (session.getStaffId() != null) {
            pushMessageToMerchant(session.getStaffId(), closeMessage);
        }
        
        // 清除缓存
        clearSessionCache(sessionId);
    }
    
    /**
     * 保存快捷回复
     */
    @Transactional
    public void saveQuickReply(Long merchantId, QuickReplyRequest request) {
        PoiQuickReply reply = PoiQuickReply.builder()
            .merchantId(merchantId)
            .category(request.getCategory())
            .question(request.getQuestion())
            .answer(request.getAnswer())
            .keywords(request.getKeywords())
            .priority(request.getPriority())
            .createTime(LocalDateTime.now())
            .build();
        
        quickReplyRepository.save(reply);
    }
    
    /**
     * 获取快捷回复列表
     */
    public List<QuickReplyDTO> getQuickReplies(Long merchantId, String category) {
        List<PoiQuickReply> replies;
        if (category == null) {
            replies = quickReplyRepository.findByMerchantIdOrderByPriorityDesc(merchantId);
        } else {
            replies = quickReplyRepository.findByMerchantIdAndCategory(merchantId, category);
        }
        
        return replies.stream()
            .map(r -> QuickReplyDTO.builder()
                .id(r.getId())
                .category(r.getCategory())
                .question(r.getQuestion())
                .answer(r.getAnswer())
                .keywords(r.getKeywords())
                .build())
            .collect(Collectors.toList());
    }
    
    // ==================== 私有方法 ====================
    
    private String generateSessionId(Long merchantId) {
        return "POI" + merchantId + System.currentTimeMillis();
    }
    
    private void cacheSession(PoiChatSession session) {
        String key = SESSION_KEY + session.getSessionId();
        redisTemplate.opsForValue().set(key, session, SESSION_TIMEOUT_MINUTES, TimeUnit.MINUTES);
    }
    
    private void clearSessionCache(String sessionId) {
        redisTemplate.delete(SESSION_KEY + sessionId);
    }
    
    private void pushMessageToReceiver(PoiChatSession session, PoiChatMessage message) {
        if (message.getSenderType() == SenderType.USER) {
            // 用户发的消息，推送给商家
            if (session.getStaffId() != null) {
                pushMessageToMerchant(session.getStaffId(), message);
            } else {
                notifyMerchantNewMessage(session.getMerchantId(), session, message);
            }
        } else {
            // 商家/AI发的消息，推送给用户
            pushMessageToUser(session.getUserId(), message);
        }
    }
    
    private void pushMessageToUser(Long userId, PoiChatMessage message) {
        webSocketPushService.pushToUser(userId, "POI_CHAT_MESSAGE", 
            convertToMessageDTO(message));
    }
    
    private void pushMessageToMerchant(Long merchantId, PoiChatMessage message) {
        webSocketPushService.pushToUser(merchantId, "POI_CHAT_MESSAGE", 
            convertToMessageDTO(message));
    }
    
    private void notifyMerchantNewSession(PoiChatSession session) {
        webSocketPushService.pushToUser(session.getMerchantId(), "NEW_POI_SESSION",
            NewSessionNotification.builder()
                .sessionId(session.getSessionId())
                .userId(session.getUserId())
                .type(session.getType())
                .relatedOrderId(session.getRelatedOrderId())
                .build());
    }
    
    private void notifyMerchantNewMessage(Long merchantId, PoiChatSession session, 
                                           PoiChatMessage message) {
        webSocketPushService.pushToUser(merchantId, "NEW_POI_MESSAGE",
            NewMessageNotification.builder()
                .sessionId(session.getSessionId())
                .userId(session.getUserId())
                .messageContent(message.getContent())
                .build());
    }
    
    private void notifyMerchantTransferToHuman(PoiChatSession session) {
        webSocketPushService.pushToUser(session.getMerchantId(), "TRANSFER_TO_HUMAN",
            TransferNotification.builder()
                .sessionId(session.getSessionId())
                .userId(session.getUserId())
                .reason("用户请求人工客服")
                .build());
    }
    
    private void sendWelcomeMessage(PoiChatSession session) {
        PoiChatMessage welcome = PoiChatMessage.builder()
            .sessionId(session.getSessionId())
            .senderId(0L)
            .senderType(SenderType.AI)
            .contentType(ContentType.TEXT)
            .content("您好！我是店铺智能助手，请问有什么可以帮您的？")
            .sendTime(LocalDateTime.now())
            .build();
        
        messageRepository.save(welcome);
        
        session.setUnreadCount(1);
        sessionRepository.save(session);
        
        pushMessageToUser(session.getUserId(), welcome);
    }
    
    private ChatSession convertToSessionDTO(PoiChatSession session) {
        return ChatSession.builder()
            .sessionId(session.getSessionId())
            .userId(session.getUserId())
            .merchantId(session.getMerchantId())
            .type(session.getType())
            .status(session.getStatus())
            .unreadCount(session.getUnreadCount())
            .createTime(session.getCreateTime())
            .lastActivityTime(session.getLastActivityTime())
            .relatedOrderId(session.getRelatedOrderId())
            .build();
    }
    
    private ChatMessage convertToMessageDTO(PoiChatMessage message) {
        return ChatMessage.builder()
            .id(message.getId())
            .sessionId(message.getSessionId())
            .senderId(message.getSenderId())
            .senderType(message.getSenderType())
            .contentType(message.getContentType())
            .content(message.getContent())
            .mediaUrl(message.getMediaUrl())
            .sendTime(message.getSendTime())
            .read(message.isRead())
            .build();
    }
}
