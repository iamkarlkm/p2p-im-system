package com.im.backend.modules.merchant.assistant.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.merchant.assistant.entity.CustomerServiceMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 客服消息Mapper
 */
@Mapper
public interface CustomerServiceMessageMapper extends BaseMapper<CustomerServiceMessage> {
    
    /**
     * 查询会话的消息列表
     */
    @Select("SELECT * FROM customer_service_message WHERE session_id = #{sessionId} ORDER BY create_time ASC")
    List<CustomerServiceMessage> selectBySessionId(@Param("sessionId") String sessionId);
    
    /**
     * 查询会话的最新N条消息
     */
    @Select("SELECT * FROM customer_service_message WHERE session_id = #{sessionId} ORDER BY create_time DESC LIMIT #{limit}")
    List<CustomerServiceMessage> selectRecentMessages(@Param("sessionId") String sessionId, @Param("limit") int limit);
}
