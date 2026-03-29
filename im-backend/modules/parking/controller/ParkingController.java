package com.im.backend.modules.parking.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.im.backend.common.result.Result;
import com.im.backend.modules.parking.dto.*;
import com.im.backend.modules.parking.service.ParkingLotService;
import com.im.backend.modules.parking.service.ParkingRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 停车服务控制器
 * 提供停车场查询、停车记录、反向寻车、支付等API
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Tag(name = "停车服务", description = "停车场查询、停车记录、反向寻车、支付等接口")
@RestController
@RequestMapping("/api/v1/parking")
@RequiredArgsConstructor
@Validated
public class ParkingController {

    private final ParkingLotService parkingLotService;
    private final ParkingRecordService parkingRecordService;

    // ==================== 停车场管理接口 ====================

    @Operation(summary = "创建停车场", description = "创建新的停车场信息")
    @PostMapping("/lots")
    public Result<Long> createParkingLot(@RequestBody @Validated ParkingLotCreateDTO dto) {
        Long id = parkingLotService.createParkingLot(dto);
        return Result.success(id);
    }

    @Operation(summary = "更新停车场", description = "更新停车场信息")
    @PutMapping("/lots/{id}")
    public Result<Boolean> updateParkingLot(
            @Parameter(description = "停车场ID") @PathVariable Long id,
            @RequestBody @Validated ParkingLotUpdateDTO dto) {
        return Result.success(parkingLotService.updateParkingLot(id, dto));
    }

    @Operation(summary = "删除停车场", description = "删除指定停车场")
    @DeleteMapping("/lots/{id}")
    public Result<Boolean> deleteParkingLot(
            @Parameter(description = "停车场ID") @PathVariable Long id) {
        return Result.success(parkingLotService.deleteParkingLot(id));
    }

    @Operation(summary = "获取停车场详情", description = "获取停车场详细信息")
    @GetMapping("/lots/{id}")
    public Result<ParkingLotDetailVO> getParkingLotDetail(
            @Parameter(description = "停车场ID") @PathVariable Long id) {
        return Result.success(parkingLotService.getParkingLotDetail(id));
    }

    @Operation(summary = "分页查询停车场", description = "分页查询停车场列表")
    @PostMapping("/lots/page")
    public Result<Page<ParkingLotListVO>> pageParkingLots(@RequestBody ParkingLotQueryDTO dto) {
        return Result.success(parkingLotService.pageParkingLots(dto));
    }

    @Operation(summary = "搜索附近停车场", description = "根据地理位置搜索附近停车场")
    @GetMapping("/lots/nearby")
    public Result<Page<ParkingLotNearbyVO>> searchNearbyParkingLots(
            @Parameter(description = "经度") @RequestParam Double longitude,
            @Parameter(description = "纬度") @RequestParam Double latitude,
            @Parameter(description = "搜索半径(米)") @RequestParam(defaultValue = "2000") Integer radius,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(parkingLotService.searchNearbyParkingLots(longitude, latitude, radius, pageNum, pageSize));
    }

    @Operation(summary = "智能推荐停车场", description = "根据位置智能推荐停车场")
    @GetMapping("/lots/recommend")
    public Result<List<ParkingLotRecommendVO>> recommendParkingLots(
            @Parameter(description = "经度") @RequestParam Double longitude,
            @Parameter(description = "纬度") @RequestParam Double latitude,
            @Parameter(description = "推荐数量") @RequestParam(defaultValue = "5") Integer limit) {
        return Result.success(parkingLotService.recommendParkingLots(longitude, latitude, limit));
    }

    @Operation(summary = "搜索目的地周边停车场", description = "根据目的地搜索周边停车场")
    @GetMapping("/lots/around-destination")
    public Result<List<ParkingLotNearbyVO>> searchParkingLotsByDestination(
            @Parameter(description = "目的地经度") @RequestParam Double destLongitude,
            @Parameter(description = "目的地纬度") @RequestParam Double destLatitude,
            @Parameter(description = "搜索半径(米)") @RequestParam(defaultValue = "1000") Integer radius) {
        return Result.success(parkingLotService.searchParkingLotsByDestination(destLongitude, destLatitude, radius));
    }

    @Operation(summary = "停车场价格对比", description = "对比多个停车场的价格")
    @PostMapping("/lots/compare-price")
    public Result<List<ParkingPriceCompareVO>> compareParkingPrices(
            @RequestBody List<Long> parkingLotIds) {
        return Result.success(parkingLotService.compareParkingPrices(parkingLotIds));
    }

    @Operation(summary = "计算停车费用", description = "计算指定停车场的停车费用")
    @GetMapping("/lots/{id}/calculate-fee")
    public Result<BigDecimal> calculateParkingFee(
            @Parameter(description = "停车场ID") @PathVariable Long id,
            @Parameter(description = "停车时长(分钟)") @RequestParam Integer duration) {
        return Result.success(parkingLotService.calculateParkingFee(id, duration));
    }

    @Operation(summary = "获取停车场统计", description = "获取停车场的统计数据")
    @GetMapping("/lots/{id}/statistics")
    public Result<ParkingStatisticsVO> getParkingStatistics(
            @Parameter(description = "停车场ID") @PathVariable Long id) {
        return Result.success(parkingLotService.getParkingStatistics(id));
    }

    @Operation(summary = "搜索停车场", description = "根据关键词搜索停车场")
    @GetMapping("/lots/search")
    public Result<Page<ParkingLotListVO>> searchParkingLots(
            @Parameter(description = "关键词") @RequestParam String keyword,
            @Parameter(description = "城市编码") @RequestParam(required = false) String cityCode,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(parkingLotService.searchParkingLots(keyword, cityCode, pageNum, pageSize));
    }

    // ==================== 停车记录接口 ====================

    @Operation(summary = "车辆入场", description = "记录车辆入场信息")
    @PostMapping("/records/entry")
    public Result<Long> recordEntry(@RequestBody @Validated ParkingEntryDTO dto) {
        return Result.success(parkingRecordService.createParkingRecord(dto));
    }

    @Operation(summary = "车辆出场", description = "记录车辆出场信息")
    @PostMapping("/records/exit")
    public Result<Boolean> recordExit(@RequestBody @Validated ParkingExitDTO dto) {
        parkingRecordService.recordExit(dto);
        return Result.success(true);
    }

    @Operation(summary = "获取停车记录详情", description = "获取停车记录详细信息")
    @GetMapping("/records/{id}")
    public Result<ParkingRecordDetailVO> getParkingRecordDetail(
            @Parameter(description = "记录ID") @PathVariable Long id) {
        return Result.success(parkingRecordService.getParkingRecordDetail(id));
    }

    @Operation(summary = "获取当前停车记录", description = "获取用户当前的停车记录")
    @GetMapping("/records/current/{userId}")
    public Result<ParkingRecordDetailVO> getCurrentParkingRecord(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        return Result.success(parkingRecordService.getCurrentParkingRecord(userId));
    }

    @Operation(summary = "分页查询用户停车记录", description = "分页查询用户的停车历史")
    @GetMapping("/records/user/{userId}")
    public Result<Page<ParkingRecordListVO>> pageUserParkingRecords(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(parkingRecordService.pageUserParkingRecords(userId, status, pageNum, pageSize));
    }

    @Operation(summary = "标记停车位置", description = "标记停车位置和上传照片")
    @PostMapping("/records/{id}/mark-location")
    public Result<Boolean> markParkingLocation(
            @Parameter(description = "记录ID") @PathVariable Long id,
            @RequestBody @Validated ParkingLocationMarkDTO dto) {
        return Result.success(parkingRecordService.markParkingLocation(id, dto));
    }

    @Operation(summary = "获取用户停车概览", description = "获取用户的停车概览数据")
    @GetMapping("/records/user/{userId}/overview")
    public Result<UserParkingOverviewVO> getUserParkingOverview(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        return Result.success(parkingRecordService.getUserParkingOverview(userId));
    }

    @Operation(summary = "获取停车统计", description = "获取用户的停车统计")
    @GetMapping("/records/user/{userId}/statistics")
    public Result<ParkingRecordStatisticsVO> getParkingStatistics(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "开始日期") @RequestParam String startDate,
            @Parameter(description = "结束日期") @RequestParam String endDate) {
        return Result.success(parkingRecordService.getParkingStatistics(userId, startDate, endDate));
    }

    // ==================== 反向寻车接口 ====================

    @Operation(summary = "开始寻车", description = "开始反向寻车导航")
    @PostMapping("/car-finding/start")
    public Result<Long> startCarFinding(@RequestBody @Validated CarFindingStartDTO dto) {
        // TODO: 调用寻车服务
        return Result.success(0L);
    }

    @Operation(summary = "获取寻车导航路径", description = "获取到停车位的导航路径")
    @GetMapping("/car-finding/navigation-path")
    public Result<CarFindingNavigationVO> getCarFindingNavigation(
            @Parameter(description = "停车记录ID") @RequestParam Long parkingRecordId,
            @Parameter(description = "当前经度") @RequestParam Double currentLongitude,
            @Parameter(description = "当前纬度") @RequestParam Double currentLatitude) {
        // TODO: 调用寻车服务
        return Result.success(null);
    }

    @Operation(summary = "完成寻车", description = "标记寻车完成")
    @PostMapping("/car-finding/{id}/complete")
    public Result<Boolean> completeCarFinding(
            @Parameter(description = "寻车记录ID") @PathVariable Long id,
            @Parameter(description = "是否成功") @RequestParam Boolean success) {
        // TODO: 调用寻车服务
        return Result.success(true);
    }

    // ==================== 支付接口 ====================

    @Operation(summary = "创建支付订单", description = "为停车记录创建支付订单")
    @PostMapping("/payments/create-order")
    public Result<Long> createPaymentOrder(
            @Parameter(description = "停车记录ID") @RequestParam Long parkingRecordId) {
        return Result.success(parkingRecordService.createPaymentOrder(parkingRecordId));
    }

    @Operation(summary = "获取支付订单详情", description = "获取支付订单详情")
    @GetMapping("/payments/orders/{id}")
    public Result<ParkingPaymentOrderVO> getPaymentOrderDetail(
            @Parameter(description = "订单ID") @PathVariable Long id) {
        // TODO: 调用支付服务
        return Result.success(null);
    }

    @Operation(summary = "发起支付", description = "发起停车费用支付")
    @PostMapping("/payments/pay")
    public Result<ParkingPaymentResultVO> initiatePayment(@RequestBody @Validated ParkingPaymentDTO dto) {
        // TODO: 调用支付服务
        return Result.success(null);
    }

    @Operation(summary = "查询支付状态", description = "查询支付订单状态")
    @GetMapping("/payments/orders/{id}/status")
    public Result<Integer> queryPaymentStatus(
            @Parameter(description = "订单ID") @PathVariable Long id) {
        // TODO: 调用支付服务
        return Result.success(0);
    }

    @Operation(summary = "申请发票", description = "申请电子发票")
    @PostMapping("/payments/orders/{id}/invoice")
    public Result<Boolean> applyInvoice(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @RequestBody @Validated ParkingInvoiceApplyDTO dto) {
        // TODO: 调用发票服务
        return Result.success(true);
    }

    // ==================== 共享停车接口 ====================

    @Operation(summary = "发布共享停车位", description = "发布个人车位共享信息")
    @PostMapping("/shared-spaces")
    public Result<Long> publishSharedSpace(@RequestBody @Validated SharedSpacePublishDTO dto) {
        // TODO: 调用共享停车服务
        return Result.success(0L);
    }

    @Operation(summary = "搜索共享停车位", description = "搜索附近的共享停车位")
    @GetMapping("/shared-spaces/search")
    public Result<Page<SharedSpaceListVO>> searchSharedSpaces(
            @Parameter(description = "经度") @RequestParam Double longitude,
            @Parameter(description = "纬度") @RequestParam Double latitude,
            @Parameter(description = "搜索半径(米)") @RequestParam(defaultValue = "2000") Integer radius,
            @Parameter(description = "开始时间") @RequestParam String startTime,
            @Parameter(description = "结束时间") @RequestParam String endTime,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        // TODO: 调用共享停车服务
        return Result.success(null);
    }

    @Operation(summary = "预约共享停车位", description = "预约共享停车位")
    @PostMapping("/shared-spaces/{id}/reserve")
    public Result<Long> reserveSharedSpace(
            @Parameter(description = "共享停车位ID") @PathVariable Long id,
            @RequestBody @Validated SharedSpaceReserveDTO dto) {
        // TODO: 调用共享停车服务
        return Result.success(0L);
    }
}
