package com.im.service.poiim.impl;

import com.im.entity.poiim.MerchantAgent;
import com.im.service.poiim.MerchantAgentService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 商家客服管理服务实现
 */
@Service
public class MerchantAgentServiceImpl implements MerchantAgentService {
    
    private final Map<String, MerchantAgent> agentStore = new ConcurrentHashMap<>();
    
    @Override
    public MerchantAgent addAgent(MerchantAgent agent) {
        agent.setAgentId(UUID.randomUUID().toString());
        agent.setOnlineStatus("OFFLINE");
        agent.setCurrentSessionCount(0);
        agent.setTotalServiceCount(0);
        agent.setRating(5.0);
        agent.setSatisfactionRate(100.0);
        agent.setCreateTime(LocalDateTime.now());
        agent.setLastActiveTime(LocalDateTime.now());
        
        agentStore.put(agent.getAgentId(), agent);
        return agent;
    }
    
    @Override
    public MerchantAgent updateAgent(String agentId, MerchantAgent agent) {
        agent.setAgentId(agentId);
        agentStore.put(agentId, agent);
        return agent;
    }
    
    @Override
    public void deleteAgent(String agentId) {
        agentStore.remove(agentId);
    }
    
    @Override
    public MerchantAgent getAgent(String agentId) {
        return agentStore.get(agentId);
    }
    
    @Override
    public List<MerchantAgent> getAgentsByMerchant(String merchantId) {
        return agentStore.values().stream()
                .filter(a -> merchantId.equals(a.getMerchantId()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<MerchantAgent> getAvailableAgentsByPoi(String poiId) {
        return agentStore.values().stream()
                .filter(a -> a.getPoiIds() != null && a.getPoiIds().contains(poiId))
                .filter(a -> "ONLINE".equals(a.getOnlineStatus()))
                .filter(a -> a.getCurrentSessionCount() < a.getMaxConcurrentSessions())
                .sorted((a, b) -> Integer.compare(
                        a.getCurrentSessionCount(), 
                        b.getCurrentSessionCount()))
                .collect(Collectors.toList());
    }
    
    @Override
    public void updateAgentStatus(String agentId, String status) {
        MerchantAgent agent = agentStore.get(agentId);
        if (agent != null) {
            agent.setOnlineStatus(status);
            agent.setLastActiveTime(LocalDateTime.now());
        }
    }
    
    @Override
    public MerchantAgent assignBestAgent(String poiId, List<String> queryTags) {
        List<MerchantAgent> availableAgents = getAvailableAgentsByPoi(poiId);
        if (availableAgents.isEmpty()) return null;
        
        // 按技能标签匹配度排序
        return availableAgents.stream()
                .sorted((a, b) -> {
                    int matchA = calculateSkillMatch(a.getSkillTags(), queryTags);
                    int matchB = calculateSkillMatch(b.getSkillTags(), queryTags);
                    if (matchA != matchB) return Integer.compare(matchB, matchA);
                    // 技能相同则按当前负载排序
                    return Integer.compare(a.getCurrentSessionCount(), b.getCurrentSessionCount());
                })
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 计算技能匹配度
     */
    private int calculateSkillMatch(List<String> agentSkills, List<String> queryTags) {
        if (agentSkills == null || queryTags == null) return 0;
        int match = 0;
        for (String tag : queryTags) {
            if (agentSkills.contains(tag)) match++;
        }
        return match;
    }
    
    @Override
    public void incrementSessionCount(String agentId) {
        MerchantAgent agent = agentStore.get(agentId);
        if (agent != null) {
            agent.setCurrentSessionCount(agent.getCurrentSessionCount() + 1);
        }
    }
    
    @Override
    public void decrementSessionCount(String agentId) {
        MerchantAgent agent = agentStore.get(agentId);
        if (agent != null) {
            agent.setCurrentSessionCount(Math.max(0, agent.getCurrentSessionCount() - 1));
        }
    }
}
