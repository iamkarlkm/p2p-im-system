package com.im.location.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.location.entity.GeofenceArea;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 地理围栏 Mapper
 */
public interface GeofenceAreaMapper extends BaseMapper<GeofenceArea> {
    
    /**
     * 根据围栏ID查询
     */
    @Select("SELECT * FROM geofence_area WHERE geofence_id = #{geofenceId}")
    GeofenceArea selectByGeofenceId(@Param("geofenceId") String geofenceId);
    
    /**
     * 查询会话的所有围栏
     */
    @Select("SELECT * FROM geofence_area WHERE session_id = #{sessionId} AND status = 1")
    List<GeofenceArea> selectBySessionId(@Param("sessionId") String sessionId);
    
    /**
     * 查询商户的所有围栏
     */
    @Select("SELECT * FROM geofence_area WHERE merchant_id = #{merchantId} AND status = 1")
    List<GeofenceArea> selectByMerchantId(@Param("merchantId") Long merchantId);
    
    /**
     * 查询指定用途的围栏
     */
    @Select("SELECT * FROM geofence_area WHERE session_id = #{sessionId} AND purpose = #{purpose} AND status = 1")
    List<GeofenceArea> selectByPurpose(@Param("sessionId") String sessionId, @Param("purpose") Integer purpose);
}
