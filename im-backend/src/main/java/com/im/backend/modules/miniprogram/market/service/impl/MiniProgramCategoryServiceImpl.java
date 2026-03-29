package com.im.backend.modules.miniprogram.market.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.im.backend.modules.miniprogram.market.dto.CategoryResponse;
import com.im.backend.modules.miniprogram.market.entity.MiniProgramCategory;
import com.im.backend.modules.miniprogram.market.enums.SceneType;
import com.im.backend.modules.miniprogram.market.mapper.MiniProgramCategoryMapper;
import com.im.backend.modules.miniprogram.market.service.MiniProgramCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 小程序分类服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MiniProgramCategoryServiceImpl extends ServiceImpl<MiniProgramCategoryMapper, MiniProgramCategory> implements MiniProgramCategoryService {

    @Override
    public List<CategoryResponse> getCategoryTree() {
        List<MiniProgramCategory> allCategories = list();
        
        Map<String, List<MiniProgramCategory>> parentMap = allCategories.stream()
            .collect(Collectors.groupingBy(MiniProgramCategory::getParentCode));
        
        List<MiniProgramCategory> topCategories = parentMap.getOrDefault("0", List.of());
        
        return topCategories.stream()
            .map(cat -> buildCategoryTree(cat, parentMap))
            .collect(Collectors.toList());
    }

    @Override
    public List<CategoryResponse> getTopCategories() {
        List<MiniProgramCategory> categories = lambdaQuery()
            .eq(MiniProgramCategory::getParentCode, "0")
            .eq(MiniProgramCategory::getStatus, 1)
            .orderByAsc(MiniProgramCategory::getSortWeight)
            .list();
        
        return categories.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<CategoryResponse> getSubCategories(String parentCode) {
        List<MiniProgramCategory> categories = lambdaQuery()
            .eq(MiniProgramCategory::getParentCode, parentCode)
            .eq(MiniProgramCategory::getStatus, 1)
            .orderByAsc(MiniProgramCategory::getSortWeight)
            .list();
        
        return categories.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<CategoryResponse> getCategoriesByScene(Integer sceneType) {
        List<MiniProgramCategory> categories = lambdaQuery()
            .eq(MiniProgramCategory::getSceneType, sceneType)
            .eq(MiniProgramCategory::getStatus, 1)
            .orderByAsc(MiniProgramCategory::getSortWeight)
            .list();
        
        return categories.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public CategoryResponse getCategoryDetail(String categoryCode) {
        MiniProgramCategory category = lambdaQuery()
            .eq(MiniProgramCategory::getCategoryCode, categoryCode)
            .one();
        
        if (category == null) {
            return null;
        }
        
        return convertToResponse(category);
    }

    @Override
    public void initCategories() {
        // 初始化本地生活场景分类数据
        if (count() > 0) {
            return;
        }
        
        List<MiniProgramCategory> categories = new ArrayList<>();
        
        // 餐饮美食
        categories.add(createCategory("catering", "餐饮美食", "0", 1, SceneType.CATERING.getCode(), 1));
        categories.add(createCategory("catering_1", "外卖订餐", "catering", 2, SceneType.CATERING.getCode(), 1));
        categories.add(createCategory("catering_2", "餐厅预订", "catering", 2, SceneType.CATERING.getCode(), 2));
        categories.add(createCategory("catering_3", "排队叫号", "catering", 2, SceneType.CATERING.getCode(), 3));
        categories.add(createCategory("catering_4", "扫码点餐", "catering", 2, SceneType.CATERING.getCode(), 4));
        
        // 生活服务
        categories.add(createCategory("lifestyle", "生活服务", "0", 1, SceneType.LIFESTYLE.getCode(), 2));
        categories.add(createCategory("lifestyle_1", "家政保洁", "lifestyle", 2, SceneType.LIFESTYLE.getCode(), 1));
        categories.add(createCategory("lifestyle_2", "维修服务", "lifestyle", 2, SceneType.LIFESTYLE.getCode(), 2));
        categories.add(createCategory("lifestyle_3", "洗衣服务", "lifestyle", 2, SceneType.LIFESTYLE.getCode(), 3));
        categories.add(createCategory("lifestyle_4", "美容美发", "lifestyle", 2, SceneType.LIFESTYLE.getCode(), 4));
        
        // 出行旅游
        categories.add(createCategory("travel", "出行旅游", "0", 1, SceneType.TRAVEL.getCode(), 3));
        categories.add(createCategory("travel_1", "打车租车", "travel", 2, SceneType.TRAVEL.getCode(), 1));
        categories.add(createCategory("travel_2", "酒店预订", "travel", 2, SceneType.TRAVEL.getCode(), 2));
        categories.add(createCategory("travel_3", "景点门票", "travel", 2, SceneType.TRAVEL.getCode(), 3));
        categories.add(createCategory("travel_4", "旅游攻略", "travel", 2, SceneType.TRAVEL.getCode(), 4));
        
        // 购物零售
        categories.add(createCategory("shopping", "购物零售", "0", 1, SceneType.SHOPPING.getCode(), 4));
        categories.add(createCategory("shopping_1", "商超便利", "shopping", 2, SceneType.SHOPPING.getCode(), 1));
        categories.add(createCategory("shopping_2", "生鲜果蔬", "shopping", 2, SceneType.SHOPPING.getCode(), 2));
        categories.add(createCategory("shopping_3", "母婴用品", "shopping", 2, SceneType.SHOPPING.getCode(), 3));
        categories.add(createCategory("shopping_4", "数码电器", "shopping", 2, SceneType.SHOPPING.getCode(), 4));
        
        // 健康医疗
        categories.add(createCategory("health", "健康医疗", "0", 1, SceneType.HEALTH.getCode(), 5));
        categories.add(createCategory("health_1", "医院挂号", "health", 2, SceneType.HEALTH.getCode(), 1));
        categories.add(createCategory("health_2", "在线问诊", "health", 2, SceneType.HEALTH.getCode(), 2));
        categories.add(createCategory("health_3", "药品购买", "health", 2, SceneType.HEALTH.getCode(), 3));
        categories.add(createCategory("health_4", "体检预约", "health", 2, SceneType.HEALTH.getCode(), 4));
        
        // 教育培训
        categories.add(createCategory("education", "教育培训", "0", 1, SceneType.EDUCATION.getCode(), 6));
        categories.add(createCategory("education_1", "课程培训", "education", 2, SceneType.EDUCATION.getCode(), 1));
        categories.add(createCategory("education_2", "考试题库", "education", 2, SceneType.EDUCATION.getCode(), 2));
        categories.add(createCategory("education_3", "在线学习", "education", 2, SceneType.EDUCATION.getCode(), 3));
        categories.add(createCategory("education_4", "留学服务", "education", 2, SceneType.EDUCATION.getCode(), 4));
        
        saveBatch(categories);
        log.info("Initialized {} mini program categories", categories.size());
    }

    private MiniProgramCategory createCategory(String code, String name, String parentCode, 
                                                Integer level, Integer sceneType, Integer sortWeight) {
        MiniProgramCategory category = new MiniProgramCategory();
        category.setCategoryCode(code);
        category.setCategoryName(name);
        category.setParentCode(parentCode);
        category.setLevel(level);
        category.setSceneType(sceneType);
        category.setSortWeight(sortWeight);
        category.setStatus(1);
        category.setAppCount(0);
        return category;
    }

    private CategoryResponse buildCategoryTree(MiniProgramCategory category, 
                                                Map<String, List<MiniProgramCategory>> parentMap) {
        CategoryResponse response = convertToResponse(category);
        
        List<MiniProgramCategory> children = parentMap.getOrDefault(category.getCategoryCode(), List.of());
        if (!children.isEmpty()) {
            // 设置子分类
        }
        
        return response;
    }

    private CategoryResponse convertToResponse(MiniProgramCategory category) {
        CategoryResponse response = new CategoryResponse();
        BeanUtils.copyProperties(category, response);
        return response;
    }
}
