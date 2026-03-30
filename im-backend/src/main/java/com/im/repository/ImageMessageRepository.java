package com.im.repository;

import com.im.entity.ConversationType;
import com.im.entity.ImageMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 图片消息数据访问层
 * 功能#24: 图片消息
 */
@Repository
public interface ImageMessageRepository extends JpaRepository<ImageMessage, Long> {

    List<ImageMessage> findBySenderId(Long senderId);

    List<ImageMessage> findByReceiverIdAndConversationType(Long receiverId, ConversationType conversationType);

    List<ImageMessage> findByGroupIdAndConversationType(Long groupId, ConversationType conversationType);

    Optional<ImageMessage> findByMessageId(Long messageId);

    List<ImageMessage> findBySenderIdAndSendTimeBetween(Long senderId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT im FROM ImageMessage im WHERE im.senderId = ?1 ORDER BY im.sendTime DESC")
    List<ImageMessage> findRecentImagesBySenderId(Long senderId);

    Long countBySenderIdAndSendTimeAfter(Long senderId, LocalDateTime since);

    List<ImageMessage> findByImageType(String imageType);
}
