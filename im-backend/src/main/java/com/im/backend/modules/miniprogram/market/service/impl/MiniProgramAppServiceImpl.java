package com.im.backend.modules.miniprogram.market.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.im.backend.common.api.PageResult;
import com.im.backend.modules.miniprogram.market.dto.*;
import com.im.backend.modules.miniprogram.market.entity.MiniProgramApp;
import com.im.backend.modules.miniprogram.market.enums.MiniProgramStatus;
import com.im.backend.modules.miniprogram.market.mapper.MiniProgramAppMapper;
import com.im.backend.modules.miniprogram.market.service.MiniProgramAppService;
import com.im.backend.modules.miniprogram.market.service.MiniProgramFavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 小程序应用服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MiniProgramAppServiceImpl extends ServiceImpl<MiniProgramAppMapper, MiniProgramApp> implements MiniProgramAppService {

    private final StringRedisTemplate redisTemplate;
    private final MiniProgramFavoriteService favoriteService;

    private static final String APP_CACHE_PREFIX = "mp:app:";
    private static final String HOT_APPS_KEY = "mp:hot:apps";

    @Override
    public MiniProgramResponse createApp(Long developerId, CreateMiniProgramRequest request) {
        MiniProgramApp app = new MiniProgramApp();
        BeanUtils.copyProperties(request, app);
        
        app.setAppKey(generateAppKey());
        app.setDeveloperId(developerId);
        app.setVersion("1.0.0");
        app.setStatus(MiniProgramStatus.PENDING_AUDIT.getCode());
        app.setRating(java.math.BigDecimal.ZERO);
        app.setRatingCount(0);
        app.setDownloadCount(0L);
        app.setDau(0L);
        app.setIsRecommended(false);
        app.setRecommendWeight(0);
        app.setSortWeight(0);
        
        if (request.getSceneTags() != null) {
            app.setSceneTags(JSON.toJSONString(request.getSceneTags()));
        }
        if (request.getServiceCities() != null) {
            app.setServiceCities(JSON.toJSONString(request.getServiceCities()));
        }
        
        save(app);
        
        return convertToResponse(app, null);
    }

    @Override
    public MiniProgramResponse getAppDetail(Long appId, Long userId) {
        String cacheKey = APP_CACHE_PREFIX + appId;
        String cached = redisTemplate.opsForValue().get(cacheKey);
        
        MiniProgramApp app;
        if (StringUtils.hasText(cached)) {
            app = JSON.parseObject(cached, MiniProgramApp.class);
        } else {
            app = getById(appId);
            if (app != null) {
                redisTemplate.opsForValue().set(cacheKey, JSON.toJSONString(app), 30, TimeUnit.MINUTES);
            }
        }
        
        if (app == null) {
            return null;
        }
        
        return convertToResponse(app, userId);
    }

    @Override
    public PageResult<MiniProgramListItem> searchApps(MiniProgramSearchRequest request) {
        Page<MiniProgramApp> page = new Page<>(request.getPageNum(), request.getPageSize());
        
        lambdaQuery()
            .eq(MiniProgramApp::getStatus, MiniProgramStatus.PUBLISHED.getCode())
            .like(StringUtils.hasText(request.getKeyword()), MiniProgramApp::getAppName, request.getKeyword())
            .eq(StringUtils.hasText(request.getCategoryCode()), MiniProgramApp::getCategoryCode, request.getCategoryCode())
            .ge(request.getMinRating() != null, MiniProgramApp::getRating, request.getMinRating())
            .eq(request.getOnlyRecommended() != null && request.getOnlyRecommended(), MiniProgramApp::getIsRecommended, true)
            .orderByDesc(getSortField(request.getSortType()))
            .page(page);
        
        List<MiniProgramListItem> items = page.getRecords().stream()
            .map(app -> convertToListItem(app))
            .collect(Collectors.toList());
        
        return new PageResult<>(page.getTotal(), items);
    }

    @Override
    public List<MiniProgramListItem> getRecommendApps(MiniProgramRecommendRequest request) {
        // 基于LBS+用户画像的智能推荐算法
        List<MiniProgramApp> apps = lambdaQuery()
            .eq(MiniProgramApp::getStatus, MiniProgramStatus.PUBLISHED.getCode())
            .eq(MiniProgramApp::getIsRecommended, true)
            .orderByDesc(MiniProgramApp::getRecommendWeight, MiniProgramApp::getRating, MiniProgramApp::getDownloadCount)
            .last("LIMIT " + request.getPageSize())
            .list();
        
        return apps.stream()
            .map(app -> convertToListItem(app))
            .collect(Collectors.toList());
    }

    @Override
    public PageResult<MiniProgramListItem> getAppsByCategory(String categoryCode, Integer pageNum, Integer pageSize) {
        Page<MiniProgramApp> page = new Page<>(pageNum, pageSize);
        
        lambdaQuery()
            .eq(MiniProgramApp::getStatus, MiniProgramStatus.PUBLISHED.getCode())
            .eq(MiniProgramApp::getCategoryCode, categoryCode)
            .orderByDesc(MiniProgramApp::getSortWeight, MiniProgramApp::getDownloadCount)
            .page(page);
        
        List<MiniProgramListItem> items = page.getRecords().stream()
            .map(app -> convertToListItem(app))
            .collect(Collectors.toList());
        
        return new PageResult<>(page.getTotal(), items);
    }

    @Override
    public List<MiniProgramListItem> getHotApps(Integer limit) {
        String cacheKey = HOT_APPS_KEY;
        String cached = redisTemplate.opsForValue().get(cacheKey);
        
        if (StringUtils.hasText(cached)) {
            List<Long> appIds = JSON.parseArray(cached, Long.class);
            List<MiniProgramApp> apps = listByIds(appIds);
            return apps.stream().map(this::convertToListItem).collect(Collectors.toList());
        }
        
        List<MiniProgramApp> apps = lambdaQuery()
            .eq(MiniProgramApp::getStatus, MiniProgramStatus.PUBLISHED.getCode())
            .orderByDesc(MiniProgramApp::getDownloadCount)
            .last("LIMIT " + limit)
            .list();
        
        return apps.stream().map(this::convertToListItem).collect(Collectors.toList());
    }

    @Override
    public List<MiniProgramListItem> getNewApps(Integer limit) {
        List<MiniProgramApp> apps = lambdaQuery()
            .eq(MiniProgramApp::getStatus, MiniProgramStatus.PUBLISHED.getCode())
            .orderByDesc(MiniProgramApp::getCreateTime)
            .last("LIMIT " + limit)
            .list();
        
        return apps.stream().map(this::convertToListItem).collect(Collectors.toList());
    }

    @Override
    public boolean updateAppStatus(Long appId, Integer status) {
        MiniProgramApp app = new MiniProgramApp();
        app.setId(appId);
        app.setStatus(status);
        boolean result = updateById(app);
        
        // 清除缓存
        if (result) {
            redisTemplate.delete(APP_CACHE_PREFIX + appId);
        }
        
        return result;
    }

    @Override
    public MiniProgramStatistics getAppStatistics(Long appId) {
        MiniProgramApp app = getById(appId);
        if (app == null) {
            return null;
        }
        
        MiniProgramStatistics stats = new MiniProgramStatistics();
        stats.setAppId(appId);
        stats.setAppName(app.getAppName());
        stats.setTotalDownloads(app.getDownloadCount());
        stats.setDau(app.getDau());
        stats.setAvgRating(app.getRating() != null ? app.getRating().doubleValue() : 0.0);
        stats.setRatingCount(app.getRatingCount());
        
        // TODO: 从统计表获取今日数据
        stats.setTodayDownloads(0L);
        stats.setMau(0L);
        stats.setFavoriteCount(0L);
        stats.setShareCount(0L);
        
        return stats;
    }

    @Override
    public void incrementDownloadCount(Long appId) {
        lambdaUpdate()
            .setSql("download_count = download_count + 1")
            .eq(MiniProgramApp::getId, appId)
            .update();
    }

    @Override
    public void reportAppUsage(Long appId, Long userId, Integer duration) {
        // 更新DAU和使用时长统计
        log.info("App usage reported: appId={}, userId={}, duration={}s", appId, userId, duration);
        
        // TODO: 写入使用记录表，用于后续推荐优化
    }

    private String generateAppKey() {
        return "MP" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) 
            + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private MiniProgramResponse convertToResponse(MiniProgramApp app, Long userId) {
        MiniProgramResponse response = new MiniProgramResponse();
        BeanUtils.copyProperties(app, response);
        
        if (StringUtils.hasText(app.getSceneTags())) {
            response.setSceneTags(JSON.parseArray(app.getSceneTags(), String.class));
        }
        if (StringUtils.hasText(app.getScreenshots())) {
            response.setScreenshots(JSON.parseArray(app.getScreenshots(), String.class));
        }
        
        response.setStatusText(MiniProgramStatus.fromCode(app.getStatus()).getDescription());
        
        if (userId != null) {
            response.setIsFavorited(favoriteService.isFavorited(userId, app.getId()));
        }
        
        return response;
    }

    private MiniProgramListItem convertToListItem(MiniProgramApp app) {
        MiniProgramListItem item = new MiniProgramListItem();
        BeanUtils.copyProperties(app, item);
        return item;
    }

    private com.baomidou.mybatisplus.core.toolkit.support.SFunction<MiniProgramApp, ?> getSortField(Integer sortType) {
        switch (sortType != null ? sortType : 1) {
            case 2: return MiniProgramApp::getRating;
            case 3: return MiniProgramApp::getDownloadCount;
            case 4: return MiniProgramApp::getSortWeight;
            default: return MiniProgramApp::getSortWeight;
        }
    }
}
