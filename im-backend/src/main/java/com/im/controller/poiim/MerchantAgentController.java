package com.im.controller.poiim;

import com.im.common.Result;
import com.im.entity.poiim.MerchantAgent;
import com.im.entity.poiim.AgentKnowledgeBase;
import com.im.service.poiim.MerchantAgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 商家客服管理控制器
 */
@RestController
@RequestMapping("/api/v1/poi-im/agents")
public class MerchantAgentController {
    
    @Autowired
    private MerchantAgentService agentService;
    
    private final Map<String, AgentKnowledgeBase> knowledgeStore = new ConcurrentHashMap<>();
    
    /**
     * 添加客服
     */
    @PostMapping
    public Result<MerchantAgent> addAgent(@RequestBody MerchantAgent agent) {
        MerchantAgent created = agentService.addAgent(agent);
        return Result.success(created);
    }
    
    /**
     * 更新客服
     */
    @PutMapping("/{agentId}")
    public Result<MerchantAgent> updateAgent(
            @PathVariable String agentId,
            @RequestBody MerchantAgent agent) {
        MerchantAgent updated = agentService.updateAgent(agentId, agent);
        return Result.success(updated);
    }
    
    /**
     * 删除客服
     */
    @DeleteMapping("/{agentId}")
    public Result<Void> deleteAgent(@PathVariable String agentId) {
        agentService.deleteAgent(agentId);
        return Result.success();
    }
    
    /**
     * 获取客服详情
     */
    @GetMapping("/{agentId}")
    public Result<MerchantAgent> getAgent(@PathVariable String agentId) {
        MerchantAgent agent = agentService.getAgent(agentId);
        return Result.success(agent);
    }
    
    /**
     * 获取商家的所有客服
     */
    @GetMapping("/merchant/{merchantId}")
    public Result<List<MerchantAgent>> getAgentsByMerchant(@PathVariable String merchantId) {
        List<MerchantAgent> agents = agentService.getAgentsByMerchant(merchantId);
        return Result.success(agents);
    }
    
    /**
     * 更新客服在线状态
     */
    @PostMapping("/{agentId}/status")
    public Result<Void> updateAgentStatus(
            @PathVariable String agentId,
            @RequestParam String status) {
        agentService.updateAgentStatus(agentId, status);
        return Result.success();
    }
    
    /**
     * 获取POI的可用客服
     */
    @GetMapping("/poi/{poiId}/available")
    public Result<List<MerchantAgent>> getAvailableAgents(@PathVariable String poiId) {
        List<MerchantAgent> agents = agentService.getAvailableAgentsByPoi(poiId);
        return Result.success(agents);
    }
    
    // ========== 知识库管理 ==========
    
    /**
     * 添加知识库条目
     */
    @PostMapping("/knowledge")
    public Result<AgentKnowledgeBase> addKnowledge(@RequestBody AgentKnowledgeBase knowledge) {
        knowledge.setKnowledgeId(UUID.randomUUID().toString());
        knowledge.setEnabled(true);
        knowledge.setClickCount(0);
        knowledge.setCreateTime(java.time.LocalDateTime.now());
        knowledge.setUpdateTime(java.time.LocalDateTime.now());
        knowledgeStore.put(knowledge.getKnowledgeId(), knowledge);
        return Result.success(knowledge);
    }
    
    /**
     * 更新知识库条目
     */
    @PutMapping("/knowledge/{knowledgeId}")
    public Result<AgentKnowledgeBase> updateKnowledge(
            @PathVariable String knowledgeId,
            @RequestBody AgentKnowledgeBase knowledge) {
        knowledge.setKnowledgeId(knowledgeId);
        knowledge.setUpdateTime(java.time.LocalDateTime.now());
        knowledgeStore.put(knowledgeId, knowledge);
        return Result.success(knowledge);
    }
    
    /**
     * 删除知识库条目
     */
    @DeleteMapping("/knowledge/{knowledgeId}")
    public Result<Void> deleteKnowledge(@PathVariable String knowledgeId) {
        knowledgeStore.remove(knowledgeId);
        return Result.success();
    }
    
    /**
     * 获取POI的知识库
     */
    @GetMapping("/knowledge/poi/{poiId}")
    public Result<List<AgentKnowledgeBase>> getPoiKnowledge(@PathVariable String poiId) {
        List<AgentKnowledgeBase> knowledge = knowledgeStore.values().stream()
                .filter(k -> poiId.equals(k.getPoiId()))
                .filter(k -> Boolean.TRUE.equals(k.getEnabled()))
                .collect(Collectors.toList());
        return Result.success(knowledge);
    }
    
    /**
     * 搜索知识库
     */
    @GetMapping("/knowledge/search")
    public Result<List<AgentKnowledgeBase>> searchKnowledge(
            @RequestParam String poiId,
            @RequestParam String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        List<AgentKnowledgeBase> results = knowledgeStore.values().stream()
                .filter(k -> poiId.equals(k.getPoiId()))
                .filter(k -> Boolean.TRUE.equals(k.getEnabled()))
                .filter(k -> {
                    if (k.getQuestion().toLowerCase().contains(lowerKeyword)) return true;
                    if (k.getKeywords() != null) {
                        return k.getKeywords().stream()
                                .anyMatch(kw -> kw.toLowerCase().contains(lowerKeyword));
                    }
                    return false;
                })
                .collect(Collectors.toList());
        return Result.success(results);
    }
}
