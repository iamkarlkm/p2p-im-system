package com.im.local.review.controller;

import com.im.local.review.dto.MerchantReputationResponse;
import com.im.local.review.service.IMerchantReputationService;
import com.im.local.review.service.IMerchantReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 商户口碑控制器
 * 提供口碑统计、排行榜等API
 */
@RestController
@RequestMapping("/api/v1/merchant/reputation")
@RequiredArgsConstructor
public class MerchantReputationController {

    private final IMerchantReputationService reputationService;
    private final IMerchantReviewService reviewService;

    /**
     * 获取商户口碑信息
     */
    @GetMapping("/{merchantId}")
    public MerchantReputationResponse getReputation(@PathVariable Long merchantId) {
        return reviewService.getMerchantReputation(merchantId);
    }

    /**
     * 获取商圈排行榜
     */
    @GetMapping("/ranking/district/{districtId}")
    public Object getDistrictRanking(@PathVariable Long districtId,
                                     @RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "20") Integer size) {
        return reputationService.getDistrictRanking(districtId, page, size);
    }

    /**
     * 获取分类排行榜
     */
    @GetMapping("/ranking/category/{categoryId}")
    public Object getCategoryRanking(@PathVariable Integer categoryId,
                                     @RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "20") Integer size) {
        return reputationService.getCategoryRanking(categoryId, page, size);
    }

    /**
     * 手动触发口碑统计计算（内部接口）
     */
    @PostMapping("/calculate/{merchantId}")
    public void calculateReputation(@PathVariable Long merchantId) {
        reputationService.calculateReputation(merchantId);
    }
}
