package com.im.backend.modules.appointment.service;

import com.im.backend.modules.appointment.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 预约服务接口
 */
public interface AppointmentService {

    /**
     * 提交预约
     */
    AppointmentDetailDTO submitAppointment(Long userId, SubmitAppointmentRequestDTO request);

    /**
     * 取消预约
     */
    AppointmentDetailDTO cancelAppointment(Long userId, CancelAppointmentRequestDTO request);

    /**
     * 获取预约详情
     */
    AppointmentDetailDTO getAppointmentDetail(Long userId, Long appointmentId);

    /**
     * 获取我的预约列表
     */
    Page<AppointmentDetailDTO> getMyAppointments(Long userId, String status, Pageable pageable);

    /**
     * 查询可预约时段
     */
    List<AvailableTimeSlotDTO> queryAvailableSlots(Long merchantId, Long serviceId, Integer days);

    /**
     * 商家确认预约
     */
    AppointmentDetailDTO confirmAppointment(Long merchantId, Long appointmentId);

    /**
     * 用户到店签到
     */
    AppointmentDetailDTO checkIn(Long userId, Long appointmentId);

    /**
     * 完成预约
     */
    AppointmentDetailDTO completeAppointment(Long merchantId, Long appointmentId);

    /**
     * 获取商户预约列表
     */
    Page<AppointmentDetailDTO> getMerchantAppointments(Long merchantId, String status, String date, Pageable pageable);
}
