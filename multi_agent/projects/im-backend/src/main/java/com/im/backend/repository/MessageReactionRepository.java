package com.im.backend.repository;

import com.im.backend.entity.MessageReactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageReactionRepository extends JpaRepository<MessageReactionEntity, Long> {

    /** 获取消息的所有表情反应 */
    List<MessageReactionEntity> findByMessageId(Long messageId);

    /** 统计各表情的数量 */
    @Query("SELECT r.emoji, COUNT(r) FROM MessageReactionEntity r WHERE r.messageId = :messageId GROUP BY r.emoji")
    List<Object[]> countByMessageIdGroupByEmoji(@Param("messageId") Long messageId);

    /** 用户是否已添加某表情 */
    boolean existsByMessageIdAndUserIdAndEmoji(Long messageId, Long userId, String emoji);

    /** 删除用户对某消息的某个表情反应 */
    void deleteByMessageIdAndUserIdAndEmoji(Long messageId, Long userId, String emoji);

    /** 批量查询消息的反应统计 */
    @Query("SELECT r.messageId, r.emoji, COUNT(r) FROM MessageReactionEntity r WHERE r.messageId IN :messageIds GROUP BY r.messageId, r.emoji")
    List<Object[]> countByMessageIdsGroupByEmoji(@Param("messageIds") List<Long> messageIds);

    /** 查询用户对某消息的所有反应 */
    List<MessageReactionEntity> findByMessageIdAndUserId(Long messageId, Long userId);
}
