package com.im.service.multimodal;

import com.im.entity.multimodal.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 多模态AI助手服务
 * 提供AI助手的完整生命周期管理
 */
@Slf4j
@Service
public class MultimodalAIAssistantService {

    private final Map<String, MultimodalAIAssistant> assistants = new ConcurrentHashMap<>();
    private final Map<String, MultimodalConversation> conversations = new ConcurrentHashMap<>();
    private final Map<String, List<MultimodalMessage>> conversationMessages = new ConcurrentHashMap<>();
    private final Map<String, SseEmitter> activeStreams = new ConcurrentHashMap<>();
    
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    private final AtomicLong assistantCounter = new AtomicLong(0);
    private final AtomicLong conversationCounter = new AtomicLong(0);
    private final AtomicLong messageCounter = new AtomicLong(0);

    /**
     * 创建AI助手
     */
    public MultimodalAIAssistant createAssistant(MultimodalAIAssistant assistant) {
        String assistantId = "AST-" + System.currentTimeMillis() + "-" + assistantCounter.incrementAndGet();
        assistant.setAssistantId(assistantId);
        assistant.setCreateTime(LocalDateTime.now());
        assistant.setUpdateTime(LocalDateTime.now());
        assistant.setEnabled(true);
        assistant.setOnline(false);
        assistant.setTotalConversations(0L);
        assistant.setTotalMessages(0L);
        assistant.setRating(5.0);
        
        assistants.put(assistantId, assistant);
        log.info("Created AI assistant: {} ({})", assistant.getName(), assistantId);
        return assistant;
    }

    /**
     * 获取助手
     */
    public Optional<MultimodalAIAssistant> getAssistant(String assistantId) {
        return Optional.ofNullable(assistants.get(assistantId));
    }

    /**
     * 获取所有助手
     */
    public List<MultimodalAIAssistant> getAllAssistants() {
        return new ArrayList<>(assistants.values());
    }

    /**
     * 获取用户可访问的助手
     */
    public List<MultimodalAIAssistant> getAccessibleAssistants(Long userId) {
        return assistants.values().stream()
            .filter(a -> a.getEnabled() && 
                (a.getVisibility() == MultimodalAIAssistant.Visibility.PUBLIC ||
                 a.getCreatorId().equals(userId)))
            .sorted(Comparator.comparing(MultimodalAIAssistant::getRating).reversed())
            .collect(Collectors.toList());
    }

    /**
     * 更新助手
     */
    public MultimodalAIAssistant updateAssistant(String assistantId, MultimodalAIAssistant updates) {
        MultimodalAIAssistant assistant = assistants.get(assistantId);
        if (assistant == null) {
            throw new RuntimeException("Assistant not found: " + assistantId);
        }
        
        if (updates.getName() != null) assistant.setName(updates.getName());
        if (updates.getDescription() != null) assistant.setDescription(updates.getDescription());
        if (updates.getSystemPrompt() != null) assistant.setSystemPrompt(updates.getSystemPrompt());
        if (updates.getTemperature() != null) assistant.setTemperature(updates.getTemperature());
        if (updates.getMaxTokens() != null) assistant.setMaxTokens(updates.getMaxTokens());
        if (updates.getSupportedModalities() != null) assistant.setSupportedModalities(updates.getSupportedModalities());
        if (updates.getCustomConfig() != null) assistant.setCustomConfig(updates.getCustomConfig());
        
        assistant.setUpdateTime(LocalDateTime.now());
        log.info("Updated AI assistant: {}", assistantId);
        return assistant;
    }

    /**
     * 删除助手
     */
    public void deleteAssistant(String assistantId) {
        assistants.remove(assistantId);
        log.info("Deleted AI assistant: {}", assistantId);
    }

    /**
     * 设置助手在线状态
     */
    public void setAssistantOnline(String assistantId, boolean online) {
        MultimodalAIAssistant assistant = assistants.get(assistantId);
        if (assistant != null) {
            assistant.setOnline(online);
            assistant.setUpdateTime(LocalDateTime.now());
            if (online) {
                assistant.updateActivityTime();
            }
            log.info("Assistant {} is now {}", assistantId, online ? "ONLINE" : "OFFLINE");
        }
    }

    /**
     * 创建对话
     */
    public MultimodalConversation createConversation(Long userId, String assistantId, String title) {
        MultimodalAIAssistant assistant = assistants.get(assistantId);
        if (assistant == null || !assistant.getEnabled()) {
            throw new RuntimeException("Assistant not found or disabled: " + assistantId);
        }

        String conversationId = "CONV-" + System.currentTimeMillis() + "-" + conversationCounter.incrementAndGet();
        MultimodalConversation conversation = new MultimodalConversation();
        conversation.setConversationId(conversationId);
        conversation.setUserId(userId);
        conversation.setAssistantId(assistantId);
        conversation.setTitle(title != null ? title : "New Conversation");
        conversation.setStatus(MultimodalConversation.ConversationStatus.ACTIVE);
        conversation.setMessageCount(0);
        conversation.setTotalTokens(0L);
        conversation.setContextLimit(assistant.getContextWindow() != null ? assistant.getContextWindow() : 20);
        conversation.setCreateTime(LocalDateTime.now());
        conversation.setLastActivityTime(LocalDateTime.now());
        conversation.setCurrentModel(assistant.getModelName());
        conversation.setStarred(false);
        conversation.setPinned(false);
        conversation.setSystemPrompt(assistant.getSystemPrompt());

        conversations.put(conversationId, conversation);
        conversationMessages.put(conversationId, new ArrayList<>());
        
        assistant.incrementConversations();
        assistant.updateActivityTime();
        
        log.info("Created conversation: {} for user {}", conversationId, userId);
        return conversation;
    }

    /**
     * 获取对话
     */
    public Optional<MultimodalConversation> getConversation(String conversationId) {
        return Optional.ofNullable(conversations.get(conversationId));
    }

    /**
     * 获取用户的所有对话
     */
    public List<MultimodalConversation> getUserConversations(Long userId) {
        return conversations.values().stream()
            .filter(c -> c.getUserId().equals(userId))
            .filter(c -> c.getStatus() != MultimodalConversation.ConversationStatus.DELETED)
            .sorted(Comparator.comparing(MultimodalConversation::getLastActivityTime).reversed())
            .collect(Collectors.toList());
    }

    /**
     * 获取对话消息
     */
    public List<MultimodalMessage> getConversationMessages(String conversationId, int limit) {
        List<MultimodalMessage> messages = conversationMessages.getOrDefault(conversationId, new ArrayList<>());
        if (messages.size() <= limit) {
            return new ArrayList<>(messages);
        }
        return new ArrayList<>(messages.subList(messages.size() - limit, messages.size()));
    }

    /**
     * 发送消息 (非流式)
     */
    public MultimodalMessage sendMessage(String conversationId, String content, 
            MultimodalAIAssistant.ModalityType modality, List<MessageAttachment> attachments) {
        MultimodalConversation conversation = conversations.get(conversationId);
        if (conversation == null) {
            throw new RuntimeException("Conversation not found: " + conversationId);
        }

        // 创建用户消息
        MultimodalMessage userMessage = createMessage(conversationId, content, 
            MultimodalMessage.MessageRole.user, modality, attachments);
        
        conversation.incrementMessageCount();
        conversation.updateActivityTime();

        // 模拟AI响应
        MultimodalMessage aiMessage = generateAIResponse(conversation, userMessage);
        
        MultimodalAIAssistant assistant = assistants.get(conversation.getAssistantId());
        if (assistant != null) {
            assistant.incrementMessages();
            assistant.updateActivityTime();
        }

        return aiMessage;
    }

    /**
     * 发送消息 (流式)
     */
    public SseEmitter sendMessageStream(String conversationId, String content) {
        MultimodalConversation conversation = conversations.get(conversationId);
        if (conversation == null) {
            throw new RuntimeException("Conversation not found: " + conversationId);
        }

        SseEmitter emitter = new SseEmitter(300000L); // 5分钟超时
        activeStreams.put(conversationId, emitter);

        executorService.submit(() -> {
            try {
                // 模拟流式响应
                String[] chunks = generateResponseChunks(content);
                for (String chunk : chunks) {
                    emitter.send(SseEmitter.event()
                        .name("message")
                        .data(chunk));
                    Thread.sleep(100); // 模拟延迟
                }
                emitter.complete();
            } catch (IOException | InterruptedException e) {
                log.error("Stream error", e);
                emitter.completeWithError(e);
            } finally {
                activeStreams.remove(conversationId);
            }
        });

        return emitter;
    }

    /**
     * 创建消息
     */
    private MultimodalMessage createMessage(String conversationId, String content,
            MultimodalMessage.MessageRole role, MultimodalAIAssistant.ModalityType modality,
            List<MessageAttachment> attachments) {
        String messageId = "MSG-" + System.currentTimeMillis() + "-" + messageCounter.incrementAndGet();
        
        MultimodalMessage message = new MultimodalMessage();
        message.setMessageId(messageId);
        message.setConversationId(conversationId);
        message.setRole(role);
        message.setContent(content);
        message.setModalityType(modality != null ? modality : MultimodalAIAssistant.ModalityType.TEXT);
        message.setAttachments(attachments);
        message.setStatus(MultimodalMessage.MessageStatus.COMPLETED);
        message.setCreateTime(LocalDateTime.now());
        message.setProcessingProgress(100);
        
        if (attachments != null && !attachments.isEmpty()) {
            message.setTokenUsage(new MultimodalMessage.TokenUsage(
                content.length() + attachments.size() * 500, 200));
        } else {
            message.setTokenUsage(new MultimodalMessage.TokenUsage(content.length(), 200));
        }
        
        conversationMessages.computeIfAbsent(conversationId, k -> new ArrayList<>()).add(message);
        return message;
    }

    /**
     * 生成AI响应
     */
    private MultimodalMessage generateAIResponse(MultimodalConversation conversation, 
            MultimodalMessage userMessage) {
        String response = generateMockResponse(userMessage.getContent());
        
        MultimodalMessage aiMessage = createMessage(conversation.getConversationId(),
            response, MultimodalMessage.MessageRole.assistant, 
            MultimodalAIAssistant.ModalityType.TEXT, null);
        
        conversation.incrementMessageCount();
        
        long tokens = response.length() + userMessage.getContent().length();
        conversation.setTotalTokens(conversation.getTotalTokens() + tokens);
        
        return aiMessage;
    }

    /**
     * 模拟响应生成
     */
    private String generateMockResponse(String input) {
        if (input.toLowerCase().contains("hello") || input.toLowerCase().contains("hi")) {
            return "Hello! I'm your multimodal AI assistant. How can I help you today?";
        } else if (input.toLowerCase().contains("image")) {
            return "I can help you with image generation and analysis. What kind of image are you looking for?";
        } else if (input.toLowerCase().contains("code")) {
            return "I can assist with coding tasks. What programming language are you working with?";
        } else {
            return "I understand you're asking about: \"" + input + "\". Let me help you with that. " +
                   "As a multimodal AI assistant, I can process text, images, audio, and video to provide comprehensive assistance.";
        }
    }

    /**
     * 生成响应分块
     */
    private String[] generateResponseChunks(String input) {
        String response = generateMockResponse(input);
        List<String> chunks = new ArrayList<>();
        int chunkSize = 10;
        for (int i = 0; i < response.length(); i += chunkSize) {
            chunks.add(response.substring(i, Math.min(i + chunkSize, response.length())));
        }
        return chunks.toArray(new String[0]);
    }

    /**
     * 删除对话
     */
    public void deleteConversation(String conversationId) {
        MultimodalConversation conversation = conversations.get(conversationId);
        if (conversation != null) {
            conversation.delete();
            log.info("Deleted conversation: {}", conversationId);
        }
    }

    /**
     * 归档对话
     */
    public void archiveConversation(String conversationId) {
        MultimodalConversation conversation = conversations.get(conversationId);
        if (conversation != null) {
            conversation.archive();
            log.info("Archived conversation: {}", conversationId);
        }
    }

    /**
     * 获取助手统计
     */
    public Map<String, Object> getAssistantStats(String assistantId) {
        MultimodalAIAssistant assistant = assistants.get(assistantId);
        if (assistant == null) {
            return Collections.emptyMap();
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("assistantId", assistantId);
        stats.put("name", assistant.getName());
        stats.put("totalConversations", assistant.getTotalConversations());
        stats.put("totalMessages", assistant.getTotalMessages());
        stats.put("averageResponseTime", assistant.getAvgResponseTime());
        stats.put("rating", assistant.getRating());
        stats.put("online", assistant.getOnline());
        
        long activeConversations = conversations.values().stream()
            .filter(c -> c.getAssistantId().equals(assistantId))
            .filter(c -> c.getStatus() == MultimodalConversation.ConversationStatus.ACTIVE)
            .count();
        stats.put("activeConversations", activeConversations);
        
        return stats;
    }
}
