package com.im.backend.modules.merchant.dispatch.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.merchant.dispatch.entity.DispatchTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 调度任务Mapper
 * Feature #309: Instant Delivery Capacity Dispatch
 * 
 * @author IM Development Team
 * @since 2026-03-30
 */
@Mapper
public interface DispatchTaskMapper extends BaseMapper<DispatchTask> {

    /**
     * 查询待分配任务
     * 
     * @return 任务列表
     */
    @Select("SELECT * FROM dispatch_task WHERE dispatch_status = 0 AND deleted = 0 ORDER BY priority DESC, create_time ASC")
    List<DispatchTask> selectPendingTasks();

    /**
     * 查询骑手的任务列表
     * 
     * @param riderId 骑手ID
     * @param status  状态
     * @return 任务列表
     */
    @Select("SELECT * FROM dispatch_task WHERE rider_id = #{riderId} AND dispatch_status = #{status} AND deleted = 0 ORDER BY create_time DESC")
    List<DispatchTask> selectRiderTasks(@Param("riderId") Long riderId, @Param("status") Integer status);

    /**
     * 查询商户的调度任务
     * 
     * @param merchantId 商户ID
     * @return 任务列表
     */
    @Select("SELECT * FROM dispatch_task WHERE merchant_id = #{merchantId} AND deleted = 0 ORDER BY create_time DESC")
    List<DispatchTask> selectMerchantTasks(@Param("merchantId") Long merchantId);

    /**
     * 分配骑手
     * 
     * @param taskId  任务ID
     * @param riderId 骑手ID
     * @return 影响行数
     */
    @Update("UPDATE dispatch_task SET rider_id = #{riderId}, dispatch_status = 1, dispatch_time = NOW(), update_time = NOW() WHERE id = #{taskId} AND dispatch_status = 0")
    int assignRider(@Param("taskId") Long taskId, @Param("riderId") Long riderId);

    /**
     * 骑手接单
     * 
     * @param taskId  任务ID
     * @param riderId 骑手ID
     * @return 影响行数
     */
    @Update("UPDATE dispatch_task SET dispatch_status = 2, accept_time = NOW(), update_time = NOW() WHERE id = #{taskId} AND rider_id = #{riderId} AND dispatch_status = 1")
    int acceptTask(@Param("taskId") Long taskId, @Param("riderId") Long riderId);

    /**
     * 完成任务
     * 
     * @param taskId 任务ID
     * @return 影响行数
     */
    @Update("UPDATE dispatch_task SET dispatch_status = 4, complete_time = NOW(), update_time = NOW() WHERE id = #{taskId}")
    int completeTask(@Param("taskId") Long taskId);

    /**
     * 取消任务
     * 
     * @param taskId 任务ID
     * @param reason 原因
     * @return 影响行数
     */
    @Update("UPDATE dispatch_task SET dispatch_status = 5, update_time = NOW() WHERE id = #{taskId}")
    int cancelTask(@Param("taskId") Long taskId);
}
