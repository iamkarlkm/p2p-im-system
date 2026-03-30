package com.im.repository;

import com.im.entity.ConversationType;
import com.im.entity.EmojiMessage;
import com.im.entity.EmojiType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 表情消息数据访问层
 * 功能#23: 表情消息
 */
@Repository
public interface EmojiMessageRepository extends JpaRepository<EmojiMessage, Long> {

    List<EmojiMessage> findBySenderId(Long senderId);

    List<EmojiMessage> findByReceiverIdAndConversationType(Long receiverId, ConversationType conversationType);

    List<EmojiMessage> findByGroupIdAndConversationType(Long groupId, ConversationType conversationType);

    List<EmojiMessage> findBySenderIdAndSendTimeBetween(Long senderId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT em.emojiCode, COUNT(em) as cnt FROM EmojiMessage em " +
           "WHERE em.senderId = ?1 GROUP BY em.emojiCode ORDER BY cnt DESC")
    List<Object[]> findTopEmojisBySenderId(Long senderId);

    List<EmojiMessage> findByEmojiType(EmojiType emojiType);

    List<EmojiMessage> findByEmojiCategory(String category);

    Long countBySenderIdAndSendTimeAfter(Long senderId, LocalDateTime since);
}
