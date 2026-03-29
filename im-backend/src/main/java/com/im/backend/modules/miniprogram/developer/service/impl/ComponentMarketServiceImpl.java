package com.im.backend.modules.miniprogram.developer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.im.backend.modules.miniprogram.developer.dto.*;
import com.im.backend.modules.miniprogram.developer.entity.ComponentMarket;
import com.im.backend.modules.miniprogram.developer.enums.ComponentCategory;
import com.im.backend.modules.miniprogram.developer.repository.ComponentMarketMapper;
import com.im.backend.modules.miniprogram.developer.service.IComponentMarketService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 组件市场服务实现
 */
@Service
@RequiredArgsConstructor
public class ComponentMarketServiceImpl implements IComponentMarketService {
    
    private final ComponentMarketMapper componentMapper;
    
    @Override
    @Transactional
    public ComponentResponse publishComponent(PublishComponentRequest request, Long developerId) {
        ComponentMarket component = new ComponentMarket();
        component.setComponentKey(UUID.randomUUID().toString().replace("-", ""));
        component.setComponentName(request.getComponentName());
        component.setDescription(request.getDescription());
        component.setCategory(request.getCategory());
        component.setIcon(request.getIcon());
        component.setPreviewImages(request.getPreviewImages());
        component.setCodePackage(request.getCodePackage());
        component.setDefaultConfig(request.getDefaultConfig());
        component.setPropsConfig(request.getPropsConfig());
        component.setEventConfig(request.getEventConfig());
        component.setDeveloperId(developerId);
        component.setVersion("1.0.0");
        component.setPrice(request.getPrice() != null ? request.getPrice() : BigDecimal.ZERO);
        component.setDownloadCount(0);
        component.setRating(BigDecimal.ZERO);
        component.setRatingCount(0);
        component.setStatus(0);
        component.setCreateTime(LocalDateTime.now());
        component.setUpdateTime(LocalDateTime.now());
        
        componentMapper.insert(component);
        return convertToResponse(component);
    }
    
    @Override
    public ComponentResponse getComponent(Long componentId) {
        ComponentMarket component = componentMapper.selectById(componentId);
        return component != null ? convertToResponse(component) : null;
    }
    
    @Override
    public List<ComponentResponse> getComponentsByCategory(String category) {
        List<ComponentMarket> components = componentMapper.selectByCategory(category);
        return components.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    @Override
    public List<ComponentResponse> getHotComponents(Integer limit) {
        List<ComponentMarket> components = componentMapper.selectHotComponents(limit);
        return components.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    @Override
    public List<ComponentResponse> searchComponents(String keyword) {
        QueryWrapper<ComponentMarket> wrapper = new QueryWrapper<>();
        wrapper.like("component_name", keyword).or().like("description", keyword);
        List<ComponentMarket> components = componentMapper.selectList(wrapper);
        return components.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public boolean downloadComponent(Long componentId) {
        return componentMapper.incrementDownloadCount(componentId) > 0;
    }
    
    @Override
    @Transactional
    public boolean rateComponent(Long componentId, Integer rating) {
        BigDecimal ratingBd = new BigDecimal(rating);
        return componentMapper.updateRating(componentId, ratingBd) > 0;
    }
    
    private ComponentResponse convertToResponse(ComponentMarket component) {
        ComponentResponse response = new ComponentResponse();
        BeanUtils.copyProperties(component, response);
        try {
            response.setCategoryDesc(ComponentCategory.valueOf(component.getCategory().toUpperCase()).getDesc());
        } catch (Exception e) {
            response.setCategoryDesc(component.getCategory());
        }
        return response;
    }
}
