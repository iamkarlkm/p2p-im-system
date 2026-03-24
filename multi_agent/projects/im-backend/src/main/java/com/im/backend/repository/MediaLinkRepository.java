package com.im.backend.repository;

import com.im.backend.entity.MediaLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MediaLinkRepository extends JpaRepository<MediaLink, Long> {
    
    @Query("SELECT l FROM MediaLink l WHERE l.conversationId = :convId ORDER BY l.createdAt DESC")
    List<MediaLink> findByConversationId(@Param("convId") String conversationId, int offset, int limit);

    @Query("SELECT COUNT(l) FROM MediaLink l WHERE l.conversationId = :convId")
    Long countByConversationId(@Param("convId") String conversationId);

    List<MediaLink> findByConversationIdOrderByCreatedAtDesc(String conversationId);
}
