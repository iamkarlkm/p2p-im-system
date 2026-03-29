package com.im.backend.controller;

import com.im.backend.dto.poi.*;
import com.im.backend.service.PoiCheckinService;
import com.im.backend.service.UserPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * POI签到与积分系统控制器
 * 提供签到、积分查询、等级信息等相关API
 */
@RestController
@RequestMapping("/api/v1/checkin")
public class PoiCheckinController {

    @Autowired
    private PoiCheckinService poiCheckinService;

    @Autowired
    private UserPointService userPointService;

    /**
     * 执行POI签到
     */
    @PostMapping("/poi")
    public ResponseEntity<PoiCheckinResponse> checkin(
            @RequestAttribute("userId") Long userId,
            @RequestBody PoiCheckinRequest request) {
        PoiCheckinResponse response = poiCheckinService.checkin(userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取用户签到记录列表
     */
    @GetMapping("/records")
    public ResponseEntity<List<CheckinRecordDTO>> getUserCheckinRecords(
            @RequestAttribute("userId") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<CheckinRecordDTO> records = poiCheckinService.getUserCheckinRecords(userId, page, size);
        return ResponseEntity.ok(records);
    }

    /**
     * 获取POI签到记录
     */
    @GetMapping("/poi/{poiId}/records")
    public ResponseEntity<List<CheckinRecordDTO>> getPoiCheckinRecords(
            @PathVariable String poiId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<CheckinRecordDTO> records = poiCheckinService.getPoiCheckinRecords(poiId, page, size);
        return ResponseEntity.ok(records);
    }

    /**
     * 获取签到详情
     */
    @GetMapping("/records/{checkinId}")
    public ResponseEntity<CheckinRecordDTO> getCheckinDetail(@PathVariable Long checkinId) {
        CheckinRecordDTO record = poiCheckinService.getCheckinDetail(checkinId);
        if (record != null) {
            return ResponseEntity.ok(record);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 检查今日是否已签到
     */
    @GetMapping("/poi/{poiId}/status")
    public ResponseEntity<Boolean> hasCheckedInToday(
            @RequestAttribute("userId") Long userId,
            @PathVariable String poiId) {
        Boolean hasCheckedIn = poiCheckinService.hasCheckedInToday(userId, poiId);
        return ResponseEntity.ok(hasCheckedIn);
    }

    /**
     * 获取附近热门签到地点
     */
    @GetMapping("/poi/nearby/hot")
    public ResponseEntity<List<HotPoiDTO>> getNearbyHotPois(
            @RequestParam Double longitude,
            @RequestParam Double latitude,
            @RequestParam(defaultValue = "5000") int radius,
            @RequestParam(defaultValue = "10") int limit) {
        List<HotPoiDTO> pois = poiCheckinService.getNearbyHotPois(longitude, latitude, radius, limit);
        return ResponseEntity.ok(pois);
    }

    /**
     * 获取连续签到统计
     */
    @GetMapping("/stats/consecutive")
    public ResponseEntity<ConsecutiveCheckinStatsDTO> getConsecutiveStats(
            @RequestAttribute("userId") Long userId) {
        ConsecutiveCheckinStatsDTO stats = poiCheckinService.getConsecutiveStats(userId);
        return ResponseEntity.ok(stats);
    }

    /**
     * 删除签到记录
     */
    @DeleteMapping("/records/{checkinId}")
    public ResponseEntity<Void> deleteCheckin(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long checkinId) {
        Boolean success = poiCheckinService.deleteCheckin(userId, checkinId);
        if (success) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // ========== 积分系统接口 ==========

    /**
     * 获取用户积分账户信息
     */
    @GetMapping("/points/account")
    public ResponseEntity<UserPointAccountResponse> getUserPointAccount(
            @RequestAttribute("userId") Long userId) {
        UserPointAccountResponse account = userPointService.getUserPointAccount(userId);
        return ResponseEntity.ok(account);
    }

    /**
     * 获取积分交易记录
     */
    @GetMapping("/points/transactions")
    public ResponseEntity<List<PointTransactionDTO>> getTransactionRecords(
            @RequestAttribute("userId") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<PointTransactionDTO> records = userPointService.getTransactionRecords(userId, page, size);
        return ResponseEntity.ok(records);
    }

    /**
     * 获取用户等级信息
     */
    @GetMapping("/points/level")
    public ResponseEntity<UserLevelInfoDTO> getUserLevelInfo(
            @RequestAttribute("userId") Long userId) {
        UserLevelInfoDTO levelInfo = userPointService.getUserLevelInfo(userId);
        return ResponseEntity.ok(levelInfo);
    }

    /**
     * 获取所有等级规则
     */
    @GetMapping("/points/level-rules")
    public ResponseEntity<List<LevelRuleDTO>> getAllLevelRules() {
        List<LevelRuleDTO> rules = userPointService.getAllLevelRules();
        return ResponseEntity.ok(rules);
    }

    /**
     * 获取用户成就徽章
     */
    @GetMapping("/achievements")
    public ResponseEntity<List<AchievementDTO>> getUserAchievements(
            @RequestAttribute("userId") Long userId) {
        List<AchievementDTO> achievements = userPointService.getUserAchievements(userId);
        return ResponseEntity.ok(achievements);
    }
}
