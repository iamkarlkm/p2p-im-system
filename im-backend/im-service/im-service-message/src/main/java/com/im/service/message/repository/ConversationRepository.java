package com.im.service.message.repository;

import com.im.service.message.entity.Conversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, String> {

    Optional<Conversation> findByIdAndDeletedFalse(String id);

    List<Conversation> findByCreatorIdOrderByUpdatedAtDesc(String creatorId, Pageable pageable);

    Page<Conversation> findByTypeAndDeletedFalseOrderByUpdatedAtDesc(String type, Pageable pageable);

    @Modifying
    @Query("UPDATE Conversation c SET c.lastMessageId = :messageId, c.lastMessageAt = :now, c.updatedAt = :now WHERE c.id = :id")
    int updateLastMessage(@Param("id") String id, @Param("messageId") String messageId, @Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE Conversation c SET c.memberCount = c.memberCount + :delta, c.updatedAt = :now WHERE c.id = :id")
    int updateMemberCount(@Param("id") String id, @Param("delta") int delta, @Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE Conversation c SET c.deleted = true, c.updatedAt = :now WHERE c.id = :id")
    int softDelete(@Param("id") String id, @Param("now") LocalDateTime now);
}
