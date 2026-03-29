package com.im.backend.modules.merchant.assistant.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.merchant.assistant.entity.MessageTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 消息模板Mapper
 */
@Mapper
public interface MessageTemplateMapper extends BaseMapper<MessageTemplate> {
    
    /**
     * 根据商户和类型查询模板
     */
    @Select("SELECT * FROM message_template WHERE (merchant_id = #{merchantId} OR merchant_id = 0) AND template_type = #{templateType} AND enabled = 1 ORDER BY merchant_id DESC")
    List<MessageTemplate> selectByMerchantAndType(@Param("merchantId") Long merchantId, @Param("templateType") String templateType);
    
    /**
     * 增加使用次数
     */
    @Update("UPDATE message_template SET usage_count = usage_count + 1 WHERE id = #{templateId}")
    int incrementUsageCount(@Param("templateId") Long templateId);
}
