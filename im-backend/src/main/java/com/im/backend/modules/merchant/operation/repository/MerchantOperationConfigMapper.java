package com.im.backend.modules.merchant.operation.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.merchant.operation.entity.MerchantOperationConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 商户运营配置Mapper
 * Feature #307: Local Merchant Smart Operation Assistant
 * 
 * @author IM Development Team
 * @since 2026-03-30
 */
@Mapper
public interface MerchantOperationConfigMapper extends BaseMapper<MerchantOperationConfig> {

    /**
     * 根据商户ID查询配置
     * 
     * @param merchantId 商户ID
     * @return 配置信息
     */
    @Select("SELECT * FROM merchant_operation_config WHERE merchant_id = #{merchantId} AND deleted = 0 LIMIT 1")
    MerchantOperationConfig selectByMerchantId(@Param("merchantId") Long merchantId);

    /**
     * 查询开启智能提醒的商户配置
     * 
     * @return 配置列表
     */
    @Select("SELECT * FROM merchant_operation_config WHERE smart_reminder_enabled = 1 AND status = 1 AND deleted = 0")
    List<MerchantOperationConfig> selectEnabledSmartReminderConfigs();

    /**
     * 查询开启自动营销的商户配置
     * 
     * @return 配置列表
     */
    @Select("SELECT * FROM merchant_operation_config WHERE auto_marketing_enabled = 1 AND status = 1 AND deleted = 0")
    List<MerchantOperationConfig> selectEnabledAutoMarketingConfigs();

    /**
     * 更新配置状态
     * 
     * @param id     配置ID
     * @param status 状态
     * @return 影响行数
     */
    @Update("UPDATE merchant_operation_config SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 逻辑删除
     * 
     * @param id 配置ID
     * @return 影响行数
     */
    @Update("UPDATE merchant_operation_config SET deleted = 1 WHERE id = #{id}")
    int logicDelete(@Param("id") Long id);
}
