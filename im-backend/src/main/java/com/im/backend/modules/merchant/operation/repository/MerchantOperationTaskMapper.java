package com.im.backend.modules.merchant.operation.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.im.backend.modules.merchant.operation.entity.MerchantOperationTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 商户运营任务Mapper
 * Feature #307: Local Merchant Smart Operation Assistant
 * 
 * @author IM Development Team
 * @since 2026-03-30
 */
@Mapper
public interface MerchantOperationTaskMapper extends BaseMapper<MerchantOperationTask> {

    /**
     * 分页查询商户任务
     * 
     * @param page       分页参数
     * @param merchantId 商户ID
     * @param status     状态
     * @return 任务列表
     */
    Page<MerchantOperationTask> selectPageByMerchantId(Page<MerchantOperationTask> page, 
                                                        @Param("merchantId") Long merchantId, 
                                                        @Param("status") Integer status);

    /**
     * 查询待处理的任务
     * 
     * @param merchantId 商户ID
     * @return 任务列表
     */
    @Select("SELECT * FROM merchant_operation_task WHERE merchant_id = #{merchantId} AND status = 0 AND deleted = 0 ORDER BY priority DESC, create_time ASC")
    List<MerchantOperationTask> selectPendingTasks(@Param("merchantId") Long merchantId);

    /**
     * 查询高优先级未完成任务
     * 
     * @param merchantId 商户ID
     * @param minPriority 最低优先级
     * @return 任务列表
     */
    @Select("SELECT * FROM merchant_operation_task WHERE merchant_id = #{merchantId} AND status = 0 AND priority >= #{minPriority} AND deleted = 0 ORDER BY priority DESC")
    List<MerchantOperationTask> selectHighPriorityTasks(@Param("merchantId") Long merchantId, 
                                                         @Param("minPriority") Integer minPriority);

    /**
     * 更新任务状态
     * 
     * @param id     任务ID
     * @param status 状态
     * @return 影响行数
     */
    @Update("UPDATE merchant_operation_task SET status = #{status}, update_time = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 标记任务为已执行
     * 
     * @param id       任务ID
     * @param result   执行结果
     * @return 影响行数
     */
    @Update("UPDATE merchant_operation_task SET status = 2, executed_time = NOW(), execute_result = #{result} WHERE id = #{id}")
    int markAsExecuted(@Param("id") Long id, @Param("result") String result);

    /**
     * 查询到期的计划任务
     * 
     * @param now 当前时间
     * @return 任务列表
     */
    @Select("SELECT * FROM merchant_operation_task WHERE status = 0 AND scheduled_time <= #{now} AND deleted = 0")
    List<MerchantOperationTask> selectScheduledTasks(@Param("now") LocalDateTime now);
}
