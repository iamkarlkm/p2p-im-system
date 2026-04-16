package com.im.service.bi.service;

import com.im.service.bi.dto.CustomerPortraitResponse;

/**
 * 用户画像服务接口
 */
public interface ICustomerPortraitService {

    /**
     * 获取用户画像分析
     */
    CustomerPortraitResponse getCustomerPortrait(Long merchantId);

    /**
     * 更新用户画像
     */
    void refreshCustomerPortrait(Long merchantId);

    /**
     * 获取地域分布热力图数据
     */
    byte[] getRegionHeatmap(Long merchantId);
}
