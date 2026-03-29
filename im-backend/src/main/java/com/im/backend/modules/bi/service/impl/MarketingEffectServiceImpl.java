package com.im.backend.modules.bi.service.impl;

import com.im.backend.modules.bi.dto.MarketingEffectResponse;
import com.im.backend.modules.bi.entity.MarketingCampaignEffect;
import com.im.backend.modules.bi.repository.MarketingCampaignEffectMapper;
import com.im.backend.modules.bi.service.IMarketingEffectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 营销效果分析服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MarketingEffectServiceImpl implements IMarketingEffectService {

    private final MarketingCampaignEffectMapper effectMapper;

    @Override
    public MarketingEffectResponse getMarketingEffect(Long merchantId, LocalDate startDate, LocalDate endDate) {
        MarketingEffectResponse response = new MarketingEffectResponse();

        // 查询活动效果列表
        List<MarketingCampaignEffect> effectList = effectMapper.selectByDateRange(merchantId, startDate, endDate);

        // 计算汇总数据
        int totalExpose = 0;
        int totalClaim = 0;
        int totalUsed = 0;
        BigDecimal totalConvert = BigDecimal.ZERO;
        int campaignCount = 0;
        int activeCount = 0;

        List<MarketingEffectResponse.CampaignEffectItem> items = new ArrayList<>();

        for (MarketingCampaignEffect effect : effectList) {
            campaignCount++;
            totalExpose += effect.getExposeCount() != null ? effect.getExposeCount() : 0;
            totalClaim += effect.getClaimCount() != null ? effect.getClaimCount() : 0;
            totalUsed += effect.getUsedCount() != null ? effect.getUsedCount() : 0;
            if (effect.getConvertAmount() != null) {
                totalConvert = totalConvert.add(effect.getConvertAmount());
            }

            MarketingEffectResponse.CampaignEffectItem item = new MarketingEffectResponse.CampaignEffectItem();
            item.setCampaignId(effect.getCampaignId());
            item.setCampaignName(effect.getCampaignName());
            item.setCampaignType(effect.getCampaignType());
            item.setExposeCount(effect.getExposeCount());
            item.setClaimCount(effect.getClaimCount());
            item.setUsedCount(effect.getUsedCount());
            item.setClaimRate(effect.getClaimRate());
            item.setUseRate(effect.getUseRate());
            item.setConvertAmount(effect.getConvertAmount());
            item.setRoi(effect.getRoi());
            items.add(item);
        }

        response.setTotalCampaigns(campaignCount);
        response.setActiveCampaigns(activeCount);
        response.setTotalExposeCount(totalExpose);
        response.setTotalClaimCount(totalClaim);
        response.setTotalUsedCount(totalUsed);
        response.setTotalConvertAmount(totalConvert);

        // 计算整体率
        if (totalExpose > 0) {
            response.setOverallClaimRate(
                new BigDecimal(totalClaim).multiply(new BigDecimal(100))
                    .divide(new BigDecimal(totalExpose), 2, RoundingMode.HALF_UP));
        }
        if (totalClaim > 0) {
            response.setOverallUseRate(
                new BigDecimal(totalUsed).multiply(new BigDecimal(100))
                    .divide(new BigDecimal(totalClaim), 2, RoundingMode.HALF_UP));
        }

        response.setCampaignEffects(items);

        // 转化漏斗
        MarketingEffectResponse.ConversionFunnel funnel = new MarketingEffectResponse.ConversionFunnel();
        funnel.setExposeCount(totalExpose);
        funnel.setClaimCount(totalClaim);
        funnel.setUsedCount(totalUsed);
        funnel.setOrderCount(totalUsed);
        if (totalExpose > 0) {
            funnel.setClaimConversion(
                new BigDecimal(totalClaim).multiply(new BigDecimal(100))
                    .divide(new BigDecimal(totalExpose), 2, RoundingMode.HALF_UP));
        }
        if (totalClaim > 0) {
            funnel.setUseConversion(
                new BigDecimal(totalUsed).multiply(new BigDecimal(100))
                    .divide(new BigDecimal(totalClaim), 2, RoundingMode.HALF_UP));
        }
        funnel.setOrderConversion(funnel.getUseConversion());
        response.setConversionFunnel(funnel);

        return response;
    }

    @Override
    public MarketingEffectResponse getRealtimeMarketingData(Long merchantId) {
        LocalDate today = LocalDate.now();
        return getMarketingEffect(merchantId, today, today);
    }

    @Override
    public void calculateCampaignRoi(Long campaignId) {
        log.info("Calculating ROI for campaign: {}", campaignId);
    }
}
