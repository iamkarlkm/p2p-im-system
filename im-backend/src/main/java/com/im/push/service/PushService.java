package com.im.push.service;

import com.im.push.dto.PushRequest;

import java.util.List;

/**
 * 推送服务接口
 * 
 * @author IM Development Team
 * @version 1.0
 */
public interface PushService {
    
    /**
     * 发送单条推送
     */
    boolean sendPush(PushRequest request);
    
    /**
     * 批量发送推送
     */
    int batchSendPush(List<PushRequest> requests);
    
    /**
     * 处理离线消息推送
     */
    int processOfflinePush(Long userId);
}
