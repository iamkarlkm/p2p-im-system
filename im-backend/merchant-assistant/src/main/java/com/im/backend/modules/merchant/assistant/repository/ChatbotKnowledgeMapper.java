package com.im.backend.modules.merchant.assistant.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.merchant.assistant.entity.ChatbotKnowledge;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 客服知识库Mapper
 */
@Mapper
public interface ChatbotKnowledgeMapper extends BaseMapper<ChatbotKnowledge> {
    
    /**
     * 查询商户启用的知识条目
     */
    @Select("SELECT * FROM chatbot_knowledge WHERE merchant_id = #{merchantId} AND enabled = 1 ORDER BY priority DESC")
    List<ChatbotKnowledge> selectByMerchantId(@Param("merchantId") Long merchantId);
    
    /**
     * 根据意图标签查询
     */
    @Select("SELECT * FROM chatbot_knowledge WHERE merchant_id = #{merchantId} AND intent_tag = #{intentTag} AND enabled = 1 LIMIT 1")
    ChatbotKnowledge selectByIntentTag(@Param("merchantId") Long merchantId, @Param("intentTag") String intentTag);
}
