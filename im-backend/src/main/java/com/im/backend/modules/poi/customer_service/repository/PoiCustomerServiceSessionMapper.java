package com.im.backend.modules.poi.customer_service.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.im.backend.modules.poi.customer_service.entity.PoiCustomerServiceSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 客服会话Mapper
 */
@Mapper
public interface PoiCustomerServiceSessionMapper extends BaseMapper<PoiCustomerServiceSession> {

    /**
     * 查询用户的会话列表
     */
    @Select("SELECT * FROM poi_cs_session WHERE user_id = #{userId} AND deleted = 0 ORDER BY last_message_time DESC")
    List<PoiCustomerServiceSession> selectByUserId(@Param("userId") Long userId);

    /**
     * 查询客服的会话列表
     */
    @Select("SELECT * FROM poi_cs_session WHERE agent_id = #{agentId} AND status = 'ACTIVE' AND deleted = 0 ORDER BY last_message_time DESC")
    List<PoiCustomerServiceSession> selectActiveByAgentId(@Param("agentId") Long agentId);

    /**
     * 根据会话ID查询
     */
    @Select("SELECT * FROM poi_cs_session WHERE session_id = #{sessionId} AND deleted = 0")
    PoiCustomerServiceSession selectBySessionId(@Param("sessionId") String sessionId);

    /**
     * 查询用户的POI活跃会话
     */
    @Select("SELECT * FROM poi_cs_session WHERE user_id = #{userId} AND poi_id = #{poiId} AND status = 'ACTIVE' AND deleted = 0 LIMIT 1")
    PoiCustomerServiceSession selectActiveSessionByUserAndPoi(@Param("userId") Long userId, @Param("poiId") Long poiId);

    /**
     * 增加用户未读数
     */
    @Update("UPDATE poi_cs_session SET user_unread_count = user_unread_count + 1 WHERE session_id = #{sessionId}")
    int incrementUserUnread(@Param("sessionId") String sessionId);

    /**
     * 增加客服未读数
     */
    @Update("UPDATE poi_cs_session SET agent_unread_count = agent_unread_count + 1 WHERE session_id = #{sessionId}")
    int incrementAgentUnread(@Param("sessionId") String sessionId);

    /**
     * 清空用户未读数
     */
    @Update("UPDATE poi_cs_session SET user_unread_count = 0 WHERE session_id = #{sessionId}")
    int clearUserUnread(@Param("sessionId") String sessionId);

    /**
     * 清空客服未读数
     */
    @Update("UPDATE poi_cs_session SET agent_unread_count = 0 WHERE session_id = #{sessionId}")
    int clearAgentUnread(@Param("sessionId") String sessionId);

    /**
     * 更新会话状态
     */
    @Update("UPDATE poi_cs_session SET status = #{status} WHERE session_id = #{sessionId}")
    int updateStatus(@Param("sessionId") String sessionId, @Param("status") String status);

    /**
     * 查询待分配的会话
     */
    @Select("SELECT * FROM poi_cs_session WHERE poi_id = #{poiId} AND status = 'PENDING' AND deleted = 0 ORDER BY create_time ASC")
    List<PoiCustomerServiceSession> selectPendingSessions(@Param("poiId") Long poiId);

    /**
     * 分页查询用户的会话
     */
    Page<PoiCustomerServiceSession> selectPageByUserId(Page<PoiCustomerServiceSession> page, @Param("userId") Long userId);
}
