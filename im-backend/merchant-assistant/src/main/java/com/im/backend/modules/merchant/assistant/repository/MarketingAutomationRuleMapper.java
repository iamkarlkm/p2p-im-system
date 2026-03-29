package com.im.backend.modules.merchant.assistant.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.merchant.assistant.entity.MarketingAutomationRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 营销自动化规则Mapper
 */
@Mapper
public interface MarketingAutomationRuleMapper extends BaseMapper<MarketingAutomationRule> {
    
    /**
     * 查询商户启用的规则列表
     */
    @Select("SELECT * FROM marketing_automation_rule WHERE merchant_id = #{merchantId} AND enabled = 1")
    List<MarketingAutomationRule> selectEnabledRules(@Param("merchantId") Long merchantId);
    
    /**
     * 查询指定类型的规则
     */
    @Select("SELECT * FROM marketing_automation_rule WHERE merchant_id = #{merchantId} AND rule_type = #{ruleType} AND enabled = 1")
    List<MarketingAutomationRule> selectByRuleType(@Param("merchantId") Long merchantId, @Param("ruleType") String ruleType);
    
    /**
     * 增加触发次数
     */
    @Update("UPDATE marketing_automation_rule SET trigger_count = trigger_count + 1 WHERE id = #{ruleId}")
    int incrementTriggerCount(@Param("ruleId") Long ruleId);
    
    /**
     * 增加转化次数
     */
    @Update("UPDATE marketing_automation_rule SET conversion_count = conversion_count + 1 WHERE id = #{ruleId}")
    int incrementConversionCount(@Param("ruleId") Long ruleId);
}
