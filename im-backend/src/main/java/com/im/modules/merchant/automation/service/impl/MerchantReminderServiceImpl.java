package com.im.modules.merchant.automation.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.im.modules.merchant.automation.dto.*;
import com.im.modules.merchant.automation.entity.MerchantReminderConfig;
import com.im.modules.merchant.automation.entity.MerchantReminderRecord;
import com.im.modules.merchant.automation.enums.ReminderRecordStatus;
import com.im.modules.merchant.automation.enums.ReminderRuleType;
import com.im.modules.merchant.automation.repository.MerchantReminderConfigMapper;
import com.im.modules.merchant.automation.repository.MerchantReminderRecordMapper;
import com.im.modules.merchant.automation.service.IMerchantReminderService;
import com.im.modules.merchant.im.service.IMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商户智能提醒服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantReminderServiceImpl implements IMerchantReminderService {
    
    private final MerchantReminderConfigMapper configMapper;
    private final MerchantReminderRecordMapper recordMapper;
    private final IMessageService messageService;
    
    @Override
    public MerchantReminderConfigResponse getConfig(String merchantId) {
        MerchantReminderConfig config = configMapper.findByMerchantId(merchantId);
        if (config == null) {
            return createDefaultConfig(merchantId);
        }
        return convertToConfigResponse(config);
    }
    
    @Override
    @Transactional
    public MerchantReminderConfigResponse updateConfig(UpdateReminderConfigRequest request) {
        MerchantReminderConfig config = configMapper.findByMerchantId(request.getMerchantId());
        
        if (config == null) {
            config = new MerchantReminderConfig();
            config.setMerchantId(request.getMerchantId());
        }
        
        config.setReminderRules(request.getReminderRules() != null ? request.getReminderRules().toString() : null);
        config.setPushEnabled(request.getPushEnabled());
        config.setSmsEnabled(request.getSmsEnabled());
        config.setEmailEnabled(request.getEmailEnabled());
        config.setDailyReportTime(request.getDailyReportTime());
        config.setWeeklyReportDay(request.getWeeklyReportDay());
        config.setWeeklyReportTime(request.getWeeklyReportTime());
        
        if (config.getConfigId() == null) {
            configMapper.insert(config);
        } else {
            configMapper.updateById(config);
        }
        
        return convertToConfigResponse(config);
    }
    
    @Override
    @Transactional
    public void triggerAbnormalOrderReminder(String merchantId, String orderId, String reason) {
        createReminderRecord(merchantId, ReminderRuleType.ABNORMAL_ORDER.name(), 
                "异常订单提醒", "订单 " + orderId + " 出现异常: " + reason, 2);
    }
    
    @Override
    @Transactional
    public void triggerLowStockReminder(String merchantId, String productId, String productName, int currentStock) {
        createReminderRecord(merchantId, ReminderRuleType.LOW_STOCK.name(), 
                "库存不足提醒", "商品 " + productName + " 库存仅剩 " + currentStock + " 件，请及时补货", 2);
    }
    
    @Override
    @Transactional
    public void triggerNewOrderReminder(String merchantId, String orderId, String orderAmount) {
        createReminderRecord(merchantId, ReminderRuleType.NEW_ORDER.name(), 
                "新订单提醒", "您有新订单 " + orderId + "，金额: ¥" + orderAmount, 1);
    }
    
    @Override
    @Transactional
    public void triggerLowRatingAlert(String merchantId, String reviewId, int rating) {
        createReminderRecord(merchantId, ReminderRuleType.LOW_RATING.name(), 
                "低分评价预警", "收到 " + rating + " 星评价，评价ID: " + reviewId + "，请及时处理", 2);
    }
    
    @Override
    @Transactional
    public void sendDailyReport(String merchantId) {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String dateStr = yesterday.format(DateTimeFormatter.ofPattern("MM月dd日"));
        
        String content = String.format("【%s 经营日报】\n\n" +
                "订单数: 待统计\n" +
                "营业额: 待统计\n" +
                "访客数: 待统计\n" +
                "转化率: 待统计\n\n" +
                "详细数据请登录商户后台查看", dateStr);
        
        createReminderRecord(merchantId, ReminderRuleType.DAILY_REPORT.name(), 
                "经营日报", content, 1);
        
        sendMerchantNotification(merchantId, "经营日报", content);
    }
    
    @Override
    @Transactional
    public void sendWeeklyReport(String merchantId) {
        String content = "【本周经营周报】\n\n" +
                "本周订单趋势良好，继续保持！\n" +
                "详细数据请登录商户后台查看周报。";
        
        createReminderRecord(merchantId, ReminderRuleType.WEEKLY_REPORT.name(), 
                "经营周报", content, 1);
        
        sendMerchantNotification(merchantId, "经营周报", content);
    }
    
    @Override
    public int getUnreadCount(String merchantId) {
        return recordMapper.countUnreadByMerchant(merchantId);
    }
    
    @Override
    @Transactional
    public void markAsRead(String recordId) {
        recordMapper.markAsRead(recordId);
    }
    
    @Override
    public List<MerchantReminderRecordResponse> getReminderList(String merchantId, int page, int size) {
        Page<MerchantReminderRecord> recordPage = new Page<>(page, size);
        IPage<MerchantReminderRecord> result = recordMapper.findByMerchantId(recordPage, merchantId);
        
        return result.getRecords().stream()
                .map(this::convertToRecordResponse)
                .collect(Collectors.toList());
    }
    
    private void createReminderRecord(String merchantId, String ruleType, String ruleName, String content, int priority) {
        MerchantReminderRecord record = new MerchantReminderRecord();
        record.setMerchantId(merchantId);
        record.setRuleId("RULE_" + ruleType);
        record.setRuleName(ruleName);
        record.setRuleType(ruleType);
        record.setReminderTitle(ruleName);
        record.setReminderContent(content);
        record.setPriority(priority);
        record.setNotifyChannel("PUSH");
        record.setStatus(ReminderRecordStatus.SENT.getCode());
        record.setRelatedData("{}");
        record.setSendTime(LocalDateTime.now());
        
        recordMapper.insert(record);
    }
    
    private void sendMerchantNotification(String merchantId, String title, String content) {
        try {
            messageService.sendSystemMessage("SYSTEM", merchantId, title + "\n" + content);
        } catch (Exception e) {
            log.error("发送商户通知失败: {}", merchantId, e);
        }
    }
    
    private MerchantReminderConfigResponse createDefaultConfig(String merchantId) {
        List<MerchantReminderConfigResponse.ReminderRule> rules = new ArrayList<>();
        
        rules.add(MerchantReminderConfigResponse.ReminderRule.builder()
                .ruleId("RULE_ABNORMAL_ORDER")
                .ruleName("异常订单提醒")
                .ruleType(ReminderRuleType.ABNORMAL_ORDER.name())
                .enabled(true)
                .build());
        
        rules.add(MerchantReminderConfigResponse.ReminderRule.builder()
                .ruleId("RULE_NEW_ORDER")
                .ruleName("新订单提醒")
                .ruleType(ReminderRuleType.NEW_ORDER.name())
                .enabled(true)
                .build());
        
        rules.add(MerchantReminderConfigResponse.ReminderRule.builder()
                .ruleId("RULE_DAILY_REPORT")
                .ruleName("经营日报")
                .ruleType(ReminderRuleType.DAILY_REPORT.name())
                .enabled(true)
                .build());
        
        return MerchantReminderConfigResponse.builder()
                .merchantId(merchantId)
                .reminderRules(rules)
                .pushEnabled(true)
                .smsEnabled(false)
                .emailEnabled(false)
                .dailyReportTime("09:00")
                .weeklyReportDay("MON")
                .weeklyReportTime("09:00")
                .build();
    }
    
    private MerchantReminderConfigResponse convertToConfigResponse(MerchantReminderConfig config) {
        return MerchantReminderConfigResponse.builder()
                .configId(config.getConfigId())
                .merchantId(config.getMerchantId())
                .pushEnabled(config.getPushEnabled())
                .smsEnabled(config.getSmsEnabled())
                .emailEnabled(config.getEmailEnabled())
                .dailyReportTime(config.getDailyReportTime())
                .weeklyReportDay(config.getWeeklyReportDay())
                .weeklyReportTime(config.getWeeklyReportTime())
                .createTime(config.getCreateTime())
                .updateTime(config.getUpdateTime())
                .build();
    }
    
    private MerchantReminderRecordResponse convertToRecordResponse(MerchantReminderRecord record) {
        return MerchantReminderRecordResponse.builder()
                .recordId(record.getRecordId())
                .merchantId(record.getMerchantId())
                .ruleId(record.getRuleId())
                .ruleName(record.getRuleName())
                .ruleType(record.getRuleType())
                .ruleTypeName(ReminderRuleType.getDescByCode(parseRuleType(record.getRuleType())))
                .reminderTitle(record.getReminderTitle())
                .reminderContent(record.getReminderContent())
                .priority(record.getPriority())
                .notifyChannel(record.getNotifyChannel())
                .status(record.getStatus())
                .statusName(ReminderRecordStatus.getDescByCode(record.getStatus()))
                .relatedData(record.getRelatedData())
                .sendTime(record.getSendTime())
                .readTime(record.getReadTime())
                .createTime(record.getCreateTime())
                .build();
    }
    
    private int parseRuleType(String ruleType) {
        try {
            return ReminderRuleType.valueOf(ruleType).getCode();
        } catch (Exception e) {
            return 0;
        }
    }
}
