package com.im.backend.modules.poi.customer_service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.im.backend.common.util.JsonUtils;
import com.im.backend.common.util.SnowflakeIdGenerator;
import com.im.backend.modules.poi.customer_service.dto.*;
import com.im.backend.modules.poi.customer_service.entity.*;
import com.im.backend.modules.poi.customer_service.enums.*;
import com.im.backend.modules.poi.customer_service.repository.*;
import com.im.backend.modules.poi.customer_service.service.IPoiCustomerService;
import com.im.backend.modules.websocket.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * POI智能客服服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PoiCustomerServiceImpl extends ServiceImpl<PoiCustomerServiceMapper, PoiCustomerService> implements IPoiCustomerService {

    private final PoiCustomerServiceMapper agentMapper;
    private final PoiCustomerServiceSessionMapper sessionMapper;
    private final PoiCustomerServiceMessageMapper messageMapper;
    private final PoiCustomerServiceFaqMapper faqMapper;
    private final StringRedisTemplate redisTemplate;
    private final WebSocketService webSocketService;
    private final SnowflakeIdGenerator idGenerator;

    private static final String AGENT_STATUS_KEY = "cs:agent:status:";
    private static final String SESSION_USER_KEY = "cs:session:user:";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    @Transactional
    public SessionResponse createSession(Long userId, CreateSessionRequest request) {
        // 检查是否已有活跃会话
        PoiCustomerServiceSession existingSession = sessionMapper.selectActiveSessionByUserAndPoi(userId, request.getPoiId());
        if (existingSession != null) {
            return convertToSessionResponse(existingSession);
        }

        // 创建新会话
        String sessionId = generateSessionId();
        PoiCustomerServiceSession session = new PoiCustomerServiceSession();
        session.setSessionId(sessionId);
        session.setPoiId(request.getPoiId());
        session.setUserId(userId);
        session.setSource(request.getSource());
        session.setInquiryType(request.getInquiryType());
        session.setRelatedOrderId(request.getRelatedOrderId());
        session.setSubject(request.getSubject());
        session.setStatus(SessionStatusEnum.PENDING.getCode());
        session.setPriority("NORMAL");
        session.setUserUnreadCount(0);
        session.setAgentUnreadCount(0);
        session.setRobotHandled(request.getPreferRobot() != null && request.getPreferRobot());
        session.setStartTime(LocalDateTime.now());
        session.setCreateTime(LocalDateTime.now());

        // 处理首次消息
        if (StringUtils.hasText(request.getFirstMessage())) {
            session.setFirstMessagePreview(request.getFirstMessage().substring(0, Math.min(100, request.getFirstMessage().length())));
        }

        sessionMapper.insert(session);

        // 缓存用户会话
        redisTemplate.opsForValue().set(SESSION_USER_KEY + userId + ":" + request.getPoiId(), sessionId, 24, TimeUnit.HOURS);

        // 分配客服(如果不需要优先机器人)
        if (!session.getRobotHandled()) {
            PoiCustomerService agent = assignAgent(request.getPoiId(), request.getInquiryType());
            if (agent != null) {
                session.setAgentId(agent.getId());
                session.setStatus(SessionStatusEnum.ACTIVE.getCode());
                sessionMapper.updateById(session);
                agentMapper.incrementSessionCount(agent.getId());

                // 发送系统消息
                sendSystemMessage(sessionId, "客服 " + agent.getAgentNickname() + " 已接入为您服务");
            }
        } else {
            // 机器人模式,发送欢迎语
            sendSystemMessage(sessionId, "您好!我是智能客服助手,请问有什么可以帮您?");
        }

        return convertToSessionResponse(session);
    }

    @Override
    public PoiCustomerService assignAgent(Long poiId, String inquiryType) {
        // 从Redis获取在线客服列表
        String agentListKey = "cs:poi:agents:" + poiId;
        String agentIdsStr = redisTemplate.opsForValue().get(agentListKey);

        List<Long> agentIds = new ArrayList<>();
        if (StringUtils.hasText(agentIdsStr)) {
            agentIds = Arrays.stream(agentIdsStr.split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
        } else {
            // 从数据库加载
            List<PoiCustomerService> agents = agentMapper.selectOnlineAgentsByPoiId(poiId);
            agentIds = agents.stream().map(PoiCustomerService::getId).collect(Collectors.toList());
        }

        if (agentIds.isEmpty()) {
            return null;
        }

        // 获取客服状态,选择负载最小的
        PoiCustomerService selectedAgent = null;
        int minLoad = Integer.MAX_VALUE;

        for (Long agentId : agentIds) {
            String statusJson = redisTemplate.opsForValue().get(AGENT_STATUS_KEY + agentId);
            if (StringUtils.hasText(statusJson)) {
                CustomerServiceStatus status = JsonUtils.parseObject(statusJson, CustomerServiceStatus.class);
                if (status != null && "ONLINE".equals(status.getStatus())) {
                    int load = status.getCurrentSessions();
                    if (load < status.getMaxSessions() && load < minLoad) {
                        minLoad = load;
                        selectedAgent = agentMapper.selectById(agentId);
                    }
                }
            } else {
                PoiCustomerService agent = agentMapper.selectById(agentId);
                if (agent != null && "ONLINE".equals(agent.getStatus())
                        && agent.getCurrentSessions() < agent.getMaxConcurrentSessions()
                        && agent.getCurrentSessions() < minLoad) {
                    minLoad = agent.getCurrentSessions();
                    selectedAgent = agent;
                }
            }
        }

        return selectedAgent;
    }

    @Override
    @Transactional
    public MessageResponse sendMessage(Long senderId, String senderType, SendMessageRequest request) {
        PoiCustomerServiceSession session = sessionMapper.selectBySessionId(request.getSessionId());
        if (session == null) {
            throw new RuntimeException("会话不存在");
        }

        // 生成消息ID
        String messageId = generateMessageId();
        PoiCustomerServiceMessage message = new PoiCustomerServiceMessage();
        message.setMessageId(messageId);
        message.setSessionId(request.getSessionId());
        message.setSenderId(senderId);
        message.setSenderType(senderType);
        message.setMessageType(request.getMessageType());
        message.setContent(request.getContent());
        message.setContentExtra(request.getContentExtra());
        message.setQuoteMessageId(request.getQuoteMessageId());
        message.setStatus("SENT");
        message.setClientMessageId(request.getClientMessageId());
        message.setCreateTime(LocalDateTime.now());

        // 设置发送者信息
        if ("USER".equals(senderType)) {
            message.setSenderName("用户" + senderId);
            sessionMapper.incrementAgentUnread(request.getSessionId());
        } else if ("AGENT".equals(senderType)) {
            PoiCustomerService agent = agentMapper.selectById(senderId);
            if (agent != null) {
                message.setSenderName(agent.getAgentNickname());
                message.setSenderAvatar(agent.getAgentAvatar());
            }
            sessionMapper.incrementUserUnread(request.getSessionId());
        }

        messageMapper.insert(message);

        // 更新会话最后消息
        session.setLastMessagePreview(request.getContent().substring(0, Math.min(100, request.getContent().length())));
        session.setLastMessageTime(LocalDateTime.now());
        session.setLastMessageSender(senderType);
        sessionMapper.updateById(session);

        // WebSocket推送
        pushMessage(session, message);

        // 如果是用户消息且会话是机器人模式,尝试自动回复
        if ("USER".equals(senderType) && Boolean.TRUE.equals(session.getRobotHandled())) {
            FaqMatchResult robotReply = handleRobotReply(request.getSessionId(), request.getContent());
            if (robotReply.getMatched() && robotReply.getConfidence() > 70) {
                // 机器人自动回复
                SendMessageRequest robotRequest = new SendMessageRequest();
                robotRequest.setSessionId(request.getSessionId());
                robotRequest.setMessageType(MessageTypeEnum.TEXT.getCode());
                robotRequest.setContent(robotReply.getAnswer());
                MessageResponse robotResponse = sendMessage(0L, "ROBOT", robotRequest);
                robotResponse.setRobotSent(true);

                // 如果需要转人工
                if (Boolean.TRUE.equals(robotReply.getNeedTransfer())) {
                    transferToHuman(request.getSessionId(), "ROBOT_CANNOT_ANSWER");
                }

                return convertToMessageResponse(message);
            }
        }

        return convertToMessageResponse(message);
    }

    @Override
    public FaqMatchResult handleRobotReply(String sessionId, String userMessage) {
        PoiCustomerServiceSession session = sessionMapper.selectBySessionId(sessionId);
        if (session == null) {
            return createUnmatchedResult();
        }

        // 获取POI的FAQ列表
        List<PoiCustomerServiceFaq> faqs = faqMapper.selectByPoiIdOrCommon(session.getPoiId());

        // 简单关键词匹配(实际生产环境可用NLP模型)
        PoiCustomerServiceFaq bestMatch = null;
        int maxScore = 0;

        for (PoiCustomerServiceFaq faq : faqs) {
            int score = calculateMatchScore(userMessage, faq);
            if (score > maxScore) {
                maxScore = score;
                bestMatch = faq;
            }
        }

        FaqMatchResult result = new FaqMatchResult();
        if (bestMatch != null && maxScore >= 30) {
            result.setMatched(true);
            result.setFaqId(bestMatch.getId());
            result.setQuestion(bestMatch.getQuestion());
            result.setAnswer(bestMatch.getAnswer());
            result.setConfidence(Math.min(100, maxScore));
            result.setNeedTransfer(bestMatch.getNeedTransfer());
            result.setTransferHint(bestMatch.getTransferHint());

            // 更新FAQ命中次数
            faqMapper.incrementHitCount(bestMatch.getId());
        } else {
            result.setMatched(false);
            result.setConfidence(0);
            result.setNeedTransfer(true);
            result.setTransferHint("抱歉,我可能没理解您的问题,为您转接人工客服...");
        }

        return result;
    }

    @Override
    @Transactional
    public boolean transferToHuman(String sessionId, String reason) {
        PoiCustomerServiceSession session = sessionMapper.selectBySessionId(sessionId);
        if (session == null) {
            return false;
        }

        // 分配人工客服
        PoiCustomerService agent = assignAgent(session.getPoiId(), session.getInquiryType());
        if (agent == null) {
            sendSystemMessage(sessionId, "当前人工客服繁忙,请稍后再试");
            return false;
        }

        session.setAgentId(agent.getId());
        session.setStatus(SessionStatusEnum.ACTIVE.getCode());
        session.setRobotHandled(false);
        session.setRobotTransferred(true);
        sessionMapper.updateById(session);

        agentMapper.incrementSessionCount(agent.getId());

        // 发送转接通知
        sendSystemMessage(sessionId, "已为您转接人工客服 " + agent.getAgentNickname());

        return true;
    }

    @Override
    public List<SessionResponse> getUserSessions(Long userId) {
        List<PoiCustomerServiceSession> sessions = sessionMapper.selectByUserId(userId);
        return sessions.stream().map(this::convertToSessionResponse).collect(Collectors.toList());
    }

    @Override
    public List<SessionResponse> getAgentSessions(Long agentId) {
        List<PoiCustomerServiceSession> sessions = sessionMapper.selectActiveByAgentId(agentId);
        return sessions.stream().map(this::convertToSessionResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean closeSession(String sessionId, String closeReason) {
        PoiCustomerServiceSession session = sessionMapper.selectBySessionId(sessionId);
        if (session == null) {
            return false;
        }

        session.setStatus(SessionStatusEnum.CLOSED.getCode());
        session.setCloseReason(closeReason);
        session.setEndTime(LocalDateTime.now());

        // 计算会话时长
        if (session.getStartTime() != null) {
            session.setDuration((int) java.time.Duration.between(session.getStartTime(), session.getEndTime()).getSeconds());
        }

        sessionMapper.updateById(session);

        // 减少客服会话数
        if (session.getAgentId() != null) {
            agentMapper.decrementSessionCount(session.getAgentId());
        }

        // 清除缓存
        redisTemplate.delete(SESSION_USER_KEY + session.getUserId() + ":" + session.getPoiId());

        // 发送系统消息
        sendSystemMessage(sessionId, "会话已结束");

        return true;
    }

    @Override
    public List<MessageResponse> getSessionMessages(String sessionId, Integer page, Integer size) {
        List<PoiCustomerServiceMessage> messages = messageMapper.selectBySessionId(sessionId);
        return messages.stream().map(this::convertToMessageResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean markMessagesAsRead(String sessionId, String readerType) {
        if ("USER".equals(readerType)) {
            sessionMapper.clearUserUnread(sessionId);
        } else if ("AGENT".equals(readerType)) {
            sessionMapper.clearAgentUnread(sessionId);
        }

        String senderType = "USER".equals(readerType) ? "AGENT" : "USER";
        messageMapper.markAsRead(sessionId, senderType);

        return true;
    }

    @Override
    @Transactional
    public boolean recallMessage(String messageId, Long operatorId) {
        PoiCustomerServiceMessage message = messageMapper.selectByMessageId(messageId);
        if (message == null) {
            return false;
        }

        // 检查是否是发送者本人撤回(2分钟内)
        if (!message.getSenderId().equals(operatorId)) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        if (java.time.Duration.between(message.getCreateTime(), now).getSeconds() > 120) {
            return false;
        }

        messageMapper.recallMessage(messageId);

        // 推送撤回通知
        pushRecallNotification(message);

        return true;
    }

    @Override
    @Transactional
    public boolean rateSession(String sessionId, Integer rating, String comment) {
        PoiCustomerServiceSession session = sessionMapper.selectBySessionId(sessionId);
        if (session == null) {
            return false;
        }

        session.setUserRating(rating);
        session.setUserComment(comment);
        sessionMapper.updateById(session);

        // 更新客服评分
        if (session.getAgentId() != null) {
            PoiCustomerService agent = agentMapper.selectById(session.getAgentId());
            if (agent != null) {
                int newCount = agent.getServiceCount() + 1;
                double newRating = (agent.getRating() * agent.getServiceCount() + rating) / newCount;
                agent.setServiceCount(newCount);
                agent.setRating(newRating);
                agentMapper.updateById(agent);
            }
        }

        return true;
    }

    @Override
    public List<PoiCustomerService> getPoiAgents(Long poiId) {
        return agentMapper.selectAgentsByPoiId(poiId);
    }

    @Override
    public boolean updateAgentStatus(Long agentId, String status) {
        PoiCustomerService agent = agentMapper.selectById(agentId);
        if (agent == null) {
            return false;
        }

        agent.setStatus(status);
        if ("ONLINE".equals(status)) {
            agent.setLastOnlineTime(LocalDateTime.now());
        }
        agentMapper.updateById(agent);

        // 更新Redis缓存
        CustomerServiceStatus csStatus = new CustomerServiceStatus();
        csStatus.setAgentId(agentId);
        csStatus.setPoiId(agent.getPoiId());
        csStatus.setStatus(status);
        csStatus.setCurrentSessions(agent.getCurrentSessions());
        csStatus.setMaxSessions(agent.getMaxConcurrentSessions());
        csStatus.setLastActiveTime(LocalDateTime.now());
        csStatus.setSkillTags(agent.getSkillTags());

        redisTemplate.opsForValue().set(AGENT_STATUS_KEY + agentId, JsonUtils.toJsonString(csStatus), 5, TimeUnit.MINUTES);

        return true;
    }

    @Override
    public boolean agentHeartbeat(Long agentId) {
        String statusJson = redisTemplate.opsForValue().get(AGENT_STATUS_KEY + agentId);
        if (StringUtils.hasText(statusJson)) {
            CustomerServiceStatus status = JsonUtils.parseObject(statusJson, CustomerServiceStatus.class);
            if (status != null) {
                status.setLastHeartbeat(LocalDateTime.now());
                redisTemplate.opsForValue().set(AGENT_STATUS_KEY + agentId, JsonUtils.toJsonString(status), 5, TimeUnit.MINUTES);
            }
        }
        return true;
    }

    // ==================== 私有方法 ====================

    private String generateSessionId() {
        return "CS" + idGenerator.nextId();
    }

    private String generateMessageId() {
        return "MSG" + idGenerator.nextId();
    }

    private void sendSystemMessage(String sessionId, String content) {
        PoiCustomerServiceMessage message = new PoiCustomerServiceMessage();
        message.setMessageId(generateMessageId());
        message.setSessionId(sessionId);
        message.setSenderId(0L);
        message.setSenderType("SYSTEM");
        message.setSenderName("系统");
        message.setMessageType(MessageTypeEnum.SYSTEM.getCode());
        message.setContent(content);
        message.setStatus("SENT");
        message.setCreateTime(LocalDateTime.now());
        messageMapper.insert(message);

        // WebSocket推送
        PoiCustomerServiceSession session = sessionMapper.selectBySessionId(sessionId);
        if (session != null) {
            pushMessage(session, message);
        }
    }

    private void pushMessage(PoiCustomerServiceSession session, PoiCustomerServiceMessage message) {
        MessageResponse response = convertToMessageResponse(message);

        // 推送给用户
        if (session.getUserId() != null) {
            webSocketService.sendToUser(session.getUserId(), "cs:message", response);
        }

        // 推送给客服
        if (session.getAgentId() != null) {
            webSocketService.sendToUser(session.getAgentId(), "cs:message", response);
        }
    }

    private void pushRecallNotification(PoiCustomerServiceMessage message) {
        Map<String, Object> recallData = new HashMap<>();
        recallData.put("messageId", message.getMessageId());
        recallData.put("sessionId", message.getSessionId());

        PoiCustomerServiceSession session = sessionMapper.selectBySessionId(message.getSessionId());
        if (session != null) {
            webSocketService.sendToUser(session.getUserId(), "cs:message:recall", recallData);
            if (session.getAgentId() != null) {
                webSocketService.sendToUser(session.getAgentId(), "cs:message:recall", recallData);
            }
        }
    }

    private int calculateMatchScore(String userMessage, PoiCustomerServiceFaq faq) {
        int score = 0;
        String lowerMessage = userMessage.toLowerCase();
        String lowerQuestion = faq.getQuestion().toLowerCase();

        // 完全匹配
        if (lowerMessage.equals(lowerQuestion)) {
            score += 100;
        }

        // 包含匹配
        if (lowerMessage.contains(lowerQuestion) || lowerQuestion.contains(lowerMessage)) {
            score += 50;
        }

        // 关键词匹配
        if (StringUtils.hasText(faq.getKeywords())) {
            String[] keywords = faq.getKeywords().split(",");
            for (String keyword : keywords) {
                if (lowerMessage.contains(keyword.trim().toLowerCase())) {
                    score += 20;
                }
            }
        }

        return Math.min(100, score);
    }

    private FaqMatchResult createUnmatchedResult() {
        FaqMatchResult result = new FaqMatchResult();
        result.setMatched(false);
        result.setConfidence(0);
        result.setNeedTransfer(true);
        result.setTransferHint("抱歉,我可能没理解您的问题,为您转接人工客服...");
        return result;
    }

    private SessionResponse convertToSessionResponse(PoiCustomerServiceSession session) {
        SessionResponse response = new SessionResponse();
        response.setSessionId(session.getSessionId());
        response.setPoiId(session.getPoiId());
        response.setUserId(session.getUserId());
        response.setAgentId(session.getAgentId());
        response.setStatus(session.getStatus());
        response.setInquiryType(session.getInquiryType());
        response.setSubject(session.getSubject());
        response.setFirstMessagePreview(session.getFirstMessagePreview());
        response.setLastMessagePreview(session.getLastMessagePreview());
        response.setUserUnreadCount(session.getUserUnreadCount());
        response.setRobotHandled(session.getRobotHandled());

        if (session.getLastMessageTime() != null) {
            response.setLastMessageTime(session.getLastMessageTime().format(DATE_FORMATTER));
        }
        if (session.getCreateTime() != null) {
            response.setCreateTime(session.getCreateTime().format(DATE_FORMATTER));
        }

        // 获取客服信息
        if (session.getAgentId() != null) {
            PoiCustomerService agent = agentMapper.selectById(session.getAgentId());
            if (agent != null) {
                response.setMerchantName(agent.getMerchantName());
                response.setAgentNickname(agent.getAgentNickname());
                response.setAgentAvatar(agent.getAgentAvatar());
            }
        }

        return response;
    }

    private MessageResponse convertToMessageResponse(PoiCustomerServiceMessage message) {
        MessageResponse response = new MessageResponse();
        response.setMessageId(message.getMessageId());
        response.setSessionId(message.getSessionId());
        response.setSenderId(message.getSenderId());
        response.setSenderType(message.getSenderType());
        response.setSenderName(message.getSenderName());
        response.setSenderAvatar(message.getSenderAvatar());
        response.setMessageType(message.getMessageType());
        response.setContent(message.getContent());
        response.setContentExtra(message.getContentExtra());
        response.setQuoteMessageId(message.getQuoteMessageId());
        response.setStatus(message.getStatus());
        response.setRobotSent(message.getRobotSent());
        response.setRead(message.getRead());
        response.setRecalled(message.getRecalled());

        if (message.getReadTime() != null) {
            response.setReadTime(message.getReadTime().format(DATE_FORMATTER));
        }
        if (message.getCreateTime() != null) {
            response.setCreateTime(message.getCreateTime().format(DATE_FORMATTER));
        }

        return response;
    }
}
