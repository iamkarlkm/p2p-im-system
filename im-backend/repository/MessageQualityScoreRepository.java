package com.im.repository;

import com.im.entity.MessageQualityScoreEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MessageQualityScoreRepository extends JpaRepository<MessageQualityScoreEntity, Long> {
    
    // Basic CRUD operations
    Optional<MessageQualityScoreEntity> findByMessageId(String messageId);
    
    List<MessageQualityScoreEntity> findBySessionId(String sessionId);
    
    List<MessageQualityScoreEntity> findBySenderId(String senderId);
    
    List<MessageQualityScoreEntity> findByReceiverId(String receiverId);
    
    List<MessageQualityScoreEntity> findBySenderIdAndReceiverId(String senderId, String receiverId);
    
    List<MessageQualityScoreEntity> findByMessageType(String messageType);
    
    // Score-based queries
    List<MessageQualityScoreEntity> findBySpamScoreGreaterThanEqual(Double threshold);
    
    List<MessageQualityScoreEntity> findBySuspiciousScoreGreaterThanEqual(Double threshold);
    
    List<MessageQualityScoreEntity> findByToxicityScoreGreaterThanEqual(Double threshold);
    
    List<MessageQualityScoreEntity> findByQualityScoreLessThanEqual(Double threshold);
    
    // Flag/status queries
    List<MessageQualityScoreEntity> findByIsSpamTrue();
    
    List<MessageQualityScoreEntity> findByIsSuspiciousTrue();
    
    List<MessageQualityScoreEntity> findByIsToxicTrue();
    
    List<MessageQualityScoreEntity> findByNeedsReviewTrue();
    
    List<MessageQualityScoreEntity> findByReviewStatus(String reviewStatus);
    
    List<MessageQualityScoreEntity> findByFlaggedBySystemTrue();
    
    List<MessageQualityScoreEntity> findByFlaggedByUserTrue();
    
    // Combined score queries
    List<MessageQualityScoreEntity> findBySpamScoreGreaterThanEqualOrSuspiciousScoreGreaterThanEqualOrToxicityScoreGreaterThanEqual(
            Double spamThreshold, Double suspiciousThreshold, Double toxicityThreshold);
    
    // Review-related queries
    Page<MessageQualityScoreEntity> findByNeedsReviewTrue(Pageable pageable);
    
    Page<MessageQualityScoreEntity> findByReviewStatus(String reviewStatus, Pageable pageable);
    
    List<MessageQualityScoreEntity> findByReviewedBy(String reviewedBy);
    
    List<MessageQualityScoreEntity> findByReviewedByAndCreatedAtBetween(
            String reviewedBy, LocalDateTime startDate, LocalDateTime endDate);
    
    // Time-based queries
    List<MessageQualityScoreEntity> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    List<MessageQualityScoreEntity> findByUpdatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    List<MessageQualityScoreEntity> findByReviewedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // User feedback queries
    List<MessageQualityScoreEntity> findByUserFeedbackCountGreaterThan(Integer threshold);
    
    List<MessageQualityScoreEntity> findByAppealCountGreaterThan(Integer threshold);
    
    List<MessageQualityScoreEntity> findByAppealStatus(String appealStatus);
    
    // Aggregation queries
    @Query("SELECT AVG(m.spamScore) FROM MessageQualityScoreEntity m WHERE m.createdAt BETWEEN :startDate AND :endDate")
    Double findAverageSpamScoreBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT AVG(m.suspiciousScore) FROM MessageQualityScoreEntity m WHERE m.createdAt BETWEEN :startDate AND :endDate")
    Double findAverageSuspiciousScoreBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT AVG(m.toxicityScore) FROM MessageQualityScoreEntity m WHERE m.createdAt BETWEEN :startDate AND :endDate")
    Double findAverageToxicityScoreBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT AVG(m.qualityScore) FROM MessageQualityScoreEntity m WHERE m.createdAt BETWEEN :startDate AND :endDate")
    Double findAverageQualityScoreBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(m) FROM MessageQualityScoreEntity m WHERE m.isSpam = true AND m.createdAt BETWEEN :startDate AND :endDate")
    Long countSpamMessagesBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(m) FROM MessageQualityScoreEntity m WHERE m.isSuspicious = true AND m.createdAt BETWEEN :startDate AND :endDate")
    Long countSuspiciousMessagesBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(m) FROM MessageQualityScoreEntity m WHERE m.isToxic = true AND m.createdAt BETWEEN :startDate AND :endDate")
    Long countToxicMessagesBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(m) FROM MessageQualityScoreEntity m WHERE m.needsReview = true AND m.createdAt BETWEEN :startDate AND :endDate")
    Long countMessagesNeedingReviewBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(m) FROM MessageQualityScoreEntity m WHERE m.flaggedByUser = true AND m.createdAt BETWEEN :startDate AND :endDate")
    Long countUserFlaggedMessagesBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Sender-specific statistics
    @Query("SELECT AVG(m.spamScore) FROM MessageQualityScoreEntity m WHERE m.senderId = :senderId")
    Double findAverageSpamScoreBySender(@Param("senderId") String senderId);
    
    @Query("SELECT AVG(m.suspiciousScore) FROM MessageQualityScoreEntity m WHERE m.senderId = :senderId")
    Double findAverageSuspiciousScoreBySender(@Param("senderId") String senderId);
    
    @Query("SELECT AVG(m.toxicityScore) FROM MessageQualityScoreEntity m WHERE m.senderId = :senderId")
    Double findAverageToxicityScoreBySender(@Param("senderId") String senderId);
    
    @Query("SELECT COUNT(m) FROM MessageQualityScoreEntity m WHERE m.senderId = :senderId AND m.isSpam = true")
    Long countSpamMessagesBySender(@Param("senderId") String senderId);
    
    @Query("SELECT COUNT(m) FROM MessageQualityScoreEntity m WHERE m.senderId = :senderId AND m.isSuspicious = true")
    Long countSuspiciousMessagesBySender(@Param("senderId") String senderId);
    
    @Query("SELECT COUNT(m) FROM MessageQualityScoreEntity m WHERE m.senderId = :senderId AND m.isToxic = true")
    Long countToxicMessagesBySender(@Param("senderId") String senderId);
    
    // Search queries
    @Query("SELECT m FROM MessageQualityScoreEntity m WHERE LOWER(m.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<MessageQualityScoreEntity> findByContentKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT m FROM MessageQualityScoreEntity m WHERE LOWER(m.keywordTags) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<MessageQualityScoreEntity> findByKeywordTags(@Param("keyword") String keyword);
    
    @Query("SELECT m FROM MessageQualityScoreEntity m WHERE m.language = :language")
    List<MessageQualityScoreEntity> findByLanguage(@Param("language") String language);
    
    // High-risk sender queries
    @Query("SELECT m.senderId, COUNT(m) as spamCount " +
           "FROM MessageQualityScoreEntity m " +
           "WHERE m.isSpam = true " +
           "GROUP BY m.senderId " +
           "ORDER BY spamCount DESC")
    List<Object[]> findTopSpamSenders(Pageable pageable);
    
    @Query("SELECT m.senderId, COUNT(m) as toxicCount " +
           "FROM MessageQualityScoreEntity m " +
           "WHERE m.isToxic = true " +
           "GROUP BY m.senderId " +
           "ORDER BY toxicCount DESC")
    List<Object[]> findTopToxicSenders(Pageable pageable);
    
    @Query("SELECT m.senderId, AVG(m.spamScore) as avgSpamScore " +
           "FROM MessageQualityScoreEntity m " +
           "GROUP BY m.senderId " +
           "HAVING COUNT(m) >= :minMessages " +
           "ORDER BY avgSpamScore DESC")
    List<Object[]> findTopSpamScoreSenders(@Param("minMessages") Integer minMessages, Pageable pageable);
    
    // Session-specific queries
    @Query("SELECT m FROM MessageQualityScoreEntity m WHERE m.sessionId = :sessionId ORDER BY m.createdAt DESC")
    Page<MessageQualityScoreEntity> findBySessionIdOrderByCreatedAtDesc(@Param("sessionId") String sessionId, Pageable pageable);
    
    @Query("SELECT AVG(m.qualityScore) FROM MessageQualityScoreEntity m WHERE m.sessionId = :sessionId")
    Double findAverageQualityScoreBySession(@Param("sessionId") String sessionId);
    
    @Query("SELECT COUNT(m) FROM MessageQualityScoreEntity m WHERE m.sessionId = :sessionId AND m.needsReview = true")
    Long countMessagesNeedingReviewBySession(@Param("sessionId") String sessionId);
    
    // Batch operations
    void deleteByCreatedAtBefore(LocalDateTime cutoffDate);
    
    void deleteByMessageIdIn(List<String> messageIds);
    
    @Query("SELECT m FROM MessageQualityScoreEntity m WHERE m.messageId IN :messageIds")
    List<MessageQualityScoreEntity> findByMessageIds(@Param("messageIds") List<String> messageIds);
    
    // Appeal-related queries
    @Query("SELECT m FROM MessageQualityScoreEntity m WHERE m.appealStatus = 'PENDING' ORDER BY m.appealCount DESC")
    Page<MessageQualityScoreEntity> findPendingAppeals(Pageable pageable);
    
    @Query("SELECT m FROM MessageQualityScoreEntity m WHERE m.appealStatus = 'APPROVED'")
    Page<MessageQualityScoreEntity> findApprovedAppeals(Pageable pageable);
    
    @Query("SELECT m FROM MessageQualityScoreEntity m WHERE m.appealStatus = 'REJECTED'")
    Page<MessageQualityScoreEntity> findRejectedAppeals(Pageable pageable);
    
    // Performance optimization queries
    @Query("SELECT m FROM MessageQualityScoreEntity m WHERE m.needsReview = true AND m.reviewStatus = 'PENDING'")
    List<MessageQualityScoreEntity> findUnreviewedMessagesNeedingReview();
    
    @Query("SELECT m FROM MessageQualityScoreEntity m WHERE m.flaggedBySystem = true AND m.reviewStatus = 'PENDING'")
    List<MessageQualityScoreEntity> findSystemFlaggedUnreviewedMessages();
    
    @Query("SELECT m FROM MessageQualityScoreEntity m WHERE m.flaggedByUser = true AND m.reviewStatus = 'PENDING'")
    List<MessageQualityScoreEntity> findUserFlaggedUnreviewedMessages();
    
    // Statistics for dashboard
    @Query("SELECT COUNT(m) FROM MessageQualityScoreEntity m WHERE m.createdAt >= :sinceDate")
    Long countTotalMessagesSince(@Param("sinceDate") LocalDateTime sinceDate);
    
    @Query("SELECT COUNT(m) FROM MessageQualityScoreEntity m WHERE m.isSpam = true AND m.createdAt >= :sinceDate")
    Long countSpamMessagesSince(@Param("sinceDate") LocalDateTime sinceDate);
    
    @Query("SELECT COUNT(m) FROM MessageQualityScoreEntity m WHERE m.isSuspicious = true AND m.createdAt >= :sinceDate")
    Long countSuspiciousMessagesSince(@Param("sinceDate") LocalDateTime sinceDate);
    
    @Query("SELECT COUNT(m) FROM MessageQualityScoreEntity m WHERE m.isToxic = true AND m.createdAt >= :sinceDate")
    Long countToxicMessagesSince(@Param("sinceDate") LocalDateTime sinceDate);
    
    @Query("SELECT COUNT(m) FROM MessageQualityScoreEntity m WHERE m.needsReview = true AND m.createdAt >= :sinceDate")
    Long countMessagesNeedingReviewSince(@Param("sinceDate") LocalDateTime sinceDate);
    
    @Query("SELECT COUNT(m) FROM MessageQualityScoreEntity m WHERE m.flaggedByUser = true AND m.createdAt >= :sinceDate")
    Long countUserFlaggedMessagesSince(@Param("sinceDate") LocalDateTime sinceDate);
}