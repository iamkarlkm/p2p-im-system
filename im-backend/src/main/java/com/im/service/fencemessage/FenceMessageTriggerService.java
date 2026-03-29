package com.im.service.fencemessage;

import com.im.entity.fencemessage.FenceMessageTrigger;
import com.im.entity.fencemessage.FenceMessageRule;
import java.util.List;
import java.util.Map;

/**
 * 场景化消息触发引擎服务接口
 */
public interface FenceMessageTriggerService {
    
    /**
     * 处理围栏进入事件并触发消息
     */
    void triggerEnterMessage(String userId, String fenceId, Double longitude, Double latitude);
    
    /**
     * 处理围栏停留事件并触发消息
     */
    void triggerDwellMessage(String userId, String fenceId, Integer dwellMinutes);
    
    /**
     * 处理围栏离开事件并触发消息
     */
    void triggerExitMessage(String userId, String fenceId);
    
    /**
     * 执行消息触发
     */
    FenceMessageTrigger executeTrigger(String userId, String fenceId, String templateId, 
                                        String scene, Map<String, String> variables);
    
    /**
     * 检查去重
     */
    boolean checkDuplicate(String userId, String fenceId, String templateId, Integer windowMinutes);
    
    /**
     * 检查频次限制
     */
    boolean checkFrequencyLimit(String userId, String ruleId, FenceMessageRule rule);
    
    /**
     * 获取用户的触发记录
     */
    List<FenceMessageTrigger> getUserTriggers(String userId, Integer limit);
    
    /**
     * 获取围栏的触发统计
     */
    Map<String, Object> getFenceTriggerStats(String fenceId, String startDate, String endDate);
    
    /**
     * 重新发送失败的消息
     */
    void retryFailedMessage(String triggerId);
}
