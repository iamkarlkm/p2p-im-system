package com.im.backend.modules.local.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.im.backend.modules.local.dto.*;
import com.im.backend.modules.local.entity.Geofence;
import com.im.backend.modules.local.enums.CapacityLoadStatus;
import com.im.backend.modules.local.repository.GeofenceMapper;
import com.im.backend.modules.local.service.IGeofenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 地理围栏服务实现
 */
@Slf4j
@Service
public class GeofenceServiceImpl extends ServiceImpl<GeofenceMapper, Geofence> implements IGeofenceService {
    
    @Autowired
    private GeofenceMapper geofenceMapper;
    
    @Override
    @Transactional
    public GeofenceResponse createGeofence(CreateGeofenceRequest request) {
        Geofence geofence = new Geofence();
        BeanUtils.copyProperties(request, geofence);
        geofence.setStatus(1); // 默认启用
        geofence.setCurrentOrderCount(0);
        geofence.setAvailableStaffCount(0);
        geofence.setCapacityLoad(0);
        
        geofenceMapper.insert(geofence);
        log.info("Created geofence: {} for merchant: {}", geofence.getId(), geofence.getMerchantId());
        
        return convertToResponse(geofence);
    }
    
    @Override
    public GeofenceResponse getGeofenceById(String geofenceId) {
        Geofence geofence = geofenceMapper.selectById(geofenceId);
        return geofence != null ? convertToResponse(geofence) : null;
    }
    
    @Override
    public List<GeofenceResponse> getGeofencesByMerchant(String merchantId) {
        List<Geofence> geofences = geofenceMapper.selectByMerchantId(merchantId);
        return geofences.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public GeofenceResponse updateGeofence(String geofenceId, CreateGeofenceRequest request) {
        Geofence geofence = geofenceMapper.selectById(geofenceId);
        if (geofence == null) {
            throw new RuntimeException("Geofence not found");
        }
        
        BeanUtils.copyProperties(request, geofence);
        geofence.setId(geofenceId);
        geofenceMapper.updateById(geofence);
        
        log.info("Updated geofence: {}", geofenceId);
        return convertToResponse(geofence);
    }
    
    @Override
    @Transactional
    public GeofenceResponse toggleGeofence(String geofenceId, Boolean enable) {
        Geofence geofence = geofenceMapper.selectById(geofenceId);
        if (geofence == null) {
            throw new RuntimeException("Geofence not found");
        }
        
        geofence.setStatus(enable ? 1 : 0);
        geofenceMapper.updateById(geofence);
        
        log.info("Toggled geofence: {} to status: {}", geofenceId, enable);
        return convertToResponse(geofence);
    }
    
    @Override
    @Transactional
    public void deleteGeofence(String geofenceId) {
        geofenceMapper.deleteById(geofenceId);
        log.info("Deleted geofence: {}", geofenceId);
    }
    
    @Override
    public boolean isPointInGeofence(String geofenceId, BigDecimal longitude, BigDecimal latitude) {
        Geofence geofence = geofenceMapper.selectById(geofenceId);
        if (geofence == null) {
            return false;
        }
        
        if (geofence.getType() == 1) {
            // 圆形围栏 - 计算距离
            double distance = calculateDistance(
                geofence.getCenterLongitude(), geofence.getCenterLatitude(),
                longitude, latitude
            );
            return distance <= geofence.getRadius();
        } else {
            // 多边形围栏 - 射线法
            return isPointInPolygon(geofence.getPolygonPoints(), longitude, latitude);
        }
    }
    
    @Override
    public List<GeofenceResponse> findGeofencesByPoint(BigDecimal longitude, BigDecimal latitude) {
        List<Geofence> geofences = geofenceMapper.findCircleGeofencesByPoint(longitude, latitude);
        return geofences.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    @Override
    public CapacityLoadResponse getCapacityLoad(String geofenceId) {
        Geofence geofence = geofenceMapper.selectById(geofenceId);
        if (geofence == null) {
            throw new RuntimeException("Geofence not found");
        }
        
        CapacityLoadResponse response = new CapacityLoadResponse();
        response.setGeofenceId(geofenceId);
        response.setGeofenceName(geofence.getName());
        response.setCurrentOrders(geofence.getCurrentOrderCount());
        response.setOnlineStaffCount(geofence.getAvailableStaffCount());
        response.setIdleStaffCount(geofence.getAvailableStaffCount() / 2); // 假设一半空闲
        response.setBusyStaffCount(geofence.getAvailableStaffCount() / 2);
        response.setAverageLoadRate(geofence.getCapacityLoad());
        response.setLoadStatus(CapacityLoadStatus.getByRate(geofence.getCapacityLoad()).getCode());
        response.setAlertThreshold(85);
        response.setUpdateTime(LocalDateTime.now());
        
        // 预估等待时间
        if (response.getIdleStaffCount() > 0) {
            response.setEstimatedWaitTime(response.getCurrentOrders() / response.getIdleStaffCount() * 5);
        } else {
            response.setEstimatedWaitTime(999);
        }
        
        return response;
    }
    
    @Override
    @Transactional
    public void updateCapacityLoad(String geofenceId) {
        // 重新计算负载率
        Integer orderCount = geofenceMapper.countOrdersByGeofenceId(geofenceId);
        Geofence geofence = geofenceMapper.selectById(geofenceId);
        
        int loadRate = 0;
        if (geofence.getAvailableStaffCount() > 0) {
            loadRate = orderCount * 100 / geofence.getAvailableStaffCount();
        }
        
        geofenceMapper.updateCapacityLoad(geofenceId, Math.min(loadRate, 100));
        geofenceMapper.updateOrderCount(geofenceId, orderCount);
        
        log.info("Updated capacity load for geofence {}: {}%", geofenceId, loadRate);
    }
    
    @Override
    @Transactional
    public ResourceDispatchResponse dispatchResource(ResourceDispatchRequest request) {
        // 创建调度记录
        log.info("Dispatching resource: staff {} to geofence {}", 
            request.getStaffId(), request.getGeofenceId());
        
        ResourceDispatchResponse response = new ResourceDispatchResponse();
        response.setDispatchId(java.util.UUID.randomUUID().toString());
        response.setStaffId(request.getStaffId());
        response.setGeofenceId(request.getGeofenceId());
        response.setStatus(1); // 调度中
        response.setEstimatedArrivalTime(request.getEstimatedArrivalTime());
        response.setDispatchTime(LocalDateTime.now());
        
        return response;
    }
    
    @Override
    @Transactional
    public ResourceDispatchResponse crossFenceDispatch(String fromGeofenceId, String toGeofenceId, Integer staffCount) {
        log.info("Cross-fence dispatch: {} staff from {} to {}", staffCount, fromGeofenceId, toGeofenceId);
        
        // 跨围栏借调运力
        ResourceDispatchRequest request = new ResourceDispatchRequest();
        request.setGeofenceId(toGeofenceId);
        request.setDispatchType(3); // 跨围栏
        
        return dispatchResource(request);
    }
    
    /**
     * 计算两点距离
     */
    private double calculateDistance(BigDecimal lng1, BigDecimal lat1, BigDecimal lng2, BigDecimal lat2) {
        double R = 6371000;
        double dLat = Math.toRadians(lat2.doubleValue() - lat1.doubleValue());
        double dLon = Math.toRadians(lng2.doubleValue() - lng1.doubleValue());
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(Math.toRadians(lat1.doubleValue())) * Math.cos(Math.toRadians(lat2.doubleValue())) *
            Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
    
    /**
     * 射线法判断点是否在多边形内
     */
    private boolean isPointInPolygon(String polygonPoints, BigDecimal longitude, BigDecimal latitude) {
        // 简化版实现
        return false;
    }
    
    /**
     * 转换为响应对象
     */
    private GeofenceResponse convertToResponse(Geofence geofence) {
        GeofenceResponse response = new GeofenceResponse();
        BeanUtils.copyProperties(geofence, response);
        return response;
    }
}
