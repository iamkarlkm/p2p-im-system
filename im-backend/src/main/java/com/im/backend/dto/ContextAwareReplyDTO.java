package com.im.backend.dto;

import com.im.backend.entity.ContextAwareReplyEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 上下文感知智能回复生成器数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContextAwareReplyDTO {
    
    private Long id;
    private String userId;
    private String sessionId;
    private String triggerMessageId;
    private String triggerMessageContent;
    private String contextSummary;
    private String detectedIntent;
    private Double intentConfidence;
    private List<String> replyCandidates;
    private String selectedReply;
    private List<String> recommendedEmojis;
    private String languageStyle;
    private String replyLength;
    private String sensitivityCheckResult;
    private Boolean sensitivityPassed;
    private Map<String, Object> personalizationFeatures;
    private Integer userFeedbackScore;
    private String userFeedbackComment;
    private Boolean used;
    private Long generationTimeMs;
    private String modelVersion;
    private Map<String, Object> generationOptions;
    private String status;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiresAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    private String indexKey;
    
    /**
     * 从实体创建DTO
     */
    public static ContextAwareReplyDTO fromEntity(ContextAwareReplyEntity entity) {
        ContextAwareReplyDTO dto = new ContextAwareReplyDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setSessionId(entity.getSessionId());
        dto.setTriggerMessageId(entity.getTriggerMessageId());
        dto.setTriggerMessageContent(entity.getTriggerMessageContent());
        dto.setContextSummary(entity.getContextSummary());
        dto.setDetectedIntent(entity.getDetectedIntent());
        dto.setIntentConfidence(entity.getIntentConfidence());
        dto.setSelectedReply(entity.getSelectedReply());
        dto.setLanguageStyle(entity.getLanguageStyle());
        dto.setReplyLength(entity.getReplyLength());
        dto.setSensitivityCheckResult(entity.getSensitivityCheckResult());
        dto.setSensitivityPassed(entity.getSensitivityPassed());
        dto.setUserFeedbackScore(entity.getUserFeedbackScore());
        dto.setUserFeedbackComment(entity.getUserFeedbackComment());
        dto.setUsed(entity.getUsed());
        dto.setGenerationTimeMs(entity.getGenerationTimeMs());
        dto.setModelVersion(entity.getModelVersion());
        dto.setStatus(entity.getStatus());
        dto.setExpiresAt(entity.getExpiresAt());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setIndexKey(entity.getIndexKey());
        
        return dto;
    }
    
    /**
     * 转换到实体
     */
    public ContextAwareReplyEntity toEntity() {
        ContextAwareReplyEntity entity = new ContextAwareReplyEntity();
        entity.setId(this.id);
        entity.setUserId(this.userId);
        entity.setSessionId(this.sessionId);
        entity.setTriggerMessageId(this.triggerMessageId);
        entity.setTriggerMessageContent(this.triggerMessageContent);
        entity.setContextSummary(this.contextSummary);
        entity.setDetectedIntent(this.detectedIntent);
        entity.setIntentConfidence(this.intentConfidence);
        entity.setSelectedReply(this.selectedReply);
        entity.setLanguageStyle(this.languageStyle);
        entity.setReplyLength(this.replyLength);
        entity.setSensitivityCheckResult(this.sensitivityCheckResult);
        entity.setSensitivityPassed(this.sensitivityPassed);
        entity.setUserFeedbackScore(this.userFeedbackScore);
        entity.setUserFeedbackComment(this.userFeedbackComment);
        entity.setUsed(this.used);
        entity.setGenerationTimeMs(this.generationTimeMs);
        entity.setModelVersion(this.modelVersion);
        entity.setStatus(this.status);
        entity.setExpiresAt(this.expiresAt);
        entity.setIndexKey(this.indexKey);
        
        return entity;
    }
    
    /**
     * 构建生成请求
     */
    public static class GenerateRequest {
        private String userId;
        private String sessionId;
        private String triggerMessageContent;
        private Map<String, Object> context;
        private String languageStyle;
        private String replyLength;
        private Map<String, Object> generationOptions;
        
        // getters and setters
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        
        public String getTriggerMessageContent() { return triggerMessageContent; }
        public void setTriggerMessageContent(String triggerMessageContent) { this.triggerMessageContent = triggerMessageContent; }
        
        public Map<String, Object> getContext() { return context; }
        public void setContext(Map<String, Object> context) { this.context = context; }
        
        public String getLanguageStyle() { return languageStyle; }
        public void setLanguageStyle(String languageStyle) { this.languageStyle = languageStyle; }
        
        public String getReplyLength() { return replyLength; }
        public void setReplyLength(String replyLength) { this.replyLength = replyLength; }
        
        public Map<String, Object> getGenerationOptions() { return generationOptions; }
        public void setGenerationOptions(Map<String, Object> generationOptions) { this.generationOptions = generationOptions; }
    }
    
    /**
     * 生成响应
     */
    public static class GenerateResponse {
        private ContextAwareReplyDTO reply;
        private List<String> candidates;
        private List<String> recommendedEmojis;
        private Boolean success;
        private String message;
        private Long generationTimeMs;
        
        // getters and setters
        public ContextAwareReplyDTO getReply() { return reply; }
        public void setReply(ContextAwareReplyDTO reply) { this.reply = reply; }
        
        public List<String> getCandidates() { return candidates; }
        public void setCandidates(List<String> candidates) { this.candidates = candidates; }
        
        public List<String> getRecommendedEmojis() { return recommendedEmojis; }
        public void setRecommendedEmojis(List<String> recommendedEmojis) { this.recommendedEmojis = recommendedEmojis; }
        
        public Boolean getSuccess() { return success; }
        public void setSuccess(Boolean success) { this.success = success; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public Long getGenerationTimeMs() { return generationTimeMs; }
        public void setGenerationTimeMs(Long generationTimeMs) { this.generationTimeMs = generationTimeMs; }
        
        public static GenerateResponse success(ContextAwareReplyDTO reply, List<String> candidates, List<String> emojis, Long timeMs) {
            GenerateResponse response = new GenerateResponse();
            response.setReply(reply);
            response.setCandidates(candidates);
            response.setRecommendedEmojis(emojis);
            response.setSuccess(true);
            response.setMessage("生成成功");
            response.setGenerationTimeMs(timeMs);
            return response;
        }
        
        public static GenerateResponse error(String message) {
            GenerateResponse response = new GenerateResponse();
            response.setSuccess(false);
            response.setMessage(message);
            return response;
        }
    }
    
    /**
     * 反馈请求
     */
    public static class FeedbackRequest {
        private Integer score; // 1-5
        private String comment;
        
        // getters and setters
        public Integer getScore() { return score; }
        public void setScore(Integer score) { this.score = score; }
        
        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }
        
        public boolean isValid() {
            return score != null && score >= 1 && score <= 5;
        }
    }
    
    /**
     * 搜索请求
     */
    public static class SearchRequest {
        private String keyword;
        private String userId;
        private String sessionId;
        private String intent;
        private String languageStyle;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private String status;
        private Boolean used;
        private Integer minFeedbackScore;
        private Integer page;
        private Integer size;
        
        // getters and setters
        public String getKeyword() { return keyword; }
        public void setKeyword(String keyword) { this.keyword = keyword; }
        
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        
        public String getIntent() { return intent; }
        public void setIntent(String intent) { this.intent = intent; }
        
        public String getLanguageStyle() { return languageStyle; }
        public void setLanguageStyle(String languageStyle) { this.languageStyle = languageStyle; }
        
        public LocalDateTime getStartDate() { return startDate; }
        public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
        
        public LocalDateTime getEndDate() { return endDate; }
        public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public Boolean getUsed() { return used; }
        public void setUsed(Boolean used) { this.used = used; }
        
        public Integer getMinFeedbackScore() { return minFeedbackScore; }
        public void setMinFeedbackScore(Integer minFeedbackScore) { this.minFeedbackScore = minFeedbackScore; }
        
        public Integer getPage() { return page; }
        public void setPage(Integer page) { this.page = page; }
        
        public Integer getSize() { return size; }
        public void setSize(Integer size) { this.size = size; }
        
        public boolean hasKeyword() { return keyword != null && !keyword.trim().isEmpty(); }
        public boolean hasUserId() { return userId != null && !userId.trim().isEmpty(); }
        public boolean hasSessionId() { return sessionId != null && !sessionId.trim().isEmpty(); }
        public boolean hasIntent() { return intent != null && !intent.trim().isEmpty(); }
        public boolean hasLanguageStyle() { return languageStyle != null && !languageStyle.trim().isEmpty(); }
        public boolean hasDateRange() { return startDate != null && endDate != null; }
        public boolean hasStatus() { return status != null && !status.trim().isEmpty(); }
        public boolean hasMinFeedbackScore() { return minFeedbackScore != null && minFeedbackScore >= 1 && minFeedbackScore <= 5; }
        
        public Integer getPageOrDefault() { return page != null ? page : 0; }
        public Integer getSizeOrDefault() { return size != null ? size : 20; }
    }
    
    /**
     * 统计响应
     */
    public static class StatisticsResponse {
        private Long totalReplies;
        private Long totalUsers;
        private Long usedReplies;
        private Long highQualityReplies;
        private Double averageFeedbackScore;
        private Double averageGenerationTimeMs;
        private Map<String, Long> intentDistribution;
        private Map<String, Long> languageStyleDistribution;
        private Map<String, Long> statusDistribution;
        
        // getters and setters
        public Long getTotalReplies() { return totalReplies; }
        public void setTotalReplies(Long totalReplies) { this.totalReplies = totalReplies; }
        
        public Long getTotalUsers() { return totalUsers; }
        public void setTotalUsers(Long totalUsers) { this.totalUsers = totalUsers; }
        
        public Long getUsedReplies() { return usedReplies; }
        public void setUsedReplies(Long usedReplies) { this.usedReplies = usedReplies; }
        
        public Long getHighQualityReplies() { return highQualityReplies; }
        public void setHighQualityReplies(Long highQualityReplies) { this.highQualityReplies = highQualityReplies; }
        
        public Double getAverageFeedbackScore() { return averageFeedbackScore; }
        public void setAverageFeedbackScore(Double averageFeedbackScore) { this.averageFeedbackScore = averageFeedbackScore; }
        
        public Double getAverageGenerationTimeMs() { return averageGenerationTimeMs; }
        public void setAverageGenerationTimeMs(Double averageGenerationTimeMs) { this.averageGenerationTimeMs = averageGenerationTimeMs; }
        
        public Map<String, Long> getIntentDistribution() { return intentDistribution; }
        public void setIntentDistribution(Map<String, Long> intentDistribution) { this.intentDistribution = intentDistribution; }
        
        public Map<String, Long> getLanguageStyleDistribution() { return languageStyleDistribution; }
        public void setLanguageStyleDistribution(Map<String, Long> languageStyleDistribution) { this.languageStyleDistribution = languageStyleDistribution; }
        
        public Map<String, Long> getStatusDistribution() { return statusDistribution; }
        public void setStatusDistribution(Map<String, Long> statusDistribution) { this.statusDistribution = statusDistribution; }
    }
    
    /**
     * 分页响应
     */
    public static class PageResponse<T> {
        private List<T> content;
        private Integer page;
        private Integer size;
        private Long totalElements;
        private Integer totalPages;
        private Boolean last;
        
        // getters and setters
        public List<T> getContent() { return content; }
        public void setContent(List<T> content) { this.content = content; }
        
        public Integer getPage() { return page; }
        public void setPage(Integer page) { this.page = page; }
        
        public Integer getSize() { return size; }
        public void setSize(Integer size) { this.size = size; }
        
        public Long getTotalElements() { return totalElements; }
        public void setTotalElements(Long totalElements) { this.totalElements = totalElements; }
        
        public Integer getTotalPages() { return totalPages; }
        public void setTotalPages(Integer totalPages) { this.totalPages = totalPages; }
        
        public Boolean getLast() { return last; }
        public void setLast(Boolean last) { this.last = last; }
    }
    
    /**
     * 健康检查响应
     */
    public static class HealthResponse {
        private String status;
        private LocalDateTime timestamp;
        private Long totalReplies;
        private Long usedReplies;
        private Long highQualityReplies;
        private Double averageFeedbackScore;
        private Double averageGenerationTimeMs;
        private String version;
        private Map<String, Object> details;
        
        // getters and setters
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        
        public Long getTotalReplies() { return totalReplies; }
        public void setTotalReplies(Long totalReplies) { this.totalReplies = totalReplies; }
        
        public Long getUsedReplies() { return usedReplies; }
        public void setUsedReplies(Long usedReplies) { this.usedReplies = usedReplies; }
        
        public Long getHighQualityReplies() { return highQualityReplies; }
        public void setHighQualityReplies(Long highQualityReplies) { this.highQualityReplies = highQualityReplies; }
        
        public Double getAverageFeedbackScore() { return averageFeedbackScore; }
        public void setAverageFeedbackScore(Double averageFeedbackScore) { this.averageFeedbackScore = averageFeedbackScore; }
        
        public Double getAverageGenerationTimeMs() { return averageGenerationTimeMs; }
        public void setAverageGenerationTimeMs(Double averageGenerationTimeMs) { this.averageGenerationTimeMs = averageGenerationTimeMs; }
        
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        
        public Map<String, Object> getDetails() { return details; }
        public void setDetails(Map<String, Object> details) { this.details = details; }
        
        public static HealthResponse healthy(Long totalReplies, Long usedReplies, Long highQualityReplies, 
                                             Double avgScore, Double avgTime, String version) {
            HealthResponse response = new HealthResponse();
            response.setStatus("UP");
            response.setTimestamp(LocalDateTime.now());
            response.setTotalReplies(totalReplies);
            response.setUsedReplies(usedReplies);
            response.setHighQualityReplies(highQualityReplies);
            response.setAverageFeedbackScore(avgScore);
            response.setAverageGenerationTimeMs(avgTime);
            response.setVersion(version);
            response.setDetails(new java.util.HashMap<>());
            return response;
        }
        
        public static HealthResponse unhealthy(String error) {
            HealthResponse response = new HealthResponse();
            response.setStatus("DOWN");
            response.setTimestamp(LocalDateTime.now());
            Map<String, Object> details = new java.util.HashMap<>();
            details.put("error", error);
            response.setDetails(details);
            return response;
        }
    }
}