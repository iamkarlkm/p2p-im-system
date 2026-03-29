package com.im.backend.modules.appointment.controller;

import com.im.backend.common.api.ApiResponse;
import com.im.backend.common.api.PageResult;
import com.im.backend.modules.appointment.dto.*;
import com.im.backend.modules.appointment.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 服务预约控制器
 * 处理预约提交、取消、查询等操作
 */
@RestController
@RequestMapping("/api/v1/appointments")
@Tag(name = "服务预约", description = "服务预约与排班管理")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping
    @Operation(summary = "提交预约")
    public ApiResponse<AppointmentDetailDTO> submitAppointment(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody SubmitAppointmentRequestDTO request) {
        Long userId = Long.parseLong(userDetails.getUsername());
        AppointmentDetailDTO result = appointmentService.submitAppointment(userId, request);
        return ApiResponse.success(result);
    }

    @PostMapping("/cancel")
    @Operation(summary = "取消预约")
    public ApiResponse<AppointmentDetailDTO> cancelAppointment(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CancelAppointmentRequestDTO request) {
        Long userId = Long.parseLong(userDetails.getUsername());
        AppointmentDetailDTO result = appointmentService.cancelAppointment(userId, request);
        return ApiResponse.success(result);
    }

    @GetMapping("/{appointmentId}")
    @Operation(summary = "获取预约详情")
    public ApiResponse<AppointmentDetailDTO> getAppointmentDetail(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long appointmentId) {
        Long userId = Long.parseLong(userDetails.getUsername());
        AppointmentDetailDTO result = appointmentService.getAppointmentDetail(userId, appointmentId);
        return ApiResponse.success(result);
    }

    @GetMapping("/my")
    @Operation(summary = "获取我的预约列表")
    public ApiResponse<PageResult<AppointmentDetailDTO>> getMyAppointments(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String status,
            Pageable pageable) {
        Long userId = Long.parseLong(userDetails.getUsername());
        Page<AppointmentDetailDTO> page = appointmentService.getMyAppointments(userId, status, pageable);
        return ApiResponse.success(PageResult.from(page));
    }

    @GetMapping("/available-slots")
    @Operation(summary = "查询可预约时段")
    public ApiResponse<List<AvailableTimeSlotDTO>> queryAvailableSlots(
            @RequestParam Long merchantId,
            @RequestParam Long serviceId,
            @RequestParam(defaultValue = "7") Integer days) {
        List<AvailableTimeSlotDTO> slots = appointmentService.queryAvailableSlots(merchantId, serviceId, days);
        return ApiResponse.success(slots);
    }

    @PostMapping("/{appointmentId}/confirm")
    @Operation(summary = "商家确认预约")
    public ApiResponse<AppointmentDetailDTO> confirmAppointment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long appointmentId) {
        Long merchantId = Long.parseLong(userDetails.getUsername());
        AppointmentDetailDTO result = appointmentService.confirmAppointment(merchantId, appointmentId);
        return ApiResponse.success(result);
    }

    @PostMapping("/{appointmentId}/check-in")
    @Operation(summary = "用户到店签到")
    public ApiResponse<AppointmentDetailDTO> checkIn(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long appointmentId) {
        Long userId = Long.parseLong(userDetails.getUsername());
        AppointmentDetailDTO result = appointmentService.checkIn(userId, appointmentId);
        return ApiResponse.success(result);
    }

    @PostMapping("/{appointmentId}/complete")
    @Operation(summary = "完成预约")
    public ApiResponse<AppointmentDetailDTO> completeAppointment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long appointmentId) {
        Long merchantId = Long.parseLong(userDetails.getUsername());
        AppointmentDetailDTO result = appointmentService.completeAppointment(merchantId, appointmentId);
        return ApiResponse.success(result);
    }

    @GetMapping("/merchant/list")
    @Operation(summary = "获取商户预约列表")
    public ApiResponse<PageResult<AppointmentDetailDTO>> getMerchantAppointments(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String date,
            Pageable pageable) {
        Long merchantId = Long.parseLong(userDetails.getUsername());
        Page<AppointmentDetailDTO> page = appointmentService.getMerchantAppointments(merchantId, status, date, pageable);
        return ApiResponse.success(PageResult.from(page));
    }
}
