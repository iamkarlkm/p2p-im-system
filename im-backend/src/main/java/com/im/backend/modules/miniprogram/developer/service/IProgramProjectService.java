package com.im.backend.modules.miniprogram.developer.service;

import com.im.backend.modules.miniprogram.developer.dto.*;
import java.util.List;

/**
 * 小程序项目管理服务接口
 */
public interface IProgramProjectService {
    
    /**
     * 创建小程序项目
     */
    ProjectResponse createProject(CreateProjectRequest request, Long developerId);
    
    /**
     * 获取项目详情
     */
    ProjectResponse getProject(Long projectId);
    
    /**
     * 获取开发者的项目列表
     */
    List<ProjectResponse> getDeveloperProjects(Long developerId);
    
    /**
     * 更新项目
     */
    ProjectResponse updateProject(Long projectId, CreateProjectRequest request);
    
    /**
     * 发布项目
     */
    boolean publishProject(Long projectId);
    
    /**
     * 使用模板创建项目
     */
    ProjectResponse createProjectFromTemplate(Long templateId, Long developerId, Long merchantId);
}
