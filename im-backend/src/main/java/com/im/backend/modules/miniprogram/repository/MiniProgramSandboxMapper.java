package com.im.backend.modules.miniprogram.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.miniprogram.entity.MiniProgramSandbox;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 沙箱环境Mapper
 */
@Mapper
public interface MiniProgramSandboxMapper extends BaseMapper<MiniProgramSandbox> {

    @Select("SELECT * FROM mini_program_sandbox WHERE sandbox_id = #{sandboxId}")
    MiniProgramSandbox findBySandboxId(@Param("sandboxId") String sandboxId);

    @Select("SELECT * FROM mini_program_sandbox WHERE app_id = #{appId} AND status = 'RUNNING'")
    List<MiniProgramSandbox> findRunningByAppId(@Param("appId") Long appId);

    @Select("SELECT COUNT(*) FROM mini_program_sandbox WHERE app_id = #{appId} AND status = 'RUNNING'")
    int countRunningByAppId(@Param("appId") Long appId);

    @Update("UPDATE mini_program_sandbox SET status = 'EXPIRED' WHERE expire_time < NOW() AND status = 'RUNNING'")
    int updateExpiredSandboxes();

    @Update("UPDATE mini_program_sandbox SET status = #{status}, performance_data = #{performanceData} WHERE id = #{sandboxId}")
    int updateStatusAndPerformance(@Param("sandboxId") Long sandboxId, @Param("status") String status, @Param("performanceData") String performanceData);
}
