package com.im.service.message.repository;

import com.im.service.message.entity.MessageReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 消息反应数据访问接口
 * 
 * @author IM Team
 * @version 1.0
 */
@Repository
public interface MessageReactionRepository extends JpaRepository<MessageReaction, String> {

    /**
     * 根据消息ID查询所有反应
     */
    List<MessageReaction> findByMessageId(String messageId);

    /**
     * 根据消息ID和用户ID查询反应
     */
    Optional<MessageReaction> findByMessageIdAndUserId(String messageId, String userId);

    /**
     * 根据消息ID和反应类型查询反应列表
     */
    List<MessageReaction> findByMessageIdAndReactionType(String messageId, String reactionType);

    /**
     * 根据消息ID和反应类型统计数量
     */
    @Query("SELECT COUNT(r) FROM MessageReaction r WHERE r.messageId = :messageId AND r.reactionType = :reactionType")
    Long countByMessageIdAndReactionType(@Param("messageId") String messageId, @Param("reactionType") String reactionType);

    /**
     * 删除指定消息和用户的反应
     */
    void deleteByMessageIdAndUserId(String messageId, String userId);

    /**
     * 删除指定消息、用户和类型的反应
     */
    void deleteByMessageIdAndUserIdAndReactionType(String messageId, String userId, String reactionType);

    /**
     * 统计消息的反应数量
     */
    @Query("SELECT COUNT(r) FROM MessageReaction r WHERE r.messageId = :messageId")
    Long countByMessageId(@Param("messageId") String messageId);

    /**
     * 查询消息的所有反应类型及数量
     */
    @Query("SELECT r.reactionType, COUNT(r) FROM MessageReaction r WHERE r.messageId = :messageId GROUP BY r.reactionType")
    List<Object[]> countByReactionTypeGroup(@Param("messageId") String messageId);
}
