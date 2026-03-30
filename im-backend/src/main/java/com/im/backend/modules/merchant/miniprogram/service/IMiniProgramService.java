package com.im.backend.modules.merchant.miniprogram.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.im.backend.common.Result;
import com.im.backend.modules.merchant.miniprogram.dto.MiniProgramCreateRequest;
import com.im.backend.modules.merchant.miniprogram.entity.MiniProgramApp;

import java.util.List;

/**
 * 小程序应用服务接口 - 功能#313: 小程序开发者生态
 */
public interface IMiniProgramService {

    /**
     * 创建小程序
     */
    Result<MiniProgramApp> createApp(Long merchantId, MiniProgramCreateRequest request);

    /**
     * 获取商户小程序列表
     */
    IPage<MiniProgramApp> getMerchantApps(Long merchantId, Page<MiniProgramApp> page);

    /**
     * 获取小程序详情
     */
    MiniProgramApp getAppDetail(String appId);

    /**
     * 发布小程序
     */
    Result<Void> publishApp(Long merchantId, Long appId);

    /**
     * 获取热门小程序
     */
    List<MiniProgramApp> getHotApps(Integer limit);
}
