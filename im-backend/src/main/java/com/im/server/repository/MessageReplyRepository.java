package com.im.server.repository;

import com.im.server.entity.MessageReply;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 消息引用回复 Repository
 */
@Repository
public interface MessageReplyRepository extends JpaRepository<MessageReply, Long> {

    /**
     * 查找原消息的所有直接回复（按时间升序）
     */
    List<MessageReply> findByOriginalMsgIdAndDeletedOrderByCreateTimeAsc(String originalMsgId, Integer deleted);

    /**
     * 查找原消息的直接回复（按时间降序）
     */
    List<MessageReply> findByOriginalMsgIdAndDeletedOrderByCreateTimeDesc(String originalMsgId, Integer deleted);

    /**
     * 查找某用户对某消息的回复
     */
    List<MessageReply> findByOriginalMsgIdAndReplyUserIdOrderByCreateTimeDesc(String originalMsgId, Long replyUserId);

    /**
     * 查询某消息的引用链（按聊天和创建时间排序）
     */
    List<MessageReply> findByChatTypeAndChatIdAndDeletedOrderByCreateTimeDesc(
            Integer chatType, Long chatId, Integer deleted);

    /**
     * 查找用户最近的回复
     */
    List<MessageReply> findTopByReplyUserIdOrderByCreateTimeDesc(Long replyUserId, PageRequest pageRequest);

    /**
     * 检查消息是否被引用过
     */
    boolean existsByOriginalMsgId(String originalMsgId);

    /**
     * 获取某消息的最大引用深度
     */
    @Query("SELECT MAX(r.replyDepth) FROM MessageReply r WHERE r.originalMsgId = :originalMsgId")
    Integer findMaxDepthByOriginalMsgId(@Param("originalMsgId") String originalMsgId);

    /**
     * 统计某消息的总回复数
     */
    long countByOriginalMsgIdAndDeleted(String originalMsgId, Integer deleted);

    /**
     * 删除某消息的所有回复（硬删除）
     */
    void deleteByOriginalMsgId(String originalMsgId);
}
