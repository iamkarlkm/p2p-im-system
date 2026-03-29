package com.im.backend.modules.delivery.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.im.backend.modules.delivery.entity.DeliveryOrder;
import com.im.backend.modules.delivery.dto.*;

import java.util.List;

/**
 * 配送订单服务接口
 * 本地物流配送智能调度引擎
 */
public interface DeliveryOrderService extends IService<DeliveryOrder> {

    /**
     * 创建配送订单
     */
    OrderVO createOrder(OrderCreateDTO dto);

    /**
     * 智能派单（核心算法）
     */
    DispatchResultVO smartDispatch(Long orderId);

    /**
     * 批量派单（订单池聚合）
     */
    List<DispatchResultVO> batchDispatch(List<Long> orderIds);

    /**
     * 骑手接单
     */
    boolean riderAcceptOrder(Long riderId, Long orderId);

    /**
     * 骑手拒单
     */
    boolean riderRejectOrder(Long riderId, Long orderId, String reason);

    /**
     * 骑手确认取货
     */
    boolean pickupOrder(Long riderId, Long orderId);

    /**
     * 骑手确认送达
     */
    boolean deliverOrder(Long riderId, Long orderId, DeliveryCompleteDTO dto);

    /**
     * 重新分配订单
     */
    boolean reassignOrder(Long orderId, Long newRiderId);

    /**
     * 获取订单配送进度
     */
    OrderProgressVO getOrderProgress(Long orderId);

    /**
     * 获取骑手当前所有订单
     */
    List<OrderVO> getRiderCurrentOrders(Long riderId);

    /**
     * 分页查询订单
     */
    Page<OrderVO> pageOrders(OrderQueryDTO query);

    /**
     * 获取异常订单列表
     */
    List<OrderVO> getAbnormalOrders(AbnormalOrderQueryDTO query);

    /**
     * 取消订单
     */
    boolean cancelOrder(Long orderId, String reason);

    /**
     * 检查订单是否超时
     */
    boolean checkOrderTimeout(Long orderId);
}
