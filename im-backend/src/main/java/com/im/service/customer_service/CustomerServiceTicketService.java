package com.im.service.customer_service;

import com.im.dto.customer_service.*;
import java.util.List;

/**
 * 客服工单服务接口
 * 功能 #319 - 智能客服与工单管理系统
 */
public interface CustomerServiceTicketService {
    
    /**
     * 创建工单
     */
    TicketResponse createTicket(CreateTicketRequest request);
    
    /**
     * 获取工单详情
     */
    TicketResponse getTicketById(Long ticketId);
    
    /**
     * 更新工单
     */
    TicketResponse updateTicket(Long ticketId, CreateTicketRequest request);
    
    /**
     * 分配工单
     */
    TicketResponse assignTicket(Long ticketId, Long agentId);
    
    /**
     * 处理工单
     */
    TicketResponse processTicket(Long ticketId, String content, Long agentId);
    
    /**
     * 关闭工单
     */
    TicketResponse closeTicket(Long ticketId, String reason, Long operatorId);
    
    /**
     * 升级工单
     */
    TicketResponse escalateTicket(Long ticketId, Integer newPriority, String reason);
    
    /**
     * 获取工单列表
     */
    List<TicketResponse> getTicketList(Long userId, Integer status, Integer page, Integer size);
    
    /**
     * 添加工单备注
     */
    void addTicketComment(Long ticketId, String content, Integer commentType, Long commenterId);
    
    /**
     * 获取工单统计
     */
    TicketStatisticsResponse getTicketStatistics(Long agentId, String dateRange);
}
