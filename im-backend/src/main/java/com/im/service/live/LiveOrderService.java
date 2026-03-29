package com.im.service.live;

import com.im.common.PageResult;
import com.im.dto.live.*;

/**
 * 直播订单服务接口
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
public interface LiveOrderService {

    /**
     * 创建订单
     *
     * @param userId  用户ID
     * @param request 订单请求
     * @return 订单信息（包含支付参数）
     */
    CreateOrderResultDTO createOrder(Long userId, CreateLiveOrderRequestDTO request);

    /**
     * 取消订单
     *
     * @param orderId 订单ID
     * @param userId  用户ID
     * @param reason  取消原因
     */
    void cancelOrder(Long orderId, Long userId, String reason);

    /**
     * 支付订单
     *
     * @param orderId 订单ID
     * @param userId  用户ID
     * @param payType 支付方式
     * @return 支付参数
     */
    PayParamsDTO payOrder(Long orderId, Long userId, Integer payType);

    /**
     * 支付回调处理
     *
     * @param payType    支付方式
     * @param tradeNo    支付流水号
     * @param outTradeNo 商户订单号
     * @param status     支付状态
     */
    void handlePayCallback(Integer payType, String tradeNo, String outTradeNo, String status);

    /**
     * 获取订单详情
     *
     * @param orderId 订单ID
     * @param userId  用户ID
     * @return 订单详情
     */
    LiveOrderDTO getOrderDetail(Long orderId, Long userId);

    /**
     * 获取用户订单列表
     *
     * @param userId 用户ID
     * @param status 订单状态
     * @param page   页码
     * @param size   每页数量
     * @return 订单列表
     */
    PageResult<LiveOrderDTO> getUserOrders(Long userId, Integer status, Integer page, Integer size);

    /**
     * 获取商户订单列表
     *
     * @param merchantId 商户ID
     * @param status     订单状态
     * @param page       页码
     * @param size       每页数量
     * @return 订单列表
     */
    PageResult<LiveOrderDTO> getMerchantOrders(Long merchantId, Integer status, Integer page, Integer size);

    /**
     * 发货
     *
     * @param orderId          订单ID
     * @param merchantId       商户ID
     * @param logisticsCompany 物流公司
     * @param logisticsNo      物流单号
     */
    void shipOrder(Long orderId, Long merchantId, String logisticsCompany, String logisticsNo);

    /**
     * 确认收货
     *
     * @param orderId 订单ID
     * @param userId  用户ID
     */
    void confirmReceive(Long orderId, Long userId);

    /**
     * 申请退款
     *
     * @param orderId     订单ID
     * @param userId      用户ID
     * @param reason      退款原因
     * @param refundAmount 退款金额（分）
     * @return 退款申请结果
     */
    RefundApplyResultDTO applyRefund(Long orderId, Long userId, String reason, Long refundAmount);

    /**
     * 处理退款
     *
     * @param orderId      订单ID
     * @param merchantId   商户ID
     * @param approve      是否同意
     * @param rejectReason 拒绝原因
     */
    void handleRefund(Long orderId, Long merchantId, Boolean approve, String rejectReason);

    /**
     * 删除订单
     *
     * @param orderId 订单ID
     * @param userId  用户ID
     */
    void deleteOrder(Long orderId, Long userId);

    /**
     * 获取订单统计
     *
     * @param merchantId 商户ID
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @return 统计数据
     */
    OrderStatisticsDTO getOrderStatistics(Long merchantId, String startTime, String endTime);
}
