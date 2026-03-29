package com.im.service.delivery;

import com.im.entity.delivery.DeliveryOrder;
import java.math.BigDecimal;
import java.util.List;

/**
 * 配送订单服务接口 - 即时配送运力调度系统
 */
public interface DeliveryOrderService {
    
    /**
     * 创建配送订单
     */
    DeliveryOrder createOrder(DeliveryOrder order);
    
    /**
     * 获取配送订单详情
     */
    DeliveryOrder getOrderById(Long orderId);
    
    /**
     * 根据订单编号获取订单
     */
    DeliveryOrder getOrderByOrderNo(String orderNo);
    
    /**
     * 智能分配订单给骑手
     */
    DeliveryOrder assignOrderToRider(Long orderId);
    
    /**
     * 手动分配订单给指定骑手
     */
    DeliveryOrder assignOrderToSpecificRider(Long orderId, Long riderId);
    
    /**
     * 骑手接单
     */
    DeliveryOrder riderAcceptOrder(Long orderId, Long riderId);
    
    /**
     * 骑手到店
     */
    DeliveryOrder riderArriveAtMerchant(Long orderId, Long riderId);
    
    /**
     * 骑手取货
     */
    DeliveryOrder riderPickUpOrder(Long orderId, Long riderId);
    
    /**
     * 订单送达
     */
    DeliveryOrder riderDeliverOrder(Long orderId, Long riderId);
    
    /**
     * 订单完成
     */
    DeliveryOrder completeOrder(Long orderId);
    
    /**
     * 取消订单
     */
    DeliveryOrder cancelOrder(Long orderId, String reason, String cancelledBy);
    
    /**
     * 获取骑手的配送订单列表
     */
    List<DeliveryOrder> getRiderOrders(Long riderId, String status, Integer page, Integer size);
    
    /**
     * 获取顾客的配送订单列表
     */
    List<DeliveryOrder> getCustomerOrders(Long customerId, String status, Integer page, Integer size);
    
    /**
     * 获取商家的配送订单列表
     */
    List<DeliveryOrder> getMerchantOrders(Long merchantId, String status, Integer page, Integer size);
    
    /**
     * 获取待分配订单列表
     */
    List<DeliveryOrder> getPendingOrders(Long zoneId, Integer page, Integer size);
    
    /**
     * 更新订单路径
     */
    void updateOrderPath(Long orderId, String pathJson);
    
    /**
     * 计算预计送达时间
     */
    void calculateEstimatedDeliveryTime(Long orderId);
    
    /**
     * 标记订单异常
     */
    DeliveryOrder markOrderException(Long orderId, String exceptionType, String reason);
    
    /**
     * 订单评价
     */
    DeliveryOrder rateOrder(Long orderId, Integer rating, String comment);
    
    /**
     * 批量分配订单(智能调度)
     */
    void batchAssignOrders(List<Long> orderIds);
    
    /**
     * 重新分配订单
     */
    DeliveryOrder reassignOrder(Long orderId, String reason);
    
    /**
     * 获取订单配送进度
     */
    DeliveryOrder getOrderProgress(Long orderId);
    
    /**
     * 检查延误订单并预警
     */
    List<DeliveryOrder> checkDelayedOrders();
    
    /**
     * 更新配送距离
     */
    void updateDeliveryDistance(Long orderId, Integer distance);
    
    /**
     * 计算配送费
     */
    BigDecimal calculateDeliveryFee(Long merchantId, BigDecimal deliveryLng, BigDecimal deliveryLat, BigDecimal weight);
}
