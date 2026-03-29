package com.im.backend.modules.merchant.review.controller;

import com.im.backend.common.core.result.CommonResult;
import com.im.backend.modules.merchant.review.dto.MerchantReputationResponse;
import com.im.backend.modules.merchant.review.dto.ReputationRankRequest;
import com.im.backend.modules.merchant.review.entity.MerchantReputationStats;
import com.im.backend.modules.merchant.review.service.IMerchantReputationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商户口碑控制器
 * 提供口碑查询、榜单等功能
 */
@RestController
@RequestMapping("/api/v1/merchant-reputation")
@RequiredArgsConstructor
@Api(tags = "商户口碑管理")
public class MerchantReputationController {

    private final IMerchantReputationService reputationService;

    @GetMapping("/{merchantId}")
    @ApiOperation("获取商户口碑统计")
    public CommonResult<MerchantReputationResponse> getMerchantReputation(
            @PathVariable Long merchantId) {
        MerchantReputationResponse reputation = reputationService.getMerchantReputation(merchantId);
        return CommonResult.success(reputation);
    }

    @GetMapping("/{merchantId}/ranking")
    @ApiOperation("获取商户在同商圈的排名")
    public CommonResult<Integer> getMerchantRanking(
            @PathVariable Long merchantId) {
        Integer ranking = reputationService.getMerchantRanking(merchantId);
        return CommonResult.success(ranking);
    }

    @GetMapping("/rank")
    @ApiOperation("获取口碑榜单")
    public CommonResult<List<MerchantReputationStats>> getReputationRank(
            @RequestParam(defaultValue = "overall") String rankType,
            @RequestParam(defaultValue = "20") Integer limit) {
        List<MerchantReputationStats> rank = reputationService.getReputationRank(rankType, limit);
        return CommonResult.success(rank);
    }

    @PostMapping("/{merchantId}/refresh")
    @ApiOperation("【管理员】刷新商户口碑统计")
    public CommonResult<Void> refreshReputation(
            @PathVariable Long merchantId) {
        reputationService.updateMerchantReputation(merchantId);
        return CommonResult.success();
    }
}
