package com.im.modules.merchant.automation.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.im.modules.merchant.automation.dto.*;
import com.im.modules.merchant.automation.entity.AutoMarketingTask;
import com.im.modules.merchant.automation.enums.AutoMarketingTaskType;
import com.im.modules.merchant.automation.enums.MarketingTaskStatus;
import com.im.modules.merchant.automation.repository.AutoMarketingTaskMapper;
import com.im.modules.merchant.automation.service.IAutoMarketingService;
import com.im.modules.merchant.im.service.IMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 自动化营销服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AutoMarketingServiceImpl implements IAutoMarketingService {
    
    private final AutoMarketingTaskMapper taskMapper;
    private final IMessageService messageService;
    
    @Override
    @Transactional
    public AutoMarketingTaskResponse createTask(CreateAutoMarketingTaskRequest request) {
        AutoMarketingTask task = new AutoMarketingTask();
        task.setMerchantId(request.getMerchantId());
        task.setTaskName(request.getTaskName());
        task.setDescription(request.getDescription());
        task.setTaskType(request.getTaskType());
        task.setStatus(MarketingTaskStatus.DRAFT.getCode());
        task.setTriggerCondition(request.getTriggerCondition());
        task.setScheduleTime(request.getScheduleTime());
        task.setCronExpression(request.getCronExpression());
        task.setTargetFilter(request.getTargetFilter());
        task.setMessageTemplate(request.getMessageTemplate());
        task.setMediaUrls(String.join(",", request.getImageUrls() != null ? request.getImageUrls() : List.of()));
        task.setVariables(request.getVariables() != null ? request.getVariables().toString() : null);
        task.setTargetUserCount(0);
        task.setSentCount(0);
        task.setSuccessCount(0);
        task.setReadCount(0);
        task.setPriority(request.getPriority());
        task.setNextExecuteTime(request.getScheduleTime());
        
        taskMapper.insert(task);
        
        return convertToResponse(task);
    }
    
    @Override
    @Transactional
    public AutoMarketingTaskResponse updateTask(String taskId, CreateAutoMarketingTaskRequest request) {
        AutoMarketingTask task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }
        
        task.setTaskName(request.getTaskName());
        task.setDescription(request.getDescription());
        task.setTriggerCondition(request.getTriggerCondition());
        task.setScheduleTime(request.getScheduleTime());
        task.setCronExpression(request.getCronExpression());
        task.setTargetFilter(request.getTargetFilter());
        task.setMessageTemplate(request.getMessageTemplate());
        task.setMediaUrls(String.join(",", request.getImageUrls() != null ? request.getImageUrls() : List.of()));
        task.setPriority(request.getPriority());
        
        taskMapper.updateById(task);
        
        return convertToResponse(task);
    }
    
    @Override
    @Transactional
    public void deleteTask(String taskId) {
        taskMapper.deleteById(taskId);
    }
    
    @Override
    public AutoMarketingTaskResponse getTaskDetail(String taskId) {
        AutoMarketingTask task = taskMapper.selectById(taskId);
        if (task == null) {
            return null;
        }
        return convertToResponse(task);
    }
    
    @Override
    public IPage<AutoMarketingTaskResponse> getMerchantTasks(Page<AutoMarketingTaskResponse> page, String merchantId) {
        Page<AutoMarketingTask> taskPage = new Page<>(page.getCurrent(), page.getSize());
        IPage<AutoMarketingTask> result = taskMapper.findByMerchantId(taskPage, merchantId);
        
        List<AutoMarketingTaskResponse> records = result.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        Page<AutoMarketingTaskResponse> responsePage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        responsePage.setRecords(records);
        return responsePage;
    }
    
    @Override
    @Transactional
    public void enableTask(String taskId) {
        AutoMarketingTask task = taskMapper.selectById(taskId);
        if (task != null) {
            task.setStatus(MarketingTaskStatus.ENABLED.getCode());
            taskMapper.updateById(task);
        }
    }
    
    @Override
    @Transactional
    public void pauseTask(String taskId) {
        AutoMarketingTask task = taskMapper.selectById(taskId);
        if (task != null) {
            task.setStatus(MarketingTaskStatus.PAUSED.getCode());
            taskMapper.updateById(task);
        }
    }
    
    @Override
    @Transactional
    public void executeTaskNow(String taskId) {
        AutoMarketingTask task = taskMapper.selectById(taskId);
        if (task == null) {
            return;
        }
        
        // 执行营销任务
        executeMarketingTask(task);
        
        task.setStatus(MarketingTaskStatus.COMPLETED.getCode());
        task.setLastExecuteTime(LocalDateTime.now());
        taskMapper.updateById(task);
    }
    
    @Override
    @Transactional
    public void triggerNewUserWelcome(String merchantId, String userId) {
        List<AutoMarketingTask> tasks = taskMapper.findByMerchantAndType(merchantId, AutoMarketingTaskType.NEW_USER_WELCOME.getCode());
        for (AutoMarketingTask task : tasks) {
            if (task.getStatus() == MarketingTaskStatus.ENABLED.getCode()) {
                sendMarketingMessage(merchantId, userId, task.getMessageTemplate(), task.getMediaUrls());
            }
        }
    }
    
    @Override
    @Transactional
    public void triggerOrderReviewInvite(String merchantId, String userId, String orderId) {
        List<AutoMarketingTask> tasks = taskMapper.findByMerchantAndType(merchantId, AutoMarketingTaskType.ORDER_REVIEW_INVITE.getCode());
        for (AutoMarketingTask task : tasks) {
            if (task.getStatus() == MarketingTaskStatus.ENABLED.getCode()) {
                String message = task.getMessageTemplate().replace("${orderId}", orderId);
                sendMarketingMessage(merchantId, userId, message, task.getMediaUrls());
            }
        }
    }
    
    private void executeMarketingTask(AutoMarketingTask task) {
        log.info("执行营销任务: {}", task.getTaskName());
        
        // 模拟获取目标用户列表
        List<String> targetUsers = getTargetUsers(task);
        
        int sent = 0;
        int success = 0;
        
        for (String userId : targetUsers) {
            sent++;
            try {
                sendMarketingMessage(task.getMerchantId(), userId, task.getMessageTemplate(), task.getMediaUrls());
                success++;
            } catch (Exception e) {
                log.error("发送营销消息失败: {}", userId, e);
            }
        }
        
        task.setTargetUserCount(targetUsers.size());
        task.setSentCount(sent);
        task.setSuccessCount(success);
        taskMapper.updateById(task);
    }
    
    private void sendMarketingMessage(String merchantId, String userId, String template, String mediaUrls) {
        String message = template;
        
        // 替换变量
        message = message.replace("${time}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        message = message.replace("${date}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd")));
        
        // 调用消息服务发送
        try {
            messageService.sendSystemMessage(merchantId, userId, message);
        } catch (Exception e) {
            log.error("发送消息失败", e);
        }
    }
    
    private List<String> getTargetUsers(AutoMarketingTask task) {
        // 实际实现中根据targetFilter查询用户列表
        return List.of();
    }
    
    private AutoMarketingTaskResponse convertToResponse(AutoMarketingTask task) {
        return AutoMarketingTaskResponse.builder()
                .taskId(task.getTaskId())
                .merchantId(task.getMerchantId())
                .taskName(task.getTaskName())
                .description(task.getDescription())
                .taskType(task.getTaskType())
                .taskTypeName(AutoMarketingTaskType.getDescByCode(task.getTaskType()))
                .status(task.getStatus())
                .statusName(MarketingTaskStatus.getDescByCode(task.getStatus()))
                .triggerCondition(task.getTriggerCondition())
                .scheduleTime(task.getScheduleTime())
                .cronExpression(task.getCronExpression())
                .targetUserCount(task.getTargetUserCount())
                .sentCount(task.getSentCount())
                .successCount(task.getSuccessCount())
                .readCount(task.getReadCount())
                .messageTemplate(task.getMessageTemplate())
                .imageUrls(task.getMediaUrls() != null ? List.of(task.getMediaUrls().split(",")) : List.of())
                .priority(task.getPriority())
                .lastExecuteTime(task.getLastExecuteTime())
                .nextExecuteTime(task.getNextExecuteTime())
                .createTime(task.getCreateTime())
                .updateTime(task.getUpdateTime())
                .build();
    }
}
