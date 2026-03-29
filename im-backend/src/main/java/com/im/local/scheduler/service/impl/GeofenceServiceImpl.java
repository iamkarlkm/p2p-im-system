package com.im.local.scheduler.service.impl;

import com.im.local.scheduler.dto.*;
import com.im.local.scheduler.entity.DeliveryGeofence;
import com.im.local.scheduler.entity.DeliveryStaff;
import com.im.local.scheduler.enums.*;
import com.im.local.scheduler.repository.DeliveryGeofenceMapper;
import com.im.local.scheduler.repository.DeliveryStaffMapper;
import com.im.local.scheduler.service.IGeofenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 围栏服务实现类
 * 动态围栏边界调整与运力管理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GeofenceServiceImpl implements IGeofenceService {
    
    private final DeliveryGeofenceMapper geofenceMapper;
    private final DeliveryStaffMapper staffMapper;
    
    @Override
    public GeofenceResponse createGeofence(CreateGeofenceRequest request) {
        DeliveryGeofence geofence = DeliveryGeofence.builder()
                .name(request.getName())
                .type(request.getType())
                .shapeType(request.getShapeType())
                .centerLng(request.getCenterLng())
                .centerLat(request.getCenterLat())
                .radius(request.getRadius())
                .polygonPoints(request.getPolygonPoints())
                .cityCode(request.getCityCode())
                .districtCode(request.getDistrictCode())
                .currentOrderCount(0)
                .currentStaffCount(0)
                .saturationRate(0)
                .dynamicRadius(request.getRadius())
                .baseRadius(request.getRadius())
                .peakExpansionRatio(request.getPeakExpansionRatio() != null ? 
                        request.getPeakExpansionRatio() : new BigDecimal("1.2"))
                .dynamicAdjustEnabled(request.getDynamicAdjustEnabled() != null ? 
                        request.getDynamicAdjustEnabled() : true)
                .status(1)
                .build();
        
        geofenceMapper.insert(geofence);
        log.info("围栏创建成功: geofenceId={}, name={}", geofence.getGeofenceId(), geofence.getName());
        
        return convertToResponse(geofence);
    }
    
    @Override
    public GeofenceResponse getGeofenceById(Long geofenceId) {
        DeliveryGeofence geofence = geofenceMapper.selectById(geofenceId);
        return geofence != null ? convertToResponse(geofence) : null;
    }
    
    @Override
    public List<GeofenceResponse> listAllGeofences() {
        List<DeliveryGeofence> geofences = geofenceMapper.selectAllActive();
        return geofences.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<GeofenceResponse> listGeofencesByCity(String cityCode) {
        List<DeliveryGeofence> geofences = geofenceMapper.selectByCityCode(cityCode);
        return geofences.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean updateDynamicBoundary(Long geofenceId) {
        DeliveryGeofence geofence = geofenceMapper.selectById(geofenceId);
        if (geofence == null || !geofence.getDynamicAdjustEnabled()) {
            return false;
        }
        
        // 根据饱和度调整围栏半径
        SaturationLevel level = SaturationLevel.fromRate(geofence.getSaturationRate());
        int newRadius = geofence.getBaseRadius();
        
        switch (level) {
            case LOW:
                newRadius = geofence.getBaseRadius();
                break;
            case MEDIUM:
                newRadius = (int) (geofence.getBaseRadius() * 1.1);
                break;
            case HIGH:
                newRadius = (int) (geofence.getBaseRadius() * geofence.getPeakExpansionRatio().doubleValue());
                break;
            case OVERLOAD:
                newRadius = (int) (geofence.getBaseRadius() * geofence.getPeakExpansionRatio().doubleValue() * 1.2);
                break;
        }
        
        geofenceMapper.updateDynamicRadius(geofenceId, newRadius);
        log.info("围栏动态边界更新: geofenceId={}, oldRadius={}, newRadius={}", 
                geofenceId, geofence.getDynamicRadius(), newRadius);
        
        return true;
    }
    
    @Override
    public void batchUpdateDynamicBoundaries() {
        List<DeliveryGeofence> geofences = geofenceMapper.selectAllActive();
        for (DeliveryGeofence geofence : geofences) {
            if (geofence.getDynamicAdjustEnabled()) {
                updateDynamicBoundary(geofence.getGeofenceId());
            }
        }
        log.info("批量更新围栏动态边界完成: count={}", geofences.size());
    }
    
    @Override
    public GeofenceHeatmapResponse getHeatmapData(Long geofenceId) {
        DeliveryGeofence geofence = geofenceMapper.selectById(geofenceId);
        if (geofence == null) return null;
        
        // 获取围栏内骑手
        List<DeliveryStaff> staffList = staffMapper.selectByGeofenceId(geofenceId);
        List<GeofenceHeatmapResponse.HeatPoint> staffPoints = staffList.stream()
                .map(s -> GeofenceHeatmapResponse.HeatPoint.builder()
                        .lng(s.getCurrentLng())
                        .lat(s.getCurrentLat())
                        .count(1)
                        .weight(s.getStatus().equals(StaffStatus.IDLE.getCode()) ? 
                                new BigDecimal("1.0") : new BigDecimal("0.5"))
                        .build())
                .collect(Collectors.toList());
        
        int idleStaff = (int) staffList.stream()
                .filter(s -> s.getStatus().equals(StaffStatus.IDLE.getCode()))
                .count();
        
        return GeofenceHeatmapResponse.builder()
                .geofenceId(geofenceId)
                .name(geofence.getName())
                .staffHeatPoints(staffPoints)
                .orderHeatPoints(Collections.emptyList())
                .totalStaff(staffList.size())
                .idleStaff(idleStaff)
                .totalOrders(geofence.getCurrentOrderCount())
                .pendingOrders(batchMapper.countPending())
                .saturationRate(geofence.getSaturationRate())
                .updatedAt(new Date())
                .build();
    }
    
    @Override
    public boolean checkSaturation(Long geofenceId) {
        DeliveryGeofence geofence = geofenceMapper.selectById(geofenceId);
        if (geofence == null) return false;
        
        SaturationLevel level = SaturationLevel.fromRate(geofence.getSaturationRate());
        return level == SaturationLevel.HIGH || level == SaturationLevel.OVERLOAD;
    }
    
    @Override
    public boolean deleteGeofence(Long geofenceId) {
        int result = geofenceMapper.deleteById(geofenceId);
        log.info("围栏删除: geofenceId={}, result={}", geofenceId, result);
        return result > 0;
    }
    
    @Override
    public List<GeofenceResponse> findGeofencesByLocation(Double lng, Double lat) {
        double degree = 0.01; // 约1km范围
        BigDecimal minLng = BigDecimal.valueOf(lng - degree);
        BigDecimal maxLng = BigDecimal.valueOf(lng + degree);
        BigDecimal minLat = BigDecimal.valueOf(lat - degree);
        BigDecimal maxLat = BigDecimal.valueOf(lat + degree);
        
        List<DeliveryGeofence> geofences = geofenceMapper.selectInRange(minLng, maxLng, minLat, maxLat);
        
        return geofences.stream()
                .filter(g -> isPointInGeofence(lng, lat, g))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    private boolean isPointInGeofence(double lng, double lat, DeliveryGeofence geofence) {
        if (geofence.getShapeType().equals(ShapeType.CIRCLE.getCode())) {
            double distance = calculateDistance(
                    lng, lat,
                    geofence.getCenterLng().doubleValue(),
                    geofence.getCenterLat().doubleValue()
            );
            return distance <= geofence.getDynamicRadius();
        }
        // 多边形检测简化处理
        return true;
    }
    
    private GeofenceResponse convertToResponse(DeliveryGeofence geofence) {
        GeofenceType type = GeofenceType.fromCode(geofence.getType());
        SaturationLevel level = SaturationLevel.fromRate(geofence.getSaturationRate());
        
        return GeofenceResponse.builder()
                .geofenceId(geofence.getGeofenceId())
                .name(geofence.getName())
                .type(type != null ? type.getName() : "未知")
                .centerLng(geofence.getCenterLng())
                .centerLat(geofence.getCenterLat())
                .currentRadius(geofence.getDynamicRadius())
                .baseRadius(geofence.getBaseRadius())
                .currentOrderCount(geofence.getCurrentOrderCount())
                .currentStaffCount(geofence.getCurrentStaffCount())
                .saturationRate(geofence.getSaturationRate())
                .saturationLevel(level.getName())
                .dynamicAdjustEnabled(geofence.getDynamicAdjustEnabled())
                .status(geofence.getStatus() == 1 ? "启用" : "禁用")
                .createdAt(geofence.getCreatedAt())
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
