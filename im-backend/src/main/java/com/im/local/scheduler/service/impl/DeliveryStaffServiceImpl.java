package com.im.local.scheduler.service.impl;

import com.im.local.scheduler.dto.*;
import com.im.local.scheduler.entity.DeliveryStaff;
import com.im.local.scheduler.enums.StaffStatus;
import com.im.local.scheduler.enums.StaffType;
import com.im.local.scheduler.repository.DeliveryStaffMapper;
import com.im.local.scheduler.service.IDeliveryStaffService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ch.hsr.geohash.GeoHash;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 骑手服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryStaffServiceImpl implements IDeliveryStaffService {
    
    private final DeliveryStaffMapper staffMapper;
    
    @Override
    public StaffResponse registerStaff(RegisterStaffRequest request) {
        DeliveryStaff staff = DeliveryStaff.builder()
                .staffName(request.getStaffName())
                .phone(request.getPhone())
                .staffType(request.getStaffType())
                .status(StaffStatus.OFFLINE.getCode())
                .maxOrderCapacity(request.getMaxOrderCapacity())
                .deliveryAreaId(request.getDeliveryAreaId())
                .currentOrderCount(0)
                .todayCompletedOrders(0)
                .todayDeliveryDistance(0)
                .rating(new BigDecimal("5.0"))
                .avgDeliveryTime(30)
                .enabled(true)
                .build();
        
        staffMapper.insert(staff);
        log.info("骑手注册成功: staffId={}, name={}", staff.getStaffId(), staff.getStaffName());
        
        return convertToResponse(staff);
    }
    
    @Override
    public StaffResponse getStaffById(Long staffId) {
        DeliveryStaff staff = staffMapper.selectById(staffId);
        return staff != null ? convertToResponse(staff) : null;
    }
    
    @Override
    public boolean updateStaffLocation(UpdateStaffLocationRequest request) {
        String geohash = GeoHash.geoHashStringWithCharacterPrecision(
                request.getLat().doubleValue(), 
                request.getLng().doubleValue(), 8);
        
        DeliveryStaff staff = DeliveryStaff.builder()
                .staffId(request.getStaffId())
                .currentLng(request.getLng())
                .currentLat(request.getLat())
                .currentGeohash(geohash)
                .build();
        
        int result = staffMapper.updateLocation(staff);
        log.debug("骑手位置更新: staffId={}, lng={}, lat={}, geohash={}", 
                request.getStaffId(), request.getLng(), request.getLat(), geohash);
        
        return result > 0;
    }
    
    @Override
    public boolean updateStaffStatus(Long staffId, Integer status) {
        int result = staffMapper.updateStatus(staffId, status);
        log.info("骑手状态更新: staffId={}, status={}", staffId, status);
        return result > 0;
    }
    
    @Override
    public List<StaffResponse> findIdleStaffInGeofence(Long geofenceId) {
        List<DeliveryStaff> staffList = staffMapper.selectByGeofenceId(geofenceId);
        return staffList.stream()
                .filter(s -> s.getStatus().equals(StaffStatus.IDLE.getCode()))
                .filter(s -> s.getEnabled())
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<StaffResponse> findAvailableStaffNearby(Double lng, Double lat, Integer radius) {
        double degree = radius / 111000.0;
        BigDecimal minLng = BigDecimal.valueOf(lng - degree);
        BigDecimal maxLng = BigDecimal.valueOf(lng + degree);
        BigDecimal minLat = BigDecimal.valueOf(lat - degree);
        BigDecimal maxLat = BigDecimal.valueOf(lat + degree);
        
        List<DeliveryStaff> staffList = staffMapper.selectIdleStaffInRange(minLng, maxLng, minLat, maxLat);
        
        return staffList.stream()
                .map(this::convertToResponse)
                .sorted((a, b) -> {
                    double distA = calculateDistance(lng, lat, 
                            a.getCurrentLng().doubleValue(), a.getCurrentLat().doubleValue());
                    double distB = calculateDistance(lng, lat, 
                            b.getCurrentLng().doubleValue(), b.getCurrentLat().doubleValue());
                    return Double.compare(distA, distB);
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean updateStaffOrderStats(Long staffId, Integer completedOrders) {
        DeliveryStaff staff = staffMapper.selectById(staffId);
        if (staff == null) return false;
        
        staff.setTodayCompletedOrders(completedOrders);
        staff.setCurrentOrderCount(Math.max(0, staff.getCurrentOrderCount() - 1));
        
        int result = staffMapper.updateOrderStats(staff);
        return result > 0;
    }
    
    @Override
    public void batchUpdateStaffGeofence() {
        // 批量更新骑手围栏信息
        log.info("批量更新骑手围栏信息");
    }
    
    @Override
    public List<StaffResponse> listAllStaff() {
        List<DeliveryStaff> staffList = staffMapper.selectRecent(1000);
        return staffList.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    private StaffResponse convertToResponse(DeliveryStaff staff) {
        StaffType type = StaffType.fromCode(staff.getStaffType());
        StaffStatus status = StaffStatus.fromCode(staff.getStatus());
        
        return StaffResponse.builder()
                .staffId(staff.getStaffId())
                .staffName(staff.getStaffName())
                .phone(staff.getPhone())
                .staffType(type != null ? type.getName() : "未知")
                .status(status != null ? status.getName() : "未知")
                .currentLng(staff.getCurrentLng())
                .currentLat(staff.getCurrentLat())
                .locationUpdatedAt(staff.getLocationUpdatedAt())
                .todayCompletedOrders(staff.getTodayCompletedOrders())
                .avgDeliveryTime(staff.getAvgDeliveryTime())
                .rating(staff.getRating())
                .currentOrderCount(staff.getCurrentOrderCount())
                .maxOrderCapacity(staff.getMaxOrderCapacity())
                .remainingCapacity(staff.getMaxOrderCapacity() - staff.getCurrentOrderCount())
                .build();
    }
    
    private double calculateDistance(double lng1, double lat1, double lng2, double lat2) {
        final double R = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
