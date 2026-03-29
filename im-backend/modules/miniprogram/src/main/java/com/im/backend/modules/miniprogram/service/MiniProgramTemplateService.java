package com.im.backend.modules.miniprogram.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.im.backend.modules.miniprogram.entity.MiniProgramTemplate;
import com.im.backend.modules.miniprogram.dto.*;

import java.util.List;

/**
 * 模板市场服务接口
 */
public interface MiniProgramTemplateService extends IService<MiniProgramTemplate> {

    /**
     * 获取模板列表
     */
    List<TemplateResponse> getTemplateList(Integer category, int page, int size);

    /**
     * 获取模板详情
     */
    TemplateResponse getTemplateDetail(Long templateId);

    /**
     * 获取热门模板
     */
    List<TemplateResponse> getHotTemplates(int limit);

    /**
     * 获取官方模板
     */
    List<TemplateResponse> getOfficialTemplates();

    /**
     * 搜索模板
     */
    List<TemplateResponse> searchTemplates(String keyword, Integer category, int page, int size);

    /**
     * 使用模板
     */
    boolean useTemplate(Long templateId, Long userId);

    /**
     * 获取推荐模板
     */
    List<TemplateResponse> getRecommendTemplates(Long userId, int limit);
}
