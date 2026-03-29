package com.im.backend.service;

import com.im.backend.dto.poi.*;
import java.util.List;

/**
 * POI签到服务接口
 */
public interface PoiCheckinService {
    
    /**
     * 执行POI签到
     */
    PoiCheckinResponse checkin(Long userId, PoiCheckinRequest request);
    
    /**
     * 获取用户的签到记录列表
     */
    List<CheckinRecordDTO> getUserCheckinRecords(Long userId, int page, int size);
    
    /**
     * 获取用户在特定POI的签到记录
     */
    List<CheckinRecordDTO> getPoiCheckinRecords(String poiId, int page, int size);
    
    /**
     * 获取签到详情
     */
    CheckinRecordDTO getCheckinDetail(Long checkinId);
    
    /**
     * 检查用户今日是否已签到
     */
    Boolean hasCheckedInToday(Long userId, String poiId);
    
    /**
     * 获取附近热门签到地点
     */
    List<HotPoiDTO> getNearbyHotPois(Double longitude, Double latitude, int radius, int limit);
    
    /**
     * 获取用户连续签到统计
     */
    ConsecutiveCheckinStatsDTO getConsecutiveStats(Long userId);
    
    /**
     * 删除签到记录
     */
    Boolean deleteCheckin(Long userId, Long checkinId);
}
