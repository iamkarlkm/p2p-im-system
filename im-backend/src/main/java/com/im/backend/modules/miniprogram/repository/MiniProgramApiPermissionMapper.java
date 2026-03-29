package com.im.backend.modules.miniprogram.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.miniprogram.entity.MiniProgramApiPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * API权限Mapper
 */
@Mapper
public interface MiniProgramApiPermissionMapper extends BaseMapper<MiniProgramApiPermission> {

    @Select("SELECT * FROM mini_program_api_permission WHERE app_id = #{appId} AND api_code = #{apiCode}")
    MiniProgramApiPermission findByAppIdAndApiCode(@Param("appId") Long appId, @Param("apiCode") String apiCode);

    @Select("SELECT * FROM mini_program_api_permission WHERE app_id = #{appId} ORDER BY create_time DESC")
    List<MiniProgramApiPermission> findByAppId(@Param("appId") Long appId);

    @Select("SELECT * FROM mini_program_api_permission WHERE app_id = #{appId} AND status = 'APPROVED'")
    List<MiniProgramApiPermission> findApprovedByAppId(@Param("appId") Long appId);

    @Select("SELECT api_code FROM mini_program_api_permission WHERE app_id = #{appId} AND status = 'APPROVED'")
    List<String> findApprovedApiCodesByAppId(@Param("appId") Long appId);
}
