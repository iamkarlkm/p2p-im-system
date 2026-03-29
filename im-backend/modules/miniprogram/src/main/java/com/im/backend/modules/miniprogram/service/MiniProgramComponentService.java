package com.im.backend.modules.miniprogram.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.im.backend.modules.miniprogram.entity.MiniProgramComponent;
import com.im.backend.modules.miniprogram.dto.*;

import java.util.List;

/**
 * 组件市场服务接口
 */
public interface MiniProgramComponentService extends IService<MiniProgramComponent> {

    /**
     * 发布组件
     */
    ComponentResponse publishComponent(CreateComponentRequest request, Long authorId);

    /**
     * 更新组件
     */
    ComponentResponse updateComponent(Long componentId, CreateComponentRequest request);

    /**
     * 获取组件详情
     */
    ComponentResponse getComponentDetail(Long componentId);

    /**
     * 获取组件列表
     */
    List<ComponentResponse> getComponentList(Integer category, String keyword, int page, int size);

    /**
     * 获取热门组件
     */
    List<ComponentResponse> getHotComponents(int limit);

    /**
     * 获取开发者组件
     */
    List<ComponentResponse> getDeveloperComponents(Long developerId);

    /**
     * 下载组件
     */
    boolean downloadComponent(Long componentId, Long userId);

    /**
     * 评分组件
     */
    boolean rateComponent(Long componentId, Long userId, Integer rating);

    /**
     * 搜索组件
     */
    List<ComponentResponse> searchComponents(String keyword, Integer category, int page, int size);

    /**
     * 下架组件
     */
    boolean offlineComponent(Long componentId);
}
