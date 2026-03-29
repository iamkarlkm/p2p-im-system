package com.im.live.controller;

import com.im.common.response.ApiResponse;
import com.im.live.dto.*;
import com.im.live.service.LiveProductService;
import com.im.live.service.LiveRoomService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * 直播控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/live")
@RequiredArgsConstructor
@Validated
@Api(tags = "小程序直播服务")
public class LiveController {

    private final LiveRoomService liveRoomService;
    private final LiveProductService liveProductService;

    // ==================== 直播间管理 ====================

    @PostMapping("/rooms")
    @ApiOperation("创建直播间")
    public ApiResponse<LiveRoomDTO> createRoom(
            @Valid @RequestBody CreateLiveRoomRequestDTO request,
            @RequestAttribute("userId") Long anchorId) {
        log.info("Create live room: {}, anchor: {}", request.getTitle(), anchorId);
        LiveRoomDTO room = liveRoomService.createRoom(request, anchorId);
        return ApiResponse.success(room);
    }

    @PostMapping("/rooms/{roomId}/start")
    @ApiOperation("开始直播")
    public ApiResponse<LiveRoomDTO> startLive(
            @PathVariable Long roomId,
            @RequestAttribute("userId") Long anchorId) {
        log.info("Start live room: {}, anchor: {}", roomId, anchorId);
        LiveRoomDTO room = liveRoomService.startLive(roomId, anchorId);
        return ApiResponse.success(room);
    }

    @PostMapping("/rooms/{roomId}/end")
    @ApiOperation("结束直播")
    public ApiResponse<LiveRoomDTO> endLive(
            @PathVariable Long roomId,
            @RequestAttribute("userId") Long anchorId) {
        log.info("End live room: {}, anchor: {}", roomId, anchorId);
        LiveRoomDTO room = liveRoomService.endLive(roomId, anchorId);
        return ApiResponse.success(room);
    }

    @GetMapping("/rooms/{roomId}")
    @ApiOperation("获取直播间信息")
    public ApiResponse<LiveRoomDTO> getRoomInfo(
            @PathVariable Long roomId,
            @RequestAttribute(value = "userId", required = false) Long userId) {
        LiveRoomDTO room = liveRoomService.getRoomInfo(roomId, userId);
        return ApiResponse.success(room);
    }

    @GetMapping("/rooms")
    @ApiOperation("获取直播间列表")
    public ApiResponse<List<LiveRoomDTO>> getRoomList(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(50) int size) {
        List<LiveRoomDTO> rooms = liveRoomService.getRoomList(status, category, page, size);
        return ApiResponse.success(rooms);
    }

    @GetMapping("/rooms/search")
    @ApiOperation("搜索直播间")
    public ApiResponse<List<LiveRoomDTO>> searchRooms(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(50) int size) {
        List<LiveRoomDTO> rooms = liveRoomService.searchRooms(keyword, page, size);
        return ApiResponse.success(rooms);
    }

    @PutMapping("/rooms/{roomId}")
    @ApiOperation("更新直播间")
    public ApiResponse<LiveRoomDTO> updateRoom(
            @PathVariable Long roomId,
            @Valid @RequestBody CreateLiveRoomRequestDTO request,
            @RequestAttribute("userId") Long anchorId) {
        LiveRoomDTO room = liveRoomService.updateRoom(roomId, request, anchorId);
        return ApiResponse.success(room);
    }

    @DeleteMapping("/rooms/{roomId}")
    @ApiOperation("删除直播间")
    public ApiResponse<Void> deleteRoom(
            @PathVariable Long roomId,
            @RequestAttribute("userId") Long anchorId) {
        liveRoomService.deleteRoom(roomId, anchorId);
        return ApiResponse.success();
    }

    // ==================== 直播预约 ====================

    @PostMapping("/rooms/{roomId}/subscribe")
    @ApiOperation("预约直播")
    public ApiResponse<Void> subscribeLive(
            @PathVariable Long roomId,
            @RequestAttribute("userId") Long userId) {
        liveRoomService.subscribeLive(roomId, userId);
        return ApiResponse.success();
    }

    @PostMapping("/rooms/{roomId}/unsubscribe")
    @ApiOperation("取消预约")
    public ApiResponse<Void> unsubscribeLive(
            @PathVariable Long roomId,
            @RequestAttribute("userId") Long userId) {
        liveRoomService.unsubscribeLive(roomId, userId);
        return ApiResponse.success();
    }

    // ==================== 直播商品 ====================

    @PostMapping("/products")
    @ApiOperation("添加直播商品")
    public ApiResponse<LiveProductDTO> addProduct(
            @RequestBody LiveProductDTO productDTO,
            @RequestAttribute("userId") Long anchorId) {
        LiveProductDTO product = liveProductService.addProduct(productDTO, anchorId);
        return ApiResponse.success(product);
    }

    @PutMapping("/products/{productId}")
    @ApiOperation("更新直播商品")
    public ApiResponse<LiveProductDTO> updateProduct(
            @PathVariable Long productId,
            @RequestBody LiveProductDTO productDTO,
            @RequestAttribute("userId") Long anchorId) {
        LiveProductDTO product = liveProductService.updateProduct(productId, productDTO, anchorId);
        return ApiResponse.success(product);
    }

    @DeleteMapping("/products/{productId}")
    @ApiOperation("删除直播商品")
    public ApiResponse<Void> deleteProduct(
            @PathVariable Long productId,
            @RequestAttribute("userId") Long anchorId) {
        liveProductService.deleteProduct(productId, anchorId);
        return ApiResponse.success();
    }

    @GetMapping("/rooms/{roomId}/products")
    @ApiOperation("获取直播间商品列表")
    public ApiResponse<List<LiveProductDTO>> getRoomProducts(@PathVariable Long roomId) {
        List<LiveProductDTO> products = liveProductService.getRoomProducts(roomId);
        return ApiResponse.success(products);
    }

    @PostMapping("/rooms/{roomId}/products/{productId}/explain")
    @ApiOperation("开始讲解商品")
    public ApiResponse<Void> startExplainProduct(
            @PathVariable Long roomId,
            @PathVariable Long productId,
            @RequestAttribute("userId") Long anchorId) {
        liveProductService.startExplainProduct(roomId, productId, anchorId);
        return ApiResponse.success();
    }

    @PostMapping("/rooms/{roomId}/products/{productId}/explain/end")
    @ApiOperation("结束讲解商品")
    public ApiResponse<Void> endExplainProduct(
            @PathVariable Long roomId,
            @PathVariable Long productId,
            @RequestAttribute("userId") Long anchorId) {
        liveProductService.endExplainProduct(roomId, productId, anchorId);
        return ApiResponse.success();
    }

    @PostMapping("/products/{productId}/shelve")
    @ApiOperation("上架商品")
    public ApiResponse<Void> shelveProduct(
            @PathVariable Long productId,
            @RequestAttribute("userId") Long anchorId) {
        liveProductService.shelveProduct(productId, anchorId);
        return ApiResponse.success();
    }

    @PostMapping("/products/{productId}/unshelve")
    @ApiOperation("下架商品")
    public ApiResponse<Void> unshelveProduct(
            @PathVariable Long productId,
            @RequestAttribute("userId") Long anchorId) {
        liveProductService.unshelveProduct(productId, anchorId);
        return ApiResponse.success();
    }

    // ==================== 推荐与发现 ====================

    @GetMapping("/rooms/recommended")
    @ApiOperation("获取推荐直播间")
    public ApiResponse<List<LiveRoomDTO>> getRecommendedRooms(
            @RequestAttribute(value = "userId", required = false) Long userId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(50) int size) {
        List<LiveRoomDTO> rooms = liveRoomService.getRecommendedRooms(userId, page, size);
        return ApiResponse.success(rooms);
    }

    @GetMapping("/rooms/nearby")
    @ApiOperation("获取附近直播间")
    public ApiResponse<List<LiveRoomDTO>> getNearbyRooms(
            @RequestParam Double longitude,
            @RequestParam Double latitude,
            @RequestParam(defaultValue = "5000") Integer radius,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(50) int size) {
        List<LiveRoomDTO> rooms = liveRoomService.getNearbyRooms(longitude, latitude, radius, page, size);
        return ApiResponse.success(rooms);
    }
}
