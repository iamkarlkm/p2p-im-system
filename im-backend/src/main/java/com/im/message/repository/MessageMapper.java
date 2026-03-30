package com.im.message.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.im.message.entity.Message;
import com.im.message.dto.MessageQueryRequest;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息Mapper - 消息存储与检索数据访问层
 * 
 * @author IM Development Team
 * @version 1.0
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {
    
    /**
     * 查询会话历史消息
     */
    @Select("<script>" +
            "SELECT * FROM im_message " +
            "WHERE conversation_type = #{conversationType} " +
            "AND receiver_id = #{conversationId} " +
            "AND deleted = 0 " +
            "<if test='includeRecalled == false'> AND recalled = 0 </if>" +
            "<if test='cursor != null and forward == true'> AND id &gt; #{cursor} </if>" +
            "<if test='cursor != null and forward == false'> AND id &lt; #{cursor} </if>" +
            "<if test='startTime != null'> AND send_time &gt;= #{startTime} </if>" +
            "<if test='endTime != null'> AND send_time &lt;= #{endTime} </if>" +
            "<if test='senderId != null'> AND sender_id = #{senderId} </if>" +
            "<if test='messageTypes != null and messageTypes.size > 0'> " +
            "  AND message_type IN " +
            "  <foreach collection='messageTypes' item='type' open='(' separator=',' close=')'> #{type} </foreach> " +
            "</if>" +
            "ORDER BY id ${forward ? 'ASC' : 'DESC'} " +
            "LIMIT #{pageSize}" +
            "</script>")
    List<Message> selectConversationMessages(@Param("conversationType") Integer conversationType,
                                              @Param("conversationId") Long conversationId,
                                              @Param("cursor") Long cursor,
                                              @Param("forward") Boolean forward,
                                              @Param("pageSize") Integer pageSize,
                                              @Param("includeRecalled") Boolean includeRecalled,
                                              @Param("startTime") LocalDateTime startTime,
                                              @Param("endTime") LocalDateTime endTime,
                                              @Param("senderId") Long senderId,
                                              @Param("messageTypes") List<Integer> messageTypes);
    
    /**
     * 分页查询消息
     */
    @Select("<script>" +
            "SELECT * FROM im_message " +
            "WHERE conversation_type = #{conversationType} " +
            "AND receiver_id = #{conversationId} " +
            "AND deleted = 0 AND recalled = 0 " +
            "ORDER BY id DESC" +
            "</script>")
    IPage<Message> selectMessagePage(Page<Message> page, 
                                      @Param("conversationType") Integer conversationType,
                                      @Param("conversationId") Long conversationId);
    
    /**
     * 根据消息UUID查询
     */
    @Select("SELECT * FROM im_message WHERE message_id = #{messageUuid} AND deleted = 0 LIMIT 1")
    Message selectByMessageUuid(@Param("messageUuid") String messageUuid);
    
    /**
     * 批量查询消息
     */
    @Select("<script>" +
            "SELECT * FROM im_message WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'> #{id} </foreach> " +
            "AND deleted = 0" +
            "</script>")
    List<Message> selectBatchByIds(@Param("ids") List<Long> ids);
    
    /**
     * 查询@我的消息
     */
    @Select("SELECT * FROM im_message " +
            "WHERE receiver_id = #{conversationId} " +
            "AND conversation_type = #{conversationType} " +
            "AND (mention_user_ids LIKE CONCAT('%', #{userId}, '%') OR mention_all = 1) " +
            "AND deleted = 0 AND recalled = 0 " +
            "ORDER BY id DESC LIMIT #{limit}")
    List<Message> selectMentionMessages(@Param("conversationId") Long conversationId,
                                         @Param("conversationType") Integer conversationType,
                                         @Param("userId") Long userId,
                                         @Param("limit") Integer limit);
    
    /**
     * 查询包含附件的消息
     */
    @Select("SELECT * FROM im_message " +
            "WHERE receiver_id = #{conversationId} " +
            "AND conversation_type = #{conversationType} " +
            "AND attachments IS NOT NULL AND attachments != '' " +
            "AND deleted = 0 AND recalled = 0 " +
            "ORDER BY id DESC LIMIT #{limit}")
    List<Message> selectMessagesWithAttachment(@Param("conversationId") Long conversationId,
                                                @Param("conversationType") Integer conversationType,
                                                @Param("limit") Integer limit);
    
    /**
     * 标记消息为已读
     */
    @Update("UPDATE im_message SET status = 3, read_time = NOW() " +
            "WHERE id = #{messageId} AND status &lt; 3 AND deleted = 0")
    int markAsRead(@Param("messageId") Long messageId);
    
    /**
     * 批量标记已读
     */
    @Update("<script>" +
            "UPDATE im_message SET status = 3, read_time = NOW() " +
            "WHERE id IN " +
            "<foreach collection='messageIds' item='id' open='(' separator=',' close=')'> #{id} </foreach> " +
            "AND status &lt; 3 AND deleted = 0" +
            "</script>")
    int batchMarkAsRead(@Param("messageIds") List<Long> messageIds);
    
    /**
     * 撤回消息
     */
    @Update("UPDATE im_message SET recalled = 1, recall_time = NOW(), recall_by = #{operatorId} " +
            "WHERE id = #{messageId} AND recalled = 0 AND deleted = 0")
    int recallMessage(@Param("messageId") Long messageId, @Param("operatorId") Long operatorId);
    
    /**
     * 查询会话最新消息ID
     */
    @Select("SELECT MAX(id) FROM im_message " +
            "WHERE receiver_id = #{conversationId} " +
            "AND conversation_type = #{conversationType} " +
            "AND deleted = 0")
    Long selectLatestMessageId(@Param("conversationType") Integer conversationType,
                                @Param("conversationId") Long conversationId);
    
    /**
     * 查询会话未读消息数量
     */
    @Select("SELECT COUNT(*) FROM im_message " +
            "WHERE receiver_id = #{conversationId} " +
            "AND conversation_type = #{conversationType} " +
            "AND status &lt; 3 AND deleted = 0")
    int countUnreadMessages(@Param("conversationType") Integer conversationType,
                            @Param("conversationId") Long conversationId);
    
    /**
     * 查询某个时间点之后的消息数量
     */
    @Select("SELECT COUNT(*) FROM im_message " +
            "WHERE receiver_id = #{conversationId} " +
            "AND conversation_type = #{conversationType} " +
            "AND send_time &gt; #{sinceTime} AND deleted = 0")
    int countMessagesSince(@Param("conversationType") Integer conversationType,
                           @Param("conversationId") Long conversationId,
                           @Param("sinceTime") LocalDateTime sinceTime);
}
