package com.im.service.bi.service.impl;

import com.im.service.bi.dto.CustomerPortraitResponse;
import com.im.service.bi.enums.RfmSegment;
import com.im.service.bi.repository.CustomerPortraitMapper;
import com.im.service.bi.service.ICustomerPortraitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 用户画像服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerPortraitServiceImpl implements ICustomerPortraitService {

    private final CustomerPortraitMapper portraitMapper;

    @Override
    public CustomerPortraitResponse getCustomerPortrait(Long merchantId) {
        CustomerPortraitResponse response = new CustomerPortraitResponse();

        // 总顾客数
        Integer totalCustomers = portraitMapper.selectTotalCustomers(merchantId);
        response.setTotalCustomers(totalCustomers != null ? totalCustomers : 0);

        // 生命周期分布统计新老客
        List<Map<String, Object>> lifecycleList = portraitMapper.selectLifecycleDistribution(merchantId);
        int newCustomers = 0;
        int oldCustomers = 0;
        for (Map<String, Object> map : lifecycleList) {
            String stage = (String) map.get("lifecycle_stage");
            Long count = ((Number) map.get("count")).longValue();
            if ("new".equals(stage)) {
                newCustomers = count.intValue();
            } else {
                oldCustomers += count.intValue();
            }
        }
        response.setNewCustomers(newCustomers);
        response.setOldCustomers(oldCustomers);

        // 计算占比
        if (totalCustomers != null && totalCustomers > 0) {
            response.setNewCustomerRatio(
                new BigDecimal(newCustomers).multiply(new BigDecimal(100))
                    .divide(new BigDecimal(totalCustomers), 2, RoundingMode.HALF_UP));
            response.setOldCustomerRatio(
                new BigDecimal(oldCustomers).multiply(new BigDecimal(100))
                    .divide(new BigDecimal(totalCustomers), 2, RoundingMode.HALF_UP));
        }

        // 地域分布
        response.setRegionDistribution(getRegionDistribution(merchantId, totalCustomers));

        // RFM分布
        response.setRfmDistribution(getRfmDistribution(merchantId, totalCustomers));

        // 偏好标签(简化示例)
        response.setPreferenceTags(getPreferenceTags());

        return response;
    }

    @Override
    public void refreshCustomerPortrait(Long merchantId) {
        log.info("Refreshing customer portrait for merchant: {}", merchantId);
        // 触发画像计算任务
    }

    @Override
    public byte[] getRegionHeatmap(Long merchantId) {
        // 返回热力图图片数据
        return new byte[0];
    }

    private List<CustomerPortraitResponse.RegionDistribution> getRegionDistribution(Long merchantId, Integer total) {
        List<Map<String, Object>> list = portraitMapper.selectRegionDistribution(merchantId);
        List<CustomerPortraitResponse.RegionDistribution> result = new ArrayList<>();

        for (Map<String, Object> map : list) {
            CustomerPortraitResponse.RegionDistribution item = new CustomerPortraitResponse.RegionDistribution();
            item.setRegionName((String) map.get("regionName"));
            Long count = ((Number) map.get("count")).longValue();
            item.setCustomerCount(count.intValue());
            if (total != null && total > 0) {
                item.setRatio(new BigDecimal(count).multiply(new BigDecimal(100))
                    .divide(new BigDecimal(total), 2, RoundingMode.HALF_UP));
            }
            result.add(item);
        }
        return result;
    }

    private List<CustomerPortraitResponse.RfmDistribution> getRfmDistribution(Long merchantId, Integer total) {
        List<Map<String, Object>> list = portraitMapper.selectRfmDistribution(merchantId);
        List<CustomerPortraitResponse.RfmDistribution> result = new ArrayList<>();

        for (Map<String, Object> map : list) {
            CustomerPortraitResponse.RfmDistribution item = new CustomerPortraitResponse.RfmDistribution();
            String segment = (String) map.get("segment");
            item.setSegment(segment);
            item.setSegmentName(getSegmentName(segment));
            Long count = ((Number) map.get("count")).longValue();
            item.setCustomerCount(count.intValue());
            if (total != null && total > 0) {
                item.setRatio(new BigDecimal(count).multiply(new BigDecimal(100))
                    .divide(new BigDecimal(total), 2, RoundingMode.HALF_UP));
            }
            result.add(item);
        }
        return result;
    }

    private String getSegmentName(String code) {
        try {
            return RfmSegment.valueOf(code.toUpperCase()).getName();
        } catch (Exception e) {
            return code;
        }
    }

    private List<CustomerPortraitResponse.PreferenceTag> getPreferenceTags() {
        List<CustomerPortraitResponse.PreferenceTag> tags = new ArrayList<>();
        String[] tagNames = {"美食", "购物", "娱乐", "运动", "亲子"};
        for (String name : tagNames) {
            CustomerPortraitResponse.PreferenceTag tag = new CustomerPortraitResponse.PreferenceTag();
            tag.setTagName(name);
            tag.setCustomerCount(100);
            tag.setRatio(new BigDecimal(20));
            tags.add(tag);
        }
        return tags;
    }
}
