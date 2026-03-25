package com.im.controller;

import com.im.entity.MessageQualityScoreEntity;
import com.im.service.MessageQualityScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/message-quality")
public class MessageQualityScoreController {
    
    @Autowired
    private MessageQualityScoreService service;
    
    // Scoring endpoints
    
    @PostMapping("/score")
    public ResponseEntity<?> scoreMessage(@RequestBody ScoreMessageRequest request) {
        try {
            MessageQualityScoreEntity entity = service.scoreMessage(
                request.getMessageId(),
                request.getSessionId(),
                request.getSenderId(),
                request.getReceiverId(),
                request.getMessageType(),
                request.getContent()
            );
            return ResponseEntity.ok(entity);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/score/{messageId}")
    public ResponseEntity<?> updateMessageScores(
            @PathVariable String messageId,
            @RequestBody ScoreUpdateRequest request) {
        try {
            MessageQualityScoreEntity entity = service.updateMessageScores(
                messageId,
                request.getSpamScore(),
                request.getSuspiciousScore(),
                request.getToxicityScore(),
                request.getAiConfidence(),
                request.getAiModelVersion()
            );
            if (entity != null) {
                return ResponseEntity.ok(entity);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Message not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    // Basic CRUD endpoints
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/message/{messageId}")
    public ResponseEntity<?> getByMessageId(@PathVariable String messageId) {
        return service.findByMessageId(messageId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<MessageQualityScoreEntity>> getBySessionId(@PathVariable String sessionId) {
        return ResponseEntity.ok(service.findBySessionId(sessionId));
    }
    
    @GetMapping("/sender/{senderId}")
    public ResponseEntity<List<MessageQualityScoreEntity>> getBySenderId(@PathVariable String senderId) {
        return ResponseEntity.ok(service.findBySenderId(senderId));
    }
    
    @GetMapping("/sender/{senderId}/receiver/{receiverId}")
    public ResponseEntity<List<MessageQualityScoreEntity>> getBySenderIdAndReceiverId(
            @PathVariable String senderId,
            @PathVariable String receiverId) {
        return ResponseEntity.ok(service.findBySenderIdAndReceiverId(senderId, receiverId));
    }
    
    @GetMapping
    public ResponseEntity<Page<MessageQualityScoreEntity>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(service.findAll(PageRequest.of(page, size, Sort.by("createdAt").descending())));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/message/{messageId}")
    public ResponseEntity<Void> deleteByMessageId(@PathVariable String messageId) {
        service.deleteByMessageId(messageId);
        return ResponseEntity.noContent().build();
    }
    
    // Risk-based endpoints
    
    @GetMapping("/high-risk")
    public ResponseEntity<List<MessageQualityScoreEntity>> getHighRiskMessages(
            @RequestParam(defaultValue = "0.7") Double threshold) {
        return ResponseEntity.ok(service.findHighRiskMessages(threshold));
    }
    
    @GetMapping("/spam")
    public ResponseEntity<List<MessageQualityScoreEntity>> getSpamMessages() {
        return ResponseEntity.ok(service.findSpamMessages());
    }
    
    @GetMapping("/suspicious")
    public ResponseEntity<List<MessageQualityScoreEntity>> getSuspiciousMessages() {
        return ResponseEntity.ok(service.findSuspiciousMessages());
    }
    
    @GetMapping("/toxic")
    public ResponseEntity<List<MessageQualityScoreEntity>> getToxicMessages() {
        return ResponseEntity.ok(service.findToxicMessages());
    }
    
    @GetMapping("/needs-review")
    public ResponseEntity<Page<MessageQualityScoreEntity>> getMessagesNeedingReview(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(service.findMessagesNeedingReview(PageRequest.of(page, size)));
    }
    
    // Review management endpoints
    
    @PutMapping("/{messageId}/review")
    public ResponseEntity<?> markAsReviewed(
            @PathVariable String messageId,
            @RequestBody ReviewRequest request) {
        try {
            MessageQualityScoreEntity entity = service.markAsReviewed(
                messageId,
                request.getReviewedBy(),
                request.getReviewNotes(),
                request.getActionTaken()
            );
            if (entity != null) {
                return ResponseEntity.ok(entity);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Message not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/review-status/{status}")
    public ResponseEntity<Page<MessageQualityScoreEntity>> getByReviewStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(service.findByReviewStatus(status, PageRequest.of(page, size)));
    }
    
    // User feedback endpoints
    
    @PostMapping("/{messageId}/flag")
    public ResponseEntity<?> flagByUser(@PathVariable String messageId) {
        try {
            MessageQualityScoreEntity entity = service.flagByUser(messageId);
            if (entity != null) {
                return ResponseEntity.ok(entity);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Message not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{messageId}/flag")
    public ResponseEntity<?> unflagByUser(@PathVariable String messageId) {
        try {
            MessageQualityScoreEntity entity = service.unflagByUser(messageId);
            if (entity != null) {
                return ResponseEntity.ok(entity);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Message not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/{messageId}/flag/system")
    public ResponseEntity<?> flagBySystem(@PathVariable String messageId) {
        try {
            MessageQualityScoreEntity entity = service.flagBySystem(messageId);
            if (entity != null) {
                return ResponseEntity.ok(entity);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Message not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    // Appeal management endpoints
    
    @PostMapping("/{messageId}/appeal")
    public ResponseEntity<?> appealMessage(
            @PathVariable String messageId,
            @RequestBody AppealRequest request) {
        try {
            MessageQualityScoreEntity entity = service.appealMessage(messageId, request.getAppealStatus());
            if (entity != null) {
                return ResponseEntity.ok(entity);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Message not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/appeals/pending")
    public ResponseEntity<Page<MessageQualityScoreEntity>> getPendingAppeals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(service.findPendingAppeals(PageRequest.of(page, size)));
    }
    
    // Statistics endpoints
    
    @GetMapping("/statistics")
    public ResponseEntity<MessageQualityScoreService.QualityScoreStatistics> getStatistics(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            return ResponseEntity.ok(service.getStatistics(start, end));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(null);
        }
    }
    
    @GetMapping("/statistics/sender/{senderId}")
    public ResponseEntity<MessageQualityScoreService.SenderStatistics> getSenderStatistics(@PathVariable String senderId) {
        return ResponseEntity.ok(service.getSenderStatistics(senderId));
    }
    
    @GetMapping("/statistics/top-spam-senders")
    public ResponseEntity<List<Object[]>> getTopSpamSenders(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(service.findTopSpamSenders(limit));
    }
    
    @GetMapping("/statistics/top-toxic-senders")
    public ResponseEntity<List<Object[]>> getTopToxicSenders(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(service.findTopToxicSenders(limit));
    }
    
    // Search endpoints
    
    @GetMapping("/search")
    public ResponseEntity<List<MessageQualityScoreEntity>> searchByContentKeyword(@RequestParam String keyword) {
        return ResponseEntity.ok(service.searchByContentKeyword(keyword));
    }
    
    @GetMapping("/language/{language}")
    public ResponseEntity<List<MessageQualityScoreEntity>> getByLanguage(@PathVariable String language) {
        return ResponseEntity.ok(service.findByLanguage(language));
    }
    
    @GetMapping("/type/{messageType}")
    public ResponseEntity<List<MessageQualityScoreEntity>> getByMessageType(@PathVariable String messageType) {
        return ResponseEntity.ok(service.findByMessageType(messageType));
    }
    
    // Session-specific endpoints
    
    @GetMapping("/session/{sessionId}/quality")
    public ResponseEntity<Double> getSessionQualityScore(@PathVariable String sessionId) {
        Double score = service.getSessionQualityScore(sessionId);
        return ResponseEntity.ok(score != null ? score : 0.0);
    }
    
    @GetMapping("/session/{sessionId}/needs-review-count")
    public ResponseEntity<Long> getSessionMessagesNeedingReview(@PathVariable String sessionId) {
        return ResponseEntity.ok(service.getSessionMessagesNeedingReview(sessionId));
    }
    
    // Batch operations endpoints
    
    @PostMapping("/batch/score")
    public ResponseEntity<Map<String, Object>> batchUpdateScores(@RequestBody List<ScoreUpdateRequest> updates) {
        try {
            int count = 0;
            for (ScoreUpdateRequest update : updates) {
                MessageQualityScoreEntity entity = service.updateMessageScores(
                    update.getMessageId(),
                    update.getSpamScore(),
                    update.getSuspiciousScore(),
                    update.getToxicityScore(),
                    update.getAiConfidence(),
                    update.getAiModelVersion()
                );
                if (entity != null) {
                    count++;
                }
            }
            Map<String, Object> response = new HashMap<>();
            response.put("updated", count);
            response.put("total", updates.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/cleanup/old")
    public ResponseEntity<Void> deleteOldRecords(@RequestParam String cutoffDate) {
        try {
            LocalDateTime cutoff = LocalDateTime.parse(cutoffDate);
            service.deleteOldRecords(cutoff);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Health check endpoint
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "MessageQualityScore");
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }
    
    // Request/Response DTOs
    
    public static class ScoreMessageRequest {
        private String messageId;
        private String sessionId;
        private String senderId;
        private String receiverId;
        private String messageType;
        private String content;
        
        // Getters and setters
        public String getMessageId() { return messageId; }
        public void setMessageId(String messageId) { this.messageId = messageId; }
        
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        
        public String getSenderId() { return senderId; }
        public void setSenderId(String senderId) { this.senderId = senderId; }
        
        public String getReceiverId() { return receiverId; }
        public void setReceiverId(String receiverId) { this.receiverId = receiverId; }
        
        public String getMessageType() { return messageType; }
        public void setMessageType(String messageType) { this.messageType = messageType; }
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
    
    public static class ScoreUpdateRequest {
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
    
    public static class ReviewRequest {
        private String reviewedBy;
        private String reviewNotes;
        private String actionTaken;
        
        // Getters and setters
        public String getReviewedBy() { return reviewedBy; }
        public void setReviewedBy(String reviewedBy) { this.reviewedBy = reviewedBy; }
        
        public String getReviewNotes() { return reviewNotes; }
        public void setReviewNotes(String reviewNotes) { this.reviewNotes = reviewNotes; }
        
        public String getActionTaken() { return actionTaken; }
        public void setActionTaken(String actionTaken) { this.actionTaken = actionTaken; }
    }
    
    public static class AppealRequest {
        private String appealStatus;
        
        // Getters and setters
        public String getAppealStatus() { return appealStatus; }
        public void setAppealStatus(String appealStatus) { this.appealStatus = appealStatus; }
    }
}