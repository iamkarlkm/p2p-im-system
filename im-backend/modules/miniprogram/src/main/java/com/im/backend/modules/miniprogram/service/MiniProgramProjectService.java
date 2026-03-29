package com.im.backend.modules.miniprogram.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.im.backend.modules.miniprogram.entity.MiniProgramProject;
import com.im.backend.modules.miniprogram.dto.*;

import java.util.List;

/**
 * 小程序项目服务接口
 */
public interface MiniProgramProjectService extends IService<MiniProgramProject> {

    /**
     * 创建项目
     */
    ProjectResponse createProject(CreateProjectRequest request, Long developerId);

    /**
     * 基于模板创建项目
     */
    ProjectResponse createProjectFromTemplate(Long templateId, CreateProjectRequest request, Long developerId);

    /**
     * 更新项目
     */
    ProjectResponse updateProject(Long projectId, CreateProjectRequest request);

    /**
     * 获取项目详情
     */
    ProjectResponse getProjectDetail(Long projectId);

    /**
     * 获取项目详情ByKey
     */
    ProjectResponse getProjectByKey(String projectKey);

    /**
     * 获取开发者项目列表
     */
    List<ProjectResponse> getDeveloperProjects(Long developerId, Integer status);

    /**
     * 发布项目
     */
    boolean publishProject(Long projectId);

    /**
     * 下架项目
     */
    boolean offlineProject(Long projectId);

    /**
     * 删除项目
     */
    boolean deleteProject(Long projectId);

    /**
     * 生成预览二维码
     */
    String generatePreviewQrCode(Long projectId);

    /**
     * 获取项目统计
     */
    ProjectStatistics getProjectStatistics(Long projectId);
}
