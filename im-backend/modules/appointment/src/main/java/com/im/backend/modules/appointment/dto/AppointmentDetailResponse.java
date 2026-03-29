package com.im.backend.modules.appointment.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 预约详情响应
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
public class AppointmentDetailResponse {

    /**
     * 预约ID
     */
    private Long id;

    /**
     * 预约单号
     */
    private String appointmentNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 商户ID
     */
    private Long merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 门店ID
     */
    private Long storeId;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 服务ID
     */
    private Long serviceId;

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 服务图片
     */
    private String serviceImage;

    /**
     * 服务人员ID
     */
    private Long staffId;

    /**
     * 服务人员姓名
     */
    private String staffName;

    /**
     * 服务人员头像
     */
    private String staffAvatar;

    /**
     * 预约日期
     */
    private LocalDate appointmentDate;

    /**
     * 开始时间
     */
    private LocalTime startTime;

    /**
     * 结束时间
     */
    private LocalTime endTime;

    /**
     * 服务时长
     */
    private Integer duration;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 状态名称
     */
    private String statusName;

    /**
     * 预约来源
     */
    private Integer source;

    /**
     * 预约人数
     */
    private Integer peopleCount;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系人电话
     */
    private String contactPhone;

    /**
     * 联系人备注
     */
    private String contactRemark;

    /**
     * 预约备注
     */
    private String remark;

    /**
     * 服务价格
     */
    private BigDecimal servicePrice;

    /**
     * 定金金额
     */
    private BigDecimal depositAmount;

    /**
     * 定金支付状态
     */
    private Integer depositStatus;

    /**
     * 定金状态名称
     */
    private String depositStatusName;

    /**
     * 实际支付金额
     */
    private BigDecimal actualAmount;

    /**
     * 取消原因
     */
    private String cancelReason;

    /**
     * 到店时间
     */
    private LocalDateTime arriveTime;

    /**
     * 服务开始时间
     */
    private LocalDateTime serviceStartTime;

    /**
     * 服务完成时间
     */
    private LocalDateTime serviceEndTime;

    /**
     * 评分
     */
    private Integer rating;

    /**
     * 评价内容
     */
    private String reviewContent;

    /**
     * 评价时间
     */
    private LocalDateTime reviewTime;

    /**
     * 服务项目列表
     */
    private List<AppointmentItemResponse> items;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否可取消
     */
    private Boolean canCancel;

    /**
     * 是否可改期
     */
    private Boolean canReschedule;

    /**
     * 是否可评价
     */
    private Boolean canReview;

    /**
     * 距离预约开始还有多久(分钟)
     */
    private Long minutesUntilAppointment;
}
