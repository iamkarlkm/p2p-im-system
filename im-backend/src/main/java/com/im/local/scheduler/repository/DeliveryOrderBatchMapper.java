package com.im.local.scheduler.repository;

import com.im.local.scheduler.entity.DeliveryOrderBatch;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 配送批次数据访问层
 */
@Mapper
public interface DeliveryOrderBatchMapper {
    
    @Insert("INSERT INTO delivery_order_batch (batch_no, geofence_id, geofence_name, " +
            "staff_id, order_ids, order_count, estimated_total_distance, " +
            "estimated_total_time, optimal_route, status, created_at, updated_at) " +
            "VALUES (#{batchNo}, #{geofenceId}, #{geofenceName}, " +
            "#{staffId}, #{orderIds}, #{orderCount}, #{estimatedTotalDistance}, " +
            "#{estimatedTotalTime}, #{optimalRoute}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "batchId")
    int insert(DeliveryOrderBatch batch);
    
    @Select("SELECT * FROM delivery_order_batch WHERE batch_id = #{batchId}")
    DeliveryOrderBatch selectById(Long batchId);
    
    @Select("SELECT * FROM delivery_order_batch WHERE batch_no = #{batchNo}")
    DeliveryOrderBatch selectByBatchNo(String batchNo);
    
    @Select("SELECT * FROM delivery_order_batch WHERE staff_id = #{staffId} " +
            "AND status IN (1, 2, 3) ORDER BY created_at DESC")
    List<DeliveryOrderBatch> selectActiveByStaffId(Long staffId);
    
    @Select("SELECT * FROM delivery_order_batch WHERE geofence_id = #{geofenceId} " +
            "AND status = 0 ORDER BY created_at ASC")
    List<DeliveryOrderBatch> selectPendingByGeofenceId(Long geofenceId);
    
    @Select("SELECT * FROM delivery_order_batch WHERE status = #{status} ORDER BY created_at DESC")
    List<DeliveryOrderBatch> selectByStatus(Integer status);
    
    @Update("UPDATE delivery_order_batch SET " +
            "staff_id = #{staffId}, " +
            "status = 1, " +
            "assigned_at = NOW(), " +
            "updated_at = NOW() " +
            "WHERE batch_id = #{batchId}")
    int assignStaff(@Param("batchId") Long batchId, @Param("staffId") Long staffId);
    
    @Update("UPDATE delivery_order_batch SET " +
            "status = #{status}, " +
            "updated_at = NOW() " +
            "WHERE batch_id = #{batchId}")
    int updateStatus(@Param("batchId") Long batchId, @Param("status") Integer status);
    
    @Update("UPDATE delivery_order_batch SET " +
            "status = 4, " +
            "completed_at = NOW(), " +
            "actual_distance = #{actualDistance}, " +
            "actual_time = #{actualTime}, " +
            "updated_at = NOW() " +
            "WHERE batch_id = #{batchId}")
    int completeBatch(@Param("batchId") Long batchId, 
                      @Param("actualDistance") Integer actualDistance,
                      @Param("actualTime") Integer actualTime);
    
    @Select("SELECT COUNT(*) FROM delivery_order_batch WHERE status = 0")
    int countPending();
    
    @Select("SELECT COUNT(*) FROM delivery_order_batch WHERE status = 4 " +
            "AND DATE(created_at) = CURDATE()")
    int countCompletedToday();
}
