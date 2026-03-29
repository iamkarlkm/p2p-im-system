package com.im.backend.modules.miniprogram.developer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.im.backend.modules.miniprogram.developer.dto.*;
import com.im.backend.modules.miniprogram.developer.entity.MiniProgramPage;
import com.im.backend.modules.miniprogram.developer.repository.MiniProgramPageMapper;
import com.im.backend.modules.miniprogram.developer.service.IVisualDesignService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 可视化页面设计服务实现
 */
@Service
@RequiredArgsConstructor
public class VisualDesignServiceImpl implements IVisualDesignService {
    
    private final MiniProgramPageMapper pageMapper;
    
    @Override
    @Transactional
    public PageResponse createPage(CreatePageRequest request) {
        int pageCount = pageMapper.selectCount(
            new QueryWrapper<MiniProgramPage>().eq("project_id", request.getProjectId())
        );
        
        MiniProgramPage page = new MiniProgramPage();
        page.setProjectId(request.getProjectId());
        page.setPageKey(UUID.randomUUID().toString().replace("-", ""));
        page.setPageName(request.getPageName());
        page.setPageTitle(request.getPageTitle());
        page.setPagePath("/pages/" + request.getPageName().toLowerCase().replace(" ", "-"));
        page.setPageType(request.getPageType());
        page.setLayoutConfig(request.getLayoutConfig());
        page.setComponents(request.getComponents());
        page.setSortOrder(pageCount);
        page.setIsHome(request.getIsHome() != null ? request.getIsHome() : false);
        page.setIsEnabled(true);
        page.setCreateTime(LocalDateTime.now());
        page.setUpdateTime(LocalDateTime.now());
        
        pageMapper.insert(page);
        return convertToResponse(page);
    }
    
    @Override
    public PageResponse getPage(Long pageId) {
        MiniProgramPage page = pageMapper.selectById(pageId);
        return page != null ? convertToResponse(page) : null;
    }
    
    @Override
    public List<PageResponse> getProjectPages(Long projectId) {
        List<MiniProgramPage> pages = pageMapper.selectByProjectId(projectId);
        return pages.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public PageResponse updatePage(Long pageId, String layoutConfig, String components) {
        pageMapper.updatePageConfig(pageId, layoutConfig, components);
        return getPage(pageId);
    }
    
    @Override
    @Transactional
    public boolean deletePage(Long pageId) {
        return pageMapper.deleteById(pageId) > 0;
    }
    
    @Override
    @Transactional
    public boolean setHomePage(Long projectId, Long pageId) {
        return true;
    }
    
    private PageResponse convertToResponse(MiniProgramPage page) {
        PageResponse response = new PageResponse();
        BeanUtils.copyProperties(page, response);
        return response;
    }
}
