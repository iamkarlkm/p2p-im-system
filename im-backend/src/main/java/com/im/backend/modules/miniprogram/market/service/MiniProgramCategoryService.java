package com.im.backend.modules.miniprogram.market.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.im.backend.modules.miniprogram.market.dto.CategoryResponse;
import com.im.backend.modules.miniprogram.market.entity.MiniProgramCategory;

import java.util.List;

/**
 * 小程序分类服务接口
 */
public interface MiniProgramCategoryService extends IService<MiniProgramCategory> {

    /**
     * 获取所有分类（树形结构）
     */
    List<CategoryResponse> getCategoryTree();

    /**
     * 获取顶级分类
     */
    List<CategoryResponse> getTopCategories();

    /**
     * 获取子分类
     */
    List<CategoryResponse> getSubCategories(String parentCode);

    /**
     * 根据场景类型获取分类
     */
    List<CategoryResponse> getCategoriesByScene(Integer sceneType);

    /**
     * 获取分类详情
     */
    CategoryResponse getCategoryDetail(String categoryCode);

    /**
     * 初始化分类数据
     */
    void initCategories();
}
