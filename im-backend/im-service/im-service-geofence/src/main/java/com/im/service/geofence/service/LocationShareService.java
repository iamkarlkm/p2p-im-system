package com.im.service.geofence.service;

import com.im.service.geofence.dto.LocationShareRequest;
import com.im.service.geofence.dto.LocationShareResponse;
import com.im.service.geofence.entity.LocationShare;

import java.util.List;

/**
 * 位置分享服务接口
 */
public interface LocationShareService {

    /**
     * 发起位置分享
     */
    LocationShareResponse startLocationShare(LocationShareRequest request);

    /**
     * 更新分享位置
     */
    LocationShareResponse updateLocation(String shareId, Double latitude, Double longitude);

    /**
     * 停止位置分享
     */
    boolean stopLocationShare(String shareId);

    /**
     * 获取用户的所有激活分享
     */
    List<LocationShareResponse> getActiveShares(String userId);

    /**
     * 获取接收者的所有激活分享
     */
    List<LocationShareResponse> getActiveSharesForRecipient(String recipientId);

    /**
     * 停止用户所有分享
     */
    boolean stopAllShares(String userId);

    /**
     * 根据分享ID获取分享信息
     */
    LocationShareResponse getShareById(String shareId);

    /**
     * 清理过期分享
     */
    void cleanupExpiredShares();
}
