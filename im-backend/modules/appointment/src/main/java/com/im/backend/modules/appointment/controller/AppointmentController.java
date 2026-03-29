package com.im.backend.modules.appointment.controller;

import com.im.backend.common.core.result.PageResult;
import com.im.backend.common.core.result.Result;
import com.im.backend.modules.appointment.dto.*;
import com.im.backend.modules.appointment.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 预约管理Controller
 * 本地生活服务预约与排班管理系统
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
@Tag(name = "预约管理", description = "本地生活服务预约相关接口")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    @Operation(summary = "创建预约", description = "用户创建新的服务预约")
    public Result<AppointmentDetailResponse> createAppointment(
            @RequestBody @Validated CreateAppointmentRequest request) {
        return Result.success(appointmentService.createAppointment(request));
    }

    @GetMapping("/{appointmentId}")
    @Operation(summary = "获取预约详情", description = "获取预约详情信息")
    public Result<AppointmentDetailResponse> getAppointmentDetail(
            @PathVariable Long appointmentId) {
        return Result.success(appointmentService.getAppointmentDetail(appointmentId));
    }

    @GetMapping("/user/my")
    @Operation(summary = "获取我的预约列表", description = "获取当前用户的预约列表")
    public Result<PageResult<AppointmentListResponse>> getUserAppointments(
            @Validated AppointmentQueryRequest request) {
        return Result.success(appointmentService.getUserAppointments(request));
    }

    @GetMapping("/merchant/list")
    @Operation(summary = "获取商户预约列表", description = "商户获取预约列表")
    public Result<PageResult<AppointmentListResponse>> getMerchantAppointments(
            @Validated MerchantAppointmentQueryRequest request) {
        return Result.success(appointmentService.getMerchantAppointments(request));
    }

    @PostMapping("/{appointmentId}/cancel")
    @Operation(summary = "取消预约", description = "取消指定预约")
    public Result<Boolean> cancelAppointment(
            @PathVariable Long appointmentId,
            @RequestParam(required = false) String reason) {
        return Result.success(appointmentService.cancelAppointment(appointmentId, reason, 0));
    }

    @PostMapping("/{appointmentId}/reschedule")
    @Operation(summary = "改期预约", description = "修改预约时间")
    public Result<AppointmentDetailResponse> rescheduleAppointment(
            @PathVariable Long appointmentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate newDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime newStartTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime newEndTime) {
        return Result.success(appointmentService.rescheduleAppointment(
                appointmentId, newDate, newStartTime, newEndTime));
    }

    @PostMapping("/{appointmentId}/confirm")
    @Operation(summary = "确认预约", description = "商户确认预约")
    public Result<Boolean> confirmAppointment(@PathVariable Long appointmentId) {
        return Result.success(appointmentService.confirmAppointment(appointmentId));
    }

    @PostMapping("/{appointmentId}/arrive")
    @Operation(summary = "标记到店", description = "标记用户已到店")
    public Result<Boolean> markArrived(@PathVariable Long appointmentId) {
        return Result.success(appointmentService.markArrived(appointmentId));
    }

    @PostMapping("/{appointmentId}/start")
    @Operation(summary = "开始服务", description = "标记服务开始")
    public Result<Boolean> startService(@PathVariable Long appointmentId) {
        return Result.success(appointmentService.startService(appointmentId));
    }

    @PostMapping("/{appointmentId}/complete")
    @Operation(summary = "完成服务", description = "标记服务完成")
    public Result<Boolean> completeService(@PathVariable Long appointmentId) {
        return Result.success(appointmentService.completeService(appointmentId));
    }

    @PostMapping("/{appointmentId}/review")
    @Operation(summary = "评价预约", description = "对预约服务进行评价")
    public Result<Boolean> reviewAppointment(
            @PathVariable Long appointmentId,
            @RequestParam Integer rating,
            @RequestParam(required = false) String content) {
        return Result.success(appointmentService.reviewAppointment(appointmentId, rating, content));
    }

    @GetMapping("/available-slots")
    @Operation(summary = "获取可预约时段", description = "获取指定日期的可预约时段")
    public Result<List<AvailableTimeSlotResponse>> getAvailableTimeSlots(
            @RequestParam Long merchantId,
            @RequestParam Long storeId,
            @RequestParam Long serviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return Result.success(appointmentService.getAvailableTimeSlots(
                merchantId, storeId, serviceId, date));
    }

    @GetMapping("/recommend-slots")
    @Operation(summary = "智能推荐时段", description = "智能推荐最佳预约时段")
    public Result<List<AvailableTimeSlotResponse>> recommendTimeSlots(
            @RequestParam Long merchantId,
            @RequestParam Long storeId,
            @RequestParam Long serviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return Result.success(appointmentService.recommendTimeSlots(
                merchantId, storeId, serviceId, date));
    }

    @GetMapping("/merchant/statistics")
    @Operation(summary = "获取预约统计", description = "获取商户预约统计数据")
    public Result<AppointmentStatisticsResponse> getAppointmentStatistics(
            @RequestParam Long merchantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.success(appointmentService.getAppointmentStatistics(
                merchantId, startDate, endDate));
    }

    @GetMapping("/user/statistics")
    @Operation(summary = "获取用户预约统计", description = "获取当前用户预约统计")
    public Result<UserAppointmentStatistics> getUserAppointmentStatistics(
            @RequestParam Long userId) {
        return Result.success(appointmentService.getUserAppointmentStatistics(userId));
    }

    @GetMapping("/merchant/today")
    @Operation(summary = "获取今日预约", description = "获取商户今日预约列表")
    public Result<List<AppointmentListResponse>> getTodayAppointments(
            @RequestParam Long merchantId) {
        return Result.success(appointmentService.getTodayAppointments(merchantId));
    }

    @GetMapping("/merchant/pending-count")
    @Operation(summary = "获取待处理数量", description = "获取商户待确认预约数量")
    public Result<Integer> getPendingAppointmentCount(@RequestParam Long merchantId) {
        return Result.success(appointmentService.getPendingAppointmentCount(merchantId));
    }

    @PostMapping("/batch-confirm")
    @Operation(summary = "批量确认预约", description = "批量确认多个预约")
    public Result<Integer> batchConfirmAppointments(@RequestBody List<Long> appointmentIds) {
        return Result.success(appointmentService.batchConfirmAppointments(appointmentIds));
    }
}
