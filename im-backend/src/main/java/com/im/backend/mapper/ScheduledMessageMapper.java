package com.im.backend.mapper;

import com.im.backend.dto.ScheduledMessageDTO;
import com.im.backend.model.ScheduledMessage;
import org.springframework.stereotype.Component;

/**
 * 定时消息DTO与实体转换器
 */
@Component
public class ScheduledMessageMapper {

    /**
     * 实体转DTO
     */
    public ScheduledMessageDTO toDTO(ScheduledMessage entity) {
        if (entity == null) return null;
        
        ScheduledMessageDTO dto = new ScheduledMessageDTO();
        dto.setId(entity.getId());
        dto.setSenderId(entity.getSenderId());
        dto.setReceiverId(entity.getReceiverId());
        dto.setContent(entity.getContent());
        dto.setStatus(entity.getStatus());
        dto.setScheduledTime(entity.getScheduledTime());
        dto.setSentTime(entity.getSentTime());
        dto.setFailureReason(entity.getFailureReason());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    /**
     * DTO转实体
     */
    public ScheduledMessage toEntity(ScheduledMessageDTO dto) {
        if (dto == null) return null;
        
        ScheduledMessage entity = new ScheduledMessage();
        entity.setId(dto.getId());
        entity.setSenderId(dto.getSenderId());
        entity.setReceiverId(dto.getReceiverId());
        entity.setContent(dto.getContent());
        entity.setStatus(dto.getStatus());
        entity.setScheduledTime(dto.getScheduledTime());
        entity.setSentTime(dto.getSentTime());
        entity.setFailureReason(dto.getFailureReason());
        return entity;
    }
}
