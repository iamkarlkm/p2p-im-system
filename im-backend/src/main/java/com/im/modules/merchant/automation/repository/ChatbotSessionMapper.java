package com.im.modules.merchant.automation.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.modules.merchant.automation.entity.ChatbotSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 智能客服会话数据访问层
 */
@Mapper
public interface ChatbotSessionMapper extends BaseMapper<ChatbotSession> {
    
    /**
     * 根据商户ID和用户ID查询活跃会话
     */
    @Select("SELECT * FROM chatbot_session WHERE merchant_id = #{merchantId} AND user_id = #{userId} " +
            "AND status IN (0, 1, 2, 3) AND deleted = 0 ORDER BY create_time DESC LIMIT 1")
    ChatbotSession findActiveSession(@Param("merchantId") String merchantId, @Param("userId") String userId);
    
    /**
     * 查询商户待转人工的会话
     */
    @Select("SELECT * FROM chatbot_session WHERE merchant_id = #{merchantId} AND status = 2 AND deleted = 0 " +
            "ORDER BY transfer_time ASC")
    List<ChatbotSession> findPendingTransferSessions(@Param("merchantId") String merchantId);
    
    /**
     * 更新会话状态
     */
    @Update("UPDATE chatbot_session SET status = #{status}, update_time = NOW() WHERE session_id = #{sessionId}")
    int updateStatus(@Param("sessionId") String sessionId, @Param("status") int status);
    
    /**
     * 关闭超时会话
     */
    @Update("UPDATE chatbot_session SET status = 6, end_time = NOW(), update_time = NOW() " +
            "WHERE status IN (0, 1) AND update_time < #{timeoutTime} AND deleted = 0")
    int closeTimeoutSessions(@Param("timeoutTime") LocalDateTime timeoutTime);
    
    /**
     * 增加消息计数
     */
    @Update("UPDATE chatbot_session SET message_count = message_count + 1, update_time = NOW() WHERE session_id = #{sessionId}")
    int incrementMessageCount(@Param("sessionId") String sessionId);
}
