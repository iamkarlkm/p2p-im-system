package com.im.server.reaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageReactionRepository extends JpaRepository<MessageReaction, String> {

    List<MessageReaction> findByMessageId(String messageId);

    List<MessageReaction> findByMessageIdAndEmoji(String messageId, String emoji);

    void deleteByMessageIdAndUserIdAndEmoji(String messageId, String userId, String emoji);

    long countByMessageIdAndEmoji(String messageId, String emoji);

    @Query("SELECT r.emoji, COUNT(r) FROM MessageReaction r WHERE r.messageId = :messageId GROUP BY r.emoji")
    List<Object[]> countGroupByEmoji(@Param("messageId") String messageId);

    boolean existsByMessageIdAndUserIdAndEmoji(String messageId, String userId, String emoji);
}
