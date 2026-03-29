package com.im.backend.modules.merchant.assistant.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.merchant.assistant.entity.CustomerServiceSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 客服会话Mapper
 */
@Mapper
public interface CustomerServiceSessionMapper extends BaseMapper<CustomerServiceSession> {
    
    /**
     * 根据会话ID查询
     */
    @Select("SELECT * FROM customer_service_session WHERE session_id = #{sessionId}")
    CustomerServiceSession selectBySessionId(@Param("sessionId") String sessionId);
    
    /**
     * 查询用户的活跃会话
     */
    @Select("SELECT * FROM customer_service_session WHERE user_id = #{userId} AND session_status != 'ENDED' ORDER BY create_time DESC LIMIT 1")
    CustomerServiceSession selectActiveSessionByUser(@Param("userId") Long userId);
    
    /**
     * 查询商户的待处理会话
     */
    @Select("SELECT * FROM customer_service_session WHERE merchant_id = #{merchantId} AND session_status IN ('BOT', 'QUEUE') ORDER BY create_time ASC")
    List<CustomerServiceSession> selectPendingSessions(@Param("merchantId") Long merchantId);
    
    /**
     * 更新会话状态
     */
    @Update("UPDATE customer_service_session SET session_status = #{status}, update_time = NOW() WHERE session_id = #{sessionId}")
    int updateStatus(@Param("sessionId") String sessionId, @Param("status") String status);
}
