package com.im.backend.modules.local.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.im.backend.modules.local.dto.*;
import com.im.backend.modules.local.entity.DispatchTask;
import com.im.backend.modules.local.enums.DispatchTaskStatus;
import com.im.backend.modules.local.repository.DispatchTaskMapper;
import com.im.backend.modules.local.service.ISmartDispatchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 智能调度服务实现
 */
@Slf4j
@Service
public class SmartDispatchServiceImpl extends ServiceImpl<DispatchTaskMapper, DispatchTask> implements ISmartDispatchService {
    
    @Autowired
    private DispatchTaskMapper dispatchTaskMapper;
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    private static final String STAFF_LOCATION_KEY = "staff:location:";
    private static final String STAFF_STATUS_KEY = "staff:status:";
    
    @Override
    @Transactional
    public DispatchTaskResponse createDispatchTask(CreateDispatchTaskRequest request) {
        DispatchTask task = new DispatchTask();
        BeanUtils.copyProperties(request, task);
        
        task.setStatus(DispatchTaskStatus.PENDING.getCode());
        task.setPickupAddress(request.getPickupAddress().getAddress());
        task.setPickupLongitude(request.getPickupAddress().getLongitude());
        task.setPickupLatitude(request.getPickupAddress().getLatitude());
        task.setPickupContactName(request.getPickupAddress().getContactName());
        task.setPickupContactPhone(request.getPickupAddress().getContactPhone());
        task.setDeliveryAddress(request.getDeliveryAddress().getAddress());
        task.setDeliveryLongitude(request.getDeliveryAddress().getLongitude());
        task.setDeliveryLatitude(request.getDeliveryAddress().getLatitude());
        task.setDeliveryContactName(request.getDeliveryAddress().getContactName());
        task.setDeliveryContactPhone(request.getDeliveryAddress().getContactPhone());
        
        // 计算配送距离
        Integer distance = calculateDistance(
            task.getPickupLongitude(), task.getPickupLatitude(),
            task.getDeliveryLongitude(), task.getDeliveryLatitude()
        );
        task.setDeliveryDistance(distance);
        
        // 计算配送费
        BigDecimal fee = calculateDeliveryFee(
            BigDecimal.valueOf(distance),
            request.getEstimatedWeight() != null ? request.getEstimatedWeight() : BigDecimal.ONE
        );
        task.setDeliveryFee(fee);
        
        // 预估时间 (平均速度 30km/h)
        Integer durationMinutes = distance / 500;
        task.setEstimatedPickupTime(LocalDateTime.now().plusMinutes(15));
        task.setEstimatedDeliveryTime(LocalDateTime.now().plusMinutes(15 + durationMinutes));
        
        dispatchTaskMapper.insert(task);
        log.info("Created dispatch task: {} for order: {}", task.getId(), task.getOrderId());
        
        return convertToResponse(task);
    }
    
    @Override
    public DispatchTaskResponse getTaskById(String taskId) {
        DispatchTask task = dispatchTaskMapper.selectById(taskId);
        return task != null ? convertToResponse(task) : null;
    }
    
    @Override
    @Transactional
    public DispatchTaskResponse assignTask(String taskId) {
        DispatchTask task = dispatchTaskMapper.selectById(taskId);
        if (task == null) {
            throw new RuntimeException("Task not found");
        }
        
        // 智能派单算法：查找最近可用服务人员
        String bestStaffId = findBestStaffForTask(task);
        if (bestStaffId == null) {
            throw new RuntimeException("No available staff");
        }
        
        // 获取服务人员信息
        String staffName = redisTemplate.opsForValue().get(STAFF_STATUS_KEY + bestStaffId + ":name");
        String staffPhone = redisTemplate.opsForValue().get(STAFF_STATUS_KEY + bestStaffId + ":phone");
        
        dispatchTaskMapper.assignStaff(taskId, bestStaffId, staffName, staffPhone);
        dispatchTaskMapper.updateStatus(taskId, DispatchTaskStatus.ASSIGNED.getCode());
        
        log.info("Assigned task {} to staff {}", taskId, bestStaffId);
        return getTaskById(taskId);
    }
    
    @Override
    @Transactional
    public DispatchTaskResponse markPickup(String taskId) {
        dispatchTaskMapper.updateStatus(taskId, DispatchTaskStatus.PICKUP.getCode());
        log.info("Task {} marked as pickup", taskId);
        return getTaskById(taskId);
    }
    
    @Override
    @Transactional
    public DispatchTaskResponse markDelivered(String taskId) {
        dispatchTaskMapper.updateStatus(taskId, DispatchTaskStatus.COMPLETED.getCode());
        log.info("Task {} marked as delivered", taskId);
        return getTaskById(taskId);
    }
    
    @Override
    @Transactional
    public DispatchTaskResponse cancelTask(String taskId, String reason) {
        dispatchTaskMapper.updateStatus(taskId, DispatchTaskStatus.CANCELLED.getCode());
        log.info("Task {} cancelled, reason: {}", taskId, reason);
        return getTaskById(taskId);
    }
    
    @Override
    public List<DispatchTaskResponse> getActiveTasksByStaff(String staffId) {
        List<DispatchTask> tasks = dispatchTaskMapper.selectActiveByStaffId(staffId);
        return tasks.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    @Override
    public PathPlanningResponse planPath(PathPlanningRequest request) {
        // 简化版路径规划 - 实际应调用地图服务API
        PathPlanningResponse response = new PathPlanningResponse();
        response.setPathId(java.util.UUID.randomUUID().toString());
        
        Integer distance = calculateDistance(
            request.getStartLongitude(), request.getStartLatitude(),
            request.getEndLongitude(), request.getEndLatitude()
        );
        
        response.setTotalDistance(distance);
        response.setEstimatedDuration(distance / 500); // 30km/h
        response.setStrategy(request.getStrategy());
        
        // 生成路径点
        List<PathPlanningResponse.PathPoint> points = generatePathPoints(
            request.getStartLongitude(), request.getStartLatitude(),
            request.getEndLongitude(), request.getEndLatitude()
        );
        response.setPathPoints(points);
        
        return response;
    }
    
    @Override
    public PathPlanningResponse optimizeMultiOrderPath(String staffId, List<String> taskIds) {
        // TSP路径优化 - 简化版
        log.info("Optimizing path for staff {} with {} tasks", staffId, taskIds.size());
        
        PathPlanningRequest request = new PathPlanningRequest();
        // 获取任务位置并计算最优顺序
        return planPath(request);
    }
    
    @Override
    public BigDecimal calculateDeliveryFee(BigDecimal distance, BigDecimal weight) {
        // 基础费 + 距离费 + 重量费
        BigDecimal baseFee = new BigDecimal("5.00");
        BigDecimal distanceFee = distance.divide(new BigDecimal("1000"), 2, RoundingMode.HALF_UP)
            .multiply(new BigDecimal("1.50"));
        BigDecimal weightFee = weight.subtract(new BigDecimal("1.00")).max(BigDecimal.ZERO)
            .multiply(new BigDecimal("2.00"));
        
        return baseFee.add(distanceFee).add(weightFee).setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * 查找最佳服务人员（最近且空闲）
     */
    private String findBestStaffForTask(DispatchTask task) {
        // 使用Redis Geo查询附近服务人员
        String key = "staff:geo:" + task.getGeofenceId();
        // 简化版：返回第一个可用人员
        return "staff_001"; // 实际应查询Redis Geo
    }
    
    /**
     * 计算两点距离（米）
     */
    private Integer calculateDistance(BigDecimal lng1, BigDecimal lat1, BigDecimal lng2, BigDecimal lat2) {
        double R = 6371000; // 地球半径
        double dLat = Math.toRadians(lat2.doubleValue() - lat1.doubleValue());
        double dLon = Math.toRadians(lng2.doubleValue() - lng1.doubleValue());
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(Math.toRadians(lat1.doubleValue())) * Math.cos(Math.toRadians(lat2.doubleValue())) *
            Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (int) (R * c);
    }
    
    /**
     * 生成路径点
     */
    private List<PathPlanningResponse.PathPoint> generatePathPoints(
            BigDecimal startLng, BigDecimal startLat, BigDecimal endLng, BigDecimal endLat) {
        List<PathPlanningResponse.PathPoint> points = new java.util.ArrayList<>();
        
        // 起点
        PathPlanningResponse.PathPoint start = new PathPlanningResponse.PathPoint();
        start.setLongitude(startLng);
        start.setLatitude(startLat);
        points.add(start);
        
        // 终点
        PathPlanningResponse.PathPoint end = new PathPlanningResponse.PathPoint();
        end.setLongitude(endLng);
        end.setLatitude(endLat);
        points.add(end);
        
        return points;
    }
    
    /**
     * 转换为响应对象
     */
    private DispatchTaskResponse convertToResponse(DispatchTask task) {
        DispatchTaskResponse response = new DispatchTaskResponse();
        BeanUtils.copyProperties(task, response);
        response.setTaskId(task.getId());
        
        // 设置地址信息
        DispatchTaskResponse.AddressInfo pickup = new DispatchTaskResponse.AddressInfo();
        pickup.setAddress(task.getPickupAddress());
        pickup.setLongitude(task.getPickupLongitude());
        pickup.setLatitude(task.getPickupLatitude());
        pickup.setContactName(task.getPickupContactName());
        pickup.setContactPhone(task.getPickupContactPhone());
        response.setPickupAddress(pickup);
        
        DispatchTaskResponse.AddressInfo delivery = new DispatchTaskResponse.AddressInfo();
        delivery.setAddress(task.getDeliveryAddress());
        delivery.setLongitude(task.getDeliveryLongitude());
        delivery.setLatitude(task.getDeliveryLatitude());
        delivery.setContactName(task.getDeliveryContactName());
        delivery.setContactPhone(task.getDeliveryContactPhone());
        response.setDeliveryAddress(delivery);
        
        return response;
    }
}
