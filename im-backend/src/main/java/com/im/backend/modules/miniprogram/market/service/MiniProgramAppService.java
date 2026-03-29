package com.im.backend.modules.miniprogram.market.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.im.backend.common.api.PageResult;
import com.im.backend.modules.miniprogram.market.dto.*;
import com.im.backend.modules.miniprogram.market.entity.MiniProgramApp;

import java.util.List;

/**
 * 小程序应用服务接口
 */
public interface MiniProgramAppService extends IService<MiniProgramApp> {

    /**
     * 创建小程序
     */
    MiniProgramResponse createApp(Long developerId, CreateMiniProgramRequest request);

    /**
     * 获取小程序详情
     */
    MiniProgramResponse getAppDetail(Long appId, Long userId);

    /**
     * 搜索小程序
     */
    PageResult<MiniProgramListItem> searchApps(MiniProgramSearchRequest request);

    /**
     * 获取推荐小程序
     */
    List<MiniProgramListItem> getRecommendApps(MiniProgramRecommendRequest request);

    /**
     * 获取分类下的小程序
     */
    PageResult<MiniProgramListItem> getAppsByCategory(String categoryCode, Integer pageNum, Integer pageSize);

    /**
     * 获取热门小程序榜单
     */
    List<MiniProgramListItem> getHotApps(Integer limit);

    /**
     * 获取新品小程序
     */
    List<MiniProgramListItem> getNewApps(Integer limit);

    /**
     * 更新小程序状态
     */
    boolean updateAppStatus(Long appId, Integer status);

    /**
     * 获取小程序统计信息
     */
    MiniProgramStatistics getAppStatistics(Long appId);

    /**
     * 增加下载量
     */
    void incrementDownloadCount(Long appId);

    /**
     * 上报小程序使用
     */
    void reportAppUsage(Long appId, Long userId, Integer duration);
}
