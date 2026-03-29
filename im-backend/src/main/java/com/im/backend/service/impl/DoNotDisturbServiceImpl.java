package com.im.backend.service.impl;

import com.im.backend.dto.DoNotDisturbPeriodDTO;
import com.im.backend.model.DoNotDisturbPeriod;
import com.im.backend.repository.DoNotDisturbPeriodRepository;
import com.im.backend.service.DoNotDisturbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DoNotDisturbServiceImpl implements DoNotDisturbService {

    @Autowired
    private DoNotDisturbPeriodRepository periodRepository;

    @Override
    public List<DoNotDisturbPeriodDTO> getUserPeriods(String userId) {
        return periodRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DoNotDisturbPeriodDTO createPeriod(String userId, DoNotDisturbPeriodDTO dto) {
        DoNotDisturbPeriod period = new DoNotDisturbPeriod();
        period.setUserId(userId);
        period.setName(dto.getName());
        period.setStartHour(dto.getStartHour());
        period.setStartMinute(dto.getStartMinute());
        period.setEndHour(dto.getEndHour());
        period.setEndMinute(dto.getEndMinute());
        period.setActiveDays(dto.getActiveDays());
        period.setIsEnabled(dto.getIsEnabled() != null ? dto.getIsEnabled() : true);
        period.setAllowCalls(dto.getAllowCalls() != null ? dto.getAllowCalls() : false);
        period.setAllowMentions(dto.getAllowMentions() != null ? dto.getAllowMentions() : true);
        
        DoNotDisturbPeriod saved = periodRepository.save(period);
        return convertToDTO(saved);
    }

    @Override
    @Transactional
    public DoNotDisturbPeriodDTO updatePeriod(String userId, String periodId, DoNotDisturbPeriodDTO dto) {
        DoNotDisturbPeriod period = periodRepository.findById(periodId)
                .orElseThrow(() -> new RuntimeException("免打扰时段不存在"));
        
        if (!period.getUserId().equals(userId)) {
            throw new RuntimeException("无权修改此免打扰时段");
        }
        
        period.setName(dto.getName());
        period.setStartHour(dto.getStartHour());
        period.setStartMinute(dto.getStartMinute());
        period.setEndHour(dto.getEndHour());
        period.setEndMinute(dto.getEndMinute());
        period.setActiveDays(dto.getActiveDays());
        period.setAllowCalls(dto.getAllowCalls());
        period.setAllowMentions(dto.getAllowMentions());
        
        DoNotDisturbPeriod saved = periodRepository.save(period);
        return convertToDTO(saved);
    }

    @Override
    @Transactional
    public void deletePeriod(String userId, String periodId) {
        DoNotDisturbPeriod period = periodRepository.findById(periodId)
                .orElseThrow(() -> new RuntimeException("免打扰时段不存在"));
        
        if (!period.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此免打扰时段");
        }
        
        periodRepository.delete(period);
    }

    @Override
    @Transactional
    public DoNotDisturbPeriodDTO togglePeriod(String userId, String periodId, Boolean isEnabled) {
        DoNotDisturbPeriod period = periodRepository.findById(periodId)
                .orElseThrow(() -> new RuntimeException("免打扰时段不存在"));
        
        if (!period.getUserId().equals(userId)) {
            throw new RuntimeException("无权修改此免打扰时段");
        }
        
        period.setIsEnabled(isEnabled);
        DoNotDisturbPeriod saved = periodRepository.save(period);
        return convertToDTO(saved);
    }

    @Override
    public boolean isInDoNotDisturbMode(String userId) {
        List<DoNotDisturbPeriod> enabledPeriods = periodRepository.findByUserIdAndIsEnabledTrue(userId);
        return enabledPeriods.stream().anyMatch(DoNotDisturbPeriod::isCurrentlyActive);
    }

    @Override
    public boolean shouldAllowCalls(String userId) {
        List<DoNotDisturbPeriod> activePeriods = getActivePeriods(userId);
        if (activePeriods.isEmpty()) return true;
        return activePeriods.stream().allMatch(p -> p.getAllowCalls());
    }

    @Override
    public boolean shouldAllowMentions(String userId) {
        List<DoNotDisturbPeriod> activePeriods = getActivePeriods(userId);
        if (activePeriods.isEmpty()) return true;
        return activePeriods.stream().anyMatch(p -> p.getAllowMentions());
    }

    private List<DoNotDisturbPeriod> getActivePeriods(String userId) {
        return periodRepository.findByUserIdAndIsEnabledTrue(userId)
                .stream()
                .filter(DoNotDisturbPeriod::isCurrentlyActive)
                .collect(Collectors.toList());
    }

    private DoNotDisturbPeriodDTO convertToDTO(DoNotDisturbPeriod period) {
        DoNotDisturbPeriodDTO dto = new DoNotDisturbPeriodDTO();
        dto.setId(period.getId());
        dto.setUserId(period.getUserId());
        dto.setName(period.getName());
        dto.setStartHour(period.getStartHour());
        dto.setStartMinute(period.getStartMinute());
        dto.setEndHour(period.getEndHour());
        dto.setEndMinute(period.getEndMinute());
        dto.setActiveDays(period.getActiveDays());
        dto.setIsEnabled(period.getIsEnabled());
        dto.setAllowCalls(period.getAllowCalls());
        dto.setAllowMentions(period.getAllowMentions());
        dto.setCreatedAt(period.getCreatedAt());
        dto.setUpdatedAt(period.getUpdatedAt());
        dto.setIsCurrentlyActive(period.isCurrentlyActive());
        return dto;
    }
}
