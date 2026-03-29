package com.im.backend.modules.miniprogram.developer.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.miniprogram.developer.entity.MiniProgramTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 小程序模板数据访问层
 */
@Mapper
public interface MiniProgramTemplateMapper extends BaseMapper<MiniProgramTemplate> {
    
    /**
     * 根据模板key查询
     */
    @Select("SELECT * FROM mini_program_template WHERE template_key = #{templateKey}")
    MiniProgramTemplate selectByTemplateKey(@Param("templateKey") String templateKey);
    
    /**
     * 根据行业查询模板
     */
    @Select("SELECT * FROM mini_program_template WHERE industry = #{industry} AND status = 1 ORDER BY sort_order")
    List<MiniProgramTemplate> selectByIndustry(@Param("industry") String industry);
    
    /**
     * 查询推荐模板
     */
    @Select("SELECT * FROM mini_program_template WHERE is_recommended = 1 AND status = 1 ORDER BY sort_order")
    List<MiniProgramTemplate> selectRecommended();
    
    /**
     * 增加使用次数
     */
    @Update("UPDATE mini_program_template SET usage_count = usage_count + 1 WHERE id = #{id}")
    int incrementUsageCount(@Param("id") Long id);
}
