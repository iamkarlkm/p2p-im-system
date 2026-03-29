package com.im.backend.service;

import com.im.backend.entity.DarkModeConfigEntity;
import com.im.backend.repository.DarkModeConfigRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 暗黑模式配置服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DarkModeConfigService {

    private final DarkModeConfigRepository configRepository;
    private final ObjectMapper objectMapper;
    
    // 默认颜色配置
    private static final Map<DarkModeConfigEntity.ThemeMode, Map<String, String>> DEFAULT_THEMES = new HashMap<>();
    
    static {
        // 明亮模式默认配置
        Map<String, String> lightTheme = new HashMap<>();
        lightTheme.put("primaryColor", "#2196F3");
        lightTheme.put("backgroundColor", "#FFFFFF");
        lightTheme.put("textColor", "#212121");
        lightTheme.put("secondaryTextColor", "#757575");
        lightTheme.put("accentColor", "#FF4081");
        lightTheme.put("controlColor", "#E0E0E0");
        lightTheme.put("borderColor", "#BDBDBD");
        lightTheme.put("hoverColor", "#F5F5F5");
        DEFAULT_THEMES.put(DarkModeConfigEntity.ThemeMode.LIGHT, lightTheme);
        
        // 暗黑模式默认配置
        Map<String, String> darkTheme = new HashMap<>();
        darkTheme.put("primaryColor", "#90CAF9");
        darkTheme.put("backgroundColor", "#121212");
        darkTheme.put("textColor", "#E0E0E0");
        darkTheme.put("secondaryTextColor", "#AAAAAA");
        darkTheme.put("accentColor", "#FF80AB");
        darkTheme.put("controlColor", "#424242");
        darkTheme.put("borderColor", "#616161");
        darkTheme.put("hoverColor", "#2A2A2A");
        DEFAULT_THEMES.put(DarkModeConfigEntity.ThemeMode.DARK, darkTheme);
        
        // 自定义模式默认配置
        Map<String, String> customTheme = new HashMap<>();
        customTheme.put("primaryColor", "#4CAF50");
        customTheme.put("backgroundColor", "#FAFAFA");
        customTheme.put("textColor", "#263238");
        customTheme.put("secondaryTextColor", "#78909C");
        customTheme.put("accentColor", "#FF9800");
        customTheme.put("controlColor", "#CFD8DC");
        customTheme.put("borderColor", "#B0BEC5");
        customTheme.put("hoverColor", "#ECEFF1");
        DEFAULT_THEMES.put(DarkModeConfigEntity.ThemeMode.CUSTOM, customTheme);
    }
    
    /**
     * 为用户创建默认配置
     */
    @Transactional
    public DarkModeConfigEntity createDefaultConfig(String userId, DarkModeConfigEntity.Platform platform, String deviceId) {
        log.info("为用户 {} 创建默认暗黑模式配置，平台: {}, 设备: {}", userId, platform, deviceId);
        
        DarkModeConfigEntity config = DarkModeConfigEntity.builder()
                .userId(userId)
                .themeMode(DarkModeConfigEntity.ThemeMode.SYSTEM)
                .platform(platform)
                .deviceId(deviceId)
                .configName("default")
                .isActive(true)
                .useSystemColors(true)
                .opacityLevel(1.0)
                .fontScaleFactor(1.0)
                .highContrast(false)
                .reduceMotion(false)
                .nightProtection(false)
                .autoSwitchEnabled(false)
                .configVersion(1)
                .build();
        
        applyDefaultThemeColors(config);
        
        return configRepository.save(config);
    }
    
    /**
     * 创建自定义配置
     */
    @Transactional
    public DarkModeConfigEntity createCustomConfig(String userId, DarkModeConfigEntity.ThemeMode themeMode, 
                                                   DarkModeConfigEntity.Platform platform, String deviceId, 
                                                   Map<String, Object> customColors) {
        log.info("为用户 {} 创建自定义暗黑模式配置，主题: {}, 平台: {}", userId, themeMode, platform);
        
        DarkModeConfigEntity config = DarkModeConfigEntity.builder()
                .userId(userId)
                .themeMode(themeMode)
                .platform(platform)
                .deviceId(deviceId)
                .configName("custom_" + UUID.randomUUID().toString().substring(0, 8))
                .isActive(false)
                .useSystemColors(false)
                .opacityLevel(1.0)
                .fontScaleFactor(1.0)
                .highContrast(false)
                .reduceMotion(false)
                .nightProtection(false)
                .autoSwitchEnabled(false)
                .configVersion(1)
                .build();
        
        if (customColors != null && !customColors.isEmpty()) {
            applyCustomColors(config, customColors);
        } else {
            applyDefaultThemeColors(config);
        }
        
        if (customColors != null) {
            try {
                config.setMetadata(objectMapper.writeValueAsString(customColors));
            } catch (Exception e) {
                log.warn("无法序列化元数据: {}", e.getMessage());
            }
        }
        
        return configRepository.save(config);
    }
    
    /**
     * 获取用户的活跃配置
     */
    public Optional<DarkModeConfigEntity> getActiveConfig(String userId) {
        return configRepository.findByUserIdAndIsActiveTrue(userId);
    }
    
    /**
     * 获取用户指定设备的配置
     */
    public Optional<DarkModeConfigEntity> getDeviceConfig(String userId, String deviceId) {
        return configRepository.findByUserIdAndDeviceId(userId, deviceId);
    }
    
    /**
     * 获取用户所有配置
     */
    public List<DarkModeConfigEntity> getUserConfigs(String userId) {
        return configRepository.findByUserId(userId);
    }
    
    /**
     * 激活指定配置
     */
    @Transactional
    public boolean activateConfig(String userId, Long configId) {
        Optional<DarkModeConfigEntity> configOpt = configRepository.findById(configId);
        if (configOpt.isEmpty() || !userId.equals(configOpt.get().getUserId())) {
            log.warn("配置不存在或用户不匹配: userId={}, configId={}", userId, configId);
            return false;
        }
        
        // 停用用户的其他配置
        configRepository.deactivateOtherConfigs(userId, configId);
        
        // 激活指定配置
        int updated = configRepository.activateConfig(configId, LocalDateTime.now());
        
        log.info("激活用户 {} 的配置 {}，更新结果: {}", userId, configId, updated > 0);
        return updated > 0;
    }
    
    /**
     * 切换主题模式
     */
    @Transactional
    public DarkModeConfigEntity switchThemeMode(String userId, DarkModeConfigEntity.ThemeMode newThemeMode) {
        Optional<DarkModeConfigEntity> activeConfigOpt = getActiveConfig(userId);
        DarkModeConfigEntity config;
        
        if (activeConfigOpt.isPresent()) {
            config = activeConfigOpt.get();
            config.setThemeMode(newThemeMode);
            config.setUpdatedAt(LocalDateTime.now());
            configRepository.incrementVersion(config.getId(), LocalDateTime.now());
            
            // 如果是系统或默认主题，应用默认颜色
            if (newThemeMode != DarkModeConfigEntity.ThemeMode.CUSTOM) {
                applyDefaultThemeColors(config);
                config.setUseSystemColors(newThemeMode == DarkModeConfigEntity.ThemeMode.SYSTEM);
            }
        } else {
            // 创建新配置
            config = createDefaultConfig(userId, DarkModeConfigEntity.Platform.DESKTOP, "default");
            config.setThemeMode(newThemeMode);
        }
        
        return configRepository.save(config);
    }
    
    /**
     * 更新自定义颜色
     */
    @Transactional
    public DarkModeConfigEntity updateCustomColors(String userId, Map<String, String> colorUpdates) {
        Optional<DarkModeConfigEntity> activeConfigOpt = getActiveConfig(userId);
        if (activeConfigOpt.isEmpty()) {
            log.warn("用户没有活动配置: {}", userId);
            throw new IllegalArgumentException("用户没有活动配置");
        }
        
        DarkModeConfigEntity config = activeConfigOpt.get();
        config.setThemeMode(DarkModeConfigEntity.ThemeMode.CUSTOM);
        config.setUseSystemColors(false);
        
        // 更新颜色
        if (colorUpdates.containsKey("primaryColor")) {
            config.setPrimaryColor(colorUpdates.get("primaryColor"));
        }
        if (colorUpdates.containsKey("backgroundColor")) {
            config.setBackgroundColor(colorUpdates.get("backgroundColor"));
        }
        if (colorUpdates.containsKey("textColor")) {
            config.setTextColor(colorUpdates.get("textColor"));
        }
        if (colorUpdates.containsKey("secondaryTextColor")) {
            config.setSecondaryTextColor(colorUpdates.get("secondaryTextColor"));
        }
        if (colorUpdates.containsKey("accentColor")) {
            config.setAccentColor(colorUpdates.get("accentColor"));
        }
        if (colorUpdates.containsKey("controlColor")) {
            config.setControlColor(colorUpdates.get("controlColor"));
        }
        if (colorUpdates.containsKey("borderColor")) {
            config.setBorderColor(colorUpdates.get("borderColor"));
        }
        if (colorUpdates.containsKey("hoverColor")) {
            config.setHoverColor(colorUpdates.get("hoverColor"));
        }
        
        config.setUpdatedAt(LocalDateTime.now());
        configRepository.incrementVersion(config.getId(), LocalDateTime.now());
        
        return configRepository.save(config);
    }
    
    /**
     * 启用/禁用高对比度模式
     */
    @Transactional
    public boolean toggleHighContrast(String userId, boolean enabled) {
        Optional<DarkModeConfigEntity> activeConfigOpt = getActiveConfig(userId);
        if (activeConfigOpt.isEmpty()) {
            return false;
        }
        
        DarkModeConfigEntity config = activeConfigOpt.get();
        if (enabled) {
            configRepository.enableHighContrast(config.getId(), LocalDateTime.now());
        } else {
            configRepository.disableHighContrast(config.getId(), LocalDateTime.now());
        }
        
        log.info("用户 {} {}高对比度模式", userId, enabled ? "启用" : "禁用");
        return true;
    }
    
    /**
     * 启用/禁用减少动画
     */
    @Transactional
    public boolean toggleReduceMotion(String userId, boolean enabled) {
        Optional<DarkModeConfigEntity> activeConfigOpt = getActiveConfig(userId);
        if (activeConfigOpt.isEmpty()) {
            return false;
        }
        
        DarkModeConfigEntity config = activeConfigOpt.get();
        if (enabled) {
            configRepository.enableReduceMotion(config.getId(), LocalDateTime.now());
        } else {
            configRepository.disableReduceMotion(config.getId(), LocalDateTime.now());
        }
        
        log.info("用户 {} {}减少动画", userId, enabled ? "启用" : "禁用");
        return true;
    }
    
    /**
     * 启用/禁用夜间保护
     */
    @Transactional
    public boolean toggleNightProtection(String userId, boolean enabled) {
        Optional<DarkModeConfigEntity> activeConfigOpt = getActiveConfig(userId);
        if (activeConfigOpt.isEmpty()) {
            return false;
        }
        
        DarkModeConfigEntity config = activeConfigOpt.get();
        if (enabled) {
            configRepository.enableNightProtection(config.getId(), LocalDateTime.now());
        } else {
            configRepository.disableNightProtection(config.getId(), LocalDateTime.now());
        }
        
        log.info("用户 {} {}夜间保护", userId, enabled ? "启用" : "禁用");
        return true;
    }
    
    /**
     * 配置自动切换时间
     */
    @Transactional
    public boolean configureAutoSwitch(String userId, boolean enabled, String startTime, String endTime) {
        Optional<DarkModeConfigEntity> activeConfigOpt = getActiveConfig(userId);
        if (activeConfigOpt.isEmpty()) {
            return false;
        }
        
        DarkModeConfigEntity config = activeConfigOpt.get();
        config.setAutoSwitchEnabled(enabled);
        if (enabled) {
            // 验证时间格式
            if (!isValidTimeFormat(startTime) || !isValidTimeFormat(endTime)) {
                throw new IllegalArgumentException("时间格式应为 HH:mm");
            }
            config.setAutoSwitchStart(startTime);
            config.setAutoSwitchEnd(endTime);
        }
        config.setUpdatedAt(LocalDateTime.now());
        configRepository.save(config);
        
        log.info("用户 {} 配置自动切换: enabled={}, start={}, end={}", userId, enabled, startTime, endTime);
        return true;
    }
    
    /**
     * 检查是否应该自动切换到暗黑模式
     */
    public boolean shouldAutoSwitchToDark(String userId) {
        Optional<DarkModeConfigEntity> activeConfigOpt = getActiveConfig(userId);
        if (activeConfigOpt.isEmpty() || !activeConfigOpt.get().getAutoSwitchEnabled()) {
            return false;
        }
        
        DarkModeConfigEntity config = activeConfigOpt.get();
        if (config.getAutoSwitchStart() == null || config.getAutoSwitchEnd() == null) {
            return false;
        }
        
        try {
            LocalTime startTime = LocalTime.parse(config.getAutoSwitchStart());
            LocalTime endTime = LocalTime.parse(config.getAutoSwitchEnd());
            LocalTime currentTime = LocalTime.now();
            
            // 处理跨日的情况
            if (startTime.isBefore(endTime)) {
                // 同一天内
                return !currentTime.isBefore(startTime) && currentTime.isBefore(endTime);
            } else {
                // 跨日
                return !currentTime.isBefore(startTime) || currentTime.isBefore(endTime);
            }
        } catch (Exception e) {
            log.error("解析自动切换时间失败: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 同步配置到指定设备
     */
    @Transactional
    public boolean syncConfigToDevice(String userId, Long configId, String deviceId) {
        Optional<DarkModeConfigEntity> sourceConfigOpt = configRepository.findById(configId);
        if (sourceConfigOpt.isEmpty() || !userId.equals(sourceConfigOpt.get().getUserId())) {
            return false;
        }
        
        DarkModeConfigEntity sourceConfig = sourceConfigOpt.get();
        
        // 查找或创建设备配置
        Optional<DarkModeConfigEntity> deviceConfigOpt = configRepository.findByUserIdAndDeviceId(userId, deviceId);
        DarkModeConfigEntity deviceConfig;
        
        if (deviceConfigOpt.isPresent()) {
            deviceConfig = deviceConfigOpt.get();
            // 复制配置
            copyConfigProperties(sourceConfig, deviceConfig);
        } else {
            deviceConfig = DarkModeConfigEntity.builder()
                    .userId(userId)
                    .deviceId(deviceId)
                    .configName("device_" + deviceId)
                    .isActive(false)
                    .build();
            copyConfigProperties(sourceConfig, deviceConfig);
        }
        
        deviceConfig.setLastSyncedAt(LocalDateTime.now());
        configRepository.save(deviceConfig);
        
        log.info("同步配置到设备: userId={}, deviceId={}", userId, deviceId);
        return true;
    }
    
    /**
     * 更新透明度
     */
    @Transactional
    public boolean updateOpacity(String userId, Double opacityLevel) {
        if (opacityLevel < 0.0 || opacityLevel > 1.0) {
            throw new IllegalArgumentException("透明度必须在 0.0 到 1.0 之间");
        }
        
        Optional<DarkModeConfigEntity> activeConfigOpt = getActiveConfig(userId);
        if (activeConfigOpt.isEmpty()) {
            return false;
        }
        
        DarkModeConfigEntity config = activeConfigOpt.get();
        config.setOpacityLevel(opacityLevel);
        config.setUpdatedAt(LocalDateTime.now());
        configRepository.save(config);
        
        log.info("更新用户 {} 的透明度: {}", userId, opacityLevel);
        return true;
    }
    
    /**
     * 更新字体缩放
     */
    @Transactional
    public boolean updateFontScale(String userId, Double fontScaleFactor) {
        if (fontScaleFactor < 0.8 || fontScaleFactor > 1.5) {
            throw new IllegalArgumentException("字体缩放因子必须在 0.8 到 1.5 之间");
        }
        
        Optional<DarkModeConfigEntity> activeConfigOpt = getActiveConfig(userId);
        if (activeConfigOpt.isEmpty()) {
            return false;
        }
        
        DarkModeConfigEntity config = activeConfigOpt.get();
        config.setFontScaleFactor(fontScaleFactor);
        config.setUpdatedAt(LocalDateTime.now());
        configRepository.save(config);
        
        log.info("更新用户 {} 的字体缩放: {}", userId, fontScaleFactor);
        return true;
    }
    
    /**
     * 删除配置
     */
    @Transactional
    public boolean deleteConfig(String userId, Long configId) {
        int deleted = configRepository.deleteByUserIdAndId(userId, configId);
        log.info("删除用户 {} 的配置 {}，结果: {}", userId, configId, deleted > 0);
        return deleted > 0;
    }
    
    /**
     * 获取统计信息
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // 主题模式统计
        List<Object[]> themeStats = configRepository.countByThemeMode();
        Map<String, Long> themeCounts = new HashMap<>();
        for (Object[] stat : themeStats) {
            String theme = stat[0].toString();
            Long count = (Long) stat[1];
            themeCounts.put(theme, count);
        }
        stats.put("themeModeCounts", themeCounts);
        
        // 平台统计
        List<Object[]> platformStats = configRepository.countByPlatform();
        Map<String, Long> platformCounts = new HashMap<>();
        for (Object[] stat : platformStats) {
            String platform = stat[0].toString();
            Long count = (Long) stat[1];
            platformCounts.put(platform, count);
        }
        stats.put("platformCounts", platformCounts);
        
        // 功能使用统计
        stats.put("highContrastCount", configRepository.countHighContrastActive());
        stats.put("reduceMotionCount", configRepository.countReduceMotionActive());
        stats.put("nightProtectionCount", configRepository.countNightProtectionActive());
        stats.put("autoSwitchCount", configRepository.countAutoSwitchActive());
        
        // 总数统计
        long totalConfigs = configRepository.count();
        stats.put("totalConfigs", totalConfigs);
        
        return stats;
    }
    
    /**
     * 清理过时的临时配置
     */
    @Transactional
    public int cleanupExpiredConfigs() {
        LocalDateTime expireTime = LocalDateTime.now().minusDays(7);
        return configRepository.deleteExpiredTemporary(expireTime);
    }
    
    // 私有辅助方法
    
    private void applyDefaultThemeColors(DarkModeConfigEntity config) {
        Map<String, String> defaultColors = DEFAULT_THEMES.get(config.getThemeMode());
        if (defaultColors == null) {
            defaultColors = DEFAULT_THEMES.get(DarkModeConfigEntity.ThemeMode.SYSTEM);
        }
        
        if (defaultColors != null) {
            config.setPrimaryColor(defaultColors.get("primaryColor"));
            config.setBackgroundColor(defaultColors.get("backgroundColor"));
            config.setTextColor(defaultColors.get("textColor"));
            config.setSecondaryTextColor(defaultColors.get("secondaryTextColor"));
            config.setAccentColor(defaultColors.get("accentColor"));
            config.setControlColor(defaultColors.get("controlColor"));
            config.setBorderColor(defaultColors.get("borderColor"));
            config.setHoverColor(defaultColors.get("hoverColor"));
        }
    }
    
    private void applyCustomColors(DarkModeConfigEntity config, Map<String, Object> customColors) {
        if (customColors.containsKey("primaryColor")) {
            config.setPrimaryColor(customColors.get("primaryColor").toString());
        }
        if (customColors.containsKey("backgroundColor")) {
            config.setBackgroundColor(customColors.get("backgroundColor").toString());
        }
        if (customColors.containsKey("textColor")) {
            config.setTextColor(customColors.get("textColor").toString());
        }
        if (customColors.containsKey("secondaryTextColor")) {
            config.setSecondaryTextColor(customColors.get("secondaryTextColor").toString());
        }
        if (customColors.containsKey("accentColor")) {
            config.setAccentColor(customColors.get("accentColor").toString());
        }
        if (customColors.containsKey("controlColor")) {
            config.setControlColor(customColors.get("controlColor").toString());
        }
        if (customColors.containsKey("borderColor")) {
            config.setBorderColor(customColors.get("borderColor").toString());
        }
        if (customColors.containsKey("hoverColor")) {
            config.setHoverColor(customColors.get("hoverColor").toString());
        }
    }
    
    private void copyConfigProperties(DarkModeConfigEntity source, DarkModeConfigEntity target) {
        target.setThemeMode(source.getThemeMode());
        target.setPlatform(source.getPlatform());
        target.setPrimaryColor(source.getPrimaryColor());
        target.setBackgroundColor(source.getBackgroundColor());
        target.setTextColor(source.getTextColor());
        target.setSecondaryTextColor(source.getSecondaryTextColor());
        target.setAccentColor(source.getAccentColor());
        target.setControlColor(source.getControlColor());
        target.setBorderColor(source.getBorderColor());
        target.setHoverColor(source.getHoverColor());
        target.setUseSystemColors(source.getUseSystemColors());
        target.setOpacityLevel(source.getOpacityLevel());
        target.setFontScaleFactor(source.getFontScaleFactor());
        target.setHighContrast(source.getHighContrast());
        target.setReduceMotion(source.getReduceMotion());
        target.setNightProtection(source.getNightProtection());
        target.setAutoSwitchEnabled(source.getAutoSwitchEnabled());
        target.setAutoSwitchStart(source.getAutoSwitchStart());
        target.setAutoSwitchEnd(source.getAutoSwitchEnd());
        target.setMetadata(source.getMetadata());
        target.setConfigVersion(source.getConfigVersion() + 1);
        target.setUpdatedAt(LocalDateTime.now());
    }
    
    private boolean isValidTimeFormat(String time) {
        try {
            LocalTime.parse(time);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 获取当前主题的颜色配置
     */
    public Map<String, String> getCurrentThemeColors(String userId) {
        Optional<DarkModeConfigEntity> configOpt = getActiveConfig(userId);
        if (configOpt.isEmpty()) {
            return DEFAULT_THEMES.get(DarkModeConfigEntity.ThemeMode.LIGHT);
        }
        
        DarkModeConfigEntity config = configOpt.get();
        Map<String, String> colors = new HashMap<>();
        
        colors.put("themeMode", config.getThemeMode().name());
        colors.put("primaryColor", config.getPrimaryColor());
        colors.put("backgroundColor", config.getBackgroundColor());
        colors.put("textColor", config.getTextColor());
        colors.put("secondaryTextColor", config.getSecondaryTextColor());
        colors.put("accentColor", config.getAccentColor());
        colors.put("controlColor", config.getControlColor());
        colors.put("borderColor", config.getBorderColor());
        colors.put("hoverColor", config.getHoverColor());
        colors.put("opacityLevel", config.getOpacityLevel() != null ? config.getOpacityLevel().toString() : "1.0");
        colors.put("fontScaleFactor", config.getFontScaleFactor() != null ? config.getFontScaleFactor().toString() : "1.0");
        colors.put("highContrast", config.getHighContrast() != null ? config.getHighContrast().toString() : "false");
        colors.put("reduceMotion", config.getReduceMotion() != null ? config.getReduceMotion().toString() : "false");
        colors.put("nightProtection", config.getNightProtection() != null ? config.getNightProtection().toString() : "false");
        colors.put("useSystemColors", config.getUseSystemColors() != null ? config.getUseSystemColors().toString() : "true");
        
        return colors;
    }
    
    /**
     * 导出配置为 JSON
     */
    public String exportConfig(String userId, Long configId) {
        Optional<DarkModeConfigEntity> configOpt = configRepository.findById(configId);
        if (configOpt.isEmpty() || !userId.equals(configOpt.get().getUserId())) {
            throw new IllegalArgumentException("配置不存在或用户不匹配");
        }
        
        try {
            Map<String, Object> exportData = new HashMap<>();
            DarkModeConfigEntity config = configOpt.get();
            
            exportData.put("id", config.getId());
            exportData.put("userId", config.getUserId());
            exportData.put("themeMode", config.getThemeMode());
            exportData.put("platform", config.getPlatform());
            exportData.put("configName", config.getConfigName());
            exportData.put("isActive", config.getIsActive());
            exportData.put("colors", getCurrentThemeColors(userId));
            exportData.put("settings", Map.of(
                "highContrast", config.getHighContrast(),
                "reduceMotion", config.getReduceMotion(),
                "nightProtection", config.getNightProtection(),
                "autoSwitchEnabled", config.getAutoSwitchEnabled(),
                "autoSwitchStart", config.getAutoSwitchStart(),
                "autoSwitchEnd", config.getAutoSwitchEnd(),
                "opacityLevel", config.getOpacityLevel(),
                "fontScaleFactor", config.getFontScaleFactor()
            ));
            exportData.put("createdAt", config.getCreatedAt());
            exportData.put("updatedAt", config.getUpdatedAt());
            exportData.put("configVersion", config.getConfigVersion());
            
            return objectMapper.writeValueAsString(exportData);
        } catch (Exception e) {
            log.error("导出配置失败: {}", e.getMessage());
            throw new RuntimeException("导出配置失败", e);
        }
    }
}