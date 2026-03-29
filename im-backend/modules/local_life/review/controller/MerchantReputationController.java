package com.im.backend.modules.local_life.review.controller;

import com.im.backend.common.Result;
import com.im.backend.modules.local_life.review.dto.MerchantReputationDTO;
import com.im.backend.modules.local_life.review.dto.ReputationRankingDTO;
import com.im.backend.modules.local_life.review.dto.ReputationRankingRequestDTO;
import com.im.backend.modules.local_life.review.service.MerchantReputationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商户口碑控制器
 * 
 * @author IM Development Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/reputation")
@RequiredArgsConstructor
@Tag(name = "商户口碑", description = "本地生活商户口碑与榜单")
public class MerchantReputationController {
    
    private final MerchantReputationService reputationService;
    
    /**
     * 获取商户口碑
     */
    @GetMapping("/merchant/{merchantId}")
    @Operation(summary = "获取商户口碑", description = "获取商户的口碑统计数据")
    public Result<MerchantReputationDTO> getMerchantReputation(
            @PathVariable Long merchantId) {
        MerchantReputationDTO reputation = reputationService.getMerchantReputation(merchantId);
        return Result.success(reputation);
    }
    
    /**
     * 获取口碑榜单
     */
    @PostMapping("/ranking")
    @Operation(summary = "口碑榜单", description = "获取各类口碑榜单")
    public Result<List<ReputationRankingDTO>> getReputationRanking(
            @RequestBody ReputationRankingRequestDTO request) {
        List<ReputationRankingDTO> ranking = reputationService.getReputationRanking(request);
        return Result.success(ranking);
    }
    
    /**
     * 获取商圈口碑榜
     */
    @GetMapping("/ranking/district/{districtId}")
    @Operation(summary = "商圈口碑榜", description = "获取指定商圈的口碑榜单")
    public Result<List<ReputationRankingDTO>> getDistrictRanking(
            @PathVariable Long districtId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "20") Integer limit) {
        ReputationRankingRequestDTO request = new ReputationRankingRequestDTO();
        request.setDistrictId(districtId);
        request.setCategoryId(categoryId);
        request.setLimit(limit);
        List<ReputationRankingDTO> ranking = reputationService.getReputationRanking(request);
        return Result.success(ranking);
    }
    
    /**
     * 获取城市口碑榜
     */
    @GetMapping("/ranking/city/{cityCode}")
    @Operation(summary = "城市口碑榜", description = "获取指定城市的口碑榜单")
    public Result<List<ReputationRankingDTO>> getCityRanking(
            @PathVariable String cityCode,
            @RequestParam(required = false) String listType,
            @RequestParam(defaultValue = "20") Integer limit) {
        ReputationRankingRequestDTO request = new ReputationRankingRequestDTO();
        request.setCityCode(cityCode);
        request.setListType(listType);
        request.setLimit(limit);
        List<ReputationRankingDTO> ranking = reputationService.getReputationRanking(request);
        return Result.success(ranking);
    }
    
    /**
     * 获取商户排名
     */
    @GetMapping("/merchant/{merchantId}/rank")
    @Operation(summary = "商户排名", description = "获取商户在榜单中的排名")
    public Result<Integer> getMerchantRank(
            @PathVariable Long merchantId,
            @RequestParam String listType) {
        Integer rank = reputationService.getMerchantRanking(merchantId, listType);
        return Result.success(rank);
    }
    
    /**
     * 刷新商户口碑 (管理员用)
     */
    @PostMapping("/merchant/{merchantId}/refresh")
    @Operation(summary = "刷新口碑统计", description = "手动刷新商户口碑统计数据")
    public Result<Void> refreshReputation(
            @PathVariable Long merchantId) {
        reputationService.updateReputationStatistics(merchantId);
        return Result.success();
    }
}
