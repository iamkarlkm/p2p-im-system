package com.im.service.poiim;

import com.im.entity.poiim.MerchantAgent;
import java.util.List;

/**
 * 商家客服管理服务接口
 */
public interface MerchantAgentService {
    
    /**
     * 添加客服
     */
    MerchantAgent addAgent(MerchantAgent agent);
    
    /**
     * 更新客服信息
     */
    MerchantAgent updateAgent(String agentId, MerchantAgent agent);
    
    /**
     * 删除客服
     */
    void deleteAgent(String agentId);
    
    /**
     * 获取客服详情
     */
    MerchantAgent getAgent(String agentId);
    
    /**
     * 获取商家的所有客服
     */
    List<MerchantAgent> getAgentsByMerchant(String merchantId);
    
    /**
     * 获取POI的可用客服列表
     */
    List<MerchantAgent> getAvailableAgentsByPoi(String poiId);
    
    /**
     * 更新客服在线状态
     */
    void updateAgentStatus(String agentId, String status);
    
    /**
     * 为会话智能分配最佳客服
     */
    MerchantAgent assignBestAgent(String poiId, List<String> queryTags);
    
    /**
     * 增加客服会话数
     */
    void incrementSessionCount(String agentId);
    
    /**
     * 减少客服会话数
     */
    void decrementSessionCount(String agentId);
}
