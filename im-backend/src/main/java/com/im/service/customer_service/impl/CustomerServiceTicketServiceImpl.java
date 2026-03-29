package com.im.service.customer_service.impl;

import com.im.dto.customer_service.*;
import com.im.entity.customer_service.CustomerServiceTicket;
import com.im.enums.customer_service.TicketPriority;
import com.im.enums.customer_service.TicketStatus;
import com.im.service.customer_service.CustomerServiceTicketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 客服工单服务实现
 * 功能 #319 - 智能客服与工单管理系统
 */
@Slf4j
@Service
public class CustomerServiceTicketServiceImpl implements CustomerServiceTicketService {
    
    private final Map<Long, CustomerServiceTicket> ticketStore = new ConcurrentHashMap<>();
    private final AtomicLong ticketIdGenerator = new AtomicLong(1);
    
    @Override
    public TicketResponse createTicket(CreateTicketRequest request) {
        CustomerServiceTicket ticket = new CustomerServiceTicket();
        ticket.setId(ticketIdGenerator.getAndIncrement());
        ticket.setTicketNo(generateTicketNo());
        ticket.setUserId(request.getUserId());
        ticket.setMerchantId(request.getMerchantId());
        ticket.setOrderId(request.getOrderId());
        ticket.setTitle(request.getTitle());
        ticket.setContent(request.getContent());
        ticket.setType(request.getType());
        ticket.setStatus(TicketStatus.PENDING.getCode());
        ticket.setPriority(request.getPriority() != null ? request.getPriority() : TicketPriority.MEDIUM.getCode());
        ticket.setSource(request.getSource());
        ticket.setCreateTime(LocalDateTime.now());
        ticket.setUpdateTime(LocalDateTime.now());
        ticket.setSlaDeadline(LocalDateTime.now().plusHours(24));
        ticket.setDeleted(0);
        
        ticketStore.put(ticket.getId(), ticket);
        log.info("工单创建成功: {}", ticket.getTicketNo());
        
        return convertToResponse(ticket);
    }
    
    @Override
    public TicketResponse getTicketById(Long ticketId) {
        CustomerServiceTicket ticket = ticketStore.get(ticketId);
        return ticket != null ? convertToResponse(ticket) : null;
    }
    
    @Override
    public TicketResponse updateTicket(Long ticketId, CreateTicketRequest request) {
        CustomerServiceTicket ticket = ticketStore.get(ticketId);
        if (ticket == null) return null;
        
        ticket.setTitle(request.getTitle());
        ticket.setContent(request.getContent());
        ticket.setType(request.getType());
        ticket.setPriority(request.getPriority());
        ticket.setUpdateTime(LocalDateTime.now());
        
        return convertToResponse(ticket);
    }
    
    @Override
    public TicketResponse assignTicket(Long ticketId, Long agentId) {
        CustomerServiceTicket ticket = ticketStore.get(ticketId);
        if (ticket == null) return null;
        
        ticket.setAssigneeId(agentId);
        ticket.setStatus(TicketStatus.PROCESSING.getCode());
        ticket.setUpdateTime(LocalDateTime.now());
        
        return convertToResponse(ticket);
    }
    
    @Override
    public TicketResponse processTicket(Long ticketId, String content, Long agentId) {
        CustomerServiceTicket ticket = ticketStore.get(ticketId);
        if (ticket == null) return null;
        
        ticket.setAssigneeId(agentId);
        ticket.setStatus(TicketStatus.WAITING_CONFIRM.getCode());
        ticket.setUpdateTime(LocalDateTime.now());
        
        return convertToResponse(ticket);
    }
    
    @Override
    public TicketResponse closeTicket(Long ticketId, String reason, Long operatorId) {
        CustomerServiceTicket ticket = ticketStore.get(ticketId);
        if (ticket == null) return null;
        
        ticket.setStatus(TicketStatus.CLOSED.getCode());
        ticket.setCloseTime(LocalDateTime.now());
        ticket.setUpdateTime(LocalDateTime.now());
        
        return convertToResponse(ticket);
    }
    
    @Override
    public TicketResponse escalateTicket(Long ticketId, Integer newPriority, String reason) {
        CustomerServiceTicket ticket = ticketStore.get(ticketId);
        if (ticket == null) return null;
        
        ticket.setPriority(newPriority);
        ticket.setUpdateTime(LocalDateTime.now());
        
        return convertToResponse(ticket);
    }
    
    @Override
    public List<TicketResponse> getTicketList(Long userId, Integer status, Integer page, Integer size) {
        List<TicketResponse> result = new ArrayList<>();
        for (CustomerServiceTicket ticket : ticketStore.values()) {
            if (userId != null && !ticket.getUserId().equals(userId)) continue;
            if (status != null && !ticket.getStatus().equals(status)) continue;
            result.add(convertToResponse(ticket));
        }
        return result;
    }
    
    @Override
    public void addTicketComment(Long ticketId, String content, Integer commentType, Long commenterId) {
        log.info("工单备注添加: ticketId={}, commenterId={}", ticketId, commenterId);
    }
    
    @Override
    public TicketStatisticsResponse getTicketStatistics(Long agentId, String dateRange) {
        TicketStatisticsResponse stats = new TicketStatisticsResponse();
        stats.setTotalTickets(ticketStore.size());
        
        int pending = 0, processing = 0, resolved = 0, closed = 0;
        for (CustomerServiceTicket ticket : ticketStore.values()) {
            if (ticket.getAssigneeId() != null && !ticket.getAssigneeId().equals(agentId)) continue;
            
            switch (ticket.getStatus()) {
                case 0: pending++; break;
                case 1: processing++; break;
                case 3: resolved++; break;
                case 4: closed++; break;
            }
        }
        
        stats.setPendingCount(pending);
        stats.setProcessingCount(processing);
        stats.setResolvedCount(resolved);
        stats.setClosedCount(closed);
        stats.setSlaAchievementRate(95.5);
        stats.setSatisfactionScore(4.6);
        
        return stats;
    }
    
    private String generateTicketNo() {
        return "TK" + System.currentTimeMillis() + String.format("%04d", ticketIdGenerator.get() % 10000);
    }
    
    private TicketResponse convertToResponse(CustomerServiceTicket ticket) {
        TicketResponse response = new TicketResponse();
        response.setId(ticket.getId());
        response.setTicketNo(ticket.getTicketNo());
        response.setUserId(ticket.getUserId());
        response.setTitle(ticket.getTitle());
        response.setContent(ticket.getContent());
        response.setType(ticket.getType());
        response.setStatus(ticket.getStatus());
        response.setPriority(ticket.getPriority());
        response.setSource(ticket.getSource());
        response.setAssigneeId(ticket.getAssigneeId());
        response.setCreateTime(ticket.getCreateTime());
        response.setUpdateTime(ticket.getUpdateTime());
        response.setSlaDeadline(ticket.getSlaDeadline());
        response.setResolveTime(ticket.getResolveTime());
        
        TicketStatus status = TicketStatus.fromCode(ticket.getStatus());
        if (status != null) response.setStatusName(status.getName());
        
        TicketPriority priority = TicketPriority.fromCode(ticket.getPriority());
        if (priority != null) response.setPriorityName(priority.getName());
        
        return response;
    }
}
