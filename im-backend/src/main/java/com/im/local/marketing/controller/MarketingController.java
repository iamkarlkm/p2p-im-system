package com.im.local.marketing.controller;

import com.im.local.marketing.dto.*;
import com.im.local.marketing.service.MarketingService;
import com.im.common.result.Result;
import com.im.common.util.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.List;

/**
 * 营销活动控制器
 * 
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-28
 */
@RestController
@RequestMapping("/api/v1/marketing")
@RequiredArgsConstructor
@Validated
@Api(tags = "营销活动", description = "营销活动创建、拼团、砍价、秒杀等接口")
public class MarketingController {
    
    private final MarketingService marketingService;
    
    /**
     * 创建营销活动
     */
    @PostMapping("/activity")
    @ApiOperation(value = "创建营销活动", notes = "创建满减、折扣、秒杀、拼团、砍价等活动")
    public Result<String> createActivity(
            @Valid @RequestBody MarketingActivityDTO dto) {
        return marketingService.createActivity(dto);
    }
    
    /**
     * 获取活动列表
     */
    @GetMapping("/activities")
    @ApiOperation(value = "获取活动列表", notes = "获取营销活动列表，支持筛选")
    public Result<List<ActivityListDTO>> getActivities(
            @ApiParam("商户ID") @RequestParam(required = false) String merchantId,
            @ApiParam("活动类型") @RequestParam(required = false) String activityType,
            @ApiParam("状态") @RequestParam(required = false) String status,
            @ApiParam("页码") @RequestParam(required = false, defaultValue = "0") @Min(0) Integer page,
            @ApiParam("每页大小") @RequestParam(required = false, defaultValue = "20") @Min(1) Integer size) {
        return marketingService.getActivities(merchantId, activityType, status, page, size);
    }
    
    /**
     * 获取活动详情
     */
    @GetMapping("/activities/{activityId}")
    @ApiOperation(value = "获取活动详情", notes = "获取活动详细信息")
    public Result<ActivityDetailDTO> getActivityDetail(
            @ApiParam("活动ID") @PathVariable String activityId) {
        String userId = UserContext.getCurrentUserId();
        return marketingService.getActivityDetail(activityId, userId);
    }
    
    /**
     * 发起拼团
     */
    @PostMapping("/group-buy")
    @ApiOperation(value = "发起拼团", notes = "用户发起一个新的拼团")
    public Result<String> createGroupBuy(
            @Valid @RequestBody GroupBuyDTO dto) {
        String userId = UserContext.getCurrentUserId();
        return marketingService.createGroupBuy(userId, dto);
    }
    
    /**
     * 参与拼团
     */
    @PostMapping("/group-buy/{groupId}/join")
    @ApiOperation(value = "参与拼团", notes = "用户参与已有的拼团")
    public Result<Void> joinGroupBuy(
            @ApiParam("拼团ID") @PathVariable String groupId,
            @Valid @RequestBody GroupBuyJoinDTO dto) {
        String userId = UserContext.getCurrentUserId();
        return marketingService.joinGroupBuy(userId, groupId, dto);
    }
    
    /**
     * 获取拼团详情
     */
    @GetMapping("/group-buy/{groupId}")
    @ApiOperation(value = "获取拼团详情", notes = "获取拼团详细信息")
    public Result<GroupBuyDetailDTO> getGroupBuyDetail(
            @ApiParam("拼团ID") @PathVariable String groupId) {
        String userId = UserContext.getCurrentUserId();
        return marketingService.getGroupBuyDetail(groupId, userId);
    }
    
    /**
     * 发起砍价
     */
    @PostMapping("/bargain")
    @ApiOperation(value = "发起砍价", notes = "用户发起砍价活动")
    public Result<String> startBargain(
            @ApiParam("活动ID") @RequestParam String activityId,
            @ApiParam("商品ID") @RequestParam String productId) {
        String userId = UserContext.getCurrentUserId();
        return marketingService.startBargain(userId, activityId, productId);
    }
    
    /**
     * 帮好友砍价
     */
    @PostMapping("/bargain/{bargainId}/help")
    @ApiOperation(value = "帮好友砍价", notes = "帮好友砍掉一刀")
    public Result<BargainResultDTO> helpBargain(
            @ApiParam("砍价记录ID") @PathVariable String bargainId) {
        String userId = UserContext.getCurrentUserId();
        return marketingService.helpBargain(userId, bargainId);
    }
    
    /**
     * 获取砍价详情
     */
    @GetMapping("/bargain/{bargainId}")
    @ApiOperation(value = "获取砍价详情", notes = "获取砍价活动详情")
    public Result<BargainDetailDTO> getBargainDetail(
            @ApiParam("砍价记录ID") @PathVariable String bargainId) {
        String userId = UserContext.getCurrentUserId();
        return marketingService.getBargainDetail(bargainId, userId);
    }
    
    /**
     * 秒杀购买
     */
    @PostMapping("/flash-sale/buy")
    @ApiOperation(value = "秒杀购买", notes = "参与秒杀活动购买商品")
    public Result<FlashSaleOrderDTO> flashSaleBuy(
            @ApiParam("活动ID") @RequestParam String activityId,
            @ApiParam("商品ID") @RequestParam String productId) {
        String userId = UserContext.getCurrentUserId();
        return marketingService.flashSaleBuy(userId, activityId, productId);
    }
    
    /**
     * 计算活动优惠
     */
    @PostMapping("/discount/calculate")
    @ApiOperation(value = "计算活动优惠", notes = "计算订单参与活动后的优惠金额")
    public Result<DiscountCalcResultDTO> calculateDiscount(
            @ApiParam("活动ID") @RequestParam String activityId,
            @ApiParam("商品ID列表") @RequestParam List<String> productIds,
            @ApiParam("商品金额列表") @RequestParam List<BigDecimal> amounts) {
        return marketingService.calculateDiscount(activityId, productIds, amounts);
    }
}
