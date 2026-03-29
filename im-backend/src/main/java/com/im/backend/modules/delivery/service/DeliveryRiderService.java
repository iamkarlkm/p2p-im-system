package com.im.backend.modules.delivery.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.im.backend.modules.delivery.entity.DeliveryRider;
import com.im.backend.modules.delivery.dto.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 骑手服务接口
 * 本地物流配送智能调度引擎
 */
public interface DeliveryRiderService extends IService<DeliveryRider> {

    /**
     * 注册骑手
     */
    RiderVO register(RiderRegisterDTO dto);

    /**
     * 更新骑手位置
     */
    boolean updateLocation(Long riderId, LocationUpdateDTO dto);

    /**
     * 查找附近可用骑手
     */
    List<RiderVO> findNearbyAvailableRiders(BigDecimal lat, BigDecimal lng, Integer radius);

    /**
     * 查找最近骑手（用于智能派单）
     */
    RiderVO findNearestRider(BigDecimal lat, BigDecimal lng, String bizType);

    /**
     * 更新骑手状态
     */
    boolean updateStatus(Long riderId, String status);

    /**
     * 批量更新骑手位置（Redis Geo批量操作）
     */
    boolean batchUpdateLocations(List<LocationUpdateDTO> locations);

    /**
     * 获取骑手当前位置
     */
    RiderLocationVO getCurrentLocation(Long riderId);

    /**
     * 获取骑手的实时轨迹
     */
    List<LocationPointVO> getRiderTrajectory(Long riderId, String startTime, String endTime);

    /**
     * 分页查询骑手列表
     */
    Page<RiderVO> pageRiders(RiderQueryDTO query);

    /**
     * 获取骑手统计信息
     */
    RiderStatsVO getRiderStats(Long riderId);

    /**
     * 获取站点所有在线骑手
     */
    List<RiderVO> getOnlineRidersByStation(Long stationId);
}
