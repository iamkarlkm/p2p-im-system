package com.im.backend.modules.miniprogram.developer.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.miniprogram.developer.entity.MiniProgramDeveloper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 开发者信息数据访问层
 */
@Mapper
public interface MiniProgramDeveloperMapper extends BaseMapper<MiniProgramDeveloper> {
    
    /**
     * 根据用户ID查询开发者
     */
    @Select("SELECT * FROM mini_program_developer WHERE user_id = #{userId}")
    MiniProgramDeveloper selectByUserId(@Param("userId") Long userId);
    
    /**
     * 更新积分
     */
    @Update("UPDATE mini_program_developer SET points = points + #{points}, update_time = NOW() WHERE id = #{id}")
    int addPoints(@Param("id") Long id, @Param("points") Integer points);
    
    /**
     * 更新收益
     */
    @Update("UPDATE mini_program_developer SET balance = balance + #{amount}, " +
            "total_earnings = total_earnings + #{amount}, update_time = NOW() WHERE id = #{id}")
    int addEarnings(@Param("id") Long id, @Param("amount") java.math.BigDecimal amount);
    
    /**
     * 更新组件计数
     */
    @Update("UPDATE mini_program_developer SET component_count = component_count + 1, update_time = NOW() WHERE id = #{id}")
    int incrementComponentCount(@Param("id") Long id);
    
    /**
     * 更新小程序计数
     */
    @Update("UPDATE mini_program_developer SET program_count = program_count + 1, update_time = NOW() WHERE id = #{id}")
    int incrementProgramCount(@Param("id") Long id);
}
