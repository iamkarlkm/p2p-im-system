package com.im.entity.delivery;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 配送区域实体类 - 即时配送运力调度系统
 * 管理配送围栏、运力配置
 */
@Data
public class DeliveryZone {
    
    /** 区域ID */
    private Long id;
    
    /** 区域名称 */
    private String name;
    
    /** 区域编码 */
    private String code;
    
    /** 城市ID */
    private Long cityId;
    
    /** 城市名称 */
    private String cityName;
    
    /** 区域中心经度 */
    private BigDecimal centerLongitude;
    
    /** 区域中心纬度 */
    private BigDecimal centerLatitude;
    
    /** 区域边界GeoJSON */
    private String boundaryGeoJson;
    
    /** 覆盖半径(米) */
    private Integer coverageRadius;
    
    /** 区域类型: CORE-核心城区, SUBURB-郊区, REMOTE-偏远地区 */
    private String zoneType;
    
    /** 区域状态: ACTIVE-运营中, SUSPENDED-暂停, CLOSED-关闭 */
    private String status;
    
    /** 在线骑手数 */
    private Integer onlineRiderCount;
    
    /** 空闲骑手数 */
    private Integer availableRiderCount;
    
    /** 忙碌骑手数 */
    private Integer busyRiderCount;
    
    /** 当前订单数 */
    private Integer currentOrderCount;
    
    /** 待分配订单数 */
    private Integer pendingOrderCount;
    
    /** 运力负载率(%) */
    private BigDecimal capacityLoadRate;
    
    /** 运力预警阈值(%) */
    private Integer capacityWarningThreshold;
    
    /** 平均配送距离(米) */
    private Integer avgDeliveryDistance;
    
    /** 平均配送时长(分钟) */
    private Integer avgDeliveryTime;
    
    /** 基础配送费(元) */
    private BigDecimal baseDeliveryFee;
    
    /** 距离加价(元/公里) */
    private BigDecimal distanceFeeRate;
    
    /** 重量加价(元/kg) */
    private BigDecimal weightFeeRate;
    
    /** 时段加价配置JSON */
    private String timeFeeConfig;
    
    /** 天气加价配置JSON */
    private String weatherFeeConfig;
    
    /** 负责人ID */
    private Long managerId;
    
    /** 负责人姓名 */
    private String managerName;
    
    /** 负责人电话 */
    private String managerPhone;
    
    /** 运营开始时间 */
    private String operatingStartTime;
    
    /** 运营结束时间 */
    private String operatingEndTime;
    
    /** 描述 */
    private String description;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    private LocalDateTime updatedAt;
    
    /** 是否删除 */
    private Boolean deleted;
    
    // ========== 业务方法 ==========
    
    /**
     * 更新运力统计
     */
    public void updateCapacityStats(int online, int available, int busy, int orders, int pending) {
        this.onlineRiderCount = online;
        this.availableRiderCount = available;
        this.busyRiderCount = busy;
        this.currentOrderCount = orders;
        this.pendingOrderCount = pending;
        
        // 计算运力负载率
        if (online > 0) {
            this.capacityLoadRate = BigDecimal.valueOf((busy * 100.0) / online)
                    .setScale(2, BigDecimal.ROUND_HALF_UP);
        } else {
            this.capacityLoadRate = BigDecimal.ZERO;
        }
    }
    
    /**
     * 检查运力是否充足
     */
    public boolean hasEnoughCapacity() {
        if (capacityLoadRate == null) return true;
        return capacityLoadRate.intValue() < capacityWarningThreshold;
    }
    
    /**
     * 检查是否在运营时间
     */
    public boolean isOperatingTime() {
        java.time.LocalTime now = java.time.LocalTime.now();
        java.time.LocalTime start = java.time.LocalTime.parse(operatingStartTime);
        java.time.LocalTime end = java.time.LocalTime.parse(operatingEndTime);
        return !now.isBefore(start) && !now.isAfter(end);
    }
    
    /**
     * 计算配送费
     */
    public BigDecimal calculateDeliveryFee(int distanceMeters, BigDecimal weight) {
        BigDecimal fee = baseDeliveryFee != null ? baseDeliveryFee : BigDecimal.ZERO;
        
        // 距离费
        if (distanceFeeRate != null && distanceMeters > 0) {
            BigDecimal distanceKm = BigDecimal.valueOf(distanceMeters / 1000.0);
            fee = fee.add(distanceKm.multiply(distanceFeeRate));
        }
        
        // 重量费
        if (weightFeeRate != null && weight != null && weight.doubleValue() > 0) {
            fee = fee.add(weight.multiply(weightFeeRate));
        }
        
        return fee.setScale(2, BigDecimal.ROUND_HALF_UP);
    }
    
    /**
     * 检查坐标是否在区域内(简化判断，实际使用空间数据库)
     */
    public boolean contains(BigDecimal lng, BigDecimal lat) {
        // 简化：使用中心点和半径判断
        if (centerLongitude == null || centerLatitude == null || coverageRadius == null) {
            return false;
        }
        
        double R = 6371000;
        double radLat1 = Math.toRadians(centerLatitude.doubleValue());
        double radLat2 = Math.toRadians(lat.doubleValue());
        double deltaLat = Math.toRadians(lat.doubleValue() - centerLatitude.doubleValue());
        double deltaLng = Math.toRadians(lng.doubleValue() - centerLongitude.doubleValue());
        
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                   Math.cos(radLat1) * Math.cos(radLat2) *
                   Math.sin(deltaLng / 2) * Math.sin(deltaLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;
        
        return distance <= coverageRadius;
    }
}
