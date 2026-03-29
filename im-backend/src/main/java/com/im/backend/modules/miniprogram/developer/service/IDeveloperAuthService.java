package com.im.backend.modules.miniprogram.developer.service;

import com.im.backend.modules.miniprogram.developer.dto.*;
import java.util.List;

/**
 * 开发者认证服务接口
 */
public interface IDeveloperAuthService {
    
    /**
     * 注册成为开发者
     */
    DeveloperResponse registerDeveloper(Long userId, String developerType, String developerName);
    
    /**
     * 获取开发者信息
     */
    DeveloperResponse getDeveloperInfo(Long developerId);
    
    /**
     * 获取开发者信息（根据用户ID）
     */
    DeveloperResponse getDeveloperByUserId(Long userId);
    
    /**
     * 提交认证申请
     */
    boolean submitVerification(Long developerId, String verifyInfo);
    
    /**
     * 审核开发者
     */
    boolean auditDeveloper(Long developerId, Integer status);
    
    /**
     * 增加积分
     */
    boolean addPoints(Long developerId, Integer points);
    
    /**
     * 获取收益统计
     */
    DeveloperEarningsResponse getEarningsStats(Long developerId);
}
