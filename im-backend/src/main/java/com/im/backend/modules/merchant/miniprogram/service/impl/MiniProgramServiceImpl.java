package com.im.backend.modules.merchant.miniprogram.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.im.backend.common.Result;
import com.im.backend.modules.merchant.miniprogram.dto.MiniProgramCreateRequest;
import com.im.backend.modules.merchant.miniprogram.entity.MiniProgramApp;
import com.im.backend.modules.merchant.miniprogram.repository.MiniProgramAppMapper;
import com.im.backend.modules.merchant.miniprogram.service.IMiniProgramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

/**
 * 小程序应用服务实现 - 功能#313: 小程序开发者生态
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MiniProgramServiceImpl extends ServiceImpl<MiniProgramAppMapper, MiniProgramApp> implements IMiniProgramService {

    private final MiniProgramAppMapper appMapper;

    @Override
    public Result<MiniProgramApp> createApp(Long merchantId, MiniProgramCreateRequest request) {
        MiniProgramApp app = new MiniProgramApp();
        BeanUtils.copyProperties(request, app);
        app.setAppId(generateAppId());
        app.setMerchantId(merchantId);
        app.setStatus(0); // 开发中
        app.setVersion("1.0.0");
        app.setViewCount(0);
        app.setUseCount(0);
        
        appMapper.insert(app);
        log.info("创建小程序: {}", app.getAppId());
        return Result.success(app);
    }

    @Override
    public IPage<MiniProgramApp> getMerchantApps(Long merchantId, Page<MiniProgramApp> page) {
        return appMapper.selectByMerchantId(page, merchantId);
    }

    @Override
    public MiniProgramApp getAppDetail(String appId) {
        return appMapper.selectByAppId(appId);
    }

    @Override
    public Result<Void> publishApp(Long merchantId, Long appId) {
        MiniProgramApp app = appMapper.selectById(appId);
        if (app == null || !app.getMerchantId().equals(merchantId)) {
            return Result.error("小程序不存在或无权限");
        }
        app.setStatus(2); // 已发布
        appMapper.updateById(app);
        return Result.success();
    }

    @Override
    public List<MiniProgramApp> getHotApps(Integer limit) {
        return appMapper.selectPublished(limit);
    }

    private String generateAppId() {
        return "mp" + System.currentTimeMillis() + String.format("%04d", new Random().nextInt(10000));
    }
}
