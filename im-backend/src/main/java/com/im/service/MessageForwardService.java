package com.im.service;

import com.im.dto.*;
import java.util.List;

/**
 * 消息转发服务接口
 * 功能#22: 消息转发
 */
public interface MessageForwardService {

    /**
     * 转发消息到单个会话
     */
    ForwardMessageResponse forwardMessage(Long forwarderId, ForwardMessageRequest request);

    /**
     * 批量转发消息到多个会话
     */
    List<ForwardMessageResponse> batchForwardMessage(Long forwarderId, ForwardMessageRequest request);

    /**
     * 获取转发记录列表
     */
    List<ForwardRecordDTO> getForwardRecords(Long forwarderId, Integer limit);

    /**
     * 获取消息的转发统计
     */
    Long getForwardCount(Long messageId);

    /**
     * 检查消息是否可以转发
     */
    Boolean canForward(Long messageId, Long userId);

    /**
     * 获取合并转发内容
     */
    List<MessageDTO> getMergedForwardContent(Long forwardId);
}
