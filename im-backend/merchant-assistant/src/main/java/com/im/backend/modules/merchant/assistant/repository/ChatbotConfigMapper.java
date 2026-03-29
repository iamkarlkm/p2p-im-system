package com.im.backend.modules.merchant.assistant.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.merchant.assistant.entity.ChatbotConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 智能客服配置Mapper
 */
@Mapper
public interface ChatbotConfigMapper extends BaseMapper<ChatbotConfig> {
    
    /**
     * 根据商户ID查询配置
     */
    @Select("SELECT * FROM chatbot_config WHERE merchant_id = #{merchantId} AND enabled = 1 LIMIT 1")
    ChatbotConfig selectByMerchantId(@Param("merchantId") Long merchantId);
}
