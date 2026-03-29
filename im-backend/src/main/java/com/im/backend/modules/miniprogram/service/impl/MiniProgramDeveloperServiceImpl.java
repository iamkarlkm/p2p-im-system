package com.im.backend.modules.miniprogram.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.im.backend.modules.miniprogram.dto.*;
import com.im.backend.modules.miniprogram.entity.MiniProgramDeveloper;
import com.im.backend.modules.miniprogram.enums.DeveloperStatus;
import com.im.backend.modules.miniprogram.repository.MiniProgramDeveloperMapper;
import com.im.backend.modules.miniprogram.service.IMiniProgramDeveloperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 小程序开发者服务实现
 */
@Slf4j
@Service
public class MiniProgramDeveloperServiceImpl extends ServiceImpl<MiniProgramDeveloperMapper, MiniProgramDeveloper>
        implements IMiniProgramDeveloperService {

    @Autowired
    private MiniProgramDeveloperMapper developerMapper;

    @Override
    @Transactional
    public DeveloperResponse registerDeveloper(Long userId, RegisterDeveloperRequest request) {
        MiniProgramDeveloper existing = developerMapper.findByUserId(userId);
        if (existing != null) {
            throw new RuntimeException("用户已注册开发者");
        }

        MiniProgramDeveloper developer = new MiniProgramDeveloper();
        BeanUtils.copyProperties(request, developer);
        developer.setUserId(userId);
        developer.setStatus(DeveloperStatus.PENDING);
        developer.setVerified(false);
        developer.setAppCount(0);
        developer.setApiQuota(10000);
        developer.setUsedQuota(0);
        developer.setCreateTime(LocalDateTime.now());
        developer.setUpdateTime(LocalDateTime.now());

        developerMapper.insert(developer);
        log.info("开发者注册成功: userId={}, developerId={}", userId, developer.getId());

        return convertToResponse(developer);
    }

    @Override
    public DeveloperResponse getDeveloperInfo(Long userId) {
        MiniProgramDeveloper developer = developerMapper.findByUserId(userId);
        if (developer == null) {
            throw new RuntimeException("开发者不存在");
        }
        return convertToResponse(developer);
    }

    @Override
    @Transactional
    public DeveloperResponse updateDeveloper(Long userId, RegisterDeveloperRequest request) {
        MiniProgramDeveloper developer = developerMapper.findByUserId(userId);
        if (developer == null) {
            throw new RuntimeException("开发者不存在");
        }

        BeanUtils.copyProperties(request, developer, "id", "userId", "status", "verified");
        developer.setUpdateTime(LocalDateTime.now());
        developerMapper.updateById(developer);

        return convertToResponse(developer);
    }

    @Override
    @Transactional
    public void auditDeveloper(Long developerId, boolean approved, String reason) {
        MiniProgramDeveloper developer = developerMapper.selectById(developerId);
        if (developer == null) {
            throw new RuntimeException("开发者不存在");
        }

        if (approved) {
            developer.setStatus(DeveloperStatus.APPROVED);
            developer.setVerified(true);
            developer.setVerifiedTime(LocalDateTime.now());
        } else {
            developer.setStatus(DeveloperStatus.REJECTED);
            developer.setRejectReason(reason);
        }
        developer.setUpdateTime(LocalDateTime.now());
        developerMapper.updateById(developer);

        log.info("开发者审核完成: developerId={}, approved={}", developerId, approved);
    }

    @Override
    public boolean checkApiQuota(Long developerId, int required) {
        MiniProgramDeveloper developer = developerMapper.selectById(developerId);
        if (developer == null || developer.getStatus() != DeveloperStatus.APPROVED) {
            return false;
        }
        return (developer.getApiQuota() - developer.getUsedQuota()) >= required;
    }

    @Override
    @Transactional
    public void incrementUsedQuota(Long developerId, int count) {
        developerMapper.incrementUsedQuota(developerId, count);
    }

    private DeveloperResponse convertToResponse(MiniProgramDeveloper developer) {
        DeveloperResponse response = new DeveloperResponse();
        BeanUtils.copyProperties(developer, response);
        if (developer.getDeveloperType() != null) {
            response.setDeveloperTypeDesc(developer.getDeveloperType().getDesc());
        }
        if (developer.getStatus() != null) {
            response.setStatusDesc(developer.getStatus().getDesc());
        }
        return response;
    }
}
