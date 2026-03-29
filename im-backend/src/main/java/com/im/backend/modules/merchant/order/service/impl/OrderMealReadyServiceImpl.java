package com.im.backend.modules.merchant.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.im.backend.modules.merchant.order.dto.ConfirmMealReadyRequest;
import com.im.backend.modules.merchant.order.entity.OrderMealReadyRecord;
import com.im.backend.modules.merchant.order.repository.OrderMealReadyRecordMapper;
import com.im.backend.modules.merchant.order.service.IOrderFulfillmentMessageService;
import com.im.backend.modules.merchant.order.service.IOrderMealReadyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 订单出餐服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderMealReadyServiceImpl extends ServiceImpl<OrderMealReadyRecordMapper, OrderMealReadyRecord>
        implements IOrderMealReadyService {

    private final OrderMealReadyRecordMapper mealReadyMapper;
    private final IOrderFulfillmentMessageService messageService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmMealReady(ConfirmMealReadyRequest request) {
        OrderMealReadyRecord record = mealReadyMapper.selectByOrderId(request.getOrderId());
        
        if (record == null) {
            record = new OrderMealReadyRecord();
            record.setOrderId(request.getOrderId());
            record.setMerchantId(request.getOperatorId()); // 简化处理
            record.setCreateTime(LocalDateTime.now());
        }

        record.setOperatorId(request.getOperatorId());
        record.setMealPhotos(request.getMealPhotos());
        record.setRemark(request.getRemark());
        record.setEstimatedWaitMinutes(request.getEstimatedWaitMinutes());
        record.setStatus(1);
        record.setMealReadyTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());

        if (record.getId() == null) {
            mealReadyMapper.insert(record);
        } else {
            mealReadyMapper.updateById(record);
        }

        // 发送出餐消息
        String content = "餐品已出餐";
        if (request.getEstimatedWaitMinutes() != null) {
            content += ",预计等待" + request.getEstimatedWaitMinutes() + "分钟";
        }
        messageService.sendSystemMessage(request.getOrderId(), 104, content);

        log.info("确认出餐: orderId={}, operatorId={}", request.getOrderId(), request.getOperatorId());
    }

    @Override
    public OrderMealReadyRecord getMealReadyRecord(Long orderId) {
        return mealReadyMapper.selectByOrderId(orderId);
    }

    @Override
    public void estimateMealReadyTime(Long orderId, Integer estimatedMinutes) {
        OrderMealReadyRecord record = mealReadyMapper.selectByOrderId(orderId);
        if (record == null) {
            record = new OrderMealReadyRecord();
            record.setOrderId(orderId);
            record.setCreateTime(LocalDateTime.now());
        }
        
        record.setEstimatedWaitMinutes(estimatedMinutes);
        record.setStatus(0); // 制作中
        record.setUpdateTime(LocalDateTime.now());

        if (record.getId() == null) {
            mealReadyMapper.insert(record);
        } else {
            mealReadyMapper.updateById(record);
        }

        messageService.sendSystemMessage(orderId, 104, "商家开始备餐,预计" + estimatedMinutes + "分钟");
        
        log.info("更新预计出餐时间: orderId={}, minutes={}", orderId, estimatedMinutes);
    }

    @Override
    public boolean isMealReady(Long orderId) {
        OrderMealReadyRecord record = mealReadyMapper.selectByOrderId(orderId);
        return record != null && record.getStatus() == 1;
    }
}
