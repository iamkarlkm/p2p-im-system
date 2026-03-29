package com.im.backend.modules.miniprogram.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.im.backend.modules.miniprogram.dto.*;
import com.im.backend.modules.miniprogram.entity.MiniProgramApp;
import com.im.backend.modules.miniprogram.entity.MiniProgramDeveloper;
import com.im.backend.modules.miniprogram.enums.AppStatus;
import com.im.backend.modules.miniprogram.enums.DeveloperStatus;
import com.im.backend.modules.miniprogram.repository.MiniProgramAppMapper;
import com.im.backend.modules.miniprogram.repository.MiniProgramDeveloperMapper;
import com.im.backend.modules.miniprogram.service.IMiniProgramAppService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 小程序应用服务实现
 */
@Slf4j
@Service
public class MiniProgramAppServiceImpl extends ServiceImpl<MiniProgramAppMapper, MiniProgramApp>
        implements IMiniProgramAppService {

    @Autowired
    private MiniProgramAppMapper appMapper;

    @Autowired
    private MiniProgramDeveloperMapper developerMapper;

    @Override
    @Transactional
    public AppResponse createApp(Long developerId, CreateAppRequest request) {
        MiniProgramDeveloper developer = developerMapper.selectById(developerId);
        if (developer == null || developer.getStatus() != DeveloperStatus.APPROVED) {
            throw new RuntimeException("开发者未通过审核，无法创建应用");
        }

        MiniProgramApp app = new MiniProgramApp();
        BeanUtils.copyProperties(request, app);
        app.setAppId(generateAppId());
        app.setAppSecret(generateAppSecret());
        app.setDeveloperId(developerId);
        app.setStatus(AppStatus.DEVELOPING);
        app.setGrayReleasePercent(0);
        app.setCreateTime(LocalDateTime.now());
        app.setUpdateTime(LocalDateTime.now());

        appMapper.insert(app);
        developerMapper.incrementAppCount(developerId);

        log.info("应用创建成功: developerId={}, appId={}", developerId, app.getAppId());
        return convertToResponse(app);
    }

    @Override
    public AppResponse getApp(String appId) {
        MiniProgramApp app = appMapper.findByAppId(appId);
        if (app == null) {
            throw new RuntimeException("应用不存在");
        }
        return convertToResponse(app);
    }

    @Override
    public List<AppResponse> getDeveloperApps(Long developerId) {
        List<MiniProgramApp> apps = appMapper.findByDeveloperId(developerId);
        return apps.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AppResponse updateApp(Long developerId, String appId, CreateAppRequest request) {
        MiniProgramApp app = appMapper.findByAppId(appId);
        if (app == null || !app.getDeveloperId().equals(developerId)) {
            throw new RuntimeException("应用不存在或无权限");
        }

        BeanUtils.copyProperties(request, app, "id", "appId", "appSecret", "developerId", "status", "currentVersion");
        app.setUpdateTime(LocalDateTime.now());
        appMapper.updateById(app);

        return convertToResponse(app);
    }

    @Override
    @Transactional
    public void deleteApp(Long developerId, String appId) {
        MiniProgramApp app = appMapper.findByAppId(appId);
        if (app == null || !app.getDeveloperId().equals(developerId)) {
            throw new RuntimeException("应用不存在或无权限");
        }

        appMapper.deleteById(app.getId());
        log.info("应用删除成功: appId={}", appId);
    }

    @Override
    public String getAppSecret(Long developerId, String appId) {
        MiniProgramApp app = appMapper.findByAppId(appId);
        if (app == null || !app.getDeveloperId().equals(developerId)) {
            throw new RuntimeException("应用不存在或无权限");
        }
        return app.getAppSecret();
    }

    @Override
    @Transactional
    public String resetAppSecret(Long developerId, String appId) {
        MiniProgramApp app = appMapper.findByAppId(appId);
        if (app == null || !app.getDeveloperId().equals(developerId)) {
            throw new RuntimeException("应用不存在或无权限");
        }

        String newSecret = generateAppSecret();
        app.setAppSecret(newSecret);
        app.setUpdateTime(LocalDateTime.now());
        appMapper.updateById(app);

        log.info("应用密钥重置成功: appId={}", appId);
        return newSecret;
    }

    @Override
    public boolean validateAppCredentials(String appId, String appSecret) {
        MiniProgramApp app = appMapper.findByAppId(appId);
        return app != null && app.getAppSecret().equals(appSecret) && app.getStatus() == AppStatus.RELEASED;
    }

    @Override
    @Transactional
    public void updateGrayRelease(String appId, Integer percent) {
        if (percent < 0 || percent > 100) {
            throw new RuntimeException("灰度比例必须在0-100之间");
        }
        MiniProgramApp app = appMapper.findByAppId(appId);
        if (app == null) {
            throw new RuntimeException("应用不存在");
        }
        appMapper.updateGrayReleasePercent(app.getId(), percent);
        log.info("灰度发布比例更新: appId={}, percent={}", appId, percent);
    }

    private String generateAppId() {
        return "mp" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    private String generateAppSecret() {
        return UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");
    }

    private AppResponse convertToResponse(MiniProgramApp app) {
        AppResponse response = new AppResponse();
        BeanUtils.copyProperties(app, response);
        if (app.getCategory() != null) {
            response.setCategoryDesc(app.getCategory().getDesc());
        }
        if (app.getStatus() != null) {
            response.setStatusDesc(app.getStatus().getDesc());
        }
        return response;
    }
}
