package com.im.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.im.backend.dto.DeliveryOrderRequest;
import com.im.backend.dto.DeliveryOrderResponse;
import com.im.backend.entity.DeliveryOrder;
import com.im.backend.entity.DeliveryRider;
import com.im.backend.repository.DeliveryOrderMapper;
import com.im.backend.repository.DeliveryRiderMapper;
import com.im.backend.service.IDeliveryService;
import com.im.backend.util.DistanceUtil;
import com.im.backend.util.GeoHashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 配送服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl extends ServiceImpl<DeliveryOrderMapper, DeliveryOrder> implements IDeliveryService {

    private final DeliveryOrderMapper orderMapper;
    private final DeliveryRiderMapper riderMapper;

    @Override
    @Transactional
    public DeliveryOrderResponse createDeliveryOrder(DeliveryOrderRequest request, Long userId) {
        // 计算配送距离
        int distance = DistanceUtil.calculateDistance(
                request.getPickupLatitude(), request.getPickupLongitude(),
                request.getDeliveryLatitude(), request.getDeliveryLongitude());

        // 计算配送费
        double fee = calculateDeliveryFee(
                request.getPickupLongitude(), request.getPickupLatitude(),
                request.getDeliveryLongitude(), request.getDeliveryLatitude(),
                request.getItemWeight());

        // 预计送达时间(配送时间+30分钟准备时间)
        int deliveryMinutes = Math.max(30, distance / 200); // 假设200米/分钟
        LocalDateTime estimatedArrival = LocalDateTime.now().plusMinutes(30 + deliveryMinutes);

        DeliveryOrder order = DeliveryOrder.builder()
                .orderNo(generateOrderNo())
                .merchantOrderId(request.getMerchantOrderId())
                .merchantId(request.getMerchantId())
                .userId(userId)
                .status("PENDING")
                .pickupAddress(request.getPickupAddress())
                .pickupLongitude(request.getPickupLongitude())
                .pickupLatitude(request.getPickupLatitude())
                .deliveryAddress(request.getDeliveryAddress())
                .deliveryLongitude(request.getDeliveryLongitude())
                .deliveryLatitude(request.getDeliveryLatitude())
                .receiverName(request.getReceiverName())
                .receiverPhone(request.getReceiverPhone())
                .deliveryDistance(distance)
                .deliveryFee(BigDecimal.valueOf(fee))
                .estimatedArrivalTime(estimatedArrival)
                .itemDescription(request.getItemDescription())
                .itemWeight(request.getItemWeight())
                .remark(request.getRemark())
                .timeoutMinutes(deliveryMinutes + 30)
                .build();

        save(order);

        // 触发智能派单
        smartDispatch(order.getId());

        return convertToResponse(order);
    }

    @Override
    public DeliveryOrderResponse getDeliveryOrderDetail(Long orderId) {
        DeliveryOrder order = getById(orderId);
        return order != null ? convertToResponse(order) : null;
    }

    @Override
    public DeliveryOrderResponse getDeliveryOrderByNo(String orderNo) {
        DeliveryOrder order = orderMapper.selectByOrderNo(orderNo);
        return order != null ? convertToResponse(order) : null;
    }

    @Override
    @Transactional
    public boolean assignRider(Long orderId, Long riderId) {
        int rows = orderMapper.assignRider(orderId, riderId);
        if (rows > 0) {
            riderMapper.updateWorkStatus(riderId, "BUSY");
            riderMapper.incrementOrderCount(riderId);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean pickupOrder(Long orderId, Long riderId) {
        DeliveryOrder order = getById(orderId);
        if (order == null || !riderId.equals(order.getRiderId())) {
            return false;
        }

        order.setStatus("DELIVERING");
        order.setPickupTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        return updateById(order);
    }

    @Override
    @Transactional
    public boolean startDelivery(Long orderId, Long riderId) {
        DeliveryOrder order = getById(orderId);
        if (order == null || !riderId.equals(order.getRiderId())) {
            return false;
        }

        order.setStatus("DELIVERING");
        order.setUpdateTime(LocalDateTime.now());
        return updateById(order);
    }

    @Override
    @Transactional
    public boolean confirmDelivery(Long orderId, Long riderId) {
        DeliveryOrder order = getById(orderId);
        if (order == null || !riderId.equals(order.getRiderId())) {
            return false;
        }

        order.setStatus("COMPLETED");
        order.setActualArrivalTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        if (updateById(order)) {
            riderMapper.incrementCompletedCount(riderId);
            riderMapper.updateWorkStatus(riderId, "IDLE");
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean cancelDeliveryOrder(Long orderId, String reason) {
        DeliveryOrder order = getById(orderId);
        if (order == null || !canCancel(order.getStatus())) {
            return false;
        }

        // 如果已分配骑手，释放骑手状态
        if (order.getRiderId() != null) {
            riderMapper.updateWorkStatus(order.getRiderId(), "IDLE");
        }

        order.setStatus("CANCELLED");
        order.setCancelReason(reason);
        order.setCancelledTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        return updateById(order);
    }

    @Override
    public List<DeliveryOrderResponse> getUserDeliveryOrders(Long userId, Integer limit) {
        return orderMapper.selectByUserId(userId, limit)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<DeliveryOrderResponse> getRiderDeliveryOrders(Long riderId, Integer limit) {
        return orderMapper.selectByRiderId(riderId, limit)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<DeliveryOrderResponse> getRiderCurrentOrders(Long riderId) {
        return orderMapper.selectRiderCurrentOrders(riderId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public boolean updateRiderLocation(Long riderId, Double longitude, Double latitude) {
        return riderMapper.updateLocation(riderId, longitude, latitude) > 0;
    }

    @Override
    @Transactional
    public boolean smartDispatch(Long orderId) {
        DeliveryOrder order = getById(orderId);
        if (order == null || !"PENDING".equals(order.getStatus())) {
            return false;
        }

        // 查找附近空闲骑手
        List<DeliveryRider> nearbyRiders = riderMapper.selectNearbyIdleRiders(
                order.getPickupLongitude(), order.getPickupLatitude(), 5);

        if (!nearbyRiders.isEmpty()) {
            // 选择距离最近的骑手
            DeliveryRider selectedRider = nearbyRiders.get(0);
            return assignRider(orderId, selectedRider.getId());
        }

        // 如果没有附近骑手，查找任意空闲骑手
        List<DeliveryRider> idleRiders = riderMapper.selectIdleRiders(1);
        if (!idleRiders.isEmpty()) {
            return assignRider(orderId, idleRiders.get(0).getId());
        }

        log.warn("No available rider for order: {}", orderId);
        return false;
    }

    @Override
    public Double calculateDeliveryFee(Double pickupLng, Double pickupLat, Double deliveryLng, Double deliveryLat, Double weight) {
        // 计算距离
        int distance = DistanceUtil.calculateDistance(pickupLat, pickupLng, deliveryLat, deliveryLng);

        // 基础费用5元
        double baseFee = 5.0;

        // 距离费用(每公里1元)
        double distanceFee = distance / 1000.0 * 1.0;

        // 重量费用(超过1kg每公斤1元)
        double weightFee = weight != null && weight > 1 ? (weight - 1) * 1.0 : 0;

        double totalFee = baseFee + distanceFee + weightFee;

        // 保留两位小数
        return new BigDecimal(totalFee).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    // ============ 私有方法 ============

    private String generateOrderNo() {
        return "D" + LocalDateTime.now().toString().replaceAll("[-:T.]", "").substring(0, 14) + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    private boolean canCancel(String status) {
        return "PENDING".equals(status) || "ASSIGNED".equals(status);
    }

    private DeliveryOrderResponse convertToResponse(DeliveryOrder order) {
        DeliveryOrderResponse.RiderInfo riderInfo = null;
        if (order.getRiderId() != null) {
            DeliveryRider rider = riderMapper.selectById(order.getRiderId());
            if (rider != null) {
                riderInfo = DeliveryOrderResponse.RiderInfo.builder()
                        .riderId(rider.getId())
                        .riderName(rider.getRealName())
                        .riderPhone(rider.getPhone())
                        .avatarUrl(rider.getAvatarUrl())
                        .rating(rider.getRating())
                        .currentLongitude(rider.getCurrentLongitude())
                        .currentLatitude(rider.getCurrentLatitude())
                        .build();
            }
        }

        int remainingMinutes = 0;
        if (order.getEstimatedArrivalTime() != null) {
            remainingMinutes = (int) ChronoUnit.MINUTES.between(LocalDateTime.now(), order.getEstimatedArrivalTime());
            if (remainingMinutes < 0) remainingMinutes = 0;
        }

        return DeliveryOrderResponse.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .merchantOrderId(order.getMerchantOrderId())
                .status(order.getStatus())
                .statusText(order.getStatusText())
                .pickupAddress(order.getPickupAddress())
                .deliveryAddress(order.getDeliveryAddress())
                .receiverName(order.getReceiverName())
                .receiverPhone(order.getReceiverPhone())
                .deliveryDistance(order.getDeliveryDistance())
                .deliveryFee(order.getDeliveryFee())
                .estimatedArrivalTime(order.getEstimatedArrivalTime())
                .remainingMinutes(remainingMinutes)
                .rider(riderInfo)
                .pickupTime(order.getPickupTime())
                .actualArrivalTime(order.getActualArrivalTime())
                .itemDescription(order.getItemDescription())
                .remark(order.getRemark())
                .createTime(order.getCreateTime())
                .timeout(order.isTimeout())
                .build();
    }
}
