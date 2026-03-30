package com.im.backend.modules.merchant.operation.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.im.backend.modules.merchant.operation.dto.MerchantOperationConfigRequest;
import com.im.backend.modules.merchant.operation.dto.MerchantOperationDailyReportResponse;
import com.im.backend.modules.merchant.operation.entity.MerchantOperationConfig;
import com.im.backend.modules.merchant.operation.entity.MerchantOperationDailyReport;
import com.im.backend.modules.merchant.operation.entity.MerchantOperationTask;
import com.im.backend.modules.merchant.operation.repository.MerchantOperationConfigMapper;
import com.im.backend.modules.merchant.operation.repository.MerchantOperationDailyReportMapper;
import com.im.backend.modules.merchant.operation.repository.MerchantOperationTaskMapper;
import com.im.backend.modules.merchant.operation.service.IMerchantOperationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商户运营助手服务实现
 * Feature #307: Local Merchant Smart Operation Assistant
 * 
 * @author IM Development Team
 * @since 2026-03-30
 */
@Slf4j
@Service
public class MerchantOperationServiceImpl extends ServiceImpl<MerchantOperationConfigMapper, MerchantOperationConfig> 
        implements IMerchantOperationService {

    @Autowired
    private MerchantOperationConfigMapper configMapper;

    @Autowired
    private MerchantOperationDailyReportMapper dailyReportMapper;

    @Autowired
    private MerchantOperationTaskMapper taskMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveConfig(MerchantOperationConfigRequest request) {
        MerchantOperationConfig existingConfig = configMapper.selectByMerchantId(request.getMerchantId());
        
        MerchantOperationConfig config;
        if (existingConfig != null) {
            config = existingConfig;
            BeanUtils.copyProperties(request, config);
            config.setUpdateTime(LocalDateTime.now());
            configMapper.updateById(config);
            log.info("Updated operation config for merchant: {}", request.getMerchantId());
        } else {
            config = new MerchantOperationConfig();
            BeanUtils.copyProperties(request, config);
            config.setStatus(1);
            config.setDeleted(0);
            config.setCreateTime(LocalDateTime.now());
            config.setUpdateTime(LocalDateTime.now());
            configMapper.insert(config);
            log.info("Created operation config for merchant: {}", request.getMerchantId());
        }
        
        return config.getId();
    }

    @Override
    public MerchantOperationConfig getConfigByMerchantId(Long merchantId) {
        return configMapper.selectByMerchantId(merchantId);
    }

    @Override
    public MerchantOperationDailyReport generateDailyReport(Long merchantId, LocalDate reportDate) {
        // 模拟生成日报数据
        MerchantOperationDailyReport report = MerchantOperationDailyReport.builder()
                .merchantId(merchantId)
                .reportDate(reportDate)
                .totalOrders((int) (Math.random() * 100) + 50)
                .totalAmount(BigDecimal.valueOf(Math.random() * 5000 + 1000).setScale(2, RoundingMode.HALF_UP))
                .completedOrders((int) (Math.random() * 80) + 40)
                .refundOrders((int) (Math.random() * 5))
                .visitorCount((int) (Math.random() * 500) + 200)
                .conversionRate(BigDecimal.valueOf(Math.random() * 20 + 10).setScale(2, RoundingMode.HALF_UP))
                .avgOrderValue(BigDecimal.valueOf(Math.random() * 100 + 50).setScale(2, RoundingMode.HALF_UP))
                .newUsers((int) (Math.random() * 30) + 10)
                .returningUsers((int) (Math.random() * 50) + 20)
                .positiveReviews((int) (Math.random() * 20) + 5)
                .negativeReviews((int) (Math.random() * 3))
                .avgRating(BigDecimal.valueOf(Math.random() * 2 + 3).setScale(1, RoundingMode.HALF_UP))
                .aiSuggestions(generateAISuggestions(merchantId))
                .deleted(0)
                .build();
        
        report.setCreateTime(LocalDateTime.now());
        report.setUpdateTime(LocalDateTime.now());
        
        dailyReportMapper.insert(report);
        return report;
    }

    @Override
    public Page<MerchantOperationDailyReportResponse> queryDailyReportPage(Long merchantId, Integer pageNum, Integer pageSize) {
        Page<MerchantOperationDailyReport> page = new Page<>(pageNum, pageSize);
        Page<MerchantOperationDailyReport> reportPage = dailyReportMapper.selectPageByMerchantId(page, merchantId);
        
        List<MerchantOperationDailyReportResponse> records = reportPage.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        Page<MerchantOperationDailyReportResponse> resultPage = new Page<>();
        BeanUtils.copyProperties(reportPage, resultPage);
        resultPage.setRecords(records);
        
        return resultPage;
    }

    @Override
    public MerchantOperationDailyReportResponse getLatestDailyReport(Long merchantId) {
        MerchantOperationDailyReport report = dailyReportMapper.selectLatestByMerchantId(merchantId);
        if (report == null) {
            return null;
        }
        return convertToResponse(report);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOperationTask(MerchantOperationTask task) {
        task.setStatus(0);
        task.setDeleted(0);
        task.setCreateTime(LocalDateTime.now());
        task.setUpdateTime(LocalDateTime.now());
        taskMapper.insert(task);
        return task.getId();
    }

    @Override
    public List<MerchantOperationTask> getPendingTasks(Long merchantId) {
        return taskMapper.selectPendingTasks(merchantId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean executeTask(Long taskId, String result) {
        int rows = taskMapper.markAsExecuted(taskId, result);
        return rows > 0;
    }

    @Override
    public List<String> getAIOperationSuggestions(Long merchantId) {
        List<String> suggestions = new ArrayList<>();
        
        MerchantOperationDailyReport latestReport = dailyReportMapper.selectLatestByMerchantId(merchantId);
        if (latestReport != null) {
            if (latestReport.getConversionRate().compareTo(BigDecimal.valueOf(15)) < 0) {
                suggestions.add("转化率偏低，建议优化商品详情页或推出限时优惠活动提升转化。");
            }
            if (latestReport.getAvgRating().compareTo(BigDecimal.valueOf(4.0)) < 0) {
                suggestions.add("用户评分较低，建议加强服务质量，主动收集用户反馈并改进。");
            }
            if (latestReport.getNewUsers() < 15) {
                suggestions.add("新用户增长缓慢，建议加大推广力度或开展新用户专享活动。");
            }
            if (latestReport.getRefundOrders() > 3) {
                suggestions.add("退款订单较多，建议检查商品质量和描述准确性。");
            }
        }
        
        if (suggestions.isEmpty()) {
            suggestions.add("店铺运营良好，建议保持当前策略，可尝试推出新品类拓展业务。");
        }
        
        return suggestions;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean enableAutoMarketing(Long merchantId, String rules) {
        MerchantOperationConfig config = configMapper.selectByMerchantId(merchantId);
        if (config == null) {
            return false;
        }
        
        config.setAutoMarketingEnabled(true);
        config.setAutoMarketingRules(rules);
        config.setUpdateTime(LocalDateTime.now());
        configMapper.updateById(config);
        
        return true;
    }

    @Override
    public void processScheduledTasks() {
        LocalDateTime now = LocalDateTime.now();
        List<MerchantOperationTask> tasks = taskMapper.selectScheduledTasks(now);
        
        for (MerchantOperationTask task : tasks) {
            log.info("Processing scheduled task: {}", task.getId());
            // 执行相应的任务逻辑
            executeTask(task.getId(), "自动执行完成");
        }
    }

    @Override
    public void batchGenerateDailyReports(LocalDate date) {
        // 获取所有需要生成日报的商户
        List<MerchantOperationConfig> configs = configMapper.selectList(null);
        
        for (MerchantOperationConfig config : configs) {
            try {
                generateDailyReport(config.getMerchantId(), date);
            } catch (Exception e) {
                log.error("Failed to generate daily report for merchant: {}", config.getMerchantId(), e);
            }
        }
        
        log.info("Batch generated daily reports for date: {}", date);
    }

    private MerchantOperationDailyReportResponse convertToResponse(MerchantOperationDailyReport report) {
        MerchantOperationDailyReportResponse response = new MerchantOperationDailyReportResponse();
        BeanUtils.copyProperties(report, response);
        return response;
    }

    private String generateAISuggestions(Long merchantId) {
        List<String> suggestions = getAIOperationSuggestions(merchantId);
        return String.join("; ", suggestions);
    }
}
