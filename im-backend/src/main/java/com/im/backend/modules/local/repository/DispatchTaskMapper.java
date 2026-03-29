package com.im.backend.modules.local.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.im.backend.modules.local.entity.DispatchTask;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 调度任务Mapper
 */
@Repository
public interface DispatchTaskMapper extends BaseMapper<DispatchTask> {
    
    /**
     * 根据状态查询任务列表
     */
    @Select("SELECT * FROM dispatch_task WHERE status = #{status} AND deleted = 0 ORDER BY create_time DESC")
    List<DispatchTask> selectByStatus(@Param("status") Integer status);
    
    /**
     * 根据服务人员查询任务
     */
    @Select("SELECT * FROM dispatch_task WHERE staff_id = #{staffId} AND status IN (1,2,3) AND deleted = 0 ORDER BY create_time DESC")
    List<DispatchTask> selectActiveByStaffId(@Param("staffId") String staffId);
    
    /**
     * 根据订单ID查询任务
     */
    @Select("SELECT * FROM dispatch_task WHERE order_id = #{orderId} AND deleted = 0 LIMIT 1")
    DispatchTask selectByOrderId(@Param("orderId") String orderId);
    
    /**
     * 更新任务状态
     */
    @Update("UPDATE dispatch_task SET status = #{status}, update_time = NOW() WHERE id = #{taskId}")
    int updateStatus(@Param("taskId") String taskId, @Param("status") Integer status);
    
    /**
     * 分配服务人员
     */
    @Update("UPDATE dispatch_task SET staff_id = #{staffId}, staff_name = #{staffName}, staff_phone = #{staffPhone}, " +
            "status = 1, update_time = NOW() WHERE id = #{taskId}")
    int assignStaff(@Param("taskId") String taskId, @Param("staffId") String staffId, 
                    @Param("staffName") String staffName, @Param("staffPhone") String staffPhone);
    
    /**
     * 根据围栏ID统计订单数
     */
    @Select("SELECT COUNT(*) FROM dispatch_task WHERE geofence_id = #{geofenceId} AND status IN (0,1,2,3) AND deleted = 0")
    Integer countOrdersByGeofenceId(@Param("geofenceId") String geofenceId);
}
