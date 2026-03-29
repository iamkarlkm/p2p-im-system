package com.im.backend.modules.miniprogram.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.miniprogram.entity.MiniProgramDeveloper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 小程序开发者Mapper
 */
@Mapper
public interface MiniProgramDeveloperMapper extends BaseMapper<MiniProgramDeveloper> {

    @Select("SELECT * FROM mini_program_developer WHERE user_id = #{userId}")
    MiniProgramDeveloper findByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM mini_program_developer WHERE identity_number = #{identityNumber}")
    MiniProgramDeveloper findByIdentityNumber(@Param("identityNumber") String identityNumber);

    @Update("UPDATE mini_program_developer SET app_count = app_count + 1 WHERE id = #{developerId}")
    int incrementAppCount(@Param("developerId") Long developerId);

    @Update("UPDATE mini_program_developer SET used_quota = used_quota + #{count} WHERE id = #{developerId}")
    int incrementUsedQuota(@Param("developerId") Long developerId, @Param("count") Integer count);
}
