package com.im.backend.modules.miniprogram.market.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.im.backend.common.api.PageResult;
import com.im.backend.modules.miniprogram.market.dto.FavoriteRequest;
import com.im.backend.modules.miniprogram.market.dto.MiniProgramListItem;
import com.im.backend.modules.miniprogram.market.entity.MiniProgramApp;
import com.im.backend.modules.miniprogram.market.entity.MiniProgramFavorite;
import com.im.backend.modules.miniprogram.market.mapper.MiniProgramFavoriteMapper;
import com.im.backend.modules.miniprogram.market.service.MiniProgramAppService;
import com.im.backend.modules.miniprogram.market.service.MiniProgramFavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 小程序收藏服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MiniProgramFavoriteServiceImpl extends ServiceImpl<MiniProgramFavoriteMapper, MiniProgramFavorite> implements MiniProgramFavoriteService {

    private final MiniProgramAppService appService;
    private final StringRedisTemplate redisTemplate;

    private static final String FAVORITE_CACHE_PREFIX = "mp:fav:";

    @Override
    public boolean favoriteApp(Long userId, FavoriteRequest request) {
        // 检查是否已收藏
        if (isFavorited(userId, request.getAppId())) {
            return true;
        }
        
        MiniProgramFavorite favorite = new MiniProgramFavorite();
        favorite.setUserId(userId);
        favorite.setAppId(request.getAppId());
        favorite.setGroupId(request.getGroupId());
        favorite.setRemarkName(request.getRemarkName());
        favorite.setUseCount(0);
        
        boolean result = save(favorite);
        
        // 清除缓存
        if (result) {
            redisTemplate.delete(FAVORITE_CACHE_PREFIX + userId + ":" + request.getAppId());
            redisTemplate.delete(FAVORITE_CACHE_PREFIX + userId + ":list");
        }
        
        return result;
    }

    @Override
    public boolean unfavoriteApp(Long userId, Long appId) {
        boolean result = lambdaUpdate()
            .eq(MiniProgramFavorite::getUserId, userId)
            .eq(MiniProgramFavorite::getAppId, appId)
            .remove();
        
        if (result) {
            redisTemplate.delete(FAVORITE_CACHE_PREFIX + userId + ":" + appId);
            redisTemplate.delete(FAVORITE_CACHE_PREFIX + userId + ":list");
        }
        
        return result;
    }

    @Override
    public PageResult<MiniProgramListItem> getUserFavorites(Long userId, Integer pageNum, Integer pageSize) {
        Page<MiniProgramFavorite> page = new Page<>(pageNum, pageSize);
        
        lambdaQuery()
            .eq(MiniProgramFavorite::getUserId, userId)
            .orderByDesc(MiniProgramFavorite::getLastUseTime, MiniProgramFavorite::getCreateTime)
            .page(page);
        
        List<Long> appIds = page.getRecords().stream()
            .map(MiniProgramFavorite::getAppId)
            .collect(Collectors.toList());
        
        if (appIds.isEmpty()) {
            return new PageResult<>(0L, List.of());
        }
        
        List<MiniProgramApp> apps = appService.listByIds(appIds);
        
        List<MiniProgramListItem> items = apps.stream()
            .map(app -> {
                MiniProgramListItem item = new MiniProgramListItem();
                BeanUtils.copyProperties(app, item);
                item.setIsFavorited(true);
                return item;
            })
            .collect(Collectors.toList());
        
        return new PageResult<>(page.getTotal(), items);
    }

    @Override
    public boolean isFavorited(Long userId, Long appId) {
        if (userId == null) {
            return false;
        }
        
        String cacheKey = FAVORITE_CACHE_PREFIX + userId + ":" + appId;
        String cached = redisTemplate.opsForValue().get(cacheKey);
        
        if (cached != null) {
            return "1".equals(cached);
        }
        
        boolean exists = lambdaQuery()
            .eq(MiniProgramFavorite::getUserId, userId)
            .eq(MiniProgramFavorite::getAppId, appId)
            .exists();
        
        redisTemplate.opsForValue().set(cacheKey, exists ? "1" : "0", 10, TimeUnit.MINUTES);
        
        return exists;
    }

    @Override
    public Long getFavoriteCount(Long userId) {
        return lambdaQuery()
            .eq(MiniProgramFavorite::getUserId, userId)
            .count();
    }

    @Override
    public void updateLastUseTime(Long userId, Long appId) {
        lambdaUpdate()
            .set(MiniProgramFavorite::getLastUseTime, LocalDateTime.now())
            .setSql("use_count = use_count + 1")
            .eq(MiniProgramFavorite::getUserId, userId)
            .eq(MiniProgramFavorite::getAppId, appId)
            .update();
    }

    @Override
    public List<MiniProgramListItem> getFrequentlyUsedApps(Long userId, Integer limit) {
        List<MiniProgramFavorite> favorites = lambdaQuery()
            .eq(MiniProgramFavorite::getUserId, userId)
            .orderByDesc(MiniProgramFavorite::getUseCount, MiniProgramFavorite::getLastUseTime)
            .last("LIMIT " + limit)
            .list();
        
        List<Long> appIds = favorites.stream()
            .map(MiniProgramFavorite::getAppId)
            .collect(Collectors.toList());
        
        if (appIds.isEmpty()) {
            return List.of();
        }
        
        List<MiniProgramApp> apps = appService.listByIds(appIds);
        
        return apps.stream()
            .map(app -> {
                MiniProgramListItem item = new MiniProgramListItem();
                BeanUtils.copyProperties(app, item);
                item.setIsFavorited(true);
                return item;
            })
            .collect(Collectors.toList());
    }
}
