package com.im.backend.modules.miniprogram.developer.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.miniprogram.developer.entity.MiniProgramPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 小程序页面数据访问层
 */
@Mapper
public interface MiniProgramPageMapper extends BaseMapper<MiniProgramPage> {
    
    /**
     * 根据项目ID查询页面列表
     */
    @Select("SELECT * FROM mini_program_page WHERE project_id = #{projectId} ORDER BY sort_order")
    List<MiniProgramPage> selectByProjectId(@Param("projectId") Long projectId);
    
    /**
     * 根据页面key查询
     */
    @Select("SELECT * FROM mini_program_page WHERE page_key = #{pageKey}")
    MiniProgramPage selectByPageKey(@Param("pageKey") String pageKey);
    
    /**
     * 查询项目首页
     */
    @Select("SELECT * FROM mini_program_page WHERE project_id = #{projectId} AND is_home = 1 LIMIT 1")
    MiniProgramPage selectHomePage(@Param("projectId") Long projectId);
    
    /**
     * 更新页面配置
     */
    @Update("UPDATE mini_program_page SET layout_config = #{layoutConfig}, components = #{components}, " +
            "update_time = NOW() WHERE id = #{id}")
    int updatePageConfig(@Param("id") Long id, @Param("layoutConfig") String layoutConfig, 
                         @Param("components") String components);
}
