package com.im.entity.delivery;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 骑手实体类 - 即时配送运力调度系统
 * 存储骑手基本信息、工作状态、实时位置
 */
@Data
public class DeliveryRider {
    
    /** 骑手ID */
    private Long id;
    
    /** 用户ID */
    private Long userId;
    
    /** 骑手姓名 */
    private String name;
    
    /** 手机号码 */
    private String phone;
    
    /** 头像URL */
    private String avatar;
    
    /** 工作状态: IDLE-空闲, BUSY-配送中, OFFLINE-离线, REST-休息 */
    private String workStatus;
    
    /** 骑手等级: BRONZE-青铜, SILVER-白银, GOLD-黄金, PLATINUM-铂金, DIAMOND-钻石 */
    private String level;
    
    /** 实时经度 */
    private BigDecimal longitude;
    
    /** 实时纬度 */
    private BigDecimal latitude;
    
    /** 位置更新时间 */
    private LocalDateTime locationUpdatedAt;
    
    /** 当前配送区域ID */
    private Long currentZoneId;
    
    /** 当前配送中的订单数 */
    private Integer activeOrderCount;
    
    /** 今日完成订单数 */
    private Integer todayCompletedCount;
    
    /** 今日收入(元) */
    private BigDecimal todayIncome;
    
    /** 评分(1-5分) */
    private BigDecimal rating;
    
    /** 总配送单数 */
    private Integer totalDeliveries;
    
    /** 准时率 */
    private BigDecimal onTimeRate;
    
    /** 所属配送站点ID */
    private Long stationId;
    
    /** 配送车辆类型: ELECTRIC-电动车, MOTORCYCLE-摩托车, CAR-汽车, BICYCLE-自行车 */
    private String vehicleType;
    
    /** 车牌号 */
    private String vehiclePlate;
    
    /** 工作状态开始时间 */
    private LocalDateTime workStartTime;
    
    /** 认证状态: PENDING-待审核, APPROVED-已通过, REJECTED-已拒绝 */
    private String verifyStatus;
    
    /** 健康证过期时间 */
    private LocalDateTime healthCertExpire;
    
    /** 紧急联系人姓名 */
    private String emergencyContact;
    
    /** 紧急联系人电话 */
    private String emergencyPhone;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    private LocalDateTime updatedAt;
    
    /** 是否删除 */
    private Boolean deleted;
    
    // ========== 业务方法 ==========
    
    /**
     * 检查骑手是否可接单
     */
    public boolean isAvailable() {
        return "IDLE".equals(workStatus) && activeOrderCount < getMaxOrderLimit();
    }
    
    /**
     * 获取最大接单限制
     */
    public int getMaxOrderLimit() {
        return switch (level) {
            case "DIAMOND" -> 8;
            case "PLATINUM" -> 6;
            case "GOLD" -> 5;
            case "SILVER" -> 4;
            default -> 3;
        };
    }
    
    /**
     * 更新位置
     */
    public void updateLocation(BigDecimal lng, BigDecimal lat) {
        this.longitude = lng;
        this.latitude = lat;
        this.locationUpdatedAt = LocalDateTime.now();
    }
    
    /**
     * 计算与目标的距离(米)
     */
    public double distanceTo(BigDecimal targetLng, BigDecimal targetLat) {
        return calculateDistance(latitude.doubleValue(), longitude.doubleValue(),
                                targetLat.doubleValue(), targetLng.doubleValue());
    }
    
    /**
     * Haversine公式计算球面距离
     */
    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double R = 6371000; // 地球半径(米)
        double radLat1 = Math.toRadians(lat1);
        double radLat2 = Math.toRadians(lat2);
        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLng = Math.toRadians(lng2 - lng1);
        
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                   Math.cos(radLat1) * Math.cos(radLat2) *
                   Math.sin(deltaLng / 2) * Math.sin(deltaLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
}
