package com.im.backend.modules.local.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.local.entity.ResourceDispatch;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 资源调度Mapper
 */
@Repository
public interface ResourceDispatchMapper extends BaseMapper<ResourceDispatch> {
    
    /**
     * 根据围栏ID查询调度记录
     */
    @Select("SELECT * FROM resource_dispatch WHERE geofence_id = #{geofenceId} AND deleted = 0 ORDER BY create_time DESC")
    List<ResourceDispatch> selectByGeofenceId(@Param("geofenceId") String geofenceId);
    
    /**
     * 根据服务人员ID查询调度记录
     */
    @Select("SELECT * FROM resource_dispatch WHERE staff_id = #{staffId} AND deleted = 0 ORDER BY create_time DESC")
    List<ResourceDispatch> selectByStaffId(@Param("staffId") String staffId);
    
    /**
     * 查询进行中的调度
     */
    @Select("SELECT * FROM resource_dispatch WHERE staff_id = #{staffId} AND status = 1 AND deleted = 0 LIMIT 1")
    ResourceDispatch selectActiveByStaffId(@Param("staffId") String staffId);
}
