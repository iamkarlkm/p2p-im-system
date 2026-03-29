package com.im.backend.modules.appointment.service;

import com.im.backend.modules.appointment.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 排队叫号服务接口
 */
public interface QueueService {

    /**
     * 远程取号
     */
    QueueTicketDetailDTO takeQueue(Long userId, TakeQueueRequestDTO request);

    /**
     * 取消排队
     */
    QueueTicketDetailDTO cancelQueue(Long userId, Long ticketId);

    /**
     * 获取排队详情
     */
    QueueTicketDetailDTO getQueueDetail(Long userId, Long ticketId);

    /**
     * 获取我的排队列表
     */
    List<QueueTicketDetailDTO> getMyQueues(Long userId, String status);

    /**
     * 商家叫号
     */
    QueueTicketDetailDTO callNext(Long merchantId, String queueType);

    /**
     * 用户确认到达
     */
    QueueTicketDetailDTO confirmArrive(Long userId, Long ticketId);

    /**
     * 获取商户排队状态
     */
    List<QueueStatusDTO> getMerchantQueueStatus(Long merchantId);

    /**
     * 获取商户各队列等待人数
     */
    List<QueueTypeStatusDTO> getQueueTypeStatus(Long merchantId);
}
