package com.im.backend.modules.miniprogram.developer.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.miniprogram.developer.entity.MiniProgramProject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 小程序项目数据访问层
 */
@Mapper
public interface MiniProgramProjectMapper extends BaseMapper<MiniProgramProject> {
    
    /**
     * 根据项目key查询
     */
    @Select("SELECT * FROM mini_program_project WHERE project_key = #{projectKey}")
    MiniProgramProject selectByProjectKey(@Param("projectKey") String projectKey);
    
    /**
     * 查询开发者的项目列表
     */
    @Select("SELECT * FROM mini_program_project WHERE developer_id = #{developerId} ORDER BY create_time DESC")
    List<MiniProgramProject> selectByDeveloperId(@Param("developerId") Long developerId);
    
    /**
     * 查询商户的小程序
     */
    @Select("SELECT * FROM mini_program_project WHERE merchant_id = #{merchantId} AND status = 3 LIMIT 1")
    MiniProgramProject selectByMerchantId(@Param("merchantId") Long merchantId);
    
    /**
     * 更新项目状态
     */
    @Update("UPDATE mini_program_project SET status = #{status}, update_time = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    /**
     * 更新版本号
     */
    @Update("UPDATE mini_program_project SET version = #{version}, update_time = NOW() WHERE id = #{id}")
    int updateVersion(@Param("id") Long id, @Param("version") String version);
}
