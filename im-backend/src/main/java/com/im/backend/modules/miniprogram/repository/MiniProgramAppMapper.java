package com.im.backend.modules.miniprogram.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.miniprogram.entity.MiniProgramApp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 小程序应用Mapper
 */
@Mapper
public interface MiniProgramAppMapper extends BaseMapper<MiniProgramApp> {

    @Select("SELECT * FROM mini_program_app WHERE app_id = #{appId}")
    MiniProgramApp findByAppId(@Param("appId") String appId);

    @Select("SELECT * FROM mini_program_app WHERE developer_id = #{developerId} ORDER BY create_time DESC")
    List<MiniProgramApp> findByDeveloperId(@Param("developerId") Long developerId);

    @Select("SELECT * FROM mini_program_app WHERE developer_id = #{developerId} AND status = #{status}")
    List<MiniProgramApp> findByDeveloperIdAndStatus(@Param("developerId") Long developerId, @Param("status") String status);

    @Update("UPDATE mini_program_app SET current_version = #{version}, current_version_id = #{versionId}, status = 'RELEASED' WHERE id = #{appId}")
    int updateCurrentVersion(@Param("appId") Long appId, @Param("version") String version, @Param("versionId") Long versionId);

    @Update("UPDATE mini_program_app SET gray_release_percent = #{percent} WHERE id = #{appId}")
    int updateGrayReleasePercent(@Param("appId") Long appId, @Param("percent") Integer percent);
}
