package com.im.backend.config;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Apollo 配置中心监听器
 * 监听灰度发布配置变更并动态更新
 */
@Slf4j
@Component
public class ApolloConfigListener {

    private final String appId;
    private final String metaServer;
    private final String namespace;
    
    @Value("${im.grayrelease.enabled:false}")
    private boolean grayReleaseEnabled;

    @Value("${im.grayrelease.percentage:0}")
    private int grayReleasePercentage;

    @Value("${im.grayrelease.whitelist-users:}")
    private String[] whitelistUsers;

    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private Config config;

    public ApolloConfigListener(String appId, String metaServer, String namespace) {
        this.appId = appId;
        this.metaServer = metaServer;
        this.namespace = namespace;
    }

    @PostConstruct
    public void init() {
        try {
            // 系统属性配置
            System.setProperty("app.id", appId);
            System.setProperty("apollo.meta", metaServer);
            System.setProperty("apollo.bootstrap.enabled", "true");
            System.setProperty("apollo.bootstrap.namespaces", namespace);

            // 获取配置
            config = ConfigService.getConfig(namespace);
            initialized.set(true);

            log.info("Apollo config listener initialized for app: {}, namespace: {}", 
                    appId, namespace);
            
            // 加载初始配置
            loadInitialConfig();
        } catch (Exception e) {
            log.warn("Failed to initialize Apollo config listener: {}", e.getMessage());
            log.warn("Running without Apollo integration");
        }
    }

    /**
     * 加载初始配置
     */
    private void loadInitialConfig() {
        if (config == null) return;

        Boolean enabled = config.getProperty("grayrelease.enabled", Boolean.FALSE);
        Integer percentage = config.getProperty("grayrelease.percentage", Integer.valueOf(0));
        String[] users = config.getProperty("grayrelease.whitelist-users", new String[0]);

        log.info("Initial Apollo config loaded: enabled={}, percentage={}, whitelistSize={}", 
                enabled, percentage, users != null ? users.length : 0);
    }

    /**
     * 监听灰度发布配置变更
     */
    @ApolloConfigChangeListener(value = {"application", "gray-release"}, 
                                 interestedKeys = {"grayrelease.enabled", "grayrelease.percentage", "grayrelease.whitelist-users"})
    public void onGrayReleaseConfigChange(ConfigChangeEvent changeEvent) {
        log.info("Gray release config change detected");

        for (String key : changeEvent.changedKeys()) {
            ConfigChange change = changeEvent.getChange(key);
            log.info("Config changed: key={}, oldValue={}, newValue={}, changeType={}", 
                    key, change.getOldValue(), change.getNewValue(), change.getChangeType());

            switch (key) {
                case "grayrelease.enabled":
                    handleEnabledChange(change.getNewValue());
                    break;
                case "grayrelease.percentage":
                    handlePercentageChange(change.getNewValue());
                    break;
                case "grayrelease.whitelist-users":
                    handleWhitelistChange(change.getNewValue());
                    break;
                default:
                    log.warn("Unhandled config change: {}", key);
            }
        }
    }

    /**
     * 处理启用状态变更
     */
    private void handleEnabledChange(String newValue) {
        boolean enabled = Boolean.parseBoolean(newValue);
        log.info("Gray release enabled changed to: {}", enabled);
        // 触发事件通知其他组件
        publishConfigUpdateEvent("enabled", enabled);
    }

    /**
     * 处理百分比变更
     */
    private void handlePercentageChange(String newValue) {
        int percentage = Integer.parseInt(newValue);
        if (percentage < 0 || percentage > 100) {
            log.warn("Invalid percentage value: {}, must be between 0 and 100", percentage);
            return;
        }
        log.info("Gray release percentage changed to: {}%", percentage);
        publishConfigUpdateEvent("percentage", percentage);
    }

    /**
     * 处理白名单变更
     */
    private void handleWhitelistChange(String newValue) {
        String[] users = newValue.split(",");
        log.info("Gray release whitelist changed, new size: {}", users.length);
        publishConfigUpdateEvent("whitelist", users);
    }

    /**
     * 发布配置更新事件
     */
    private void publishConfigUpdateEvent(String key, Object value) {
        // 这里可以集成 Spring ApplicationEventPublisher
        // 或者通过其他方式通知 GrayReleaseRuleManager 更新配置
        log.debug("Config update event published: {} = {}", key, value);
    }

    /**
     * 监听所有配置变更
     */
    @ApolloConfigChangeListener
    public void onAnyConfigChange(ConfigChangeEvent changeEvent) {
        log.debug("Config change in namespace: {}, changes: {}", 
                changeEvent.getNamespace(), changeEvent.changedKeys().size());
    }

    /**
     * 获取配置值
     */
    public <T> T getConfigProperty(String key, T defaultValue) {
        if (!initialized.get() || config == null) {
            return defaultValue;
        }
        return config.getProperty(key, defaultValue);
    }

    /**
     * 获取配置值（字符串）
     */
    public String getConfigProperty(String key) {
        if (!initialized.get() || config == null) {
            return null;
        }
        return config.getProperty(key, null);
    }

    /**
     * 检查是否已初始化
     */
    public boolean isInitialized() {
        return initialized.get();
    }
}
