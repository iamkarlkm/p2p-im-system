package com.im.backend.modules.appointment.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.im.backend.common.exception.BusinessException;
import com.im.backend.modules.appointment.dto.AppointmentDTO;
import com.im.backend.modules.appointment.dto.QueueTicketDTO;
import com.im.backend.modules.appointment.entity.Appointment;
import com.im.backend.modules.appointment.entity.QueueTicket;
import com.im.backend.modules.appointment.enums.AppointmentStatus;
import com.im.backend.modules.appointment.repository.AppointmentRepository;
import com.im.backend.modules.appointment.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 预约服务实现类
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl extends ServiceImpl<AppointmentRepository, Appointment> 
        implements AppointmentService {

    private final AppointmentRepository appointmentRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppointmentDTO createAppointment(AppointmentDTO dto) {
        log.info("创建预约: merchantId={}, serviceTypeId={}, date={}", 
                dto.getMerchantId(), dto.getServiceTypeId(), dto.getAppointmentDate());

        // 检查时间冲突
        if (checkTimeConflict(dto.getMerchantId(), null, dto.getAppointmentDate(), 
                              dto.getStartTime(), dto.getEndTime())) {
            throw new BusinessException("该时段已被预约，请选择其他时间");
        }

        Appointment appointment = new Appointment();
        BeanUtils.copyProperties(dto, appointment);
        
        // 生成预约编号
        appointment.generateAppointmentNo();
        
        // 设置默认值
        appointment.setStatus(AppointmentStatus.PENDING.getCode());
        appointment.setCreateTime(LocalDateTime.now());
        appointment.setUpdateTime(LocalDateTime.now());
        appointment.setDeleted(false);
        
        // 计算结束时间
        if (dto.getEstimatedDuration() != null && dto.getStartTime() != null) {
            appointment.setEndTime(dto.getStartTime().plusMinutes(dto.getEstimatedDuration()));
        }

        save(appointment);
        
        log.info("预约创建成功: appointmentNo={}", appointment.getAppointmentNo());
        return convertToDTO(appointment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppointmentDTO confirmAppointment(Long appointmentId) {
        Appointment appointment = getById(appointmentId);
        if (appointment == null) {
            throw new BusinessException("预约不存在");
        }
        
        if (!AppointmentStatus.PENDING.getCode().equals(appointment.getStatus())) {
            throw new BusinessException("只能确认待确认的预约");
        }

        appointment.setStatus(AppointmentStatus.CONFIRMED.getCode());
        appointment.setConfirmTime(LocalDateTime.now());
        appointment.setUpdateTime(LocalDateTime.now());
        
        updateById(appointment);
        log.info("预约确认成功: appointmentId={}", appointmentId);
        
        return convertToDTO(appointment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppointmentDTO cancelAppointment(Long appointmentId, String reason, String cancelBy) {
        Appointment appointment = getById(appointmentId);
        if (appointment == null) {
            throw new BusinessException("预约不存在");
        }

        AppointmentStatus status = AppointmentStatus.fromCode(appointment.getStatus());
        if (status == null || !status.canCancel()) {
            throw new BusinessException("当前状态不能取消预约");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED.getCode());
        appointment.setCancelReason(reason);
        appointment.setCancelBy(cancelBy);
        appointment.setCancelTime(LocalDateTime.now());
        appointment.setUpdateTime(LocalDateTime.now());
        
        updateById(appointment);
        log.info("预约取消成功: appointmentId={}, cancelBy={}", appointmentId, cancelBy);
        
        return convertToDTO(appointment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppointmentDTO startService(Long appointmentId) {
        Appointment appointment = getById(appointmentId);
        if (appointment == null) {
            throw new BusinessException("预约不存在");
        }

        if (!AppointmentStatus.CONFIRMED.getCode().equals(appointment.getStatus())) {
            throw new BusinessException("只能开始已确认的预约");
        }

        appointment.setStatus(AppointmentStatus.IN_SERVICE.getCode());
        appointment.setServiceStartTime(LocalDateTime.now());
        appointment.setUpdateTime(LocalDateTime.now());
        
        updateById(appointment);
        log.info("服务开始: appointmentId={}", appointmentId);
        
        return convertToDTO(appointment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppointmentDTO completeService(Long appointmentId) {
        Appointment appointment = getById(appointmentId);
        if (appointment == null) {
            throw new BusinessException("预约不存在");
        }

        if (!AppointmentStatus.IN_SERVICE.getCode().equals(appointment.getStatus())) {
            throw new BusinessException("只能完成服务中的预约");
        }

        appointment.setStatus(AppointmentStatus.COMPLETED.getCode());
        appointment.setServiceEndTime(LocalDateTime.now());
        appointment.setUpdateTime(LocalDateTime.now());
        
        // 计算实际服务时长
        if (appointment.getServiceStartTime() != null) {
            int duration = (int) java.time.Duration.between(
                    appointment.getServiceStartTime(), 
                    appointment.getServiceEndTime()).toMinutes();
            appointment.setActualDuration(duration);
        }
        
        updateById(appointment);
        log.info("服务完成: appointmentId={}", appointmentId);
        
        return convertToDTO(appointment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppointmentDTO modifyAppointment(Long appointmentId, AppointmentDTO dto) {
        Appointment appointment = getById(appointmentId);
        if (appointment == null) {
            throw new BusinessException("预约不存在");
        }

        AppointmentStatus status = AppointmentStatus.fromCode(appointment.getStatus());
        if (status == null || !status.canModify()) {
            throw new BusinessException("当前状态不能修改预约");
        }

        // 检查新的时间是否冲突
        if (dto.getAppointmentDate() != null && dto.getStartTime() != null) {
            LocalTime endTime = dto.getEndTime();
            if (endTime == null && dto.getEstimatedDuration() != null) {
                endTime = dto.getStartTime().plusMinutes(dto.getEstimatedDuration());
            }
            
            if (checkTimeConflict(appointment.getMerchantId(), appointmentId, 
                                  dto.getAppointmentDate(), dto.getStartTime(), endTime)) {
                throw new BusinessException("该时段已被预约，请选择其他时间");
            }
        }

        // 更新字段
        if (dto.getAppointmentDate() != null) {
            appointment.setAppointmentDate(dto.getAppointmentDate());
        }
        if (dto.getStartTime() != null) {
            appointment.setStartTime(dto.getStartTime());
        }
        if (dto.getEndTime() != null) {
            appointment.setEndTime(dto.getEndTime());
        }
        if (dto.getPeopleCount() != null) {
            appointment.setPeopleCount(dto.getPeopleCount());
        }
        if (dto.getCustomerName() != null) {
            appointment.setCustomerName(dto.getCustomerName());
        }
        if (dto.getCustomerPhone() != null) {
            appointment.setCustomerPhone(dto.getCustomerPhone());
        }
        if (dto.getCustomerRemark() != null) {
            appointment.setCustomerRemark(dto.getCustomerRemark());
        }
        
        appointment.setUpdateTime(LocalDateTime.now());
        updateById(appointment);
        
        log.info("预约修改成功: appointmentId={}", appointmentId);
        return convertToDTO(appointment);
    }

    @Override
    public AppointmentDTO getAppointmentDetail(Long appointmentId) {
        Appointment appointment = getById(appointmentId);
        if (appointment == null) {
            throw new BusinessException("预约不存在");
        }
        return convertToDTO(appointment);
    }

    @Override
    public List<AppointmentDTO> getUserAppointments(Long userId) {
        List<Appointment> list = appointmentRepository.findByUserId(userId);
        return list.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public Page<AppointmentDTO> getMerchantAppointments(Long merchantId, LocalDate date, 
                                                        String status, Page<AppointmentDTO> page) {
        // 使用MyBatis-Plus分页查询
        com.baomidou.mybatisplus.core.metadata.IPage<Appointment> queryPage = 
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page.getCurrent(), page.getSize());
        
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Appointment> wrapper = 
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.eq(Appointment::getMerchantId, merchantId)
               .eq(Appointment::getDeleted, false)
               .orderByAsc(Appointment::getAppointmentDate)
               .orderByAsc(Appointment::getStartTime);
        
        if (date != null) {
            wrapper.eq(Appointment::getAppointmentDate, date);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Appointment::getStatus, status);
        }
        
        IPage<Appointment> result = page(queryPage, wrapper);
        
        List<AppointmentDTO> dtoList = result.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        page.setRecords(dtoList);
        page.setTotal(result.getTotal());
        
        return page;
    }

    @Override
    public List<AppointmentSlotDTO> getAvailableSlots(Long merchantId, Long serviceTypeId, LocalDate date) {
        // 返回可用时段列表
        // 这里简化实现，实际应该查询商户的营业时间和已预约情况
        List<AppointmentSlotDTO> slots = new java.util.ArrayList<>();
        
        // 生成9:00-21:00的时段，每30分钟一个
        LocalTime start = LocalTime.of(9, 0);
        LocalTime end = LocalTime.of(21, 0);
        
        while (start.isBefore(end)) {
            LocalTime slotEnd = start.plusMinutes(30);
            
            AppointmentSlotDTO slot = new AppointmentSlotDTO();
            slot.setStartTime(start);
            slot.setEndTime(slotEnd);
            slot.setAvailable(true);
            slot.setMaxCapacity(5);
            
            // 检查该时段是否已满
            List<Appointment> booked = appointmentRepository.findConflictingAppointments(
                    merchantId, date, start, slotEnd);
            slot.setBookedCount(booked.size());
            slot.setAvailableCount(slot.getMaxCapacity() - booked.size());
            
            if (slot.getAvailableCount() <= 0) {
                slot.setAvailable(false);
            }
            
            slots.add(slot);
            start = slotEnd;
        }
        
        return slots;
    }

    @Override
    public QueueTicketDTO takeQueueTicket(QueueTicketDTO dto) {
        // 简化实现，实际应该集成QueueTicketRepository
        log.info("远程取号: merchantId={}, serviceTypeId={}", dto.getMerchantId(), dto.getServiceTypeId());
        
        QueueTicketDTO result = new QueueTicketDTO();
        BeanUtils.copyProperties(dto, result);
        result.setTicketNo("Q" + System.currentTimeMillis() % 10000);
        result.setStatus("WAITING");
        result.setQueueNumber((int)(Math.random() * 100) + 1);
        result.setPeopleAhead((int)(Math.random() * 10));
        result.setEstimatedWaitMinutes(result.getPeopleAhead() * 10);
        result.setTakeTime(LocalDateTime.now());
        result.setCreateTime(LocalDateTime.now());
        
        return result;
    }

    @Override
    public QueueTicketDTO getQueueStatus(Long ticketId) {
        // 简化实现
        QueueTicketDTO dto = new QueueTicketDTO();
        dto.setId(ticketId);
        dto.setStatus("WAITING");
        dto.setPeopleAhead(5);
        dto.setEstimatedWaitMinutes(50);
        return dto;
    }

    @Override
    public QueueTicketDTO cancelQueueTicket(Long ticketId) {
        log.info("取消排队: ticketId={}", ticketId);
        QueueTicketDTO dto = new QueueTicketDTO();
        dto.setId(ticketId);
        dto.setStatus("CANCELLED");
        return dto;
    }

    @Override
    public QueueTicketDTO callTicket(Long ticketId, Long windowId) {
        log.info("叫号: ticketId={}, windowId={}", ticketId, windowId);
        QueueTicketDTO dto = new QueueTicketDTO();
        dto.setId(ticketId);
        dto.setStatus("CALLING");
        dto.setCallTime(LocalDateTime.now());
        return dto;
    }

    @Override
    public QueueTicketDTO requeueTicket(Long ticketId) {
        log.info("过号重排: ticketId={}", ticketId);
        QueueTicketDTO dto = new QueueTicketDTO();
        dto.setId(ticketId);
        dto.setStatus("WAITING");
        return dto;
    }

    @Override
    public List<QueueTicketDTO> getMerchantQueueList(Long merchantId, String queueId) {
        // 简化实现
        List<QueueTicketDTO> list = new java.util.ArrayList<>();
        for (int i = 0; i < 10; i++) {
            QueueTicketDTO dto = new QueueTicketDTO();
            dto.setId((long)i);
            dto.setTicketNo("Q" + (1001 + i));
            dto.setStatus("WAITING");
            dto.setQueueNumber(i + 1);
            dto.setPeopleAhead(10 - i);
            list.add(dto);
        }
        return list;
    }

    @Override
    public boolean checkTimeConflict(Long merchantId, Long excludeId, LocalDate date, 
                                     LocalTime startTime, LocalTime endTime) {
        List<Appointment> conflicts = appointmentRepository.findConflictingAppointments(
                merchantId, date, startTime, endTime);
        
        if (excludeId != null) {
            conflicts = conflicts.stream()
                    .filter(a -> !a.getId().equals(excludeId))
                    .collect(Collectors.toList());
        }
        
        return !conflicts.isEmpty();
    }

    @Override
    public void sendAppointmentReminder(Long appointmentId) {
        log.info("发送预约提醒: appointmentId={}", appointmentId);
        // 实际实现应该集成消息推送服务
    }

    @Override
    public AppointmentStatisticsDTO getMerchantStatistics(Long merchantId, LocalDate startDate, LocalDate endDate) {
        AppointmentStatisticsDTO stats = new AppointmentStatisticsDTO();
        
        // 统计总预约数
        int total = appointmentRepository.countByMerchantAndDate(merchantId, LocalDate.now());
        stats.setTodayTotal(total);
        
        // 统计各状态数量
        // 简化实现
        stats.setPendingCount(10);
        stats.setConfirmedCount(20);
        stats.setCompletedCount(50);
        stats.setCancelledCount(5);
        
        return stats;
    }

    /**
     * 转换为DTO
     */
    private AppointmentDTO convertToDTO(Appointment appointment) {
        AppointmentDTO dto = new AppointmentDTO();
        BeanUtils.copyProperties(appointment, dto);
        return dto;
    }
}

/**
 * 时段DTO（内部类）
 */
class AppointmentSlotDTO {
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean available;
    private int maxCapacity;
    private int bookedCount;
    private int availableCount;

    // Getters and Setters
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public int getMaxCapacity() { return maxCapacity; }
    public void setMaxCapacity(int maxCapacity) { this.maxCapacity = maxCapacity; }
    public int getBookedCount() { return bookedCount; }
    public void setBookedCount(int bookedCount) { this.bookedCount = bookedCount; }
    public int getAvailableCount() { return availableCount; }
    public void setAvailableCount(int availableCount) { this.availableCount = availableCount; }
}

/**
 * 统计DTO（内部类）
 */
class AppointmentStatisticsDTO {
    private int todayTotal;
    private int pendingCount;
    private int confirmedCount;
    private int completedCount;
    private int cancelledCount;

    // Getters and Setters
    public int getTodayTotal() { return todayTotal; }
    public void setTodayTotal(int todayTotal) { this.todayTotal = todayTotal; }
    public int getPendingCount() { return pendingCount; }
    public void setPendingCount(int pendingCount) { this.pendingCount = pendingCount; }
    public int getConfirmedCount() { return confirmedCount; }
    public void setConfirmedCount(int confirmedCount) { this.confirmedCount = confirmedCount; }
    public int getCompletedCount() { return completedCount; }
    public void setCompletedCount(int completedCount) { this.completedCount = completedCount; }
    public int getCancelledCount() { return cancelledCount; }
    public void setCancelledCount(int cancelledCount) { this.cancelledCount = cancelledCount; }
}
