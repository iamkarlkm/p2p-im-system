package com.im.modules.merchant.automation.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.im.modules.merchant.automation.entity.AutoMarketingTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 自动化营销任务数据访问层
 */
@Mapper
public interface AutoMarketingTaskMapper extends BaseMapper<AutoMarketingTask> {
    
    /**
     * 查询商户的任务列表
     */
    @Select("SELECT * FROM auto_marketing_task WHERE merchant_id = #{merchantId} AND deleted = 0 " +
            "ORDER BY create_time DESC")
    IPage<AutoMarketingTask> findByMerchantId(Page<AutoMarketingTask> page, @Param("merchantId") String merchantId);
    
    /**
     * 查询待执行的任务
     */
    @Select("SELECT * FROM auto_marketing_task WHERE status = 1 AND next_execute_time <= NOW() AND deleted = 0")
    List<AutoMarketingTask> findPendingTasks();
    
    /**
     * 更新任务统计信息
     */
    @Update("UPDATE auto_marketing_task SET sent_count = #{sentCount}, success_count = #{successCount}, " +
            "read_count = #{readCount}, last_execute_time = NOW(), update_time = NOW() WHERE task_id = #{taskId}")
    int updateStatistics(@Param("taskId") String taskId, @Param("sentCount") int sentCount, 
                         @Param("successCount") int successCount, @Param("readCount") int readCount);
    
    /**
     * 更新任务状态和下次执行时间
     */
    @Update("UPDATE auto_marketing_task SET status = #{status}, next_execute_time = #{nextExecuteTime}, " +
            "update_time = NOW() WHERE task_id = #{taskId}")
    int updateStatusAndNextTime(@Param("taskId") String taskId, @Param("status") int status, 
                                @Param("nextExecuteTime") LocalDateTime nextExecuteTime);
    
    /**
     * 查询商户某类型的任务
     */
    @Select("SELECT * FROM auto_marketing_task WHERE merchant_id = #{merchantId} AND task_type = #{taskType} " +
            "AND deleted = 0 ORDER BY create_time DESC")
    List<AutoMarketingTask> findByMerchantAndType(@Param("merchantId") String merchantId, @Param("taskType") int taskType);
}
