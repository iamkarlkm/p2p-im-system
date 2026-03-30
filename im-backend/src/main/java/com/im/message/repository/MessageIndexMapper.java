package com.im.message.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.message.entity.MessageIndex;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 消息索引Mapper - 全文搜索索引数据访问层
 * 
 * @author IM Development Team
 * @version 1.0
 */
@Mapper
public interface MessageIndexMapper extends BaseMapper<MessageIndex> {
    
    /**
     * 根据关键词搜索消息
     */
    @Select("<script>" +
            "SELECT * FROM im_message_index " +
            "WHERE receiver_id = #{conversationId} " +
            "AND conversation_type = #{conversationType} " +
            "<if test='keyword != null and keyword != \"\"'> " +
            "  AND (full_content LIKE CONCAT('%', #{keyword}, '%') OR keywords LIKE CONCAT('%', #{keyword}, '%')) " +
            "</if>" +
            "ORDER BY send_timestamp DESC " +
            "LIMIT #{limit}" +
            "</script>")
    List<MessageIndex> searchByKeyword(@Param("conversationId") Long conversationId,
                                        @Param("conversationType") Integer conversationType,
                                        @Param("keyword") String keyword,
                                        @Param("limit") Integer limit);
    
    /**
     * 全文搜索消息
     */
    @Select("SELECT * FROM im_message_index " +
            "WHERE MATCH(full_content, keywords) AGAINST(#{keyword} IN BOOLEAN MODE) " +
            "AND receiver_id = #{conversationId} " +
            "ORDER BY send_timestamp DESC LIMIT #{limit}")
    List<MessageIndex> fullTextSearch(@Param("conversationId") Long conversationId,
                                       @Param("keyword") String keyword,
                                       @Param("limit") Integer limit);
    
    /**
     * 根据消息ID删除索引
     */
    @Delete("DELETE FROM im_message_index WHERE message_id = #{messageId}")
    int deleteByMessageId(@Param("messageId") Long messageId);
    
    /**
     * 批量插入索引
     */
    @Insert("<script>" +
            "INSERT INTO im_message_index (message_id, message_uuid, sender_id, receiver_id, " +
            "conversation_type, keywords, keyword_hash, full_content, message_type, " +
            "send_timestamp, send_date, year_month, has_attachment, attachment_types, " +
            "mentioned, mention_user_ids, create_time, update_time) VALUES " +
            "<foreach collection='list' item='item' separator=','> " +
            "(#{item.messageId}, #{item.messageUuid}, #{item.senderId}, #{item.receiverId}, " +
            "#{item.conversationType}, #{item.keywords}, #{item.keywordHash}, #{item.fullContent}, " +
            "#{item.messageType}, #{item.sendTimestamp}, #{item.sendDate}, #{item.yearMonth}, " +
            "#{item.hasAttachment}, #{item.attachmentTypes}, #{item.mentioned}, #{item.mentionUserIds}, " +
            "NOW(), NOW()) " +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("list") List<MessageIndex> indexList);
    
    /**
     * 查询指定日期的消息索引(用于归档查询)
     */
    @Select("SELECT * FROM im_message_index " +
            "WHERE send_date = #{sendDate} " +
            "AND receiver_id = #{conversationId} " +
            "ORDER BY send_timestamp DESC")
    List<MessageIndex> selectBySendDate(@Param("sendDate") Integer sendDate,
                                         @Param("conversationId") Long conversationId);
    
    /**
     * 查询时间范围内的消息索引
     */
    @Select("SELECT * FROM im_message_index " +
            "WHERE send_timestamp &gt;= #{startTime} " +
            "AND send_timestamp &lt;= #{endTime} " +
            "AND receiver_id = #{conversationId} " +
            "ORDER BY send_timestamp DESC LIMIT #{limit}")
    List<MessageIndex> selectByTimeRange(@Param("conversationId") Long conversationId,
                                          @Param("startTime") Long startTime,
                                          @Param("endTime") Long endTime,
                                          @Param("limit") Integer limit);
    
    /**
     * 根据关键词哈希查询
     */
    @Select("SELECT * FROM im_message_index " +
            "WHERE keyword_hash = #{hash} " +
            "AND receiver_id = #{conversationId} LIMIT 100")
    List<MessageIndex> selectByKeywordHash(@Param("conversationId") Long conversationId,
                                            @Param("hash") Long hash);
    
    /**
     * 查询包含附件的消息索引
     */
    @Select("SELECT * FROM im_message_index " +
            "WHERE has_attachment = 1 " +
            "AND receiver_id = #{conversationId} " +
            "AND conversation_type = #{conversationType} " +
            "ORDER BY send_timestamp DESC LIMIT #{limit}")
    List<MessageIndex> selectWithAttachment(@Param("conversationId") Long conversationId,
                                             @Param("conversationType") Integer conversationType,
                                             @Param("limit") Integer limit);
}
