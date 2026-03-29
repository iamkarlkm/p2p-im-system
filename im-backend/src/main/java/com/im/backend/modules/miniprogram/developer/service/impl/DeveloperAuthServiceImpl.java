package com.im.backend.modules.miniprogram.developer.service.impl;

import com.im.backend.modules.miniprogram.developer.dto.*;
import com.im.backend.modules.miniprogram.developer.entity.MiniProgramDeveloper;
import com.im.backend.modules.miniprogram.developer.enums.DeveloperLevel;
import com.im.backend.modules.miniprogram.developer.repository.MiniProgramDeveloperMapper;
import com.im.backend.modules.miniprogram.developer.service.IDeveloperAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 开发者认证服务实现
 */
@Service
@RequiredArgsConstructor
public class DeveloperAuthServiceImpl implements IDeveloperAuthService {
    
    private final MiniProgramDeveloperMapper developerMapper;
    
    @Override
    @Transactional
    public DeveloperResponse registerDeveloper(Long userId, String developerType, String developerName) {
        MiniProgramDeveloper developer = new MiniProgramDeveloper();
        developer.setUserId(userId);
        developer.setDeveloperType(developerType);
        developer.setDeveloperName(developerName);
        developer.setVerifyStatus(0);
        developer.setLevel(DeveloperLevel.JUNIOR.getLevel());
        developer.setPoints(0);
        developer.setBalance(BigDecimal.ZERO);
        developer.setTotalEarnings(BigDecimal.ZERO);
        developer.setComponentCount(0);
        developer.setProgramCount(0);
        developer.setStatus(1);
        developer.setCreateTime(LocalDateTime.now());
        developer.setUpdateTime(LocalDateTime.now());
        
        developerMapper.insert(developer);
        return convertToResponse(developer);
    }
    
    @Override
    public DeveloperResponse getDeveloperInfo(Long developerId) {
        MiniProgramDeveloper developer = developerMapper.selectById(developerId);
        return developer != null ? convertToResponse(developer) : null;
    }
    
    @Override
    public DeveloperResponse getDeveloperByUserId(Long userId) {
        MiniProgramDeveloper developer = developerMapper.selectByUserId(userId);
        return developer != null ? convertToResponse(developer) : null;
    }
    
    @Override
    @Transactional
    public boolean submitVerification(Long developerId, String verifyInfo) {
        MiniProgramDeveloper developer = developerMapper.selectById(developerId);
        if (developer == null) return false;
        developer.setVerifyStatus(1);
        developer.setVerifyInfo(verifyInfo);
        developer.setUpdateTime(LocalDateTime.now());
        return developerMapper.updateById(developer) > 0;
    }
    
    @Override
    @Transactional
    public boolean auditDeveloper(Long developerId, Integer status) {
        MiniProgramDeveloper developer = developerMapper.selectById(developerId);
        if (developer == null) return false;
        developer.setVerifyStatus(status);
        developer.setUpdateTime(LocalDateTime.now());
        return developerMapper.updateById(developer) > 0;
    }
    
    @Override
    @Transactional
    public boolean addPoints(Long developerId, Integer points) {
        return developerMapper.addPoints(developerId, points) > 0;
    }
    
    @Override
    public DeveloperEarningsResponse getEarningsStats(Long developerId) {
        MiniProgramDeveloper developer = developerMapper.selectById(developerId);
        if (developer == null) return null;
        
        DeveloperEarningsResponse response = new DeveloperEarningsResponse();
        response.setDeveloperId(developerId);
        response.setBalance(developer.getBalance());
        response.setTotalEarnings(developer.getTotalEarnings());
        response.setMonthlyEarnings(BigDecimal.ZERO);
        response.setWeeklyEarnings(BigDecimal.ZERO);
        return response;
    }
    
    private DeveloperResponse convertToResponse(MiniProgramDeveloper developer) {
        DeveloperResponse response = new DeveloperResponse();
        BeanUtils.copyProperties(developer, response);
        
        String[] statusDesc = {"未认证", "认证中", "已认证"};
        if (developer.getVerifyStatus() >= 0 && developer.getVerifyStatus() < statusDesc.length) {
            response.setVerifyStatusDesc(statusDesc[developer.getVerifyStatus()]);
        }
        
        for (DeveloperLevel level : DeveloperLevel.values()) {
            if (level.getLevel().equals(developer.getLevel())) {
                response.setLevelDesc(level.getDesc());
                break;
            }
        }
        
        return response;
    }
}
