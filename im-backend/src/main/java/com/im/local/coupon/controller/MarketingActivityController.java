package com.im.local.coupon.controller;

import com.im.local.coupon.dto.*;
import com.im.local.coupon.entity.*;
import com.im.local.coupon.service.IMarketingActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 营销活动控制器
 */
@RestController
@RequestMapping("/api/v1/activity")
@RequiredArgsConstructor
public class MarketingActivityController {

    private final IMarketingActivityService activityService;

    /**
     * 创建营销活动
     */
    @PostMapping("/create")
    public Long createActivity(@RequestBody @Valid CreateActivityRequest request,
                               @RequestAttribute("userId") Long userId) {
        MarketingActivity activity = new MarketingActivity();
        BeanUtils.copyProperties(request, activity);
        return activityService.createActivity(activity, userId);
    }

    /**
     * 获取活动详情
     */
    @GetMapping("/{activityId}")
    public MarketingActivityResponse getActivityDetail(@PathVariable Long activityId) {
        MarketingActivity activity = activityService.getActivityDetail(activityId);
        if (activity == null) return null;

        MarketingActivityResponse resp = new MarketingActivityResponse();
        BeanUtils.copyProperties(activity, resp);
        return resp;
    }

    /**
     * 获取进行中的活动列表
     */
    @GetMapping("/list/active")
    public List<MarketingActivityResponse> getActiveActivities() {
        List<MarketingActivity> activities = activityService.getActiveActivities();
        return activities.stream().map(a -> {
            MarketingActivityResponse resp = new MarketingActivityResponse();
            BeanUtils.copyProperties(a, resp);
            return resp;
        }).collect(Collectors.toList());
    }

    /**
     * 获取商户活动列表
     */
    @GetMapping("/merchant/{merchantId}")
    public List<MarketingActivityResponse> getMerchantActivities(@PathVariable Long merchantId) {
        List<MarketingActivity> activities = activityService.getMerchantActivities(merchantId);
        return activities.stream().map(a -> {
            MarketingActivityResponse resp = new MarketingActivityResponse();
            BeanUtils.copyProperties(a, resp);
            return resp;
        }).collect(Collectors.toList());
    }

    /**
     * 参与活动
     */
    @PostMapping("/{activityId}/participate")
    public ActivityParticipation participateActivity(@PathVariable Long activityId,
                                                      @RequestParam Integer participateType,
                                                      @RequestAttribute("userId") Long userId) {
        return activityService.participateActivity(activityId, userId, participateType);
    }

    /**
     * 创建拼团
     */
    @PostMapping("/{activityId}/group/create")
    public GroupBuying createGroupBuying(@PathVariable Long activityId,
                                         @RequestAttribute("userId") Long userId) {
        return activityService.createGroupBuying(activityId, userId);
    }

    /**
     * 加入拼团
     */
    @PostMapping("/group/{groupId}/join")
    public boolean joinGroupBuying(@PathVariable Long groupId,
                                   @RequestAttribute("userId") Long userId) {
        return activityService.joinGroupBuying(groupId, userId);
    }

    /**
     * 获取拼团详情
     */
    @GetMapping("/group/{groupId}")
    public GroupBuying getGroupBuyingDetail(@PathVariable Long groupId) {
        return activityService.getGroupBuyingDetail(groupId);
    }

    /**
     * 发起砍价
     */
    @PostMapping("/{activityId}/bargain/start")
    public BargainActivity startBargain(@PathVariable Long activityId,
                                        @RequestAttribute("userId") Long userId) {
        return activityService.startBargain(activityId, userId);
    }

    /**
     * 帮助砍价
     */
    @PostMapping("/bargain/{bargainId}/help")
    public BargainHelpRecord helpBargain(@PathVariable Long bargainId,
                                         @RequestAttribute("userId") Long userId) {
        return activityService.helpBargain(bargainId, userId);
    }

    /**
     * 获取砍价详情
     */
    @GetMapping("/bargain/{bargainId}")
    public BargainActivity getBargainDetail(@PathVariable Long bargainId) {
        return activityService.getBargainDetail(bargainId);
    }
}
