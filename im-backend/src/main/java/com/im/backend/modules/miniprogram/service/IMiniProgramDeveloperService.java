package com.im.backend.modules.miniprogram.service;

import com.im.backend.modules.miniprogram.dto.*;

import java.util.List;

/**
 * 小程序开发者服务接口
 */
public interface IMiniProgramDeveloperService {

    /**
     * 注册开发者
     */
    DeveloperResponse registerDeveloper(Long userId, RegisterDeveloperRequest request);

    /**
     * 获取开发者信息
     */
    DeveloperResponse getDeveloperInfo(Long userId);

    /**
     * 更新开发者信息
     */
    DeveloperResponse updateDeveloper(Long userId, RegisterDeveloperRequest request);

    /**
     * 审核开发者
     */
    void auditDeveloper(Long developerId, boolean approved, String reason);

    /**
     * 检查API配额
     */
    boolean checkApiQuota(Long developerId, int required);

    /**
     * 增加已使用配额
     */
    void incrementUsedQuota(Long developerId, int count);
}
