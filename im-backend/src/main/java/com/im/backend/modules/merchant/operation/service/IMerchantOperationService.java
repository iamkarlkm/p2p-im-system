package com.im.backend.modules.merchant.operation.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.im.backend.modules.merchant.operation.dto.MerchantOperationConfigRequest;
import com.im.backend.modules.merchant.operation.dto.MerchantOperationDailyReportResponse;
import com.im.backend.modules.merchant.operation.entity.MerchantOperationConfig;
import com.im.backend.modules.merchant.operation.entity.MerchantOperationDailyReport;
import com.im.backend.modules.merchant.operation.entity.MerchantOperationTask;

import java.time.LocalDate;
import java.util.List;

/**
 * 商户运营助手服务接口
 * Feature #307: Local Merchant Smart Operation Assistant
 * 
 * @author IM Development Team
 * @since 2026-03-30
 */
public interface IMerchantOperationService extends IService<MerchantOperationConfig> {

    /**
     * 创建或更新运营配置
     * 
     * @param request 配置请求
     * @return 配置ID
     */
    Long saveConfig(MerchantOperationConfigRequest request);

    /**
     * 获取商户运营配置
     * 
     * @param merchantId 商户ID
     * @return 配置信息
     */
    MerchantOperationConfig getConfigByMerchantId(Long merchantId);

    /**
     * 生成日报数据
     * 
     * @param merchantId 商户ID
     * @param reportDate 报表日期
     * @return 日报数据
     */
    MerchantOperationDailyReport generateDailyReport(Long merchantId, LocalDate reportDate);

    /**
     * 分页查询日报
     * 
     * @param merchantId 商户ID
     * @param pageNum    页码
     * @param pageSize   页大小
     * @return 日报分页数据
     */
    Page<MerchantOperationDailyReportResponse> queryDailyReportPage(Long merchantId, Integer pageNum, Integer pageSize);

    /**
     * 获取最新日报
     * 
     * @param merchantId 商户ID
     * @return 最新日报
     */
    MerchantOperationDailyReportResponse getLatestDailyReport(Long merchantId);

    /**
     * 创建运营任务
     * 
     * @param task 任务实体
     * @return 任务ID
     */
    Long createOperationTask(MerchantOperationTask task);

    /**
     * 获取待处理任务列表
     * 
     * @param merchantId 商户ID
     * @return 任务列表
     */
    List<MerchantOperationTask> getPendingTasks(Long merchantId);

    /**
     * 执行任务
     * 
     * @param taskId 任务ID
     * @param result 执行结果
     * @return 是否成功
     */
    Boolean executeTask(Long taskId, String result);

    /**
     * 获取AI运营建议
     * 
     * @param merchantId 商户ID
     * @return 建议列表
     */
    List<String> getAIOperationSuggestions(Long merchantId);

    /**
     * 开启自动营销
     * 
     * @param merchantId 商户ID
     * @param rules      营销规则
     * @return 是否成功
     */
    Boolean enableAutoMarketing(Long merchantId, String rules);

    /**
     * 处理定时任务
     */
    void processScheduledTasks();

    /**
     * 批量生成日报
     * 
     * @param date 日期
     */
    void batchGenerateDailyReports(LocalDate date);
}
