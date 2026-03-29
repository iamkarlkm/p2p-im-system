package com.im.backend.modules.appointment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.im.backend.common.core.result.PageResult;
import com.im.backend.modules.appointment.dto.*;
import com.im.backend.modules.appointment.entity.Appointment;
import com.im.backend.modules.appointment.entity.AppointmentTimeConfig;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 服务预约Service接口
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
public interface AppointmentService extends IService<Appointment> {

    /**
     * 创建预约
     *
     * @param request 创建请求
     * @return 预约详情
     */
    AppointmentDetailResponse createAppointment(CreateAppointmentRequest request);

    /**
     * 获取预约详情
     *
     * @param appointmentId 预约ID
     * @return 预约详情
     */
    AppointmentDetailResponse getAppointmentDetail(Long appointmentId);

    /**
     * 获取用户预约列表
     *
     * @param request 查询请求
     * @return 分页结果
     */
    PageResult<AppointmentListResponse> getUserAppointments(AppointmentQueryRequest request);

    /**
     * 获取商户预约列表
     *
     * @param request 查询请求
     * @return 分页结果
     */
    PageResult<AppointmentListResponse> getMerchantAppointments(MerchantAppointmentQueryRequest request);

    /**
     * 取消预约
     *
     * @param appointmentId 预约ID
     * @param reason 取消原因
     * @param cancelBy 取消人 (0-用户, 1-商户, 2-系统)
     * @return 是否成功
     */
    boolean cancelAppointment(Long appointmentId, String reason, Integer cancelBy);

    /**
     * 改期预约
     *
     * @param appointmentId 预约ID
     * @param newDate 新日期
     * @param newStartTime 新开始时间
     * @param newEndTime 新结束时间
     * @return 新预约详情
     */
    AppointmentDetailResponse rescheduleAppointment(Long appointmentId, LocalDate newDate, 
            LocalTime newStartTime, LocalTime newEndTime);

    /**
     * 确认预约
     *
     * @param appointmentId 预约ID
     * @return 是否成功
     */
    boolean confirmAppointment(Long appointmentId);

    /**
     * 标记到店
     *
     * @param appointmentId 预约ID
     * @return 是否成功
     */
    boolean markArrived(Long appointmentId);

    /**
     * 开始服务
     *
     * @param appointmentId 预约ID
     * @return 是否成功
     */
    boolean startService(Long appointmentId);

    /**
     * 完成服务
     *
     * @param appointmentId 预约ID
     * @return 是否成功
     */
    boolean completeService(Long appointmentId);

    /**
     * 评价预约
     *
     * @param appointmentId 预约ID
     * @param rating 评分
     * @param content 评价内容
     * @return 是否成功
     */
    boolean reviewAppointment(Long appointmentId, Integer rating, String content);

    /**
     * 获取可预约时段
     *
     * @param merchantId 商户ID
     * @param storeId 门店ID
     * @param serviceId 服务ID
     * @param date 日期
     * @return 可预约时段列表
     */
    List<AvailableTimeSlotResponse> getAvailableTimeSlots(Long merchantId, Long storeId, 
            Long serviceId, LocalDate date);

    /**
     * 智能推荐预约时段
     *
     * @param merchantId 商户ID
     * @param storeId 门店ID
     * @param serviceId 服务ID
     * @param date 日期
     * @return 推荐的时段列表
     */
    List<AvailableTimeSlotResponse> recommendTimeSlots(Long merchantId, Long storeId, 
            Long serviceId, LocalDate date);

    /**
     * 获取预约统计
     *
     * @param merchantId 商户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计数据
     */
    AppointmentStatisticsResponse getAppointmentStatistics(Long merchantId, 
            LocalDate startDate, LocalDate endDate);

    /**
     * 获取用户预约统计
     *
     * @param userId 用户ID
     * @return 统计数据
     */
    UserAppointmentStatistics getUserAppointmentStatistics(Long userId);

    /**
     * 检查时段是否可预约
     *
     * @param merchantId 商户ID
     * @param storeId 门店ID
     * @param serviceId 服务ID
     * @param date 日期
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 检查结果
     */
    boolean checkTimeSlotAvailable(Long merchantId, Long storeId, Long serviceId,
            LocalDate date, LocalTime startTime, LocalTime endTime);

    /**
     * 批量确认预约
     *
     * @param appointmentIds 预约ID列表
     * @return 成功数量
     */
    int batchConfirmAppointments(List<Long> appointmentIds);

    /**
     * 检查并标记爽约
     * 定时任务调用
     *
     * @return 标记数量
     */
    int checkAndMarkNoShows();

    /**
     * 发送预约提醒
     * 定时任务调用
     *
     * @return 发送数量
     */
    int sendAppointmentReminders();

    /**
     * 获取今日预约列表
     *
     * @param merchantId 商户ID
     * @return 预约列表
     */
    List<AppointmentListResponse> getTodayAppointments(Long merchantId);

    /**
     * 获取待处理预约数量
     *
     * @param merchantId 商户ID
     * @return 待处理数量
     */
    int getPendingAppointmentCount(Long merchantId);
}
