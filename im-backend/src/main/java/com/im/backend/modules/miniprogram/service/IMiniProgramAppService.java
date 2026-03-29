package com.im.backend.modules.miniprogram.service;

import com.im.backend.modules.miniprogram.dto.*;

import java.util.List;

/**
 * 小程序应用服务接口
 */
public interface IMiniProgramAppService {

    /**
     * 创建应用
     */
    AppResponse createApp(Long developerId, CreateAppRequest request);

    /**
     * 获取应用详情
     */
    AppResponse getApp(String appId);

    /**
     * 获取开发者应用列表
     */
    List<AppResponse> getDeveloperApps(Long developerId);

    /**
     * 更新应用
     */
    AppResponse updateApp(Long developerId, String appId, CreateAppRequest request);

    /**
     * 删除应用
     */
    void deleteApp(Long developerId, String appId);

    /**
     * 获取应用密钥
     */
    String getAppSecret(Long developerId, String appId);

    /**
     * 重置应用密钥
     */
    String resetAppSecret(Long developerId, String appId);

    /**
     * 验证AppID和Secret
     */
    boolean validateAppCredentials(String appId, String appSecret);

    /**
     * 更新灰度发布比例
     */
    void updateGrayRelease(String appId, Integer percent);
}
