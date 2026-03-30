package com.im.backend.modules.merchant.operation.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.im.backend.modules.merchant.operation.entity.MerchantOperationDailyReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 商户运营日报Mapper
 * Feature #307: Local Merchant Smart Operation Assistant
 * 
 * @author IM Development Team
 * @since 2026-03-30
 */
@Mapper
public interface MerchantOperationDailyReportMapper extends BaseMapper<MerchantOperationDailyReport> {

    /**
     * 分页查询商户日报
     * 
     * @param page       分页参数
     * @param merchantId 商户ID
     * @return 日报列表
     */
    @Select("SELECT * FROM merchant_operation_daily_report WHERE merchant_id = #{merchantId} AND deleted = 0 ORDER BY report_date DESC")
    Page<MerchantOperationDailyReport> selectPageByMerchantId(Page<MerchantOperationDailyReport> page, @Param("merchantId") Long merchantId);

    /**
     * 查询日期范围内的日报
     * 
     * @param merchantId 商户ID
     * @param startDate  开始日期
     * @param endDate    结束日期
     * @return 日报列表
     */
    @Select("SELECT * FROM merchant_operation_daily_report WHERE merchant_id = #{merchantId} AND report_date BETWEEN #{startDate} AND #{endDate} AND deleted = 0 ORDER BY report_date DESC")
    List<MerchantOperationDailyReport> selectByDateRange(@Param("merchantId") Long merchantId, 
                                                          @Param("startDate") LocalDate startDate, 
                                                          @Param("endDate") LocalDate endDate);

    /**
     * 查询最新日报
     * 
     * @param merchantId 商户ID
     * @return 最新日报
     */
    @Select("SELECT * FROM merchant_operation_daily_report WHERE merchant_id = #{merchantId} AND deleted = 0 ORDER BY report_date DESC LIMIT 1")
    MerchantOperationDailyReport selectLatestByMerchantId(@Param("merchantId") Long merchantId);

    /**
     * 统计商户总订单数
     * 
     * @param merchantId 商户ID
     * @return 总订单数
     */
    @Select("SELECT SUM(total_orders) FROM merchant_operation_daily_report WHERE merchant_id = #{merchantId} AND deleted = 0")
    Integer sumTotalOrdersByMerchantId(@Param("merchantId") Long merchantId);
}
