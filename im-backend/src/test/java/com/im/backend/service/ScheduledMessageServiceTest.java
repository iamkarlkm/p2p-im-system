package com.im.backend.service;

import com.im.backend.dto.ScheduledMessageDTO;
import com.im.backend.model.ScheduledMessage;
import com.im.backend.repository.ScheduledMessageRepository;
import com.im.backend.service.impl.ScheduledMessageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 定时消息服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class ScheduledMessageServiceTest {

    @Mock
    private ScheduledMessageRepository scheduledMessageRepository;

    @InjectMocks
    private ScheduledMessageServiceImpl scheduledMessageService;

    private ScheduledMessage mockMessage;
    private ScheduledMessageDTO mockDTO;

    @BeforeEach
    void setUp() {
        mockMessage = new ScheduledMessage();
        mockMessage.setId(1L);
        mockMessage.setSenderId(100L);
        mockMessage.setReceiverId(200L);
        mockMessage.setContent("测试定时消息");
        mockMessage.setStatus(ScheduledMessage.Status.PENDING);
        mockMessage.setScheduledTime(LocalDateTime.now().plusHours(1));

        mockDTO = new ScheduledMessageDTO();
        mockDTO.setReceiverId(200L);
        mockDTO.setContent("测试定时消息");
        mockDTO.setScheduledTime(LocalDateTime.now().plusHours(1));
    }

    @Test
    void testCreateScheduledMessage_Success() {
        when(scheduledMessageRepository.save(any(ScheduledMessage.class))).thenReturn(mockMessage);

        ScheduledMessageDTO result = scheduledMessageService.createScheduledMessage(100L, mockDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(scheduledMessageRepository).save(any(ScheduledMessage.class));
    }

    @Test
    void testCreateScheduledMessage_InvalidTime() {
        mockDTO.setScheduledTime(LocalDateTime.now().minusHours(1));

        assertThrows(IllegalArgumentException.class, () -> {
            scheduledMessageService.createScheduledMessage(100L, mockDTO);
        });
    }

    @Test
    void testCancelScheduledMessage_Success() {
        when(scheduledMessageRepository.findById(1L)).thenReturn(Optional.of(mockMessage));
        when(scheduledMessageRepository.save(any(ScheduledMessage.class))).thenReturn(mockMessage);

        ScheduledMessageDTO result = scheduledMessageService.cancelScheduledMessage(1L, 100L);

        assertNotNull(result);
        assertEquals(ScheduledMessage.Status.CANCELLED, result.getStatus());
    }

    @Test
    void testCancelScheduledMessage_Unauthorized() {
        mockMessage.setSenderId(999L);
        when(scheduledMessageRepository.findById(1L)).thenReturn(Optional.of(mockMessage));

        assertThrows(RuntimeException.class, () -> {
            scheduledMessageService.cancelScheduledMessage(1L, 100L);
        });
    }

    @Test
    void testGetScheduledMessage_Success() {
        when(scheduledMessageRepository.findById(1L)).thenReturn(Optional.of(mockMessage));

        Optional<ScheduledMessageDTO> result = scheduledMessageService.getScheduledMessage(1L, 100L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void testMarkAsSent() {
        when(scheduledMessageRepository.findById(1L)).thenReturn(Optional.of(mockMessage));
        when(scheduledMessageRepository.save(any(ScheduledMessage.class))).thenReturn(mockMessage);

        assertDoesNotThrow(() -> scheduledMessageService.markAsSent(1L));
        assertEquals(ScheduledMessage.Status.SENT, mockMessage.getStatus());
        assertNotNull(mockMessage.getSentTime());
    }
}
