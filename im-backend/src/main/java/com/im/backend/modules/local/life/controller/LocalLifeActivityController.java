package com.im.backend.modules.local.life.controller;

import com.im.backend.common.core.result.PageResult;
import com.im.backend.common.core.result.Result;
import com.im.backend.modules.local.life.dto.*;
import com.im.backend.modules.local.life.service.*;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 本地生活活动与社交圈控制器
 * 处理活动发布、报名、社交圈等相关功能
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/local-life")
@RequiredArgsConstructor
@Api(tags = "本地生活-活动与社交圈")
public class LocalLifeActivityController {

    private final ActivityService activityService;
    private final ActivityRegistrationService registrationService;
    private final SocialCircleService socialCircleService;

    // ==================== 活动管理接口 ====================

    @PostMapping("/activity/create")
    @ApiOperation("创建活动")
    public Result<ActivityResponse> createActivity(
            @RequestBody @Validated CreateActivityRequest request,
            @RequestAttribute("userId") Long userId) {
        log.info("创建活动, userId: {}, title: {}", userId, request.getTitle());
        ActivityResponse response = activityService.createActivity(request, userId);
        return Result.success(response);
    }

    @PutMapping("/activity/{activityId}")
    @ApiOperation("更新活动")
    public Result<ActivityResponse> updateActivity(
            @PathVariable Long activityId,
            @RequestBody @Validated CreateActivityRequest request,
            @RequestAttribute("userId") Long userId) {
        ActivityResponse response = activityService.updateActivity(activityId, request, userId);
        return Result.success(response);
    }

    @PostMapping("/activity/{activityId}/publish")
    @ApiOperation("发布活动")
    public Result<Void> publishActivity(
            @PathVariable Long activityId,
            @RequestAttribute("userId") Long userId) {
        activityService.publishActivity(activityId, userId);
        return Result.success();
    }

    @PostMapping("/activity/{activityId}/cancel")
    @ApiOperation("取消活动")
    public Result<Void> cancelActivity(
            @PathVariable Long activityId,
            @RequestAttribute("userId") Long userId,
            @RequestParam String reason) {
        activityService.cancelActivity(activityId, userId, reason);
        return Result.success();
    }

    @GetMapping("/activity/{activityId}")
    @ApiOperation("获取活动详情")
    public Result<ActivityResponse> getActivityDetail(
            @PathVariable Long activityId,
            @RequestAttribute(value = "userId", required = false) Long userId) {
        activityService.incrementViewCount(activityId);
        ActivityResponse response = activityService.getActivityDetail(activityId, userId);
        return Result.success(response);
    }

    @PostMapping("/activity/list")
    @ApiOperation("查询活动列表")
    public Result<PageResult<ActivityResponse>> queryActivities(
            @RequestBody ActivityQueryRequest request,
            @RequestAttribute(value = "userId", required = false) Long userId) {
        PageResult<ActivityResponse> result = activityService.queryActivities(request, userId);
        return Result.success(result);
    }

    @GetMapping("/activity/nearby")
    @ApiOperation("获取附近活动")
    public Result<List<ActivityResponse>> getNearbyActivities(
            @RequestParam Double longitude,
            @RequestParam Double latitude,
            @RequestParam(defaultValue = "5000") Integer radius,
            @RequestParam(defaultValue = "20") Integer limit) {
        List<ActivityResponse> list = activityService.getNearbyActivities(longitude, latitude, radius, limit);
        return Result.success(list);
    }

    @GetMapping("/activity/hot")
    @ApiOperation("获取热门活动")
    public Result<List<ActivityResponse>> getHotActivities(
            @RequestParam(defaultValue = "10") Integer limit) {
        List<ActivityResponse> list = activityService.getHotActivities(limit);
        return Result.success(list);
    }

    @GetMapping("/activity/recommend")
    @ApiOperation("获取推荐活动")
    public Result<List<ActivityResponse>> getRecommendedActivities(
            @RequestAttribute("userId") Long userId,
            @RequestParam(defaultValue = "10") Integer limit) {
        List<ActivityResponse> list = activityService.getRecommendedActivities(userId, limit);
        return Result.success(list);
    }

    @GetMapping("/activity/search")
    @ApiOperation("搜索活动")
    public Result<PageResult<ActivityResponse>> searchActivities(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        PageResult<ActivityResponse> result = activityService.searchActivities(keyword, pageNum, pageSize);
        return Result.success(result);
    }

    @GetMapping("/activity/{activityId}/statistics")
    @ApiOperation("获取活动统计")
    public Result<ActivityStatisticsResponse> getActivityStatistics(
            @PathVariable Long activityId,
            @RequestAttribute("userId") Long userId) {
        ActivityStatisticsResponse response = activityService.getActivityStatistics(activityId, userId);
        return Result.success(response);
    }

    // ==================== 报名管理接口 ====================

    @PostMapping("/activity/register")
    @ApiOperation("报名活动")
    public Result<ActivityRegistrationResponse> registerActivity(
            @RequestBody @Validated RegisterActivityRequest request,
            @RequestAttribute("userId") Long userId) {
        ActivityRegistrationResponse response = registrationService.registerActivity(request, userId);
        return Result.success(response);
    }

    @PostMapping("/activity/{activityId}/cancel-registration")
    @ApiOperation("取消报名")
    public Result<Void> cancelRegistration(
            @PathVariable Long activityId,
            @RequestAttribute("userId") Long userId,
            @RequestParam String reason) {
        registrationService.cancelRegistration(activityId, userId, reason);
        return Result.success();
    }

    @PostMapping("/registration/{registrationId}/confirm")
    @ApiOperation("确认参加")
    public Result<Void> confirmParticipation(
            @PathVariable Long registrationId,
            @RequestAttribute("userId") Long userId) {
        registrationService.confirmParticipation(registrationId, userId);
        return Result.success();
    }

    @GetMapping("/activity/{activityId}/registrations")
    @ApiOperation("获取活动报名列表")
    public Result<PageResult<ActivityRegistrationResponse>> getActivityRegistrations(
            @PathVariable Long activityId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        PageResult<ActivityRegistrationResponse> result = registrationService.getActivityRegistrations(activityId, pageNum, pageSize);
        return Result.success(result);
    }

    @GetMapping("/activity/user/registrations")
    @ApiOperation("获取我的报名列表")
    public Result<List<ActivityRegistrationResponse>> getUserRegistrations(
            @RequestAttribute("userId") Long userId,
            @RequestParam(required = false) String status) {
        List<ActivityRegistrationResponse> list = registrationService.getUserRegistrations(userId, status);
        return Result.success(list);
    }

    @PostMapping("/activity/{activityId}/checkin")
    @ApiOperation("活动签到")
    public Result<Void> checkIn(
            @PathVariable Long activityId,
            @RequestAttribute("userId") Long userId,
            @RequestParam Double longitude,
            @RequestParam Double latitude) {
        registrationService.checkIn(activityId, userId, longitude, latitude);
        return Result.success();
    }

    @PostMapping("/activity/{activityId}/rate")
    @ApiOperation("评价活动")
    public Result<Void> rateActivity(
            @PathVariable Long activityId,
            @RequestAttribute("userId") Long userId,
            @RequestParam Integer rating,
            @RequestParam(required = false) String content) {
        registrationService.rateActivity(activityId, userId, rating, content);
        return Result.success();
    }

    // ==================== 社交圈接口 ====================

    @PostMapping("/circle/create")
    @ApiOperation("创建圈子")
    public Result<CircleResponse> createCircle(
            @RequestBody @Validated CreateCircleRequest request,
            @RequestAttribute("userId") Long userId) {
        CircleResponse response = socialCircleService.createCircle(request, userId);
        return Result.success(response);
    }

    @PutMapping("/circle/{circleId}")
    @ApiOperation("更新圈子")
    public Result<CircleResponse> updateCircle(
            @PathVariable Long circleId,
            @RequestBody @Validated CreateCircleRequest request,
            @RequestAttribute("userId") Long userId) {
        CircleResponse response = socialCircleService.updateCircle(circleId, request, userId);
        return Result.success(response);
    }

    @GetMapping("/circle/{circleId}")
    @ApiOperation("获取圈子详情")
    public Result<CircleResponse> getCircleDetail(
            @PathVariable Long circleId,
            @RequestAttribute(value = "userId", required = false) Long userId) {
        CircleResponse response = socialCircleService.getCircleDetail(circleId, userId);
        return Result.success(response);
    }

    @GetMapping("/circle/list")
    @ApiOperation("查询圈子列表")
    public Result<PageResult<CircleResponse>> queryCircles(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        PageResult<CircleResponse> result = socialCircleService.queryCircles(keyword, category, pageNum, pageSize);
        return Result.success(result);
    }

    @GetMapping("/circle/nearby")
    @ApiOperation("获取附近圈子")
    public Result<List<CircleResponse>> getNearbyCircles(
            @RequestParam Double longitude,
            @RequestParam Double latitude,
            @RequestParam(defaultValue = "5000") Integer radius,
            @RequestParam(defaultValue = "20") Integer limit) {
        List<CircleResponse> list = socialCircleService.getNearbyCircles(longitude, latitude, radius, limit);
        return Result.success(list);
    }

    @GetMapping("/circle/hot")
    @ApiOperation("获取热门圈子")
    public Result<List<CircleResponse>> getHotCircles(
            @RequestParam(defaultValue = "10") Integer limit) {
        List<CircleResponse> list = socialCircleService.getHotCircles(limit);
        return Result.success(list);
    }

    @PostMapping("/circle/join")
    @ApiOperation("加入圈子")
    public Result<Void> joinCircle(
            @RequestBody @Validated JoinCircleRequest request,
            @RequestAttribute("userId") Long userId) {
        socialCircleService.joinCircle(request, userId);
        return Result.success();
    }

    @PostMapping("/circle/{circleId}/leave")
    @ApiOperation("退出圈子")
    public Result<Void> leaveCircle(
            @PathVariable Long circleId,
            @RequestAttribute("userId") Long userId) {
        socialCircleService.leaveCircle(circleId, userId);
        return Result.success();
    }

    @PostMapping("/circle/{circleId}/member/{memberId}/role")
    @ApiOperation("设置成员角色")
    public Result<Void> setMemberRole(
            @PathVariable Long circleId,
            @PathVariable Long memberId,
            @RequestParam String role,
            @RequestAttribute("userId") Long userId) {
        socialCircleService.setMemberRole(circleId, memberId, role, userId);
        return Result.success();
    }

    @PostMapping("/circle/{circleId}/member/{memberId}/remove")
    @ApiOperation("移除成员")
    public Result<Void> removeMember(
            @PathVariable Long circleId,
            @PathVariable Long memberId,
            @RequestAttribute("userId") Long userId) {
        socialCircleService.removeMember(circleId, memberId, userId);
        return Result.success();
    }

    @PostMapping("/circle/{circleId}/member/{memberId}/mute")
    @ApiOperation("禁言成员")
    public Result<Void> muteMember(
            @PathVariable Long circleId,
            @PathVariable Long memberId,
            @RequestParam Integer minutes,
            @RequestAttribute("userId") Long userId) {
        socialCircleService.muteMember(circleId, memberId, minutes, userId);
        return Result.success();
    }

    @GetMapping("/circle/user/circles")
    @ApiOperation("获取我加入的圈子")
    public Result<List<CircleResponse>> getUserCircles(
            @RequestAttribute("userId") Long userId) {
        List<CircleResponse> list = socialCircleService.getUserCircles(userId);
        return Result.success(list);
    }

    @PostMapping("/circle/{circleId}/announcement")
    @ApiOperation("发布圈子公告")
    public Result<Void> publishAnnouncement(
            @PathVariable Long circleId,
            @RequestParam String announcement,
            @RequestAttribute("userId") Long userId) {
        socialCircleService.publishAnnouncement(circleId, announcement, userId);
        return Result.success();
    }

    @PostMapping("/circle/{circleId}/disband")
    @ApiOperation("解散圈子")
    public Result<Void> disbandCircle(
            @PathVariable Long circleId,
            @RequestAttribute("userId") Long userId) {
        socialCircleService.disbandCircle(circleId, userId);
        return Result.success();
    }
}
