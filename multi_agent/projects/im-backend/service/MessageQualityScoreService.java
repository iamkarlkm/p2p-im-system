package com.im.service;

import com.im.entity.MessageQualityScoreEntity;
import com.im.repository.MessageQualityScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MessageQualityScoreService {
    
    @Autowired
    private MessageQualityScoreRepository repository;
    
    // Core scoring methods
    
    @Transactional
    public MessageQualityScoreEntity scoreMessage(String messageId, String sessionId, String senderId, 
                                                  String receiverId, String messageType, String content) {
        MessageQualityScoreEntity entity = new MessageQualityScoreEntity(messageId, sessionId, senderId, 
                                                                         receiverId, messageType, content);
        
        // Perform AI/NLP analysis (simplified for now)
        AiAnalysisResult analysisResult = performAiAnalysis(content, messageType);
        
        entity.updateScores(analysisResult.getSpamScore(), analysisResult.getSuspiciousScore(), 
                           analysisResult.getToxicityScore(), analysisResult.getAiConfidence(),
                           analysisResult.getModelVersion());
        
        entity.setLanguage(analysisResult.getLanguage());
        entity.setSentimentScore(analysisResult.getSentimentScore());
        entity.setKeywordTags(String.join(",", analysisResult.getKeywords()));
        entity.setMetadata(analysisResult.getMetadata());
        
        return repository.save(entity);
    }
    
    @Transactional
    public MessageQualityScoreEntity updateMessageScores(String messageId, Double spamScore, Double suspiciousScore, 
                                                         Double toxicityScore, Double aiConfidence, String aiModelVersion) {
        Optional<MessageQualityScoreEntity> optionalEntity = repository.findByMessageId(messageId);
        if (optionalEntity.isPresent()) {
            MessageQualityScoreEntity entity = optionalEntity.get();
            entity.updateScores(spamScore, suspiciousScore, toxicityScore, aiConfidence, aiModelVersion);
            return repository.save(entity);
        }
        return null;
    }
    
    // Basic CRUD operations
    
    public MessageQualityScoreEntity save(MessageQualityScoreEntity entity) {
        return repository.save(entity);
    }
    
    public Optional<MessageQualityScoreEntity> findByMessageId(String messageId) {
        return repository.findByMessageId(messageId);
    }
    
    public List<MessageQualityScoreEntity> findBySessionId(String sessionId) {
        return repository.findBySessionId(sessionId);
    }
    
    public List<MessageQualityScoreEntity> findBySenderId(String senderId) {
        return repository.findBySenderId(senderId);
    }
    
    public List<MessageQualityScoreEntity> findBySenderIdAndReceiverId(String senderId, String receiverId) {
        return repository.findBySenderIdAndReceiverId(senderId, receiverId);
    }
    
    public List<MessageQualityScoreEntity> findAll() {
        return repository.findAll();
    }
    
    public Page<MessageQualityScoreEntity> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }
    
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
    
    public void deleteByMessageId(String messageId) {
        repository.findByMessageId(messageId).ifPresent(entity -> repository.delete(entity));
    }
    
    // Score-based queries
    
    public List<MessageQualityScoreEntity> findHighRiskMessages(Double threshold) {
        return repository.findBySpamScoreGreaterThanEqualOrSuspiciousScoreGreaterThanEqualOrToxicityScoreGreaterThanEqual(
                threshold, threshold, threshold);
    }
    
    public List<MessageQualityScoreEntity> findSpamMessages() {
        return repository.findByIsSpamTrue();
    }
    
    public List<MessageQualityScoreEntity> findSuspiciousMessages() {
        return repository.findByIsSuspiciousTrue();
    }
    
    public List<MessageQualityScoreEntity> findToxicMessages() {
        return repository.findByIsToxicTrue();
    }
    
    public List<MessageQualityScoreEntity> findMessagesNeedingReview() {
        return repository.findByNeedsReviewTrue();
    }
    
    public Page<MessageQualityScoreEntity> findMessagesNeedingReview(Pageable pageable) {
        return repository.findByNeedsReviewTrue(pageable);
    }
    
    // Review management
    
    @Transactional
    public MessageQualityScoreEntity markAsReviewed(String messageId, String reviewedBy, 
                                                    String reviewNotes, String actionTaken) {
        Optional<MessageQualityScoreEntity> optionalEntity = repository.findByMessageId(messageId);
        if (optionalEntity.isPresent()) {
            MessageQualityScoreEntity entity = optionalEntity.get();
            entity.markAsReviewed(reviewedBy, reviewNotes, actionTaken);
            return repository.save(entity);
        }
        return null;
    }
    
    public List<MessageQualityScoreEntity> findByReviewStatus(String reviewStatus) {
        return repository.findByReviewStatus(reviewStatus);
    }
    
    public Page<MessageQualityScoreEntity> findByReviewStatus(String reviewStatus, Pageable pageable) {
        return repository.findByReviewStatus(reviewStatus, pageable);
    }
    
    // User feedback and flagging
    
    @Transactional
    public MessageQualityScoreEntity flagByUser(String messageId) {
        Optional<MessageQualityScoreEntity> optionalEntity = repository.findByMessageId(messageId);
        if (optionalEntity.isPresent()) {
            MessageQualityScoreEntity entity = optionalEntity.get();
            entity.setFlaggedByUser(true);
            entity.setNeedsReview(true);
            return repository.save(entity);
        }
        return null;
    }
    
    @Transactional
    public MessageQualityScoreEntity unflagByUser(String messageId) {
        Optional<MessageQualityScoreEntity> optionalEntity = repository.findByMessageId(messageId);
        if (optionalEntity.isPresent()) {
            MessageQualityScoreEntity entity = optionalEntity.get();
            entity.setFlaggedByUser(false);
            // Note: doesn't automatically set needsReview to false
            return repository.save(entity);
        }
        return null;
    }
    
    @Transactional
    public MessageQualityScoreEntity flagBySystem(String messageId) {
        Optional<MessageQualityScoreEntity> optionalEntity = repository.findByMessageId(messageId);
        if (optionalEntity.isPresent()) {
            MessageQualityScoreEntity entity = optionalEntity.get();
            entity.setFlaggedBySystem(true);
            entity.setNeedsReview(true);
            return repository.save(entity);
        }
        return null;
    }
    
    // Appeal management
    
    @Transactional
    public MessageQualityScoreEntity appealMessage(String messageId, String appealStatus) {
        Optional<MessageQualityScoreEntity> optionalEntity = repository.findByMessageId(messageId);
        if (optionalEntity.isPresent()) {
            MessageQualityScoreEntity entity = optionalEntity.get();
            entity.markAsAppealed(appealStatus);
            return repository.save(entity);
        }
        return null;
    }
    
    public Page<MessageQualityScoreEntity> findPendingAppeals(Pageable pageable) {
        return repository.findPendingAppeals(pageable);
    }
    
    // Statistics and analytics
    
    public QualityScoreStatistics getStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        QualityScoreStatistics stats = new QualityScoreStatistics();
        
        stats.setTotalMessages(repository.countTotalMessagesSince(startDate));
        stats.setSpamMessages(repository.countSpamMessagesBetweenDates(startDate, endDate));
        stats.setSuspiciousMessages(repository.countSuspiciousMessagesBetweenDates(startDate, endDate));
        stats.setToxicMessages(repository.countToxicMessagesBetweenDates(startDate, endDate));
        stats.setMessagesNeedingReview(repository.countMessagesNeedingReviewBetweenDates(startDate, endDate));
        stats.setUserFlaggedMessages(repository.countUserFlaggedMessagesBetweenDates(startDate, endDate));
        
        stats.setAverageSpamScore(repository.findAverageSpamScoreBetweenDates(startDate, endDate));
        stats.setAverageSuspiciousScore(repository.findAverageSuspiciousScoreBetweenDates(startDate, endDate));
        stats.setAverageToxicityScore(repository.findAverageToxicityScoreBetweenDates(startDate, endDate));
        stats.setAverageQualityScore(repository.findAverageQualityScoreBetweenDates(startDate, endDate));
        
        return stats;
    }
    
    public SenderStatistics getSenderStatistics(String senderId) {
        SenderStatistics stats = new SenderStatistics();
        
        stats.setSenderId(senderId);
        stats.setTotalMessages((long) repository.findBySenderId(senderId).size());
        stats.setSpamMessages(repository.countSpamMessagesBySender(senderId));
        stats.setSuspiciousMessages(repository.countSuspiciousMessagesBySender(senderId));
        stats.setToxicMessages(repository.countToxicMessagesBySender(senderId));
        
        stats.setAverageSpamScore(repository.findAverageSpamScoreBySender(senderId));
        stats.setAverageSuspiciousScore(repository.findAverageSuspiciousScoreBySender(senderId));
        stats.setAverageToxicityScore(repository.findAverageToxicityScoreBySender(senderId));
        
        return stats;
    }
    
    public List<Object[]> findTopSpamSenders(int limit) {
        return repository.findTopSpamSenders(Pageable.ofSize(limit));
    }
    
    public List<Object[]> findTopToxicSenders(int limit) {
        return repository.findTopToxicSenders(Pageable.ofSize(limit));
    }
    
    public List<Object[]> findTopSpamScoreSenders(int minMessages, int limit) {
        return repository.findTopSpamScoreSenders(minMessages, Pageable.ofSize(limit));
    }
    
    // Search and filtering
    
    public List<MessageQualityScoreEntity> searchByContentKeyword(String keyword) {
        return repository.findByContentKeyword(keyword);
    }
    
    public List<MessageQualityScoreEntity> findByLanguage(String language) {
        return repository.findByLanguage(language);
    }
    
    public List<MessageQualityScoreEntity> findByMessageType(String messageType) {
        return repository.findByMessageType(messageType);
    }
    
    public List<MessageQualityScoreEntity> findByMessageIds(List<String> messageIds) {
        return repository.findByMessageIds(messageIds);
    }
    
    // Time-based queries
    
    public List<MessageQualityScoreEntity> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return repository.findByCreatedAtBetween(startDate, endDate);
    }
    
    public List<MessageQualityScoreEntity> findByUpdatedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return repository.findByUpdatedAtBetween(startDate, endDate);
    }
    
    public List<MessageQualityScoreEntity> findByReviewedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return repository.findByReviewedAtBetween(startDate, endDate);
    }
    
    // Session-specific queries
    
    public Page<MessageQualityScoreEntity> findBySessionIdOrderByCreatedAtDesc(String sessionId, Pageable pageable) {
        return repository.findBySessionIdOrderByCreatedAtDesc(sessionId, pageable);
    }
    
    public Double getSessionQualityScore(String sessionId) {
        return repository.findAverageQualityScoreBySession(sessionId);
    }
    
    public Long getSessionMessagesNeedingReview(String sessionId) {
        return repository.countMessagesNeedingReviewBySession(sessionId);
    }
    
    // Batch operations
    
    @Transactional
    public void deleteOldRecords(LocalDateTime cutoffDate) {
        repository.deleteByCreatedAtBefore(cutoffDate);
    }
    
    @Transactional
    public int batchUpdateScores(List<MessageScoreUpdate> updates) {
        int count = 0;
        for (MessageScoreUpdate update : updates) {
            MessageQualityScoreEntity entity = updateMessageScores(
                update.getMessageId(), update.getSpamScore(), update.getSuspiciousScore(),
                update.getToxicityScore(), update.getAiConfidence(), update.getAiModelVersion()
            );
            if (entity != null) {
                count++;
            }
        }
        return count;
    }
    
    // Helper methods
    
    private AiAnalysisResult performAiAnalysis(String content, String messageType) {
        // This is a simplified placeholder for actual AI/NLP analysis
        // In production, this would integrate with external AI services or internal models
        
        AiAnalysisResult result = new AiAnalysisResult();
        
        // Simple heuristic-based analysis
        double spamScore = calculateSpamScore(content, messageType);
        double suspiciousScore = calculateSuspiciousScore(content, messageType);
        double toxicityScore = calculateToxicityScore(content, messageType);
        
        result.setSpamScore(spamScore);
        result.setSuspiciousScore(suspiciousScore);
        result.setToxicityScore(toxicityScore);
        result.setAiConfidence(0.85); // Example confidence
        result.setModelVersion("v1.0.0");
        result.setLanguage("en"); // Simple detection
        result.setSentimentScore(calculateSentimentScore(content));
        
        // Extract keywords (simplified)
        String[] keywords = extractKeywords(content);
        result.setKeywords(keywords);
        
        result.setMetadata("{\"analysis_type\": \"heuristic\", \"content_length\": " + content.length() + "}");
        
        return result;
    }
    
    private double calculateSpamScore(String content, String messageType) {
        // Simple spam detection heuristics
        String lowerContent = content.toLowerCase();
        
        double score = 0.0;
        
        // Check for common spam indicators
        if (lowerContent.contains("free") && lowerContent.contains("money")) score += 0.3;
        if (lowerContent.contains("click") && lowerContent.contains("link")) score += 0.3;
        if (lowerContent.contains("winner") && lowerContent.contains("prize")) score += 0.2;
        if (lowerContent.contains("urgent") && lowerContent.contains("action")) score += 0.2;
        if (lowerContent.contains("guaranteed") && lowerContent.contains("profit")) score += 0.3;
        
        // Excessive punctuation or capitalization
        int exclamationCount = countOccurrences(content, '!');
        int questionCount = countOccurrences(content, '?');
        int uppercaseCount = countUppercaseLetters(content);
        
        if (exclamationCount > 3) score += 0.1 * Math.min(exclamationCount, 10);
        if (questionCount > 3) score += 0.05 * Math.min(questionCount, 10);
        if (uppercaseCount > content.length() * 0.3) score += 0.2;
        
        return Math.min(score, 1.0);
    }
    
    private double calculateSuspiciousScore(String content, String messageType) {
        // Suspicious content detection
        String lowerContent = content.toLowerCase();
        
        double score = 0.0;
        
        // Check for suspicious patterns
        if (lowerContent.contains("password") && lowerContent.contains("reset")) score += 0.3;
        if (lowerContent.contains("account") && lowerContent.contains("verify")) score += 0.3;
        if (lowerContent.contains("login") && lowerContent.contains("credentials")) score += 0.3;
        if (lowerContent.contains("credit") && lowerContent.contains("card")) score += 0.2;
        if (lowerContent.contains("ssn") || lowerContent.contains("social security")) score += 0.4;
        if (lowerContent.contains("bank") && lowerContent.contains("account")) score += 0.3;
        
        // URLs without context
        if (content.contains("http://") || content.contains("https://")) {
            score += 0.2;
        }
        
        // Phone numbers or email addresses from unknown senders
        if (containsPhoneNumber(content) || containsEmailAddress(content)) {
            score += 0.1;
        }
        
        return Math.min(score, 1.0);
    }
    
    private double calculateToxicityScore(String content, String messageType) {
        // Toxicity detection
        String lowerContent = content.toLowerCase();
        
        double score = 0.0;
        
        // Toxic language indicators
        String[] toxicWords = {"hate", "stupid", "idiot", "moron", "kill", "die", "crap", "shit", "fuck", "bitch", 
                               "asshole", "bastard", "retard", "nigger", "fag", "whore", "slut", "cunt"};
        
        for (String word : toxicWords) {
            if (lowerContent.contains(word)) {
                score += 0.1;
            }
        }
        
        // Threats or violent language
        if (lowerContent.contains("i will kill") || lowerContent.contains("i'll kill")) score += 0.5;
        if (lowerContent.contains("die") && lowerContent.contains("you")) score += 0.4;
        if (lowerContent.contains("beat") && lowerContent.contains("you")) score += 0.3;
        if (lowerContent.contains("hurt") && lowerContent.contains("you")) score += 0.3;
        
        // Excessive negative sentiment
        String[] negativeWords = {"hate", "disgusting", "awful", "terrible", "horrible", "worst", "stupid", "dumb"};
        int negativeCount = 0;
        for (String word : negativeWords) {
            if (lowerContent.contains(word)) {
                negativeCount++;
            }
        }
        score += 0.05 * negativeCount;
        
        return Math.min(score, 1.0);
    }
    
    private double calculateSentimentScore(String content) {
        // Simple sentiment analysis (-1.0 to 1.0)
        String lowerContent = content.toLowerCase();
        
        double score = 0.0;
        
        // Positive words
        String[] positiveWords = {"good", "great", "excellent", "awesome", "wonderful", "happy", "love", "like", 
                                  "thanks", "thank you", "appreciate", "perfect", "best", "amazing", "fantastic"};
        for (String word : positiveWords) {
            if (lowerContent.contains(word)) {
                score += 0.05;
            }
        }
        
        // Negative words
        String[] negativeWords = {"bad", "terrible", "awful", "horrible", "worst", "hate", "dislike", "angry",
                                  "sad", "disappointed", "frustrated", "upset", "annoying", "stupid", "dumb"};
        for (String word : negativeWords) {
            if (lowerContent.contains(word)) {
                score -= 0.05;
            }
        }
        
        // Exclamation marks can indicate strong emotion
        int exclamationCount = countOccurrences(content, '!');
        if (exclamationCount > 0) {
            // If we have more positive than negative words, exclamation enhances positivity
            // Otherwise, it enhances negativity
            score *= (1 + 0.1 * exclamationCount);
        }
        
        return Math.max(-1.0, Math.min(1.0, score));
    }
    
    private String[] extractKeywords(String content) {
        // Simple keyword extraction
        String[] commonWords = {"the", "a", "an", "and", "or", "but", "in", "on", "at", "to", "for", "of", "with", 
                                "by", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", 
                                "do", "does", "did", "will", "would", "can", "could", "shall", "should", "may", 
                                "might", "must", "i", "you", "he", "she", "it", "we", "they", "me", "him", "her", 
                                "us", "them", "my", "your", "his", "her", "its", "our", "their"};
        
        // Split content into words
        String[] words = content.toLowerCase().replaceAll("[^a-zA-Z0-9\\s]", "").split("\\s+");
        
        // Filter out common words and duplicates
        java.util.Set<String> keywords = new java.util.HashSet<>();
        for (String word : words) {
            if (word.length() > 3 && !java.util.Arrays.asList(commonWords).contains(word)) {
                keywords.add(word);
            }
        }
        
        return keywords.toArray(new String[0]);
    }
    
    private int countOccurrences(String str, char ch) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == ch) {
                count++;
            }
        }
        return count;
    }
    
    private int countUppercaseLetters(String str) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (Character.isUpperCase(str.charAt(i))) {
                count++;
            }
        }
        return count;
    }
    
    private boolean containsPhoneNumber(String str) {
        // Simple phone number pattern
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}");
        return pattern.matcher(str).find();
    }
    
    private boolean containsEmailAddress(String str) {
        // Simple email pattern
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
        return pattern.matcher(str).find();
    }
    
    // Inner classes for data transfer
    
    public static class AiAnalysisResult {
        private Double spamScore;
        private Double suspiciousScore;
        private Double toxicityScore;
        private Double aiConfidence;
        private String modelVersion;
        private String language;
        private Double sentimentScore;
        private String[] keywords;
        private String metadata;
        
        // Getters and setters
        public Double getSpamScore() { return spamScore; }
        public void setSpamScore(Double spamScore) { this.spamScore = spamScore; }
        
        public Double getSuspiciousScore() { return suspiciousScore; }
        public void setSuspiciousScore(Double suspiciousScore) { this.suspiciousScore = suspiciousScore; }
        
        public Double getToxicityScore() { return toxicityScore; }
        public void setToxicityScore(Double toxicityScore) { this.toxicityScore = toxicityScore; }
        
        public Double getAiConfidence() { return aiConfidence; }
        public void setAiConfidence(Double aiConfidence) { this.aiConfidence = aiConfidence; }
        
        public String getModelVersion() { return modelVersion; }
        public void setModelVersion(String modelVersion) { this.modelVersion = modelVersion; }
        
        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }
        
        public Double getSentimentScore() { return sentimentScore; }
        public void setSentimentScore(Double sentimentScore) { this.sentimentScore = sentimentScore; }
        
        public String[] getKeywords() { return keywords; }
        public void setKeywords(String[] keywords) { this.keywords = keywords; }
        
        public String getMetadata() { return metadata; }
        public void setMetadata(String metadata) { this.metadata = metadata; }
    }
    
    public static class QualityScoreStatistics {
        private Long totalMessages;
        private Long spamMessages;
        private Long suspiciousMessages;
        private Long toxicMessages;
        private Long messagesNeedingReview;
        private Long userFlaggedMessages;
        private Double averageSpamScore;
        private Double averageSuspiciousScore;
        private Double averageToxicityScore;
        private Double averageQualityScore;
        
        // Getters and setters
        public Long getTotalMessages() { return totalMessages; }
        public void setTotalMessages(Long totalMessages) { this.totalMessages = totalMessages; }
        
        public Long getSpamMessages() { return spamMessages; }
        public void setSpamMessages(Long spamMessages) { this.spamMessages = spamMessages; }
        
        public Long getSuspiciousMessages() { return suspiciousMessages; }
        public void setSuspiciousMessages(Long suspiciousMessages) { this.suspiciousMessages = suspiciousMessages; }
        
        public Long getToxicMessages() { return toxicMessages; }
        public void setToxicMessages(Long toxicMessages) { this.toxicMessages = toxicMessages; }
        
        public Long getMessagesNeedingReview() { return messagesNeedingReview; }
        public void setMessagesNeedingReview(Long messagesNeedingReview) { this.messagesNeedingReview = messagesNeedingReview; }
        
        public Long getUserFlaggedMessages() { return userFlaggedMessages; }
        public void setUserFlaggedMessages(Long userFlaggedMessages) { this.userFlaggedMessages = userFlaggedMessages; }
        
        public Double getAverageSpamScore() { return averageSpamScore; }
        public void setAverageSpamScore(Double averageSpamScore) { this.averageSpamScore = averageSpamScore; }
        
        public Double getAverageSuspiciousScore() { return averageSuspiciousScore; }
        public void setAverageSuspiciousScore(Double averageSuspiciousScore) { this.averageSuspiciousScore = averageSuspiciousScore; }
        
        public Double getAverageToxicityScore() { return averageToxicityScore; }
        public void setAverageToxicityScore(Double averageToxicityScore) { this.averageToxicityScore = averageToxicityScore; }
        
        public Double getAverageQualityScore() { return averageQualityScore; }
        public void setAverageQualityScore(Double averageQualityScore) { this.averageQualityScore = averageQualityScore; }
    }
    
    public static class SenderStatistics {
        private String senderId;
        private Long totalMessages;
        private Long spamMessages;
        private Long suspiciousMessages;
        private Long toxicMessages;
        private Double averageSpamScore;
        private Double averageSuspiciousScore;
        private Double averageToxicityScore;
        
        // Getters and setters
        public String getSenderId() { return senderId; }
        public void setSenderId(String senderId) { this.senderId = senderId; }
        
        public Long getTotalMessages() { return totalMessages; }
        public void setTotalMessages(Long totalMessages) { this.totalMessages = totalMessages; }
        
        public Long getSpamMessages() { return spamMessages; }
        public void setSpamMessages(Long spamMessages) { this.spamMessages = spamMessages; }
        
        public Long getSuspiciousMessages() { return suspiciousMessages; }
        public void setSuspiciousMessages(Long suspiciousMessages) { this.suspiciousMessages = suspiciousMessages; }
        
        public Long getToxicMessages() { return toxicMessages; }
        public void setToxicMessages(Long toxicMessages) { this.toxicMessages = toxicMessages; }
        
        public Double getAverageSpamScore() { return averageSpamScore; }
        public void setAverageSpamScore(Double averageSpamScore) { this.averageSpamScore = averageSpamScore; }
        
        public Double getAverageSuspiciousScore() { return averageSuspiciousScore; }
        public void setAverageSuspiciousScore(Double averageSuspiciousScore) { this.averageSuspiciousScore = averageSuspiciousScore; }
        
        public Double getAverageToxicityScore() { return averageToxicityScore; }
        public void setAverageToxicityScore(Double averageToxicityScore) { this.averageToxicityScore = averageToxicityScore; }
    }
    
    public static class MessageScoreUpdate {
        private String messageId;
        private Double spamScore;
        private Double suspiciousScore;
        private Double toxicityScore;
        private Double aiConfidence;
        private String aiModelVersion;
        
        // Getters and setters
        public String getMessageId() { return messageId; }
        public void setMessageId(String messageId) { this.messageId = messageId; }
        
        public Double getSpamScore() { return spamScore; }
        public void setSpamScore(Double spamScore) { this.spamScore = spamScore; }
        
        public Double getSuspiciousScore() { return suspiciousScore; }
        public void setSuspiciousScore(Double suspiciousScore) { this.suspiciousScore = suspiciousScore; }
        
        public Double getToxicityScore() { return toxicityScore; }
        public void setToxicityScore(Double toxicityScore) { this.toxicityScore = toxicityScore; }
        
        public Double getAiConfidence() { return aiConfidence; }
        public void setAiConfidence(Double aiConfidence) { this.aiConfidence = aiConfidence; }
        
        public String getAiModelVersion() { return aiModelVersion; }
        public void setAiModelVersion(String aiModelVersion) { this.aiModelVersion = aiModelVersion; }
    }
}