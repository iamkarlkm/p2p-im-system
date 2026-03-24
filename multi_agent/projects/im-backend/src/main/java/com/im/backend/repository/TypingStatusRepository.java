package com.im.backend.repository;

import com.im.backend.entity.TypingStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TypingStatusRepository extends JpaRepository<TypingStatusEntity, Long> {

    /** 查询会话中所有有效Typing状态 */
    @Query("SELECT t FROM TypingStatusEntity t WHERE t.conversationId = :convId AND t.expiresAt > :now")
    List<TypingStatusEntity> findActiveByConversation(@Param("convId") String conversationId, @Param("now") LocalDateTime now);

    /** 查询某用户在某会话中的Typing状态 */
    Optional<TypingStatusEntity> findByConversationIdAndUserId(String conversationId, String userId);

    /** 清理过期状态 */
    @Modifying
    @Query("DELETE FROM TypingStatusEntity t WHERE t.expiresAt <= :now")
    int deleteExpired(@Param("now") LocalDateTime now);

    /** 删除某用户所有Typing状态 */
    @Modifying
    @Query("DELETE FROM TypingStatusEntity t WHERE t.userId = :userId")
    int deleteByUserId(@Param("userId") String userId);
}
