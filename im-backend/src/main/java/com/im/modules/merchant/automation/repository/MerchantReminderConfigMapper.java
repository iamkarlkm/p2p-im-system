package com.im.modules.merchant.automation.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.modules.merchant.automation.entity.MerchantReminderConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 商户智能提醒配置数据访问层
 */
@Mapper
public interface MerchantReminderConfigMapper extends BaseMapper<MerchantReminderConfig> {
    
    /**
     * 根据商户ID查询配置
     */
    @Select("SELECT * FROM merchant_reminder_config WHERE merchant_id = #{merchantId} AND deleted = 0")
    MerchantReminderConfig findByMerchantId(@Param("merchantId") String merchantId);
    
    /**
     * 检查商户是否有配置
     */
    @Select("SELECT COUNT(*) FROM merchant_reminder_config WHERE merchant_id = #{merchantId} AND deleted = 0")
    int existsByMerchantId(@Param("merchantId") String merchantId);
}
