package com.im.backend.modules.miniprogram.developer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.im.backend.modules.miniprogram.developer.dto.*;
import com.im.backend.modules.miniprogram.developer.entity.MiniProgramProject;
import com.im.backend.modules.miniprogram.developer.entity.MiniProgramTemplate;
import com.im.backend.modules.miniprogram.developer.enums.ProjectStatus;
import com.im.backend.modules.miniprogram.developer.repository.MiniProgramProjectMapper;
import com.im.backend.modules.miniprogram.developer.repository.MiniProgramTemplateMapper;
import com.im.backend.modules.miniprogram.developer.service.IProgramProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 小程序项目管理服务实现
 */
@Service
@RequiredArgsConstructor
public class ProgramProjectServiceImpl implements IProgramProjectService {
    
    private final MiniProgramProjectMapper projectMapper;
    private final MiniProgramTemplateMapper templateMapper;
    private final MiniProgramDeveloperMapper developerMapper;
    
    @Override
    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request, Long developerId) {
        MiniProgramProject project = new MiniProgramProject();
        project.setProjectKey(UUID.randomUUID().toString().replace("-", ""));
        project.setProjectName(request.getProjectName());
        project.setDescription(request.getDescription());
        project.setDeveloperId(developerId);
        project.setMerchantId(request.getMerchantId());
        project.setTemplateType(request.getTemplateType());
        project.setStatus(ProjectStatus.DRAFT.getCode());
        project.setVersion("1.0.0");
        project.setPageConfig(request.getPageConfig());
        project.setCreateTime(LocalDateTime.now());
        project.setUpdateTime(LocalDateTime.now());
        
        projectMapper.insert(project);
        developerMapper.incrementProgramCount(developerId);
        
        return convertToResponse(project);
    }
    
    @Override
    public ProjectResponse getProject(Long projectId) {
        MiniProgramProject project = projectMapper.selectById(projectId);
        return project != null ? convertToResponse(project) : null;
    }
    
    @Override
    public List<ProjectResponse> getDeveloperProjects(Long developerId) {
        List<MiniProgramProject> projects = projectMapper.selectByDeveloperId(developerId);
        return projects.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public ProjectResponse updateProject(Long projectId, CreateProjectRequest request) {
        MiniProgramProject project = projectMapper.selectById(projectId);
        if (project == null) return null;
        
        project.setProjectName(request.getProjectName());
        project.setDescription(request.getDescription());
        project.setPageConfig(request.getPageConfig());
        project.setUpdateTime(LocalDateTime.now());
        
        projectMapper.updateById(project);
        return convertToResponse(project);
    }
    
    @Override
    @Transactional
    public boolean publishProject(Long projectId) {
        return projectMapper.updateStatus(projectId, ProjectStatus.PUBLISHED.getCode()) > 0;
    }
    
    @Override
    @Transactional
    public ProjectResponse createProjectFromTemplate(Long templateId, Long developerId, Long merchantId) {
        MiniProgramTemplate template = templateMapper.selectById(templateId);
        if (template == null) return null;
        
        CreateProjectRequest request = new CreateProjectRequest();
        request.setProjectName(template.getTemplateName());
        request.setDescription(template.getDescription());
        request.setMerchantId(merchantId);
        request.setTemplateType(template.getIndustry());
        request.setPageConfig(template.getPages());
        
        ProjectResponse response = createProject(request, developerId);
        templateMapper.incrementUsageCount(templateId);
        
        return response;
    }
    
    private ProjectResponse convertToResponse(MiniProgramProject project) {
        ProjectResponse response = new ProjectResponse();
        BeanUtils.copyProperties(project, response);
        response.setStatusDesc(ProjectStatus.values()[project.getStatus()].getDesc());
        return response;
    }
}
