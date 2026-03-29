package com.im.backend.modules.merchant.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 订单签收记录实体
 * 记录订单送达签收信息
 */
@Data
@TableName("im_order_delivery_receipt")
public class OrderDeliveryReceipt {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 签收码
     */
    private String receiptCode;

    /**
     * 签收方式: 1-签收码验证, 2-拍照确认, 3-直接签收, 4-寄存/放门口
     */
    private Integer receiptType;

    /**
     * 签收人ID(用户)
     */
    private Long recipientId;

    /**
     * 签收人姓名
     */
    private String recipientName;

    /**
     * 签收人电话
     */
    private String recipientPhone;

    /**
     * 签收照片URL
     */
    private String receiptPhotoUrl;

    /**
     * 签收备注
     */
    private String remark;

    /**
     * 签收状态: 0-待签收, 1-已签收, 2-异常签收
     */
    private Integer status;

    /**
     * 签收时间
     */
    private LocalDateTime receiptTime;

    /**
     * 送达地址
     */
    private String deliveryAddress;

    /**
     * 送达经度
     */
    private Double deliveryLongitude;

    /**
     * 送达纬度
     */
    private Double deliveryLatitude;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
