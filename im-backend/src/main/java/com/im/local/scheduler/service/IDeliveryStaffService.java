package com.im.local.scheduler.service;

import com.im.local.scheduler.dto.*;
import com.im.local.scheduler.entity.DeliveryStaff;
import java.util.List;

/**
 * 骑手服务接口
 */
public interface IDeliveryStaffService {
    
    /**
     * 注册骑手
     */
    StaffResponse registerStaff(RegisterStaffRequest request);
    
    /**
     * 根据ID查询骑手
     */
    StaffResponse getStaffById(Long staffId);
    
    /**
     * 更新骑手位置
     */
    boolean updateStaffLocation(UpdateStaffLocationRequest request);
    
    /**
     * 更新骑手状态
     */
    boolean updateStaffStatus(Long staffId, Integer status);
    
    /**
     * 查询围栏内空闲骑手
     */
    List<StaffResponse> findIdleStaffInGeofence(Long geofenceId);
    
    /**
     * 获取附近可派单骑手
     */
    List<StaffResponse> findAvailableStaffNearby(Double lng, Double lat, Integer radius);
    
    /**
     * 更新骑手订单统计
     */
    boolean updateStaffOrderStats(Long staffId, Integer completedOrders);
    
    /**
     * 批量更新骑手当前围栏
     */
    void batchUpdateStaffGeofence();
    
    /**
     * 查询所有骑手列表
     */
    List<StaffResponse> listAllStaff();
}
