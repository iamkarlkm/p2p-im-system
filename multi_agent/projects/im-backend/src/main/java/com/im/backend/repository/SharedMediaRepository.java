package com.im.backend.repository;

import com.im.backend.entity.SharedMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface SharedMediaRepository extends JpaRepository<SharedMedia, Long> {
    
    @Query("SELECT m FROM SharedMedia m WHERE m.conversationId = :convId AND m.isDeleted = false " +
           "AND (:type IS NULL OR m.mediaType = :type) " +
           "AND (:senderId IS NULL OR m.senderId = :senderId) " +
           "AND (:startTime IS NULL OR m.createdAt >= :startTime) " +
           "AND (:endTime IS NULL OR m.createdAt <= :endTime) " +
           "ORDER BY m.createdAt DESC")
    List<SharedMedia> findByConversationIdAndFilters(
        @Param("convId") String conversationId,
        @Param("type") SharedMedia.MediaType mediaType,
        @Param("senderId") String senderId,
        @Param("startTime") Long startTime,
        @Param("endTime") Long endTime,
        int offset, int limit);

    @Query("SELECT COUNT(m) FROM SharedMedia m WHERE m.conversationId = :convId " +
           "AND (:type IS NULL OR m.mediaType = :type) AND m.isDeleted = false")
    Long countByConversationIdAndType(@Param("convId") String conversationId,
                                      @Param("type") SharedMedia.MediaType mediaType);

    @Query("SELECT COUNT(m) FROM SharedMedia m WHERE m.conversationId = :convId " +
           "AND m.mediaType = :type AND m.isDeleted = false")
    Long countByConversationAndType(@Param("convId") String conversationId,
                                    @Param("type") SharedMedia.MediaType mediaType);

    @Query("SELECT COALESCE(SUM(m.fileSize), 0) FROM SharedMedia m WHERE m.conversationId = :convId AND m.isDeleted = false")
    Long sumFileSizeByConversation(@Param("convId") String conversationId);

    List<SharedMedia> findByConversationIdAndIsDeletedFalseOrderByCreatedAtDesc(String conversationId);

    @Query("SELECT m FROM SharedMedia m WHERE m.conversationId = :convId AND m.isDeleted = false " +
           "ORDER BY m.createdAt DESC")
    List<SharedMedia> findTimelineByConversation(@Param("convId") String conversationId, int offset, int limit);

    @Query("SELECT m FROM SharedMedia m WHERE m.conversationId = :convId AND m.mediaType = :type " +
           "AND m.isDeleted = false ORDER BY m.createdAt DESC")
    List<SharedMedia> findByConversationIdAndType(@Param("convId") String conversationId,
                                                   @Param("type") SharedMedia.MediaType mediaType,
                                                   int offset, int limit);
}
