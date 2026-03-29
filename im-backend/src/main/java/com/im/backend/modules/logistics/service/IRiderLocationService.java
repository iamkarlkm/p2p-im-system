package com.im.backend.modules.logistics.service;

import com.im.backend.modules.logistics.dto.*;
import com.im.backend.modules.logistics.entity.RiderLocationTrace;

import java.util.List;

/**
 * 骑手位置服务接口
 */
public interface IRiderLocationService {

    /**
     * 上报骑手位置
     */
    boolean reportLocation(RiderLocationReportRequest request);

    /**
     * 批量上报位置
     */
    boolean batchReportLocations(List<RiderLocationReportRequest> requests);

    /**
     * 获取骑手当前位置
     */
    RiderLocationResponse getRiderCurrentLocation(Long riderId);

    /**
     * 获取订单配送轨迹
     */
    List<RiderLocationTrace> getOrderDeliveryTrace(Long orderId);

    /**
     * 获取附近骑手
     */
    List<RiderLocationResponse> getNearbyRiders(Double longitude, Double latitude, Double radius);

    /**
     * 更新骑手在线状态
     */
    boolean updateRiderWorkStatus(Long riderId, Integer workStatus);
}
