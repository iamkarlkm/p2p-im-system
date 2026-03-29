package com.im.backend.modules.miniprogram.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.im.backend.modules.miniprogram.entity.MiniProgramPage;
import com.im.backend.modules.miniprogram.dto.*;

import java.util.List;

/**
 * 小程序页面服务接口
 */
public interface MiniProgramPageService extends IService<MiniProgramPage> {

    /**
     * 创建页面
     */
    PageResponse createPage(CreatePageRequest request);

    /**
     * 更新页面
     */
    PageResponse updatePage(Long pageId, CreatePageRequest request);

    /**
     * 获取页面详情
     */
    PageResponse getPageDetail(Long pageId);

    /**
     * 获取项目页面列表
     */
    List<PageResponse> getProjectPages(Long projectId);

    /**
     * 设置首页
     */
    boolean setHomePage(Long pageId);

    /**
     * 更新组件树
     */
    boolean updateComponentTree(Long pageId, Object componentTree);

    /**
     * 更新页面样式
     */
    boolean updatePageStyle(Long pageId, Object pageStyle);

    /**
     * 排序页面
     */
    boolean sortPages(List<Long> pageIds);

    /**
     * 删除页面
     */
    boolean deletePage(Long pageId);

    /**
     * 复制页面
     */
    PageResponse copyPage(Long pageId, String newPageName);
}
