package com.im.backend.modules.merchant.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.im.backend.modules.merchant.order.dto.ConfirmDeliveryReceiptRequest;
import com.im.backend.modules.merchant.order.dto.DeliveryReceiptResponse;
import com.im.backend.modules.merchant.order.entity.OrderDeliveryReceipt;
import com.im.backend.modules.merchant.order.enums.ReceiptType;
import com.im.backend.modules.merchant.order.repository.OrderDeliveryReceiptMapper;
import com.im.backend.modules.merchant.order.service.IOrderDeliveryReceiptService;
import com.im.backend.modules.merchant.order.service.IOrderFulfillmentMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * 订单配送签收服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderDeliveryReceiptServiceImpl extends ServiceImpl<OrderDeliveryReceiptMapper, OrderDeliveryReceipt>
        implements IOrderDeliveryReceiptService {

    private final OrderDeliveryReceiptMapper receiptMapper;
    private final IOrderFulfillmentMessageService messageService;

    @Override
    public String generateReceiptCode(Long orderId) {
        // 生成4位数字签收码
        Random random = new Random();
        String code = String.format("%04d", random.nextInt(10000));
        
        // 检查是否已存在签收记录
        OrderDeliveryReceipt existing = receiptMapper.selectByOrderId(orderId);
        if (existing == null) {
            OrderDeliveryReceipt receipt = new OrderDeliveryReceipt();
            receipt.setOrderId(orderId);
            receipt.setReceiptCode(code);
            receipt.setStatus(0);
            receipt.setCreateTime(LocalDateTime.now());
            receiptMapper.insert(receipt);
        } else if (existing.getStatus() == 0) {
            // 未签收,更新签收码
            existing.setReceiptCode(code);
            existing.setUpdateTime(LocalDateTime.now());
            receiptMapper.updateById(existing);
        }
        
        return code;
    }

    @Override
    public boolean verifyReceiptCode(Long orderId, String receiptCode) {
        OrderDeliveryReceipt receipt = receiptMapper.selectByOrderId(orderId);
        if (receipt == null || receipt.getStatus() != 0) {
            return false;
        }
        return receipt.getReceiptCode().equals(receiptCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeliveryReceiptResponse confirmReceipt(ConfirmDeliveryReceiptRequest request, Long userId) {
        OrderDeliveryReceipt receipt = receiptMapper.selectByOrderId(request.getOrderId());
        if (receipt == null) {
            throw new RuntimeException("签收记录不存在");
        }

        // 验证签收码
        if (request.getReceiptType() == ReceiptType.CODE_VERIFICATION.getCode()) {
            if (!receipt.getReceiptCode().equals(request.getReceiptCode())) {
                throw new RuntimeException("签收码错误");
            }
        }

        // 更新签收信息
        receipt.setRecipientId(userId);
        receipt.setRecipientName(request.getRecipientName());
        receipt.setRecipientPhone(request.getRecipientPhone());
        receipt.setReceiptPhotoUrl(request.getReceiptPhotoUrl());
        receipt.setRemark(request.getRemark());
        receipt.setReceiptType(request.getReceiptType());
        receipt.setStatus(1);
        receipt.setReceiptTime(LocalDateTime.now());
        receipt.setUpdateTime(LocalDateTime.now());
        
        receiptMapper.updateById(receipt);

        // 发送签收成功消息
        messageService.sendSystemMessage(request.getOrderId(), 110, "订单已签收");

        log.info("订单签收成功: orderId={}, userId={}, type={}", 
                request.getOrderId(), userId, request.getReceiptType());

        return convertToResponse(receipt);
    }

    @Override
    public DeliveryReceiptResponse getReceiptInfo(Long orderId) {
        OrderDeliveryReceipt receipt = receiptMapper.selectByOrderId(orderId);
        return receipt != null ? convertToResponse(receipt) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeliveryReceiptResponse photoReceipt(Long orderId, String photoUrl, String remark) {
        OrderDeliveryReceipt receipt = receiptMapper.selectByOrderId(orderId);
        if (receipt == null) {
            receipt = new OrderDeliveryReceipt();
            receipt.setOrderId(orderId);
            receipt.setCreateTime(LocalDateTime.now());
        }

        receipt.setReceiptType(ReceiptType.PHOTO_CONFIRMATION.getCode());
        receipt.setReceiptPhotoUrl(photoUrl);
        receipt.setRemark(remark);
        receipt.setStatus(1);
        receipt.setReceiptTime(LocalDateTime.now());
        receipt.setUpdateTime(LocalDateTime.now());

        if (receipt.getId() == null) {
            receiptMapper.insert(receipt);
        } else {
            receiptMapper.updateById(receipt);
        }

        messageService.sendSystemMessage(orderId, 110, "订单已拍照签收");

        return convertToResponse(receipt);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeliveryReceiptResponse directReceipt(Long orderId, Long userId) {
        OrderDeliveryReceipt receipt = receiptMapper.selectByOrderId(orderId);
        if (receipt == null) {
            receipt = new OrderDeliveryReceipt();
            receipt.setOrderId(orderId);
            receipt.setCreateTime(LocalDateTime.now());
        }

        receipt.setRecipientId(userId);
        receipt.setReceiptType(ReceiptType.DIRECT_RECEIPT.getCode());
        receipt.setStatus(1);
        receipt.setReceiptTime(LocalDateTime.now());
        receipt.setUpdateTime(LocalDateTime.now());

        if (receipt.getId() == null) {
            receiptMapper.insert(receipt);
        } else {
            receiptMapper.updateById(receipt);
        }

        messageService.sendSystemMessage(orderId, 110, "订单已直接签收");

        return convertToResponse(receipt);
    }

    private DeliveryReceiptResponse convertToResponse(OrderDeliveryReceipt receipt) {
        DeliveryReceiptResponse response = new DeliveryReceiptResponse();
        BeanUtils.copyProperties(receipt, response);
        
        ReceiptType type = ReceiptType.fromCode(receipt.getReceiptType());
        if (type != null) {
            response.setReceiptTypeDesc(type.getDesc());
        }
        
        response.setStatusDesc(receipt.getStatus() == 1 ? "已签收" : "待签收");
        
        return response;
    }
}
