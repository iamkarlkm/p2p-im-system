package com.im.modules.merchant.automation.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.modules.merchant.automation.entity.ChatbotMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 智能客服消息数据访问层
 */
@Mapper
public interface ChatbotMessageMapper extends BaseMapper<ChatbotMessage> {
    
    /**
     * 查询会话消息列表
     */
    @Select("SELECT * FROM chatbot_message WHERE session_id = #{sessionId} AND deleted = 0 " +
            "ORDER BY create_time ASC LIMIT #{limit}")
    List<ChatbotMessage> findBySessionId(@Param("sessionId") String sessionId, @Param("limit") int limit);
    
    /**
     * 查询会话最新消息
     */
    @Select("SELECT * FROM chatbot_message WHERE session_id = #{sessionId} AND deleted = 0 " +
            "ORDER BY create_time DESC LIMIT 1")
    ChatbotMessage findLatestMessage(@Param("sessionId") String sessionId);
    
    /**
     * 查询未读消息数
     */
    @Select("SELECT COUNT(*) FROM chatbot_message WHERE session_id = #{sessionId} AND sender_type = 1 " +
            "AND is_read = 0 AND deleted = 0")
    int countUnreadMessages(@Param("sessionId") String sessionId);
    
    /**
     * 标记消息为已读
     */
    @Update("UPDATE chatbot_message SET is_read = 1, read_time = NOW() WHERE session_id = #{sessionId} " +
            "AND sender_type = 1 AND is_read = 0 AND deleted = 0")
    int markAsRead(@Param("sessionId") String sessionId);
    
    /**
     * 查询用户最新消息上下文
     */
    @Select("SELECT * FROM chatbot_message WHERE session_id = #{sessionId} AND sender_type = 1 AND deleted = 0 " +
            "ORDER BY create_time DESC LIMIT #{contextSize}")
    List<ChatbotMessage> findUserContextMessages(@Param("sessionId") String sessionId, @Param("contextSize") int contextSize);
}
