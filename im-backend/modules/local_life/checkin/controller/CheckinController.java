package com.im.backend.modules.local_life.checkin.controller;

import com.im.backend.common.Result;
import com.im.backend.modules.local_life.checkin.dto.*;
import com.im.backend.modules.local_life.checkin.service.BadgeService;
import com.im.backend.modules.local_life.checkin.service.CheckinService;
import com.im.backend.modules.local_life.checkin.service.PointService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 签到控制器
 */
@Api(tags = "POI签到与积分系统")
@RestController
@RequestMapping("/api/v1/checkin")
@RequiredArgsConstructor
public class CheckinController {

    private final CheckinService checkinService;
    private final PointService pointService;
    private final BadgeService badgeService;

    @ApiOperation("用户签到")
    @PostMapping("/do")
    public Result<CheckinResponse> checkin(
            @RequestAttribute("userId") Long userId,
            @RequestBody @Validated CheckinRequest request) {
        return Result.success(checkinService.checkin(userId, request));
    }

    @ApiOperation("检查今日是否已签到")
    @GetMapping("/today")
    public Result<Boolean> hasCheckedInToday(@RequestAttribute("userId") Long userId) {
        return Result.success(checkinService.hasCheckedInToday(userId));
    }

    @ApiOperation("获取用户连续签到天数")
    @GetMapping("/streak")
    public Result<Integer> getStreakDays(@RequestAttribute("userId") Long userId) {
        return Result.success(checkinService.getStreakDays(userId));
    }

    @ApiOperation("获取用户积分账户")
    @GetMapping("/points/account")
    public Result<PointAccountResponse> getPointAccount(@RequestAttribute("userId") Long userId) {
        return Result.success(pointService.getPointAccount(userId));
    }

    @ApiOperation("获取积分交易记录")
    @GetMapping("/points/transactions")
    public Result<List<PointTransactionDTO>> getTransactions(
            @RequestAttribute("userId") Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        return Result.success(pointService.getTransactions(userId, page, size));
    }

    @ApiOperation("获取用户所有徽章")
    @GetMapping("/badges")
    public Result<List<BadgeDTO>> getUserBadges(@RequestAttribute("userId") Long userId) {
        return Result.success(badgeService.getUserBadges(userId));
    }

    @ApiOperation("领取徽章奖励")
    @PostMapping("/badges/{badgeCode}/claim")
    public Result<Boolean> claimBadgeReward(
            @RequestAttribute("userId") Long userId,
            @PathVariable String badgeCode) {
        return Result.success(badgeService.claimBadgeReward(userId, badgeCode));
    }

    @ApiOperation("获取附近签到的人")
    @GetMapping("/nearby")
    public Result<List<NearbyCheckinDTO>> getNearbyCheckins(
            @RequestParam Double longitude,
            @RequestParam Double latitude,
            @RequestParam(defaultValue = "5000") Integer radius,
            @RequestParam(defaultValue = "20") Integer limit) {
        return Result.success(checkinService.getNearbyCheckins(longitude, latitude, radius, limit));
    }

    @ApiOperation("获取签到动态列表")
    @GetMapping("/moments")
    public Result<List<CheckinMomentDTO>> getCheckinMoments(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        // 简化实现
        return Result.success(List.of());
    }
}
