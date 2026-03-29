package com.im.backend.modules.miniprogram.market.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.im.backend.common.api.PageResult;
import com.im.backend.modules.miniprogram.market.dto.FavoriteRequest;
import com.im.backend.modules.miniprogram.market.dto.MiniProgramListItem;
import com.im.backend.modules.miniprogram.market.entity.MiniProgramFavorite;

import java.util.List;

/**
 * 小程序收藏服务接口
 */
public interface MiniProgramFavoriteService extends IService<MiniProgramFavorite> {

    /**
     * 收藏小程序
     */
    boolean favoriteApp(Long userId, FavoriteRequest request);

    /**
     * 取消收藏
     */
    boolean unfavoriteApp(Long userId, Long appId);

    /**
     * 获取用户收藏列表
     */
    PageResult<MiniProgramListItem> getUserFavorites(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 检查是否已收藏
     */
    boolean isFavorited(Long userId, Long appId);

    /**
     * 获取用户收藏数量
     */
    Long getFavoriteCount(Long userId);

    /**
     * 更新最后使用时间
     */
    void updateLastUseTime(Long userId, Long appId);

    /**
     * 获取常用小程序列表
     */
    List<MiniProgramListItem> getFrequentlyUsedApps(Long userId, Integer limit);
}
