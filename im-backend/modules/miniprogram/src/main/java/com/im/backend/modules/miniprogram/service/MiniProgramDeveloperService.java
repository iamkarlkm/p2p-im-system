package com.im.backend.modules.miniprogram.service;

import com.im.backend.modules.miniprogram.dto.*;

import java.util.List;

/**
 * 开发者中心服务接口
 */
public interface MiniProgramDeveloperService {

    /**
     * 注册开发者
     */
    DeveloperResponse registerDeveloper(Long userId, RegisterDeveloperRequest request);

    /**
     * 获取开发者信息
     */
    DeveloperResponse getDeveloperInfo(Long developerId);

    /**
     * 更新开发者信息
     */
    DeveloperResponse updateDeveloperInfo(Long developerId, UpdateDeveloperRequest request);

    /**
     * 提交认证
     */
    boolean submitAuthentication(Long developerId, AuthRequest request);

    /**
     * 获取开发者统计
     */
    DeveloperStatistics getDeveloperStatistics(Long developerId);

    /**
     * 获取收益明细
     */
    List<IncomeRecord> getIncomeRecords(Long developerId, int page, int size);

    /**
     * 提现申请
     */
    boolean withdrawRequest(Long developerId, WithdrawRequest request);

    /**
     * 获取开发者等级列表
     */
    List<DeveloperLevel> getDeveloperLevels();

    /**
     * 搜索开发者
     */
    List<DeveloperResponse> searchDevelopers(String keyword, int page, int size);
}
