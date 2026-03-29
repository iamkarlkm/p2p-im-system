package com.im.backend.modules.miniprogram.developer.service;

import com.im.backend.modules.miniprogram.developer.dto.*;
import java.util.List;

/**
 * 可视化页面设计服务接口
 */
public interface IVisualDesignService {
    
    /**
     * 创建页面
     */
    PageResponse createPage(CreatePageRequest request);
    
    /**
     * 获取页面详情
     */
    PageResponse getPage(Long pageId);
    
    /**
     * 获取项目的所有页面
     */
    List<PageResponse> getProjectPages(Long projectId);
    
    /**
     * 更新页面配置
     */
    PageResponse updatePage(Long pageId, String layoutConfig, String components);
    
    /**
     * 删除页面
     */
    boolean deletePage(Long pageId);
    
    /**
     * 设置首页
     */
    boolean setHomePage(Long projectId, Long pageId);
}
