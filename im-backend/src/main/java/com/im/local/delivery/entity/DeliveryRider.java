package com.im.local.delivery.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 骑手实体
 * 本地物流配送系统的核心骑手信息
 */
@Data
public class DeliveryRider {
    
    /** 骑手ID */
    private Long id;
    
    /** 用户ID */
    private Long userId;
    
    /** 骑手姓名 */
    private String realName;
    
    /** 手机号 */
    private String phone;
    
    /** 身份证号 */
    private String idCard;
    
    /** 工号 */
    private String employeeNo;
    
    /** 所属配送站点ID */
    private Long stationId;
    
    /** 当前纬度 */
    private BigDecimal currentLat;
    
    /** 当前经度 */
    private BigDecimal currentLng;
    
    /** 位置更新时间 */
    private LocalDateTime locationUpdatedAt;
    
    /** 骑手状态：0-离线, 1-空闲, 2-接单中, 3-取餐中, 4-配送中 */
    private Integer status;
    
    /** 今日接单数 */
    private Integer todayOrderCount;
    
    /** 今日配送距离(米) */
    private Integer todayDistance;
    
    /** 评分(1-5分) */
    private BigDecimal rating;
    
    /** 总配送单数 */
    private Integer totalDeliveries;
    
    /** 认证状态：0-未认证, 1-审核中, 2-已认证, 3-驳回 */
    private Integer authStatus;
    
    /** 工作城市 */
    private String workCity;
    
    /** 工作区域 */
    private String workDistrict;
    
    /** 账户状态：0-禁用, 1-正常 */
    private Integer accountStatus;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    private LocalDateTime updatedAt;
    
    /**
     * 检查骑手是否在线
     */
    public boolean isOnline() {
        return status != null && status > 0;
    }
    
    /**
     * 检查骑手是否可接单
     */
    public boolean isAvailable() {
        return status != null && status == 1 && accountStatus != null && accountStatus == 1;
    }
    
    /**
     * 获取当前位置GeoHash(用于空间索引)
     */
    public String getGeoHash() {
        if (currentLat == null || currentLng == null) {
            return null;
        }
        return encodeGeoHash(currentLat.doubleValue(), currentLng.doubleValue(), 7);
    }
    
    /**
     * 简单的GeoHash编码实现
     */
    private String encodeGeoHash(double lat, double lng, int precision) {
        String base32 = "0123456789bcdefghjkmnpqrstuvwxyz";
        double[] latRange = {-90.0, 90.0};
        double[] lngRange = {-180.0, 180.0};
        StringBuilder geohash = new StringBuilder();
        boolean isEven = true;
        int bit = 0;
        int ch = 0;
        
        while (geohash.length() < precision) {
            if (isEven) {
                double mid = (lngRange[0] + lngRange[1]) / 2.0;
                if (lng >= mid) {
                    ch |= (1 << (4 - bit));
                    lngRange[0] = mid;
                } else {
                    lngRange[1] = mid;
                }
            } else {
                double mid = (latRange[0] + latRange[1]) / 2.0;
                if (lat >= mid) {
                    ch |= (1 << (4 - bit));
                    latRange[0] = mid;
                } else {
                    latRange[1] = mid;
                }
            }
            
            isEven = !isEven;
            if (bit < 4) {
                bit++;
            } else {
                geohash.append(base32.charAt(ch));
                bit = 0;
                ch = 0;
            }
        }
        return geohash.toString();
    }
}
