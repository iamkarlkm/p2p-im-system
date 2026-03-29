package com.im.modules.merchant.automation.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.im.modules.merchant.automation.entity.MerchantReminderRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 智能提醒记录数据访问层
 */
@Mapper
public interface MerchantReminderRecordMapper extends BaseMapper<MerchantReminderRecord> {
    
    /**
     * 查询商户的提醒记录
     */
    @Select("SELECT * FROM merchant_reminder_record WHERE merchant_id = #{merchantId} AND deleted = 0 " +
            "ORDER BY create_time DESC")
    IPage<MerchantReminderRecord> findByMerchantId(Page<MerchantReminderRecord> page, @Param("merchantId") String merchantId);
    
    /**
     * 查询待发送的提醒
     */
    @Select("SELECT * FROM merchant_reminder_record WHERE status = 0 AND deleted = 0 ORDER BY priority DESC, create_time ASC LIMIT 100")
    List<MerchantReminderRecord> findPendingReminders();
    
    /**
     * 标记提醒为已发送
     */
    @Update("UPDATE merchant_reminder_record SET status = 2, send_time = NOW(), update_time = NOW() WHERE record_id = #{recordId}")
    int markAsSent(@Param("recordId") String recordId);
    
    /**
     * 标记提醒为已读
     */
    @Update("UPDATE merchant_reminder_record SET status = 3, read_time = NOW(), update_time = NOW() WHERE record_id = #{recordId}")
    int markAsRead(@Param("recordId") String recordId);
    
    /**
     * 查询商户未读提醒数
     */
    @Select("SELECT COUNT(*) FROM merchant_reminder_record WHERE merchant_id = #{merchantId} AND status < 3 AND deleted = 0")
    int countUnreadByMerchant(@Param("merchantId") String merchantId);
    
    /**
     * 查询商户某类型的提醒记录
     */
    @Select("SELECT * FROM merchant_reminder_record WHERE merchant_id = #{merchantId} AND rule_type = #{ruleType} " +
            "AND deleted = 0 ORDER BY create_time DESC LIMIT 50")
    List<MerchantReminderRecord> findByMerchantAndType(@Param("merchantId") String merchantId, @Param("ruleType") String ruleType);
}
