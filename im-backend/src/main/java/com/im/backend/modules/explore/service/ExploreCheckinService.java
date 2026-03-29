package com.im.backend.modules.explore.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.im.backend.modules.explore.entity.ExploreCheckin;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 用户探店打卡服务接口
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
public interface ExploreCheckinService extends IService<ExploreCheckin> {

    /**
     * 用户打卡
     */
    ExploreCheckin checkin(ExploreCheckin checkin);

    /**
     * 获取用户的打卡记录列表
     */
    IPage<ExploreCheckin> getUserCheckins(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 获取POI的打卡记录列表
     */
    IPage<ExploreCheckin> getPoiCheckins(Long poiId, Integer pageNum, Integer pageSize);

    /**
     * 获取用户的探店足迹地图数据
     */
    List<ExploreCheckin> getUserFootprintMap(Long userId);

    /**
     * 获取用户的探店统计
     */
    CheckinStatistics getUserStatistics(Long userId);

    /**
     * 检查用户今天是否已打卡该POI
     */
    boolean hasCheckedInToday(Long userId, Long poiId);

    /**
     * 围栏自动打卡
     */
    ExploreCheckin autoCheckinByFence(Long userId, Long poiId, BigDecimal longitude, BigDecimal latitude);

    /**
     * 获取用户的连续打卡天数
     */
    int getConsecutiveCheckinDays(Long userId);

    /**
     * 获取用户在某时间段内的打卡记录
     */
    List<ExploreCheckin> getCheckinsByDateRange(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * 删除打卡记录
     */
    boolean deleteCheckin(Long checkinId, Long userId);

    /**
     * 打卡统计DTO
     */
    class CheckinStatistics {
        private Integer totalCheckins;
        private Integer uniquePoiCount;
        private Integer consecutiveDays;
        private Integer thisMonthCheckins;
        private Long totalSpend;
        private String mostVisitedCategory;
        private Integer mostVisitedCount;

        // Getters and Setters
        public Integer getTotalCheckins() { return totalCheckins; }
        public void setTotalCheckins(Integer totalCheckins) { this.totalCheckins = totalCheckins; }
        
        public Integer getUniquePoiCount() { return uniquePoiCount; }
        public void setUniquePoiCount(Integer uniquePoiCount) { this.uniquePoiCount = uniquePoiCount; }
        
        public Integer getConsecutiveDays() { return consecutiveDays; }
        public void setConsecutiveDays(Integer consecutiveDays) { this.consecutiveDays = consecutiveDays; }
        
        public Integer getThisMonthCheckins() { return thisMonthCheckins; }
        public void setThisMonthCheckins(Integer thisMonthCheckins) { this.thisMonthCheckins = thisMonthCheckins; }
        
        public Long getTotalSpend() { return totalSpend; }
        public void setTotalSpend(Long totalSpend) { this.totalSpend = totalSpend; }
        
        public String getMostVisitedCategory() { return mostVisitedCategory; }
        public void setMostVisitedCategory(String mostVisitedCategory) { this.mostVisitedCategory = mostVisitedCategory; }
        
        public Integer getMostVisitedCount() { return mostVisitedCount; }
        public void setMostVisitedCount(Integer mostVisitedCount) { this.mostVisitedCount = mostVisitedCount; }
    }
}
