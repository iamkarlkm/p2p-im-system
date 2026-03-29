package com.im.backend.modules.local.life.service;

import com.im.backend.modules.local.life.dto.PoiGuidanceDTO;
import com.im.backend.modules.local.life.dto.PoiGuidanceQueryRequestDTO;

import java.util.List;

/**
 * POI引导服务接口
 * POI Guidance Service Interface
 */
public interface PoiGuidanceService {

    /**
     * 获取POI引导点列表
     */
    List<PoiGuidanceDTO> getPoiGuidanceList(PoiGuidanceQueryRequestDTO request);

    /**
     * 获取主入口引导点
     */
    PoiGuidanceDTO getMainEntrance(Long poiId);

    /**
     * 获取最近引导点
     */
    PoiGuidanceDTO getNearestGuidance(Long poiId, Double userLng, Double userLat);

    /**
     * 获取停车场信息
     */
    PoiGuidanceDTO getParkingInfo(Long poiId);

    /**
     * 获取室内导航信息
     */
    List<PoiGuidanceDTO> getIndoorGuidance(Long poiId, String indoorMapId, Integer floor);
}
