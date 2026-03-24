package com.im.backend.repository;

import com.im.backend.entity.AtMention;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * @提及数据访问层
 */
@Repository
public interface AtMentionRepository extends JpaRepository<AtMention, Long> {

    /** 查询用户的所有@提及记录 */
    Page<AtMention> findByMentionedUserIdOrderByMentionedAtDesc(Long mentionedUserId, Pageable pageable);

    /** 查询用户未读的@提及数量 */
    long countByMentionedUserIdAndIsReadFalse(Long mentionedUserId);

    /** 查询用户某群聊的未读@提及数量 */
    long countByMentionedUserIdAndRoomIdAndIsReadFalse(Long mentionedUserId, Long roomId);

    /** 查询群聊的所有@提及记录 */
    Page<AtMention> findByRoomIdOrderByMentionedAtDesc(Long roomId, Pageable pageable);

    /** 批量标记为已读 */
    @Modifying
    @Query("UPDATE AtMention m SET m.isRead = true WHERE m.mentionedUserId = :userId AND m.id IN :ids")
    int markAsRead(@Param("userId") Long userId, @Param("ids") List<Long> ids);

    /** 标记所有为已读 */
    @Modifying
    @Query("UPDATE AtMention m SET m.isRead = true WHERE m.mentionedUserId = :userId AND m.roomId = :roomId AND m.isRead = false")
    int markAllAsReadInRoom(@Param("userId") Long userId, @Param("roomId") Long roomId);

    /** 查询用户最近的@提及（分页） */
    @Query("SELECT m FROM AtMention m WHERE m.mentionedUserId = :userId ORDER BY m.mentionedAt DESC")
    Page<AtMention> findRecentMentions(@Param("userId") Long userId, Pageable pageable);

    /** 查询某消息的所有@提及 */
    List<AtMention> findByMessageId(Long messageId);

    /** 删除消息的所有@提及 */
    @Modifying
    @Query("DELETE FROM AtMention m WHERE m.messageId = :messageId")
    void deleteByMessageId(@Param("messageId") Long messageId);

    /** 获取用户未读@提及列表 */
    List<AtMention> findByMentionedUserIdAndIsReadFalseOrderByMentionedAtDesc(Long mentionedUserId);
}
