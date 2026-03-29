package com.im.backend.modules.local.life.controller;

import com.im.backend.common.api.ApiResponse;
import com.im.backend.modules.local.life.dto.PoiGuidanceDTO;
import com.im.backend.modules.local.life.dto.PoiGuidanceQueryRequestDTO;
import com.im.backend.modules.local.life.service.PoiGuidanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * POI引导控制器
 * POI Guidance Controller
 */
@RestController
@RequestMapping("/api/v1/poi-guidance")
@RequiredArgsConstructor
@Tag(name = "POI引导服务", description = "POI导航引导点相关接口")
public class PoiGuidanceController {

    private final PoiGuidanceService poiGuidanceService;

    @GetMapping("/list")
    @Operation(summary = "获取POI引导点列表", description = "获取指定POI的所有引导点")
    public ApiResponse<List<PoiGuidanceDTO>> getPoiGuidanceList(
            @Validated PoiGuidanceQueryRequestDTO request) {
        List<PoiGuidanceDTO> list = poiGuidanceService.getPoiGuidanceList(request);
        return ApiResponse.success(list);
    }

    @GetMapping("/{poiId}/main-entrance")
    @Operation(summary = "获取主入口", description = "获取POI主入口引导点")
    public ApiResponse<PoiGuidanceDTO> getMainEntrance(@PathVariable Long poiId) {
        PoiGuidanceDTO guidance = poiGuidanceService.getMainEntrance(poiId);
        return ApiResponse.success(guidance);
    }

    @GetMapping("/{poiId}/nearest")
    @Operation(summary = "获取最近引导点", description = "获取距离用户最近的引导点")
    public ApiResponse<PoiGuidanceDTO> getNearestGuidance(
            @PathVariable Long poiId,
            @RequestParam Double userLng,
            @RequestParam Double userLat) {
        PoiGuidanceDTO guidance = poiGuidanceService.getNearestGuidance(poiId, userLng, userLat);
        return ApiResponse.success(guidance);
    }

    @GetMapping("/{poiId}/parking")
    @Operation(summary = "获取停车场信息", description = "获取POI停车场信息")
    public ApiResponse<PoiGuidanceDTO> getParkingInfo(@PathVariable Long poiId) {
        PoiGuidanceDTO guidance = poiGuidanceService.getParkingInfo(poiId);
        return ApiResponse.success(guidance);
    }

    @GetMapping("/{poiId}/indoor")
    @Operation(summary = "获取室内导航信息", description = "获取室内地图导航信息")
    public ApiResponse<List<PoiGuidanceDTO>> getIndoorGuidance(
            @PathVariable Long poiId,
            @RequestParam(required = false) String indoorMapId,
            @RequestParam(required = false) Integer floor) {
        List<PoiGuidanceDTO> list = poiGuidanceService.getIndoorGuidance(poiId, indoorMapId, floor);
        return ApiResponse.success(list);
    }
}
