package com.im.controller.delivery;

import com.im.entity.delivery.DeliveryRider;
import com.im.entity.delivery.RiderLocation;
import com.im.service.delivery.RiderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 骑手控制器 - 即时配送运力调度系统
 * 提供骑手注册、位置上报、状态管理等API接口
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/delivery/riders")
public class RiderController {

    @Autowired
    private RiderService riderService;

    /**
     * 骑手注册
     */
    @PostMapping("/register")
    public Map<String, Object> registerRider(@RequestBody DeliveryRider rider) {
        Map<String, Object> result = new HashMap<>();
        try {
            DeliveryRider registered = riderService.registerRider(rider);
            result.put("success", true);
            result.put("code", 200);
            result.put("message", "注册成功");
            result.put("data", registered);
        } catch (Exception e) {
            result.put("success", false);
            result.put("code", 500);
            result.put("message", "注册失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取骑手信息
     */
    @GetMapping("/{riderId}")
    public Map<String, Object> getRider(@PathVariable Long riderId) {
        Map<String, Object> result = new HashMap<>();
        DeliveryRider rider = riderService.getRiderById(riderId);
        if (rider != null) {
            result.put("success", true);
            result.put("code", 200);
            result.put("data", rider);
        } else {
            result.put("success", false);
            result.put("code", 404);
            result.put("message", "骑手不存在");
        }
        return result;
    }

    /**
     * 更新骑手工作状态
     */
    @PostMapping("/{riderId}/status")
    public Map<String, Object> updateWorkStatus(@PathVariable Long riderId, @RequestParam String status) {
        Map<String, Object> result = new HashMap<>();
        try {
            riderService.updateWorkStatus(riderId, status);
            result.put("success", true);
            result.put("code", 200);
            result.put("message", "状态更新成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("code", 500);
            result.put("message", "更新失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 上报位置
     */
    @PostMapping("/{riderId}/location")
    public Map<String, Object> updateLocation(@PathVariable Long riderId,
                                               @RequestParam BigDecimal longitude,
                                               @RequestParam BigDecimal latitude,
                                               @RequestParam(required = false, defaultValue = "GPS") String source) {
        Map<String, Object> result = new HashMap<>();
        try {
            riderService.updateLocation(riderId, longitude, latitude, source);
            result.put("success", true);
            result.put("code", 200);
            result.put("message", "位置上报成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("code", 500);
            result.put("message", "上报失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取骑手实时位置
     */
    @GetMapping("/{riderId}/location")
    public Map<String, Object> getRiderLocation(@PathVariable Long riderId) {
        Map<String, Object> result = new HashMap<>();
        RiderLocation location = riderService.getRiderCurrentLocation(riderId);
        if (location != null) {
            result.put("success", true);
            result.put("code", 200);
            result.put("data", location);
        } else {
            result.put("success", false);
            result.put("code", 404);
            result.put("message", "位置信息不存在");
        }
        return result;
    }

    /**
     * 获取骑手位置历史
     */
    @GetMapping("/{riderId}/location/history")
    public Map<String, Object> getLocationHistory(@PathVariable Long riderId,
                                                   @RequestParam String startTime,
                                                   @RequestParam String endTime) {
        Map<String, Object> result = new HashMap<>();
        List<RiderLocation> history = riderService.getRiderLocationHistory(riderId, startTime, endTime);
        result.put("success", true);
        result.put("code", 200);
        result.put("data", history);
        return result;
    }

    /**
     * 骑手签到(开始工作)
     */
    @PostMapping("/{riderId}/checkin")
    public Map<String, Object> riderCheckIn(@PathVariable Long riderId) {
        Map<String, Object> result = new HashMap<>();
        try {
            riderService.riderCheckIn(riderId);
            result.put("success", true);
            result.put("code", 200);
            result.put("message", "签到成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("code", 500);
            result.put("message", "签到失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 骑手签退(结束工作)
     */
    @PostMapping("/{riderId}/checkout")
    public Map<String, Object> riderCheckOut(@PathVariable Long riderId) {
        Map<String, Object> result = new HashMap<>();
        try {
            riderService.riderCheckOut(riderId);
            result.put("success", true);
            result.put("code", 200);
            result.put("message", "签退成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("code", 500);
            result.put("message", "签退失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取骑手今日统计
     */
    @GetMapping("/{riderId}/stats/today")
    public Map<String, Object> getTodayStats(@PathVariable Long riderId) {
        Map<String, Object> result = new HashMap<>();
        DeliveryRider stats = riderService.getTodayStats(riderId);
        if (stats != null) {
            Map<String, Object> data = new HashMap<>();
            data.put("completedCount", stats.getTodayCompletedCount());
            data.put("income", stats.getTodayIncome());
            data.put("rating", stats.getRating());
            data.put("onTimeRate", stats.getOnTimeRate());
            
            result.put("success", true);
            result.put("code", 200);
            result.put("data", data);
        } else {
            result.put("success", false);
            result.put("code", 404);
            result.put("message", "统计数据不存在");
        }
        return result;
    }

    /**
     * 获取附近可用骑手
     */
    @GetMapping("/nearby")
    public Map<String, Object> getNearbyRiders(@RequestParam BigDecimal longitude,
                                                @RequestParam BigDecimal latitude,
                                                @RequestParam(defaultValue = "5000") Integer radius) {
        Map<String, Object> result = new HashMap<>();
        List<DeliveryRider> riders = riderService.findNearbyAvailableRiders(longitude, latitude, radius);
        result.put("success", true);
        result.put("code", 200);
        result.put("data", riders);
        result.put("count", riders.size());
        return result;
    }

    /**
     * 获取骑手列表
     */
    @GetMapping
    public Map<String, Object> getRiderList(@RequestParam(required = false) String status,
                                             @RequestParam(required = false) Long zoneId,
                                             @RequestParam(defaultValue = "1") Integer page,
                                             @RequestParam(defaultValue = "20") Integer size) {
        Map<String, Object> result = new HashMap<>();
        List<DeliveryRider> riders = riderService.getRiderList(status, zoneId, page, size);
        result.put("success", true);
        result.put("code", 200);
        result.put("data", riders);
        return result;
    }
}
