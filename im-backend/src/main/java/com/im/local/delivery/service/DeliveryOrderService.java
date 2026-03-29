package com.im.local.delivery.service;

import com.im.core.exception.BusinessException;
import com.im.local.delivery.dto.*;
import com.im.local.delivery.entity.DeliveryOrder;
import com.im.local.delivery.enums.DeliveryStatus;
import com.im.local.delivery.event.DeliveryEventPublisher;
import com.im.local.delivery.repository.DeliveryOrderRepository;
import com.im.core.websocket.WebSocketPushService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 配送订单服务
 * 处理配送订单的创建、状态更新、取消等操作
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryOrderService {

    private final DeliveryOrderRepository orderRepository;
    private final WebSocketPushService webSocketPushService;
    private final DeliveryEventPublisher eventPublisher;
    private final DeliveryRiderService riderService;

    /**
     * 创建配送订单
     */
    @Transactional
    public DeliveryOrderDTO createOrder(CreateDeliveryOrderRequest request) {
        log.info("创建配送订单，商家ID: {}, 用户ID: {}", 
            request.getMerchantId(), request.getUserId());
        
        DeliveryOrder order = new DeliveryOrder();
        order.setOrderNo(generateOrderNo());
        order.setMerchantId(request.getMerchantId());
        order.setUserId(request.getUserId());
        order.setStatus(DeliveryStatus.PENDING_DISPATCH);
        
        // 取货信息
        order.setPickupAddress(request.getPickupAddress());
        order.setPickupLatitude(request.getPickupLatitude());
        order.setPickupLongitude(request.getPickupLongitude());
        order.setPickupContactName(request.getPickupContactName());
        order.setPickupContactPhone(request.getPickupContactPhone());
        
        // 送货信息
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setDeliveryLatitude(request.getDeliveryLatitude());
        order.setDeliveryLongitude(request.getDeliveryLongitude());
        order.setDeliveryContactName(request.getDeliveryContactName());
        order.setDeliveryContactPhone(request.getDeliveryContactPhone());
        
        // 订单信息
        order.setItems(request.getItems());
        order.setTotalAmount(request.getTotalAmount());
        order.setDeliveryFee(request.getDeliveryFee());
        order.setRiderIncome(calculateRiderIncome(request.getDeliveryFee()));
        order.setRemark(request.getRemark());
        order.setPickupCode(generatePickupCode());
        order.setDeliveryCode(generateDeliveryCode());
        
        // 时间信息
        order.setCreateTime(LocalDateTime.now());
        order.setExpectedDeliveryTime(request.getExpectedDeliveryTime());
        
        // 区域编码
        order.setRegionCode(request.getRegionCode());
        
        orderRepository.save(order);
        
        // 发布订单创建事件
        eventPublisher.publishOrderCreated(order);
        
        log.info("配送订单创建成功，订单号: {}", order.getOrderNo());
        
        return convertToDTO(order);
    }

    /**
     * 获取订单详情
     */
    public DeliveryOrderDetailDTO getOrderDetail(Long orderId) {
        DeliveryOrder order = orderRepository.findById(orderId)
            .orElseThrow(() -> new BusinessException("订单不存在: " + orderId));
        
        return convertToDetailDTO(order);
    }

    /**
     * 取消订单
     */
    @Transactional
    public void cancelOrder(Long orderId, String reason) {
        log.info("取消配送订单，订单ID: {}, 原因: {}", orderId, reason);
        
        DeliveryOrder order = orderRepository.findById(orderId)
            .orElseThrow(() -> new BusinessException("订单不存在: " + orderId));
        
        // 检查订单状态
        if (order.getStatus() == DeliveryStatus.DELIVERED || 
            order.getStatus() == DeliveryStatus.CANCELLED) {
            throw new BusinessException("订单状态不允许取消");
        }
        
        DeliveryStatus oldStatus = order.getStatus();
        order.setStatus(DeliveryStatus.CANCELLED);
        order.setCancelReason(reason);
        order.setCancelTime(LocalDateTime.now());
        
        orderRepository.save(order);
        
        // 如果已分配骑手，通知骑手
        if (order.getRiderId() != null) {
            notifyRiderOrderCancelled(order.getRiderId(), order);
        }
        
        // 通知用户
        notifyUserOrderCancelled(order.getUserId(), order);
        
        // 通知商家
        notifyMerchantOrderCancelled(order.getMerchantId(), order);
        
        // 发布订单取消事件
        eventPublisher.publishOrderCancelled(order, oldStatus, reason);
        
        log.info("配送订单取消成功，订单ID: {}", orderId);
    }

    /**
     * 商家发货
     */
    @Transactional
    public void merchantShip(Long orderId, Long merchantId, MerchantShipRequest request) {
        log.info("商家发货，订单ID: {}, 商家ID: {}", orderId, merchantId);
        
        DeliveryOrder order = orderRepository.findById(orderId)
            .orElseThrow(() -> new BusinessException("订单不存在: " + orderId));
        
        if (!order.getMerchantId().equals(merchantId)) {
            throw new BusinessException("无权操作此订单");
        }
        
        if (order.getStatus() != DeliveryStatus.PENDING_PICKUP) {
            throw new BusinessException("订单状态不正确");
        }
        
        order.setStatus(DeliveryStatus.PENDING_PICKUP);
        order.setShipTime(LocalDateTime.now());
        order.setPackageWeight(request.getPackageWeight());
        order.setPackageCount(request.getPackageCount());
        order.setPackageRemark(request.getRemark());
        
        orderRepository.save(order);
        
        // 通知骑手
        if (order.getRiderId() != null) {
            notifyRiderOrderReady(order.getRiderId(), order);
        }
        
        log.info("商家发货成功，订单ID: {}", orderId);
    }

    /**
     * 骑手取货确认
     */
    @Transactional
    public void confirmPickup(Long orderId, Long riderId, String pickupCode) {
        log.info("骑手取货确认，订单ID: {}, 骑手ID: {}", orderId, riderId);
        
        DeliveryOrder order = orderRepository.findById(orderId)
            .orElseThrow(() -> new BusinessException("订单不存在: " + orderId));
        
        if (!order.getRiderId().equals(riderId)) {
            throw new BusinessException("无权操作此订单");
        }
        
        if (order.getStatus() != DeliveryStatus.RIDER_ASSIGNED) {
            throw new BusinessException("订单状态不正确");
        }
        
        // 验证取货码
        if (pickupCode != null && !pickupCode.equals(order.getPickupCode())) {
            throw new BusinessException("取货码不正确");
        }
        
        order.setStatus(DeliveryStatus.PICKED_UP);
        order.setPickupTime(LocalDateTime.now());
        
        orderRepository.save(order);
        
        // 通知用户
        notifyUserOrderPickedUp(order.getUserId(), order);
        
        // 通知商家
        notifyMerchantOrderPickedUp(order.getMerchantId(), order);
        
        log.info("骑手取货确认成功，订单ID: {}", orderId);
    }

    /**
     * 订单送达确认
     */
    @Transactional
    public void confirmDelivery(Long orderId, Long riderId, String deliveryCode) {
        log.info("订单送达确认，订单ID: {}, 骑手ID: {}", orderId, riderId);
        
        DeliveryOrder order = orderRepository.findById(orderId)
            .orElseThrow(() -> new BusinessException("订单不存在: " + orderId));
        
        if (!order.getRiderId().equals(riderId)) {
            throw new BusinessException("无权操作此订单");
        }
        
        if (order.getStatus() != DeliveryStatus.PICKED_UP && 
            order.getStatus() != DeliveryStatus.DELIVERING) {
            throw new BusinessException("订单状态不正确");
        }
        
        // 验证送达码
        if (deliveryCode != null && !deliveryCode.equals(order.getDeliveryCode())) {
            throw new BusinessException("送达码不正确");
        }
        
        order.setStatus(DeliveryStatus.DELIVERED);
        order.setDeliveryTime(LocalDateTime.now());
        
        // 计算实际配送时长
        if (order.getPickupTime() != null) {
            order.setActualDeliveryMinutes(
                (int) java.time.Duration.between(
                    order.getPickupTime(), 
                    order.getDeliveryTime()
                ).toMinutes()
            );
        }
        
        orderRepository.save(order);
        
        // 更新骑手统计
        riderService.updateDeliveryStats(riderId, order);
        
        // 通知用户
        notifyUserOrderDelivered(order.getUserId(), order);
        
        // 通知商家
        notifyMerchantOrderDelivered(order.getMerchantId(), order);
        
        // 发布订单完成事件
        eventPublisher.publishOrderCompleted(order);
        
        log.info("订单送达确认成功，订单ID: {}", orderId);
    }

    /**
     * 更新订单状态
     */
    @Transactional
    public void updateStatus(Long orderId, DeliveryStatus status, String remark) {
        DeliveryOrder order = orderRepository.findById(orderId)
            .orElseThrow(() -> new BusinessException("订单不存在: " + orderId));
        
        DeliveryStatus oldStatus = order.getStatus();
        order.setStatus(status);
        order.setStatusRemark(remark);
        orderRepository.save(order);
        
        // 发布状态变更事件
        eventPublisher.publishStatusChanged(order, oldStatus, status, remark);
    }

    /**
     * 获取用户订单列表
     */
    public PageResult<DeliveryOrderDTO> getUserOrders(
            Long userId, 
            List<DeliveryStatus> statuses, 
            Pageable pageable) {
        
        Page<DeliveryOrder> page;
        if (statuses == null || statuses.isEmpty()) {
            page = orderRepository.findByUserIdOrderByCreateTimeDesc(userId, pageable);
        } else {
            page = orderRepository.findByUserIdAndStatusInOrderByCreateTimeDesc(
                userId, statuses, pageable);
        }
        
        List<DeliveryOrderDTO> orders = page.getContent().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        return PageResult.of(orders, page.getTotalElements(), page.getTotalPages());
    }

    /**
     * 获取骑手订单列表
     */
    public PageResult<DeliveryOrderDTO> getRiderOrders(
            Long riderId, 
            List<DeliveryStatus> statuses, 
            Pageable pageable) {
        
        Page<DeliveryOrder> page;
        if (statuses == null || statuses.isEmpty()) {
            page = orderRepository.findByRiderIdOrderByCreateTimeDesc(riderId, pageable);
        } else {
            page = orderRepository.findByRiderIdAndStatusInOrderByCreateTimeDesc(
                riderId, statuses, pageable);
        }
        
        List<DeliveryOrderDTO> orders = page.getContent().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        return PageResult.of(orders, page.getTotalElements(), page.getTotalPages());
    }

    /**
     * 获取商家订单列表
     */
    public PageResult<DeliveryOrderDTO> getMerchantOrders(
            Long merchantId, 
            List<DeliveryStatus> statuses, 
            Pageable pageable) {
        
        Page<DeliveryOrder> page;
        if (statuses == null || statuses.isEmpty()) {
            page = orderRepository.findByMerchantIdOrderByCreateTimeDesc(merchantId, pageable);
        } else {
            page = orderRepository.findByMerchantIdAndStatusInOrderByCreateTimeDesc(
                merchantId, statuses, pageable);
        }
        
        List<DeliveryOrderDTO> orders = page.getContent().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        return PageResult.of(orders, page.getTotalElements(), page.getTotalPages());
    }

    // ==================== 私有方法 ====================

    private String generateOrderNo() {
        return "DEL" + System.currentTimeMillis() + 
               String.format("%04d", (int)(Math.random() * 10000));
    }

    private String generatePickupCode() {
        return String.format("%06d", (int)(Math.random() * 1000000));
    }

    private String generateDeliveryCode() {
        return String.format("%04d", (int)(Math.random() * 10000));
    }

    private double calculateRiderIncome(double deliveryFee) {
        // 骑手获得配送费的80%
        return deliveryFee * 0.8;
    }

    private DeliveryOrderDTO convertToDTO(DeliveryOrder order) {
        return DeliveryOrderDTO.builder()
            .id(order.getId())
            .orderNo(order.getOrderNo())
            .status(order.getStatus())
            .statusText(order.getStatus().getDescription())
            .merchantId(order.getMerchantId())
            .userId(order.getUserId())
            .riderId(order.getRiderId())
            .pickupAddress(order.getPickupAddress())
            .deliveryAddress(order.getDeliveryAddress())
            .totalAmount(order.getTotalAmount())
            .deliveryFee(order.getDeliveryFee())
            .createTime(order.getCreateTime())
            .expectedDeliveryTime(order.getExpectedDeliveryTime())
            .actualDeliveryTime(order.getDeliveryTime())
            .build();
    }

    private DeliveryOrderDetailDTO convertToDetailDTO(DeliveryOrder order) {
        DeliveryOrderDetailDTO detail = new DeliveryOrderDetailDTO();
        detail.setId(order.getId());
        detail.setOrderNo(order.getOrderNo());
        detail.setStatus(order.getStatus());
        detail.setStatusText(order.getStatus().getDescription());
        
        // 取货信息
        detail.setPickupAddress(order.getPickupAddress());
        detail.setPickupLatitude(order.getPickupLatitude());
        detail.setPickupLongitude(order.getPickupLongitude());
        detail.setPickupContactName(order.getPickupContactName());
        detail.setPickupContactPhone(order.getPickupContactPhone());
        
        // 送货信息
        detail.setDeliveryAddress(order.getDeliveryAddress());
        detail.setDeliveryLatitude(order.getDeliveryLatitude());
        detail.setDeliveryLongitude(order.getDeliveryLongitude());
        detail.setDeliveryContactName(order.getDeliveryContactName());
        detail.setDeliveryContactPhone(order.getDeliveryContactPhone());
        
        // 订单信息
        detail.setItems(order.getItems());
        detail.setTotalAmount(order.getTotalAmount());
        detail.setDeliveryFee(order.getDeliveryFee());
        detail.setRemark(order.getRemark());
        
        // 时间信息
        detail.setCreateTime(order.getCreateTime());
        detail.setAcceptTime(order.getAcceptTime());
        detail.setPickupTime(order.getPickupTime());
        detail.setDeliveryTime(order.getDeliveryTime());
        detail.setExpectedDeliveryTime(order.getExpectedDeliveryTime());
        detail.setActualDeliveryMinutes(order.getActualDeliveryMinutes());
        
        // 骑手信息
        detail.setRiderId(order.getRiderId());
        
        return detail;
    }

    // ==================== 通知方法 ====================

    private void notifyRiderOrderCancelled(Long riderId, DeliveryOrder order) {
        webSocketPushService.pushToUser(riderId, "ORDER_CANCELLED",
            OrderCancelledNotification.builder()
                .orderId(order.getId())
                .orderNo(order.getOrderNo())
                .reason(order.getCancelReason())
                .build());
    }

    private void notifyUserOrderCancelled(Long userId, DeliveryOrder order) {
        webSocketPushService.pushToUser(userId, "ORDER_CANCELLED",
            OrderCancelledNotification.builder()
                .orderId(order.getId())
                .orderNo(order.getOrderNo())
                .reason(order.getCancelReason())
                .build());
    }

    private void notifyMerchantOrderCancelled(Long merchantId, DeliveryOrder order) {
        webSocketPushService.pushToUser(merchantId, "ORDER_CANCELLED",
            OrderCancelledNotification.builder()
                .orderId(order.getId())
                .orderNo(order.getOrderNo())
                .reason(order.getCancelReason())
                .build());
    }

    private void notifyRiderOrderReady(Long riderId, DeliveryOrder order) {
        webSocketPushService.pushToUser(riderId, "ORDER_READY_FOR_PICKUP",
            OrderReadyNotification.builder()
                .orderId(order.getId())
                .orderNo(order.getOrderNo())
                .pickupAddress(order.getPickupAddress())
                .pickupCode(order.getPickupCode())
                .build());
    }

    private void notifyUserOrderPickedUp(Long userId, DeliveryOrder order) {
        webSocketPushService.pushToUser(userId, "ORDER_PICKED_UP",
            OrderPickedUpNotification.builder()
                .orderId(order.getId())
                .orderNo(order.getOrderNo())
                .estimatedDeliveryTime(order.getEstimatedDeliveryTime())
                .build());
    }

    private void notifyMerchantOrderPickedUp(Long merchantId, DeliveryOrder order) {
        webSocketPushService.pushToUser(merchantId, "ORDER_PICKED_UP",
            OrderPickedUpNotification.builder()
                .orderId(order.getId())
                .orderNo(order.getOrderNo())
                .riderId(order.getRiderId())
                .build());
    }

    private void notifyUserOrderDelivered(Long userId, DeliveryOrder order) {
        webSocketPushService.pushToUser(userId, "ORDER_DELIVERED",
            OrderDeliveredNotification.builder()
                .orderId(order.getId())
                .orderNo(order.getOrderNo())
                .deliveryTime(order.getDeliveryTime())
                .build());
    }

    private void notifyMerchantOrderDelivered(Long merchantId, DeliveryOrder order) {
        webSocketPushService.pushToUser(merchantId, "ORDER_DELIVERED",
            OrderDeliveredNotification.builder()
                .orderId(order.getId())
                .orderNo(order.getOrderNo())
                .riderId(order.getRiderId())
                .build());
    }
}
