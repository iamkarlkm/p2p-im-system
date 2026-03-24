package com.im.backend.security.differentialprivacy.service;

import com.im.backend.security.differentialprivacy.entity.DifferentialPrivacyConfigEntity;
import com.im.backend.security.differentialprivacy.repository.DifferentialPrivacyConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 差分隐私配置服务
 * 提供配置管理、验证和查询功能
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DifferentialPrivacyConfigService {
    
    private final DifferentialPrivacyConfigRepository configRepository;
    
    @Transactional(readOnly = true)
    public Optional<DifferentialPrivacyConfigEntity> getConfigByKey(String configKey) {
        return configRepository.findByConfigKey(configKey);
    }
    
    @Transactional(readOnly = true)
    public List<DifferentialPrivacyConfigEntity> getAllActiveConfigs() {
        return configRepository.findByIsActiveTrue();
    }
    
    @Transactional(readOnly = true)
    public Page<DifferentialPrivacyConfigEntity> getConfigsPage(Pageable pageable) {
        return configRepository.findByIsActiveTrue(pageable);
    }
    
    @Transactional
    public DifferentialPrivacyConfigEntity createConfig(DifferentialPrivacyConfigEntity config) {
        log.info("Creating differential privacy config: {}", config.getConfigKey());
        config.setIsActive(true);
        config.setVersion(1);
        return configRepository.save(config);
    }
    
    @Transactional
    public DifferentialPrivacyConfigEntity updateConfig(String configKey, DifferentialPrivacyConfigEntity updates) {
        log.info("Updating differential privacy config: {}", configKey);
        return configRepository.findByConfigKey(configKey)
            .map(existing -> {
                if (updates.getConfigValue() != null) {
                    existing.setConfigValue(updates.getConfigValue());
                }
                if (updates.getDescription() != null) {
                    existing.setDescription(updates.getDescription());
                }
                if (updates.getEpsilon() != null) {
                    existing.setEpsilon(updates.getEpsilon());
                }
                if (updates.getDelta() != null) {
                    existing.setDelta(updates.getDelta());
                }
                if (updates.getPrivacyBudgetLimit() != null) {
                    existing.setPrivacyBudgetLimit(updates.getPrivacyBudgetLimit());
                }
                if (updates.getNoiseMechanism() != null) {
                    existing.setNoiseMechanism(updates.getNoiseMechanism());
                }
                if (updates.getRequiresApproval() != null) {
                    existing.setRequiresApproval(updates.getRequiresApproval());
                }
                return configRepository.save(existing);
            })
            .orElseThrow(() -> new RuntimeException("Config not found: " + configKey));
    }
    
    @Transactional
    public void deleteConfig(String configKey) {
        log.info("Deleting differential privacy config: {}", configKey);
        configRepository.findByConfigKey(configKey)
            .ifPresent(config -> {
                config.setIsActive(false);
                configRepository.save(config);
            });
    }
    
    @Transactional
    public DifferentialPrivacyConfigEntity updateApprovalStatus(String configKey, String status) {
        log.info("Updating approval status for config: {} to {}", configKey, status);
        return configRepository.findByConfigKey(configKey)
            .map(config -> {
                config.setApprovalStatus(status);
                return configRepository.save(config);
            })
            .orElseThrow(() -> new RuntimeException("Config not found: " + configKey));
    }
    
    @Transactional(readOnly = true)
    public List<DifferentialPrivacyConfigEntity> getPendingApprovals() {
        return configRepository.findPendingApprovals();
    }
    
    @Transactional(readOnly = true)
    public List<DifferentialPrivacyConfigEntity> getSensitiveConfigs() {
        return configRepository.findByIsSensitiveTrue();
    }
    
    @Transactional(readOnly = true)
    public List<DifferentialPrivacyConfigEntity> searchConfigs(String keyword) {
        return configRepository.searchByKeyword(keyword);
    }
    
    @Transactional(readOnly = true)
    public Long getActiveConfigCount() {
        return configRepository.countActiveConfigs();
    }
    
    @Transactional(readOnly = true)
    public Long getSensitiveConfigCount() {
        return configRepository.countSensitiveConfigs();
    }
    
    @Transactional(readOnly = true)
    public boolean validateEpsilon(Double epsilon, String configKey) {
        return getConfigByKey(configKey)
            .map(config -> config.getEpsilon() != null && epsilon <= config.getEpsilon())
            .orElse(false);
    }
}