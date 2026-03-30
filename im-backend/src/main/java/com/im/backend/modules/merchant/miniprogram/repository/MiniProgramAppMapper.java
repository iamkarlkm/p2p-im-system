package com.im.backend.modules.merchant.miniprogram.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.im.backend.modules.merchant.miniprogram.entity.MiniProgramApp;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 小程序应用数据访问层 - 功能#313: 小程序开发者生态
 */
@Repository
public interface MiniProgramAppMapper extends BaseMapper<MiniProgramApp> {

    /**
     * 查询商户的小程序列表
     */
    IPage<MiniProgramApp> selectByMerchantId(Page<MiniProgramApp> page, @Param("merchantId") Long merchantId);

    /**
     * 查询已发布的小程序
     */
    @Select("SELECT * FROM mini_program_app WHERE status = 2 AND deleted = 0 ORDER BY use_count DESC LIMIT #{limit}")
    List<MiniProgramApp> selectPublished(@Param("limit") Integer limit);

    /**
     * 根据appId查询
     */
    @Select("SELECT * FROM mini_program_app WHERE app_id = #{appId} AND deleted = 0 LIMIT 1")
    MiniProgramApp selectByAppId(@Param("appId") String appId);
}
