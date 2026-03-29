package com.im.backend.modules.miniprogram.developer.service;

import com.im.backend.modules.miniprogram.developer.dto.*;
import java.util.List;

/**
 * 组件市场服务接口
 */
public interface IComponentMarketService {
    
    /**
     * 发布组件
     */
    ComponentResponse publishComponent(PublishComponentRequest request, Long developerId);
    
    /**
     * 获取组件详情
     */
    ComponentResponse getComponent(Long componentId);
    
    /**
     * 根据分类获取组件列表
     */
    List<ComponentResponse> getComponentsByCategory(String category);
    
    /**
     * 获取热门组件
     */
    List<ComponentResponse> getHotComponents(Integer limit);
    
    /**
     * 搜索组件
     */
    List<ComponentResponse> searchComponents(String keyword);
    
    /**
     * 下载组件
     */
    boolean downloadComponent(Long componentId);
    
    /**
     * 评价组件
     */
    boolean rateComponent(Long componentId, Integer rating);
}
