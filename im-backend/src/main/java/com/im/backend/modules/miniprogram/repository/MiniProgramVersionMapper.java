package com.im.backend.modules.miniprogram.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.miniprogram.entity.MiniProgramVersion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 小程序版本Mapper
 */
@Mapper
public interface MiniProgramVersionMapper extends BaseMapper<MiniProgramVersion> {

    @Select("SELECT * FROM mini_program_version WHERE app_id = #{appId} AND version = #{version}")
    MiniProgramVersion findByAppIdAndVersion(@Param("appId") Long appId, @Param("version") String version);

    @Select("SELECT * FROM mini_program_version WHERE app_id = #{appId} ORDER BY create_time DESC")
    List<MiniProgramVersion> findByAppId(@Param("appId") Long appId);

    @Select("SELECT * FROM mini_program_version WHERE app_id = #{appId} AND status = #{status}")
    List<MiniProgramVersion> findByAppIdAndStatus(@Param("appId") Long appId, @Param("status") String status);

    @Update("UPDATE mini_program_version SET status = #{status}, audit_result = #{result}, audit_time = NOW() WHERE id = #{versionId}")
    int updateAuditResult(@Param("versionId") Long versionId, @Param("status") String status, @Param("result") String result);

    @Update("UPDATE mini_program_version SET status = 'RELEASED', release_time = NOW() WHERE id = #{versionId}")
    int updateReleased(@Param("versionId") Long versionId);
}
