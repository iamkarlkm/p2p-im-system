package com.im.backend.modules.poi.customer_service.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.poi.customer_service.entity.PoiCustomerService;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 商家客服Mapper
 */
@Mapper
public interface PoiCustomerServiceMapper extends BaseMapper<PoiCustomerService> {

    /**
     * 查询POI的在线客服列表
     */
    @Select("SELECT * FROM poi_customer_service WHERE poi_id = #{poiId} AND status = 'ONLINE' AND enabled = 1 AND deleted = 0")
    List<PoiCustomerService> selectOnlineAgentsByPoiId(@Param("poiId") Long poiId);

    /**
     * 查询POI的所有客服
     */
    @Select("SELECT * FROM poi_customer_service WHERE poi_id = #{poiId} AND enabled = 1 AND deleted = 0 ORDER BY current_sessions ASC")
    List<PoiCustomerService> selectAgentsByPoiId(@Param("poiId") Long poiId);

    /**
     * 增加客服当前会话数
     */
    @Update("UPDATE poi_customer_service SET current_sessions = current_sessions + 1 WHERE id = #{agentId}")
    int incrementSessionCount(@Param("agentId") Long agentId);

    /**
     * 减少客服当前会话数
     */
    @Update("UPDATE poi_customer_service SET current_sessions = current_sessions - 1 WHERE id = #{agentId} AND current_sessions > 0")
    int decrementSessionCount(@Param("agentId") Long agentId);

    /**
     * 根据客服用户ID查询
     */
    @Select("SELECT * FROM poi_customer_service WHERE agent_user_id = #{userId} AND deleted = 0")
    PoiCustomerService selectByAgentUserId(@Param("userId") Long userId);
}
