package com.im.backend.modules.merchant.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.im.backend.modules.merchant.order.dto.ReportDeliveryExceptionRequest;
import com.im.backend.modules.merchant.order.entity.OrderDeliveryException;
import com.im.backend.modules.merchant.order.enums.DeliveryExceptionType;
import com.im.backend.modules.merchant.order.repository.OrderDeliveryExceptionMapper;
import com.im.backend.modules.merchant.order.service.IOrderDeliveryExceptionService;
import com.im.backend.modules.merchant.order.service.IOrderFulfillmentMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单配送异常服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderDeliveryExceptionServiceImpl extends ServiceImpl<OrderDeliveryExceptionMapper, OrderDeliveryException>
        implements IOrderDeliveryExceptionService {

    private final OrderDeliveryExceptionMapper exceptionMapper;
    private final IOrderFulfillmentMessageService messageService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reportException(ReportDeliveryExceptionRequest request, Long riderId) {
        OrderDeliveryException exception = new OrderDeliveryException();
        exception.setOrderId(request.getOrderId());
        exception.setRiderId(riderId);
        exception.setExceptionType(request.getExceptionType());
        exception.setDescription(request.getDescription());
        exception.setExceptionPhotos(request.getExceptionPhotos());
        exception.setHandleStatus(0); // 待处理
        exception.setReportTime(LocalDateTime.now());
        exception.setCreateTime(LocalDateTime.now());
        exception.setUpdateTime(LocalDateTime.now());

        exceptionMapper.insert(exception);

        // 发送异常通知
        DeliveryExceptionType type = DeliveryExceptionType.fromCode(request.getExceptionType());
        String content = "配送异常: " + (type != null ? type.getDesc() : "其他");
        if (request.getDescription() != null && !request.getDescription().isEmpty()) {
            content += " - " + request.getDescription();
        }
        messageService.sendSystemMessage(request.getOrderId(), 111, content);

        log.info("上报配送异常: orderId={}, riderId={}, type={}", 
                request.getOrderId(), riderId, request.getExceptionType());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleException(Long exceptionId, Long handlerId, String handleResult, Integer newStatus) {
        exceptionMapper.updateHandleStatus(exceptionId, newStatus, handlerId, handleResult);
        
        log.info("处理配送异常: exceptionId={}, handlerId={}, status={}", exceptionId, handlerId, newStatus);
    }

    @Override
    public List<OrderDeliveryException> getOrderExceptions(Long orderId) {
        return exceptionMapper.selectByOrderId(orderId);
    }

    @Override
    public List<OrderDeliveryException> getRiderPendingExceptions(Long riderId) {
        return exceptionMapper.selectPendingByRiderId(riderId);
    }

    @Override
    public int getPendingExceptionCount() {
        return exceptionMapper.countPendingExceptions();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reassignOrder(Long orderId, Long newRiderId) {
        // 转派订单给新骑手
        // TODO: 更新订单骑手信息,通知原骑手和新骑手
        
        messageService.sendSystemMessage(orderId, 105, "订单已转派给新骑手");
        
        log.info("转派订单: orderId={}, newRiderId={}", orderId, newRiderId);
    }
}
